<%if("true".equals( (String)request.getAttribute("success"))) { %>
	 <div class="alert alert-success  fade-show mt-4" role="alert">
		<%=((String)request.getAttribute("successMessage"))%>
			<button type="button" class="btn-close float-end mt-1" data-bs-dismiss="alert" aria-label="Close"></button>
	</div>
<%} %>

<%if("true".equals( (String)request.getAttribute("error"))) { %>
	<div class="alert alert-danger  fade-show mt-4" role="alert">		
		<%=((String)request.getAttribute("errorMessage"))%>
		 <button type="button" class="btn-close float-end mt-1" data-bs-dismiss="alert" aria-label="Close"></button>
	</div>
<%} %>
