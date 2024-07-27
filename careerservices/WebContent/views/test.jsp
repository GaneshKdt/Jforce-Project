<html>
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">

<body>
	<div class="m-5">
	
		<h2> Enter plaintext SAML </h2>
	  		<div class="form-group row m-2">
	  			<div class="col-12 p-3">
					<textarea style="min-width: 100%; height: 278px;" id="input">${ SAMLGeneratedPlainText }</textarea>
	  			</div>
	  			<div class="col-12 p-3">
			    	<button class="btn btn-primary" onclick="update()" > Update </button>
	  			</div>
	  		</div>
		<hr>	
	  	<h2> Base64</h2>
		<form  method="post" action="https://m.manpower.com/REv_SSO_WebProfile/singleSignOnServlet">
	  		<div class="form-group row m-2">
		  		
	  			<div class="col-12 p-3">
					<textarea style="min-width: 100%; height: 278px;" name="SAMLResponse" id="SAMLResponse">${ SAMLGeneratedBase64 }</textarea>
	  			</div>
			    
	  			<div class="col-12 p-3">
			    	<input class="btn btn-primary" type="submit" value="Submit" />
	  			</div>
	  			
	  		</div>
	  	</form>	
	</div>
</body>
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>

  	<script>
  		function update(){
  			var input = $("#input").val();
  			var base64 = btoa(input);
  			$("#SAMLResponse").val(base64);
  		}
  	</script>
</html>