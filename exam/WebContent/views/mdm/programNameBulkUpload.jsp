<!DOCTYPE html>
<html lang="en"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@page import="com.nmims.beans.ProgramExamBean"%>
<jsp:include page="../adminCommon/jscss.jsp">
<jsp:param value="Program Name Bulk Upload" name="title" />
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
s
</jsp:include>
<style>
.panel-title .glyphicon {
	font-size: 14px;
}

.column {
	margin-bottom: 20px;
}
</style>
<body>
<%@ include file="../adminCommon/header.jsp"%>
<div class="sz-main-content-wrapper">
		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Program Name Bulk Upload" name="breadcrumItems" />
		</jsp:include>
       <div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
			        <%@ include file="../adminCommon/messages.jsp"%>
			        <%@ include file="../mdm/uploadProgramNameError.jsp" %>
			        <div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
			        	<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data" action="programNameBulkUpload">
							<fieldset>
								<div class="panel-body">
									<div class="column">
										<div class="col-md-6 column">
											<div class="form-group">
												<form:label for="fileData" path="fileData">Select file</form:label>
												<form:input path="fileData" type="file" />
											</div>
					        			</div>
					        			
					        			<div class="col-md-12 column">
											<b>Format of Upload: </b><br>
											 
											<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/programNameBulkUpload.xlsx" target="_blank">Download a Sample Template</a>
										</div>
				        				<br>
					        			<div class="row">
										<div class="col-md-6 column">
											<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="programNameBulkUpload">Upload</button>
											<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
										</div>
									</div>	
								</div>
							</div>
						</fieldset>
					</form:form>
					</div>
		        </div>	
 			</div>	
  	 	</div>	
  	 </div>	
  </div>
<jsp:include page="../adminCommon/footer.jsp" />
  
</body>
</html>