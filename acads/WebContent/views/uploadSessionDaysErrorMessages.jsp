<%@page import="com.nmims.beans.SessionDayTimeAcadsBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>


<%

	List<SessionDayTimeAcadsBean> errorBeanList = (ArrayList<SessionDayTimeAcadsBean>)request.getAttribute("errorBeanList"); 
	if(errorBeanList != null && errorBeanList.size() > 0){ 
		%>
				
		<div class="alert alert-danger">
	
		
<%	
		for(int i = 0 ; i < errorBeanList.size() ; i++ ){
			SessionDayTimeAcadsBean bean = (SessionDayTimeAcadsBean)errorBeanList.get(i);
			out.println(bean.getErrorMessage());
			out.println("<br/>");
		}//End of for
		out.println("</div>");
	}//End of if

%>