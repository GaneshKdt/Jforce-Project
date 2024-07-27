<!DOCTYPE html>

<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="/views/adminCommon/jscss.jsp">
	<jsp:param value="Counselling Feedback" name="title" />
</jsp:include>	
<%
	String counsellingId = request.getParameter("counsellingId");
%>
<body class="inside">

<jsp:include page="/views/common/header.jsp" />
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row">
        	<legend>Counselling Feedback</legend>
        </div>
        <jsp:include page="/views/common/messages.jsp" />
		
		<form:form  action="counsellingFeedback" method="post" modelAttribute="counsellingFeedback">
			<div class="panel-body">
				<div class="row"> 
					<c:forEach var="parameter" items="${ feedbackParam }">
						<div class="col-md-4 column form-group">
							<form:label for="${parameter.key}" path="${parameter.key}">${parameter.value}</form:label> 
							<form:select id="${parameter.key}" path="${parameter.key}" class="form-control" required="required">
								<form:option value="" >Select Rating</form:option>
								<form:options items="${rating}"/>
							</form:select>
						</div>
					</c:forEach>
				</div>
				
				<form:input for="userId" path="userId" value="${userId}" hidden='true'/>
					
				<div class="row">
					<div class="col-lg-12 form-group">
						<form:label for="strength" path="strength">Areas of Strength</form:label>
						<form:textarea id="strength" path="strength" class="form-control" placeholder="Description" 
						cols="47" required="required"/>
					</div>
				</div>
				<div class="row">
					<div class="col-lg-12 form-group">
						<form:label for="improvements" path="improvements">Areas of Improvement</form:label> 
						<form:textarea id="improvements" path="improvements" class="form-control" placeholder="Description" 
						cols="47" required="required"/>
					</div>
				</div>
				<div class="row">
					<div class="col-lg-12 form-group1">
						<form:label for="cvtweaking" path="cvtweaking">Feedback on CV tweaking as per the role requirements and interviewing techniques</form:label> 
						<form:textarea id="cvtweaking" path="cvtweaking" class="form-control" placeholder="Description" 
						cols="47" required="required"/>
					</div>
				</div>
				<div class="row">
					<div class="col-lg-12 form-group">
						<form:label for="careerchoice" path="careerchoice">Alternate career choice and next interview to be prepared on those lines, if Yes, please specify the role/industry below</form:label> 
						<form:textarea id="careerchoice" path="careerchoice" class="form-control" placeholder="Description" 
						cols="47" required="required"/>
					</div>
				</div> 
				<div class="row" > 
					<div class="col-lg-6 column">
						<button id="submit" name="submit" class="btn btn-large btn-primary"
							formaction="counsellingFeedback?facultyId=${ userId }&counsellingId=${ param.counsellingId }" style="margin: 10px;">Submit</button>
					</div>
				</div>
			</div> 
		</form:form>
		
		</div>
	
	</section>

	  <jsp:include page="/views/adminCommon/footer.jsp" />
</body>  
</html>
