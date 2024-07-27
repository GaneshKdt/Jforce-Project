<%if("true".equals( (String)request.getAttribute("success"))) { %>
	<div class="alert alert-success alert-dismissible">
		<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>
		<%=((String)request.getAttribute("successMessage"))%>
	</div>
<%} %>

<%if("true".equals( (String)request.getAttribute("error"))) { %>
	<div class="alert alert-danger alert-dismissible">
		<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>
		<%=((String)request.getAttribute("errorMessage"))%>
	</div>
<%} %>
