<!-- bootstrap 5.2.3 used -->
<%if("true".equals( (String)request.getAttribute("success"))) { %>
		<div class="alert alert-success  alert-dismissible" role="alert">
		  <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close" ></button>
		  <%=((String)request.getAttribute("successMessage"))%>
		</div>
<%} %>

<%if("true".equals( (String)request.getAttribute("error"))) { %>
	<div class="alert alert-danger alert-dismissible " role="alert">
  <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
  <%=((String)request.getAttribute("errorMessage"))%>
</div>

<%} %>
