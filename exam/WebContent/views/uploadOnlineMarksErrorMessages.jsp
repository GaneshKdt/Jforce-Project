<%@page import="com.nmims.beans.OnlineExamMarksBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>


<%

	List<OnlineExamMarksBean> errorBeanList = (ArrayList<OnlineExamMarksBean>)request.getAttribute("errorBeanList"); 
	if(errorBeanList != null && errorBeanList.size() > 0){ 
		%>
				
		<div class="alert alert-danger">
				
<%	
		for(int i = 0 ; i < errorBeanList.size() ; i++ ){
			OnlineExamMarksBean bean = (OnlineExamMarksBean)errorBeanList.get(i);
			out.println(bean.getErrorMessage());
			out.println("<br/>");
		}//End of for
		out.println("</div>");
	}//End of if

%>