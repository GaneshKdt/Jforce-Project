<div class="my-2" id="messages">
	<% if(request.getAttribute("successMessage") != null){ %>
		<div class="alert alert-success alert-dismissible">
			<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>
			<%=((String)request.getAttribute("successMessage"))%>
		</div>
	<% } %>
	
	<% if(request.getAttribute("errorMessage") != null){ %>
		<div class="alert alert-danger alert-dismissible">
			<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>
			<%=((String)request.getAttribute("errorMessage"))%>
		</div>
	<% } %>
	
	<% if(request.getAttribute("infoMessage") != null){ %>
		<div class="alert alert-info alert-dismissible">
			<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>
			<%=((String)request.getAttribute("infoMessage"))%>
		</div>
	<% } %>
	
	<% if(request.getParameter("successMessage") != null){ %>
		<div class="alert alert-success alert-dismissible">
			<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>
			<%=((String)request.getParameter("successMessage"))%>
		</div>
	<% } %>
	
	<% if(request.getParameter("errorMessage") != null){ %>
		<div class="alert alert-danger alert-dismissible">
			<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>
			<%=((String)request.getParameter("errorMessage"))%>
		</div>
	<% } %>
	
	<% if(request.getParameter("infoMessage") != null){ %>
		<div class="alert alert-info alert-dismissible">
			<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>
			<%=((String)request.getParameter("infoMessage"))%>
		</div>
	<% } %>
	
	
</div>
<script>

	function successMessage(message){
		var div = `
					<div class="alert alert-success alert-dismissible">
						<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>
					` + message + `
					</div>`;
		addMessage(div);
	}

	function errorMessage(message){
		var div = `	<div class="alert alert-danger alert-dismissible">
						<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>
					` + message + `
					</div>`;
		addMessage(div);
	}
	
	function addMessage(messageData){
		document.getElementById('messages').innerHTML += messageData;
	}
</script>

