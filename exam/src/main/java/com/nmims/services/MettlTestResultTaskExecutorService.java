package com.nmims.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.nmims.beans.MettlFetchTestResultBean;
import com.nmims.beans.MettlPGResponseBean;
import com.nmims.beans.MettlResultCandidateBean;

@Service
public class MettlTestResultTaskExecutorService {


	public static final Logger multithreadlogger = LoggerFactory.getLogger("fetchTestResult-PG-multithread");
	public static final Logger logger = LoggerFactory.getLogger("fetchTestResult-PG");
	
	@Autowired
	private MettlTeeMarksService mettlTeeMarksService;
	
	private MettlFetchTestResultBean finalResponse ;
	
	class FetchTestResultTask implements Callable<MettlFetchTestResultBean> {
		private String schedule_accessKey; 
		private List<MettlPGResponseBean> confirmBookingList;
		private String createdBy; 
		Integer count ;
		Set<String> bodQuestionIds;
		
	    FetchTestResultTask(String schedule_accessKey,List<MettlPGResponseBean> confirmBookingList,  String createdBy,Integer count, Set<String> bodQuestionIds) {
	     	this.schedule_accessKey = schedule_accessKey; 
	     	this.confirmBookingList = confirmBookingList; 
	     	this.createdBy = createdBy; 
	     	this.count  = count; 
	     	this.bodQuestionIds = bodQuestionIds;
	     }
	    @Override
	     public MettlFetchTestResultBean call() throws Exception{
	    	multithreadlogger.info(" Task No "+count+" Started schedule_accessKey "+schedule_accessKey+" confirmBookingList size "+confirmBookingList.size());
	    	MettlFetchTestResultBean bean = new MettlFetchTestResultBean();
	    	bean.setSchedule_accessKey(schedule_accessKey); 
	    	bean.setConfirmBookingList(confirmBookingList);
	    	List<MettlResultCandidateBean> mettlResultList = new ArrayList<>();
	    	mettlTeeMarksService.getTestStatusForAllInSchedule(schedule_accessKey, mettlResultList, true, 50);
	    	mettlTeeMarksService.vaildateMettlResultWithConfirmBooking(confirmBookingList,mettlResultList);
	    	mettlTeeMarksService.mapandupsertData(confirmBookingList, bean, createdBy, bodQuestionIds); 
	    	multithreadlogger.info(" Task No "+count+" Task Completed "+schedule_accessKey+" confirmBookingList size "+confirmBookingList.size());
	    	return bean;
	     }
	 }
	
	
	public void fetchTestResult(List<MettlPGResponseBean> list, String createdBy, MettlFetchTestResultBean resultBean, Set<String> questionIds) {
		finalResponse =  new MettlFetchTestResultBean();
		BasicThreadFactory factory = new BasicThreadFactory.Builder()
                .namingPattern("fetch-pg-tee-result-%d")
                .build();
		ExecutorService service = Executors.newFixedThreadPool( 8, factory ); 
		
		
		logger.info(" booking size " + list.size());
		
		List<Future<MettlFetchTestResultBean>> allFutures = new ArrayList<>(); 
		finalResponse.setBookingcount(list.size());
		finalResponse.setPullProcessStart(true);
		finalResponse.setExamMonth(resultBean.getExamMonth());
		finalResponse.setExamYear(resultBean.getExamYear());
		Integer count = 1;
		
		Map<String, List<MettlPGResponseBean>> scheduleAccessKeyGrouped = list.stream().collect(Collectors.groupingBy(MettlPGResponseBean::getSchedule_accessKey));
		logger.info(" scheduleAccessKeyGrouped size " + scheduleAccessKeyGrouped.size());
		for(Map.Entry<String, List<MettlPGResponseBean>> entry : scheduleAccessKeyGrouped.entrySet()) {
			Future<MettlFetchTestResultBean> f = service.submit(new FetchTestResultTask(entry.getKey(), entry.getValue(),createdBy, count, questionIds));
			allFutures.add(f);
			count++;
		}
		
		
		CompletableFuture.runAsync(new Runnable() {
		    @Override
		    public void run() {
		    	checkFutureTask(allFutures, service, false);
		    }
		});
		
		
		
	}
	
	private void checkFutureTask(List<Future<MettlFetchTestResultBean>> allFutures, ExecutorService service, boolean fetchResultForCandidate) {
		Integer i = 1;
		for (Future<MettlFetchTestResultBean> future : allFutures) {
			try {
				MettlFetchTestResultBean result = future.get();
				finalResponse.setSuccesscount( finalResponse.getSuccesscount()+ result.getSuccesscount() );
				finalResponse.setTransferredCount(finalResponse.getTransferredCount() + result.getTransferredCount());
				finalResponse.getFailureResponse().addAll(result.getFailureResponse());
				finalResponse.setBodAppliedCount(finalResponse.getBodAppliedCount() + result.getBodAppliedCount());
				
				if(fetchResultForCandidate) {
					System.out.println("Future Task No - " +i+ " done " + future.isDone()+" finalResponse.getSuccesscount() "+finalResponse.getSuccesscount()+" finalResponse.getFailureResponse().size() "+finalResponse.getFailureResponse().size());
					logger.info("Future Task No - " +i+ " done " + future.isDone()+" finalResponse.getSuccesscount "+finalResponse.getSuccesscount()+" finalResponse.getFailureResponse().size() "+finalResponse.getFailureResponse().size());
				}else {
					System.out.println("Future Task No - " +i+ " done " + future.isDone()+" result.getSuccesscount() "+result.getSuccesscount());
					logger.info("Future Task No - " +i+ " done " + future.isDone()+" schedule_accessKey "+result.getSchedule_accessKey()+" confirmBookingList size "+result.getConfirmBookingList().size()+" successResponse "+result.getSuccesscount()+" failureResponse "+result.getFailureResponse().size()+" total finalResponse.getSuccesscount "+finalResponse.getSuccesscount()+" total finalResponse.getFailureResponse().size() "+finalResponse.getFailureResponse().size());
				}
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("Future Task No - " +i+ " InterruptedException | ExecutionException  "+e.getMessage());
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				logger.error("Future Task No - " +i+ " Exception "+e.getMessage());
			
			}
			i++;
		}
		
		service.shutdown();
		System.out.println("Service Shutdown " );
		logger.info("Service Shutdown " );
		logger.info(" Finished Processing Successful inserts : " + finalResponse.getSuccesscount()
				+ " Failed inserts : " + finalResponse.getFailureResponse().size()
				+ " Failed inserts JSON : " + new Gson().toJson(finalResponse.getFailureResponse())
			);
			
		finalResponse.setPullTaskCompleted(true);
		finalResponse.setPullProcessStart(false);
	}
	
	protected MettlFetchTestResultBean  getPullTaskStatus() {
		return finalResponse;
	}
	
	
	class fetchMettlTestResultForCandidateTask implements Callable<MettlFetchTestResultBean> {
		private MettlPGResponseBean bean; 
		private String createdBy; 
		Integer count ;
		private Set<String> bodQuestionIds;
		
		fetchMettlTestResultForCandidateTask(MettlPGResponseBean bean, String createdBy, Integer count, Set<String> bodQuestionIds) {
	     	this.bean = bean; 
	     	this.createdBy = createdBy; 
	     	this.count  = count; 
	     	this.bodQuestionIds = bodQuestionIds;
	     }
	    @Override
	     public MettlFetchTestResultBean call() throws Exception{
	    	multithreadlogger.info(" Task No "+count+" Started Sapid "+bean.getSapid()+" Subject "+bean.getSubject());
	    	MettlFetchTestResultBean fetchTestResultBean = new MettlFetchTestResultBean();
	    	mettlTeeMarksService.fetchMettlTestResultForCandidate(bean, createdBy, fetchTestResultBean, bodQuestionIds);
	    	multithreadlogger.info(" Task No "+count+" Task Completed "+bean.getSapid()+" Subject "+bean.getSubject());
	    	return fetchTestResultBean;
	     }
	 }
	
	
	public void fetchMettlTestResultForCandidate(List<MettlPGResponseBean> bookings, String createdBy, MettlFetchTestResultBean resultBean, Set<String> questionIds) {
		finalResponse = new MettlFetchTestResultBean();
		logger.info(" fetchMettlTestResultForCandidate booking size " + bookings.size());
		BasicThreadFactory factory = new BasicThreadFactory.Builder()
                .namingPattern("fetch-pg-tee-result-for-candidate-%d")
                .build();
		ExecutorService service = Executors.newFixedThreadPool( 8, factory ); 
		List<Future<MettlFetchTestResultBean>> allFutures = new ArrayList<>();  
		
		finalResponse.setBookingcount(bookings.size());
		finalResponse.setPullProcessStart(true);
		finalResponse.setExamMonth(resultBean.getExamMonth());
		finalResponse.setExamYear(resultBean.getExamYear());
		Integer count = 1;
		for (MettlPGResponseBean booking : bookings) {
			Future<MettlFetchTestResultBean> f = service.submit(new fetchMettlTestResultForCandidateTask(booking, createdBy, count, questionIds));
			allFutures.add(f);
			count++;
		}
		
		CompletableFuture.runAsync(new Runnable() {
		    @Override
		    public void run() {
		    	checkFutureTask(allFutures, service, true);
		    }
		});
	}
	
	
	
	
}
