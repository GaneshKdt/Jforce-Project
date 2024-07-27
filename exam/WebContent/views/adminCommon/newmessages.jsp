<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%! private static final String KEY_ERROR = "error";
	private static final String KEY_SUCCESS = "success"; 
	private static final String CSS_CLASS_S = "alert alert-success alert-dismissible";
	private static final String CSS_CLASS_E = "alert alert-danger alert-dismissible";
	String successMsg = null; 
	String errorMsg = null;
%>

<%
successMsg = (String)request.getAttribute(KEY_SUCCESS);
errorMsg = (String)request.getAttribute(KEY_ERROR);
try { 
%>
<div id="parentmsgdiv"><!-- placeholder to insert msgdiv from Javascript -->
<div id="msgdiv"  class="<%= ((null == successMsg && null == errorMsg) ? "" : ((null != successMsg) ? CSS_CLASS_S : CSS_CLASS_E) ) %>">
	<button id="msgbtn" type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>
	<%= ((null == successMsg && null == errorMsg) ? "" : ((null != successMsg) ? successMsg : errorMsg) ) %>
</div>
</div>

<% }catch(Exception e){} %>