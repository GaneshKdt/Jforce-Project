<%@page import="com.nmims.beans.FacultyCourseBean"%>
<%@page import="com.nmims.beans.FacultyUnavailabilityBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>




<%

	List<FacultyCourseBean> errorBeanList = (ArrayList<FacultyCourseBean>)request.getAttribute("errorBeanList"); 
	if(errorBeanList != null && errorBeanList.size() > 0){ 
		%>
		<div class="alert alert-danger">
		
<%	
		for(int i = 0 ; i < errorBeanList.size() ; i++ ){
			FacultyCourseBean bean = (FacultyCourseBean)errorBeanList.get(i);
			out.println(bean.getErrorMessage());
			out.println("<br/>");
		}//End of for
		out.println("</div>");
	}//End of if

%>