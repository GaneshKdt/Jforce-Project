package com.nmims.controllers;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.nmims.beans.DivisionBean;
import com.nmims.beans.StudentDivisionMappingBean;
import com.nmims.beans.StudentSubjectConfigExamBean;
import com.nmims.interfaces.DivisionService;
@Controller
public class MBAWXDivisonController extends BaseController{
	@Autowired
	DivisionService DivisionService;
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;
	
	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}")
	private List<String> ACAD_MONTH_LIST;
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	@GetMapping("admin/divisionForm")
	public ModelAndView divisionForm(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model=new ModelAndView("division");
		DivisionBean DivisionBean= new DivisionBean();
		List<DivisionBean> existingDivisionList = new ArrayList<DivisionBean>();
		try {
		existingDivisionList = DivisionService.getExistingDivisionList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addObject("bean", DivisionBean);
		model.addObject("yearList", ACAD_YEAR_LIST);
		model.addObject("monthList", ACAD_MONTH_LIST);
		model.addObject("existingDivisionList", existingDivisionList);
		model.addObject("rowCount", existingDivisionList.size());
		return model;
	}
	
	
	@PostMapping("admin/divisionFormSubmit")
	public ModelAndView divisionFormSubmit(HttpServletRequest request, HttpServletResponse response, @ModelAttribute DivisionBean bean,
			RedirectView view) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ModelAndView model= new ModelAndView("redirect:/admin/divisionForm");
		String userId = (String)request.getSession().getAttribute("userId");
		bean.setCreatedBy(userId);
		try {
			 DivisionService.insertDivisionDetails(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  model;
	}
	
	@GetMapping("admin/addStudentsTodivision")
	public ModelAndView addStudentsTodivision(@RequestParam("id") String divisionId,@RequestParam("year") String year,HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute("status") String status,
			@ModelAttribute("data") String data,
			@ModelAttribute("errorData") ArrayList<StudentDivisionMappingBean>errorStudent,
			@ModelAttribute("errorBean") String errorBean) {
		
		System.out.println("errorStudent::"+errorStudent);
		ModelAndView model=new ModelAndView("addStudentToDivision");
		request.getSession().setAttribute("divisionId",divisionId);
		request.getSession().setAttribute("year",year);
		StudentSubjectConfigExamBean fileBean = new StudentSubjectConfigExamBean();
		List<StudentDivisionMappingBean> listOfExistingStudent=new ArrayList<StudentDivisionMappingBean>();
		try {
			listOfExistingStudent = DivisionService.getListOfExistingStudent(divisionId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addObject("fileBean",fileBean);
		model.addObject("listOfExistingStudent",listOfExistingStudent);
		
		data=StringUtils.isBlank(data)?"":data;
		if(!StringUtils.isBlank(status)) {
			if(  status.equalsIgnoreCase("success")) {
				setSuccess(request,data+" records inserted successfully!");
			}
//			else {
//				setError(request,data+" Error While Inserting Records!");
//			}
		}
		
		
		String errorMsg="";
		
		for (StudentDivisionMappingBean e : errorStudent) {
			errorMsg=e.getErrorMessage()+" \n";
		}
		
		
		if(!StringUtils.isBlank(errorBean)&&errorBean.equalsIgnoreCase("error"))
			setError(request, errorMsg);
		
//		
//		if(errorStudent.size()>0) {
//			System.out.println("errorStudent::"+errorStudent.size());
//			System.out.println("result data::"+errorStudent);
//			errorStudent.stream().forEach(e->{
//				if(!StringUtils.isBlank(errorBean)&&errorBean.equalsIgnoreCase("error"))
//					setError(request, e.getErrorMessage());
//			});
//		}
		
		return model;
	}
	
	@RequestMapping(value="admin/uploadStudentToDivision",method = {RequestMethod.GET,RequestMethod.POST})
	public Object uploadStudentToDivision(@ModelAttribute StudentSubjectConfigExamBean fileBean,HttpServletRequest request,
			HttpServletResponse response,RedirectAttributes redirectAttributes) {
		String divisionId=(String)request.getSession().getAttribute("divisionId");
		String year=(String)request.getSession().getAttribute("year");
		//ModelAndView model= new ModelAndView("redirect:/admin/addStudentsTodivision?id="+divisionId);
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		RedirectView redirectView =new RedirectView();
		redirectView.setUrl(SERVER_PATH+"exam/admin/addStudentsTodivision?id="+divisionId+"&year="+year);
		String createdBy = (String)request.getSession().getAttribute("userId");
		ArrayList<StudentDivisionMappingBean>listOfStudent=new ArrayList<StudentDivisionMappingBean>();
		ArrayList<StudentDivisionMappingBean>errorStudent=new ArrayList<StudentDivisionMappingBean>();
		try {
			ArrayList<List<?>> readSapIdFromExcel = readSapIdFromExcel(fileBean,divisionId,year);
			listOfStudent = (ArrayList<StudentDivisionMappingBean>)readSapIdFromExcel.get(0);
			errorStudent = (ArrayList<StudentDivisionMappingBean>)readSapIdFromExcel.get(1);
			
			
			
			//handel error bean data and success error msg on ui
			System.out.println("errorStudent size :: "+errorStudent.size());
			if(errorStudent.size()>0){
				redirectAttributes.addFlashAttribute("errorBean", "error" );
				redirectAttributes.addFlashAttribute("errorData",errorStudent);
				//return redirectView;
			}
			//data insertion on division mapping 
			int batchUpdateCount = DivisionService.insertStudentToDivisionMappingBean(listOfStudent,createdBy);
			System.out.println("batchUpdateCount::"+batchUpdateCount);
			if(batchUpdateCount>0) {
				redirectAttributes.addFlashAttribute("status", "success" );
				redirectAttributes.addFlashAttribute("data",listOfStudent.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("status", "error" );
			redirectAttributes.addFlashAttribute("data","");
		}
		return redirectView;
	}
	
	//ExcelReading for divisionMapping
	public ArrayList<List<?>> readSapIdFromExcel(StudentSubjectConfigExamBean fileBean,String divisionId,String year)throws Exception{
		int SAPID_INDEX = 0;
		ArrayList<List<?>>listOfResult= new ArrayList<List<?>>();
		ArrayList<StudentDivisionMappingBean> students=new ArrayList<StudentDivisionMappingBean>();
		ArrayList<StudentDivisionMappingBean>errorStudent= new ArrayList<StudentDivisionMappingBean>();
		ByteArrayInputStream bis= new ByteArrayInputStream(fileBean.getFileData().getBytes()); 
		Workbook workbook;
		Iterator<Row> rowIterator = null;
		try {
			if (fileBean.getFileData().getOriginalFilename().endsWith(".xls")) {
				workbook = new HSSFWorkbook(bis);
				HSSFSheet sheet = (HSSFSheet)workbook.getSheetAt(0);
				rowIterator = sheet.iterator();
			} else if (fileBean.getFileData().getOriginalFilename().endsWith(".xlsx")) {
				workbook = new XSSFWorkbook(bis);
				XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
				rowIterator = sheet.iterator();
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			if(rowIterator.hasNext()) {
				Row row= rowIterator.next();
			}
			int i=0;
			
			List<String> listOfStudentByYear = DivisionService.getListOfStudentByYear(year);
			System.out.println("listOfStudentByYear::"+listOfStudentByYear);
			
			while(rowIterator.hasNext()) {
				Row row = rowIterator.next();
				if(row!=null) {
					row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String sapId=row.getCell(SAPID_INDEX ,Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					if("".equals(sapId.trim())){
						break;
					}
					
					Boolean studentYearCheck=listOfStudentByYear.contains(sapId);
					if (studentYearCheck){
						//check for duplicate entries from data base and studentExistOrNot
						Boolean check = DivisionService.duplicateStudentEntriesCheck(sapId,divisionId);
							if(!check) {
								StudentDivisionMappingBean bean= new StudentDivisionMappingBean();
								bean.setSapId(sapId);
								bean.setDivisionId(divisionId);
								students.add(bean);
							}else {
								StudentDivisionMappingBean erroBean= new StudentDivisionMappingBean();
								erroBean.setSapId(sapId);
								erroBean.setErrorMessage("Row : "+ (i+1)+" "+erroBean.getSapId().trim()+" Invalid Sapid / Duplicate Entries");
								errorStudent.add(erroBean);
							}
					}else {
							StudentDivisionMappingBean erroBean= new StudentDivisionMappingBean();
							erroBean.setSapId(sapId);
							erroBean.setErrorMessage("Row : "+ (i+1)+" "+erroBean.getSapId().trim()+" Invalid Sapid / Not Belong to This year "+year);
							errorStudent.add(erroBean);
						
					}
				}
			}
			
			//removed duplicate entries from existing list but user wont get message if entries are removed
			ArrayList<StudentDivisionMappingBean> studentsList=new ArrayList<StudentDivisionMappingBean>(new HashSet<StudentDivisionMappingBean>(students));

			
//			
//			 List<String> sapIds = students.stream().map(e->e.getSapId()).collect(Collectors.toList());
//			 HashMap<String,StudentDivisionMappingBean> bean=new HashMap<String, StudentDivisionMappingBean>(); 
//			 sapIds.stream().forEach(e->{
//				 students.stream().forEach(x->{
//					 if(e.equals(x.getSapId()))
//						 bean.put(e, x);
//				 });
//			 });
//			 bean.
			//remove duplicates from its own list
	//		List<StudentDivisionMappingBean> collect = students.stream().distinct().collect(Collectors.toList());
			studentsList.forEach(e->System.out.println("from excel helper::"+e));
//			collect.forEach(e->System.out.println(e));
			
			listOfResult.add(0, studentsList);
			listOfResult.add(1, errorStudent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfResult;
	}
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
}