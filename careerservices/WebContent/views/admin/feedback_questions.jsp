<!DOCTYPE html>
<html class="no-js">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="/views/adminCommon/jscss.jsp">
    <jsp:param value=" ${ title } " name="title" />
</jsp:include>


<body class="inside">

    <%@ include file="/views/adminCommon/header.jsp"%>
    <section class="content-container login">

        <div class="container-fluid customTheme">
            <div class="row">
                <legend>${ title }</legend>
            </div>
            <%@ include file="/views/common/messages.jsp"%>

        <form style="padding:15px" class="form panel-body margin-x" onsubmit="return false;">

            <div class="form-group">
                <input type="hidden" class="form-control" id="id" name="id">
            </div>
            
			
            <div class="col-md-4 mb-3">
                <label for="feedbackQuestionGroupId">Question Group</label>
                <select class="form-control pl-2" id="feedbackQuestionGroupId" name="feedbackQuestionGroupId">
                    <option value="1">General</option>
                    <option value="2">Session</option>
                    <option value="3">Speaker</option>
                    <option value="4">Topic</option>
                    <option value="5">Technical</option>
                </select>
            </div>
            
            
            <div class="col-md-4 mb-3">
                <label for="questionType">Question Type</label>
                <select class="form-control pl-2" id="questionType" name="questionType">
                    <option value="boolean">Yes/No</option>
                    <option value="text">Text</option>
                    <option value="number">Rating</option>
                </select>
            </div>

            <div class="col-md-4 mb-3">
                <label for="giveAdditionalComment">Give Additional Comment Field</label>
                <select class="form-control pl-2" id="giveAdditionalComment" name="giveAdditionalComment">
                    <option value="false">No</option>
                    <option value="true">Yes</option>
                </select>
            </div>

            <div class="col-md-4 mb-3">
                <label for="questionString">Question Text</label>
                <input type="text" class="form-control" id="questionString" name="questionString">
            </div>

            <div class="clearfix"></div>
            <br>
            <div class="col-md-4">
                <button class="btn btn-primary" type="submit">Submit</button>
            </div>
        </form> <br><br>

        <div class="clearfix"></div>
 	<div class="container-fluid customTheme"> 	
 		<legend> ${ tableTitle }</legend>
            <table id="dataTable" class="table table-striped table-bordered" style="width:100%">
                <thead>
                    <tr>
                        <th>Sr.No</th>
                        <th>Question Group</th>
                        <th>Question </th>
                        <th>Question Type </th>
                        <th>Give Additional Comments </th>
                        <th>Links</th>
                    </tr>
                </thead>
                <tbody>
                    <% int i = 0; %>
                    <c:forEach items="${AllQuestions}" var="question">
                        <% i++; %>
                        <tr>
                            <td><%= i %></td>
                            <td>${ question.feedbackQuestionGroupName }</td>
                            <td>${ question.questionString }</td>
                            <td>${ question.questionType }</td>
                            <td>${ question.giveAdditionalComment }</td>
                            <td>
		            			<a href="deleteFeedbackQuestion?questionId=${ question.feedbackQuestionId }">
		            				<i class="fa fa-delete"></i> Delete
	            				</a>
            				</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

        </div>

        </div>
    </section>
    <jsp:include page="/views/adminCommon/footer.jsp" />

    <script>
        $(document).ready(function () {
            $('#dataTable').DataTable();
        });

        $(document).ready(function () {
            $("#id").val("${ Dependency.id }");
            $("#entitlementId").val("${ Dependency.entitlementId }");
            $("#dependsOnFeatureId").val("${ Dependency.dependsOnFeatureId }");
            $("#requiresCompletion").val("${ Dependency.requiresCompletion }");
            $("#monthsAfterCompletion").val("${ Dependency.monthsAfterCompletion }");
            $("#monthsAfterActivation").val("${ Dependency.monthsAfterActivation }");
            $("#activationsMinimumRequired").val("${ Dependency.activationsMinimumRequired }");

            $('#dataTable').DataTable();
        });

        $(document).ready(function () {

            $('form').submit(function (e) {
                e.preventDefault();
                var ddata = formDataToJSON();


                $.ajax({
                    url: '${url}',
                    type: 'post',
                    dataType: 'json',
                    contentType: 'application/json',
                    data: ddata,
                    success: function (result) {
                        if (result.status == "success") {
                            successMessage("Success");
                            window.location.reload();
                        } else {
                            errorMessage(result.message);
                        }
                        return false;
                    },
                    error: function (xhr, resp, text) {
                        console.log(xhr, resp, text);
                        errorMessage("Error. Please check all fields and retry!");
                    }
                });
            });
        });
    </script>
</body>

</html>