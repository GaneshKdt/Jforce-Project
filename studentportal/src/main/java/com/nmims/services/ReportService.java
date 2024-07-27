package com.nmims.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.ReportBean;
import com.nmims.daos.ReportsDao;
import com.nmims.stratergies.ReportsInterface;

@Service
public class ReportService implements ReportsInterface {

	@Autowired
	ReportsDao rDao;

	@Override
	public List<ReportBean> getAllPowerBIReportDetails(String roles) {
		List<String> reportCategoryList = getReportcategoryByRoles(roles);
		List<ReportBean> reportList = rDao.getPowerbiReportsList(reportCategoryList);
		return reportList;
	}

	private List<String> getReportcategoryByRoles(String roles) {
		List<String> listOfRoles = Arrays.asList(roles.trim().split(","));
		List<String> reportCategoryList = new ArrayList<>();
		reportCategoryList.add("Common");

		listOfRoles.stream().map(String::trim).forEach(role -> {
			if (role.equals("Acads Admin")) {
				reportCategoryList.add("Academic");
			}

			if (role.equals("Exam Admin")) {
				reportCategoryList.add("Exam");
			}

			if (role.equals("Student Support")) {
				reportCategoryList.add("Student Support");
			}
		});
		return reportCategoryList;
	}

}
