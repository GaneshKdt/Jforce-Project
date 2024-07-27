package com.nmims.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.PassFailDAO;

/**
 * Pass Fail Process multi-threaded and shifted from pass fail controller to
 * process pass fail. as per card : 13725
 * 
 * @since Pass Fail Optimization series Jan 2023
 * @author swarup.rajpurohit.EX
 *
 */
@Service
public class PassFailExecutorService {

	public static final Logger logger = LoggerFactory.getLogger("processPassFail-multiThreading");

	@Autowired
	private PassFailDAO passFailDao;

	@Autowired
	private PassFailService passFailService;

	@Autowired
	AssignmentService assignmentService;
	
	/**
	 * implements callable and returns number of rows inserted / updated for logging
	 * purpose
	 */
	class ProcessPassFailStagingTasks implements Callable<Integer> {

		private List<PassFailExamBean> passFailExamBeans;
		private HashMap<String, String> keyMap;

		public ProcessPassFailStagingTasks(List<PassFailExamBean> passFailExamBeans, HashMap<String, String> keyMap) {
			this.keyMap = keyMap;
			this.passFailExamBeans = passFailExamBeans;
		}

		@Override
		public Integer call() {
			try {
				passFailDao.upsertPassFailStagingRecordsBySAPID(passFailExamBeans, keyMap);
				Integer count = passFailExamBeans.size();
				return count;
			} finally {
				this.keyMap = null;
				this.passFailExamBeans = null;
			}
		}

	}

	/**
	 * updates pass fail table and marks processed as 'Y' in marks table
	 * 
	 * @param passFailExamBeans
	 * @param keyMap
	 */
	public void updatePassFailStagingTable(ArrayList<PassFailExamBean> passFailExamBeans, HashMap<String, String> keyMap) {

		logger.info("----- UPDATE PASS FAIL MULTITHREADING START -----");

		// number of threads for executor service pool
		final int numberOfThreads = 8;

		List<List<PassFailExamBean>> subLists = null;

		final ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

		List<Future<Integer>> futures = new ArrayList<>();

		try {
			// since update will work in multithreading fashion we'll have to divide list
			// into number of threads available in the pool so that none of the thread would
			// have to go into blocking state if one of them is still isn't done
			final int batchSize = passFailExamBeans.size() > 100 ? (passFailExamBeans.size() / numberOfThreads) : 1;

			// partition of list into sublists
			subLists = Lists.partition(passFailExamBeans, batchSize);

			logger.info("list of size : {} was divided into {} lists of {} entries each", passFailExamBeans.size(),
					subLists.size(), batchSize);

			// submitting divided lists to executor service via class ProcessPassFailtasks
			// since it implements callable
			// and collecting futures of these submit as list
			futures = subLists.stream().map(list -> executorService.submit(new ProcessPassFailStagingTasks(list, keyMap)))
					.collect(Collectors.toList());

			int taskCount = 0;
			int updatedInsertedRowsCount = 0;

			// iterating through all the futures we previously collected and getting them,
			// this will block the operation till all the threads are executed and only then
			// it'll move forward
			for (Future<Integer> future : futures) {
				Integer returnedCount = future.get();
				taskCount++;

				updatedInsertedRowsCount = updatedInsertedRowsCount + returnedCount;

				logger.info("future task {} completed out of {} total rows updated / inserted : {}", taskCount,
						subLists.size(), updatedInsertedRowsCount);
			}

			logger.info("----- UPDATE PASS FAIL MULTITHREADING END -----");

		} catch (ExecutionException | InterruptedException e) {
			logger.info("Error while trying to update and insert pass fail data : {}", Throwables.getStackTraceAsString(e));
		} finally {

			if (!executorService.isShutdown())
				executorService.shutdown();
			
			// setting resources to null in finally block to release heap memory for these
			// variables
			subLists = null;
			futures = null;

		}
	}

	/**
	 * Divided ProcessNew method from PassFailDao to get all students to be
	 * processed in the pass fail table implements Callables<List<PassFailExamBean>>
	 * for set of data received
	 */
	class ProcessNewForPassFailStudents implements Callable<List<PassFailExamBean>> {

		private HashMap<String, Integer> programSubjectPassScoreMap;
		private HashMap<String, StudentExamBean> studentsMap;
		private HashMap<String, ProgramSubjectMappingExamBean> programSubjectPassingConfigurationMap;
		private Map<String, ArrayList> keysMap;

		ProcessNewForPassFailStudents(HashMap<String, Integer> programSubjectPassScoreMap,
				HashMap<String, StudentExamBean> studentsMap,
				HashMap<String, ProgramSubjectMappingExamBean> programSubjectPassingConfigurationMap,
				Map<String, ArrayList> keysMap) {

			this.programSubjectPassScoreMap = programSubjectPassScoreMap;
			this.studentsMap = studentsMap;
			this.programSubjectPassingConfigurationMap = programSubjectPassingConfigurationMap;
			this.keysMap = keysMap;
		}

		@Override
		public List<PassFailExamBean> call() throws Exception {
			try {

				return passFailService.executePassFailLogicForIndividualStudent(new HashMap<>(keysMap), studentsMap,
						programSubjectPassingConfigurationMap, programSubjectPassScoreMap);
			} finally {
				this.keysMap = null;
			}
		}

	}

	// Pass Fail logic for general students.
	public ArrayList<PassFailExamBean> processNewForPassFail(HashMap<String, ArrayList> keysMap) {
		logger.info("----- Getting PassFailStudents From Process New Method START ------");
		
		if(keysMap == null || keysMap.isEmpty())
			throw new RuntimeException("No records to process pass fail on");
			
		ArrayList<PassFailExamBean> finalListOfStudent = new ArrayList<>();

		List<Map<String, ArrayList>> subMaps = null;
		List<Future<List<PassFailExamBean>>> futures = null;

		final int numberOfThreads = 4;

		final ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

		try {
			// dividing tasks into number of threads
			final int mapDividendNumber = keysMap.size() > 100 ?  (keysMap.size() / numberOfThreads) : 1;

			// getting all students list to avoid hitting DB for individual student
			HashMap<String, StudentExamBean> studentsMap = passFailDao.getAllStudents();

			// create Program Subject PassScore map
			// getProgramSubjectPassScoreMap();//shifted down by Vilpesh on 2022-05-23
			HashMap<String, ProgramSubjectMappingExamBean> programSubjectPassingConfigurationMap = passFailDao
					.getProgramSubjectPassingConfigurationMap();

			// Equivalent code by Vilpesh on 2022-05-23
			HashMap<String, Integer> programSubjectPassScoreMap = passFailDao
					.getProgramSubjectPassScoreMap(programSubjectPassingConfigurationMap);

			logger.info("received map with size : " + keysMap.size());

			// dividing hashmap we have received into number of threads
			subMaps = Lists.newArrayList(Iterables.partition(keysMap.entrySet(), mapDividendNumber)).stream()
					.map(e -> e.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
					.collect(Collectors.toList());

			logger.info("divided keyMaps into " + subMaps.size() + " parts with " + mapDividendNumber + " in each maps");

			// submitting tasks to executor service and collecting futures as list of future
			// containing placeholder / future for all tasks submitted, in our case small
			// list of students which will be combined to one
			futures = subMaps.stream()
					.map(map -> executorService.submit(new ProcessNewForPassFailStudents(programSubjectPassScoreMap,
							studentsMap, programSubjectPassingConfigurationMap, map)))
					.collect(Collectors.toList());

			int taskCount = 0;
			// iterating through list of futures and getting tasks done
			for (Future<List<PassFailExamBean>> future : futures) {
				List<PassFailExamBean> listOfPassFailBeans;
				try {
					listOfPassFailBeans = future.get();
					taskCount++;

					logger.info("Fetched {} rows out of {} task {} executed out of {}", listOfPassFailBeans.size(),
							keysMap.size(), taskCount, futures.size());

					finalListOfStudent.addAll(listOfPassFailBeans);

				} catch (ExecutionException | InterruptedException e1) {
					logger.info("Error while trying to fetch pass fail data : {}", Throwables.getStackTraceAsString(e1));
				}
			}

			// clearing these HashMaps once the task is done to avoid heap memory issue
			programSubjectPassScoreMap.clear();
			programSubjectPassingConfigurationMap.clear();
			studentsMap.clear();
		} catch (Exception e2) {
			logger.info("Error while trying to fetch pass fail data : {}", Throwables.getStackTraceAsString(e2));
		}

		finally {
			// shutting down the executor service and clearing collections used
			if (!executorService.isShutdown())
				executorService.shutdown();

			subMaps = null;
			futures = null;
			logger.info("----- Getting PassFailStudents From Process New Method END ------");
		}
		return finalListOfStudent;
	}

	/**
	 * process assginment pass fail logic to replace single threaded method
	 */
	class ProcessAssignmentPassFail implements Callable<Integer> {

		private List<PassFailExamBean> studentList;

		ProcessAssignmentPassFail(List<PassFailExamBean> studentList) {
			this.studentList = studentList;
		}

		@Override
		public Integer call() throws Exception {
			int sizeOfListProcessed;
			try {
				sizeOfListProcessed = studentList.size();
				assignmentService.updateQuickAssgTableOnPassfailProcess(new ArrayList<>(studentList));
			} finally {
				this.studentList = null;
			}
			return sizeOfListProcessed;
		}

	}

	/**
	 * Divides list into number of threads and gets the task done in multi threaded
	 * fashion, updates quick assignment passfail logic as per logic in the
	 * assignment service class
	 * 
	 * @param passFailStudentList
	 */
	public void updateQuickAssgTableOnPassfailProcess(ArrayList<PassFailExamBean> passFailStudentList) {

		logger.info("----- UPDATE QUICK ASSIGNMENT PASS FAIL PROCESS START -----");
		// declaring executor service and number of threads
		final int numberOfThreads = 8;
		final ExecutorService executorService = Executors.newFixedThreadPool(8);

		List<List<PassFailExamBean>> subLists = null;
		List<Future<Integer>> futures = null;

		try {
			logger.info("Number of pass fail students to process : {}", passFailStudentList.size());

			final int partitionSize = passFailStudentList.size() > 100 ? (passFailStudentList.size() / numberOfThreads) : 1;

			subLists = Lists.partition(passFailStudentList, partitionSize);

			logger.info("list divided into {} parts for {} threads to process having size {} each", subLists.size(),
					numberOfThreads, partitionSize);

			// assigning task (callables) to executor service and getting list of futures in
			// return
			futures = subLists.stream().map(list -> executorService.submit(new ProcessAssignmentPassFail(list)))
					.collect(Collectors.toList());

			logger.info("number of future tasks : {}", futures.size());

			int count = 0;
			int totalNumberOfProcessedEntries = 0;

			// iterating through futures and getting the same
			for (Future<Integer> future : futures) {
				Integer numberOfProcessedEntries;
				try {
					numberOfProcessedEntries = future.get();
					count++;
					totalNumberOfProcessedEntries = totalNumberOfProcessedEntries + numberOfProcessedEntries;

					logger.info("Executed task {} out of {}, total number of lists executed : {}", count,
							futures.size(), totalNumberOfProcessedEntries);

				} catch (InterruptedException | ExecutionException e) {
					logger.info("Error trying to process assignment passfail logic : {}", Throwables.getStackTraceAsString(e));
				}
			}
			logger.info("----- UPDATE QUICK ASSIGNMENT PASS FAIL PROCESS END -----");
		} catch (Exception e) {
			logger.info("Error trying to process assignment passfail logic : {}", Throwables.getStackTraceAsString(e));
		} finally {

			// shutting down executor service and setting data to null for memory and
			// resources relief
			if (!executorService.isShutdown())
				executorService.shutdown();

			futures = null;
			subLists = null;
		}
	}

}
