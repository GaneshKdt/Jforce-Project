<!DOCTYPE html>

<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>  
<%@page pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Edit Session Plan" name="title" />
</jsp:include>


<!-- Include Editor style. -->
<link href="https://cdnjs.cloudflare.com/ajax/libs/froala-editor/2.5.1/css/froala_editor.pkgd.min.css" rel="stylesheet" type="text/css" />
<link href="https://cdnjs.cloudflare.com/ajax/libs/froala-editor/2.5.1/css/froala_style.min.css" rel="stylesheet" type="text/css" />
<link data-require="sweet-alert@*" data-semver="0.4.2" rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/sweetalert/1.1.3/sweetalert.min.css" />
   
   <!-- for medium editor start -->
 <script src="//cdn.jsdelivr.net/npm/medium-editor@latest/dist/js/medium-editor.min.js"></script>
<link rel="stylesheet" href="//cdn.jsdelivr.net/npm/medium-editor@latest/dist/css/medium-editor.min.css" type="text/css" media="screen" charset="utf-8">

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.47/css/bootstrap-datetimepicker-standalone.css" type="text/css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.47/css/bootstrap-datetimepicker-standalone.min.css" type="text/css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.47/css/bootstrap-datetimepicker-standalone.min.css.map" type="text/css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.47/css/bootstrap-datetimepicker.css" type="text/css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.47/css/bootstrap-datetimepicker.min.css" type="text/css">
 <link href="themes/1/tooltip.css" rel="stylesheet" type="text/css" />



 <!-- for medium editor end -->
   	
<!-- css for accordian start -->
<style>
.panel-title > a:before {
    float: right !important;
    font-family: FontAwesome;
    content:"\f068";
    padding-right: 5px;
}
.panel-title > a.collapsed:before {
    float: right !important;
    content:"\f067";
}
.panel-title > a:hover, 
.panel-title > a:active, 
.panel-title > a:focus  {
    text-decoration:none;
}
 
 .editable{
 	border : 1px solid grey;
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
.input-group{
    width: 260px;

}
.input-group-check {
    padding: 8px;
    font-size: 12px;
    font-weight: normal;
        cursor: pointer;
        display: table-cell;
    
}

.disabLehover:hover{
  color: #555555;

}

.input-group{
    width: 250px;

}

 .mcTooltip{
transition: none 0s ease 0s; width: 500px; height: 70px;
}
.mcTooltipInner{
width: 250.3px; height: 170.3px; top: auto; left: auto; position: absolute; opacity: 1; transition: all 200ms ease 0s;
} 

.newTooltip{
position: relative;
}
.tooltipInner{

}
		.newTooltip .tooltipData {
			margin-left: -999em;
			position: absolute;
			
		}
		.newTooltip:hover span {
			border-radius: 5px 5px; -moz-border-radius: 5px; -webkit-border-radius: 5px; 
			box-shadow: 5px 5px 5px rgba(0, 0, 0, 0.1); -webkit-box-shadow: 5px 5px rgba(0, 0, 0, 0.1); -moz-box-shadow: 5px 5px rgba(0, 0, 0, 0.1);
			font-family: Calibri, Tahoma, Geneva, sans-serif;
			font-size: 1.0em;
			position: absolute; left:-10em; top: 2.4em; z-index: 99;
			margin-left: 0; width: 300px;
			white-space: pre-wrap;
			  
			
			
		}
		.newTooltip:hover img {
			border: 0; margin: -10px 0 0 -55px;
			float: left; position: absolute;
		}
		.newTooltip:hover em {
			font-family: Candara, Tahoma, Geneva, sans-serif; font-size: 1.2em; font-weight: bold;
			display: block; padding: 0.2em 0 0.6em 0;
		}
		.tooltipData {
		 padding:2px;
    line-height:16px;
    border-width: 1px;   
    color:#333; 
    border-color:#BBB;
    padding:10px;    
    font-size: 12px;
    font-family: Verdana, Arial;
    border-radius:6px;
    background-color:#F6F6F6;
     overflow-wrap: break-word;
    }
		.tooltipInnerData {
		 padding:2px;
		 
   
    }
		
		
	.multiselect{
		border-radius: 2px; 
		display: inline-block;
		background-color: white;
	    border: 1px solid #bababa;
	}
	
	.option{
		float: none;
    	width: 20px;
    	display: inline;
   		vertical-align: middle;
	}
	
	.chapterinput{
	    outline: none;
	    border: none;
	    width: 100%;
	    margin-right: 5px;
	}
</style>
<!-- css for accordian end  -->

<body class="inside">

	<%@ include file="header.jsp"%>
	
	
	<!-- code for accordian start -->
	<div class="well well-lg">
	
	<ul class="breadcrumb">
		<li><a href="/">Home</a></li>
		<li><a href="/acads/mbax/sp/a/admin/manageSessionPlan">Manage Session Plans</a></li>
		<li class="active" >Edit Session Plan</li>
	</ul>
	<%@ include file="messages.jsp"%>
	<legend>Session Plan For Subject : ${formBean.subject} </legend>	
	<div class="panel-group">
  <div class="panel panel-default">
    <div class="panel-heading">
      <h4 class="panel-title">
        <a class="btn btn-info btn-block collapsed" data-toggle="collapse" href="#collapse1">Edit Session Plan</a>
      </h4>
    </div>
    <div id="collapse1" class="panel-collapse collapse border border-primary">
      <div class="panel-body">
      	
      	<!-- Code for Form start -->
	<div class="well well-lg">				
				<form:form modelAttribute="formBean" method="post">
					<form:hidden path="id" value="${formBean.id }"/> 
										
					<div class="row">
					<div class="container-fluid">
					
					<div class="row">
					
					<!-- ///////////////////////////////////////////////////////////////// -->
					
							<div class="col-md-4">
							<div class="form-group">
							<label for="consumerType">Consumer Type</label>
							<form:select data-id="consumerTypeDataId" id="consumerTypeId"
								path="consumerTypeId"   class="selectConsumerType"
								required="required">
							<c:forEach var="consumerType" items="${consumerType}">
				               <c:choose>
				               	<c:when test="${consumerType.id == formBean.consumerTypeId}">
					               	<option selected value="<c:out value="${consumerType.id}"/>">
					                  <c:out value="${consumerType.name}"/>
					                </option>
				               	</c:when>
				               	<c:otherwise>
					               	<option value="<c:out value="${consumerType.id}"/>">
					                  <c:out value="${consumerType.name}"/>
					                </option>
				               	</c:otherwise>
				               </c:choose>
				               
				            </c:forEach>
							</form:select>
							</div>
						</div>
						
						<div class="col-md-4">
							<div class="form-group">
							<label for="programStructure">Program Structure</label>
							<form:select  data-id="programStructureDataId" id="programStructureId" path="programStructureId" class="selectProgramStructure" required="required">
								<c:choose>
				               	<c:when test="${fn:length(fn:split(formBean.programStructureId, ',')) > 1}">
					               	<option selected value="${formBean.programStructureId}">All</option>
				               	</c:when>
				               	<c:otherwise>
					               	<option selected
					               	 value="${formBean.programStructureId}">
					               	 	${programStructureIdNameMap[formBean.programStructureId]}
					               	</option>
								</c:otherwise>
				               </c:choose> 
				               
							</form:select>
							</div>
						</div> 
							
						<div class="col-md-4">
							<div class="form-group">
							<label for="Program">Program</label>
							<form:select data-id="programDataId" id="programId" path="programId" class="selectProgram">
								
								<c:choose>
				               	<c:when test="${fn:length(fn:split(formBean.programId, ',')) > 1}">
					               	<option selected value="${formBean.programId}">All</option>
				               	</c:when>
				               	<c:otherwise>
					               	<option  selected
					               	 value="${formBean.programId}">
					               	 	${programIdNameMap[formBean.programId]}
					               	</option>
								</c:otherwise>
				               </c:choose>

							</form:select>
							</div>
						</div>
						
						
					<div class="col-md-4">
							<div class="form-group">
							<label >Subject</label>
							<form:select  data-id="subjectId" id="subject" path="subject" class="selectSubject">
							
								<c:choose>
				               	<c:when test="${ not empty formBean.subject}">
					               	<option selected value="${formBean.subject}">${formBean.subject}</option>
				               	</c:when>
				               	<c:otherwise>
					               	<option selected value="">Select Subject</option>
								</c:otherwise>
				               </c:choose>
							</form:select>
							</div>
					</div>
						
					</div>
					<!-- ///////////////////////////////////////////////////////////////// -->
					
					<div class="row">
						<div class="col-md-4">
						<div class="form-group">
							<label >Acads Year</label>
							<form:select id="year" path="year" type="text"	placeholder="Acads Year" 
							class="form-control" required="required"  itemValue="${formBean.year}">
								<form:option value="">Select Acads Year</form:option>
								
				                 <form:options items="${acadsYearList}"/>
				                
				            
								
							</form:select>
						</div>
						</div>
						
						<div class="col-md-4">
						<div class="form-group">
							<label >Acads Month</label>
							<form:select id="month" path="month" type="text" placeholder="Acads Month" 
							class="form-control" required="required" itemValue="${formBean.month}">
								<form:option value="">Select Acads Month</form:option>
								 <form:options items="${acadsMonthList}"/>
							</form:select>
						</div>
						</div>
					
					</div>
					
					
					<%-- <div class="row">
					<div class="">
						<div class="col-md-6">
							<div class="form-group">
								<label >Session Plan Title</label>
								<form:input path="title" value="${formBean.title}" required="required"  placeholder="Session Plan Title"
									style="color:black; padding:6px 12px; width:100%;"/>
							</div>
						</div>
						
					</div>
					</div> --%>
					
					<div class="row">
					<div class="well">
						<h6 align="left">Teaching Scheme</h6>
						
						<div class="col-md-4">
							<div class="form-group">
								<label >No Of Sessions </label>
								<form:input type="number" path="noOfClassroomSessions" value="${formBean.noOfClassroomSessions}"
									required="required"  placeholder="0" 
									style="color:black; padding:6px 12px; width:100%;"/>
							</div>
						</div>
						
						<div class="col-md-4">
							<div class="form-group">
								<label >No Of Group work (If Any) </label>
								<form:input type="number" path="noOf_Practical_Group_Work" value="${formBean.noOf_Practical_Group_Work}"
									required="required"  placeholder="0" 
									style="color:black; padding:6px 12px; width:100%;"/>
							</div>
						</div>
						
						<div class="col-md-4">
							<div class="form-group">
								<label >No Of Assessments </label>
								<form:input type="number" path="noOfAssessments" value="${formBean.noOfAssessments}"
									required="required"  placeholder="0" 
									style="color:black; padding:6px 12px; width:100%;"/>
							</div>
						</div>
						
					</div>
					</div>
					
					
					
					
					
					<div class="row">
					<div class="">
						<div class="col-md-12">
							<div class="form-group">
								<label >Course Rationale : </label>
								<form:textarea class="editable"  path="courseRationale" value="${formBean.courseRationale}"  rows="5" cols="150" />
							</div>
						</div>
						
					</div>
					</div>
					
					
					<div class="row">
					<div class="">
						<div class="col-md-12">
							<div class="form-group">
								<label >Course Objectives : </label>
								<form:textarea class="editable"  path="objectives" value="${formBean.objectives}"  rows="5" cols="150" />
							</div>
						</div>
						
					</div>
					</div>
					
					
					<div class="row">
					<div class="">
						<div class="col-md-12">
							<div class="form-group">
								<label >Learning Outcomes : </label>
								<form:textarea class="editable"  path="learningOutcomes" value="${formBean.learningOutcomes}"  rows="5" cols="150" />
							</div>
						</div>
						
					</div>
					</div>
					
					<div class="row">
					<div class="">
						<div class="col-md-12">
							<div class="form-group">
								<label >Pre-requisites : </label>
								<form:textarea class="editable"  path="prerequisites" value="${formBean.prerequisites}"  rows="5" cols="150" />
							</div>
						</div>
						
					</div>
					</div>
					
					<div class="row">
					<div class="">
						<div class="col-md-12">
							<div class="form-group">
								<label >Pedagogy : </label>
								<form:textarea class="editable"  path="pedagogy" value="${formBean.pedagogy}"  rows="5" cols="150" />
							</div>
						</div>
						
					</div>
					</div>
					
					<div class="row">
					<div class="">
						<div class="col-md-12">
							<div class="form-group">
								<label >TextBook : </label>
								<form:textarea class="editable"  path="textbook" value="${formBean.textbook}"  rows="5" cols="150" />
							</div>
						</div>
						
					</div>
					</div>
					
					<div class="row">
					<div class="">
						<div class="col-md-12">
							<div class="form-group">
								<label >Journals for session plan module : </label>
								<form:textarea class="editable"  path="journals" value="${formBean.journals}"  rows="5" cols="150" />
							</div>
						</div>
						
					</div>
					</div>
					
					<div class="row">
					<div class="">
						<div class="col-md-12">
							<div class="form-group">
								<label >Links To Websites : </label>
								<form:textarea class="editable"  path="links" value="${formBean.links}"  rows="5" cols="150" />
							</div>
						</div>
						
					</div>
					</div>
					
					
					<br>
					
					<div class="form-group">
						<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="saveSessionPlan">Save Session Plan</button>
					</div>
					</div>

			</div>

			</form:form>
	</div>		
	<!-- Code for Form end -->
      	
      
      </div><!-- panelbody end -->
      <div class="panel-footer"></div>
    </div>
  </div>
  
  <!-- code for add module start -->
  	<div class="panel panel-default">
    <div class="panel-heading">
      <h4 class="panel-title">
        <a class="btn btn-info btn-block" data-toggle="collapse" href="#collapse2">Add A New Module For This Session Plan</a>
      </h4>
    </div>
    <div id="collapse2" class="panel-collapse collapse border border-primary">
      <div class="panel-body">
      	<form:form modelAttribute="module" method="post">
			<form:hidden path="sessionPlanId" value="${formBean.id }"/> 
			<form:hidden path="cloneSyllabus" value="${ false }"/>
			
			<div class="row">
				<div class="container-fluid">
					
					
					<div class="row">
					<div class="">
						<div class="col-md-6">
							<div class="form-group" style="min-width: 100%">
								<label >Module Topic</label>
								<form:input path="topic" value="${module.topic}" required="required"  placeholder="Module Topic"
									style="color:black; padding:6px 12px; width:100%;"/>
							</div>
						</div>
	
						<!-- _____________________________________ start of multiselect chapter ____________________________________ -->

						<div class='col-md-6'>
							<label for="selected" style="float: left">Chapter</label>
							<div class='multiselect' style="min-width: 100%; cursor: pointer;">
								<div id='edit'>
									<input id='chapterdata' class='chapterinput' type="text" onchange='storeData(chapterdata)' placeholder="Select Chapter">
									<span id='selected' style='color: gray; font-size: 1em'></span>
									<span class="fa-solid fa-caret-down" style="float: right"></span>
								</div>
								<div id="options_edit" style="display: none; float:left; padding: 10px;">
								
									<c:set var="count" value="${ 1 }"></c:set>
									<c:choose>
										<c:when test="${ empty syllabus }">
											<label style="display: inline;">No chapters found.</label>
										</c:when>
										<c:otherwise>
											<c:forEach var="item" items="${ syllabus }">
												<span  style="cursor: pointer;">
													<input class="option" type="checkbox" id="item_${ count }" value="${ item.id }" chapterName="${ item.chapter }" onclick='storeData(`item_${ count }`)'>
													<label for="item_${ count }" style="display: inline; padding: 5px">${ item.chapter } - ${ item.title }</label>
												</span><br>
												<c:set var="count" value="${ count+1 }"></c:set>
											</c:forEach>
										</c:otherwise>
									</c:choose>
									
							 	</div>
							</div>
							<form:hidden id="chapter" path="chapter" value="" />
							<form:hidden id="chapters" path="chapters" value="" />
							
							<!-- ____________________________________ end of multiselect chapter ____________________________________ -->
						</div>
					</div>
					</div>

					<div class="row">
					<div class="well">
						
						<div class="col-md-6">
							<div class="form-group">
								<label >Session Number :  </label>
								<form:input type="number" path="sessionModuleNo" value="${module.sessionModuleNo}"
									required="required"  placeholder="0" 
									style="color:black; padding:6px 12px; width:100%;"/>
							</div>
						</div>
						
						
						
					</div>
					</div>
					
					
					<div class="row">
					<div class="">
						<div class="col-md-12">
							<div class="form-group">
								<label >Module Outcomes : </label>
								<form:textarea class="editable"  path="outcomes" value="${module.outcomes}"  rows="5" cols="150" />
							</div>
						</div>
						
					</div>
					</div>
					
					
					<div class="row">
					<div class="">
						<div class="col-md-12">
							<div class="form-group">
								<label >Module Pedagogical Tool : </label>
								<form:textarea class="editable"  path="pedagogicalTool" value="${module.pedagogicalTool}"  rows="5" cols="150" />
							</div>
						</div>
						
					</div>
					</div>
					
					<br>
					
					<div class="form-group">
						<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="saveSessionPlanModule">Save Module</button>
					</div>
					
				</div>
			</div>
		</form:form>
      </div><!-- panelbody end -->
      <div class="panel-footer"></div>
    </div>
  </div>
  <!-- code for add module end -->
  
  <!--  ________________________ code for cloning syllabus as session plan ________________________ -->
    <c:if test="${ fn:length(sessionPlanModulesList) <= 1 }">

		<div class="panel panel-default">
			    <div class="panel-heading">
			      	<h4 class="panel-title">
			        	<a class="btn btn-info btn-block" data-toggle="collapse" href="#collapse3">Clone Syllabus as Session Plan</a>
			      	</h4>
			    </div>
			    <div id="collapse3" class="panel-collapse collapse border border-primary" style="margin: 20px">
			      	<div class="panel-body">
			      		<form:form modelAttribute="module" method="post">
			      		
							<form:hidden path="id" value="${formBean.id }"/> 
							<form:hidden path="cloneSyllabus" value="${ true }"/>
							
							<table id='syllabus' class="table table-striped table-hover" style="width: 100%; margin: auto;">
				      			<thead>
				      				<tr>
				      					<th>Sr.</th>
				      					<th>Title</th>
				      					<th>Topic</th>
				      					<th>Outcomes</th>
				      					<th>Pedagogical Tool</th>
				      				</tr>
				      			</thead>
				      			<tbody>
				      				<c:set var="count" value="1"></c:set>
				      				<c:forEach var='chapter' items="${ syllabus }">
				      					<tr>
				      						<td>${ count }</td>
				      						<td>${ chapter.title }</td>
				      						<td>${ chapter.topic }</td>
				      						<td>${ chapter.outcomes }</td>
				      						<td>${ chapter.pedagogicalTool }</td>
				      					</tr>
				      					<c:set var="count" value="${ count+1 }"></c:set>
				      				</c:forEach>
				      			</tbody>
				      		</table>
				      		<button type="submit" class="btn btn-primary" formaction="saveSessionPlanModule">Clone Syllabus</button>
							
			      		</form:form>
			      	</div>
			    </div>
		  </div>

	</c:if>
	
  <!--  ________________________ end of code for cloning syllabus as session plan ________________________ -->
  
</div>
	
	</div>	
	<!-- code for accordian end -->
		
	<!-- Code for table start -->
			<div class="clearfix"></div>
				<div class="well well-lg" style="">
					<div class="table-responsive">
						<table class="table table-striped table-hover tables"
							style="font-size: 12px">
							<thead>
								<tr>
									<th>Sr. No.</th>
									<th>Module Topic</th>
									<th>Session No </th>
									<th>Chapter</th>
									<th>
										<!-- >div class="row">
					
					<div class=" col-md-12 column form-group">
						Session Date Time
				
					</div>
					<div class="col-md-2 column form-group">
						Create Quick Session
					</div>
										
					</div!-->Session Date Time
								
									
									</th>
									<!--  th>Create Quick Session</th!-->
									<th>Modify Details</th>
									<th>Add Test</th>
									<th>Upload</th>
									<!-- th>Create Live Session</th!--->
									<th>Question Bank</th> 
									<th>Edit</th>  
									<th>Delete</th>
					
								</tr>
							</thead>
							<tbody>
								 <c:forEach var="moduleBean" items="${sessionPlanModulesList}"   varStatus="status">
								 
					        		 <tr>
							            <td ><c:out value="${status.count}" /></td>
							            <td ><c:out value="${moduleBean.topic}" /></td>
							            <td ><c:out value="${moduleBean.sessionModuleNo}" /></td>
							            <td ><c:out value="${moduleBean.chapter}" /></td>
							<td style="width: 265px"><form:form modelAttribute="session"
									id="addScheduledSessionForm-${moduleBean.sessionplan_module_id}"
									method="post" action="/acads/mbax/sp/a/admin/addScheduledSession">

									<form:input type="hidden"
										value="${moduleBean.timebondFacultyId}"
										path="timebondFacultyId" />
									<form:input type="hidden"
										value="Session ${moduleBean.sessionModuleNo }"
										path="sessionName" />
									<form:input type="hidden"
										value="${moduleBean.sessionplanMonth }" path="month" />
									<form:input type="hidden"
										value="${moduleBean.sessionplanYear }" path="year" />
									<form:input type="hidden"
										value="${moduleBean.sessionplanCreatedBy }"
										path="sessionplanCreatedBy" />
									<form:input type="hidden"
										value="${moduleBean.sessionplanLastModifiedBy }"
										path="sessionplanLastModifiedBy" />
									<form:input type="hidden"
										value="${moduleBean.sessionplanSubject }"
										path="sessionplanSubject" />
									<form:input type="hidden" value="${moduleBean.corporateName }"
										path="corporateName" />
									<form:input type="hidden"
										value="${moduleBean.moduleSessionPlanId }"
										path="moduleSessionPlanId" />
									<form:input type="hidden"
										value="${moduleBean.sessionplan_module_id }" path="moduleId" />


									<div class='input-group date'
										id='datetimepicker-${status.count}'>
										<form:input value="${moduleBean.date} ${moduleBean.startTime}"
											path="dateTime"
											id="datetimeValue-${moduleBean.sessionplan_module_id}"
											type='text' class="form-control datepickerbutton" />
										<c:choose>
											<c:when test="${moduleBean.sessionId != 0}">
												<a
													href="/acads/mbax/sp/a/admin/admineditScheduledSession?id=${moduleBean.sessionId}&consumerProgramStructureId=${moduleBean.consumerProgramStructureId }"
													class="input-group-addon disabLehover "> <b><i
														style="font-size: 20px; padding-left: 5px;"
														class="fa-solid fa-floppy-disk" aria-hidden="true"></i></b>
												</a>

												<td><a
													href="/acads/mbax/sp/a/admin/editScheduledSession?id=${moduleBean.sessionId}&consumerProgramStructureId=${moduleBean.consumerProgramStructureId }">
														<b><i style="font-size: 20px; padding-left: 15px"
															class="fa-solid fa-pen-to-square" aria-hidden="true"></i></b>
												</a></td>
											</c:when>

											<c:otherwise>
												<span style="color: #c72127; cursor: pointer;"
													class="input-group-addon newTooltip"
													onclick="saveDateTimeFunction(${moduleBean.sessionplan_module_id})">

													<span class="tooltipData">
														<em>Default Session Data</em>
															<p style="text-align: left" class="tooltipInnerData">
																Year : ${moduleBean.sessionplanYear } <br>
																Month : ${moduleBean.sessionplanMonth } <br>
																Corporate Name : ${moduleBean.corporateName } <br>
																Subject : ${moduleBean.sessionplanSubject } <br>
																Faculty Location : Remote <br>
																Session Name : Session ${moduleBean.sessionModuleNo } <br>
																Faculty Id : ${moduleBean.timebondFacultyId}
															</p>
													</span> 
													<b>
														<i style="font-size: 20px; padding-left: 5px;"class="fa-solid fa-floppy-disk" aria-hidden="true"></i>
													</b>

												</span>

												<td><span style="color: #c72127;"
													onclick="addSessionwithDetails('${moduleBean.sessionplan_module_id}','${moduleBean.sessionplanYear }','${moduleBean.sessionplanMonth }','${moduleBean.corporateName }','${moduleBean.sessionplanSubject }','${moduleBean.sessionModuleNo }','${moduleBean.timebondFacultyId }',)"
													class="input-group-check"> <b><i
															style="font-size: 20px; padding-left: 5px"
															class="fa-solid fa-bars-progress"></i></b>
												</span></td>

												<!-- a href="/acads/admin/addScheduledSessionForm?id=${moduleBean.sessionplan_module_id }&year=${moduleBean.sessionplanYear }&month=${moduleBean.sessionplanMonth }&corporateName=${moduleBean.corporateName }&subject=${moduleBean.sessionplanSubject }&sessionName=Session ${moduleBean.sessionModuleNo }&facultyId=${moduleBean.timebondFacultyId}" style="color:#333333"  class="input-group-check">      
					                               <i class="fa fa-save"></i>&ensp;Add Details
					          </a!-->

         				</c:otherwise>
      				</c:choose>	
       			</div>
       		</form:form>
							            </td>
							            <td> 
							            <form:form modelAttribute="testBean"  method="post" >	
				
							            	<c:if test="${moduleBean.testId eq null}">  
							                 
								            	<form:input type="hidden" value="${moduleBean.topic}"  path="topic"/>	
												<form:input type="hidden" value="${moduleBean.sessionModuleNo}"  path="sessionModuleNo"/>	
												<form:input type="hidden" value="${moduleBean.sessionplan_module_id}"  path="id"/>	
												<form:input type="hidden" value="${moduleBean.sessionPlanId}"  path="sessionPlanId"/>	
									            <form:input type="hidden" value="${moduleBean.timebondFacultyId}"  path="facultyId"/>	
									            <form:input type="hidden" value="2022-03-29"  path="date"/>
								            	<form:input type="hidden" value="${formBean.year}"  path="acadYear"/> 
								            	<form:input type="hidden" value="${formBean.month}"  path="acadMonth"/>  
								            	<form:input type="hidden" value="${formBean.consumerTypeId}"  path="consumerTypeIdFormValue"/>
								            	<form:input type="hidden" value="${formBean.programStructureId}"  path="programStructureIdFormValue"/>
								            	<form:input type="hidden" value="${formBean.programId}"  path="programIdFormValue"/>    
								            	<form:input type="hidden" value="${formBean.subject}"  path="subject"/>     
								            	<form:input type="hidden" value="${moduleBean.examYear}"  path="examYear"/>  
								            	<form:input type="hidden" value="${moduleBean.examMonth}"  path="examMonth"/>
								            	<form:input type="hidden" value="${moduleBean.batchName}"  path="batchName"/>    
							            	    <button title="Add Test" style="padding:0px;background-color:transparent"  
							            	    class="btn btn-primary btn-sm" formaction="/exam/mbax/ia/a/addTestForm">
							            	    <i style="font-size:20px;color:#c72127; " class="fa-solid fa-circle-plus" aria-hidden="true"></i>
							            	    </button>            
         				 						      
											</c:if>
											<c:if test="${moduleBean.testId ne null}">
												<a href="/exam/mbax/ia/a/editTest?id=${moduleBean.testId}" 
												    title="Edit Test"> 
												     <jsp:useBean id="currentDate" class="java.util.Date"/>
													<fmt:formatDate var="now" value="${currentDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
													
													<c:if test="${moduleBean.testEndDate > now  }">
													<i title="Edit Test" style="font-size:20px; padding-left:5px" class="fa-solid fa-pencil" aria-hidden="true"></i>
													</c:if>
													<c:if test="${moduleBean.testEndDate < now  }">
													 <i title="Couldn't edit.Test Expired." style="font-size:20px; padding-left:5px;color:#808080ad;" onclick="return false;" class="fa-solid fa-pencil" aria-hidden="true"></i>
													</c:if>
													
												</a>
											</c:if>
											</form:form> 
							            </td>
							             <td>
											<a href="/acads/admin/manageSessionPlanModuleById?id=${moduleBean.sessionplan_module_id }&action=upload" 
											   title="Add Learning Reasources " class="">
												<b>
													<i style="font-size:20px; padding-left:5px" class="fa-solid fa-upload" aria-hidden="true"></i>
												</b>
											</a>
							            </td>
							            <td>
											<a href="/exam/uploadQuestionBankForm?id=${moduleBean.sessionplan_module_id}" 
											   title="Add Learning Reasources " class="">
												<b>
													<i style="font-size:20px; padding-left:5px" class="fa-solid fa-upload" aria-hidden="true"></i>
												</b>
											</a>
							            </td>
										<td>
											<a href="/acads/admin/manageSessionPlanModuleById?id=${moduleBean.sessionplan_module_id }&action=edit" title="Edit SessionPlan" class="">
												<b>
													<i style="font-size:20px; padding-left:5px" class="fa-solid fa-pen-to-square" aria-hidden="true"></i>
												</b>
											</a>
										</td>
										<td>
											<a href="/acads/admin/deleteSessionPlanModule?id=${moduleBean.sessionplan_module_id }&sessionPlanId=${moduleBean.sessionPlanId}" title="Delete sessionPlan Module" class="">
												<b>
													<i style="font-size:20px; padding-left:5px" class="fa-regular fa-trash-can" aria-hidden="true"></i>
												</b>
											</a>
										</td>
							          </tr>
							      </c:forEach>
					    
							</tbody>
						</table>
					</div>
				
					
				</div> 
				
				

				
				
					 
	
	<jsp:include page="footer.jsp" />
	
        <!-- jQuery (necessary for Bootstrap's JavaScript plugins) --> 
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script> 


<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>  


<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js" ></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js" ></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.47/js/bootstrap-datetimepicker.min.js" ></script>



<script>var editor = new MediumEditor('.editable');</script>
						
<script>
var totalCount = ${sessionPlanModulesList.size()};
for(i=1 ; i<= totalCount; i++){


$('#datetimepicker-'+i).datetimepicker({
	format: 'YYYY-MM-DD HH:mm:ss',
	widgetPositioning:{
		horizontal: 'auto',
		vertical: 'bottom'
	}
});
}
</script>

 
 
 

	<script>
		
	
	
	

	$('.tables').DataTable( {
        initComplete: function () {
        	
        	
        	
            this.api().columns().every( function () {
                var column = this;
                var headerText = $(column.header()).text();
                console.log("header :"+headerText);
                if(headerText == "Subject")
                {
                   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
                    .appendTo( $(column.header()) )
                    .on( 'change', function () {
                        var val = $.fn.dataTable.util.escapeRegex(
                            $(this).val()
                        );
 
                        column
                            .search( val ? '^'+val+'$' : '', true, false )
                            .draw();
                    } );
 
                column.data().unique().sort().each( function ( d, j ) {
                    select.append( '<option value="'+d+'">'+d+'</option>' )
                } );
              }
                
                if(headerText == "Year")
                {
                   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
                    .appendTo( $(column.header()) )
                    .on( 'change', function () {
                        var val = $.fn.dataTable.util.escapeRegex(
                            $(this).val()
                        );
 
                        column
                            .search( val ? '^'+val+'$' : '', true, false )
                            .draw();
                    } );
 
                column.data().unique().sort().each( function ( d, j ) {
                    select.append( '<option value="'+d+'">'+d+'</option>' )
                } );
              }
                
                if(headerText == "Month")
                {
                   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
                    .appendTo( $(column.header()) )
                    .on( 'change', function () {
                        var val = $.fn.dataTable.util.escapeRegex(
                            $(this).val()
                        );
 
                        column
                            .search( val ? '^'+val+'$' : '', true, false )
                            .draw();
                    } );
 
                column.data().unique().sort().each( function ( d, j ) {
                    select.append( '<option value="'+d+'">'+d+'</option>' )
                } );
              }
                
                
                
            } );
        }
	
	
    } );
	
	</script>

<script type="text/javascript">

$(document).ready (function(){


	
	
	
///////////////////////////////////////////////////////////////////
	
	
	$('.selectConsumerType').on('change', function(){
	
	
	let id = $(this).attr('data-id');
	
	
	let options = "<option>Loading... </option>";
	$('#programStructureId').html(options);
	$('#programId').html(options);
	$('.selectSubject').html(options);
	
	 
	var data = {
			id:this.value
	}
console.log(this.value)
	
	console.log("===================> data id : " + id);
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "/exam/admin/getDataByConsumerType",   
		data : JSON.stringify(data),
		success : function(data) {
			console.log("SUCCESS Program Structure: ", data.programStructureData);
			console.log("SUCCESS Program: ", data.programData);
			console.log("SUCCESS Subject: ", data.subjectsData);
			var programData = data.programData;
			var programStructureData = data.programStructureData;
			var subjectsData = data.subjectsData;
			
			options = "";
			let allOption = "";
			
			//Data Insert For Program List
			//Start
			for(let i=0;i < programData.length;i++){
				allOption = allOption + ""+ programData[i].id +",";
				options = options + "<option value='" + programData[i].id + "'> " + programData[i].name + " </option>";
			}
			allOption = allOption.substring(0,allOption.length-1);
			
			//console.log("==========> options\n" + options);
			$('#programId').html(
					 "<option value='"+ allOption +"'>All</option>" + options
			);
			//End
			options = ""; 
			allOption = "";
			//Data Insert For Program Structure List
			//Start
			for(let i=0;i < programStructureData.length;i++){
				allOption = allOption + ""+ programStructureData[i].id +",";
				options = options + "<option value='" + programStructureData[i].id + "'> " + programStructureData[i].name + " </option>";
			}
			allOption = allOption.substring(0,allOption.length-1);
			
			//console.log("==========> options\n" + options);
			$('#programStructureId').html(
					 "<option value='"+ allOption +"'>All</option>" + options
			);
			//End
			
			options = ""; 
			allOption = "";
			//Data Insert For Subjects List
			//Start
			for(let i=0;i < subjectsData.length;i++){
				console.log(subjectsData[i].name);
				options = options + "<option value='" + subjectsData[i].name.replace(/'/g, "&#39;") + "'> " + subjectsData[i].name + " </option>";
			}
			
			
			console.log("==========> options2\n" + options);
			$('.selectSubject').html(
					" <option disabled selected value=''> Select Subject </option> " + options
			);
			//End
			
			
			
		},
		error : function(e) {
			
			alert("Please Refresh The Page.")
			
			console.log("ERROR: ", e);
			display(e);
		}
	});
	
	
});
	
	///////////////////////////////////////////////////////
	
	
		$('.selectProgramStructure').on('change', function(){
	
	
	let id = $(this).attr('data-id');
	
	
	let options = "<option>Loading... </option>";
	$('#programId').html(options);
	$('.selectSubject').html(options);
	
	 
	var data = {
			programStructureId:this.value,
			consumerTypeId:$('#consumerTypeId').val()
	}
	//console.log(this.value)
	
	//console.log("===================> data id : " + $('#consumerTypeId').val());
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "/exam/admin/getDataByProgramStructure",   
		data : JSON.stringify(data),
		success : function(data) {
			
			//console.log("SUCCESS: ", data.programData);
			var programData = data.programData;
			var subjectsData = data.subjectsData;
			
			options = "";
			let allOption = "";
			
			//Data Insert For Program List
			//Start
			for(let i=0;i < programData.length;i++){
				allOption = allOption + ""+ programData[i].id +",";
				options = options + "<option value='" + programData[i].id + "'> " + programData[i].name + " </option>";
			}
			allOption = allOption.substring(0,allOption.length-1);
			
			//console.log("==========> options\n" + options);
			$('#programId').html(
					 "<option value='"+ allOption +"'>All</option>" + options
			);
			//End
			
			options = ""; 
			allOption = "";
			//Data Insert For Subjects List
			//Start
			for(let i=0;i < subjectsData.length;i++){
				
				options = options + "<option value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";
			}
			
			
			console.log("==========> options\n" + options);
			$('.selectSubject').html(
					" <option disabled selected value=''> Select Subject </option> " + options
			);
			//End
			
			
			
			
		},
		error : function(e) {
			
			alert("Please Refresh The Page.")
			
			console.log("ERROR: ", e);
			display(e);
		}
	});
	
	
	
});


/////////////////////////////////////////////////////////////

	
		$('.selectProgram').on('change', function(){
	
	
	let id = $(this).attr('data-id');
	
	
	let options = "<option>Loading... </option>";
	$('.selectSubject').html(options);
	
	 
	var data = {
			programId:this.value,
			consumerTypeId:$('#consumerTypeId').val(),
			programStructureId:$('#programStructureId').val()
	}
	//console.log(this.value)
	
	
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "/exam/admin/agetDataByProgram",   
		data : JSON.stringify(data),
		success : function(data) {
			
			console.log("SUCCESS: ", data.subjectsData);
			
			var subjectsData = data.subjectsData;
			
			
			
			
			options = ""; 
			//Data Insert For Subjects List
			//Start
			for(let i=0;i < subjectsData.length;i++){
				
				options = options + "<option value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";
			}
			
			
			console.log("==========> options2\n" + options);
			$('.selectSubject').html(
					" <option disabled selected value=''> Select Subject </option> " + options
			);
			//End
			
			
			
			
		},
		error : function(e) {
			
			alert("Please Refresh The Page.")
			
			console.log("ERROR: ", e);
			display(e);
		}
	});
	
	
});

//////////////////////////////////////////////








	
	
	
});



</script>


<!-- script>

function saveDateTime(sessionId,sessionPlanId) {
	
	let datetimeValueId = "datetimeValue-"+sessionPlanId;
	let datetimeValue = document.getElementById(datetimeValueId).value;
	 var dateTimeArray = new Array();
	 dateTimeArray=datetimeValue.split(" ");
	 var date = dateTimeArray[0];
	 var time = dateTimeArray[1];
	/* let data = {
				'date':date,
				'startTime':time,
				'sessionId':sessionId
				
			};*/
		//var session  = new Object();

		//	session.date = date;
		//	session.startTime = time; 
		//	session.id =  sessionId; 
		$.ajax({
			   type : "POST",
			   contentType : "application/json",
			   url : "addScheduledSession",   
			   data : { date : date, starttime : time , id: sessionId },
			   success : function(data) {
				alert("Date Time Successfully Updated");
			   },
			   error : function(e) {
				   alert("Please Refresh The Page.")
			   }
		});
	  
	
	}
</script!-->


<script>

function saveDateTimeFunction(sessionplanModuleId) {
	let formId = "addScheduledSessionForm-"+sessionplanModuleId;
	
	/*let datetimeValueId = "datetimeValue-"+sessionPlanId;
	let datetimeValue = document.getElementById(datetimeValueId).value;
	 var dateTimeArray = new Array();
	 dateTimeArray=datetimeValue.split(" ");
	 var date = dateTimeArray[0];
	 var time = dateTimeArray[1];	
	 
	 var dateId = "date-"+sessionPlanId;
	 var timeId = "startTime-"+sessionPlanId;
	 var inputDate = document.getElementById(dateId);
	  var inputTime = document.getElementById(timeId);
	  inputDate.value = date;
	  inputTime.value = time;*/
	  document.getElementById(formId).submit();
	  
}


	
</script>

<script>
function addSessionwithDetails(id,year,month,corporateName,subject,sessionName,facultyId){
	var dateTime = document.getElementById("datetimeValue-"+id).value;
	if(month === 'Oct'){
		month = 'Jul'
	}else if(month === 'Apr'){
		month = 'Jan'
	}else{
		month = month
	}

    window.location="/acads/admin/addScheduledSessionForm?id="+id+"&year="+year+"&month="+month+"&corporateName="+corporateName+"&subject="+subject+"&sessionName=Session "+sessionName+"&facultyId="+facultyId+"&dateTime="+dateTime;
   
}
</script>	

<script>

	let chapters = [];
	let displayString = ""; 
	
	$(document).ready (function(){

		$('#edit').on('click', function(event) {
			event.preventDefault();
			$('#options_edit').slideToggle()
		});
		
		$('#add').on('click', function(event) {
			event.preventDefault();
			$('#options_add').slideToggle()
		});

		$('#chapterdata').on('change', function(event) {
			let inputValue = $('#chapterdata').val();

			if(inputValue != null){
				displayString = inputValue;
				chapters.push(inputValue);
				$('#chapters').val(chapters); 
				$('#chapter').val(chapters); 
			}else{
				const index = chapters.indexOf(inputValue);
				if (index > -1) {
					chapters.splice(index, 1);
				}
			}

		});
			
	});

	function storeData(instance){

		let selected = $("#"+instance).val();
		let chapterName = $('#'+instance).attr('chapterName')
		let inputValue = $('#chapterdata').val();
		
		if(displayString == 'Select Chapter'){
			displayString = ""
		}
		
		if($('#' + instance).is(":checked")){
			if(displayString.trim() == "")
				displayString = chapterName+" ";
			else
				displayString = displayString+", "+chapterName+",";

			chapters.push(selected);
			displayString = displayString.substring(0, (displayString.length-1));  
			if(displayString.trim() == "")
				displayString = 'Select Chapter';
			$('#selected').empty().append(displayString);
			$('#chapter').val(displayString); 
			$('#chapters').val(chapters); 
		}else{
			const index = chapters.indexOf(selected);
			if (index > -1) {
				chapters.splice(index, 1);
			}
			displayString  = displayString.replace(chapterName,'');
			displayString  = displayString.replace(",",'');
			if(displayString.trim() == "")
				displayString = 'Select Chapter';
			$('#selected').empty().append(displayString);
			$('#chapter').val(displayString); 
			$('#chapters').val(chapters); 
		}

	}
	
	$('#syllabus').DataTable({
		"lengthMenu": [5, 10, 15, 20],
        "pageLength": 5
		});
</script>
</body>
</html>
