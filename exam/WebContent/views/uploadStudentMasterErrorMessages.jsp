<%@page import="com.nmims.beans.StudentExamBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>


<%

	List<StudentExamBean> errorBeanList = (ArrayList<StudentExamBean>)request.getAttribute("errorBeanList"); 
	if(errorBeanList != null && errorBeanList.size() > 0){ 
		%>
				
		<div class="alert alert-danger">
		
		
		
<%	
		for(int i = 0 ; i < errorBeanList.size() ; i++ ){
			StudentExamBean bean = (StudentExamBean)errorBeanList.get(i);
			out.println(bean.getErrorMessage());
			out.println("<br/>");
		}//End of for
		out.println("</div>");
	}//End of if

%>