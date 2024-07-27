
 <!DOCTYPE html>


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
 

<!-- Froala wysiwyg editor CSS -->
 <jsp:include page="../jscss.jsp">  
	<jsp:param value="Upload Assignment Files" name="title" />
</jsp:include>


<link
	href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css"
	rel="stylesheet">
<script src="https://code.jquery.com/jquery-3.3.1.js"
	integrity="sha256-2Kok7MbOyxpgUVvAk/HJ2jigOSYS2auK4Pfzbm7uH60="
	crossorigin="anonymous"></script>
<script
	src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>

<!-- Roboto Web Font -->
<link
	href="https://fonts.googleapis.com/css?family=Roboto:regular,bold,italic,thin,light,bolditalic,black,medium&amp;lang=en"
	rel="stylesheet">

<!-- Include CSS for icons. -->
<link
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.4.0/css/font-awesome.min.css"
	rel="stylesheet" type="text/css" />

<!-- Include Editor style. -->
<link
	href="https://cdnjs.cloudflare.com/ajax/libs/froala-editor/2.5.1/css/froala_editor.pkgd.min.css"
	rel="stylesheet" type="text/css" />
<link
	href="https://cdnjs.cloudflare.com/ajax/libs/froala-editor/2.5.1/css/froala_style.min.css"
	rel="stylesheet" type="text/css" />
<link data-require="sweet-alert@*" data-semver="0.4.2" rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/sweetalert/1.1.3/sweetalert.min.css" />


<link rel="stylesheet"
	href="assets/js/ckeditor/ckeditor_responsive_table/plugin.css">

<style>
	.bgsuccess{color: #fff; 
	    background-color: #5cb85c;
	    border-color: #4cae4c;
	    color: #fff!important;
	    }
	.cardstyle{
	    padding:2rem;
	}
	.card-body .rotate i {
	    color: rgba(20, 20, 20, 0.15);
	    position: absolute;
	    left: 0;
	    left: auto;
	    right: 24px; 
	    bottom: 3px;
	    display: block;
	    -webkit-transform: rotate(-44deg);
	    -moz-transform: rotate(-44deg);
	    -o-transform: rotate(-44deg);
	    -ms-transform: rotate(-44deg);
	    transform: rotate(-44deg);
	}
	.dash-count{
		color:white!important;
	} 
</style>
<body class="inside">

	<%-- <%@ include file="header.jsp"%> --%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
								 	
			<div class="row"><legend>Upload Assignment Question</legend></div>
			
			<form:form modelAttribute="questionsBean" method="post"	 action="saveAssignmentQuestionsInQpTable">
				<div class="qn_div"> 
					 <form:hidden path="year" value="${questionsBean.examYear }"   />
				       <form:hidden path="month" value="${questionsBean.examMonth }"  />  
				       <form:hidden path="pss_id" value="${questionsBean.pss_id }"/> 
				       <form:hidden path="subject" value="${questionsBean.subject }"/> 
				       <form:hidden path="facultyId" value="${questionsBean.facultyId }"/> 
				       <form:hidden path="qpId" value="${questionsBean.qpId }"/>
						<c:forEach var="bean" items="${questionsBean.assignmentFilesSet}" varStatus="i">
	  				    
							<div class="form-group">
								<div class="row">
									<div class="col-md-3 "> 
										<input name="qnNos[${i.count-1 }]" placeholder="Enter Qn No:"   value="${bean.qnNo }" class="form-control qnNo" required/> 
									</div>	 
								</div>	
								<br> 
								<textarea id="question-textArea${i.count}" name="questions[${i.count-1}]" class="form-control editor" required="required">${bean.question }</textarea>
							    <br>
							    <div class="row">
							    <div class="col-md-6 ">
							    <input name="marks[${i.count-1 }]" placeholder="Enter Marks"   value="${bean.mark }" class="form-control marks" required/> 
								</div>
								<div class="col-md-12 "> 
									<div class="pull-right">  
										<a > <i class="fa-solid fa-circle-minus delete_qn" style="font-size: 27px; color: #dc3545"></i></a>
									</div>  
								</div>
								
								</div>
								
							</div>
							
					</c:forEach>
				</div>  
				<div class="pull-right">  
				<a > <i class="fa-solid fa-circle-plus add-btn" style="font-size: 27px; color: #28a745"></i></a>
				</div>  
				<button type="submit" class="btn btn-warning"
				style="text-transform: none; margin: 0px 0px 0px 5px; padding: 8px 16px; font-size: 0.9rem; font-weight: unset; cursor: pointer;">Save</button>
		
		 </form:form> 
     </div>
     </section>  
     <div style="min-height:25rem;">
	
	</div>
			<jsp:include page="../footer.jsp" />
	
	<script src="https://unpkg.com/sweetalert/dist/sweetalert.min.js"></script>

	<script type="text/javascript"
		src="//cdn.ckeditor.com/4.16.1/standard-all/ckeditor.js"></script>
	<script type="text/javascript"
		src="//cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.0/MathJax.js?config=TeX-AMS_HTML"></script>


	<script src="assets/js/ckeditor/ckeditor_responsive_table/plugin.js"></script>
	 
<script src="resources_2015/js/vendor/froala_editor.min.js"></script>
	<script type="text/javascript">
	$(document).ready(function(){
		var size= "${questionsBean.assignmentFilesSet.size()}";
		if(size==0){  
			$(".add-btn").trigger( "click" );
			}  
	});
	    
	initializeEditor();  
	var questionsTextEditorObj=null;
	$(document).on('click', '.add-btn', function(){
		createNewEditor(); 
	});
	$(document).on('click', '.delete_qn', function(){
		$(this).closest(".form-group").remove();  
		initializeEditor(); 
	});
	var i=0;
	function createNewEditor() {
		   
		  var element = document.createElement("textarea"); 
		  var div = document.createElement("div");
		  $(div).addClass("form-group");
		  $(element).attr('id', 'editorj['+i+']'); 
		  
		  i=i+1; 
			 var rowDiv1 = document.createElement("div");
			  $(rowDiv1).addClass("row");  
			  var colDiv1 = document.createElement("div");
			  $(colDiv1).addClass("col-md-3");
			  var inputElement1 = document.createElement("input");
			  $(inputElement1).attr('class', 'qnNo');
			  $(inputElement1).attr('required', 'required'); 
			  $(inputElement1).attr('placeholder', 'Enter Qn No:'); 
			  var br1 = document.createElement("br");  
			  $(br1).appendTo($(colDiv1)); 
			  $(inputElement1).appendTo($(colDiv1)); 
			  $(colDiv1).appendTo($(rowDiv1)); 
			  $(rowDiv1).appendTo(div);
			  $(br1).appendTo($(div));    
			  $(div).appendTo(".qn_div"); 
			  $(element)
			    .addClass("editor")  
			    .appendTo($(div));   
		  var rowDiv = document.createElement("div");
		  $(rowDiv).addClass("row");
		  var colDiv = document.createElement("div");
		  $(colDiv).addClass("col-md-6");
		  var inputElement2 = document.createElement("input"); 
		  $(inputElement2).attr('class', 'marks');
		  $(inputElement2).attr('required', 'required'); 
		  $(inputElement2).attr('placeholder', 'Enter Marks'); 
		  var br = document.createElement("br");
		  $(br).appendTo($(colDiv));
		  $(inputElement2).appendTo($(colDiv)); 
		  $(colDiv).appendTo($(rowDiv)); 
		  $(rowDiv).append('<div class="col-md-12 "><div class="pull-right">'+  
					'<a > <i class="fa-solid fa-circle-minus delete_qn" style="font-size: 27px; color: #dc3545"></i></a></div></div>');
		  $(rowDiv).appendTo(div);
		  $(div).appendTo(".qn_div"); 
		   
			initializeEditor(); 
		} 
	function initializeEditor(){ 
		var j=0; 
		$(".editor").each(function () {   
		$(this).attr('name', 'questions['+j+']');
		$(this).parent().find(".marks").attr('name', 'marks['+j+']');
		$(this).parent().find(".qnNo").attr('name', 'qnNos['+j+']'); 
		j++; 	
	questionsTextEditorObj =  CKEDITOR.replace(
			(this.id),  
			{
				extraPlugins : 'editorplaceholder,uploadimage,image2,mathjax,autogrow,colorbutton,font,justify,print,tableresize,uploadfile,pastefromword,liststyle,pagebreak',
				//      extraPlugins: 'colorbutton,font,justify,print,tableresize,uploadimage,uploadfile,pastefromword,liststyle,pagebreak',
				editorplaceholder: 'Type question here...', 
				mathJaxLib : 'https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.0/MathJax.js?config=TeX-AMS_HTML',
				uploadUrl : '/exam/ckeditorFileUpload',

			      // Configure your file manager integration. This example uses CKFinder 3 for PHP.
			      filebrowserBrowseUrl: '/exam/ckeditorFileUpload',
			      filebrowserImageBrowseUrl: '/exam/ckeditorFileUpload',
			      filebrowserUploadUrl: '/exam/ckeditorFileUpload',
			      filebrowserImageUploadUrl: '/exam/ckeditorFileUpload',
				

			      // Load the default contents.css file plus customizations for this sample.
			      contentsCss: [
			    	'assets/css/ckeditor/contents.css',
			        //'https://ckeditor.com/docs/vendors/4.14.0/ckeditor/assets/css/pastefromgdocs.css',
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
		 });
	}
</script>

</body>
</html>
 