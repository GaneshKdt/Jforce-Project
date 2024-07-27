
<!DOCTYPE html>


<%@page import="java.util.HashMap"%>
<%@page import="com.nmims.beans.FaqQuestionAnswerTableBean"%>
<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="FAQ UPDATE" name="title" />
</jsp:include>

<link rel="stylesheet" type="text/css"
	href="https://cdn.datatables.net/1.11.3/css/jquery.dataTables.css">

<script type="text/javascript" charset="utf8"
	src="https://cdn.datatables.net/1.11.3/js/jquery.dataTables.js"></script>


<script src="https://cdn.ckeditor.com/4.16.2/standard/ckeditor.js"></script>
<body>
<%
FaqQuestionAnswerTableBean faq= (FaqQuestionAnswerTableBean) request.getAttribute("faq");
ArrayList<Integer> subCatArr = (ArrayList<Integer>) request.getAttribute("subCatArr");

HashMap<Integer, String> faqgroupIdNameMap = (HashMap<Integer,String>) request.getSession().getAttribute("faqGroupIdName");
HashMap<Integer, String> faqcategoryIdNameMap =(HashMap<Integer, String>) request.getSession().getAttribute("faqCategoryIdName");
HashMap<Integer, String> faqsubCategoryIdNameMap =(HashMap<Integer, String>) request.getSession().getAttribute("faqSubCategoryIdName");

%>

	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Important Documents;FAQ Entry;FAQ Update"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">

						<div class="clearfix"></div>


						<div class="common-content supportWrap">

							<div class="row">
								<div class="col-lg-9 col-sm-6 col-xs-12">
									<h3 class="information-title red">Update Entry for Frequently
										Asked Questions</h3>
								</div>

							</div>
							<%
							if (String.valueOf(request.getAttribute("succes")).equals("true")) {
							%>
							<div class="alert alert-success" id="succesalert">
								<strong>Success!</strong> Updated Successfully.
							</div>
							<%
							}
							%>

							<%
							if (String.valueOf(request.getAttribute("succes")).equals("false")) {
							%>
							<div class="alert alert-danger" id="failalert">
								<strong>Failed!</strong> Update Failed
							</div>
							<%
							}
							%>
							<div class="panel-content-wrapper" style="min-height: 450px;">

								<form action="updateFaqEntry" method="post">
								
								  <div class="form-group">
							             <input type="hidden" 
											 name="faqid"  value="<%=faq.getId() %>" >
										</input>
									</div>
									<div class="form-group">
										<label for="program">FAQ Program Group Name:</label> <select
											class="form-control" aria-label="Default select example"
											id="program" name="faqgroup" required="true">
											<option value="<%=faq.getFaqGroupId()%>" selected><%=faqgroupIdNameMap.get(Integer.parseInt(faq.getFaqGroupId())) %></option>
										</select>
									</div>
									<div class="form-group">
										<label for="category">Category:</label> <select
											class="form-control" aria-label="Default select example"
											id="category" name="category"
											onchange="loadListOfSubCategories(this)">
											<option value="<%=faq.getCategoryId()%>" selected><%=faqcategoryIdNameMap.get(Integer.parseInt(faq.getCategoryId()))%></option>
										</select>
									</div>

									<div class="form-group">
										<label for="subcat"> Sub Category:</label> <select
											class="form-control" aria-label="Default select example"
											id="subcat" name="subcategory">
 											<%if(faq.getSubCategoryId().equals("") ||faq.getSubCategoryId().equals(null)) { %> 
											<option value="" selected>select SubCategory</option>
											<c:forEach items="<%=subCatArr%>" var="faqsubCategorylist">
												<%-- <% System.out.println("FaqXXXX: " + (Integer)pageContext.getAttribute("faqsubCategorylist")); %> --%>
                                                <option value="${faqsubCategorylist}"><%=faqsubCategoryIdNameMap.get((Integer)pageContext.getAttribute("faqsubCategorylist")) %></option>
                                                </c:forEach>
 											<%} else{ %> 
										<option value="" selected><%=faqsubCategoryIdNameMap.get(Integer.parseInt(faq.getSubCategoryId())) %></option> 
											<%} %> 
										</select>
									</div>
										
									 <div class="form-group">
										<label for="question">Question</label>
										<textarea class="form-control is-invalid" id="question" 
											placeholder="Enter Question" name="question"  required><%=faq.getQuestion() %></textarea>
									</div> 

									<div class="form-group">
										<label for="answer">Answer</label>
										<textarea class="form-control ckeditor" id="answer"
											placeholder="Enter Answer" name="answer" required><%=faq.getAnswer() %></textarea>
									</div>
									<button type="submit" class="btn btn-default"
										onclick="return validation()">Update</button>
								</form>

							</div>



							<div class="clearfix"></div>



						</div>
					</div>
				</div>

				<div>></div>
			</div>

		</div>



		<jsp:include page="../adminCommon/footer.jsp" />



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