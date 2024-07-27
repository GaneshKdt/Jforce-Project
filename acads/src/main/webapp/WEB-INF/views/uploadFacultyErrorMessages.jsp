<%@page import="com.nmims.beans.FacultyBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>


<%
	List<FacultyBean> errorBeanList = (ArrayList<FacultyBean>)request.getAttribute("errorBeanList"); 
	if(errorBeanList != null && errorBeanList.size() > 0){ 
		%>
				
		<div class="alert alert-danger">
<%	
		for(int i = 0 ; i < errorBeanList.size() ; i++ ){
			FacultyBean bean = (FacultyBean)errorBeanList.get(i);
			out.println(bean.getErrorMessage());
			out.println("<br/>");
		}//End of for
		out.println("</div>");
	}//End of if

%>