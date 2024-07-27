package com.nmims.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.AISHEUGCExcelReportBean;
import com.nmims.beans.AISHEUGCReportsBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.AISHEUGCReportsDao;
import com.nmims.daos.AISHEUGCReportsDaoImpl;
import com.nmims.interfaces.AISHEUGCReportsService;

@Service
public class AISHEUGCReportsServiceImpl implements AISHEUGCReportsService {

	@Autowired
	private AISHEUGCReportsDaoImpl dao;
	
	@Override
	public ArrayList<AISHEUGCReportsBean> getListOfAppreadStudentByFirstLetterOfPrograms(String enrollmentYear,
			String enrollmentMonth, String sem, String firstLetterOfProgram) throws Exception {
		List<AISHEUGCReportsBean> listOfStudentAppearedByProgram =new  ArrayList<>();
		List<String> listOfSapid = dao.getListOfSapidForAllStudentsAppearedByExamYearMonthSem(enrollmentYear,
				enrollmentMonth, sem, firstLetterOfProgram);
		List<String> listOfProgramByFirstLetterOfProgram = dao
				.getListOfProgramByExamYearMonthSemFirstLetterofProgramForAllStudentsAppeared(enrollmentYear,
						enrollmentMonth, sem, firstLetterOfProgram);
		if(listOfSapid.size()>0 && listOfProgramByFirstLetterOfProgram.size()>0) {
		 listOfStudentAppearedByProgram = dao.getListOfStudentAppeared(listOfSapid,
				listOfProgramByFirstLetterOfProgram, firstLetterOfProgram);
		}
		return (ArrayList<AISHEUGCReportsBean>) listOfStudentAppearedByProgram;
	}

	// Get list of Female students Appeared by fist letter for all programs
	public ArrayList<AISHEUGCReportsBean> getListOfFemalesAppearedByFirstLetterOfPrograms(String enrollmentYear,
			String enrollmentMonth, String sem, String firstLetterOfProgram) throws Exception {
		List<AISHEUGCReportsBean> listOfFemalesAppearedByProgram = new ArrayList<>();
		List<String> listOfSapid = dao.getListOfSapidForAllStudentsAppearedByExamYearMonthSem(enrollmentYear,
				enrollmentMonth, sem, firstLetterOfProgram);
		List<String> listOfProgramByFirstLetterOfProgram = dao
				.getListOfProgramByExamYearMonthSemFirstLetterofProgramForAllStudentsAppeared(enrollmentYear,
						enrollmentMonth, sem, firstLetterOfProgram);
		if(listOfSapid.size()>0 && listOfProgramByFirstLetterOfProgram.size()>0 ) {
		 listOfFemalesAppearedByProgram = dao.getListOfFemaleStudentAppeared(listOfSapid,
				listOfProgramByFirstLetterOfProgram, firstLetterOfProgram);
		}
		return (ArrayList<AISHEUGCReportsBean>) listOfFemalesAppearedByProgram;
	}

	// get list of students passed by fist letter for all programs
	@Override
	public ArrayList<AISHEUGCReportsBean> getListOfStudentPassesByFirstLetterOfPrograms(String enrollmentYear,
			String enrollmentMonth, String sem, String firstLetterOfProgramList) throws Exception {
		List<AISHEUGCReportsBean> listOfPassedStudentByFirstLetterOfProgram =new ArrayList<>();
		List<String> listOfSapid = dao.getListOfSapidForTotalMarksByExamYearMonthSem(enrollmentYear, enrollmentMonth,
				sem, firstLetterOfProgramList);
		List<String> listOfSapidNY = dao.getListOfSapidNYForAllPass(enrollmentYear, enrollmentMonth,
				firstLetterOfProgramList);
		if(listOfSapid.size()>0 && listOfSapidNY.size()>0) {
		 listOfPassedStudentByFirstLetterOfProgram = dao.getListOfStudentPass(listOfSapidNY,
				listOfSapid, firstLetterOfProgramList);
		}
		return (ArrayList<AISHEUGCReportsBean>) listOfPassedStudentByFirstLetterOfProgram;
	}

	// Get list of Female students passed by fist letter for all programs
	@Override
	public ArrayList<AISHEUGCReportsBean> getListOfFemalePassesByFirstLetterOfProgram(String enrollmentYear,
			String enrollmentMonth, String sem, String firstLetterOfProgramList) throws Exception {
		List<AISHEUGCReportsBean> listOfFemalePassedStudentByFirstLetterOfProgram= new ArrayList<>();
		List<String> listOfSapid = dao.getListOfSapidForTotalMarksByExamYearMonthSem(enrollmentYear, enrollmentMonth,sem, firstLetterOfProgramList);
		List<String> listOfSapidNY = dao.getListOfSapidNYForAllPass(enrollmentYear, enrollmentMonth,firstLetterOfProgramList);
		if(listOfSapid.size()>0 && listOfSapidNY.size()>0) {
		 listOfFemalePassedStudentByFirstLetterOfProgram = dao.getListOfFemaleStudentPass(listOfSapidNY, listOfSapid, firstLetterOfProgramList);
		}
		return (ArrayList<AISHEUGCReportsBean>) listOfFemalePassedStudentByFirstLetterOfProgram;
	}

	public HashMap<String, Integer> getMapOfStudentsTotalMarksByFirstLetterOfProgram(String enrollmentYear,String enrollmentMonth, String sem, String firstLetterOfProgramList)
			throws Exception {
		List<String> listOfSapidAndProgram = new ArrayList<>();
		 List<AISHEUGCReportsBean> listOfStudentsWithTotalMarksByFirstLetterOfProgram =new ArrayList<>();
		List<String> listOfSapid = dao.getListOfSapidForTotalMarksByExamYearMonthSem(enrollmentYear, enrollmentMonth,sem, firstLetterOfProgramList);
		List<String> listOfSapidNY = dao.getListOfSapidNYForTotalMarks(enrollmentYear, enrollmentMonth,firstLetterOfProgramList);
		if(listOfSapid.size()>0 && listOfSapidNY.size() >0 ) {
		 listOfSapidAndProgram = dao.getListOfSapidAndProgramForTotalMarks(listOfSapidNY, listOfSapid,firstLetterOfProgramList);
		 listOfStudentsWithTotalMarksByFirstLetterOfProgram = dao.getListOfTotalMarks(listOfSapidAndProgram);
		}
		ArrayList<AISHEUGCReportsBean> listOfNoOfStudentAbove60ByFirstLetterOfProgram = new ArrayList<AISHEUGCReportsBean>();
		HashMap<String, String> studentIdByConsumerProgramStructureIdFromHashMap = studentIdByconsumerProgramStructureId();
		ArrayList<AISHEUGCReportsBean> listOfApplicableStudent = getapplicableSubjectByProgramAndMasterKey();
		HashMap<String, Integer> mapOfApplicableSubjectByProgramAndMasterKey = getMapFromApplicableSubjectByProgramAndMasterKey(
				listOfApplicableStudent);

		listOfStudentsWithTotalMarksByFirstLetterOfProgram.forEach((bean) -> {
			Double totalmarks = Double.parseDouble(bean.getTotalMarks());
			String MasterKey = studentIdByConsumerProgramStructureIdFromHashMap.get(bean.getSapid());
			// creating key by using master key ,program ,sem
			String Key = MasterKey + "-" + bean.getProgram() + "-" + bean.getSem();
			Integer noSubjectApplicable = mapOfApplicableSubjectByProgramAndMasterKey.get(Key);
			Double percentage = totalmarks / noSubjectApplicable;
			if (percentage >= 60) {
				listOfNoOfStudentAbove60ByFirstLetterOfProgram.add(bean);
			}

		});
		HashMap<String, Integer> mapOfStudentCountAbove60PercentageByFirstLetterOfProgram = createhashmapFromListOfNoOfStudentAbove60ByFirstLetterOfProgram(
				listOfNoOfStudentAbove60ByFirstLetterOfProgram);
		return mapOfStudentCountAbove60PercentageByFirstLetterOfProgram;
	}

	public HashMap<String, Integer> createhashmapFromListOfNoOfStudentAbove60ByFirstLetterOfProgram(
			ArrayList<AISHEUGCReportsBean> listOfNoOfStudentAbove60ByFirstLetterOfProgram) throws Exception {
		Map<String, Integer> mapOfNoOfStudentAbove60PercentageByFirstLetterOfProgram = listOfNoOfStudentAbove60ByFirstLetterOfProgram
				.stream().collect(Collectors.groupingBy(AISHEUGCReportsBean::getProgram,
						Collectors.reducing(0, program -> 1, Integer::sum)));
		return (HashMap<String, Integer>) mapOfNoOfStudentAbove60PercentageByFirstLetterOfProgram;

	}

	public HashMap<String, Integer> getMapOfFemaleStudentsTotalMarksByFirstLetterOfProgram(String enrollmentYear,
			String enrollmentMonth, String sem, String firstLetterOfProgramList) throws Exception {
		List<String> listOfSapidAndProgram =new ArrayList<>();
		List<AISHEUGCReportsBean> listOfFemaleStudentsWithTotalMarksByFirstLetterOfProgram=new ArrayList<>();
		List<String> listOfSapid = dao.getListOfSapidForTotalMarksByExamYearMonthSem(enrollmentYear, enrollmentMonth,sem, firstLetterOfProgramList);
		List<String> listOfSapidNY = dao.getListOfSapidNYForTotalMarks(enrollmentYear, enrollmentMonth,firstLetterOfProgramList);
		if(listOfSapid.size()>0 && listOfSapidNY.size()>0) {
		 listOfSapidAndProgram = dao.getListOfSapidAndProgramForFemaleTotalmarks(listOfSapidNY, listOfSapid,firstLetterOfProgramList);
		 listOfFemaleStudentsWithTotalMarksByFirstLetterOfProgram = dao.getListOfTotalMarks(listOfSapidAndProgram);
		}
		
		ArrayList<AISHEUGCReportsBean> listOfNoOfFeamleStudentAbove60ByFirstLetterOfProgram = new ArrayList<AISHEUGCReportsBean>();
		HashMap<String, String> studentIdByConsumerProgramStructureIdFromHashMap = studentIdByconsumerProgramStructureId();
		ArrayList<AISHEUGCReportsBean> listOfApplicableStudent = getapplicableSubjectByProgramAndMasterKey();
		HashMap<String, Integer> mapOfApplicableSubjectByProgramAndMasterKey = getMapFromApplicableSubjectByProgramAndMasterKey(
				listOfApplicableStudent);
		listOfFemaleStudentsWithTotalMarksByFirstLetterOfProgram.forEach((aisheugcReportsBean) -> {
			Double totalmarks = Double.parseDouble(aisheugcReportsBean.getTotalMarks());

			String MasterKey = studentIdByConsumerProgramStructureIdFromHashMap.get(aisheugcReportsBean.getSapid());

			String Key = MasterKey + "-" + aisheugcReportsBean.getProgram() + "-" + aisheugcReportsBean.getSem();

			Integer noSubjectApplicable = mapOfApplicableSubjectByProgramAndMasterKey.get(Key);

			Double percentage = totalmarks / noSubjectApplicable;

			if (percentage >= 60) {
				listOfNoOfFeamleStudentAbove60ByFirstLetterOfProgram.add(aisheugcReportsBean);
			}
		});

		HashMap<String, Integer> mapOfFemaleStudentCountAbove60PercentageByFirstLetterOfProgram = createhashmapFromListOfNoOfFemaleStudentAbove60ByFirstLetterOfProgram(
				listOfNoOfFeamleStudentAbove60ByFirstLetterOfProgram);
		return mapOfFemaleStudentCountAbove60PercentageByFirstLetterOfProgram;
	}

//map of student has more than 60 percentage  

	public HashMap<String, Integer> createhashmapFromListOfNoOfFemaleStudentAbove60ByFirstLetterOfProgram(
			ArrayList<AISHEUGCReportsBean> listOfNoOfFeamleStudentAbove60ByFirstLetterOfProgram) {
		Map<String, Integer> mapOfNoOfFemaleStudentAbove60PercentageByFirstLetterOfProgram = listOfNoOfFeamleStudentAbove60ByFirstLetterOfProgram
				.stream().collect(Collectors.groupingBy(AISHEUGCReportsBean::getProgram,
						Collectors.reducing(0, e -> 1, Integer::sum)));
		return (HashMap<String, Integer>) mapOfNoOfFemaleStudentAbove60PercentageByFirstLetterOfProgram;
	}

	public ArrayList<AISHEUGCReportsBean> getapplicableSubjectByProgramAndMasterKey() throws Exception {
		ArrayList<AISHEUGCReportsBean> listOfprogramsAndProgramId = dao.getProgramAndProgramId();
		ArrayList<AISHEUGCReportsBean> listOfapplicableSubject = dao
				.getapplicableSubjectBySemAndconsumerProgramStructureId();
		ArrayList<AISHEUGCReportsBean> listOfProgramIdAndconsumerProgramStructureId = dao
				.getProgramIdAndconsumerProgramStructureId();
		listOfprogramsAndProgramId.forEach((bean1) -> {
			listOfProgramIdAndconsumerProgramStructureId.forEach((bean2) -> {
				if ((bean1.getProgramId().equals(bean2.getProgramId()))) {
					bean2.setProgram(bean1.getProgram());
					listOfapplicableSubject.forEach((bean3) -> {
						if (bean2.getConsumerProgramStructureId().equals(bean3.getConsumerProgramStructureId())) {
							bean3.setProgramId(bean1.getProgramId());
							bean3.setProgram(bean2.getProgram());
						}

					});
				}
			});
		});
		HashMap<String, Integer> mapOfApplicablesubjectByprogramaAndMasterKey = getMapFromApplicableSubjectByProgramAndMasterKey(
				listOfapplicableSubject);
		return listOfapplicableSubject;
	}

	public HashMap<String, Integer> getMapFromApplicableSubjectByProgramAndMasterKey(
			ArrayList<AISHEUGCReportsBean> listOfapplicableSubject) {
		HashMap<String, Integer> MapFromApplicableSubjectByProgramAndMasterKey = new HashMap<>();
		listOfapplicableSubject.forEach((aisheugcReportsBean) -> {
			MapFromApplicableSubjectByProgramAndMasterKey.put(aisheugcReportsBean.getConsumerProgramStructureId() + "-"
					+ aisheugcReportsBean.getProgram() + "-" + aisheugcReportsBean.getSem(),
					aisheugcReportsBean.getNoOfApplicablesubject());
		});
		return MapFromApplicableSubjectByProgramAndMasterKey;

	}

	public HashMap<String, String> studentIdByconsumerProgramStructureId() throws Exception {
		ArrayList<AISHEUGCReportsBean> AllStudentsDetails = dao.getSapidAndCpsidForAllStudents();
		HashMap<String, String> mapOfStudentIdByConsumerProgramStructureId = getstudentIdByconsumerProgramStructureId(
				AllStudentsDetails);
		return mapOfStudentIdByConsumerProgramStructureId;
	}

	private HashMap<String, String> getstudentIdByconsumerProgramStructureId(
			ArrayList<AISHEUGCReportsBean> allStudentsDetails) {

		HashMap<String, String> mapOfStudentIdByConsumerProgramStructureId = new HashMap<>();
		allStudentsDetails.forEach((aisheugcReportsBean) -> {
			mapOfStudentIdByConsumerProgramStructureId.put(aisheugcReportsBean.getSapid(),
					aisheugcReportsBean.getConsumerProgramStructureId());
		});
		return mapOfStudentIdByConsumerProgramStructureId;
	}

	public ArrayList<AISHEUGCExcelReportBean> getAllListOfProgram(String enrollmentYear, String enrollmentMonth,
			String sem) throws Exception {
		ArrayList<AISHEUGCExcelReportBean> aggrigateList = new ArrayList<>();
		ArrayList<String> programlist = new ArrayList<String>();
		programlist.add("P");
		programlist.add("D");
		programlist.add("C");
		programlist.add("MB");
		programlist.stream().flatMap(firstLetterOfProgram -> ExcelListByFirstLetterOfProgram(enrollmentMonth,
				enrollmentYear, sem, firstLetterOfProgram).stream()).forEachOrdered(aggrigateList::add);
		return aggrigateList;

	}

	// method is created to handle exception
	private ArrayList<AISHEUGCExcelReportBean> ExcelListByFirstLetterOfProgram(String enrollmentMonth,
			String enrollmentYear, String sem, String firstLetterOfProgram) {
		try {
			return getExcelListByFirstLetterOfProgram(enrollmentYear, enrollmentMonth, sem, firstLetterOfProgram);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());

		}
	}

	public ArrayList<AISHEUGCExcelReportBean> getExcelListByFirstLetterOfProgram(String enrollmentYear,
			String enrollmentMonth, String sem, String firstLetterOfProgram) throws Exception {

		ArrayList<AISHEUGCReportsBean> ListOfStudentAppearedByFirstLetterOfProgram = getListOfAppreadStudentByFirstLetterOfPrograms(
				enrollmentYear, enrollmentMonth, sem, firstLetterOfProgram);
		ArrayList<AISHEUGCReportsBean> ListOfFemaleStudentAppearedByFirstLetterOfProgram = getListOfFemalesAppearedByFirstLetterOfPrograms(
				enrollmentYear, enrollmentMonth, sem, firstLetterOfProgram);
		ArrayList<AISHEUGCReportsBean> ListOfStudentPassedByFirstLetterOfProgram = getListOfStudentPassesByFirstLetterOfPrograms(
				enrollmentYear, enrollmentMonth, sem, firstLetterOfProgram);
		ArrayList<AISHEUGCReportsBean> ListOfFemaleStudentPassedByFirstLetterOfProgram = getListOfFemalePassesByFirstLetterOfProgram(
				enrollmentYear, enrollmentMonth, sem, firstLetterOfProgram);

		HashMap<String, Integer> mapOfStudentCountAbove60PercentageByFirstLetterOfProgram = getMapOfStudentsTotalMarksByFirstLetterOfProgram(
				enrollmentYear, enrollmentMonth, sem, firstLetterOfProgram);
		HashMap<String, Integer> mapOfFemaleStudentCountAbove60PercentageByFirstLetterOfProgram = getMapOfFemaleStudentsTotalMarksByFirstLetterOfProgram(
				enrollmentYear, enrollmentMonth, sem, firstLetterOfProgram);
		ArrayList<AISHEUGCReportsBean> listByFirstLetterOfProgram = getallListOfProgramAndCounts(
				ListOfStudentAppearedByFirstLetterOfProgram, ListOfFemaleStudentAppearedByFirstLetterOfProgram,
				ListOfStudentPassedByFirstLetterOfProgram, ListOfFemaleStudentPassedByFirstLetterOfProgram,
				mapOfStudentCountAbove60PercentageByFirstLetterOfProgram,
				mapOfFemaleStudentCountAbove60PercentageByFirstLetterOfProgram);
		ArrayList<AISHEUGCExcelReportBean> ListOfExcelReport = createDataForAllStudentInAllProgram(
				listByFirstLetterOfProgram);
		return ListOfExcelReport;

	}

	public ArrayList<AISHEUGCReportsBean> getallListOfProgramAndCounts(ArrayList<AISHEUGCReportsBean> allListOfProgram,
			ArrayList<AISHEUGCReportsBean> allFemaleListOfProgramAppreadInFinal,
			ArrayList<AISHEUGCReportsBean> allStudentListOfPassedInAllProgram,
			ArrayList<AISHEUGCReportsBean> AllListOfFemaleStudentPassesInAllProgram,
			HashMap<String, Integer> MapOfAllStudentAbove60PercentageForAllProgram,
			HashMap<String, Integer> MapOfAllFemaleStudentAbove60PercentageForAllProgram) {
		allListOfProgram.forEach((bean1) -> {

			if (bean1.getProgram().startsWith("P")) {
				bean1.setTotalForPg(bean1.getTotal());
			} else if (bean1.getProgram().startsWith("D")) {
				bean1.setTotalForDiploma(bean1.getTotal());
			} else if (bean1.getProgram().startsWith("C")) {
				bean1.setTotalForCertificate(bean1.getTotal());
			} else {
				bean1.setTotaForMba(bean1.getTotal());
			}

		});

		allListOfProgram.forEach((bean1) -> {
			allFemaleListOfProgramAppreadInFinal.forEach((bean2) -> {
				if (bean2.getProgram().equals(bean1.getProgram())) {
					if (bean1.getProgram().startsWith("P")) {
						bean1.setGirlsForPg(bean2.getGirlsTotal());
					} else if (bean1.getProgram().startsWith("D")) {
						bean1.setGirlsForDiploma(bean2.getGirlsTotal());
					} else if (bean1.getProgram().startsWith("C")) {
						bean1.setGirlsForCertificate(bean2.getGirlsTotal());
					} else {
						bean1.setGirlsForMba(bean2.getGirlsTotal());
					}
				}

			});
		});

		allListOfProgram.forEach((bean1) -> {
			AllListOfFemaleStudentPassesInAllProgram.forEach((bean2) -> {
				if (bean2.getProgram().equals(bean1.getProgram())) {
					if (bean1.getProgram().startsWith("P")) {
						bean1.setGirlspassPg(bean2.getGirlsPass());

					} else if (bean1.getProgram().startsWith("D")) {
						bean1.setGirlsPassDiploma(bean2.getGirlsPass());
					} else if (bean1.getProgram().startsWith("C")) {
						bean1.setGirlsPassCertificate(bean2.getGirlsPass());
					} else {
						bean1.setGirlsPassMba(bean2.getGirlsPass());
					}
				}

			});
		});

		allListOfProgram.forEach((bean1) -> {
			allStudentListOfPassedInAllProgram.forEach((bean2) -> {
				if (bean2.getProgram().equals(bean1.getProgram())) {
					if (bean1.getProgram().startsWith("P")) {
						bean1.setTotalPassPg(bean2.getTotalPass());
					} else if (bean1.getProgram().startsWith("D")) {
						bean1.setTotalPassDiploma(bean2.getTotalPass());
					} else if (bean1.getProgram().startsWith("C")) {
						bean1.setTotalPassCertificate(bean2.getTotalPass());
					} else {
						bean1.setTotaPassMba(bean2.getTotalPass());
					}
				}

			});
		});

		allListOfProgram.forEach((bean1) -> {
			if (MapOfAllStudentAbove60PercentageForAllProgram.containsKey(bean1.getProgram())) {
				if (bean1.getProgram().startsWith("D")) {
					bean1.setTotalAbove60PercentageDiploma(
							MapOfAllStudentAbove60PercentageForAllProgram.get(bean1.getProgram()));
				} else if (bean1.getProgram().startsWith("P")) {
					bean1.setTotalAbove60PercentagePg(
							MapOfAllStudentAbove60PercentageForAllProgram.get(bean1.getProgram()));
				} else if (bean1.getProgram().startsWith("C")) {
					bean1.setTotalAbove60PercentageCertificate(
							MapOfAllStudentAbove60PercentageForAllProgram.get(bean1.getProgram()));
				} else {

					bean1.setTotalAbove60PercentageMba(
							MapOfAllStudentAbove60PercentageForAllProgram.get(bean1.getProgram()));
				}
			}

		});

		allListOfProgram.forEach((bean1) -> {
			if (MapOfAllFemaleStudentAbove60PercentageForAllProgram.containsKey(bean1.getProgram())) {
				if (bean1.getProgram().startsWith("D")) {

					bean1.setGirlsAbove60PercentageDiploma(
							MapOfAllFemaleStudentAbove60PercentageForAllProgram.get(bean1.getProgram()));
				} else if (bean1.getProgram().startsWith("P")) {

					bean1.setGirlsAbove60PercentagePg(
							MapOfAllFemaleStudentAbove60PercentageForAllProgram.get(bean1.getProgram()));
				} else if (bean1.getProgram().startsWith("C")) {
					bean1.setGirlsAbove60PercentageCertificate(
							MapOfAllFemaleStudentAbove60PercentageForAllProgram.get(bean1.getProgram()));
				}

				else {

					bean1.setGirlsAbove60PercentageMba(
							MapOfAllFemaleStudentAbove60PercentageForAllProgram.get(bean1.getProgram()));

				}
			}

		});

		return allListOfProgram;
	}

	public ArrayList<AISHEUGCExcelReportBean> createDataForAllStudentInAllProgram(
			ArrayList<AISHEUGCReportsBean> allListOfProgram) {
		ArrayList<AISHEUGCExcelReportBean> studentProgramCountList = new ArrayList<AISHEUGCExcelReportBean>();
		allListOfProgram.forEach((bean) -> {
			AISHEUGCExcelReportBean excelbean = new AISHEUGCExcelReportBean();
			excelbean.setEnrollmentYear(bean.getEnrollmentYear());
			excelbean.setEnrollmentMonth(bean.getEnrollmentMonth());
			excelbean.setProgram(bean.getProgram());
			excelbean.setSem(bean.getSem());
			if (bean.getProgram().startsWith("P")) {
				excelbean.setTotalNoOfStudentsAppearedInFinalYear(bean.getTotalForPg());
				excelbean.setTotalNoOfGirlsStudentsAppearedInFinalYear(bean.getGirlsForPg());
			} else if (bean.getProgram().startsWith("D")) {
				excelbean.setTotalNoOfStudentsAppearedInFinalYear(bean.getTotalForDiploma());
				excelbean.setTotalNoOfGirlsStudentsAppearedInFinalYear(bean.getGirlsForDiploma());
			} else if (bean.getProgram().startsWith("C")) {
				excelbean.setTotalNoOfStudentsAppearedInFinalYear(bean.getTotalForCertificate());
				excelbean.setTotalNoOfGirlsStudentsAppearedInFinalYear(bean.getGirlsForCertificate());
			} else {
				excelbean.setTotalNoOfStudentsAppearedInFinalYear(bean.getTotaForMba());
				excelbean.setTotalNoOfGirlsStudentsAppearedInFinalYear(bean.getGirlsForMba());

			}

			if (bean.getProgram().startsWith("P")) {
				excelbean.setTotalNoOfStudentsPassed(bean.getTotalPassPg());
				excelbean.setTotalNoOfGirlsStudentsPasseded(bean.getGirlspassPg());
			} else if (bean.getProgram().startsWith("D")) {
				excelbean.setTotalNoOfStudentsPassed(bean.getTotalPassDiploma());

				excelbean.setTotalNoOfGirlsStudentsPasseded(bean.getGirlsPassDiploma());

			} else if (bean.getProgram().startsWith("C")) {
				excelbean.setTotalNoOfStudentsPassed(bean.getTotalPassCertificate());
				excelbean.setTotalNoOfGirlsStudentsPasseded(bean.getGirlsPassCertificate());
			} else {
				excelbean.setTotalNoOfStudentsPassed(bean.getTotaPassMba());
				excelbean.setTotalNoOfGirlsStudentsPasseded(bean.getGirlsPassMba());
			}

			if (bean.getProgram().startsWith("D")) {

				if (bean.getTotalAbove60PercentageDiploma() != null) {
					excelbean.setTotalNoOfStudentsAbove60percentage(bean.getTotalAbove60PercentageDiploma());
				} else {
					excelbean.setTotalNoOfStudentsAbove60percentage((0));
				}
				if (bean.getGirlsAbove60PercentageDiploma() != null) {
					excelbean.setTotalNoOfGirlsStudentsAbove60Percentage(bean.getGirlsAbove60PercentageDiploma());
				} else {
					excelbean.setTotalNoOfGirlsStudentsAbove60Percentage((0));
				}
			} else if (bean.getProgram().startsWith("P")) {
				if (bean.getTotalAbove60PercentagePg() != null) {

					excelbean.setTotalNoOfStudentsAbove60percentage(bean.getTotalAbove60PercentagePg());

				} else {

					excelbean.setTotalNoOfStudentsAbove60percentage((0));
				}

				if (bean.getGirlsAbove60PercentagePg() != null) {

					excelbean.setTotalNoOfGirlsStudentsAbove60Percentage(bean.getGirlsAbove60PercentagePg());

				} else {
					excelbean.setTotalNoOfGirlsStudentsAbove60Percentage((0));
				}
			} else if (bean.getProgram().startsWith("C")) {

				if (bean.getTotalAbove60PercentageCertificate() != null) {
					excelbean.setTotalNoOfStudentsAbove60percentage(bean.getTotalAbove60PercentageCertificate());

				} else {
					excelbean.setTotalNoOfStudentsAbove60percentage((0));
				}

				if (bean.getGirlsAbove60PercentageCertificate() != null) {
					excelbean.setTotalNoOfGirlsStudentsAbove60Percentage(bean.getGirlsAbove60PercentageCertificate());

				}

				else {
					excelbean.setTotalNoOfGirlsStudentsAbove60Percentage((0));
				}
			} else if (bean.getProgram().startsWith("MB")) {

				if (bean.getTotalAbove60PercentageMba() != null) {
					excelbean.setTotalNoOfStudentsAbove60percentage(bean.getTotalAbove60PercentageMba());

				} else {
					excelbean.setTotalNoOfStudentsAbove60percentage((0));
				}

				if (bean.getGirlsAbove60PercentageMba() != null) {

					excelbean.setTotalNoOfGirlsStudentsAbove60Percentage(bean.getGirlsAbove60PercentageMba());

				} else {
					excelbean.setTotalNoOfGirlsStudentsAbove60Percentage((0));
				}
			}

			studentProgramCountList.add(excelbean);
		});
		return studentProgramCountList;

	}

}