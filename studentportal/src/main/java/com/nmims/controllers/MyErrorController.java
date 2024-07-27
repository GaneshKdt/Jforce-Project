package com.nmims.controllers;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MyErrorController {
	@RequestMapping(value = "/error", method = RequestMethod.GET)
	public String handleError(HttpServletRequest request,Model m) {
	    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
	    Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
	    Object errorType = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION_TYPE);
	    Object errorException = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
	    Object errorURI = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
	    Object errorServletName = request.getAttribute(RequestDispatcher.ERROR_SERVLET_NAME);
	    

	    try {
//			System.out.println("status : "+(String)status);
//			System.out.println("errorMessage : "+(String)errorMessage);
//			System.out.println("errorType : "+(String)errorType);
//			System.out.println("errorException : "+(String)errorException);
//			System.out.println("errorURI : "+(String)errorURI);	
//			System.out.println("errorServletName : "+(String)errorServletName);
		} catch (Exception e) {
			//e.printStackTrace();
		}
	    
	    if (status != null) {
	        Integer statusCode = Integer.valueOf(status.toString());
	    
	        if(statusCode == HttpStatus.NOT_FOUND.value()) {
	        	m.addAttribute("errorStatusCode","404");
	        }
	        else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
	        	m.addAttribute("errorStatusCode","500");
	        }else {
	        	m.addAttribute("errorStatusCode","NA");
	        }
	    }else {
        	m.addAttribute("errorStatusCode","NA");
        }
	    return "jsp/customError";
	}


    @RequestMapping(value = "errors", method = RequestMethod.GET)
    public ModelAndView renderErrorPage(HttpServletRequest httpRequest) {
        
        ModelAndView errorPage = new ModelAndView("jsp/customError");
        String errorMsg = "";
        
        

	    try {
	    	Object status = httpRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		    Object errorMessage = httpRequest.getAttribute(RequestDispatcher.ERROR_MESSAGE);
		    Object errorType = httpRequest.getAttribute(RequestDispatcher.ERROR_EXCEPTION_TYPE);
		    Object errorException = httpRequest.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
		    Object errorURI = httpRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
		    Object errorServletName = httpRequest.getAttribute(RequestDispatcher.ERROR_SERVLET_NAME);
		    
//			System.out.println("status : "+(String)status.toString());
//			System.out.println("errorMessage : "+(String)errorMessage);
//			System.out.println("errorURI : "+(String)errorURI);	
//			System.out.println("errorServletName : "+(String)errorServletName);
//			System.out.println("errorException : "+((Exception)errorException).toString());
//			System.out.println("errorType : "+((String)errorType!=null?errorType:"").toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}

        
        int httpErrorCode = getErrorCode(httpRequest);

        switch (httpErrorCode) {
            case 400: {
                errorMsg = "Http Error Code: 400. Bad Request";
                break;
            }
            case 401: {
                errorMsg = "Http Error Code: 401. Unauthorized";
                break;
            }
            case 403: {
                errorMsg = "Http Error Code: 403. Access denied";
                break;
            }
            case 404: {
                errorMsg = "Http Error Code: 404. Resource not found";
                break;
            }
            case 500: {
                errorMsg = "Http Error Code: 500. Internal Server Error";
                break;
            }
        }
        errorPage.addObject("errorMsg", errorMsg);
        return errorPage;
    }
    
    private int getErrorCode(HttpServletRequest httpRequest) {
        return (Integer) httpRequest
          .getAttribute("javax.servlet.error.status_code");
    }

	
//	@Override
//	public String getErrorPath() {
//		// TODO Auto-generated method stub
//		System.out.println("===> IN getErrorPath() ");
//		return "/error";
//	}
}

/*
 @Controller
public class ErrorController {

    @RequestMapping(value = "errors", method = RequestMethod.GET)
    public ModelAndView renderErrorPage(HttpServletRequest httpRequest) {
        
        ModelAndView errorPage = new ModelAndView("jsp/errorPage");
        String errorMsg = "";
        int httpErrorCode = getErrorCode(httpRequest);

        switch (httpErrorCode) {
            case 400: {
                errorMsg = "Http Error Code: 400. Bad Request";
                break;
            }
            case 401: {
                errorMsg = "Http Error Code: 401. Unauthorized";
                break;
            }
            case 404: {
                errorMsg = "Http Error Code: 404. Resource not found";
                break;
            }
            case 500: {
                errorMsg = "Http Error Code: 500. Internal Server Error";
                break;
            }
        }
        errorPage.addObject("errorMsg", errorMsg);
        return errorPage;
    }
    
    private int getErrorCode(HttpServletRequest httpRequest) {
        return (Integer) httpRequest
          .getAttribute("javax.servlet.error.status_code");
    }
}
 * */
