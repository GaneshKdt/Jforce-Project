/**
 * 
 */
package com.nmims.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.RemarksGradeResultsBean;
import com.nmims.beans.RemarksGradeResultsDTO;
import com.nmims.beans.StudentExamBean;
import com.nmims.interfaces.GradingTypeResultsServiceInterface;
import com.nmims.interfaces.ProductGradingResultsFactoryInterface;

/**
 * @author vil_m  
 *
 */
@Controller
public class RemarksGradeResultsController extends BaseController {

	@Autowired(required = false)
	ApplicationContext act;

	@Autowired
	private ProductGradingResultsFactoryInterface productGradingResultsFactory;

	private GradingTypeResultsServiceInterface gradingTypeResultsServiceInterface = null;

	public static final Logger logger = LoggerFactory.getLogger(RemarksGradeResultsController.class);


	static {

	}

	@RequestMapping(value = "/student/stepRGResults", method = { RequestMethod.GET })

	public ModelAndView stepRGResults(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Entering RemarksGradeResultsController : stepRGResults");
		int rowCount = 0;
		boolean areResultsAvailable = Boolean.FALSE;
		List<RemarksGradeResultsDTO> list1 = null;
		ModelAndView mav = null;
		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
		} else {
			mav = new ModelAndView("examHome/rgResults");
			StudentExamBean student = (StudentExamBean) request.getSession().getAttribute("studentExam");

			gradingTypeResultsServiceInterface = productGradingResultsFactory.getProductGradingResultsType(
					ProductGradingResultsFactoryInterface.PRODUCT_UG,
					ProductGradingResultsFactoryInterface.GRADING_REMARK);

			areResultsAvailable = gradingTypeResultsServiceInterface.checkResultsAvailable(student.getSapid(),
					RemarksGradeResultsBean.RESULT_LIVE, null,
					null, RemarksGradeResultsBean.PROCESSED,
					RemarksGradeResultsBean.ROWS_NOT_DELETED);//, RemarksGradeResultsBean.ATTEMPTED,RemarksGradeResultsBean.STATUS_RESET_PASSFAIL
			
			if (areResultsAvailable) {
				list1 = gradingTypeResultsServiceInterface.fetchStudentsResult(student.getSapid(),
						RemarksGradeResultsBean.RESULT_LIVE, null,
						null, RemarksGradeResultsBean.ROWS_NOT_DELETED);//, RemarksGradeResultsBean.ATTEMPTED,RemarksGradeResultsBean.STATUS_RESET_PASSFAIL

				if (null != list1 && !list1.isEmpty()) {
					rowCount = list1.size();
				}
			}
			mav.addObject("rowCount", rowCount);
			mav.addObject("dataList", list1);
		}
		return mav;
	}
}
