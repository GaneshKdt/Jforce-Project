package com.nmims.controllers;




import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.nmims.beans.FaqQuestionAnswerTableBean;
import com.nmims.beans.FaqSubCategoryBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.FaqDao;
import com.nmims.services.ISupportService;



@RestController
@RequestMapping("/m")
public class SupportRestController {
	
	@Value("${ENVIRONMENT}")
	private String ENVIRONMENT;
	
	@Autowired
	ApplicationContext act;
	
	@Autowired
	ISupportService supportService;

	@Autowired
	FaqDao faqdao;


	
	@RequestMapping(value = "/getFaqGroupType", method = RequestMethod.POST)
	public ResponseEntity<String> getGroupNameUsingMasterkey(HttpServletRequest request,
			HttpServletResponse response) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		String masterkey = request.getParameter("masterkey");
		
		
		String faqGroupType = "";

		try
		{
			faqGroupType = faqdao.getFaqGroupType(masterkey);
			
		}catch (Exception e) {
			System.out.println(e);
		}
		
		return new ResponseEntity<>(faqGroupType, headers, HttpStatus.OK);
	}
	
	

	@PostMapping(value = "/getAllFaqsListWithCategoryWise")
	public ResponseEntity<ArrayList<FaqQuestionAnswerTableBean>> getAllFaqsListWithCategoryWise(HttpServletRequest request,
			@RequestBody FaqQuestionAnswerTableBean bean) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		String groupid = bean.getFaqGroupId();
		String catid = bean.getCategoryId();
		
		
		ArrayList<FaqQuestionAnswerTableBean> faqCatlist = new ArrayList<FaqQuestionAnswerTableBean>();
		ArrayList<FaqSubCategoryBean> subcategorylist = new ArrayList<FaqSubCategoryBean>();
		
		if(groupid!=null && catid!=null )
		{
			
		
		try {
			faqCatlist = faqdao.getListOfFaqQuestionAnswer(groupid, catid, "");
			
			subcategorylist = faqdao.getFAQSubCategories(catid);
			
			for(FaqSubCategoryBean bean1: subcategorylist)
			{
				
				FaqQuestionAnswerTableBean faqQuestionAnswerTableBean = new FaqQuestionAnswerTableBean();
				faqQuestionAnswerTableBean.setQuestion(bean1.getSubCategoryName());
				faqQuestionAnswerTableBean.setAnswer("");
				faqQuestionAnswerTableBean.setFaqGroupId(groupid);
				faqQuestionAnswerTableBean.setCategoryId(catid);
				faqQuestionAnswerTableBean.setSubCategoryId(String.valueOf(bean1.getId()));
				ArrayList<FaqQuestionAnswerTableBean> faqSubCatlist = new ArrayList<FaqQuestionAnswerTableBean>();
				faqSubCatlist = faqdao.getListOfFaqQuestionAnswer(groupid, catid, String.valueOf(bean1.getId()));
				faqQuestionAnswerTableBean.setFaqSubCategoryList(faqSubCatlist);
				
				faqCatlist.add(faqQuestionAnswerTableBean);
			}
			return new ResponseEntity<ArrayList<FaqQuestionAnswerTableBean>>(faqCatlist, headers, HttpStatus.OK);	
			
		} catch (Exception e) {
		
//			e.printStackTrace();
			return new ResponseEntity<ArrayList<FaqQuestionAnswerTableBean>>(faqCatlist,headers, HttpStatus.OK);
		}
		}
		else {
			return new ResponseEntity<ArrayList<FaqQuestionAnswerTableBean>>(faqCatlist,headers, HttpStatus.OK);
		}
				
		
	}

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PostMapping("/connectMyCases")
	public ResponseEntity<Map<String, String>> connectMyCases(@RequestBody StudentStudentPortalBean bean) {
		String student_No = bean.getSapid();
		Map<String, String> map = new HashMap<>();
		String token = "";
		String url = "";
		try {
			token = supportService.getToken(student_No);
		} catch (InvalidKeyException e) {
			map.put("status", "error");
			// e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			map.put("status", "error");
			// e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			map.put("status", "error");
			// e.printStackTrace();
		} catch (BadPaddingException e) {
			map.put("status", "error");
			// e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			map.put("status", "error");
			// e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			map.put("status", "error");
			// e.printStackTrace();
		}
		if (ENVIRONMENT.equalsIgnoreCase("PROD")) {
			map.put("status", "success");
			url = "https://ngasce.secure.force.com/apex/MyTickets?token=" + token;
		} else {
			map.put("status", "success");
			url = "https://ngasce--sandbox.sandbox.my.salesforce-sites.com/apex/MyCases?token=" + token;
		}
		map.put("url", url);
		return new ResponseEntity<>(map, HttpStatus.OK);
	}
	

}
