<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Add Announcement" name="title" />
</jsp:include>
<!-- Froala wysiwyg editor CSS -->
<link href="<c:url value="/resources_2015/css/froala/froala_editor.min.css" />" rel="stylesheet">
<link href="<c:url value="/resources_2015/css/froala/froala_style.min.css" />" rel="stylesheet">
<link href="<c:url value="/resources_2015/css/froala/froala_content.min.css" />" rel="stylesheet">

<link href="<c:url value="/resources_2015/css/froala/themes/dark.min.css" />" rel="stylesheet">	
<link href="<c:url value="/resources_2015/css/froala/themes/grey.min.css" />" rel="stylesheet">	
<link href="<c:url value="/resources_2015/css/froala/themes/red.min.css" />" rel="stylesheet">	
<link href="<c:url value="/resources_2015/css/froala/themes/royal.min.css" />" rel="stylesheet">		
<link href="<c:url value="/resources_2015/css/froala/themes/blue.min.css" />" rel="stylesheet">		

<style>
/* pill box start */
.pill-box {
  line-height: 15px;
}
.some-light-bg {
  float: left;
  padding: 5px;
  margin: 25px 0 0 25px;
  	border-radius: 10px;
  max-width:1500px;
    border: 1px solid #cfcfcf; 
      box-shadow: 0 0 5px  rgba(0, 0, 0, 0.075) ;
	background-color:#ffffff;
}

.btn{
	text-decoration:none;
	color:#333333;
	font-size:14.4px;
	font-weight:bold;
	padding:0 15px;
	line-height:40px;
	height: auto;
	display:inline-block;
	text-align:center;
	background-color:#DDDDDD;
  margin-bottom:8px;
}

.btn.pill{
  -webkit-border-radius: 16px;
  -moz-border-radius: 16px;
  border-radius: 16px;
  
}


.dropdown {
  display: inline-block;
  position: relative;
}

.dd-button {
display: block;
    width: 100%;
    height: 34px;
    padding: 6px 12px;
    font-size: 14px;
    line-height: 1.42857143;
    color: #555555;
    background-color: #ffffff;
    background-image: none;
    border: 1px solid #cccccc;
    font-weight:200;

  font-family:'aller_rg-webfont';
  
}

.dd-button:after {
    content: '';
    position: absolute;
    top: 50%;
    right: 5px;
    transform: translateY(-50%);
    width: 0;
    height: 0;
    border-left: 4px solid transparent;
    border-right: 6px solid transparent;
    border-top: 5px solid black;
    color:#555555;
}

.dd-button:hover {
  background-color: #eeeeee;
}


.dd-input {
  display: none;
}

.dd-menu {
  position: relative;
  top: 100%;
  border: 1px solid #ccc;
  border-radius: 4px;
  padding: 0;
  margin: 2px 0 0 0;
  box-shadow: 0 0 6px 0 rgba(0,0,0,0.1);
  background-color: #ffffff;
  list-style-type: none;
  width:100%;
}


.dd-input:checked + .dd-menu {
  display: block;
} 

.dd-menu li {
	height:270px; 
  padding: 10px 20px;
  cursor: pointer;
  white-space: nowrap;
}

.dd-menu li:hover {
  background-color: #f6f6f6;
}

.dd-menu li a {
  display: block;
  margin: -10px -20px;
  padding: 10px 20px;
}

.dd-menu li.divider{
  padding: 0;
  border-bottom: 1px solid #cccccc;
}

a {
    text-decoration: none !important;
    color: #26a9e0;
}


.modal-dialog{
z-index:1040;
}

/**********************************************
		MODALS --START--
***********************************************/
.modal-content {
  border: 0;
  border-radius: 0; }

.modal-header h4.modal-title {
  color: #d2232a;
  font-family: "Open Sans";
  font-weight: bold;
  font-size: 1.3rem;
  text-transform: uppercase;
  margin: 0; }
  .modal-header h4.modal-title span {
    float: right;
    display: block; }

.modal-header {
  border-color: #d2232a;
  padding: 1.2rem;
  border-width: 2px; }

.modal-header .close {
  border-radius: 50%;
  background: #404041;
  opacity: 1;
  height: 22px;
  width: 22px;
  color: #fff;
  
  line-height: 1em;
  margin: 0; }

.modal-body {
  max-height: calc(100vh - 250px);
  overflow: scroll; }
  .modal-body button {
    width: 100%;
    margin: 0 !important;
    background-color: #d2232a;
    border: 0;
    font-weight: 600;
    font-family: "Open Sans";
    text-transform: uppercase;
    color: #fff;
    border-radius: 0;
    padding: 1em 0; }

.modal-footer {
  padding: 0;
  border-top: 1px solid #d2232a; }
  .modal-footer button {
    width: 100%;
    margin: 0 !important;
    background-color: #d2232a;
    border: 0;
    font-weight: 900;
    font-family: "Open Sans";
    text-transform: uppercase;
    color: #fff;
    border-radius: 0;
    padding: 1em 0; }
  .modal-footer .nav-tabs {
    padding: 0;
    border: 0;
    border-top: 1px solid #d2232a; }
    .modal-footer .nav-tabs li {
      width: 50%;
      margin: 0 !important;
      border-radius: 0;
      text-align: center; }
      .modal-footer .nav-tabs li a {
        border-radius: 0;
        border: 0 !important;
        color: #d2232a;
        margin: 0 !important;
        padding: 1em 0;
        font-family: "Open Sans";
        font-weight: bold; }
      .modal-footer .nav-tabs li.active a {
        color: #fff;
        background-color: #d2232a; }
  .modal-footer .nav-tabs > li.active > a:hover {
    background-color: #d2232a; }

body.modal-open {
  overflow: auto;
  padding: 0 !important; }
  body.modal-open .courses-toggle:after {
    color: #d2232a; }

.btn-default, .btn-default:hover, .btn-default:active, .btn-default:focus {
  	color:white;
    background: #d2232a;
    
}

.tr:hover {
  background-color: #f5f5f5;
}
/**********************************************
		Modals --END--
***********************************************/
.deletebtn{
    text-decoration: none !important;
    color: #26a9e0;
    cursor:pointer;
}
.deletebtn:hover{
color:#c72127;
}


@media only screen  and (max-width: 800px)  {
  .programWidget {
        padding: 5px 20px;
    font-size: 12px;
    line-height: 1.5;
    border-radius: 3px;
  }
   .multiWidget{
  width:100%;  
  }
  
  .programOptions{
  width:100%;
  padding:10px;
  }
  
  .dd-menu{
  height:720px;
  }
  
  
}

@media only screen  and (min-width: 800px) and (max-width: 1000px)  {
  .programWidget {
        padding: 5px 5px;
    font-size: 12px;
    line-height: 1.5;
    border-radius: 3px;
   
    
  }
   .multiWidget{
  width:100%;  
    }
  
  
  
}


@media only screen and (min-width: 1000px) and (max-width: 1200px)  {
  .programWidget {
  
      padding: 12px 11px;
    font-size: 12px;
    line-height: 1.5;
    border-radius: 3px;
  
  }
   .multiWidget{
  width:78%;  
  }
  
  
}

@media only screen and (min-width: 1200px) and (max-width: 1850px)  {
  .programWidget {
  
     
    line-height: 40px;
    padding:0px 30px;
  
  }
  .multiWidget{
  width:78%;  
  }

}


</style>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
         <div class="row"><legend>
       <%if("true".equals((String)request.getAttribute("edit"))){ %>  Edit Announcement <%}else{ %> Add Announcement <%} %> 
         </legend></div>
        <form:form  action="addAnnouncement" method="post" modelAttribute="announcement" enctype="multipart/form-data">
			<fieldset>
			<form:hidden path="attachment1"/>
			<form:hidden path="attachment2"/>
			<form:hidden path="attachment3"/>
       
        <%@ include file="messages.jsp"%>
		<div class="panel-body">
		
			
			<div class="col-md-18 column">
			
		

				<%if("true".equals((String)request.getAttribute("edit"))){ %>
				<form:input type="hidden" path="id" value="${announcement.id}"/>
				<%} %>
				
					<div class="form-group">
						<form:input id="subject" path="subject" type="text"
							placeholder="Subject" class="form-control" required="required" value="${announcement.subject}"/>
					</div>
				
					<div class="form-group">
						<form:textarea path="description" id="editor" required="required" title="${announcement.description}"/>
					</div>
				<div class="row">
				
					<div class="form-group col-md-7 column">
					<label>Start Date :-</label>
								<form:input id="startDate" path="startDate" type="date" required="required" placeholder="Start Date" class="form-control" value="${announcement.startDate}"/>
					</div>
	
	
					<div class="form-group col-md-7 column">
					<label>End Date :-</label>
								<form:input id="endDate" path="endDate" type="date" required="required" placeholder="End Date" class="form-control" value="${announcement.endDate}"/>
					</div>
	</div>
	<div class="row">
				<div class="form-group col-md-7 column">
						<form:select id="active" path="active"
							type="text" placeholder="Active" class="form-control"
							required="required" itemValue="${announcement.active}">
							<form:option value="">Make Active</form:option>
							<form:option value="Y">Active</form:option>
							<form:option value="N">In-Active</form:option>
						</form:select>
					</div>
					
					<div class="form-group col-md-7 column">
						<form:select id="category" path="category"
							type="text" placeholder="Category" class="form-control"
							required="required" itemValue="${announcement.category}">
							<form:option value="">Select Category</form:option>
							<form:option value="Academics">Academics</form:option>
							<form:option value="Admission">Admission</form:option>
							<form:option value="Exams">Exams</form:option>
							<form:option value="General">General</form:option>
						</form:select>
					</div>
					</div>
				<!-- 	Added for allowing separate announcements creation for Executive Programs start-->
					<!--div class="form-group">
						<form:select id="program" path="program"
							type="text" placeholder="Program" class="form-control"
							required="required" itemValue="${announcement.program}">
							<form:option value="All">Select Program</form:option>
							<form:option value="All">All</form:option>
							<form:options items = "${program}" />
							
							
						</form:select>
					</div --> 
				
					<%
							if ("true".equals((String) request.getAttribute("edit"))) {
						%>
					
						<!-- div class="row">
						
							<div class="form-group">
							
						<div class="some-light-bg">
											<label>Programs :</label>
						
  <div class="pill-box">

      <c:forEach items="${announcementprograms }" var="bean"> 
     <a class="btn pill" href="#">${bean.consumer_type} - ${bean.program} - ${bean.program_structure} </a>
 


						</c:forEach>
    
    
</div>
  </div>
</div></div!-->
<div class="form-group col-md-7 column" >
					<label>Programs :-</label>
											  <a href="javascript:void(0)" data-id="${ announcement.id }" class="commonLinkbtn">Common Announcement For ${ announcement.count } programs</a>
							
					</div>
	






						
					<!-- div class="col-md-7 column form-group">
						<form:select id="program" path="program"
							type="text" placeholder="Program" class="form-control"
							required="required" itemValue="${announcement.program}">
							<form:option value="All">Select Program</form:option>
							<form:option value="All">All</form:option>
							<form:options items = "${program}" />
							
							
						</form:select>
					</div!--><%
							} 
							else if ("true".equals((String) request.getAttribute("editAnnouncementProgram"))) { %>
							<div class="col-md-7 column form-group" style="padding-left:0px">
						<form:select  id="program" path="program" readonly="readonly"
							type="text" placeholder="Program" class="form-control"
							required="required" itemValue="${announcement.program}">
							<form:option value="All">Select Program</form:option>
							<form:option value="All">All</form:option>
							<form:options items = "${program}" />
							
							
						</form:select>
					</div>	
							
								
						<%	}						
							else {
						%>
					
		<div class="col-md-7 column multiWidget" style="padding-left:0px" > 
<label class="dropdown" style="margin-bottom:15px">

  <div class="dd-button">
    Select Program
  </div>
  <input type="checkbox" class="dd-input" id="test">

  <ul class="dd-menu">
    <li>
        <div class="col-xs-7 col-md-7 col-lg-7 column programOptions" >
        
<input type="text" id="programInput" class="form-control" onkeyup="programFunction()" placeholder="Search for program.." >

<form:select id="program" path="selectAllOptions" multiple="multiple" style="height:200px;overflow:scroll" class="form-control "  itemValue="${announcement.program}" >


 <c:forEach items="${masterannouncement }" var="bean"> 
 <!-- data-toggle="tooltip" title="${bean.consumer_type} - ${bean.program} - ${bean.program_structure}"  -->
<form:option value="${bean.id }">${bean.consumer_type} - ${bean.program_structure} - ${bean.program} </form:option>  

						</c:forEach>


</form:select>


        </div>
        <center>
        <div class=" col-xs-17  col-sm-17 col-sm-3 col-lg-4">
            <div class="input-group">
                <button type="button" value="" class="btn  programWidget btn-xs btn-danger " id="add">Add >></button>
            </div>
<div class="input-group" style="padding-top:10px;">
<button type="button" value="" class="btn  programWidget btn-xs btn-primary" id="remove"><< Remove</button> 
            </div>

            <div class="input-group" style="padding-top:10px;">
                <button type="button" value="" class="btn programWidget btn-xs btn-danger" id="select_all">Select All</button>
            </div>

<div class="input-group" style="padding-top:10px;">
                <button type="button" value="" class="btn programWidget btn-xs btn-primary" id="deselect_all">DeSelect All</button>
            </div>
        </div>
        </center>
        <div class="col-xs-7 col-md-7 col-lg-7 column programOptions ">
        <input type="text" id="selectedProgramInput" class="form-control" onkeyup="selectedProgramFunction()" placeholder="Search for program.." >
        
            <form:select   selected="selected"  class="form-control"  required="required" path="program" size="9" id="selected_program" multiple="multiple" style="height:200px;overflow:scroll"   >
          
</form:select>
        </div>
    </li>
  </ul>
  
</label></div>
<%
							} 
						%>
					<!-- ^multiselect code ---->
					
					
					<div>
					<% if ("true".equals((String) request.getAttribute("editAnnouncementProgram"))) { %>
			
				 <div class="form-group col-md-7 column">
						<form:select id="programStructure" path="programStructure"
							type="text" placeholder="ProgramStructure" class="form-control" readonly="readonly"
							required="required" itemValue="${announcement.programStructure}">
							<form:option value="All">Select Program Structure</form:option>
							<form:option value="All">All</form:option>
							<form:options items="${programStructure }" />
							
							
						</form:select>
					</div!>  
					
 							<form:input type="hidden"  path="masterId" value="${masterId}"/>
 							<form:input type="hidden"  path="announcementId" value="${announcementId}"/> 
 							<form:input type="hidden"  path="masterKey" value="${masterKey}"/>
 							
												

					 <%} else{%> 
						<!-- div class="form-group col-md-7 column">
						<form:select id="programStructure" path="programStructure"
							type="text" placeholder="ProgramStructure" class="form-control"
							required="required" itemValue="${announcement.programStructure}">
							<form:option value="All">Select Program Structure</form:option>
							<form:option value="All">All</form:option>
							<form:options items="${programStructure }" />
							
							
						</form:select>
					</div!-->
						<% } %>
					
					
					</div>  
					<!-- 	Added for allowing separate announcements creation for Executive Programs end-->
					<div class="row">
					
					<div class=" col-md-7 column form-group">
						<label>Attachment 1:</label>
						<input type="file" name="file1"  class="form-control">
				
					</div>
					<div class="col-md-7 column form-group">
						<label>Attachment 2:</label>
						<input type="file" name="file2"  class="form-control">
					</div>
					
					<div class=" col-md-7 column form-group">
						<label>Attachment 3:</label>
						<input type="file" name="file3"  class="form-control">
					</div></div>
					
					
					
					<!-- Button (Double) -->
				<div class="form-group">
					<label class="control-label" for="submit"></label>
					<div class="controls">
						<%
							if ("true".equals((String) request.getAttribute("edit"))) {
						%>
						<button id="submit" name="submit"
							class="btn btn-large btn-primary" formaction="updateAnnouncement">Update</button>
						<%
							}else if("true".equals((String) request.getAttribute("editAnnouncementProgram"))){
					%>
								<button id="submit" name="submit"
										class="btn btn-large btn-primary" formaction="addAnotherAnnouncement">Submit</button>
						<%					
								
							} 
							 
							
							
							
							else {
						%>
						<button id="submit" name="submit"
							class="btn btn-large btn-primary" onclick="if(confirm('Are you sure you want to Submit?')) { $('#selected_program option').prop('selected', true);}else{   return false; } "; formaction="addAnnouncement">Submit</button>
						<%
							}
						%>
						<button id="cancel" name="cancel" class="btn btn-danger"
							formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
				</div>
				
					
				</div>
				
				
							
				
		</div>
		</fieldset>
		</form:form>
	</div>
	
</section>
  <div id="myModal" class="modal fade" role="dialog">
  <div class="modal-dialog">

    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title">Common Program List</h4>
      </div>
      <div class="modal-body modalBody">
      	
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
    </div>

  </div>
</div>
	
<jsp:include page="footer.jsp" />
<script src="resources_2015/js/vendor/froala_editor.min.js"></script>
<script src="resources_2015/js/vendor/froala-plugins/tables.min.js"></script>
<script src="resources_2015/js/vendor/froala-plugins/lists.min.js"></script>
<script src="resources_2015/js/vendor/froala-plugins/colors.min.js"></script>
<script src="resources_2015/js/vendor/froala-plugins/font_family.min.js"></script>
<script src="resources_2015/js/vendor/froala-plugins/font_size.min.js"></script>
<script src="resources_2015/js/vendor/froala-plugins/block_styles.min.js"></script>
<script src="resources_2015/js/vendor/froala-plugins/media_manager.min.js"></script>
<script src="resources_2015/js/vendor/froala-plugins/inline_styles.min.js"></script>
<script src="resources_2015/js/vendor/froala-plugins/fullscreen.min.js"></script>
<script src="resources_2015/js/vendor/froala-plugins/char_counter.min.js"></script>
<script src="resources_2015/js/vendor/froala-plugins/entities.min.js"></script>
<script src="resources_2015/js/vendor/froala-plugins/file_upload.min.js"></script>
<script src="resources_2015/js/vendor/froala-plugins/urls.min.js"></script>

<script type="text/javascript">
$(document).ready(function(){
   $('#add').click(function() {  
    return !$('#program option:selected:visible')
.remove().appendTo('#selected_program');  
   });  
   $('#remove').click(function() {  
    return !$('#selected_program option:selected:visible')
.remove().appendTo('#program');  
   });  
    
 $('#select_all').click(function() {
    return $('#program option').prop('selected', true);
}); 

 $('#deselect_all').click(function() {
    return $('#program option').prop('selected', false);
});
function selectall()  {  
$('#selected_program').find('option').each(function() {  
   $(this).attr('selected', 'selected');  
  }); 

}  


});
</script>

<script>
$(document).ready(function(){
  $('[data-toggle="tooltip"]').tooltip();   
});
</script>

<script type="text/javascript">

$('#editor').editable({inlineMode: false,
    buttons: ['bold', 'italic', 'underline', 'sep', 
              'strikeThrough', 'subscript', 'superscript', 'sep',
              'fontFamily', 'fontSize', 'color', 'formatBlock', 'blockStyle', 'inlineStyle','sep',
              'align', 'insertOrderedList', 'insertUnorderedList', 'outdent', 'indent', 'selectAll','sep',
              'createLink', 'table','sep',
              'undo', 'redo', 'sep',
              'insertHorizontalRule', 'removeFormat', 'fullscreen'],
    minHeight: 200,
    paragraphy: false,
    placeholder: 'Enter Email Body here',
    theme: 'blue',
    key:'vA-16ddvvzalxvB-13C2uF-10A-8mG-7eC5lnmhuD3mmD-16==',
    toolbarFixed: false
});

	$("#endDate").change(function () {
	    var startDate = document.getElementById("startDate").value;
	    var endDate = document.getElementById("endDate").value;
	    var date1 = new Date(startDate);
	    var date2 = new Date(endDate);
	    var timeDiff = Math.abs(date2.getTime() - date1.getTime());
	    
	    var diffDays = Math.ceil(timeDiff / (1000 * 3600 * 24)); 
	    if(diffDays > 30){
	    	alert("End date should not be greater than 30 Days");
	    }
	 	
	});
</script>

<script>
function programFunction() {
    var input, filter, ul, li, a, i, txtValue;
    input = document.getElementById("programInput");
    filter = input.value.toUpperCase();
    select = document.getElementById("program");
   
    
    for (i = 0; i < select.length; i++) {
    	a = select.options[i];
  
        txtValue = a.text;
        if (txtValue.toUpperCase().indexOf(filter) > -1) {
            select.options[i].style.display = "";
        } else {
            select.options[i].style.display = "none";
        }
    }
}

function selectedProgramFunction() {
    var input, filter, ul, li, a, i, txtValue;
    input = document.getElementById("selectedProgramInput");
    filter = input.value.toUpperCase();
    select = document.getElementById("selected_program");
   
    
    for (i = 0; i < select.length; i++) {
    	a = select.options[i];
  
        txtValue = a.text;
        if (txtValue.toUpperCase().indexOf(filter) > -1) {
            select.options[i].style.display = "";
        } else {
            select.options[i].style.display = "none";
        }
    }
}
</script>

 <script type="text/javascript">

$(document).ready(function() {
	
	$('.commonLinkbtn').on('click',function(){

		let id = $(this).attr('data-id');
		
		
		let modalBody = "<center><h4>Loading...</h4></center>";
		let data = {
			'id':id
		};
		$.ajax({
			   type : "POST",
			   contentType : "application/json",
			   url : "getCommonAnnouncementProgramsList",   
			   data : JSON.stringify(data),
			   success : function(data) {
				   
			
				   modalBody = '<div class="table-responsive"> <table class="table"> <th scope="col">Consumer Type</th> <th scope="col">Program</th> <th scope="col">Program Structure</th><th scope="col">Actions</th> </thead><tbody>';
				   for(let i=0;i < data.length;i++){
					   modalBody = modalBody + '<tr class="table-row"><td>'+ data[i].consumer_type +'</td><td>'+ data[i].program +'</td><td>'+ data[i].program_structure +'</td><td style="width:100px"> <a class="glyphicon glyphicon-info-sign" href=viewAnnouncementDetails?id='+id+' title="Details"></a> &ensp; <a href=editAnnouncementProgram?masterKey='+data[i].id+'&&announcementId='+id+' title="Edit"><i class="fa fa-pencil-square-o fa-lg"></i></a> &ensp;<span class="deletebtn" onclick="deleteProgram('+data[i].id+','+id+' )" title="Delete">  <i class="fa fa-trash fa-lg"></i></span></td></tr>';
				   }
				  				   
				   modalBody = modalBody + '<tbody></table></div>';
				   $('.modalBody').html(modalBody);
			   },
			   error : function(e) {
				   alert("Please Refresh The Page.")
			   }
		});
		$('.modalBody').html(modalBody);
		//modal-body
		$('#myModal').modal('show');
	});
	
	
 
});

</script>
<script>
function deleteProgram(masterKey , announcementId){

	if(confirm('Are you sure you want to Submit?')){
		
	  window.location="/studentportal/deleteAnnouncementProgram?masterKey="+masterKey+"&&announcementId="+announcementId;
	}
	   
	
}
</script>




</body>
</html>
