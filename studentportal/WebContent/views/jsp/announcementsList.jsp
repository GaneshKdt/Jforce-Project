<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Announcements" name="title" />


</jsp:include>
<style>
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
tr:hover{
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
</style>

<body class="inside">

<%@ include file="header.jsp"%>
        


	
    <section class="content-container login">
        <div class="container-fluid customTheme">

				<div class="row"><legend>All Announcements</legend></div>

					<table class="table table-striped panel-body">
						<thead>
							<tr>
								<th>Sr. No.</th>
								<th>Subject</th>
								<th><center>Program</center></th>
								<th>Start Date</th>
								<th>End Date</th>
								<th>Category</th>
								<th>Active</th>
								<th>Actions</th>
							</tr>
						</thead>
						<tbody>
						
						<c:forEach var="announcement" items="${announcements}" varStatus="status">
						
					        <tr>
					            <td><c:out value="${status.count}"/></td>
					            <td><c:out value="${announcement.subject}"/></td>
       					  <td ><center><a href="javascript:void(0)" data-id="${ announcement.id }" data-id2 = "${announcement.startDate }" class="commonLinkbtn">Common Announcement For ${ announcement.count } programs</center></a></td>
					            <td><c:out value="${announcement.startDate}"/></td>
					            <td><c:out value="${announcement.endDate}"/></td>
					            <td><c:out value="${announcement.category}"/></td>
					            <td><c:out value="${announcement.active}"/></td>
					     		 <!--  td><c:out value="${editAnnouncement}"/></td-->
					            
					            <td> 
						            <c:url value="/admin/editAnnouncement" var="editurl">
									  <c:param name="id" value="${announcement.id}" />
									</c:url>
									<c:url value="/admin/deleteAnnouncement" var="deleteurl">
									  <c:param name="id" value="${announcement.id}" />
									</c:url>
									<c:url value="/admin/viewAnnouncementDetails" var="detailsUrl">
									  <c:param name="id" value="${announcement.id}" />
									</c:url>
									<a class="fa-solid fa-circle-info fa-xl" href="${detailsUrl}" title="Details"></a>
									<%if(roles.indexOf("Portal Admin") != -1 || roles.indexOf("Student Support")!=-1){ %>
									<a class="fa-solid fa-pencil" href="${editurl}" title="Edit"></a>
									<a class="fa-regular fa-trash-can" href="${deleteurl}" title="Delete" onclick="return confirm('Are you sure you want to delete this Announcement?')"></a>
									<%} %>
					            </td>
					        </tr>   
					      
					    </c:forEach>
							
							
						</tbody>
					</table>
					
					
			</div>
			
			
			<c:url var="firstUrl" value="/admin/getAllAnnouncementsPage?pageNo=1" />
			<c:url var="lastUrl"
			value="/admin/getAllAnnouncementsPage?pageNo=${page.totalPages}" />
			<c:url var="prevUrl"
			value="/admin/getAllAnnouncementsPage?pageNo=${page.currentIndex - 1}" />
			<c:url var="nextUrl"
			value="/admin/getAllAnnouncementsPage?pageNo=${page.currentIndex + 1}" />


		<c:choose>
			<c:when test="${page.totalPages > 1}">
				<div align="center">
					<ul class="pagination">
						<c:choose>
							<c:when test="${page.currentIndex == 1}">
								<li class="disabled"><a href="#">&lt;&lt;</a></li>
								<li class="disabled"><a href="#">&lt;</a></li>
							</c:when>
							<c:otherwise>
								<li><a href="${firstUrl}">&lt;&lt;</a></li>
								<li><a href="${prevUrl}">&lt;</a></li>
							</c:otherwise>
						</c:choose>
						<c:forEach var="i" begin="${page.beginIndex}"
							end="${page.endIndex}">
							<c:url var="pageUrl" value="getAllAnnouncementsPage?pageNo=${i}" />
							<c:choose>
								<c:when test="${i == page.currentIndex}">
									<li class="active"><a href="${pageUrl}"><c:out
												value="${i}" /></a></li>
								</c:when>
								<c:otherwise>
									<li><a href="${pageUrl}"><c:out value="${i}" /></a></li>
								</c:otherwise>
							</c:choose>
						</c:forEach>
						<c:choose>
							<c:when test="${page.currentIndex == page.totalPages}">
								<li class="disabled"><a href="#">&gt;</a></li>
								<li class="disabled"><a href="#">&gt;&gt;</a></li>
							</c:when>
							<c:otherwise>
								<li><a href="${nextUrl}">&gt;</a></li>
								<li><a href="${lastUrl}">&gt;&gt;</a></li>
							</c:otherwise>
						</c:choose>
					</ul>
				</div>
			</c:when>
		</c:choose>
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
			   url : "/studentportal/admin/getCommonAnnouncementProgramsList",   
			   data : JSON.stringify(data),
			   success : function(data) {
				   
			
				   modalBody = '<div class="table-responsive"> <table class="table"> <th>Consumer Type</th> <th>Program</th> <th>Program Structure</th><th>Actions</th> </thead><tbody>';
				   for(let i=0;i < data.length;i++){
					   modalBody = modalBody + '<tr><td>'+ data[i].consumer_type +'</td><td>'+ data[i].program +'</td><td>'+ data[i].program_structure +'</td><td  style="width: 100px;"> <a class="glyphicon glyphicon-info-sign" href=/studentportal/admin/viewAnnouncementDetails?id='+id+' title="Details"></a>&ensp;<a href=/studentportal/admin/editAnnouncementProgram?masterKey='+data[i].id+'&&announcementId='+id+' title="Edit"><i class="fa fa-pencil-square-o fa-lg"></i></a>&ensp;<span class="deletebtn" onclick="deleteProgram('+data[i].id+','+id+' )" title="Delete">  <i class="fa-solid fa-trash fa-lg"></i></span> </td></tr>';
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
		
	  window.location="/studentportal/admin/deleteAnnouncementProgram?masterKey="+masterKey+"&&announcementId="+announcementId;
	}
	   
	
}
</script>

</body>
</html>
