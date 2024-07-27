<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.helpers.Person"%>
<html class="no-js"> <!--<![endif]-->
	
	<jsp:include page="jscss.jsp">
		<jsp:param value="Confirm Information for Marksheet" name="title" />
	</jsp:include>
	
	<script type="text/javascript">
	
	function validateForm(){
		var fatherName = document.getElementById("fatherName").value;
		var motherName = document.getElementById("motherName").value;
		
		if(fatherName.match(/\s/g) || motherName.match(/\s/g)){
            alert("Please enter only First Name for Parents, Full name not allowed.");
            return false;
        }
		
		return true;
		
	}
	
	</script>
	
	<body class="inside">
	
   <%@ include file="limitedAccessHeader.jsp"%>
    
    <%
    Person p = (Person)session.getAttribute("user_studentportal");

	String email = "";
	String mobile = "";
	String altMobile = "";
	String postalAddress = "";
    
    if(p != null){
    	email = p.getEmail();
    	mobile = p.getContactNo();
    	postalAddress = p.getPostalAddress();
    	altMobile = p.getAltContactNo();
    }
    %>
    
    <section class="content-container login">
        <div class="container-fluid customTheme">
          <div class="row">
             <legend>Confirm Information for Marksheet</legend>
          </div> <!-- /row -->
           
          <%@ include file="messages.jsp" %>
		  
		  <div class="panel-body">          
            <div class="col-xs-18">
				<div class="panel-body">
                <h4>Verify Information</h4>
                <p>Please verify/update information below. Father and Mother name will be displayed on Marksheet and Certificate</p>
               
              </div>
              
              <div class="panel-body">
              
                <form id="updateFirstTimeForm" action="updateParentName" method="post" onSubmit="return validateForm();">
                <fieldset>
                <div class="row">          
	              <div class="col-sm-9">
                   
                    
                     <div class="form-group">
                      <label for="mobile">Father Name(* - Please enter only First Name)</label>
                      <input type="text" required="required" class="form-control" id="fatherName" name="fatherName" placeholder="Enter Father Name" value="${student.fatherName}">
                    </div>
                    
                    <div class="form-group">
                      <label for="mobile">Mother Name(* - Please enter only First Name)</label>
                      <input type="text" required="required" class="form-control" id="motherName" name="motherName" placeholder="Enter Mother Name" value="${student.motherName}">
                    </div>

                    
                     <div class="form-group">
                      <button type="submit" class="btn btn-primary">Update Information</button>
                    </div>
                    
                    
                  </div>
	              
                </div>  
                </fieldset>
                </form>
                
              </div> <!--/module-box-->
            </div> <!-- /col-xs-6 -->
          </div> <!-- /row -->
          
        </div> <!-- /container -->
    </section>
    
    <jsp:include page="footer.jsp" />
    
    
    
  </body>
</html>
 --%>

<!DOCTYPE html>
<%@page import="com.nmims.helpers.PersonStudentPortalBean"%>

<%
	PersonStudentPortalBean p = (PersonStudentPortalBean)session.getAttribute("user_studentportal");

	String email = "";
	String mobile = "";
	String altMobile = "";
	String postalAddress = "";
    
    if(p != null){
    	email = p.getEmail();
    	mobile = p.getContactNo();
    	postalAddress = p.getPostalAddress();
    	altMobile = p.getAltContactNo();
    }
    %>

<html lang="en">




<jsp:include page="common/jscss.jsp">
	<jsp:param value="Confirm Information for Marksheet" name="title" />
</jsp:include>

<style>
.complete-profile-warpper .sz-content-wrapper.withBgImage .student-info-bar .student-image
	{
	border: 2px solid #000;
}

.complete-profile-warpper .sz-content-wrapper.withBgImage .student-info-bar ul.student-info-list li
	{
	color: #333;
}
</style>

<script type="text/javascript">
	
	function validateForm(){
		var fatherName = document.getElementById("fatherName").value;
		var motherName = document.getElementById("motherName").value;
		fatherName = fatherName.trim();
		motherName = motherName.trim();
		if(fatherName.match(/\s/g) || motherName.match(/\s/g)){
            alert("Please enter only First Name for Parents, Full name not allowed.");
            return false;
        }
		
		return true;
		
	}
	
	</script>

<body>

	<%@ include file="common/header.jsp"%>

	<div class="sz-main-content-wrapper complete-profile-warpper">

		<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Confirm Information"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="container">

				<div class="sz-content-wrapper dashBoard withBgImage">
					<%@ include file="common/studentInfoBar.jsp"%>


					<h2 class="red text-capitalize" style="margin-top: -20px;">Confirm
						Information for Marksheet</h2>
					<div class="clearfix"></div>
					<div class="panel-content-wrapper">
						<%@ include file="common/messages.jsp"%>

						<p>Please verify/update information below. Father and Mother
							name will be displayed on Marksheet and Certificate</p>
						<form id="updateFirstTimeForm" action="updateParentName"
							method="post" onSubmit="return validateForm();">
							<fieldset>
								<div class="row">
									<div class="col-md-6">


										<div class="form-group">
											<label for="mobile">Father Name(* - Please enter only
												First Name)</label> <input type="text" required="required"
												class="form-control" id="fatherName" name="fatherName"
												placeholder="Enter Father Name"
												value="${student.fatherName}">
										</div>

										<div class="form-group">
											<label for="mobile">Mother Name(* - Please enter only
												First Name)</label> <input type="text" required="required"
												class="form-control" id="motherName" name="motherName"
												placeholder="Enter Mother Name"
												value="${student.motherName}">
										</div>


										<div class="form-group">
											<button type="submit" class="btn btn-primary">Update
												Information</button>
										</div>


									</div>

								</div>
							</fieldset>
						</form>
					</div>

				</div>


			</div>
		</div>
	</div>


	<jsp:include page="common/footer.jsp" />


</body>
</html>