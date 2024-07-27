
<!DOCTYPE html>


<%@page import="com.nmims.beans.FaqQuestionAnswerTableBean"%>
<%@page import="java.util.HashMap"%>
<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="FAQ Entry" name="title" />
</jsp:include>

<link rel="stylesheet" type="text/css"
	href="https://cdn.datatables.net/1.11.3/css/jquery.dataTables.css">

<script type="text/javascript" charset="utf8"
	src="https://cdn.datatables.net/1.11.3/js/jquery.dataTables.js"></script>


<script src="https://cdn.ckeditor.com/4.16.2/standard/ckeditor.js"></script>
<body>
<%
ArrayList<FaqQuestionAnswerTableBean> faqlist=(ArrayList<FaqQuestionAnswerTableBean>)request.getAttribute("faqlist");
HashMap<Integer, String> faqgroupIdNameMap = (HashMap<Integer,String>) request.getSession().getAttribute("faqGroupIdName");
HashMap<Integer, String> faqcategoryIdNameMap =(HashMap<Integer, String>) request.getSession().getAttribute("faqCategoryIdName");
HashMap<Integer, String> faqsubCategoryIdNameMap =(HashMap<Integer, String>) request.getSession().getAttribute("faqSubCategoryIdName");





%>
	<%@ include file="adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Important Documents;FAQ Entry"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu"/>
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">

						<div class="clearfix"></div>


						<div class="common-content supportWrap">

							<div class="row">
								<div class="col-lg-9 col-sm-6 col-xs-12">
									<h3 class="information-title red">Add Entry for Frequently
										Asked Questions</h3>
								</div>

							</div>
							<%
							if (String.valueOf(request.getAttribute("succes")).equals("true")) {
							%>
							<div class="alert alert-success" id="succesalert">
								<strong>Success!</strong> Entered Successfully.
							</div>
							<%
							}
							%>

							<%
							if (String.valueOf(request.getAttribute("succes")).equals("false")) {
							%>
							<div class="alert alert-danger" id="failalert">
								<strong>Failed!</strong> Please added New questions for this
								particular faqgroup.
							</div>
							<%
							}
							%>
							<div class="panel-content-wrapper" style="min-height: 450px;">

								<form action="addFaqEntry" method="post">
									<div class="form-group">
										<label for="program">FAQ Program Group Name:</label> <select
											class="form-control" aria-label="Default select example"
											id="program" name="faqgroup" required="true">
											<option value="" selected>select Program</option>
										</select>
									</div>
									<div class="form-group">
										<label for="category">Category:</label> <select
											class="form-control" aria-label="Default select example"
											id="category" name="category"
											onchange="loadListOfSubCategories(this)">
											<option value="" selected>select Category</option>
										</select>
									</div>

									<div class="form-group">
										<label for="subcat"> Sub Category:</label> <select
											class="form-control" aria-label="Default select example"
											id="subcat" name="subcategory">
											<option value="" selected>select SubCategory</option>
										</select>
									</div>

									<div class="form-group">
										<label for="question">Question</label>
										<textarea class="form-control is-invalid" id="question"
											placeholder="Enter Question" name="question" required></textarea>
									</div>

									<div class="form-group">
										<label for="answer">Answer</label>
										<textarea class="form-control ckeditor" id="answer"
											placeholder="Enter Answer" name="answer" required></textarea>
									</div>
									<button type="submit" class="btn btn-default"
										onclick="return validation()">Submit</button>
								</form>

								<div class="table-responsive">
									<table id="table_id"
										class="table datatable table-striped table-hover tables"
										style="font-size: 12px">
										<thead>
											<tr>
												<th>Sr No</th>
												<th>Question</th>
												<th>Answer</th>
												<th>Group Name</th>
												<th>Category Name</th>
												<th>SubCategory Name</th>
												<th>Action</th>
											</tr>
										</thead>
										<tbody>
											<%for(FaqQuestionAnswerTableBean faq:faqlist) {  %>
												<tr>
													<td><%=faq.getId()%></td>
													<td><%=faq.getQuestion()%> </td>
													<td><%=faq.getAnswer() %> </td>
													<td><%=faqgroupIdNameMap.get(Integer.parseInt(faq.getFaqGroupId())) %> </td>
													<td><%=faqcategoryIdNameMap.get(Integer.parseInt(faq.getCategoryId())) %> </td>
													<%if(faq.getSubCategoryId().equals("") || faq.getSubCategoryId().equals(null)) {%>
													<td> No subCategory </td>
													<%}else { %>
													<td><%=faqsubCategoryIdNameMap.get(Integer.parseInt(faq.getSubCategoryId()))%> </td>
													<%} %>
													<td><a
														href="updatefaqentrypage?id=<%=faq.getId() %>"
														style="color:green;"> Update </a>
															&nbsp;
														 <a
														href="javascript:void(0)" id="<%= faq.getId() %>"
														class="deletefaqfromlist"
														style="color: #c72127;"> Delete </a></td>

												</tr>
											<%} %>

										</tbody>
									</table>
								</div>
							</div>



							<div class="clearfix"></div>



						</div>
					</div>
				</div>

				<div>></div>
			</div>

		</div>



		<jsp:include page="adminCommon/footer.jsp" />
		<!-- <script
			src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script> -->
			<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.tabledit.js"></script>

<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>

		<script type="text/javascript">
 
 $(document).ready( function () {
		$('#table_id').DataTable({
		    "autoWidth": true
		});
		$(document).on('click',".deletefaqfromlist",function(){
			let prompt = confirm("Are you sure you want to delete?");
			if(!prompt){
				return false;
			}
			let id = $(this).attr("id");
			$.ajax({
				url:"deletefaq?id=" + id,
				method:"POST",
				success:function(response){
					if(response.status == "success"){
						alert(response.message);
						window.location.href = "faqEntryPage";
					}else{
						alert("unable to delete this record");
					}
				},
				error:function(error){
					alert("Failed to delete record");
					console.log(error);
				}
			});
		})
		
	});
 
 </script>

		<script>
	
	var program=document.getElementById("program");
	var category=document.getElementById("category");
	var subcategory=document.getElementById("subcat");
	
	window.onload=function(){
		
		
		
		var duration = 5000; //2 seconds
	    setTimeout(function () { $('#succesalert').hide(); }, duration);
		
	    setTimeout(function () { $('#failalert').hide(); }, duration);
		
		loadListOfFAQGroupList();
		loadListOfCategories();
		
		$(document).ready( function () {
		    $('#table_id').DataTable();
		} );
	};
	

	
 function	loadListOfFAQGroupList()
 {
	 fetch("/studentportal/getFAQProgramGroupList").then((response)=>{

		 return response.json();
		 }).then((res)=>{

		 for(let i=0;i<res.length;i++)
		 {
		 	 var opt = document.createElement('option');
		     opt.value = res[i].id;
		     opt.innerHTML = res[i].groupname;
		     program.appendChild(opt);
		 }

		 })
 }
 
  function loadListOfCategories()
  {
	  fetch(" /studentportal/getFaqCategoryList").then((response)=>{

			 return response.json();
			 }).then((res)=>{

			 for(let i=0;i<res.length;i++)
			 {
			 	 var opt = document.createElement('option');
			     opt.value = res[i].id;
			     opt.innerHTML = res[i].categoryname;
			     category.appendChild(opt);
			 }

			 })
  }
  
 function loadListOfSubCategories(evt)
 {
	 console.log(evt.value);
	 
	 var options = document.querySelectorAll('#subcat option');
	    options.forEach(o => o.remove());
	 
	 fetch("/studentportal/getSubFaqCategoryList?categoryid="+evt.value).then((response)=>{

		 return response.json();
		 }).then((res)=>{
			 var opt = document.createElement('option');
			 opt.value="";
		     opt.innerHTML = "select subcategory";
		     opt.selected=true;
		     subcategory.appendChild(opt);
		 for(let i=0;i<res.length;i++)
		 {
		 	 var opt = document.createElement('option');
		     opt.value = res[i].id;
		     opt.innerHTML = res[i].subCategoryName;
		     subcategory.appendChild(opt);
		 }

		 })
	 
	
 }
	
 function validation()
 {
	 console.log("test1 "+document.getElementById("program").value);
	 console.log("test2"+document.getElementById("category").value);
	 
	if(document.getElementById("program").value=="" || document.getElementById("category").value=="" )
		{
		alert("Please select options properly");
		return false;
		}
	
	return true;
 }
	
 

 
	</script>
</body>
</html>