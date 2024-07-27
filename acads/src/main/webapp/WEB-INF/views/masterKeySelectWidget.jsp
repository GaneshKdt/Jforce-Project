		<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%-- <jsp:include page="jscss.jsp"> --%>
<%-- <jsp:param value="Add/Edit Session" name="title" /> --%>
<%-- </jsp:include> --%>
<style>

.dropdown {
  display: block;
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

.modal-dialog{
z-index:1040;
}

</style>
<body class="inside">

<%-- <%@ include file="header.jsp"%> --%>
<!-- 	 <section class="content-container login"> -->
<!--         <div class="container-fluid customTheme"> -->
<%--         	<form:form action="" method="post" modelAttribute="session"> --%>
        		<div class="col-md-18 column multiWidget" style="padding-left:0px" > 
					<label class="dropdown" style="margin-bottom:15px">

  					<div class="dd-button"> Select Program </div>
  					<input type="checkbox" class="dd-input" id="test">

  						<ul class="dd-menu">
    						<li>
        						<div class="col-xs-7 col-md-7 col-lg-7 column programOptions" >
        						<input type="text" id="programInput" class="form-control" onkeyup="programFunction()" placeholder="Search for program.." >

<%-- 									<form:select id="masterKey" path="selectAllOptions" multiple="multiple" style="height:200px;overflow:scroll" class="form-control masterKeyMultiSelect" itemValue="${session.masterKey}" > --%>
										
<%-- 									</form:select> --%>
									
									<form:select id="masterKey" path="selectAllOptions" multiple="multiple" style="height:200px;overflow:scroll" class="form-control" itemValue="${session.masterKey}" >
										<c:forEach items="${masterKeysWithSubjectCodes}" var="bean"> 
											<form:option value="${bean.pssId}">
												${bean.consumerType} - ${bean.programStructure} - ${bean.program} - ${bean.subjectcode } - ${bean.subject }
											</form:option>  
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
		        						<form:select selected="selected" class="form-control" path="masterKey" size="9" id="selected_program" 
		        									  multiple="multiple" style="height:200px;overflow:scroll">
	          
										</form:select>
	        					</div>
    						</li>
  						</ul>
					</label>
<!-- 					<button id="submit" name="submit" class="btn btn-large btn-primary" onclick="if(confirm('Are you sure you want to Submit?')) { $('#selected_program option').prop('selected', true);}else{   return false; } "; formaction="testPageWidget">Submit</button> -->
				</div>
<%--         	</form:form> --%>
<!--         </div> -->
<!--    	</section> -->

<%-- <jsp:include page="footer.jsp" /> --%>


<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-1.11.2.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/bootstrap.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>


<script type="text/javascript">
	$(document).ready(function(){
	   $('#add').click(function() {
	    return !$('#masterKey option:selected:visible')
			.remove().appendTo('#selected_program');  
	   });
	   
	   $('#remove').click(function() {  
	    return !$('#selected_program option:selected:visible')
			.remove().appendTo('#masterKey');  
	   });
    
	 	$('#select_all').click(function() {
	     return $('#masterKey option').prop('selected', true);
		}); 

	 	$('#deselect_all').click(function() {
	     return $('#masterKey option').prop('selected', false);
		});
	 	
		function selectall()  {  
			$('#selected_program').find('option').each(function() {  
	   			$(this).attr('selected', 'selected');  
	  		});
		}
	});
</script>


<script>
	function programFunction() {
	    var input, filter, ul, li, a, i, txtValue;
	    input = document.getElementById("programInput");
	    filter = input.value.toUpperCase();
	    select = document.getElementById("masterKey");
	
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
	
	$('#subject, .dupSubject').on('change', function(){
		var subject = this.value;
		let options = "<option>Loading... </option>";
		$('.masterKeyMultiSelect').html(options);
		
		 var data = {
			subject:subject
		 }
		
		 $.ajax({
			   type : "POST",
			   contentType : "application/json",
			   url : "/acads/admin/getConsumerProgramStructureDataBySubject",   
			   data : JSON.stringify(data),
			   success : function(data) {
				   
				   	options = "";
				   	//Inserting options into list 
				   	if(data.length > 0){
					  	for(let i = 0; i < data.length; i++){
							var masterKeyName = data[i].consumerType + ' - ' + data[i].programStructure + ' - ' + data[i].program;
							options = options + "<option value='" + data[i].id + "'> " + masterKeyName + " </option>";
						}
				   	}else{
				   		options = options + "<option value=0> NO Program Found</option>";	
				   	}
				   
				   	//console.debug("options\n" + options);
				   	$('.masterKeyMultiSelect').html(options);
			   },
			   
			   error : function(e) {
				    alert("Please Refresh The Page.")
				    console.log("ERROR: ", e);
				    display(e);
				}
		 });
		 //Remove all selected programs
		 $('#selected_program option').prop('selected', true);
		 return !$('#selected_program option:selected:visible').remove().appendTo('#masterKey'); 
	});
</script>

</body>
</html>