<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Email Templates" name="title" />
</jsp:include>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/js/ckeditor/ckeditor_responsive_table/plugin.css">
	
<style>
	.snackbar{
	    background: #5b5a5a;
	    width: 30%;
	    padding: 20px;
	    margin: 25px;
	    text-align: center;
	    border-radius: 4px;
   	 	position: fixed;
    	z-index: 2;
	    left: 50%;
	    transform: translate(-50%, 0);
	    display: none;
	}
</style>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container">
		<div class="container-fluid customTheme">

			<%@ include file="messages.jsp"%>

			<div class="panel-body" style="margin-top: 40px;">
				<h3 style="margin-top: 10px;">Email Templates</h3>
				
				<div id='snackbar' class='snackbar'>
					<p style="color: white;">Updated the mail successfully</p>
				</div>
				
				<div class='row'>
					<div class="form-group col-md-12" style="margin-top: 10px;">
						<select class="form-control" id='module' style="text-transform: capitalize;" onchange="moduleChangeHandler()">
							<option value="0">Select Module</option>
							<c:forEach var="module" items="${ modules }">
								<option value="${module.id}" data-vendor='${module.provider}' style="text-transform: capitalize;">${module.module} - ${module.topic}</option>
							</c:forEach>
						</select>
					</div>
				</div>
				
				<div class="row">
					<div class="form-group col-md-12">
						<h2>Message Details</h2>
						<label for="from">From</label>
						<input type="text" class="form-control" id="from">
						<label for="fromEmailId">From Email</label>
						<input type="text" class="form-control" id="fromEmailId">
						<label for="vendor">Vendor</label>
						<select id="vendor">
							<option selected="selected">Select Vendor</option>
							<option value="SMTP">SMTP</option>
							<option value="TNMails">TNMails</option>
							<option value="AWSSES">AWSSES</option>
						</select>
						<label for="subject">Subject</label>
						<input type="text" class="form-control" id="subject">
						<label for="body">Body</label>
						<textarea class="form-control" id="body" rows="20">
						</textarea>
						<button class='btn btn-primary' style="margin-top:10px; border-radius: 4px;" id='updateDetials' onclick="updateMessage()">
						Update</button>
					</div>
				</div>
				
			</div>

		</div>

	</section>

	<jsp:include page="footer.jsp" />
	
	<script type="text/javascript" src="//cdn.ckeditor.com/4.14.0/standard-all/ckeditor.js"></script>
	<script src="${pageContext.request.contextPath}/assets/js/ckeditor/ckeditor_responsive_table/plugin.js"></script>
	
	<script>

		let vendor = $('#module').find(':selected').attr('data-vendor');
		let vendorUpdated = false
	
		bodyObject =  CKEDITOR.replace(
			'body',
			{
				extraPlugins : 'uploadimage,image2,mathjax,autogrow,colorbutton,font,justify,print,tableresize,uploadfile,pastefromword,liststyle,pagebreak',
				//      extraPlugins: 'colorbutton,font,justify,print,tableresize,uploadimage,uploadfile,pastefromword,liststyle,pagebreak',

				
				mathJaxLib : 'https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.0/MathJax.js?config=TeX-AMS_HTML',
				uploadUrl : '/exam/ckeditorFileUpload',

			      // Configure your file manager integration. This example uses CKFinder 3 for PHP.
			      filebrowserBrowseUrl: '/exam/ckeditorFileUpload',
			      filebrowserImageBrowseUrl: '/exam/ckeditorFileUpload',
			      filebrowserUploadUrl: '/exam/ckeditorFileUpload',
			      filebrowserImageUploadUrl: '/exam/ckeditorFileUpload',
				

			      // Load the default contents.css file plus customizations for this sample.
			      contentsCss: [
			        'https://cdn.ckeditor.com/4.14.0/full-all/contents.css',
			        'https://ckeditor.com/docs/vendors/4.14.0/ckeditor/assets/css/pastefromgdocs.css'
			        
			      ],

			      // Configure the Enhanced Image plugin to use classes instead of styles and to disable the
			      // resizer (because image size is controlled by widget styles or the image takes maximum
			      // 100% of the editor width).
			      image2_alignClasses: ['image-align-left', 'image-align-center', 'image-align-right'],
			      image2_disableResizer: true,
			      
			      autoGrow_minHeight: 200,
			      autoGrow_maxHeight: 600,
			      autoGrow_bottomSpace: 50,

			}
			
		);

		function moduleChangeHandler(){

			let moduleId = $('#module').val();
			
			if(	moduleId ==	0 ){
				$('#from').val('');
				$('#fromEmailId').val('');
				$('#subject').val('');
				$('#vendor option:contains()').prop('selected', true)
				CKEDITOR.instances.body.setData( "", function() {
					this.checkDirty();  // true
				});
				
				return;
			}
				
			let body = {
					'moduleId':moduleId
					};
			
			$.ajax({
				type : "POST",
				contentType : "application/json",
				url : "/studentportal/getMessageForModule",   
				data : JSON.stringify(body),
				success : function(data) {

					$('#from').val(data.from)
					$('#fromEmailId').val(data.fromEmailId)
					$('#subject').val(data.subject)
					
					if(!vendor || !vendorUpdated)
						vendor = $('#module').find(':selected').attr('data-vendor')
						
					$('#vendor option:contains('+vendor+')').prop('selected', true)
					
					CKEDITOR.instances.body.setData( data.body, function() {
						this.checkDirty();  // true
					});
					
					vendorUpdated = false
					
				},
				error : function(e) {
				
					console.log("ERROR: ", e);
					alert("Please Refresh The Page.")
					
				}
			});

		}

		function updateMessage(){

			let from = $('#from').val();
			let fromEmailId = $('#fromEmailId').val();
			let subject = $('#subject').val();
			let provider = $('#vendor :selected').val();
			let moduleId = $('#module').val();
			let key = 'body';
			let body = CKEDITOR.instances[key].getData();
			
			if(provider){
				if(provider == 'Select Vendor'){
					alert('Please select a Vendor.')
					return
				}
				vendor = provider
				vendorUpdated = true
			}

			let emailMessage = {
						'moduleId':moduleId,
						'from':from,
						'fromEmailId':fromEmailId,
						'subject':subject,
						'provider':provider,
						'body':body,
					};
			
			$.ajax({
				type : "POST",
				contentType : "application/json",
				url : "/studentportal/updateMessage",   
				data : JSON.stringify(emailMessage),
				success : function(data) {

		    		$('#snackbar').fadeIn(10);
		    		$('#snackbar').fadeOut(2000);
		    		moduleChangeHandler();
					
				},
				error : function(e) {
				
					console.log("ERROR: ", e);
					alert("Please Refresh The Page.")
					
				}
			});

		}
		
	</script>
</body>
</html>
