package com.nmims.services.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.nmims.beans.FinalCertificateABCreportBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.FinalCertificateABCreportDao;
import com.nmims.interfaces.FinalCertificateABCreportServiceInterface;

@Service
public class FinalCertificateABCreportService implements FinalCertificateABCreportServiceInterface {

	Logger ABCreportLogger = LoggerFactory.getLogger(FinalCertificateABCreportService.class);

	@Autowired
	FinalCertificateABCreportDao abcreportDao;

	/**
	 * 
	 * method fetch the Students data On the basis of their exam cycle provided by
	 * admin
	 * 
	 * @author shivam.sangale.EXT
	 * @param FinalCertificateABCreportBean
	 * @return FinalCertificateABCreportBean - All time report data of final
	 *         certificates and List of data on the exam cycle
	 * @throws Exception
	 */
	@Override
	public List<FinalCertificateABCreportBean> getStudentsDataForABCreport(FinalCertificateABCreportBean bean)
			throws Exception {

		List<FinalCertificateABCreportBean> ABCreport = getStudentsEligibleServiceRequestData(bean.getExamYear(),
				bean.getExamMonth());

		return ABCreport;
	}

	/**
	 * Service Request FinalCertificateList For Particular exam Cycle
	 * 
	 * @param examYear
	 * @param examMonth
	 * @return List<FinalCertificateABCreportBean>
	 * @throws Exception
	 */
	private List<FinalCertificateABCreportBean> getStudentsEligibleServiceRequestData(String examYear, String examMonth)
			throws Exception {

		List<String> examCycleSapid = abcreportDao.getSapidForABCFinalCertificateOnExamCycle(examMonth, examYear);
		Map<String, List<PassFailExamBean>> passFailMapByExamYearMonth = abcreportDao.getPassfailListByYearMonth(examMonth, examYear);
		if(CollectionUtils.isEmpty(examCycleSapid) && examCycleSapid.size() == 0) {
			throw new RuntimeException("No records Found !");
		}
		List<FinalCertificateABCreportBean> serviceRequestdata = abcreportDao.getServiceRequestData(examCycleSapid);
		List<PassFailExamBean> serviceRequestList= serviceRequestdata.stream().map(k -> {
			PassFailExamBean examBean =  new PassFailExamBean();
			examBean.setSapid(k.getSapid());
			return examBean;
		}).collect(Collectors.toList());
	
		ArrayList<FinalCertificateABCreportBean> sapidResultData = new ArrayList<FinalCertificateABCreportBean>();
		Set<String> applicableYearMonthSet = new HashSet<String>();
		for(PassFailExamBean servicerequestBean : serviceRequestList) {
				
				List<PassFailExamBean> temp = passFailMapByExamYearMonth.get(servicerequestBean.getSapid());
				String applicableYearMonth = "";
				String tempMonth = "";
				String tempYear = "";
				String beanDate = "";
				Date maxDate = null;
				Date tempDate = new Date();
				for (PassFailExamBean bean : temp) {
					try {
						beanDate = "01 " + bean.getResultProcessedMonth() + " " + bean.getResultProcessedYear();
						
						SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
						
						tempDate = formatter.parse(beanDate);
						
						if (maxDate != null && tempDate.after(maxDate)) {
							maxDate = tempDate;
							applicableYearMonth = bean.getResultProcessedMonth() + bean.getResultProcessedYear();
							tempMonth = bean.getResultProcessedMonth();
							tempYear = bean.getResultProcessedYear();
							
						} else {
							maxDate = tempDate;
							applicableYearMonth = bean.getResultProcessedMonth() + bean.getResultProcessedYear();
							tempMonth = bean.getResultProcessedMonth();
							tempYear = bean.getResultProcessedYear();
							
						}
					} catch (Exception e) {
						ABCreportLogger.error("sapid {} Bean Date :" + beanDate +" Error : "+bean.getSapid() , e);
					}
				} // end for
				applicableYearMonthSet.add(applicableYearMonth);
				FinalCertificateABCreportBean bean = new FinalCertificateABCreportBean();
				bean.setSapid(servicerequestBean.getSapid());
				bean.setPassingMonth(tempMonth);
				bean.setPassingYear(tempYear);
				sapidResultData.add(bean);
				}
				
				
			
		Map<String, String> yearMonthResultDateMap = abcreportDao
				.getResultDeclareDateByYearMonthList(applicableYearMonthSet);
		ABCreportLogger.info("yearMonthResultDateMap : " + yearMonthResultDateMap);
		Map<String, StudentExamBean> studentData = abcreportDao.getStudentData(examCycleSapid);
		Map<String, String> certificate = createMapOfServiceRequestData(serviceRequestdata);
		for (FinalCertificateABCreportBean bean : sapidResultData) {
			try {
				ABCreportLogger.info(
						"bean.getPassingMonth()+bean.getPassingYear() : " + bean.getPassingMonth() + bean.getPassingYear());
				StudentExamBean sBean = studentData.get(bean.getSapid());
				bean.setDeclareDate(yearMonthResultDateMap.get(bean.getPassingMonth() + bean.getPassingYear()));
				bean.setMotherName(sBean.getMotherName());
				bean.setFatherName(sBean.getFatherName());
				bean.setMobile(sBean.getMobile());
				bean.setGender(sBean.getGender());
				bean.setStudentName(sBean.getFirstName());
				bean.setDateOfBirth(sBean.getDob());
				bean.setEmail(sBean.getEmailId());
				bean.setEnrollmentMonth(sBean.getEnrollmentMonth());
				bean.setProgramName(sBean.getProgram());
				bean.setEnrollmentYear(sBean.getEnrollmentYear());
				bean.setResult("Pass");
				bean.setCertificateNumber(certificate.get(bean.getSapid()));	
			} catch (Exception e) {
				e.printStackTrace();
				ABCreportLogger.error("Error  : "+e);
			}
			
		}
		return sapidResultData;

	}

	private Map<String, String> createMapOfServiceRequestData(List<FinalCertificateABCreportBean> serviceRequestdata) {
		Map<String, String> certificate = new HashMap<String, String>();
		for (FinalCertificateABCreportBean bean : serviceRequestdata) {
			certificate.put(bean.getSapid(), bean.getCertificateNumber());
		}
		return certificate;

	}

}
