
function getExamData(event){
	event.preventDefault();
	loadDashboardData();
}	

function loadDashboardData(){
	var programType = document.getElementById("programType").value;
	var examType = document.getElementById("examType").value;
	var examDate = document.getElementById("examDate").value;
	var examTime = document.getElementById("examTime").value;
	var sapid = document.getElementById("sapid").value;
	document.getElementById("searchBody").style.display="none";
	if(programType == "" || examType == "" || examDate == "" || examTime=="")
	{
		alert("Please Provide All Input")
		return false;
	}
	hideSubmitBtn();
	var tableData=$('#tableId').DataTable();
	$.ajax({
		url:"/exam/m/getMbaWxExamData?programType=" + encodeURIComponent(programType) + "&examType=" + examType + "&examTime=" + examTime + "&sapid=" + sapid,
		method:'GET',
		contentType:'application/json',
		success:function(response)
		{
			showSubmitBtn();
			document.getElementById("programType").value=programType;
			document.getElementById("examType").value=examType;
			document.getElementById("examTime").value=examTime;
			let html="";
			let srNo=0;
			document.getElementById("totalCount").innerHTML=response.length;
			console.log(response.length)
			for(let i=0;i<response.length;i++)
			{
				srNo++;
			//subject, timebound_id, sapid, firstname, lastname, emailId, examStartDateTime, schedule_id,  acessKey, joinURL, max_score, portalStatus, mettlStatus
				html += '<tr class="rowClass" data-uid="'+response[i].sapid+'~'+response[i].emailId+'~'+response[i].acessKey+'">';
				html += '<td >'+srNo+'</td>';
				html += '<td class="userId" >'+response[i].sapid+'</td>';
				html += '<td class="studentName" >'+response[i].firstname+' '+response[i].lastname+'</td>';
				html += '<td class="subject">'+response[i].subject+'</td>';	
				html += '<td class="examTime" id="showExamTime-'+srNo+'">'+response[i].examStartDateTime+'</td>';
				html += '<td class="registeredEmailId" id="showRegisteredEmailId-'+srNo+'">'+(response[i].emailId ? response[i].emailId : '')+'</td>';
				html += '<td class="portalStatus" id="portalStatus-'+srNo+'">'+(response[i].portalStatus ? response[i].portalStatus : '-')+'</td>';
				html +='<td class="joinLink" id="showJoinLink-'+srNo+'">'
				+ ( response[i].emailId ?  '<div class="btn-group btn-group-sm">'
						+ '<button type="button"  class="button-copy-link btn btn-sm btn-default" style="float: none;"  onclick="copyJoinLink('+response[i].sapid+','+response[i].timebound_id+','+response[i].schedule_id+')" >'
						+ 'Copy Join Link'
						+ '</button>'
						+ '</div>' : '-') 
						+'</td>';
				html += '<td class="send-email" id="send-email-'+srNo+'">'
				+ '<div class="btn-group btn-group-sm">'
				+ '<button type="button" '
				+ ' data-subject="' + response[i].subject + '" '
				+ ' data-sapid="' + response[i].sapid + '" '
				+ ' data-examStartDateTime="' + response[i].examStartDateTime + '" '
				+ ' data-schedule_id="' + response[i].schedule_id + '" '
				+ ' data-timebound_id="' + response[i].timebound_id + '" '
				+ ' data-emailId="' + response[i].emailId + '" '
				+ ' class="button-send-email btn btn-sm btn-default" style="float: none;" onclick="sendEmailToStudentsMbaWx(event)">'
				+ 'Send Email'
				+ '</button>'
				+ '</div>'
				+'</td>';


				html += '<td >'
					+ '<div class="btn-group btn-group-sm">'
					+ '<button type="button"  class="btn btn-sm btn-default" style="float: none;" id="btnDemoExam" onclick="getDemoExamLogBySapid('+response[i].sapid+')">'
					+ 'Demo Exam Log'
					+ '</button>'
					+ '</div>' 
					+ '</td>';
				html += '<td >'
					+ '<div class="btn-group btn-group-sm">'
					+ '<button type="button"  class="btn btn-sm btn-default" style="float: none;" id="btnDemoExam" onclick="getTestTakenFlagBySapidAndAccessKey('+response[i].sapid+','+response[i].schedule_id+')">'
					+ 'Show Mettl Status'
					+ '</button>'
					+ '</div>' 
					+ '</td>';
				html += '</tr>';
			}
			tableData.destroy();
			document.getElementById("searchBody").style.display="block";
			document.getElementById("testTable").innerHTML=html;	
			tableData=$('#tableId').DataTable();
			
		},
		error:function(error)
		{
			showSubmitBtn();
			console.log(error)
			document.getElementById("searchBody").style.display="none";
			alert("Unable to get data for program:!"+programType);
		}
	})
}

function sendEmailToStudentsMbaWx(e) {
	let element = e.target;
	var subject = element.getAttribute('data-subject');
	var sapid = element.getAttribute('data-sapid');
	var examStartDateTime = element.getAttribute('data-examStartDateTime');
	var schedule_id = element.getAttribute('data-schedule_id');
	var timebound_id = element.getAttribute('data-timebound_id');
	var emailId = element.getAttribute('data-emailId');


	let data = {
			"subject" : subject,
			"emailId" : emailId,
			"schedule_id" : schedule_id,
			"timebound_id" : timebound_id,
			"examStartDateTime" : examStartDateTime,
			"sapid" : sapid
	};

	$.ajax({
		type : "POST",
		url : '/exam/m/sendEmailLinkMbaWxDashboard',
		contentType : "application/json",
		data : JSON.stringify(data),
		dataType : "JSON",
		success : function(data) {
			if(data.code == 422) {
				alert(data.message)
			} else {
				alert("Mail Sent Successfully!")
			}

		},
		error : function(error) {
			alert("Error sending email : " + error)
		},
		complete: function(){
			showSubmitBtn();
		}
	})
}

function examTimeListOnExamDate(e){
	let examDate = e.target.value;
	
	var examType = document.getElementById("examType").value;
	let optionList = '<option disabled selected value="">-- select exam time --</option>';

	if(examType=="40"){
		optionList = optionList + '<option value="'+examDate+' 11:00:00">'+examDate+' 11:00:00</option>'
		document.getElementById("examTime").innerHTML=optionList;
	}else{
		optionList = '<option disabled selected value="">Loading...</option>';	
		document.getElementById("examTime").innerHTML=optionList;
		$.ajax({
			url:"/exam/m/getExamTimeByExamDate?examDate=" + examDate,
			method:"GET",
			success:function(response){
				console.log(response)
				optionList = '<option disabled selected value="">-- select exam time --</option>';
				for(let i=0;i<=response.length-1;i++)
				{
					optionList= optionList + '<option value="'+response[i]+'">'+response[i]+'</option>';
				}
				document.getElementById("examTime").innerHTML=optionList;
			},
			error:function(error){
				alert("Error while getting exam date");
			}
		})
	}	
}

function copyJoinLink(sapId,timeboundId,scheduleId){
	
	hideSubmitBtn();
	let body = {
			"sapid" :  sapId,
			"timebound_id" : timeboundId,
			"schedule_id" : scheduleId,
	};
	
	$.ajax({
		type : "POST",
		url : '/exam/m/getJoinLinksMbaWx',
		contentType : "application/json",
		data : JSON.stringify(body),
		dataType : "JSON",
		success : function(data){
			if(data.code == 422){
				alert(data.message);
			}else{
				navigator.clipboard.writeText(data.tcsOnlineExamBean.joinUrl).then(function() {
					alert('Copied!');
				}, function(err) {
					alert('Error Copying : '+ err);
				});
			}
		},error: function (result, status, err) {
			alert("Sorry, there was a Error in processing API!");

		},complete: function(){
			showSubmitBtn();
		}	
	});
	
}

function getDemoExamLogBySapid(sapid){
	
	
	hideSubmitBtn();
    
     $.ajax({
      type : "GET",
	  url : '/exam/m/getDemoExamLogsMbaWx?sapid='+sapid,
      success : function(data){
    	  if(data.code == 422){
	         	alert(data.message);
	        }else{
		    	  	let htmlDemoExam ='';
					let len = data.demoExamAttendanceList.length;
					let demoExamAttendanceList = data.demoExamAttendanceList;
					let srNo = 0 ;
					
					for(let i =0 ; i < len; i++){
						srNo++;
		
						htmlDemoExam += '<tr >';
						
						htmlDemoExam += '<td >'+srNo+'</td>';
						htmlDemoExam += '<td >'+demoExamAttendanceList[i].sapid+'</td>';
						htmlDemoExam += '<td >'+(demoExamAttendanceList[i].demoExamId ? demoExamAttendanceList[i].demoExamId : '-')+'</td>';
						htmlDemoExam += '<td >'+(demoExamAttendanceList[i].accessKey ? demoExamAttendanceList[i].accessKey : '-')+'</td>';
						htmlDemoExam += '<td >'+(demoExamAttendanceList[i].startedTime ? demoExamAttendanceList[i].startedTime : '-')+'</td>';
						htmlDemoExam += '<td >'+(demoExamAttendanceList[i].endTime ? demoExamAttendanceList[i].endTime : '-')+'</td>';	
						htmlDemoExam += '<td >'+(demoExamAttendanceList[i].markAttend ? demoExamAttendanceList[i].markAttend : '-')+'</td>';
						
						htmlDemoExam += '</tr>';
					}
					$('#demoExamLogModalBody').html(htmlDemoExam);
					$('#demoExamCount').text(len);
					$('#demoExamLogModal').modal('show');
					 
	        }
		     		

      },error: function (result, status, err) {
	          alert("Sorry, there was a Error in processing API!");
	          
	  },complete: function(){
		  showSubmitBtn();
	      }
    	
    });

}

function getTestTakenFlagBySapidAndAccessKey(sapId,scheduleId){
	
	
	hideSubmitBtn();

	let body = {
			"sapid" :  sapId,
			"schedule_id" : scheduleId
			};
	
	$.ajax({
		type : "POST",
		url : '/exam/m/getExamStatusMbaWx',
		contentType : "application/json",
		data : JSON.stringify(body),
		dataType : "JSON",
		success : function(data){
			if(data.code == 422){
	         	alert(data.message);
	        }else{
				let htmlTestTaken ='';
				let len = data.mettlRegisterCandidateBeanMBAWX.length;
				let mettlRegisterCandidateBeanMBAWX = data.mettlRegisterCandidateBeanMBAWX;
				let srNo = 0 ;
				
				for(let i =0 ; i < len; i++){
					srNo++;
					gradedJson = mettlRegisterCandidateBeanMBAWX[i].gradedJsonResponse;	
					htmlTestTaken += '<tr >';
				
					htmlTestTaken += '<td >'+mettlRegisterCandidateBeanMBAWX[i].sapid+'</td>';
					htmlTestTaken += '<td >'+mettlRegisterCandidateBeanMBAWX[i].firstname+' '+mettlRegisterCandidateBeanMBAWX[i].lastname+'</td>';
					htmlTestTaken += '<td >'+(mettlRegisterCandidateBeanMBAWX[i].testTaken ? mettlRegisterCandidateBeanMBAWX[i].testTaken : '-')+'</td>';
					
					htmlTestTaken += '<td >'+(mettlRegisterCandidateBeanMBAWX[i].startJsonResponse ? "<button class='btn btn-sm btn-primary' onclick='beautifyJson("+mettlRegisterCandidateBeanMBAWX[i].startJsonResponse+");'> View Start Details  </button> " : "-")+"</td>";
					htmlTestTaken += '<td >'+(mettlRegisterCandidateBeanMBAWX[i].finishedJsonResponse ? "<button class='btn btn-sm btn-primary' onclick='beautifyJson("+mettlRegisterCandidateBeanMBAWX[i].finishedJsonResponse+");'> View Finished Details </button> " : "-")+"</td>";
					htmlTestTaken += '<td >'+(mettlRegisterCandidateBeanMBAWX[i].finish_mode ? mettlRegisterCandidateBeanMBAWX[i].finish_mode : '-')+'</td>';
					htmlTestTaken += '<td >'+(mettlRegisterCandidateBeanMBAWX[i].gradedJsonResponse ? '<button class="btn btn-sm btn-primary" onclick="beautifyGradedJson(gradedJson);"> View Graded Details </button> ' : '-')+'</td>';
					htmlTestTaken += '<td >'+(mettlRegisterCandidateBeanMBAWX[i].resumeJsonResponse ? "<button class='btn btn-sm btn-primary' onclick='beautifyJson("+mettlRegisterCandidateBeanMBAWX[i].resumeJsonResponse+");'> View Resume Details </button> " : "-")+"</td>";
					
					htmlTestTaken += '</tr>';
				}
				$('#testTakenModalBody').html(htmlTestTaken);
				$('#testTakenModal').modal('show');
				
	        }
			
			
		},error: function (result, status, err) {
			alert("Sorry, there was a Error in processing API!");
			
		},complete: function(){
			showSubmitBtn();
		}
		
	});
	
}

function gmtToist(val){
	let dateIST = new Date(val); 
    dateIST = dateIST.toLocaleString('en-IN', {dateStyle : 'full', timeStyle : 'long', hour12: true });
    return dateIST;
}

function beautifyJson(obj){
	let jsonObj = JSON.parse(JSON.stringify(obj));
	console.log(jsonObj)
	let dateIST = gmtToist(jsonObj.timestamp_GMT);
	jsonObj.timestamp_IST = dateIST;
	delete jsonObj['timestamp_GMT'];
	obj = jsonObj;
	document.getElementById("displayStudentJson").innerHTML = JSON.stringify(obj, undefined, 4);
	$('#studentJsonModal').modal('show');
}

function beautifyGradedJson(obj){
	let jsonObj = JSON.parse(obj);
	document.getElementById("displayStudentJson").innerHTML = JSON.stringify(jsonObj, undefined, 1);
	$('#studentJsonModal').modal('show');
}

function showSubmitBtn() {
	document.getElementById("theImg").style.display="none";
	document.getElementById("submitBtn").style.display="block";
}

function hideSubmitBtn() {
	document.getElementById("submitBtn").style.display="none";
	document.getElementById("theImg").style.display="block";
}
