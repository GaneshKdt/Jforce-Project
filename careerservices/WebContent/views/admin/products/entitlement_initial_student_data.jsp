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
                <legend> ${ title } </legend>
            </div>
            <%@ include file="/views/common/messages.jsp"%>
        </div>

        <form style="padding:15px" class="form px-3" onsubmit="return false;">

            <div class="form-group">
                <input type="hidden" class="form-control" id="entitlementId" name="entitlementId">
            </div>

            <div class="col-md-5 column">

                <div class="form-group">
                    <label for="activated">Activated</label>
                    <select class="form-control pl-2" id="activated" name="activated">
                        <option value="false">No</option>
                        <option value="true">Yes</option>
                    </select>
                </div>

            </div>
            <div class="col-md-5 column">

                <div class="form-group">
                    <label for="activatedByStudent">Activated By Student</label>
                    <select class="form-control pl-2" id="activatedByStudent" name="activatedByStudent">
                        <option value="false">No</option>
                        <option value="true">Yes</option>
                    </select>
                </div>
            </div>
            <div class="col-md-5 column">

                <div class="form-group">
                    <label for="activationsLeft">Activations Left</label>
                    <input type="number" class="form-control" id="activationsLeft" name="activationsLeft">
                </div>
            </div>
            <div class="clearfix"></div>
            <br>
            <button class="btn btn-primary" type="submit">Submit form</button>
        </form>
        <div class="clearfix"></div>
 	<div class="container-fluid customTheme"> 	
 		<legend> ${ tableTitle }</legend>

            <table id="dataTable" class="table table-striped table-bordered" style="width:100%">
                <thead>
                    <tr>
                        <th>Sr.No</th>
                        <th>Package Name </th>
                        <th>Feature Name </th>
                        <th>Duration Type </th>
                        <th>Activated</th>
                        <th>Activated By Student</th>
                        <th>Activations Left</th>
                        <th>Links</th>
                    </tr>
                </thead>
                <tbody>
					<% int i = 0; %>
					<c:forEach items="${AllInitialStudentData}" var="initialStudentData">	
						<tr>
							<% i++; %>
							<td> <%= i %> </td>
	                        <td><a href="updatePackage?packageId=${ initialStudentData.packageId }">${ initialStudentData.packageName }</a></td>
	                        <td><a href="updatePackageFeature?packageId=${ initialStudentData.packageId }">${ initialStudentData.featureName }</a></td>
	                        <td>${ initialStudentData.durationType }</td>
	                        <td>${ initialStudentData.activated }</td>
	                        <td>${ initialStudentData.activatedByStudent }</td>
	                        <td>${ initialStudentData.activationsLeft }</td>
	                        <td></td>
	                    </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </section>

    <jsp:include page="/views/adminCommon/footer.jsp" />

</body>
<script>
	$(document).ready(function() {
		<jsp:include page="/views/adminCommon/datatables.jsp" />
	});

    $(document).ready(function () {
        $("#entitlementId").val("${ InitialStudentData.entitlementId }");

        $("#activationsLeft").val("${ InitialStudentData.activationsLeft }");
        $("#activatedByStudent").val("${ InitialStudentData.activatedByStudent }");
        $("#activated").val("${ InitialStudentData.activated }");

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

</html>