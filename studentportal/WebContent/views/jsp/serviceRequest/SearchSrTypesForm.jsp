
<!DOCTYPE html>
<%@page import="java.util.*"%>
<%@page import="java.text.DateFormat"%>
<html lang="en">


<%@page import="com.nmims.beans.PageStudentPortal"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
	<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Search Assignment Status" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="../adminCommon/header.jsp"%>
        <div class="sz-main-content-wrapper">
        
		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="StudentPortal;Search Service Request Types" name="breadcrumItems"/>
			</jsp:include>
        	 
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
                   		 <div id="sticky-sidebar"> 
								<jsp:include page="../adminCommon/left-sidebar.jsp">
									<jsp:param value="" name="activeMenu"/>
								</jsp:include>
							</div>
              				
              				
              				<div class="sz-content-wrapper examsPage">
					  <%@ include file="../adminCommon/adminInfoBar.jsp"%>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">Search Service Request Types</h2>
											<div class="clearfix"></div>
							<div class="panel-content-wrapper" style="min-height:450px;">
								<%@ include file="../adminCommon/messages.jsp"%>
									<form:form  method="post" modelAttribute="assignmentStatus">
										<fieldset>
												<div class="col-sm-5">
														
														
														<div class="form-group">
			            <select data-id="consumerTypeDataId" id="consumerTypeId" name="consumerTypeId"  class="selectConsumerType form-control"  >
			             <option disabled selected value="">Select Consumer Type</option>
			             <c:forEach var="consumerType" items="${consumerType}">
			              <c:choose>
			               <c:when test="${consumerType.id == assignmentStatus.consumerTypeId}">
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
			            </select>
			          </div>
			          <div class="form-group">
			            <select id="programStructureId" name="programStructureId"  class="selectProgramStructure form-control"  >
			             <option disabled selected value="">Select Program Structure</option>
			            </select>
			          </div>
			          <div class="form-group">
			            <select id="programId" name="programId"  class="selectProgram form-control" >
			             <option disabled selected value="">Select Program</option>
			            </select>
			          </div> 
             				
													<div class="form-group">
														<label class="control-label" for="submit"></label>
															<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="SearchSrTypes">Search</button>
													</div> 
												</div>
											</fieldset> 
									</form:form>
							</div>
							<c:choose>
								<c:when test="${rowCount > 0}">
						
									<h2 style="margin-left:50px;">&nbsp;&nbsp;Service Request Types<font size="2px"> (${rowCount} Records Found)&nbsp; </font></h2>
									<div class="clearfix"></div> 
									<div class="panel-content-wrapper">
										<div class="table-responsive">
										<table class="table table-striped table-hover"
											style="font-size: 12px"> 
											<thead><tr> <th>SR Types</th> <th class="pull-right">DeActivate/Activate</th> </tr></thead> 
											<c:forEach var="requestType" items="${requestTypes}" varStatus="status"> 
											  <tr>
												<td>${requestType.serviceRequestName} </td>
												<td class="pull-right">
												 <a><c:if test="${requestType.active eq 'Y'}">
													<c:set var="checked" value="checked"/> 
													<c:set var="title" value="Switch to DeAct"/>
													</c:if>
													<c:if test="${requestType.active eq 'N'}">
													<c:set var="checked" value=""/> 
													<c:set var="title" value="Switch to Act"/>
													</c:if>
													<input type="hidden" class="id" value="${requestType.id}"/>
													<label class="switch header-switch" title="${title }">
													  <input name="chkbox" type="checkbox" ${checked }> 
													  <span class="slider round"></span>
													</label>  </a> 
												  
												</td>   
												</tr> 
											 </c:forEach>
										</table>
										</div>
									</div> 
									<br>
								</c:when>
							</c:choose>
							
							</div>
              			</div>
    				</div>
			   </div>
		    </div>
		<jsp:include page="../adminCommon/footer.jsp" />
        <script>
		 var consumerTypeId = '${ assignmentStatus.consumerTypeId }';
		 var programStructureId = '${ assignmentStatus.programStructureId }';
		 var programId = '${ assignmentStatus.programId }'; 
		 $(".header-switch").change(function() { 
			 var id = $(this).closest("tr").find(".id").val();
			 var val="N";
				if($(this).find('input[name=chkbox]').is(':checked')){
					val="Y";  
				}  
				var data = {
					    id:id,
					    active:val
					  } 
				$.ajax({
					   type : "POST",
					   contentType : "application/json",
					   url : "/studentportal/activateDeactivateSRsByCpsId",   
					   data : JSON.stringify(data) 
					  });
			});     
		</script> 
		
		<%@ include file="../common/consumerProgramStructure.jsp" %>
    </body>
</html>