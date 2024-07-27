<%if("true".equals( (String)request.getAttribute("success"))) { %>
	<div class="alert alert-success alert-dismissible row">
	<div class="col-8">
	    <%=((String)request.getAttribute("successMessage"))%>
	</div>
	<div class="col-4 float-end">
	    <button type="button" class="close" data-dismiss="alert"  id = "clearData" aria-hidden="true">  &times;  </button>
	</div>	
	</div>
<%} %>

<%if("true".equals( (String)request.getAttribute("error"))) { %>
	<div class="alert alert-danger alert-dismissible">
		<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>
		<%=((String)request.getAttribute("errorMessage"))%>
	</div>
<%} %>