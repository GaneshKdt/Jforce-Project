package com.nmims.controllers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.itextpdf.text.log.SysoCounter;
import com.nmims.beans.FileAcadsBean;
import com.nmims.beans.ModuleContentAcadsBean;
import com.nmims.beans.ProgramSubjectMappingAcadsBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.VideoContentAcadsBean;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.LearningResourcesDAO;
import com.nmims.helpers.ExcelHelper;

@Controller
public class LearningResourcesController extends BaseController {
	
	@Autowired(required = false)
	ApplicationContext act;
	private ArrayList<String> subjectList = null;
	
	private static final int BUFFER_SIZE = 4096;
	@Value( "${LEARNING_RESOURCES_BASE_PATH}" )
	private String LEARNING_RESOURCES_BASE_PATH;
	
	@ModelAttribute("subjectList")
	public ArrayList<String> getSubjectList() {
		if (this.subjectList == null) {
			ContentDAO dao = (ContentDAO) act.getBean("contentDAO");
			this.subjectList = dao.getActiveSubjects();
		}

		return subjectList;
	}
	private ArrayList<ProgramSubjectMappingAcadsBean> programSubjectMappingList = null;


	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;



	@RequestMapping(value = "/admin/uploadLearningResourcesExcelForm", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView uploadLearningResourcesExcelForm(HttpServletRequest request, HttpServletResponse response) {

		/* Uncomment after whole module is complete
		 * if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}
		*/
		ModelAndView modelAndView = new ModelAndView("uploadLearningResourcesExcelForm");
		modelAndView.addObject("fileBean", new FileAcadsBean());

		LearningResourcesDAO dao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		List<ModuleContentAcadsBean> moduleContentsList = dao.getAllModuleContentsList();
		modelAndView.addObject("moduleContentsList", moduleContentsList);
		modelAndView.addObject("moduleContentBean", new ModuleContentAcadsBean());
		return modelAndView;
	}
	
	@RequestMapping(value = "/admin/uploadLearningResourcesFiles", method = { RequestMethod.POST })
	public ModelAndView uploadLearningResourcesFiles(HttpServletRequest request,	
												HttpServletResponse response,
												@ModelAttribute FileAcadsBean fileBean) {

		/* Uncomment later
		 * if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}*/
		ModelAndView modelAndView = new ModelAndView("uploadLearningResourcesExcelForm");
		modelAndView.addObject("fileBean", fileBean);

		String userId = (String) request.getSession().getAttribute("userId_acads");
		LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		MultipartFile file = fileBean.getFileData();
		if (file.isEmpty()) {// Check if File was attached
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Please Select File to Upload...");

			List<ModuleContentAcadsBean> moduleContentsList = lrDao.getAllModuleContentsList();
			modelAndView.addObject("moduleContentsList", moduleContentsList);
			modelAndView.addObject("moduleContent", new ModuleContentAcadsBean()); 

			return modelAndView;
		}

		try {
			ExcelHelper excelHelper = new ExcelHelper();

			ArrayList<List> resultList = excelHelper.readModuleContentExcel(fileBean, subjectList, userId);
			ArrayList<ModuleContentAcadsBean> moduleContentsList = (ArrayList<ModuleContentAcadsBean>) resultList.get(0);
			ArrayList<ModuleContentAcadsBean> errorBeanList = (ArrayList<ModuleContentAcadsBean>) resultList.get(1);

			if (errorBeanList.size() > 0) {
				request.setAttribute("error", "true");
				String errorMessage="Error while uploading data caused due to bad data of rows with topic name : ";
				for(ModuleContentAcadsBean errorBean : errorBeanList) {
					errorMessage = errorMessage +"\n"+errorBean.getModuleName();
				}
				
				request.setAttribute("errorMessage", errorMessage+". Please Select File to Upload...");
				request.setAttribute("errorBeanList", errorBeanList);

				modelAndView.addObject("fileBean", fileBean);
				List<ModuleContentAcadsBean> moduleContentList = lrDao.getAllModuleContentsList();
				modelAndView.addObject("moduleContentsList", moduleContentsList);
				modelAndView.addObject("moduleContent", new ModuleContentAcadsBean());

				return modelAndView;
			}

			ArrayList<String> errorList = lrDao.batchUpdateModuleContent(moduleContentsList);

			if (errorList.size() == 0) {
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", moduleContentsList.size() + " rows out of "
						+ moduleContentsList.size() + " inserted successfully.");

				List<ModuleContentAcadsBean> moduleContentList = lrDao.getAllModuleContentsList();
				modelAndView.addObject("moduleContentsList", moduleContentList);
				modelAndView.addObject("moduleContent", new ModuleContentAcadsBean());

			} else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", errorList.size()
						+ " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "
						+ errorList);

				List<ModuleContentAcadsBean> moduleContentList = lrDao.getAllModuleContentsList();
				modelAndView.addObject("moduleContentsList", moduleContentList);
				modelAndView.addObject("moduleContent", new ModuleContentAcadsBean());

			}
		} catch (Exception e) {
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in inserting rows.");

			List<ModuleContentAcadsBean> moduleContentsList = lrDao.getAllModuleContentsList();
			modelAndView.addObject("moduleContentsList", moduleContentsList);
			modelAndView.addObject("moduleContent", new ModuleContentAcadsBean());

		}
		return modelAndView;
	}
	
	@RequestMapping(value = "/admin/saveModuleContents", method = RequestMethod.POST)
	public ModelAndView saveModuleContent(@ModelAttribute ModuleContentAcadsBean moduleContent, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView("uploadLearningResourcesExcelForm");
		LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		String userId = (String) request.getSession().getAttribute("userId_acads");
		try {
			if ( moduleContent.getId() == null) {
				moduleContent.setCreatedBy(userId);

				long key = lrDao.saveModuleContent(moduleContent);
				setSuccess(request, "ModuleContent has been saved successfully");
			} else {
				moduleContent.setLastModifiedBy(userId);
				boolean ModuleContentUpdated = lrDao.updateModuleContent(moduleContent);
				if (ModuleContentUpdated) {
					setSuccess(request, "ModuleContent has been updated successfully");
				}else {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Module Content Failed to upload. Try Again.");
					
				}
			}
		} catch (Exception e) {
			  
		}
		modelAndView.addObject("fileBean", new FileAcadsBean());

		List<ModuleContentAcadsBean> moduleContentsList = lrDao.getAllModuleContentsList();
		modelAndView.addObject("moduleContentsList", moduleContentsList);
		modelAndView.addObject("moduleContent", new ModuleContentAcadsBean());
		return modelAndView;
	}

	
	@RequestMapping(value = "/admin/editModuleContents", method = RequestMethod.GET)
	public ModelAndView editModuleContent(@RequestParam("id") int id, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView("moduleContentDetails");
		LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		String userId = (String) request.getSession().getAttribute("userId_acads");
		ModuleContentAcadsBean moduleContent = lrDao.getModuleContentById(id);
		List<ModuleContentAcadsBean> moduleDocumnentList = new ArrayList<ModuleContentAcadsBean>();
		moduleDocumnentList =  lrDao.getModuleDocumentDataById(id);
		modelAndView.addObject("fileBean", new FileAcadsBean());
		modelAndView.addObject("moduleDocumnentList", moduleDocumnentList); 
		modelAndView.addObject("moduleDocument", new ModuleContentAcadsBean()); 
		if (moduleContent == null) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Module Content Not Found");
			modelAndView.addObject("moduleContent", new ModuleContentAcadsBean()); 

		} else {
			modelAndView.addObject("moduleContent", moduleContent); 
		}
		
		return modelAndView;
	}

	@RequestMapping(value = "/admin/deleteModuleContents", method = RequestMethod.GET)
	public ModelAndView deleteModuleContent(@RequestParam("id") int id, HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView("uploadLearningResourcesExcelForm");
		LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		
		int deletedRow = lrDao.deleteModuleContent(id);
		if (deletedRow == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error Occured while deleting the Module Content.");
		} else {
			setSuccess(request, "ModuleContent has been deleted successfully");
		}
		modelAndView.addObject("fileBean", new FileAcadsBean());

		List<ModuleContentAcadsBean> moduleContentsList = lrDao.getAllModuleContentsList();
		modelAndView.addObject("moduleContentsList", moduleContentsList);
		modelAndView.addObject("moduleContent", new ModuleContentAcadsBean()); 
		return modelAndView;
	}

	@RequestMapping(value = "/admin/uploadModulesPdf",  method = RequestMethod.POST)
	public ModelAndView uploadModulesPdf(HttpServletRequest request,	
										 HttpServletResponse response,
										 @ModelAttribute FileAcadsBean fileBean){

		/*if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}*/

		ModelAndView modelAndView = new ModelAndView("moduleContentDetails");
		String userId = (String) request.getSession().getAttribute("userId_acads");
		
		LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		List<ModuleContentAcadsBean> moduleContentsList = lrDao.getAllModuleContentsList();
		modelAndView.addObject("moduleContentsList", moduleContentsList);
		modelAndView.addObject("fileBean", new FileAcadsBean());
		
		Integer id = fileBean.getFileId();
		ModuleContentAcadsBean moduleContent = lrDao.getModuleContentById(id);
		if (moduleContent == null) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Module Content Not Found");
			modelAndView.addObject("moduleContent", new ModuleContentAcadsBean()); 
			return modelAndView;
		}
		
		if(fileBean == null || fileBean.getFileData() == null  ){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in file Upload: No File Selected");
			modelAndView.addObject("moduleContent", new ModuleContentAcadsBean()); 
			return modelAndView;
		}
		moduleContent.setModuleId(id);
		String fileName = fileBean.getFileData().getOriginalFilename();  
		if(fileName == null || "".equals(fileName.trim()) ){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in file Upload Inner: No File Selected");
			modelAndView.addObject("moduleContent", new ModuleContentAcadsBean()); 
			return modelAndView;
		}
		String error=uploadPDF(fileBean, moduleContent,userId);
		if(error==null) {
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "PDF saved sucessfully.");
			
		}else {
			
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in Saving File. Try Again.");
			modelAndView.addObject("moduleContent", moduleContent); 
			return modelAndView;
		}
		moduleContent = lrDao.getModuleContentById(id);
		modelAndView.addObject("moduleContent", moduleContent); 
		return modelAndView;
	}
	


private String uploadPDF(FileAcadsBean bean, ModuleContentAcadsBean moduleContent, String userId) {

            String errorMessage = null;
            InputStream inputStream = null;   
            OutputStream outputStream = null;
            String subject=moduleContent.getSubject();
            String moduleName=moduleContent.getModuleName();

            CommonsMultipartFile file = bean.getFileData(); 
            String fileName = file.getOriginalFilename();   

            /*long fileSizeInBytes = bean.getFileData().getSize();
            if(fileSizeInBytes > MAX_FILE_SIZE){
                  errorMessage = "File size exceeds 5MB. Please upload a file with size less than 1MB";
                  return errorMessage;
            }*/

            //Replace special characters in file name
            subject = subject.replaceAll("'", "_");
            subject = subject.replaceAll(",", "_");
            subject = subject.replaceAll("&", "and");
            subject = subject.replaceAll(" ", "_");
            subject = subject.replaceAll(":", "");
            
            
            moduleName = moduleName.replaceAll("'", "_");
            moduleName = moduleName.replaceAll(",", "_");
            moduleName = moduleName.replaceAll("&", "and");
            moduleName = moduleName.replaceAll(" ", "_");
            moduleName = moduleName.replaceAll(":", "");
            
            if(!(fileName.toUpperCase().endsWith(".PDF") ) ){
                  errorMessage = "File type not supported. Please upload .pdf file.";
                  return errorMessage;
            }
            fileName = moduleName + ".pdf";
            try {  
                  //PDF stores first 4 letters as %PDF, which can be used to check if a file is actually a pdf file and not just going by extension
                  InputStream tempInputStream = file.getInputStream();  ;
                  byte[] initialbytes = new byte[4];   
                  tempInputStream.read(initialbytes);
                  tempInputStream.close();
                  String fileType = new String(initialbytes);

                  if(!"%PDF".equalsIgnoreCase(fileType)){
                        errorMessage = "File is not a PDF file. Please upload .pdf file.";
                        return errorMessage;
                  }

                  inputStream = file.getInputStream();   
                  String filePath = LEARNING_RESOURCES_BASE_PATH + subject + "/"+moduleContent.getId()+"/"+fileName;
                  //Check if Folder exists which is one folder per Exam (Jun2015, Dec2015 etc.) 
                  File folderPath = new File(LEARNING_RESOURCES_BASE_PATH + subject+"/"+moduleContent.getId()+"/");
                  if (!folderPath.exists()) {
                        boolean created = folderPath.mkdirs();
                  }   

                  File newFile = new File(filePath);   
                  outputStream = new FileOutputStream(newFile);   
                  int read = 0;   
                  byte[] bytes = new byte[1024];   

                  while ((read = inputStream.read(bytes)) != -1) {   
                        outputStream.write(bytes, 0, read);   
                  }
                  outputStream.close();
                  inputStream.close(); 
                  
                  //code for saving images to directory start
                  File folderPathForImages = new File(LEARNING_RESOURCES_BASE_PATH + subject + "/" + moduleContent.getModuleId());
                  moduleContent.setType(bean.getFileData().getContentType());
                  moduleContent.setFolderPath(LEARNING_RESOURCES_BASE_PATH + subject + "/" + moduleContent.getModuleId());
                  moduleContent.setDocumentName(bean.getFileName());
                  moduleContent.setCreatedBy(userId);
                  moduleContent.setLastModifiedBy(userId);
                  moduleContent.setPreviewPath("/lr/"+subject+"/"+moduleContent.getId()+"/"+fileName);
                  moduleContent.setDocumentPath(LEARNING_RESOURCES_BASE_PATH+subject+"/"+moduleContent.getId()+"/"+fileName);
                  if (!folderPathForImages.exists()) {
                        boolean created = folderPathForImages.mkdirs();
                  }
                  try {
                  // String sourceDir = moduleContentBean.getFilePath() ; // Pdf files are read from this folder
                  File sourceFile = new File(filePath);
                   FileAcadsBean fb ;
                   if (sourceFile.exists()) {
                       PDDocument document = PDDocument.load(filePath);
                       List<PDPage> list = document.getDocumentCatalog().getAllPages();
                       Integer noOfPages = list !=null ? list.size() : 0;
                       moduleContent.setNoOfPages(noOfPages);           
                       moduleContent.setActive("Y");
                       int pageNumber = 1;
                       
                     //Code to  save docu details in DB start
                        /*LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
                        boolean ModuleContentUpdated = lrDao.updateModuleContent(moduleContent);
                        if (!ModuleContentUpdated) {
                              errorMessage = "Module Content Not Updated.";
                              return errorMessage;
                        }*/
                       
                      //changed the folderPath data here as old data was not useful while showing images in slider
                     // moduleContent.setFolderPath("/lr/"+subject+"/"+moduleContent.getModuleId()+"/Module_"+moduleContent.getModuleId()+"_Doc_"+createdDocument+"_Page_"+pageNumber);
                       moduleContent.setFolderPath("lr/"+subject+"/"+moduleContent.getModuleId()+"/Module_"+moduleContent.getModuleId()+"_Doc_");
                        LearningResourcesDAO dao=(LearningResourcesDAO) act.getBean("learningResourcesDAO");
                        long createdDocument = dao.saveModuleDocument(moduleContent);
                        if(createdDocument==0) {
                              errorMessage = "Error in saving document details to dB. Pdf images too not not created  ";
                              return errorMessage;
                        }
                        //Code to  save docu details in DB end
                        
                        for (PDPage page : list) {
                        		String imageName = "Module_"+moduleContent.getModuleId()+"_Doc_"+createdDocument+"_Page_"+pageNumber;
                        		BufferedImage image = page.convertToImage();
                        		File outputfile = new File(LEARNING_RESOURCES_BASE_PATH + subject + "/" + moduleContent.getModuleId() + "/"+ imageName +".png");
                        		ImageIO.write(image, "png", outputfile);
                        		pageNumber++;
                        	}

                       outputStream.close();
                              inputStream.close(); 

                           pageNumber++;
                       
                       document.close();
                   }
                        else {
                       System.err.println(sourceFile.getName() +" File not exists");
                   }

               } catch (Exception e) {
                     
               }
                  
                  //code for saving images to directory end
                  
                  
            } catch (IOException e) {   
                  errorMessage = "Error in uploading file for "+bean.getFileId();
                       
            }   
            return errorMessage;
      }
     




 	
 	//Code for modulevideos admin view start
 	@RequestMapping(value = "/admin/editModuleVideoContents", method = RequestMethod.GET)
	public ModelAndView editModuleVideoContents(@RequestParam("moduleId") int moduleId, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView();
		LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		
		ModuleContentAcadsBean moduleContent = lrDao.getModuleContentById(moduleId);
		if (moduleContent == null) {
			modelAndView.addObject("fileBean", new FileAcadsBean());
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Module Content Not Found");
			modelAndView.addObject("moduleContent", new ModuleContentAcadsBean()); 
			modelAndView.setViewName("moduleContentDetails");
			List<ModuleContentAcadsBean> moduleContentsList = lrDao.getAllModuleContentsList();
			modelAndView.addObject("moduleContentsList", moduleContentsList);
			
			return modelAndView;
		} else {
			modelAndView.setViewName("editModuleVideoContents");
			modelAndView.addObject("moduleContent", moduleContent); 
			ArrayList<VideoContentAcadsBean> videoTopicsList = (ArrayList<VideoContentAcadsBean>)lrDao.getVideoSubTopicsListBySubject(moduleContent.getSubject());
			modelAndView.addObject("videoTopicsList", videoTopicsList);
			List<VideoContentAcadsBean> mapped=new ArrayList<VideoContentAcadsBean>();
			List<VideoContentAcadsBean> unMapped=new ArrayList<VideoContentAcadsBean>();
			unMapped.addAll(videoTopicsList);
			List<ModuleContentAcadsBean> moduleVideoMapeedData = lrDao.getModuleVideDataById(moduleId);
			modelAndView.addObject("moduleVideoMapeedData", moduleVideoMapeedData);
			
			for(VideoContentAcadsBean topicVideo : videoTopicsList){
				for(ModuleContentAcadsBean moduleVideo : moduleVideoMapeedData){
					if(topicVideo.getId().intValue()== moduleVideo.getVideoSubtopicId().intValue()) {
							mapped.add(topicVideo);
					}
					
				}
			}
			unMapped.removeAll(mapped);
			modelAndView.addObject("mapped", mapped);
			modelAndView.addObject("unmapped", unMapped);
			
			return modelAndView;
			
		}
	}

	//Start 
 	@RequestMapping(value = "/admin/mapModuleVideo", method = RequestMethod.GET)
	public ModelAndView mapModuleVideo(@RequestParam("moduleId") int moduleId,
										@RequestParam("videoId") int videoId,
										HttpServletRequest request,
										HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView();
		LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		
		ModuleContentAcadsBean moduleContent = lrDao.getModuleContentById(moduleId);
		if (moduleContent == null) {
			modelAndView.addObject("fileBean", new FileAcadsBean());
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Module Content Not Found");
			modelAndView.addObject("moduleContent", new ModuleContentAcadsBean()); 
			modelAndView.setViewName("moduleContentDetails");
			List<ModuleContentAcadsBean> moduleContentsList = lrDao.getAllModuleContentsList();
			modelAndView.addObject("moduleContentsList", moduleContentsList);
			
		} else {
			modelAndView.setViewName("editModuleVideoContents");
			modelAndView.addObject("moduleContent", moduleContent); 
			List<VideoContentAcadsBean> videoTopicsList = lrDao.getVideoSubTopicsListBySubject(moduleContent.getSubject());
			modelAndView.addObject("videoTopicsList", videoTopicsList);
			List<ModuleContentAcadsBean> moduleVideoMapeedData = lrDao.getModuleVideDataById(moduleId);
			modelAndView.addObject("moduleVideoMapeedData", moduleVideoMapeedData);
			
			moduleContent.setVideoSubtopicId(videoId);
			
			long mapedId = lrDao.mapModuleVideo(moduleContent);
			if(mapedId==0) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error While Saving Mapping Data");
				
			}
			request.setAttribute("success", "true");
			request.setAttribute("successMessage","Module and Vidoe Mapped Successfully ");
			return editModuleVideoContents(moduleId,request,  response);
		}
		return modelAndView;
		
	}
 	//end
 	
	//Start 
 	@RequestMapping(value = "/admin/deleteModuleVideoMap", method = RequestMethod.GET)
	public ModelAndView deleteModuleVideoMap(@RequestParam("moduleId") int moduleId,
										@RequestParam("videoId") int videoId,
										HttpServletRequest request,
										HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView();
		LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		
		ModuleContentAcadsBean moduleContent = lrDao.getModuleContentById(moduleId);
		if (moduleContent == null) {
			modelAndView.addObject("fileBean", new FileAcadsBean());
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Module Content Not Found");
			modelAndView.addObject("moduleContent", new ModuleContentAcadsBean()); 
			modelAndView.setViewName("moduleContentDetails");
			List<ModuleContentAcadsBean> moduleContentsList = lrDao.getAllModuleContentsList();
			modelAndView.addObject("moduleContentsList", moduleContentsList);
			
		} else {
			modelAndView.setViewName("editModuleVideoContents");
			modelAndView.addObject("moduleContent", moduleContent); 
			List<VideoContentAcadsBean> videoTopicsList = lrDao.getVideoSubTopicsListBySubject(moduleContent.getSubject());
			modelAndView.addObject("videoTopicsList", videoTopicsList);
			List<ModuleContentAcadsBean> moduleVideoMapeedData = lrDao.getModuleVideDataById(moduleId);
			modelAndView.addObject("moduleVideoMapeedData", moduleVideoMapeedData);
			
			moduleContent.setVideoSubtopicId(videoId);
			
			int mapedId = lrDao.deleteModuleVideoMap(moduleId, videoId);
			if(mapedId==0) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error While Deleting Data ");
				
			}
			request.setAttribute("success", "true");
			request.setAttribute("successMessage","Module and Vidoe Mapping Deleted Successfully ");
			return editModuleVideoContents(moduleId,request,  response);
			
		}
		return modelAndView;
		
	}
 	//end
 	
 	//Code for modulevideo admin view end
 	//Commented By Riya as mapping is shifted in LearningResourcesStudentController
 	/*
	@RequestMapping(value = "/learningModule", method = RequestMethod.GET)
	public String learningModule(Model m, HttpServletRequest request, HttpServletResponse respnse) {
		if (!checkSession(request, respnse)) {
			return "studentPortalRedirect";
		}
		ArrayList<String> allsubjects = applicableSubjectsForStudent(request);
		String s1 = "";
		HashMap<String, List<ModuleContentBean>> listOfContent = new HashMap<String, List<ModuleContentBean>>();
		ModuleContentBean moduleContentBean = new ModuleContentBean();
		List<ModuleContentBean> getContentList = new ArrayList<ModuleContentBean>();
		String userId = (String) request.getSession().getAttribute("userId_acads");
		LearningResourcesDAO dao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		List<String> getSubjectList = dao.getSubjectList(userId);
		Integer percentageOverall=0;
		List<ModuleContentBean> moduleDocumnentList=null;
		List<String> listOfPercentage=new ArrayList<>();
		List<Integer> listOfModuleDocumentsCount=new ArrayList<>();
		List<Integer> listOfModuleVideosCount=new ArrayList<>();
		for (int i = 0; i < allsubjects.size(); i++) {
			s1 = allsubjects.get(i);
			ArrayList<String> tempSubject = new ArrayList<String>();
			tempSubject.add(s1);
			getContentList = dao.getContentList(tempSubject);
			//tempSubject = null;
			int contentCount = getContentList != null ? getContentList.size() : 0;
			if (!(contentCount == 0)) {
				listOfContent.put(s1, getContentList);
				for (ModuleContentBean bean : getContentList) {
					 moduleDocumnentList = dao.getModuleDocumentDataById( bean.getId());
					Integer percentageForDoc=dao.getModuleDocumentPercentage(userId, bean.getId());
					Integer percentageForVideo=dao.getModuleVideoPercentage(userId, bean.getId());
					Integer n=dao.getModuleProgressCount(userId, bean.getId());
					Integer noOfModuleDocuments=dao.getModuleDocumentsCount(bean.getId());
					Integer noOfModuleVideos=dao.getModuleVideosCount(bean.getId());
					
					if(n!=0) {
						percentageOverall=(percentageForDoc+percentageForVideo)/n;
					}
					else {
						 percentageOverall=(percentageForDoc+percentageForVideo);
					}
					listOfModuleDocumentsCount.add(noOfModuleDocuments);
					listOfModuleVideosCount.add(noOfModuleVideos);
					listOfPercentage.add((Double.toString(percentageOverall)));
					//uncomment after testing
					ArrayList<Integer> allDocPercentage = new ArrayList<Integer>();
					for (ModuleContentBean docBean : moduleDocumnentList) {
						int percentage;
						allDocPercentage.add(docBean.getPercentComplete());
					}
					if(noOfModuleDocuments!=0) {
						moduleContentBean.setNoOfModuleDocuments(noOfModuleDocuments); }
						else {
						moduleContentBean.setNoOfModuleDocuments(0);	}
					
					if(noOfModuleVideos!=0) {
						moduleContentBean.setNoOfModuleVideos(noOfModuleVideos); }
						else {
						moduleContentBean.setNoOfModuleVideos(0);	}
					
					int sum = 0;
					int total = allDocPercentage.size();
					for (Integer docBean : allDocPercentage) {

						sum = sum + docBean;
					}

					Double overallModulePercentage = (double) sum / total;
					float tempFloat = percentageOverall.floatValue();
					int tempInt = Math.round(tempFloat);

					if (total != 0) {
						bean.setPercentageCombined(percentageOverall);
						bean.setPercentComplete(tempInt);
					} else {
						bean.setPercentage(0);
						bean.setPercentageCombined(0);
					}
				
				}
			}
		}
		m.addAttribute("moduleDocumnentList",moduleDocumnentList);
		m.addAttribute("listOfContent", listOfContent);
		m.addAttribute("moduleContentBean", moduleContentBean);
		return "learningModule";
	}

	@RequestMapping(value="/moduleLibraryList",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView ModuleLibraryList(@RequestParam("moduleId") Integer moduleId, 
										  @RequestParam(required=false) String colorId,
														HttpServletRequest request,
														HttpServletResponse response){
		if(!checkSession(request, response))
		{
			return new ModelAndView("studentRedirect");
		}
		String userId = (String)request.getSession().getAttribute("userId_acads");
		ModelAndView mv = new ModelAndView("moduleLibraryList");
		LearningResourcesDAO dao=(LearningResourcesDAO) act.getBean("learningResourcesDAO");
		ModuleContentBean moduleContentBean =  dao.getModuleContentById(moduleId);
		Integer modulePercentage=dao.getModuleDocumentPercentage(userId, moduleId);
		moduleContentBean.setPercentComplete(modulePercentage);
		Integer videoPercentage=dao.getModuleVideoPercentage(userId, moduleId);
		moduleContentBean.setVideoPercentage(videoPercentage);
		List<ModuleContentBean> moduleDocumnentList =  dao.getModuleDocumentDataById(moduleId);
		List<VideoContentBean> videoTopicsList=dao.getVideoSubTopicsListByModuleId(moduleId);
		VideoContentBean videoToBePlayed=null;
		List<VideoContentBean> relatedTopics =new ArrayList<VideoContentBean>();
		relatedTopics.addAll(videoTopicsList);
		if(videoTopicsList!=null && videoTopicsList.size()!=0) {
		 videoToBePlayed = videoTopicsList.get(0);
		 relatedTopics.remove(videoToBePlayed);
		}
		mv.addObject("moduleContentBean",moduleContentBean);
		//mv.addObject("moduleDocumnentList",moduleDocumnentList);
		mv.addObject("videoToBePlayed",videoToBePlayed);
		mv.addObject("relatedTopics",relatedTopics);
		mv.addObject("SERVER_PATH",SERVER_PATH);
		
		//Added to session to be used in viewVideoModuleTopic it will override each time this method is called.
		request.getSession().setAttribute("moduleDocumnentList", moduleDocumnentList);
		request.getSession().setAttribute("videoTopicsList", videoTopicsList);
		return mv;
	}
	
	@RequestMapping(value="/viewVideoModuleTopic",method=RequestMethod.GET)
	public ModelAndView viewVideoModuleTopic(@RequestParam("moduleId") Integer moduleId, 
											 @RequestParam("videoSubtopicId") long videoSubtopicId, 
											 HttpServletRequest request,
											 HttpServletResponse response){
		if(!checkSession(request, response))
		{
			return new ModelAndView("studentRedirect");
		}
		String userId = (String)request.getSession().getAttribute("userId_acads");
		
		ModelAndView mv = new ModelAndView("viewVideoModuleTopic");
		LearningResourcesDAO dao=(LearningResourcesDAO) act.getBean("learningResourcesDAO");
		ModuleContentBean moduleContentBean =  dao.getModuleContentById(moduleId);
		Integer modulePercentage=dao.getModuleDocumentPercentage(userId, moduleId);
		moduleContentBean.setPercentComplete(modulePercentage);
		Integer videoPercentage=dao.getModuleVideoPercentage(userId, moduleId);
		moduleContentBean.setVideoPercentage(videoPercentage);
		List<ModuleContentBean> moduleDocumnentList =  (List<ModuleContentBean>)request.getSession().getAttribute("moduleDocumnentList");
		List<VideoContentBean> videoTopicsList= (List<VideoContentBean>)request.getSession().getAttribute("videoTopicsList");
		VideoContentBean videoToBePlayed=null;
		List<VideoContentBean> relatedTopics =new ArrayList<VideoContentBean>();
		relatedTopics.addAll(videoTopicsList);
		if(videoTopicsList!=null && videoTopicsList.size()!=0) {
			for(VideoContentBean  tempVideo : videoTopicsList) {
				if(tempVideo.getId().longValue() == videoSubtopicId) {
				
					videoToBePlayed =tempVideo;
				}
			}
			relatedTopics.remove(videoToBePlayed);
		}

		mv.addObject("moduleContentBean",moduleContentBean);
		mv.addObject("moduleDocumnentList",moduleDocumnentList);
		mv.addObject("videoToBePlayed",videoToBePlayed);
		mv.addObject("relatedTopics",relatedTopics);
		
		return mv;
	}
	*/
	//end by Riya
	
	/*@RequestMapping(value="/viewPdfModule",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView viewPdfModule(@RequestParam("moduleId") Integer moduleId,
														Model m, 
														HttpServletRequest request,
														HttpServletResponse response){
		if(!checkSession(request, response))
		{
			return new ModelAndView("studentRedirect");
		}
		ModelAndView mv = new ModelAndView("viewPdfModule");
		LearningResourcesDAO dao=(LearningResourcesDAO) act.getBean("learningResourcesDAO");
		ModuleContentBean moduleContentBean =  dao.getModuleContentById(moduleId);
		ArrayList<String> images= new ArrayList<String>();
        try {
        String sourceDir = moduleContentBean.getFilePath() ; // Pdf files are read from this folder
        File sourceFile = new File(sourceDir);
        FileBean fb ;
        if (sourceFile.exists()) {
            PDDocument document = PDDocument.load(sourceDir);
            List<PDPage> list = document.getDocumentCatalog().getAllPages();

            String fileName = sourceFile.getName().replace(".pdf", "");             
            int pageNumber = 1;
            for (PDPage page : list) {
                BufferedImage image = page.convertToImage();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write( image, "jpg", baos );
                baos.flush();
                byte[] imageInByte = baos.toByteArray();
                baos.close();
                fb=new FileBean();
                pageNumber++;
                byte[] encodeBase64 = Base64.getEncoder().encode(imageInByte);
                String base64Encoded = new String(encodeBase64, "UTF-8");
                images.add(base64Encoded);
            }
            document.close();
            } else {
            System.err.println(sourceFile.getName() +" File not exists");
        }

    } catch (Exception e) {
          
    }
		
		mv.addObject("images",images);
		mv.addObject("moduleContentBean",moduleContentBean);
		return mv;
	}
	*/

 	//Commented By Riya as mapping is shifted in LearningResourcesStudentController
 	/*
	@RequestMapping(value="/viewVideoModule",method=RequestMethod.GET)
	public ModelAndView viewVideoModule(@RequestParam("moduleId") Integer moduleId,
														HttpServletRequest request,
														HttpServletResponse response){
		if(!checkSession(request, response))
		{
			return new ModelAndView("studentRedirect");
		}
		ModelAndView mv = new ModelAndView("viewVideoModuleTopic");
		LearningResourcesDAO dao=(LearningResourcesDAO) act.getBean("learningResourcesDAO");
		ModuleContentBean moduleContentBean =  dao.getModuleContentById(moduleId);
		mv.addObject("moduleContentBean",moduleContentBean);
		return mv;
	}*/

	//coded to get applicabel subjects start 
	public ArrayList<String> applicableSubjectsForStudent(HttpServletRequest request) {
		ArrayList<ProgramSubjectMappingAcadsBean> failSubjectsBeans = new ArrayList<>();
		ArrayList<ProgramSubjectMappingAcadsBean> allsubjects = new ArrayList<>();
		
		ArrayList<ProgramSubjectMappingAcadsBean> unAttemptedSubjectsBeans = new ArrayList<>();
		
		String sapId = (String)request.getSession().getAttribute("userId_acads");
		//So admins/faculty would see Videos Page with all videos 
		if(!sapId.startsWith("7")) {
			request.getSession().setAttribute("applicableSubjects", subjectList);
			return subjectList;
		}
		ContentDAO cdao = (ContentDAO)act.getBean("contentDAO");
		StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");
		StudentAcadsBean studentRegistrationData = cdao.getStudentRegistrationData(sapId);


		if(studentRegistrationData == null){
			//Get fail subjects content if studnet does not have registration for current sem.
			failSubjectsBeans = getFailSubjects(student);

			if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){
				allsubjects.addAll(failSubjectsBeans);
			}
			
		}else{
			//Take program from Registration data and not Student data. 
			student.setProgram(studentRegistrationData.getProgram());
			student.setSem(studentRegistrationData.getSem());
			//student.setPrgmStructApplicable(studentRegistrationData.getPrgmStructApplicable());
			//student.setWaivedOffSubjects(studentRegistrationData.getWaivedOffSubjects());
			ArrayList<ProgramSubjectMappingAcadsBean> currentSemSubjects = getSubjectsForStudent(student);
			if(currentSemSubjects != null && currentSemSubjects.size() > 0){
				allsubjects.addAll(currentSemSubjects);
				request.getSession().setAttribute("currentSemSubjects", currentSemSubjects);
			}
			
			//If current sem is 1, then there will be no failed subjects. Get failed subjects only when he is in higher semesters
			if(!"1".equals(studentRegistrationData.getSem())){
				failSubjectsBeans = getFailSubjects(student);

				if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){
					allsubjects.addAll(failSubjectsBeans);
				}
			}
		}
		
		//Get subjects never attempted or results not declared
		unAttemptedSubjectsBeans = cdao.getUnAttemptedSubjects(sapId);
		if(unAttemptedSubjectsBeans != null && unAttemptedSubjectsBeans.size() > 0){
			allsubjects.addAll(unAttemptedSubjectsBeans);
		}


		//Sort all subjects semester wise.
		Collections.sort(allsubjects);
		
		if(allsubjects.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No subjects found for you."); 
		}
		ArrayList<String> applicableSubjects=new ArrayList<>();
		for(ProgramSubjectMappingAcadsBean psmb:allsubjects){
			if(!student.getWaivedOffSubjects().contains(psmb.getSubject())) {
				applicableSubjects.add(psmb.getSubject());
			}
		}
		
		request.getSession().setAttribute("failSubjectsBeans", failSubjectsBeans);
		request.getSession().setAttribute("applicableSubjects", applicableSubjects);
		return applicableSubjects;
				
	}
	private ArrayList<ProgramSubjectMappingAcadsBean> getFailSubjects(StudentAcadsBean student) {
		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		ArrayList<ProgramSubjectMappingAcadsBean> failSubjectList;
		try {
			failSubjectList = dao.getFailSubjectsForAStudent(student.getSapid());
		} catch (Exception e) {
			failSubjectList=new ArrayList<ProgramSubjectMappingAcadsBean>();
			  
		}
		return failSubjectList;
	}
	private ArrayList<ProgramSubjectMappingAcadsBean> getSubjectsForStudent(StudentAcadsBean student) {
		ArrayList<ProgramSubjectMappingAcadsBean> programSubjectMappingList = getProgramSubjectMappingList();
		ArrayList<ProgramSubjectMappingAcadsBean> subjects = new ArrayList<>();
		for (int i = 0; i < programSubjectMappingList.size(); i++) {
			ProgramSubjectMappingAcadsBean bean = programSubjectMappingList.get(i);

			if(
					bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) 
					&& bean.getProgram().equals(student.getProgram())
					&& bean.getSem().equals(student.getSem())
					&& !student.getWaivedOffSubjects().contains(bean.getSubject())//Subjects has not already cleared it
					){
				subjects.add(bean);

			}
		}
		return subjects;
	}
	public ArrayList<ProgramSubjectMappingAcadsBean> getProgramSubjectMappingList(){
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0){
			ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
			this.programSubjectMappingList = dao.getProgramSubjectMappingList();
		}
		return programSubjectMappingList;
	} 
	
	//CRUD For Module Document Start
	
	@RequestMapping(value = "/admin/sMD", method = RequestMethod.POST)
	public ModelAndView saveModuleDocuments(@ModelAttribute ModuleContentAcadsBean moduleDocument, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView("moduleContentDetails");
		LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		Integer id= moduleDocument.getModuleId();
		String userId = (String)request.getSession().getAttribute("userId_acads");
		ModuleContentAcadsBean moduleContent = lrDao.getModuleContentById(id);
		if (moduleContent == null) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Module Content Not Found");
			modelAndView.addObject("moduleContent", new ModuleContentAcadsBean()); 

		} else {
		try {
			if ( moduleDocument.getId()==null) {
				moduleDocument.setCreatedBy(userId);

				long key = lrDao.saveModuleDocument(moduleDocument);
				setSuccess(request, "Module Document has been saved successfully");
			} else {
				moduleDocument.setLastModifiedBy(userId);
				boolean ModuleContentUpdated = lrDao.updateModuleDocuments(moduleDocument);
				if (ModuleContentUpdated) {
					setSuccess(request, "Module Document has been updated successfully");
				}else {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Module Document Failed to upload. Try Again.");
					
				}
			}
		} catch (Exception e) {
			  
		}
		}
		List<ModuleContentAcadsBean> moduleDocumnentList = new ArrayList<ModuleContentAcadsBean>();
	//	moduleDocumnentList =  lrDao.getModuleDocumentDataById(userId,id);
		modelAndView.addObject("fileBean", new FileAcadsBean());
		modelAndView.addObject("moduleDocumnentList", moduleDocumnentList); 
		modelAndView.addObject("moduleDocument", new ModuleContentAcadsBean()); 
		modelAndView.addObject("moduleContent", moduleContent); 
		
		return modelAndView;
	}

	
	@RequestMapping(value = "/admin/deleteModuleDocument", method = RequestMethod.GET)
	public ModelAndView deleteModuleDocument(@RequestParam("id") int id, HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView("moduleContentDetails");
		LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		String userId = (String) request.getSession().getAttribute("userId_acads");
		ModuleContentAcadsBean moduleDocument = lrDao.getModuleDocumentById(id);

		ModuleContentAcadsBean moduleContent = lrDao.getModuleContentById(moduleDocument.getModuleId());	
		List<ModuleContentAcadsBean> moduleDocumnentList = new ArrayList<ModuleContentAcadsBean>();
		//moduleDocumnentList =  lrDao.getModuleDocumentDataById(userId,moduleDocument.getModuleId());
		modelAndView.addObject("fileBean", new FileAcadsBean());
		modelAndView.addObject("moduleDocumnentList", moduleDocumnentList); 
		modelAndView.addObject("moduleDocument", new ModuleContentAcadsBean()); 
		modelAndView.addObject("moduleContent", moduleContent); 
		if (moduleContent == null) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Module Document Not Found");
			modelAndView.addObject("moduleContent", new ModuleContentAcadsBean()); 

		} else {

		int deletedRow = lrDao.deleteModuleDocumentsContent(id);
		if (deletedRow == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error Occured while deleting the Module Document.");
		} else {
			setSuccess(request, "ModuleDocument has been deleted successfully");
		}
		}
		return modelAndView;
		
	}
	//CRUD for module document end
	
	//REST API Start
	//pageViewed Start
	@RequestMapping(value = "/pageViewed", method = RequestMethod.GET)
	public ResponseEntity<String> pageViewed(@RequestParam("moduleId") Integer moduleId,
											@RequestParam("documentId") Integer documentId,
											@RequestParam("pageNo") Integer pageNo,
											@RequestParam(required=false) Double percentage,
											HttpServletRequest request,HttpServletResponse response) {

		
		String sapId = (String) request.getSession().getAttribute("userId_acads");
		
		HttpHeaders headers = new HttpHeaders();
		if (moduleId != null && documentId != null && pageNo != null) {
			LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
			ModuleContentAcadsBean moduleContent = lrDao.getModuleContentById(moduleId);	
			Integer noOfPage=lrDao.getNoOfPages(moduleId,documentId);
			
			percentage=((double)pageNo/(double)noOfPage)*100;
			float tempFloat = percentage.floatValue();
			int tempInt= Math.round(tempFloat);
			
			String subject=moduleContent.getSubject();
			boolean isSuccess = lrDao.updatePageViewedNo(sapId,subject,moduleId,documentId,pageNo,tempInt);
			if(isSuccess) {
				return new ResponseEntity<String>("success", headers, HttpStatus.OK);
			}
		}
		
		return new ResponseEntity<String>("error", headers, HttpStatus.BAD_REQUEST);
	}
	//pageViewed End
	
	//videoViewed Start
		@RequestMapping(value = "/videoViewed", method = RequestMethod.GET)
		public ResponseEntity<String> videoViewed(@RequestParam("moduleId") Integer moduleId,
												@RequestParam("videoTopicId") Integer videoTopicId, 
												HttpServletRequest request,HttpServletResponse response) {

			
			String sapId = (String) request.getSession().getAttribute("userId_acads");
			HttpHeaders headers = new HttpHeaders();
			if (moduleId != null && videoTopicId != null ) {
				LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
				ModuleContentAcadsBean moduleContent = lrDao.getModuleContentById(moduleId);	
				
				ModuleContentAcadsBean moduleVideoContent = lrDao.getModuleVideDataByTopicId(moduleId,videoTopicId);	
				
				String subject=moduleContent.getSubject();
				boolean isSuccess = lrDao.updateVideoViewed(sapId,subject,moduleId,moduleVideoContent.getId());
				if(isSuccess) {
					return new ResponseEntity<String>("success", headers, HttpStatus.OK);
				}
			}
			return new ResponseEntity<String>("error", headers, HttpStatus.BAD_REQUEST);
		}
		//videoViewed End
		
	//REST API End
	
	
	//download file added on 5/3/2018
	//Commented By Riya as mapping is shifted in LearningResourcesStudentController
	/*
	@RequestMapping(value = "/downloadDocument", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadDocument(HttpServletRequest request, HttpServletResponse response ){
		ModelAndView modelnView = new ModelAndView("downloadDocument");

		String fullPath = request.getParameter("filePath");
		try{
			// get absolute path of the application
			ServletContext context = request.getSession().getServletContext();
			String appPath = context.getRealPath("");

			// construct the complete absolute path of the file
			//String fullPath = appPath + filePath;		
			File downloadFile = new File(fullPath);
			FileInputStream inputStream = new FileInputStream(downloadFile);

			// get MIME type of the file
			String mimeType = context.getMimeType(fullPath);
			if (mimeType == null) {
				// set to binary type if MIME mapping not found
				mimeType = "application/octet-stream";
			}

			// set content attributes for the response
			response.setContentType(mimeType);
			response.setContentLength((int) downloadFile.length());

			// set headers for the response
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"",
					downloadFile.getName());
			response.setHeader(headerKey, headerValue);

			// get output stream of the response
			OutputStream outStream = response.getOutputStream();

			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;

			// write bytes read from the input stream into the output stream
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}
			inputStream.close();
			outStream.close();
		}catch(Exception e){
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in downloading file.");
		}
		return modelnView;
	}
*/
}
