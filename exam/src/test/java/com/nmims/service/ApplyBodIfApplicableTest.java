package com.nmims.service;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.nmims.beans.MettlPGResponseBean;
import com.nmims.beans.MettlSectionQuestionResponse;
import com.nmims.beans.MettlStudentSectionInfo;
import com.nmims.services.MettlTeeMarksService;

/**
 * Test class to test apply bod process during pull
 * 
 * @author Swarup Singh Rajpurohit
 *
 */
public class ApplyBodIfApplicableTest {

	private MettlTeeMarksService mettlTeeMarksService = null;

	private final static String ATTEMPTED_STR = "Attempted";
	private final static String NOT_ATTEMPTED_STR = "Not Attempted";

	// to test student who have not attempted exam on mettl
	@Test public void not_apply_bod_to_not_attempted_test() {

		// pass status as not attempted
		MettlPGResponseBean bean = getMettlResponseBean(NOT_ATTEMPTED_STR);

		List<MettlStudentSectionInfo> sectiontInfoList = null;
		List<MettlSectionQuestionResponse> questionList = null;
		Set<String> bodQuestionIds = null;

		this.mettlTeeMarksService = new MettlTeeMarksService();

		mettlTeeMarksService.applyBodIfApplicable(bean, sectiontInfoList, questionList, bodQuestionIds);

		// bod not applied since status is not attempted!
		assertEquals(Boolean.FALSE, bean.isBodApplied());
	}

	// attempted records but not applicable for bod i.e. bod question was not for this schedule
	@Test public void attempted_but_not_applicable_test() {

		// pass status as attempted
		MettlPGResponseBean bean = getMettlResponseBean(ATTEMPTED_STR);
		List<MettlStudentSectionInfo> sectiontInfoList = getSectionInfoList();

		// pass false so the method will return not applicable record
		List<MettlSectionQuestionResponse> questionList = getSectionQuestionResponseList(Boolean.FALSE);

		Set<String> bodQuestionIds = getBodQuestionIds();

		this.mettlTeeMarksService = new MettlTeeMarksService();
		mettlTeeMarksService.applyBodIfApplicable(bean, sectiontInfoList, questionList, bodQuestionIds);

		// bod was not applied since students was not applicable
		assertEquals(Boolean.FALSE, bean.isBodApplied());

		// no full marks because not applicable for bod
		assertEquals(30.00, bean.getTotalMarks(), 0.0);

		// total marks 60.00 as usual
		assertEquals(60.00, bean.getMax_marks(), 0.0);
	}

	// attempted students as well as applicable for bod
	@Test public void attempted_and_applicable_test() {
		// atttempted record
		MettlPGResponseBean bean = getMettlResponseBean(ATTEMPTED_STR);
		List<MettlStudentSectionInfo> sectiontInfoList = getSectionInfoList();

		// pass true so it'll populate and return applicable records
		List<MettlSectionQuestionResponse> questionList = getSectionQuestionResponseList(Boolean.TRUE);

		Set<String> bodQuestionIds = getBodQuestionIds();

		this.mettlTeeMarksService = new MettlTeeMarksService();
		mettlTeeMarksService.applyBodIfApplicable(bean, sectiontInfoList, questionList, bodQuestionIds);

		// bod was applied so expecting it to be true
		assertEquals(Boolean.TRUE, bean.isBodApplied());

		// expecting scored full marks as bodMarks = maxMarks - totalMarks
		assertEquals(60.00, bean.getTotalMarks(), 0.0);

		// expecting max marks
		assertEquals(60.00, bean.getMax_marks(), 0.0);
	}

	// returns MettlPGResponseBean while setting status as passed as parameter
	private static MettlPGResponseBean getMettlResponseBean(String attemptedStatus) {
		MettlPGResponseBean mettlPGResponseBean = new MettlPGResponseBean();

		// setting parameter as passed i.e. attempted / not attempted
		mettlPGResponseBean.setStatus(attemptedStatus);

		mettlPGResponseBean.setSapid("test_sapid");
		mettlPGResponseBean.setSubject("test_subject");

		return mettlPGResponseBean;
	}

	// returns sample question id which are applicable for BOD
	private static Set<String> getBodQuestionIds() {
		return new HashSet<String>(Arrays.asList("question_bod_one", "question_bod_two", "question_bod_three"));
	}

	// returns all sections and total marks scored / max marks
	private static List<MettlStudentSectionInfo> getSectionInfoList() {

		MettlStudentSectionInfo sectionInfoOne = new MettlStudentSectionInfo();
		sectionInfoOne.setSectionName("Section 1_");
		sectionInfoOne.setTotalMarks(20.00);
		sectionInfoOne.setMaxMarks(20.00);

		MettlStudentSectionInfo sectionInfoTwo = new MettlStudentSectionInfo();
		sectionInfoTwo.setSectionName("Section 2_");
		sectionInfoTwo.setTotalMarks(10.00);
		sectionInfoTwo.setMaxMarks(20.00);

		MettlStudentSectionInfo sectionInfoThree = new MettlStudentSectionInfo();
		sectionInfoThree.setSectionName("Section 3_");
		sectionInfoThree.setTotalMarks(00.00);
		sectionInfoThree.setMaxMarks(20.00);

		return Arrays.asList(sectionInfoOne, sectionInfoTwo, sectionInfoThree);
	}

	// return section question list based on making it applicable or not
	private static List<MettlSectionQuestionResponse> getSectionQuestionResponseList(boolean isApplicable) {
		MettlSectionQuestionResponse questionResponseOne = new MettlSectionQuestionResponse();
		questionResponseOne.setMaxMarks(20);
		// if applicable half marks if not full marks
		questionResponseOne.setMarksScored(isApplicable ? 10 : 20);
		questionResponseOne.setQuestionId("question_bod_one");
		questionResponseOne.setSectionName("Section 1_");

		MettlSectionQuestionResponse questionResponseTwo = new MettlSectionQuestionResponse();
		questionResponseTwo.setMaxMarks(20);
		questionResponseTwo.setMarksScored(10);
		questionResponseTwo.setQuestionId("question_two");
		questionResponseTwo.setSectionName("Section 2_");

		MettlSectionQuestionResponse questionResponseThree = new MettlSectionQuestionResponse();
		questionResponseThree.setMaxMarks(20);
		questionResponseThree.setMarksScored(00);
		// if applicable add bod question otherwise not applicable ones
		questionResponseThree.setQuestionId(isApplicable ? "question_bod_three" : "question_three");
		questionResponseThree.setSectionName("Section 3_");

		return Arrays.asList(questionResponseOne, questionResponseTwo, questionResponseThree);
	}

}
