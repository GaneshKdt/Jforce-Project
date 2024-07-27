<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../jscss.jsp">
<jsp:param value="Reallocate Project Evaluation" name="title" />
</jsp:include>

<style>
.column .dataTables .btn-group {
	width: auto;
}
.modal-dialog {
  width: 50%;
  height: 70%;
}
</style>
<!-- Dynamic Properties for Multi-Select Dropdown -->
<script type="text/javascript">
	var config={
	    search:true,
	    height:'20rem',//change only if required
	    placeholder:'Select Students Project',
	    txtSelected:'Students Project Selected',
	    txtAll:'All',
	    txtRemove: 'Students Project Removed',
	    txtSearch:'Search Sapid Or Program'
	  };
</script>

<body class="inside">

<%@ include file="../header.jsp"%>
	
	<!-- Page For Finding Faculty And List Out Faculty START -->
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Search Project Re-Allocation</legend></div>
        <%@ include file="../messages.jsp"%>
        
        	<!-- Form For Finding Faculty By Year, Month and Faculty START -->
	        <form:form  action="getNoOfProjects" method="post" modelAttribute="searchBean">
				<fieldset>
					<div class="panel-body clearfix">
						<div class="col-md-6 column">
							
							<div class="form-group">
								<form:select id="year" path="year" required="required" class="form-control"   itemValue="${searchBean.year}">
									<form:option value="">Select Year</form:option>
									<form:options items="${yearList}" />
								</form:select>
							</div>
						
							<div class="form-group">
								<form:select id="month" path="month" required="required" class="form-control"  itemValue="${searchBean.month}">
									<form:option value="">Select Month</form:option>
									<form:option value="Apr">Apr</form:option>
									<form:option value="Jun">Jun</form:option>
									<form:option value="Sep">Sep</form:option>
									<form:option value="Dec">Dec</form:option>
								</form:select>
							</div>
							
							<div class="form-group" style="overflow:visible;">
								<form:select id="firstName" path="facultyId"  class="combobox form-control" itemValue="${searchBean.firstName} ${searchBean.lastName} - ${searchBean.facultyId}">
									<form:option value="" selected="selected">Select Or Search Faculty By Id Or Name</form:option>
									<c:forEach var="faculty" items="${facultyList}"
										varStatus="status">
								    	<option value="${faculty.facultyId}">${faculty.firstName} ${faculty.lastName} - ${faculty.facultyId}</option>
									</c:forEach>
								</form:select>
							</div>
							
							<div class="form-group">
								<label class="control-label"></label>
								<div class="controls">
									<button id="getNoOfProjects" name="submit" class="btn btn-large btn-primary" formaction="getNoOfProjectsForReallocation">Find Projects</button>
									
									<button id="cancel" name="cancel" class="btn btn-danger" formaction="/studentportal/home" formnovalidate="formnovalidate">Cancel</button>
								</div>
							</div>
						</div>
					</div>
				</fieldset>
			</form:form>
		</div>
	</section>
	<!-- Form For Finding Faculty By Year, Month and Faculty START -->
			
			
	<!-- If Faculty Found Then List Out Them START -->
	<div class="panel-body clearfix">
	<%int i = 0; %>
	<c:forEach var="faculty" items="${searchedFacultyList}" varStatus="status">
        <div class="col-md-6 column" style="position:relative;margin-bottom:70px;">
        	<div class="col-md-9 column" style="position:absolute;top:10px;">
        		<span style="padding-left:0px">${faculty.facultyId} ${faculty.firstName} ${faculty.lastName} (${faculty.projectsAllocated }/${MAX_ASSIGNMENTS_PER_FACULTY})</span>
        	</div>
        	<span style="padding-left:3px;top:0px;position:absolute;right:120px"><button id="reallocate" class="btn btn-small btn-primary" onclick="facultyReallocation('${faculty.firstName}','${faculty.lastName}','${faculty.facultyId}','${faculty.projectsAllocated}','${faculty.yetEvaluated}','${MAX_ASSIGNMENTS_PER_FACULTY}')">Reallocate</button></span>
        </div>
        <%i++; %>
    </c:forEach>
	</div>
	<!-- If Faculty Found Then List Out Them END -->
		
		
	<!-- Page For Finding Faculty And List Out Faculty END -->
	

	<jsp:include page="../footer.jsp" />


	<!-- Modal For Reallocate Faculty Projects START -->
	<div class="modal fade" id="reallocateFaculty" role="dialog" >
   		<div class="modal-dialog modal-fullscreen-sm-down modal-md">
   			<div class="modal-content" style="border-radius: 5px;">
    			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<div class="text-center">
					<h2 class="modal-title selected" style="color:#d2232a">Reallocate Projects To Faculty</h2>
				</div>
			</div>
   				<div class="modal-body" style="padding: 20px 40px;">
   					<div class="panel-content-wrapper">
   						<div class="row">
   							<div class="col-10">
    								<form:form id="reallocateProject" action="reallocateProjectsToFaculty" method="post" modelAttribute="searchBean">
										
										<form:hidden path="year" id="modelYear"/>
										
										<form:hidden path="month" id="modelMonth"/>
										
										<div class="form-group mt-5">
											<label for="modeltofacultyId">Faculty ID : </label>
											<form:input path="facultyId" type="text" id="modeltofacultyId" class="form-control"/>
										</div> 
										
										<br><br>
										
										<div class="form-group mt-5">
											<label for="modelfacultyName">Faculty Name : </label>
											<form:input path="facultyName" type="text" id="modelfacultyName" class="form-control"/>
										</div>
										
										<br><br>
										
										<div class="form-group mt-5">
											<label for="modelprojectsAllocated">Evaluated Projects :</label>
											<form:input path="projectsAllocated" type="text" id="modelprojectsAllocated" class="form-control"/>
										</div> 
										
										<br><br>
										
										<div class="form-group mt-5 test">
											<label for="modelprojectsYetAllocated">Projects Yet To Be Evaluated :</label>
											<form:input path="yetEvaluated" type="text" id="modelprojectsYetAllocated" class="form-control"/>
										</div>
										
										<br><br>
										
										<div class="form-group mt-5" style="overflow:visible;">
											<label for="modelfromfacultyId">Select Faculty To Reallocate</label>
											<form:select id="modelfromfacultyId" path="toFacultyId" required="required"  class="combobox form-control" itemValue="${searchBean.firstName} ${searchBean.lastName} (${searchBean.facultyId})">
												<form:option value="" selected="selected">Select Or Search Faculty By Id Or Name</form:option>
												<c:forEach var="faculty" items="${facultyList}">
													varStatus="status">
											    	<option value="${faculty.facultyId}">${faculty.firstName} ${faculty.lastName} - ${faculty.facultyId}</option>
												</c:forEach>
											</form:select>
										</div>
										<div class="form-group">
											<label for="students">Select Students Project</label>
											<select name="sapids" id="students" class="form-control" required="required" multiple multiselect-search="true" multiselect-max-items="0" multiselect-select-all="true">
											
											</select>
										</div>
										
										<br><br>
										
										<div class="form-group mt-5">
											<div class="controls text-center">
												<button name="submit" id="reallocateBtn" class="btn btn-large btn-primary" formaction="reallocateProjectsToFaculty" onClick="validateInput()">Reallocate To Faculty</button>												
												<button id="cancel" name="cancel" class="btn btn-danger" data-dismiss="modal">Cancel</button>
											</div>
										</div>
										
								</form:form>
   							</div>
   						</div>
   					</div>	  					
   				</div>
			</div>
   		</div>
	</div>
	<!-- Modal For Reallocate Faculty Projects END -->


	<!-- JavaScript START -->
	<script src="${pageContext.request.contextPath}/assets/js/multipleCheckboxDropdown.min.js"></script>
	
	
	
	<script type="text/javascript">

		function facultyReallocation(firstName,lastName,facultyId,projectsAllocated,yetEvaluated,maxProjects)
		{
			//setting data dynamically in modal input filed
			document.getElementById("modeltofacultyId").value=facultyId;
			document.getElementById("modelYear").value=document.getElementById("year").value;
			document.getElementById("modelMonth").value=document.getElementById("month").value;
			document.getElementById("modelfacultyName").value=firstName+" "+lastName;
			document.getElementById("modelprojectsAllocated").value=projectsAllocated-yetEvaluated;
			document.getElementById("modelprojectsYetAllocated").value=yetEvaluated;
			year = document.getElementById("year").value;
			month = document.getElementById("month").value;
			
			getStudentsByfacultyId(facultyId,year,month);

			$(document).ready(function() {
			      $('.multiselect-dropdown').remove();
			});
			
			MultiselectDropdowns(window.MultiselectDropdownOptions);
			
			$('#reallocateFaculty').modal('show');
		}

		function getStudentsByfacultyId(facultyId,year,month)
		{
			let options = "";

			var data = {
				facultyId : facultyId,
				year : year,
				month : month
			}

			$.ajax({
				type : "POST",
				contentType : "application/json",
				url : "/exam/m/getStudentsDetailByFacultyId",
				data : JSON.stringify(data),
				async: false,
				success: success,
				error : error
			});

			function success(data)
			{
				options = "";
				for (let i = 0; i < data.length; i++) {
					options = options
							+ "<option value='"
							+ data[i].sapid
							+ "'> "
							+ data[i].sapid + " - " + data[i].programCode
							+ " </option>";
				}
				$('#students').html(options);
			}
			function error(e)
			{
				console.log("error in getting students list = "+e);
			}
		}

		function MultiselectDropdowns(options){
			
			  function newEl(tag,attrs){
			    var e=document.createElement(tag);
			    if(attrs!==undefined) Object.keys(attrs).forEach(k=>{
			      if(k==='class') { Array.isArray(attrs[k]) ? attrs[k].forEach(o=>o!==''?e.classList.add(o):0) : (attrs[k]!==''?e.classList.add(attrs[k]):0)}
			      else if(k==='style'){  
			        Object.keys(attrs[k]).forEach(ks=>{
			          e.style[ks]=attrs[k][ks];
			        });
			       }
			      else if(k==='text'){attrs[k]===''?e.innerHTML='&nbsp;':e.innerText=attrs[k]}
			      else e[k]=attrs[k];
			    });
			    return e;
			  }
		
			  
			  document.querySelectorAll("select[multiple]").forEach((el,k)=>{
			    
			    var div=newEl('div',{class:'multiselect-dropdown',style:{width: '100%',padding:'4px'}});
			    el.style.display='none';
			    el.parentNode.insertBefore(div,el.nextSibling);
			    var listWrap=newEl('div',{class:'multiselect-dropdown-list-wrapper'});
			    var list=newEl('div',{class:'multiselect-dropdown-list',style:{height:config.height}});
			    var search=newEl('input',{class:['multiselect-dropdown-search'].concat([config.searchInput?.class??'form-control']),style:{width:'100%',display:el.attributes['multiselect-search']?.value==='true'?'block':'none'},placeholder:config.txtSearch});
			    listWrap.appendChild(search);
			    div.appendChild(listWrap);
			    listWrap.appendChild(list);
		
			    el.loadOptions=()=>{
			      list.innerHTML='';
			      
			      if(el.attributes['multiselect-select-all']?.value=='true'){
			        var op=newEl('div',{class:'multiselect-dropdown-all-selector'})
			        var ic=newEl('input',{type:'checkbox'});
			        op.appendChild(ic);
			        op.appendChild(newEl('label',{text:config.txtAll}));
			  
			        op.addEventListener('click',()=>{
			          op.classList.toggle('checked');
			          op.querySelector("input").checked=!op.querySelector("input").checked;
			          
			          var ch=op.querySelector("input").checked;
			          list.querySelectorAll(":scope > div:not(.multiselect-dropdown-all-selector)")
			            .forEach(i=>{if(i.style.display!=='none'){i.querySelector("input").checked=ch; i.optEl.selected=ch}});
			  
			          el.dispatchEvent(new Event('change'));
			        });
			        ic.addEventListener('click',(ev)=>{
			          ic.checked=!ic.checked;
			        });
			  
			        list.appendChild(op);
			      }
		
			      Array.from(el.options).map(o=>{
			        var op=newEl('div',{class:o.selected?'checked':'',optEl:o})
			        var ic=newEl('input',{type:'checkbox',checked:o.selected});
			        op.appendChild(ic);
			        op.appendChild(newEl('label',{text:o.text}));
		
			        op.addEventListener('click',()=>{
			          op.classList.toggle('checked');
			          op.querySelector("input").checked=!op.querySelector("input").checked;
			          op.optEl.selected=!!!op.optEl.selected;
			          el.dispatchEvent(new Event('change'));
			        });
			        ic.addEventListener('click',(ev)=>{
			          ic.checked=!ic.checked;
			        });
			        o.listitemEl=op;
			        list.appendChild(op);
			      });
			      div.listEl=listWrap;
		
			      div.refresh=()=>{
			        div.querySelectorAll('span.optext, span.placeholder').forEach(t=>div.removeChild(t));
			        var sels=Array.from(el.selectedOptions);
			        if(sels.length>(el.attributes['multiselect-max-items']?.value??5)){
			          div.appendChild(newEl('span',{class:['optext','maxselected'],text:sels.length+' '+config.txtSelected}));          
			        }
			        else{
			          sels.map(x=>{
			            var c=newEl('span',{class:'optext',text:x.text, srcOption: x});
			            if((el.attributes['multiselect-hide-x']?.value !== 'true'))
			              c.appendChild(newEl('span',{class:'optdel',text:'🗙',title:config.txtRemove, onclick:(ev)=>{c.srcOption.listitemEl.dispatchEvent(new Event('click'));div.refresh();ev.stopPropagation();}}));
		
			            div.appendChild(c);
			          });
			        }
			        if(0==el.selectedOptions.length) div.appendChild(newEl('span',{class:'placeholder',text:el.attributes['placeholder']?.value??config.placeholder}));
			      };
			      div.refresh();
			    }
			    el.loadOptions();
			    
			    search.addEventListener('input',()=>{
			      list.querySelectorAll(":scope div:not(.multiselect-dropdown-all-selector)").forEach(d=>{
			        var txt=d.querySelector("label").innerText.toUpperCase();
			        d.style.display=txt.includes(search.value.toUpperCase())?'block':'none';
			      });
			    });
		
			    div.addEventListener('click',()=>{
			      div.listEl.style.display='block';
			      search.focus();
			      search.select();
			    });
			    
			    document.addEventListener('click', function(event) {
			      if (!div.contains(event.target)) {
			        listWrap.style.display='none';
			        div.refresh();
			      }
			    });    
			  });
		}

		function validateInput()
		{
			/* $('#modelfromfacultyId option').each(function() {
			    if(this.selected)
			    {
			    	var centerCount = $('#students option:not(:selected)').length;
			    	alert(centerCount);
			    } */
			    console.log("alert");
		}

		$("#reallocateBtn").click(function(){
			
		    var facultySelected = $('#modelfromfacultyId').val();
		    var studentSelected = $('#students option:selected').length;
		    
		    if(facultySelected)
		    {
		    	if(studentSelected <= 0)
		    	{
					alert("Please select at least any one studet project to reallocate!!");
					$("#reallocateProject").submit(function(e){
				        e.preventDefault();
				    });
		    	}
		    	else
			    {
		    		$("#reallocateProject").unbind('submit');
				}
		    }
		    else
			{
		    	$("#reallocateProject").submit(function(e){
			        e.preventDefault();
			    });
			}
		});
		
	</script>
	<!-- JavaScript END -->
	

</body>
</html>