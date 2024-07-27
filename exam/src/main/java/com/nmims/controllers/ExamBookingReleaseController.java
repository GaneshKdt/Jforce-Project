package com.nmims.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.nmims.dto.ExamBookingTransactionDTO;
import com.nmims.factory.IExamBookingSeatReleaseFactory;
import com.nmims.helpers.MailSender;
import com.nmims.services.ExamBookingCancelService;
import com.nmims.services.ICacheService;
import com.nmims.services.IExamBookingSeatReleaseService;
/**
 * 
 * @author Siddheshwar_K
 *
 */
@Controller
public class ExamBookingReleaseController extends BaseController{
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}") 
	private List<String> ACAD_YEAR_LIST;
	
	@Autowired
	private IExamBookingSeatReleaseFactory examBookingSeatReleaseFactory;
	
	@Autowired 
	@Qualifier("examBookingCancelService")
	private ExamBookingCancelService examBookingCancelService;
	
	@Autowired
	private ICacheService cacheService;
	
	@Autowired
	private MailSender mailSender;
	
	public static final Logger logger = LoggerFactory.getLogger("examBookingSeatRelease");
	
	@GetMapping("/admin/searchBookingsToReleaseForm")
	public String searchBookingsToReleaseForm(HttpServletRequest request, HttpServletResponse response, Model model) {
		logger.debug("Search bookings to release form launch - START");
		
		//Create DTO object for from backing.
		ExamBookingTransactionDTO booking = new ExamBookingTransactionDTO();
		
		//Add product type in model
		booking.setProductType("PRODUCT_PG");
		
		//Add academic year list and DTO into model
		model.addAttribute("yearList", ACAD_YEAR_LIST);
		model.addAttribute("booking", booking);

		logger.debug("Search bookings to release form launch - END");
		
		//return logical view name
		return "releaseBooking";
	}//searchBookingsToReleaseForm()
	
	@PostMapping("/admin/searchBookingsToRelease")
	public String searchBookingsToRelease(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute ExamBookingTransactionDTO booking, Model model) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		logger.debug("Search bookings to release - START");
		
		//Add DTO in session.
		request.getSession().setAttribute("booking", booking);
		
		try {
			//Get the Seat Release Service class instance from the factory.
			IExamBookingSeatReleaseService seatReleaseService = examBookingSeatReleaseFactory.getBookingReleaseInstance(booking.getProductType());
			
			//get confirmed And Released Bookings subjects details
			List<ExamBookingTransactionDTO> confirmedAndReleasedBookingsList =  seatReleaseService.searchConfirmedAndReleasedBookings(booking);
			
			//Add confirmed And Released Bookings subjects details into model and session as well.
			request.getSession().setAttribute("confirmedBookings", confirmedAndReleasedBookingsList);
			model.addAttribute("confirmedBookings", confirmedAndReleasedBookingsList);
			
			//Set the total count of bookings in model.
			model.addAttribute("rowCount", confirmedAndReleasedBookingsList.size());
			
			logger.info(confirmedAndReleasedBookingsList.size()+": Confirmed and Released Subjects Found.");
		}
		catch (Exception e) {
			//Set error message to the request if any exception occurred.
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No booked subjects found for this student."+e.getMessage());
			
			logger.error("Error occurred while searching the confirmed and released subjects details - Error Message : "+e.getMessage());
		}
		finally {
			//Add academic year list and search details for form backing in model.  
			model.addAttribute("yearList", ACAD_YEAR_LIST);
			model.addAttribute("booking", booking);
		}
		
		try {
			//Get corporate Center User mapping from the In-Memory cache and add to session.
			Map<String,String> corporateCenterUserMapping = cacheService.getCorporateCenterUserMapping();
			request.getSession().setAttribute("corporateCenterUserMapping", corporateCenterUserMapping);
			
			//Check seat release user is corporate or not if corporate then get corporate exam centerInName map else
			// else get exam centerIdName map and set to the session scope.
			if(corporateCenterUserMapping.containsKey(booking.getSapid())){
				request.getSession().setAttribute("examCenterIdNameMap", cacheService.getCorporateExamCenterIdNameMap());
			}else{
				request.getSession().setAttribute("examCenterIdNameMap", cacheService.getExamCenterIdNameMap());
			}
		}
		catch (Exception e) {
			logger.error("Error occurred while Exam Center Id Name Map - Error Message : "+e.getMessage());
		}
		
		logger.debug("Search bookings to release - END");
		
		//return logical view name
		return "releaseBooking";
	}//searchBookingsToRelease()
	
	
	@SuppressWarnings("unchecked")
	@PostMapping("/admin/releaseBookings")
	public String releaseBookings(HttpServletRequest request, HttpServletResponse response, 
			@ModelAttribute("booking") ExamBookingTransactionDTO exmBkingTxnDTO, Model model){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		logger.debug("Release bookings - START");

		boolean isCorporateStudent = false;
		String emailId = "";
		String lastModifiedBy = (String)request.getSession().getAttribute("userId");
		exmBkingTxnDTO.setLastModifiedBy(lastModifiedBy);
		
		//Read charges status from the request and set to DTO bean
		String chargesStatus = request.getParameter("noCharges");
		exmBkingTxnDTO.setChargesStatus(chargesStatus);
		
		//Read confirmed and released booking details from the session
		List<ExamBookingTransactionDTO> confirmedBookingsList = (List<ExamBookingTransactionDTO>) request.getSession().getAttribute("confirmedBookings");
		
		try {
			//Get corporate Center User mapping to Check user for which seat to be release is corporate or not from the In-Memory cache 
			isCorporateStudent = cacheService.getCorporateCenterUserMapping().containsKey(exmBkingTxnDTO.getSapid());
		} catch (Exception e) {
			logger.error("Error occurred while fetching Corporate Center User Mapping - Error Message :"+e.getMessage());
		}
		
		try {
			//Get emailId of a user.
			emailId = confirmedBookingsList.get(0).getEmailId(); 
			
			//Get the Seat Release Service class instance from the factory.
			IExamBookingSeatReleaseService seatReleaseService = examBookingSeatReleaseFactory.getBookingReleaseInstance(exmBkingTxnDTO.getProductType());
			
			logger.info("Before release confirmed and released subjects list :"+confirmedBookingsList);
			logger.info(exmBkingTxnDTO.getReleaseSubjects()+": Subjects has to be release.");
			logger.info(exmBkingTxnDTO.getReleaseReasonsList()+": release reasons list.");

			//Execute release booking 
			confirmedBookingsList = seatReleaseService.releaseExamBookingSeats(exmBkingTxnDTO, confirmedBookingsList, isCorporateStudent);
			
			//Add Modified confirmed And Released Bookings subjects details into model and session as well.
			model.addAttribute("confirmedBookings", confirmedBookingsList);
			request.getSession().setAttribute("confirmedBookings", confirmedBookingsList);
			
			logger.info("Updated confirmed and released subjects list :"+confirmedBookingsList);
			
			//Set the total count of bookings in model.
			model.addAttribute("rowCount", confirmedBookingsList.size());
			
			//Set success message to the request.
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Seats released Successfully");
		}
		catch (Exception e) {
			//Set error message to the request if any exception occurred.
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in releasing the seats. Please try again"+e.getMessage());
			
			logger.error("Error occured while releasing the bookings() - Error Message :"+e.getMessage());
		}
		finally {
			exmBkingTxnDTO.setReleaseReasonsList(new ArrayList<>(1));
			//Add academic year list and search details for form backing in model.  
			model.addAttribute("booking", exmBkingTxnDTO);
			model.addAttribute("yearList", ACAD_YEAR_LIST);
		}
		
		logger.debug("Sending Seat Release Confirmation Email to  EmailId :"+emailId +" of student SAPID :"+exmBkingTxnDTO.getSapid());
		//Send seat release email notification to the student for particular subject(s).
		mailSender.sendSeatsRealseEmail(exmBkingTxnDTO.getSapid(), (ArrayList<String>)exmBkingTxnDTO.getReleaseSubjects(), 
				emailId, chargesStatus);
		
		logger.debug("Release bookings - END");
		
		//return logical view
		return "releaseBooking";
	}
	
	@PostMapping("/admin/cancelExamBookings")
	public String cancelExamBookings(HttpServletRequest request, HttpServletResponse response, 
			@ModelAttribute("booking") ExamBookingTransactionDTO exmBkingTxnDTO, Model model)
	{
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		logger.debug("Cancel bookings - START");

		boolean isCorporateStudent = false;
		String emailId = "";
		String lastModifiedBy = (String)request.getSession().getAttribute("userId");
		exmBkingTxnDTO.setLastModifiedBy(lastModifiedBy);
		
		//Read charges status from the request and set to DTO bean
		String chargesStatus = request.getParameter("refund");
		exmBkingTxnDTO.setChargesStatus(chargesStatus);
		
		//Read confirmed and released and cancelled booking details from the session
		List<ExamBookingTransactionDTO> confirmedBookingsList = (List<ExamBookingTransactionDTO>) request.getSession().getAttribute("confirmedBookings");
		
		try {
			//Get corporate Center User mapping to Check user for which seat to be cancelled is corporate or not from the In-Memory cache 
			isCorporateStudent = cacheService.getCorporateCenterUserMapping().containsKey(exmBkingTxnDTO.getSapid());
		} catch (Exception e) {
			logger.error("Error occurred while fetching Corporate Center User Mapping - Error Message :"+e.getMessage());
		}
		
		try {
			//Get emailId of a user.
			emailId = confirmedBookingsList.get(0).getEmailId(); 

			logger.info("Before cancelled confirmed and cancelled subjects list :"+confirmedBookingsList);
			logger.info(exmBkingTxnDTO.getReleaseSubjects()+": Subjects has to be cancelled.");
			logger.info(exmBkingTxnDTO.getReleaseReasonsList()+": cancelled reasons list.");

			ArrayList<ExamBookingTransactionDTO> subjectExamDateTime=examBookingCancelService.getCancelledSubjectStartDateTime((ArrayList<String>)exmBkingTxnDTO.getReleaseSubjects(),confirmedBookingsList);
			
			//Execute Cancel booking 
			confirmedBookingsList=examBookingCancelService.cancelExamBookingSeats(exmBkingTxnDTO, confirmedBookingsList, isCorporateStudent);
			
			//Add Modified confirmed And Released And Cancelled Bookings subjects details into model and session as well.
			model.addAttribute("confirmedBookings", confirmedBookingsList);
			request.getSession().setAttribute("confirmedBookings", confirmedBookingsList);
			
			logger.info("Updated confirmed and released and cancelled subjects list :"+confirmedBookingsList.toString());
			
			//Set the total count of bookings in model.
			model.addAttribute("rowCount", confirmedBookingsList.size());
			
			//Set success message to the request.
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Seats canceled Successfully");
			
			logger.debug("Sending Seat Cancel Confirmation Email to  EmailId :"+emailId +" of student SAPID :"+exmBkingTxnDTO.getSapid());
			//Send seat cancelled email notification to the student for particular subject(s).
			
			mailSender.sendSeatsCancelEmail(exmBkingTxnDTO.getSapid(), subjectExamDateTime, 
					emailId);
		}
		catch (Exception e) {
			//Set error message to the request if any exception occurred.
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in cancelling the seats. Please try again"+e.getMessage());
			
			logger.error("Error occured while cancelling the bookings() - Error Message :"+e.getMessage());
		}
		finally {
			exmBkingTxnDTO.setReleaseReasonsList(new ArrayList<>(1));
			//Add academic year list and search details for form backing in model.  
			model.addAttribute("booking", exmBkingTxnDTO);
			model.addAttribute("yearList", ACAD_YEAR_LIST);
		}
		
		logger.debug("Cancel bookings - END");
		
		//return logical view
		return "releaseBooking";
	}
	
}
