package com.nmims.services;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.CenterStudentPortalBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.louReportBean;
import com.nmims.beans.programStudentPortalBean;
import com.nmims.daos.PortalDao;

@Service
public class louService {

	@Autowired 
	private PortalDao portalDAO;
	
	public ArrayList<String> getProgramNameByProgramType(String programType){
		ArrayList<String> typelist = portalDAO.getProgramNameByProgramType(programType);
		return typelist;
	}
	
	public ArrayList<String> getProgramTypeList(){
		ArrayList<String> list = portalDAO.getProgramTypeNameList();
		return list;
	}
	
	public int getsemByProgramName(String programName){
		int sem = portalDAO.getsembyprogramname(programName);
		return sem;
	}
	
	public ArrayList<StudentStudentPortalBean> generatereport(louReportBean bean){
		ArrayList<StudentStudentPortalBean> list = portalDAO.generateloudata(bean);
		return list;
	}

	public HashMap<String,String> getlcnamebycode() {
		HashMap<String,String> lcmap=new HashMap<String,String>();
		ArrayList<CenterStudentPortalBean> lc = (ArrayList<CenterStudentPortalBean>)portalDAO.getlcnamebycode();
		for(CenterStudentPortalBean map:lc) {
 			lcmap.put(map.getCenterCode(), map.getLc());
 		}
		return lcmap;
	}

	public HashMap<String,String> getprogramnamebyprogram() {
		HashMap<String,String> map=new HashMap<String,String>();
		ArrayList<programStudentPortalBean> programmap = (ArrayList<programStudentPortalBean>)portalDAO.getprogramnamebyprogram();
		for(programStudentPortalBean program:programmap) {
 			map.put(program.getCode(), program.getName());
 		}
 		return map;
	}
}
