	function redirectToExam(element) {
		var subject = encodeURIComponent(element.getAttribute("data-subject"));
		var month = element.getAttribute("data-month");
		var year = element.getAttribute("data-year");

		var todaysExam = false;
		var examDate = new Date(element.getAttribute("data-exam-date") + " 00:00:00 GMT+0530 (India Standard Time)");
		var currentTime = new Date(new Date().toLocaleString("en-US", {timeZone: "Asia/Kolkata"}));

		if(examDate.setHours(0,0,0,0) == currentTime.setHours(0,0,0,0)) {
			todaysExam = true;
		}
		window.open('/exam/student/viewAssessmentDetails?subject=' + subject + '&month=' + month + '&year=' + year + '&showJoinLink=true');
	}

	$("#redirectToExam").click(function(){
		redirectToExam(this);
	});