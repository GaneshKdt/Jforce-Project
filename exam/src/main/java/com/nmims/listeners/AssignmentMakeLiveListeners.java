package com.nmims.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import com.nmims.events.MakeExamLive;
import com.nmims.services.PassFailService;


/**
 * Consists of Assignment related event listeners, Added in March 2023 
 *
 */
@Component
@EnableAsync
public class AssignmentMakeLiveListeners {

	@Autowired
	private PassFailService passFailService;

	/**
	 * Asynchronously updates quick assignment table upon making results live
	 * 
	 * @param ExamLive Event Object that provides exam year and month to update
	 *                 quick assignment table
	 */
	@Async("makeLiveAsyncExecutor")
	@EventListener
	public void updateQuickAssignmentTablePostExamMakeLive(final MakeExamLive makeExamLive) {
		System.out.println("------------------- Make Exam Live Listener : updateQuickAssignmentTablePostExamMakeLive START -------------------");
		
		passFailService.executeAssignmentLogicPostPassFailProcess(makeExamLive.getExamYear(),
				makeExamLive.getExamMonth());
		
		System.out.println("------------------- Make Exam Live Listener : updateQuickAssignmentTablePostExamMakeLive FINISHED -------------------");
	}
}
