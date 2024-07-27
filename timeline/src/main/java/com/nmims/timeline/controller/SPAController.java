package com.nmims.timeline.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SPAController {
	
	@RequestMapping(value = "/login")
    public String index(@RequestParam String sapid ) {
		//System.out.println("IN login got sapid : "+sapid);
		return "/index.html";
	}
	
	@RequestMapping(value = "/home")
	public String home() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/changePassword")
    public String changePassword() {
		return "/index.html";
    }
	
	@RequestMapping(value = "/watchVideo")
	public String watchVideo() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/calendar")
	public String calendar() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/todo")
	public String todo() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/selectSR")
	public String selectSR() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/changeInDOB")
	public String changeInDOB() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/changeInID")
	public String changeInID() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/changeInName")
	public String changeInName() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/changeInPhotograph")
	public String changeInPhotograph() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/duplicateFeeReceipt")
	public String duplicateFeeReceipt() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/duplicateICard")
	public String duplicateICard() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/duplicateStudyKit")
	public String duplicateStudyKit() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/issuanceOfMarksheet")
	public String issuanceOfMarksheet() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/marksheetSummary")
	public String marksheetSummary() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/marksheetRequestConfirmation")
	public String marksheetRequestConfirmation() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/programDereg")
	public String programDereg() {
		return "/index.html";
    }
	
	@RequestMapping(value = "/srCreated")
	public String srCreated() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/publicProfile")
	public String publicProfile() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/instructorProfile")
	public String instructorProfile() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/studentProfile")
	public String studentProfile() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/InstructorProfileNew")
	public String InstructorProfileNew() {
		 return "/index.html";
	}
	
	@RequestMapping(value = "/myCommunications")
    public String myCommunications() {
		return "/index.html";
    }

	@RequestMapping(value = "/notification")
    public String notification() {
		return "/index.html";
    }
	
	@RequestMapping(value = "/notificationsAnnouncements")
	public String notificationsAnnouncements() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/eLearning")
	public String eLearning() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/postAQuery")
	public String postAQuery() {
		return "/index.html";
	}

	@RequestMapping(value = "/testString")
	public String testString() {
		return "testString";
    }
	
	@RequestMapping(value = "/preReads")
    public String preReads() {
		return "/index.html";
    }
	
	@RequestMapping(value = "/documentViewer")
    public String documentViewer() {
		return "/index.html";
    }
	
	@RequestMapping(value = "/sessionPlan")
	public String sessionPlan() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/sessionPlanModule")
    public String sessionPlanModule() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/contactUs")
    public String contactUs() {
		return "/index.html";
    }

	@RequestMapping(value = "/courseExamHome")
	public String courseExamHome() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/eLearn")
	public String eLearn() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/chatTest")
	public String chatTest() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/startIATest")
    public String startIATest() {
		return "/index.html";
    }
	
	@RequestMapping(value = "/sessionFeedback")
    public String sessionFeedback() {
		return "/index.html";
    }
	
	@RequestMapping(value = "/courseQueries")
    public String courseQueries() {
		return "/index.html";
    }
	
	@RequestMapping(value = "/downloadMarksheet")
    public String downloadMarksheet() {
		return "/index.html";
    }
	
	@RequestMapping(value = "/redispatchOfStudyKit")
    public String redispatchOfStudyKit() {
		return "/index.html";
    }
	
	@RequestMapping(value = "/singleBook")
    public String singleBook() {
		return "/index.html";
    }
	
	@RequestMapping(value = "/issuanceOfFinalCertificate")
    public String issuanceOfFinalCertificate() {
		return "/index.html";
    }
	
	@RequestMapping(value = "/examBookingError")
    public String examBookingError() {
		return "/index.html";
    }
	
	@RequestMapping(value = "/examBookingSuccess")
    public String examBookingSuccess() {
		return "/index.html";
    }
	
	@RequestMapping(value = "/examBooking")
    public String examBooking() {
		return "/index.html";
    }
	
	@RequestMapping(value = "/newExamBooking")
    public String newExamBooking() {
		return "/index.html";
    }
	
	@RequestMapping(value = "/reScheduleExamBooking")
    public String reScheduleExamBooking() {
		return "/index.html";
    }
	
	@RequestMapping(value = "/viewTestResults")
    public String viewTestResults() {
		return "/index.html";
    }
	
	@RequestMapping(value = "/hallticketDownload")
    public String hallticketDownload() {
		return "/index.html";
    }
	
	@RequestMapping(value = "/attachmentViewer")
    public String attachmentViewer() {
		return "/index.html";
    }
	
	@RequestMapping(value = "/sRPaymentSuccess")
    public String sRPaymentSuccess() {
		return "/index.html";
    }
	
	@RequestMapping(value = "/SRStatusFailure")
    public String SRStatusFailure() {
		return "/index.html";
    }
	
	@RequestMapping(value = "/courseSpecialisationMain")
    public String courseSpecialisationMain() {
		return "/index.html";
    }
	
	@RequestMapping(value = "/courseSpecialisation")
    public String courseSpecialisation() {
		return "/index.html";
    }
	
	@RequestMapping(value = "/notifications")
	 public String notifications() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/logout")
	 public String logout() {
		return "/index.html";
	}

	@RequestMapping(value = "/startTEEAssessment")
	 public String startTEEAssessment() {
		return "/index.html";
	}

	///timeline/testIAQuickJoin
	@RequestMapping(value = "/testIAQuickJoin")
	 public String testIAQuickJoin() {
		return "/index.html";
	}
	
	///timeline/startIATestQuickJoin
	@RequestMapping(value = "/startIATestQuickJoin")
	 public String startIATestQuickJoin() {
		return "/index.html";
	}
	
}
