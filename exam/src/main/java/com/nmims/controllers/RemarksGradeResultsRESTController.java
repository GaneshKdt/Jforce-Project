/**
 * 
 */
package com.nmims.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.RemarksGradeInputDTO;
import com.nmims.beans.RemarksGradeOutputDTO;
import com.nmims.beans.RemarksGradeResultsDTO;
import com.nmims.interfaces.GradingTypeResultsServiceInterface;
import com.nmims.interfaces.ProductGradingResultsFactoryInterface;

/**
 * @author vil_m 
 *
 */
@RestController
public class RemarksGradeResultsRESTController {
	
	//start - PROGRAMMING Constants
	public static final String KEY_ERROR = "error";
	public static final String KEY_SUCCESS = "success";
	public static final String RESULTS_UNAVAILABLE = "Results Unavailable";
	
	//end - PROGRAMMING Constants
	
	//start - DB Constants
	public static final String ROWS_NOT_DELETED = "Y";
	public static final Integer PROCESSED = 1;//for processed - 0 not processed, 1 processed. Initially set not processed.
	public static final Integer RESULT_LIVE = 1;//Displays Result
	
	//end - DB Constants

	@Autowired
	private ProductGradingResultsFactoryInterface productGradingResultsFactory;

	public static final Logger logger = LoggerFactory.getLogger(RemarksGradeResultsRESTController.class);


	/*@Deprecated
	 @PostMapping(path = "/m/stepNonWebRGResults")
	public List<RemarksGradeResultsBean> stepNonWebRGResults(
			@RequestBody RemarksGradeResultsBean remarksGradeResultsBean) throws Exception {

		logger.info("Entering RemarksGradeResultsRESTController : stepNonWebRGResults");
		
		boolean areResultsAvailable = Boolean.FALSE;
		List<RemarksGradeResultsBean> list1 = null;
		GradingTypeResultsServiceInterface gradingTypeResultsServiceInterface = null;

		logger.info(" RemarksGradeResultsRESTController : stepNonWebRGResults : (SapId, ProductType, GradingType) ("
				+ remarksGradeResultsBean.getSapid() + "," + remarksGradeResultsBean.getProductType() + ","
				+ remarksGradeResultsBean.getGradingType());


		if (null != remarksGradeResultsBean.getProductType() && null != remarksGradeResultsBean.getGradingType()) {
			if (ProductGradingResultsFactoryInterface.PRODUCT_UG
					.equalsIgnoreCase(remarksGradeResultsBean.getProductType())
					&& ProductGradingResultsFactoryInterface.GRADING_REMARK
							.equalsIgnoreCase(remarksGradeResultsBean.getGradingType())) {

				gradingTypeResultsServiceInterface = productGradingResultsFactory.getProductGradingResultsType(
						remarksGradeResultsBean.getProductType(), remarksGradeResultsBean.getGradingType());

				areResultsAvailable = gradingTypeResultsServiceInterface.checkResultsAvailable(remarksGradeResultsBean,
						RemarksGradeResultsBean.RESULT_LIVE, null,
						null, RemarksGradeResultsBean.PROCESSED,
						RemarksGradeResultsBean.ROWS_NOT_DELETED);//, RemarksGradeResultsBean.ATTEMPTED,RemarksGradeResultsBean.STATUS_RESET_PASSFAIL
				
				logger.info("RemarksGradeResultsRESTController : stepNonWebRGResults : ResultsAvailable : "
						+ areResultsAvailable);
				
				if (areResultsAvailable) {
					list1 = gradingTypeResultsServiceInterface.fetchStudentsResult(remarksGradeResultsBean,
							RemarksGradeResultsBean.RESULT_LIVE, null,
							null, RemarksGradeResultsBean.ROWS_NOT_DELETED);//, RemarksGradeResultsBean.ATTEMPTED,RemarksGradeResultsBean.STATUS_RESET_PASSFAIL
					if (null != list1 && !list1.isEmpty()) {
						logger.info("RemarksGradeResultsRESTController : stepNonWebRGResults : Result Size : "
								+ list1.size());
					}
				}
			} else {
				logger.error(
						"RemarksGradeResultsRESTController : stepNonWebRGResults : Wrong Product Type or Grading Type.");
				throw new Exception("Wrong Product Type or Grading Type.");
			}
		} else {
			logger.error(
					"RemarksGradeResultsRESTController : stepNonWebRGResults : Product Type or Grading Type Empty.");
			throw new Exception("Product Type or Grading Type Empty.");
		}
		return list1;
	}*/
	
	@PostMapping(path = "/m/stepNonWebRGResults")
	public RemarksGradeOutputDTO stepNonWebRGResults(@RequestBody RemarksGradeInputDTO remarksGradeInputDTO)
			throws Exception {

		logger.info("Entering RemarksGradeResultsRESTController : stepNonWebRGResults");
		
		boolean areResultsAvailable = Boolean.FALSE;
		GradingTypeResultsServiceInterface gradingTypeResultsServiceInterface = null;
		RemarksGradeOutputDTO remarksGradeOutputDTO = null;
		List<RemarksGradeResultsDTO> list1 = null;

		logger.info(" RemarksGradeResultsRESTController : stepNonWebRGResults : (SapId, ProductType, GradingType) ("
				+ remarksGradeInputDTO.getSapid() + "," + remarksGradeInputDTO.getProductType() + ","
				+ remarksGradeInputDTO.getGradingType());


		if (null != remarksGradeInputDTO.getProductType() && null != remarksGradeInputDTO.getGradingType()) {
			if (ProductGradingResultsFactoryInterface.PRODUCT_UG
					.equalsIgnoreCase(remarksGradeInputDTO.getProductType())
					&& ProductGradingResultsFactoryInterface.GRADING_REMARK
							.equalsIgnoreCase(remarksGradeInputDTO.getGradingType())) {

				gradingTypeResultsServiceInterface = productGradingResultsFactory.getProductGradingResultsType(
						remarksGradeInputDTO.getProductType(), remarksGradeInputDTO.getGradingType());

				areResultsAvailable = gradingTypeResultsServiceInterface.checkResultsAvailable(remarksGradeInputDTO.getSapid(),
						RemarksGradeResultsRESTController.RESULT_LIVE, null, null,
						RemarksGradeResultsRESTController.PROCESSED,
						RemarksGradeResultsRESTController.ROWS_NOT_DELETED);// , ATTEMPTED,STATUS_RESET_PASSFAIL
				
				logger.info("RemarksGradeResultsRESTController : stepNonWebRGResults : ResultsAvailable : "
						+ areResultsAvailable);
				
				if (areResultsAvailable) {
					list1 = gradingTypeResultsServiceInterface.fetchStudentsResult(remarksGradeInputDTO.getSapid(),
							RemarksGradeResultsRESTController.RESULT_LIVE, null, null,
							RemarksGradeResultsRESTController.ROWS_NOT_DELETED);// , ATTEMPTED,STATUS_RESET_PASSFAIL

					remarksGradeOutputDTO = new RemarksGradeOutputDTO(list1);

					if (null != list1 && !list1.isEmpty()) {
						logger.info("RemarksGradeResultsRESTController : stepNonWebRGResults : Result Size : "
								+ list1.size());
						remarksGradeOutputDTO.setMessage("Result Size : " + list1.size());
					} else {
						remarksGradeOutputDTO.setMessage("Empty Result.");
					}
				} else {
					remarksGradeOutputDTO = new RemarksGradeOutputDTO();
					remarksGradeOutputDTO.setMessage(RemarksGradeResultsRESTController.RESULTS_UNAVAILABLE);
				}
				remarksGradeOutputDTO.setStatus(RemarksGradeResultsRESTController.KEY_SUCCESS);
				return remarksGradeOutputDTO;
			} else {
				logger.error(
						"RemarksGradeResultsRESTController : stepNonWebRGResults : Wrong Product Type or Grading Type.");
				remarksGradeOutputDTO = new RemarksGradeOutputDTO();
				remarksGradeOutputDTO.setMessage("Error Code 2, please check with support.");
				remarksGradeOutputDTO.setStatus(RemarksGradeResultsRESTController.KEY_ERROR);
				return remarksGradeOutputDTO;
				//throw new Exception("Wrong Product Type or Grading Type.");
			}
		} else {
			logger.error(
					"RemarksGradeResultsRESTController : stepNonWebRGResults : Product Type or Grading Type Empty.");
			remarksGradeOutputDTO = new RemarksGradeOutputDTO();
			remarksGradeOutputDTO.setMessage("Error Code 1, please check with support.");
			remarksGradeOutputDTO.setStatus(RemarksGradeResultsRESTController.KEY_ERROR);
			return remarksGradeOutputDTO;
			//throw new Exception("Product Type or Grading Type Empty.");
		}
	}
}
