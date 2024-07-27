<%if("true".equals( (String)request.getAttribute("success"))) { %>
	<div class="alert alert-success alert-dismissible row">
	<div class="col-8">
	    <%=((String)request.getAttribute("successMessage"))%>
	</div>
	<div class="col-4 d-flex justify-content-end">
	    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
	</div>	
	</div>
<%} %>

<%if("true".equals( (String)request.getAttribute("error"))) { %>
	<div class="alert alert-danger alert-dismissible row">
	<div class="col-8">
	    <%=((String)request.getAttribute("errorMessage"))%>
	</div>
	<div class="col-4 d-flex justify-content-end">
	   <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
	</div>	
	</div>
<%} %>


