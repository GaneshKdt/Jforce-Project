$(document).ready (function(){
	isGrader(document.getElementById("title").value);
}); 

/* Description */
$('#editor').editable({
	inlineMode : false,
	buttons : [ 'bold', 'italic', 'underline', 'sep', 'strikeThrough', 'subscript', 'superscript', 'sep', 'fontFamily', 'fontSize', 'color', 'formatBlock',
		'blockStyle', 'inlineStyle', 'sep', 'align', 'insertOrderedList', 'insertUnorderedList', 'outdent', 'indent', 'selectAll', 'sep',
		'createLink', 'table', 'sep', 'undo', 'redo', 'sep', 'insertHorizontalRule', 'removeFormat', 'fullscreen' ],
	minHeight : 200,
	paragraphy : false,
	placeholder : 'Enter Faculty Description here...',
	theme : 'blue',
	key : 'vA-16ddvvzalxvB-13C2uF-10A-8mG-7eC5lnmhuD3mmD-16==',
	toolbarFixed : false
});

/* Form Submit */
$("#submit").click(function() {
	if ($("#approvedInSlab").val() == "C (3000)") {
		$("#dateOfECMeetingApprovalTaken").val("");
	}
	
	//Validating a mobile number
	var number = $("#mobile").val();
	if(!(/^[0-9]*$/.test(number))){
		alert("Please enter Valid Mobile number");
		return false;
	}
			
	if((($("#title").val() != 'Grader'))){
	//please fill out Description
		if($("#editor").val() == ''){
			alert("Please fill the description");
			return false;
		}
	}
			
	if ($("#isConsentForm").val() == "N") {
		$("#facultyConsentFormData").val("");
	}
	
	var cclength = $('#countryCode').val();
	if(cclength.length > 6){
		alert("Please enter valid Country Code");
		return false;
	}

	var ccmobilelength = $('#mobile').val();
	if(ccmobilelength.length > 13){
		alert("Please enter valid Mobile Number");
		return false;
	}

	var firstname = $('#firstName').val();
	var lastname = $('#lastName').val();
	if(!(/^[a-zA-Z ]*$/.test(firstname))){
		alert("Please enter valid Firstname");
		return false;
	}

	if(!(/^[a-zA-Z ]*$/.test(lastname))){
		alert("Please enter valid Lastname");
		return false;
	}
	
	if(!(/^[A-Za-z0-9]*$/.test($('#facultyId').val())) && $('#facultyId').val() != ''){
		alert("Please enter valid Faculty Id");
		return false;
	}
	
	if ($("#facultyImageFileData")[0].title == '' && (($("#title").val() != 'Grader'))) {
		document.getElementById("facultyImageFileData").required = true;
	}
	
	if(!(/^\d{4}-\d{2}-\d{2}$/.test($('#ecApprovalDate').val())) && $('#ecApprovalDate').val() != ''){
		alert("Please enter valid EC Aprooval Date");
		return false;
	}
	
	if(!(/^\d{4}-\d{2}-\d{2}$/.test($('#dateOfECMeetingApprovalTaken').val())) && $('#dateOfECMeetingApprovalTaken').val() != ''){
		alert("Please enter valid Slab Date");
		return false;
	}

	if($('#ecApprovalDate').val() != '' && $('#ecApprovalProof').val() == ''){
		if($('#ecPath')[0].value != ''){
			return true;
		}else{
			alert("Please add EC Approval Proof");
			return false;
		} 
	}

	if($('#ecApprovalDate').val() == '' && $('#ecApprovalProof').val() != ''){
		alert("Please add EC Approval Date");
		return false;
	}

});

/* Select Group */
var selectedProgGroups = "${selectedProgGroups}";
if (selectedProgGroups.length != 0) {
	var types = selectedProgGroups.split(",");
	for (var i = 0; i < types.length; i++) {
		$('#programGroup option[value="' + types[i] + '"]').prop("selected", true);
	}
}
		
var selectedProgNames = "${selectedProgNames}";
if (selectedProgNames.length != 0) {
	var types = selectedProgNames.split(",");
	for (var i = 0; i < types.length; i++) {
		$('#programName option[value="' + types[i] + '"]').prop("selected", true);
	}
}
	
$("#programGroup").change(function() {
	$("#programName option").remove();
	if ($(this).val() != "") {
		$.ajax({
			url : '/acads/admin/getProgramNames/'+ $(this).val(),
			type : 'GET',
			success : function(data) {
				var formOptions = "<option value=''>Select Program Name</option>";
				for (var i = 0; i < data.length; i++) {
					formOptions += "<option value='"+data[i]+"'>" + data[i] + "</option>"
				}
				$("#programName").append(formOptions);
			},
			error : function(error) {
				alert(error.responseText);
			}
		});
	}
});

/* Select Change */
$("select").each(function(){
	if ($(this).attr("id") == "approvedInSlab" && ($(this).val() == "A (7500)" || $(this).val() == "B (5000)")) {
		$("." + $(this).attr("id")).attr("style","display:block;");
		$("."+ $(this).attr("id")+ " input").attr("required","required");
	} 
	else if ($(this).attr("id") == "isConsentForm"
		&& ($(this).val() == "Y")) {
		$("." + $(this).attr("id")).attr("style","display:block;");
		$("."+ $(this).attr("id")+ " input").attr("required","required");
	}
});


$("select").change(function() {
	if ($(this).attr("id") == "approvedInSlab") {
		if ($(this).val() == "A (7500)") {
			$("." + $(this).attr("id")).attr("style", "display:block;");
			$("." + $(this).attr("id") + " input").attr("required", "required");
		} else if ($(this).val() == "B (5000)" && $(this).attr("id") == "approvedInSlab") {
			$("." + $(this).attr("id")).attr("style", "display:block;");
			$("." + $(this).attr("id") + " input").attr("required", "required");
		} else {
			$("." + $(this).attr("id")).attr("style", "display:none;");
			$("." + $(this).attr("id") + " input").removeAttr("required");
		}
	}
	else if ($(this).attr("id") == "isConsentForm") {
		if ($(this).val() == "Y") {
			$("." + $(this).attr("id")).attr("style", "display:block;");
			$("." + $(this).attr("id") + " input").attr("required", "required");
		} else {
			$("." + $(this).attr("id")).attr("style", "display:none;");
			$("." + $(this).attr("id") + " input").removeAttr("required");
		}
	}
});

/* Other */
function Other(val){
	var element = document.getElementById("other");
	if(val == "Any Other"){
		alert("Type your desired area of specialisation.");
		element.style.display='block';	
	}else {
		element.style.display='none'; 
		document.getElementById("otherAreaOfSpecialisation").value = '';
	} 
}

/* Grader */
function isGrader(value)
{ 
	if(value == 'Grader'){
		document.getElementById("linkedInProfileUrl").required = false;
		document.getElementById("programName").required = false;
		document.getElementById("programGroup").required = false;
		document.getElementById("facultyImageFileData").required = false;
	}		
	else{
		document.getElementById("linkedInProfileUrl").required = true;		
		document.getElementById("programName").required = true;
		document.getElementById("programGroup").required = true;

	}
}