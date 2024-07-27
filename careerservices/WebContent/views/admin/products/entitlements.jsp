<!DOCTYPE html>
<html class="no-js">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="/views/adminCommon/jscss.jsp">
    <jsp:param value="${ title }" name="title" />
</jsp:include>


<body class="inside">

    <%@ include file="/views/adminCommon/header.jsp"%>
    <section class="content-container login">

        <div class="container-fluid customTheme">
            <div class="row" style="padding-top:15px">
                <legend>${ title }</legend>
            </div>
            <%@ include file="/views/common/messages.jsp"%>
            <form style="padding:15px" class="form px-3" onsubmit="return false;">
                <div class="row" style="padding-top:15px">
                    <div class="form-group">
                        <input type="hidden" class="form-control" id="packageFeaturesId" name="packageFeaturesId">
                    </div>

                    <div class="form-group">
                        <input type="hidden" class="form-control" id="entitlementId" name="entitlementId">
                    </div>

                    <div class="col-md-4 mb-3 pb-3">
                        <label for="Entitlement">Requires Other Entitlement</label>
                        <select class="form-control pl-2" id="requiresOtherEntitlement" name="requiresOtherEntitlement">
                            <option value="false">No</option>
                            <option value="true">Yes</option>
                        </select>
                    </div>

                    <div class="col-md-4 mb-3">
                        <label for="Activation">Requires Student Activation</label>
                        <select class="form-control pl-2" id="requiresStudentActivation"
                            name="requiresStudentActivation">
                            <option value="false">No</option>
                            <option value="true">Yes</option>
                        </select>
                    </div>
                    <div class="col-md-4 mb-3">
                        <label for="totalActivation">Total Activation</label>
                        <input type="number" class="form-control" id="totalActivations" name="totalActivations">
                    </div>
                    <div class="col-md-4 mb-3">
                        <label for="duration">Duration</label>
                        <input type="number" class="form-control" id="duration" name="duration">
                    </div>
				</div>
                <div class="row" style="padding-top:15px">
                    <div class="col-md-4 mb-3">
                        <label for="initialActivations">Initial Activation</label>
                        <input type="number" class="form-control" id="initialActivations" name="initialActivations">
                    </div>

                    <div class="col-md-4 mb-3">
                        <label for="initialCycleGap">Initial Cycle Gap Months</label>
                        <input type="number" class="form-control" id="initialCycleGapMonths" name="initialCycleGapMonths">
                    </div>
                    
                    <div class="col-md-4 mb-3">
                        <label for="initialCycleGap">Initial Cycle Gap Days</label>
                        <input type="number" class="form-control" id="initialCycleGapDays" name="initialCycleGapDays">
                    </div>
				</div>
                <div class="row" style="padding-top:15px">
                    <div class="col-md-4 mb-3">
                        <label for="activationsEveryCycle">Activations Every Cycle</label>
                        <input type="number" class="form-control" id="activationsEveryCycle"
                            name="activationsEveryCycle">
                    </div>
                    <div class="col-md-4 mb-3">
                        <label for="activationCycleMonths">Activation Cycle Months</label>
                        <input type="number" class="form-control" id="activationCycleMonths" name="activationCycleMonths">
                    </div>

                    <div class="col-md-4 mb-3">
                        <label for="activationCycleMonths">Activation Cycle Days</label>
                        <input type="number" class="form-control" id="activationCycleDays" name="activationCycleDays">
                    </div>
				</div>
                <div class="row" style="padding-top:15px">
                    <div class="col-md-4 mb-3">
                        <label for="extendIfActivationsLeft">Extend If Activations Left</label>
                        <select class="form-control pl-2" id="extendIfActivationsLeft" name="extendIfActivationsLeft">
                            <option value="false">No</option>
                            <option value="true">Yes</option>
                        </select>
                    </div>

                    <div class="col-md-4 mb-3">
                        <label for="extendByMaxMonths">Extend By Max Months</label>
                        <input type="number" class="form-control" id="extendByMaxMonths" name="extendByMaxMonths">
                    </div>

                    <div class="col-md-4 mb-3">
                        <label for="extendByMaxMonths">Extend By Max Days</label>
                        <input type="number" class="form-control" id="extendByMaxDays" name="extendByMaxDays">
                    </div>

				</div>
                <div class="row" style="padding-top:15px">

                    <div class="col-md-4 mb-3">
                        <label for="hasViewableData">Has Viewable Data</label>
                        <select class="form-control pl-2" id="hasViewableData" name="hasViewableData">
                            <option value="false">No</option>
                            <option value="true">Yes</option>
                        </select>
                    </div>

                    <div class="col-md-4 mb-3">
                        <label for="giveAccessAfterExpiry">Give Access After Expiry</label>
                        <select class="form-control pl-2" id="giveAccessAfterExpiry" name="giveAccessAfterExpiry">
                            <option value="false">No</option>
                            <option value="true">Yes</option>
                        </select>
                    </div>

                    <div class="col-md-4 mb-3">
                        <label for="giveAccessAfterActivationsConsumed">Give Access After Activations Consumed</label>
                        <select class="form-control pl-2" id="giveAccessAfterActivationsConsumed" name="giveAccessAfterActivationsConsumed">
                            <option value="false">No</option>
                            <option value="true">Yes</option>
                        </select>
                    </div>

                    <div class="clearfix"></div>
                    <br>
                    <div class="col-md-4">
                        <button class="btn btn-primary" type="submit">Submit</button>
                    </div>

                </div>
            </form>
        </div>

        <br><br>

 	<div class="container-fluid customTheme"> 	
 		<legend> ${ tableTitle }</legend>
        	<div class="clearfix"></div>
	 		<div style="overflow-x:scroll">
	 	
				<table id="table" class="table table-striped table-bordered display compact" style="width:100%">
	                <thead>
	                    <tr>
	                        <th>Sr.No</th>
	                        <th>Entitlement Id</th>
	                        <th>Package Name</th>
	                        <th>Feature Name</th>
	                        <th>Duration Type</th>
	                        <th>Requires Other Entitlement </th>
	                        <th>Requires Student Activation </th>
	                        <th>Total Activations </th>
	                        <th>Initial Activations</th>
	                        <th>Initial Cycle Gap Months</th>
	                        <th>Initial Cycle Gap Days</th>
	                        <th>Activation Cycle Months</th>
	                        <th>Activation Cycle Days</th>
	                        <th>Activations Every Cycle</th>
	                        <th>Extend If Activations Left </th>
	                        <th>Extend By Max Months</th>
	                        <th>Duration</th>
	                        <th>Has Viewable Data</th>
	                        <th>Give Access After Expiry</th>
	                        <th>Give Access After Activations Consumed</th>
	                        <th>Links</th>
	                    </tr>
	                </thead>
	                <tbody>
				        <% int i = 0; %>
					 	<c:forEach items="${AllEntitlements}" var="thisEntitlement">	
						 	<tr>
						 		<% i++; %>
						 		<td> <%= i %> </td>
		                        <td>${ thisEntitlement.entitlementId }</td>
		                        <td>${ thisEntitlement.packageName }</td>
		                        <td>${ thisEntitlement.featureName }</td>
		                        <td>${ thisEntitlement.durationType }</td>
		                        <td>${ thisEntitlement.requiresOtherEntitlement }</td>
		                        <td>${ thisEntitlement.requiresStudentActivation }</td>
		                        <td>${ thisEntitlement.totalActivations }</td>
		                        <td>${ thisEntitlement.initialActivations }</td>
		                        <td>${ thisEntitlement.initialCycleGapMonths }</td>
		                        <td>${ thisEntitlement.initialCycleGapDays }</td>
		                        <td>${ thisEntitlement.activationCycleMonths }</td>
		                        <td>${ thisEntitlement.activationCycleDays }</td>
		                        <td>${ thisEntitlement.activationsEveryCycle }</td>
		                        <td>${ thisEntitlement.extendIfActivationsLeft }</td>
		                        <td>${ thisEntitlement.extendByMaxMonths }</td>
		                        <td>${ thisEntitlement.duration }</td>
		                        <td>${ thisEntitlement.hasViewableData }</td>
		                        <td>${ thisEntitlement.giveAccessAfterExpiry }</td>
		                        <td>${ thisEntitlement.giveAccessAfterActivationsConsumed }</td>
		                        <td>
		                        	<a href="addEntitlementDependency?entitlementId=${ thisEntitlement.entitlementId }"> 
		                        		<i class="fa fa-plus"></i> Add/View Dependencies
		                        	</a>
		                        	<br>
		                        	<a href="updateEntitlementInitialInfo?entitlementId=${ thisEntitlement.entitlementId }"> 
		                        		<i class="fa fa-eye"></i> Update Initial Student Info
		                        	</a>
		                        	<br>
	                        	</td>
		                    </tr>
	                    </c:forEach>
	                </tbody>
	            </table>

       		</div>
        </div>
        <br><br><br><br>

    </section>
    <br><br><br><br>
    <jsp:include page="/views/adminCommon/footer.jsp" />

</body>

<script>
    $(document).ready(function () {
        $("#packageFeaturesId").val("${ Entitlement.packageFeaturesId}");
        $("#entitlementId").val("${ Entitlement.entitlementId}");
        $("#requiresOtherEntitlement").val("${ Entitlement.requiresOtherEntitlement}");
        $("#requiresStudentActivation").val("${ Entitlement.requiresStudentActivation}");
        $("#totalActivations").val("${ Entitlement.totalActivations}");
        $("#initialActivations").val("${ Entitlement.initialActivations}");
        $("#initialCycleGapMonths").val("${ Entitlement.initialCycleGapMonths}");
        $("#initialCycleGapDays").val("${ Entitlement.initialCycleGapDays}");
        $("#activationCycleMonths").val("${ Entitlement.activationCycleMonths}");
        $("#activationCycleDays").val("${ Entitlement.activationCycleDays}");
        $("#activationsEveryCycle").val("${ Entitlement.activationsEveryCycle}");
        $("#extendIfActivationsLeft").val("${ Entitlement.extendIfActivationsLeft}");
        $("#extendByMaxMonths").val("${ Entitlement.extendByMaxMonths}");
        $("#extendByMaxDays").val("${ Entitlement.extendByMaxDays}");
        $("#duration").val("${ Entitlement.duration}");
        $("#hasViewableData").val("${ Entitlement.hasViewableData}");
        $("#giveAccessAfterExpiry").val("${ Entitlement.giveAccessAfterExpiry}");
        $("#giveAccessAfterActivationsConsumed").val("${ Entitlement.giveAccessAfterActivationsConsumed}");

        $(document).ready(function() {
			<jsp:include page="/views/adminCommon/datatables.jsp" />
		});
        
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
                }
            });
        });
    });
</script>

</html>