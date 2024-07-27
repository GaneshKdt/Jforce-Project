
 <!DOCTYPE html>
<html lang="en">
	
<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.*"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>



<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
    <jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Dummy Users" name="title"/>
    </jsp:include>
    
    <link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/dataTables.bootstrap.css"> 
    <%    
    List<DummyUserBean> dummyUsersList = (List<DummyUserBean>)request.getSession().getAttribute("dummyUsersList"); 
    %>
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/2.0.0/jquery.min.js"></script>
    <body>
    <%try{ %>
    	<jsp:include page="adminCommon/header.jsp" />
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Dummy Users" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">Search Dummy Users</h2>
											<div class="clearfix"></div>
											<div class="panel-content-wrapper" >
											<!-- style="min-height:450px;" -->
											<%@ include file="adminCommon/messages.jsp" %>
									<form:form  action="dummyUsersReport" method="post" modelAttribute="batchBean">									
											<fieldset>
													
											<div class="col-md-4 column">			
													
												<%-- <div class="form-group">
													<form:select path="program"  required="required"	class="form-control"   itemValue="${batchBean.program}">
														<form:option value="">Select Program</form:option>
														<form:option value="MBA - WX">MBA - WX</form:option>															
													</form:select>
												</div>	 --%>
																								
												<div class="form-group">
												<label for="consumerType">Consumer Type</label>
												<form:select data-id="consumerTypeDataId" id="consumerTypeId" path="consumerTypeId"  class="selectConsumerType form-control" required="required">
													<form:option  value="" disabled="disabled" selected="selected">Select Consumer Type</form:option>
													<c:forEach var="consumerType" items="${consumerType}">
										                <form:option value="${consumerType.id}">
										                  ${consumerType.name}
										                </form:option>
										            </c:forEach>
													</form:select>
												</div>
												
												<div class="form-group" >
												<label for="programType">Program Type</label>
												<form:select data-id="programTypeDataId" id="programTypeId" path="programType" class="selectProgramType form-control"> 
													<form:option value="" disabled="disabled" selected="selected">Select Program Type</form:option>													
												</form:select>
												</div> 
												
												<div class="form-group">
												<label for="programStructure">Program Structure</label>
												<form:select data-id="programStructureDataId" id="programStructureId" path="programStructureId" class="selectProgramStructure form-control">
													<form:option disabled="disabled" selected="selected" value="">Select Program Structure</form:option>
					
												</form:select>
												</div>
												
												<div class="form-group">
												<label for="Program">Program</label>
												<form:select data-id="programDataId" id="programId" path="programId" class="selectProgram form-control">
													<form:option disabled="disabled" selected="selected" value="">Select Program</form:option>
						
												</form:select>
												</div>
												
												<div class="form-group">
												<label for="sem">Sem/Term</label>
												<form:select data-id="semDataId" id="semId" path="sem" class="selectSem form-control">
													<form:option selected="selected" value="-1">Select Sem/Term</form:option>
													<form:option value="">All</form:option>
													<form:option value="1">1</form:option>
													<form:option value="2">2</form:option>
													<form:option value="3">3</form:option>
													<form:option value="4">4</form:option>
												</form:select>
												</div>																	
													
												<div class="form-group">
													<label for="batchId">Batch</label>													
													<form:select data-id="batchDataId" id="batchId" path="batchId" class="selectBatch form-control">	
														<form:option disabled="disabled" selected="selected" value="">Select Batch</form:option>														
													</form:select>
												</div>										
													
												</div>
												
												
												
												<div class="col-md-4 column">
																							
										<div class="form-group"> 
												<button id="submit" name="submit" class="btn  btn-primary" formaction="dummyUsersReport">Search</button>												
												<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
										</div>

												</div>
										</fieldset>
								</form:form>
							</div> 
						
		<div class="">
			<c:if test="${dummyUsersListSize > 0 }">
				<h2>&nbsp;Dummy Users Report<font size="2px"> (${dummyUsersListSize} Records Found) &nbsp; <a href="downloadDummyUsers" style="color:blue;">Download to Excel</a></font></h2>
				<div class="clearfix"></div>
				<div class="panel-content-wrapper">								
					<div style="overflow:hidden;overflow-x: scroll;">
						<table  id="dataTable" class=" table table-striped table-hover" style="font-size:12px;">												
						<thead>
							<tr> 							
								<th>Sr. No.</th>
								<th>User Id</th>
								<th>Consumer Type</th>
								<th>Program Type</th>								
								<th>Program Structure</th>
								<th>Program</th>								
								<th>Action</th>
												
							</tr>
						</thead>		
						<tbody>
							<%
							int srCount = 0;
							for(DummyUserBean dummyUser : dummyUsersList){ %>
							<tr>
									<td> <%= ++srCount %> </td>									
									<td><%=dummyUser.getUserId() %> </td>
									<td><%=dummyUser.getConsumerType() %></td>
									<td><%=dummyUser.getProgramType() %></td>									
									<td><%=dummyUser.getProgramStructure() %></td>
									<td><%=dummyUser.getProgram() %></td>																				
									<td><button class="btn btn-large btn-primary" onclick="loginDummyUser('<%= dummyUser.getUserId() %>')">Login As</button></td>
							</tr>
							<%} %>
	
						</tbody>		
						
					
					</table>
				
			</div>		
			</div>
			</c:if>
		</div>
				</div>
              			</div>
    				</div>
			   </div>
		    </div>
        <jsp:include page="adminCommon/footer.jsp"/>
        <%}catch(Exception e){e.printStackTrace();}%>
		
		
    </body>
    <script type="text/javascript">
    
    function loginDummyUser(userId){
    	$.ajax({
	         type: 'POST',
	         url: '/acads/impersonate',
	         data: {
	        	 username: userId
	         },
	         success: function (data) {
	            console.log(data);
	         },
	         error: function(jqXHR, exception) {
		         console.log(jqXHR.responseText);
		         console.log(exception)
	         }
	     }); 
		
		$.ajax({
            type: 'POST',
            url: '/exam/impersonate',
            data: {
            	username: userId
            },
            success: function (data) {
            	console.log(data);
            }
        });
            	
    	//var url = "dummyUserLogin?userId="+userId; 
    	var url = "/studentportal/impersonate?username="+userId;   
    	var height = screen.availHeight
    	var width = screen.availWidth
    	
    	window.open(url,'_self'); 
    }
    
    </script>
    

       <script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
       <script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
       <script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.buttons.min.js"></script>
       <script
              src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
       <script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
       <script
              src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.buttons.min.js"></script>       

<script type="text/javascript">

$(document).ready (function(){
       $('#dataTable').DataTable();
       
       $('.selectConsumerType').on('change', function(){
    		
    		
    		let id = $(this).attr('data-id');
    		
    		
    		let options = "<option>Loading... </option>";
    		$('#programStructureId').html(options);
    		$('#programId').html(options);
    		$("#programTypeId").html(options);
    		$("#batchId").html(options);
    		
    		var data = {
    				consumerTypeId:this.value
    		}
    		console.log(this.value)
    		
    		console.log("===================> data id : " + id);
    		$.ajax({
    			type : "POST",
    			contentType : "application/json",
    			url : "getDataBasedOnConsumerType",   
    			data : JSON.stringify(data),
    			success : function(data) {
    				console.log("SUCCESS Program Structure: ", data.programStructureData);
    				console.log("SUCCESS Program Type: ", data.programTypeData);
    				console.log("SUCCESS Program: ", data.programData);
    				console.log("SUCCESS Batch: ", data.batchData);
    				var programData = data.programData;
    				var programStructureData = data.programStructureData;
    				var programTypeData = data.programTypeData;
    				var batchData = data.batchData;
    				
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
    						 "<option value=''>All</option>" + options
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
    						 "<option value=''>All</option>" + options
    				);
    				//End 
    				options = ""; 
    				allOption = "";
    				//Data Insert For Program Type List
    				//Start
    				for(let i=0;i < programTypeData.length;i++){
    					allOption = allOption + ""+ programTypeData[i].name +",";
    					options = options + "<option value='" + programTypeData[i].name + "'> " + programTypeData[i].name + " </option>";
    				}
    				allOption = allOption.substring(0,allOption.length-1);
    				
    				//console.log("==========> options\n" + options);
    				$('#programTypeId').html(
    						 "<option value=''>All</option>" + options
    				);
    				//End
    				
    				//set All for sem list
    				$('#semId option[value="-1"]').remove();
    				$("#semId").val("");
    				
    				options = ""; 
    				allOption = "";
    				//Data Insert For Batch List
    				//Start
    				for(let i=0;i < batchData.length;i++){
    					allOption = allOption + ""+ batchData[i].id +",";
    					options = options + "<option value='" + batchData[i].id + "'> " + batchData[i].name + " </option>";
    				}
    				allOption = allOption.substring(0,allOption.length-1);
    				
    				//console.log("==========> options\n" + options);
    				$('#batchId').html(
    						 "<option value=''>All</option>" + options
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
    	
    	$('.selectProgramType').on('change', function(){
    		
    		
    		let id = $(this).attr('data-id');
    		
    		
    		let options = "<option>Loading... </option>";
    		$('#programStructureId').html(options);
    		$('#programId').html(options); 
    		$('#batchId').html(options); 
    		
    		var data = {
    				programType:this.value,
    				consumerTypeId:$('#consumerTypeId').val()
    		}
    		//console.log(this.value)
    		
    		//console.log("===================> data id : " + $('#consumerTypeId').val());
    		$.ajax({
    			type : "POST",
    			contentType : "application/json",
    			url : "getDataBasedOnProgramType",   
    			data : JSON.stringify(data),
    			success : function(data) {
    				
    				//console.log("SUCCESS: ", data.programData);
    				var programData = data.programData; 
    				var programStructureData = data.programStructureData;    				
    				var batchData = data.batchData;
    				
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
    						 "<option value=''>All</option>" + options
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
    						 "<option value=''>All</option>" + options
    				);
    				//End
    				
    				//set All for sem list
    				$('#semId option[value="-1"]').remove();
    				$("#semId").val("");
    				
    				options = ""; 
    				allOption = "";
    				//Data Insert For Batch List
    				//Start
    				for(let i=0;i < batchData.length;i++){
    					allOption = allOption + ""+ batchData[i].id +",";
    					options = options + "<option value='" + batchData[i].id + "'> " + batchData[i].name + " </option>";
    				}
    				allOption = allOption.substring(0,allOption.length-1);
    				
    				//console.log("==========> options\n" + options);
    				$('#batchId').html(
    						 "<option value=''>All</option>" + options
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
    		$('#batchId').html(options);
    		 
    		var data = {
    				programStructureId:this.value,
    				programType:$('#programTypeId').val(),
    				consumerTypeId:$('#consumerTypeId').val()
    		}
    		//console.log(this.value)
    		
    		//console.log("===================> data id : " + $('#consumerTypeId').val());
    		$.ajax({
    			type : "POST",
    			contentType : "application/json",
    			url : "getDataBasedOnProgramStructure",   
    			data : JSON.stringify(data),
    			success : function(data) {
    				
    				//console.log("SUCCESS: ", data.programData);
    				var programData = data.programData;
    				var batchData = data.batchData;
    				
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
    						 "<option value=''>All</option>" + options
    				);
    				//End
    				
    				//set All for sem list
    				$('#semId option[value="-1"]').remove();
    				$("#semId").val("");
    				
    				options = ""; 
    				allOption = "";
    				//Data Insert For Batch List
    				//Start
    				for(let i=0;i < batchData.length;i++){
    					allOption = allOption + ""+ batchData[i].id +",";
    					options = options + "<option value='" + batchData[i].id + "'> " + batchData[i].name + " </option>";
    				}
    				allOption = allOption.substring(0,allOption.length-1);
    				
    				//console.log("==========> options\n" + options);
    				$('#batchId').html(
    						 "<option value=''>All</option>" + options
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
    		$('.selectBatch').html(options);
    		    		
    		var data = {
    				programId:this.value,
    				consumerTypeId:$('#consumerTypeId').val(),    				
    				programStructureId:$('#programStructureId').val(),
    				programType:$('#programTypeId').val(),
    				
    		}
    		//console.log(this.value)
    		
    		
    		$.ajax({
    			type : "POST",
    			contentType : "application/json",
    			url : "getDataBasedOnProgram",   
    			data : JSON.stringify(data),
    			success : function(data) {
    				
    				console.log("SUCCESS: ", data.batchData);
    				
    				var batchData = data.batchData;

    				//set All for sem list
    				$('#semId option[value="-1"]').remove();
    				$("#semId").val("");
    				
    				options = "";
    				let allOption= "";
    				//Data Insert For Batch List
    				//Start
    				for(let i=0;i < batchData.length;i++){
    					allOption = allOption + ""+ batchData[i].id +",";
    					options = options + "<option value='" + batchData[i].id + "'> " + batchData[i].name + " </option>";
    				}
    				allOption = allOption.substring(0,allOption.length-1);
    				
    				//console.log("==========> options\n" + options);
    				$('#batchId').html(
    						 "<option value=''>All</option>" + options
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
    	
    	$('.selectSem').on('change', function(){
    		
    		
    		let id = $(this).attr('data-id');
    		
    		
    		let options = "<option>Loading... </option>";
    		$('.selectBatch').html(options);
    		
    		 
    		var data = {
    				sem:this.value,
    				programId:$("#programId").val(),
    				consumerTypeId:$('#consumerTypeId').val(),    				
    				programStructureId:$('#programStructureId').val(),
    				programType:$('#programTypeId').val(),
    				
    		}
    		//console.log(this.value)
    		
    		
    		$.ajax({
    			type : "POST",
    			contentType : "application/json",
    			url : "getDataBySem",   
    			data : JSON.stringify(data),
    			success : function(data) {
    				
    				console.log("SUCCESS: ", data.batchData);
    				
    				var batchData = data.batchData;    			
    				
    				options = "";
    				let allOption= "";
    				//Data Insert For Batch List
    				//Start
    				for(let i=0;i < batchData.length;i++){
    					allOption = allOption + ""+ batchData[i].id +",";
    					options = options + "<option value='" + batchData[i].id + "'> " + batchData[i].name + " </option>";
    				}
    				allOption = allOption.substring(0,allOption.length-1);
    				
    				//console.log("==========> options\n" + options);
    				$('#batchId').html(
    						 "<option value=''>All</option>" + options
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

    
</html>