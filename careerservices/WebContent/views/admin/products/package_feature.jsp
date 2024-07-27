<html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="/views/adminCommon/jscss.jsp">
    <jsp:param value="${ title }" name="title" />
</jsp:include>


<body class="inside">

    <%@ include file="/views/adminCommon/header.jsp"%>

    <section class="content-container login">

        <div class="container-fluid customTheme">
            <div class="row">
                <legend>${ title }</legend>
            </div>
            <%@ include file="/views/common/messages.jsp"%>


            <form style="padding:15px" class="form px-3">

                <div class="form-group">
                    <input type="hidden" class="form-control" id="packageId" name="packageId">
                </div>

                <div class="form-group">
                    <label for="featureId">Feature </label>
                    <select class="form-control pl-2" id="featureId" name="featureId">
                        <c:forEach items="${FeaturesNotInPackage}" var="feature">
                            <option value="${ feature.featureId }">${ feature.featureName }</option>
                        </c:forEach>
                    </select>
                </div>
                <button class="btn btn-primary" type="submit">Submit form</button>
            </form>

        </div>

        <div class="clearfix"></div>
 	<div class="container-fluid customTheme"> 	
 		<legend> ${ tableTitle }</legend>
            <div class="col-md-18 column">
                <div class="container-fluid">
                    <table id="dataTable" class="table table-striped table-bordered" style="width:100%">
                        <thead>
                            <tr>
                                <th>Sr.No</th>
                                <th>Package Name</th>
                                <th>Feature Id</th>
                                <th>Duration Type</th>
                                <th>Entitlement</th>
                                <th>Links</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% int i = 0; %>
                            <c:forEach items="${AllPackageFeatures}" var="packageFeature">
                                <tr>
                                    <% i++; %>
                                    <td><%= i %></td>
                                    <td><a href="updatePackage?packageId=${ packageFeature.packageId }">${ packageFeature.packageName }</a></td>
                                    <td>${ packageFeature.featureName }</td>
                                    <td>${ packageFeature.durationType }</td>
                                    <td>
                                        <a href="updateEntitlement?packageFeatureId=${ packageFeature.uid }">
                                            <i class="fa fa-eye"></i> View Entitlement Info
                                        </a>
                                    </td>
                                    <td>
                                        <a
                                            href="deletePackageFeature?packageId=${ packageFeature.packageId }&featureId=${ packageFeature.featureId }">
                                            <i class="fa fa-trash"></i> Delete
                                        </a>
                                        <br>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    </section>

    <jsp:include page="/views/adminCommon/footer.jsp" />

    <script>
	    $(document).ready(function() {
			<jsp:include page="/views/adminCommon/datatables.jsp" />
		});
	    
        $(document).ready(function () {
            $("#packageId").val("${ packageId }");
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