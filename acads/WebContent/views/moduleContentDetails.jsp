
<!DOCTYPE html>
<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.PageAcads"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.ModuleContentAcadsBean"%>
<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="jscss.jsp">
	<jsp:param value="Edit Module Content" name="title" />
</jsp:include>

<body class="inside">
<%
	ArrayList<ModuleContentAcadsBean> moduleList = (ArrayList<ModuleContentAcadsBean>)request.getAttribute("moduleContentsList");

	int size = moduleList !=null? moduleList.size() : 0;
%>

	<%@ include file="header.jsp"%>
	<section class="content-container">
		<div class="container-fluid customTheme">
		<div class="row"><legend>&nbsp; Module Content</legend></div>
		<div class="panel-body">
		<%@ include file="messages.jsp"%>
		
    <%try{ %>
    		<div class="sz-content">
 										<h2 class="red text-capitalize">
												Edit Module Content
									    </h2>
									    
									    
											<div class="container-fluid " style="color:black; padding-top:20px;">
											<div class="row">
											<div class=" col-md-5">
											<form:form  action="saveModuleContents" method="post" modelAttribute="moduleContent">
												<fieldset> 
													<form:hidden path="id" value="${moduleContent.id}"/> 
													<div class="clearfix"></div>
													<div class="form-group">
														<label for="subject">Subject </label>
														<form:input path="subject" value="${moduleContent.subject}" required="required"  placeholder="Add Subject" style="color:black; padding:6px 12px; width:100%;"/>
													</div>
													<div class="form-group">
														<label for="moduleName"> Module Title </label>
														<form:input path="moduleName" value="${moduleContent.moduleName}" required="required"  placeholder="Module Name" style="color:black; padding:6px 12px; width:100%;"/>
													</div> 
													<div class="form-group">
															<label for="description"> Module Description </label>
														<form:textarea path="description" id="description" maxlength="1000" class="form-control" value='${moduleContent.description}' placeholder="Add description for the Module Content..." cols="50"/>
													</div>
													<%-- <div class="form-group">
														<label for="dueDate"> Module Due Date </label>
														<form:input type="date" path="dueDate" value="${moduleContent.dueDate}" required="required"  placeholder="Add Due Date" style="color:black; padding:6px 12px; width:100%;"/>
													</div>
													
													<div class="form-group">
														<label for="videoId"> Add Video ID Of Video This Module Is </label>
														<form:input path="videoId" value="${videoContent.videoId}" required="required"  placeholder="Add Video ID" style="color:black; padding:6px 12px; width:100%;"/>
													</div>
													<div class="form-group">
														<label for="startTime"> Module Start Time (HH:MM:SS) </label>
														<form:input  path="startTime" value="${moduleContent.startTime}" required="required"  placeholder="HH:MM:SS" style="color:black; padding:6px 12px; width:100%;"/>
													</div>
													<div class="form-group">
														<label for="moduleVideoUrl"> Module Video Url </label>
														<form:input path="moduleVideoUrl" value="${moduleContent.moduleVideoUrl}" required="required"  placeholder="Add Video Url" style="color:black; padding:6px 12px; width:100%;"/>
													</div> 
													
													 --%>
													<div class="form-group">
														<form:select id="active" path="active" type="text" placeholder="Month" class="form-control"  itemValue="${moduleContent.active}">
															<form:option value="">Select Month</form:option>
															<form:option value="Y">Yes</form:option>
															<form:option value="N">No</form:option>
														</form:select>
													</div>
													
													<div class=" form-group controls">
														<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="saveModuleContents" >Save</button>
													</div>
									
												</fieldset>
											</form:form>
											<div class="well">
											 <a href="/acads/admin/editModuleVideoContents?moduleId=${moduleContent.id}" class="btn btn-default">
											 	Module to Video Topice Mapping
											 </a>
											</div>
											</div>

																						
											<div class=" col-md-7">
											
											<div class="well">
												<h4>Upload Document Details </h4>
												
												<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data" action="uploadModulesPdf">
												<form:hidden path="fileId" value="${moduleContent.id }"/> 
													
												<div class="panel-body">
												<div class="col-md-6 column">
													
													<div class="form-group">
														<label for="fileName"> Document Name </label>
														<form:input path="fileName"  required="required"  placeholder="Document Title" style="color:black; padding:6px 12px; width:100%;"/>
													</div> 
													<div class="form-group">
														<form:label for="fileData" path="fileData">Select file</form:label>
														<form:input path="fileData" type="file" />
													</div>
													
												</div>
										
										
										
										
										</div>
										<br>
										<div class="row">
											<div class="col-md-6 column">
												<button id="submit" name="submit" class="btn btn-large btn-primary"
													formaction="uploadModulesPdf">Upload</button>
											</div>
								
										</div>
										</form:form>
										</div> 
										
										<div class="well">
											<c:if test="${not empty moduleDocumnentList}">
													<h4>Edit Document Details </h4>
												
												<div class="panel-group" id="accordion">
												 <c:forEach var="document" items="${moduleDocumnentList}">
												  <div class="panel panel-default">
												    <div class="panel-heading">
												      <div class="row" style=""> 
												      <div class="col-sm-11" style=""> 
												      <h4 class="panel-title">
												        <a data-toggle="collapse" data-parent="#accordion" href="#collapse${document.id}">
												        > &nbsp; ${document.documentName}
												        </a>
												      </h4>
												      </div>
												      <div class="col-sm-1" style=""> 
												       <a href="/acads/admin/deleteModuleDocument?id=${document.id}" style="float:right; padding: 5px 10px !important;" >
												      <i class="fa-regular fa-trash-can" style="font-size:20px !important; color:black;" aria-hidden="true"></i>
												      </a> 
												      </div>
												      </div>
												    </div>
												    <div id="collapse${document.id}" class="panel-collapse collapse">
												      <div class="panel-body">
													  <!-- id, moduleId, documentName, folderPath, type, active, noOfPages,
													  	 createdBy, lastModifiedBy, createdDate, lastModifiedDate -->
															<form:form  action="sMD" method="post" modelAttribute="moduleDocument">
																<fieldset> 
																	<form:hidden path="id" value="${document.id }"/> 
																	<div class="clearfix"></div>
																	<div class="form-group">
																		<label for="moduleId">Module ID  </label>
																		<form:input path="moduleId" value="${document.moduleId}" required="required"  placeholder="Add Module ID" style="color:black; padding:6px 12px; width:100%;"/>
																	</div>
																	<div class="form-group">
																		<label for="documentName"> Document Title </label>
																		<form:input path="documentName" value="${document.documentName}" required="required"  placeholder="Document Title" style="color:black; padding:6px 12px; width:100%;"/>
																	</div> 
																	 <%-- 
																	<div class="form-group">
																		<p><b>Note : </b> Select a <b>PDF</b> file only to update earlier file </p>
																		<form:label for="fileData" path="fileData">Select file (optional)</form:label>
																		<form:input path="fileData" type="file" />
																	</div> --%>
																	
																	<div class="form-group">
																			<label for="folderPath"> Document Folder Path </label>
																		<form:input path="folderPath" value="${document.folderPath}" required="required"  placeholder="Add Folder Path" style="color:black; padding:6px 12px; width:100%;"/>
																	</div>
																	
																	<div class="form-group">
																		<label for="type"> Document Type </label>
																		<form:input path="type" value="${document.type}" required="required"  placeholder="Document Type" style="color:black; padding:6px 12px; width:100%;"/>
																	</div> 
																	<div class="form-group">
																		<label for="noOfPages"> No Of Pages  </label>
																		<form:input path="noOfPages" value="${document.noOfPages}" required="required"  placeholder="Add noOfPages" style="color:black; padding:6px 12px; width:100%;"/>
																	</div><%-- 
																	<div class="form-group">
																		<label for="noOfPages"> Select Active Status</label>
																		<form:select  path="active" type="text" placeholder="Active" class="form-control"  itemValue="${document.active}">
																			<form:option value="${document.active}">${document.active}</form:option>
																			<form:option value="Y">Yes</form:option>
																			<form:option value="N">No</form:option>
																		</form:select>
																	</div> --%>
																	
																	
																	<div class=" for	m-group controls">
																		<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="sMD" >Save</button>
																	</div>
													
																</fieldset>
															</form:form>													 
													 
													  </div>
												    </div>
												  </div> 
												  </c:forEach>
												</div>
												</c:if>
										</div>
									
								</div>
              				</div>
              				
												  </div>
												  </div>
												  
              				
              				
              		</div>
         </div>
         
        <%}catch(Exception e){
        	  
        } %>
	</section>

	<jsp:include page="footer.jsp" />

</body>
</html>
