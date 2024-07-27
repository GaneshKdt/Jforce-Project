/**
 * 
 */


let uniqueId =[];
let jsonObj = [];
let singleJsonObj = [];
let gradedJson ;

 function callExamCenter() {
	let examYear  = document.getElementById("year").value;		
	let examMonth = document.getElementById("month").value; 
	$('#ExamDateDD').load(" #ExamDateDD > *");
	$('#examTimeDD').load(" #examTimeDD > *");
	    if (examYear == "" || examMonth == "" )
	   {
	    return false;
	   }
	    getExamCenterDD(examYear, examMonth);
} 


$('#centerDD').on('change', function() {
		
	let cId = document.getElementById("centerDD").value; 
	if (cId == "" )
	{
		return false;
	}
	getExamDateDD(cId);
});

$('#ExamDateDD').on('change', function() {
	let cId = document.getElementById("centerDD").value; 
	let examDate = document.getElementById("ExamDateDD").value; 
	if (cId == "" || examDate == "")
	{
		return false;
	}
	getExamTimeDD();
});


$('#fileId').on('change', function() {
	
	var validExts = new Array(".xlsx", ".xls");
    var fileExt = this.value;
    fileExt = fileExt.substring(fileExt.lastIndexOf('.'));
    if (validExts.indexOf(fileExt) < 0) {
    	
      alert("Invalid file selected, valid files are of " +
               validExts.toString() + " types.");
      $(this).val('');
      return false;
    }
	
		hideSearchBtn();
		
	 	$('#downloadBtn').css("display", "none"); 	
	 	$('#fileUpdload').css("display", "none");
	 	
	 	let form = $('#fileUploadForm')[0];
	    
	    let formData = new FormData(form);
	    
	    // Ajax call for file uploaling
	     $.ajax({
	      url : 'excelBulkPreview',
	      type : 'POST',
	      enctype: 'multipart/form-data',
	      data : formData,
	      cache : false,
	      contentType : false,
	      processData : false,
	      success : function(data){
	    	  	let html='';
				let len = data.tcsOnlineExamBeanList.length;
				let tcsOnlineExamBeanList = data.tcsOnlineExamBeanList;
				let srNo = 0 ;
				$('#total').text(len);
				for(let i =0 ; i < len; i++){
					srNo++;
	
					html += '<tr >';
					html += '<td >'+srNo+'</td>';
					html += '<td >'+tcsOnlineExamBeanList[i].uniqueRequestId+'</td>';
					html += '<td >'+tcsOnlineExamBeanList[i].userId+'</td>';
					html += '<td >'+tcsOnlineExamBeanList[i].firstName+' '+tcsOnlineExamBeanList[i].lastName+'</td>';
					html += '<td >'+tcsOnlineExamBeanList[i].examYear+'</td>';
					html += '<td >'+tcsOnlineExamBeanList[i].examMonth+'</td>';
					html += '<td >'+tcsOnlineExamBeanList[i].program+'</td>';
					html += '<td >'+tcsOnlineExamBeanList[i].subject+'</td>';	
					html += '<td >'+tcsOnlineExamBeanList[i].examCenterName+'</td>';
					html += '<td >'+tcsOnlineExamBeanList[i].examDate+'</td>';
					html += '<td >'+tcsOnlineExamBeanList[i].examTime+'</td>';
					html += '<td >'+tcsOnlineExamBeanList[i].bulkAction+'</td>';
					html += '</tr>';
				}
				$('#previewExcelBody').html(html);
				 $('#previewModal').modal('show');
				 
				 if(data.code == 200){
		    			//console.log('200');
						 if(data.tcsOnlineExamBeanList.length > 0){
							 $('#bulkUploadExcel').attr('disabled', false);
						 }
			         }
			     	if(data.code == 422){
			         	alert(data.message);
			         	$('#bulkUploadExcel').attr('disabled', true);
			        }
			     	if(data.code == 421){
			     		 if( confirm(data.message)){
			     			 //console.log('proceed');
			     			 $('#bulkUploadExcel').attr('disabled', false);
			     		 }else{
			     			$('#bulkUploadExcel').attr('disabled', true);
			     		 }
			     	}	

	      },error: function (result, status, err) {
		          alert("Sorry, there was a Error in processing API!");
		          
		  },complete: function(){
				showSearchBtn();
				if($('#searchBody').css("display") == "block"){
					$('#downloadBtn').css("display", "block");
				}
				$('#fileUpdload').css("display", "block");
		      }
	    	
	    });
	
	
});

$('#bulkUploadCancel').click(function(e) {
	$('#fileId').val('');
});

function getExamCenterDD(year,month){
	
	 let data = {
				"examYear" :  year,
				"examMonth" : month
			};
		$.ajax({
			type : "POST",
			url : 'getExamCenterDropdown',
			contentType : "application/json",
			data : JSON.stringify(data),
			dataType : "JSON",
			success : function(data) {
				let len =  data.tcsOnlineExamBeanList.length;
				
				$("#centerDD").children("option").remove();
				$('#centerDD').append("<option value='' disabled selected>Select Center</option>");
				for(let i =0 ; i < len; i++){
					let centerId = data.tcsOnlineExamBeanList[i].centerId;
					let examCenterName = data.tcsOnlineExamBeanList[i].examCenterName;
			
					$('#centerDD').append("<option value='" +centerId + "'>" + examCenterName + "</option>");	
				}				
			}
		});	
}

function getExamDateDD(cId){
	let data = {
				"examYear" : document.getElementById("year").value,
				"examMonth" :  document.getElementById("month").value,
				"centerId": cId
			};
		$.ajax({
			type : "POST",
			url : 'getExamDateDropdown',
			contentType : "application/json",
			data : JSON.stringify(data),
			dataType : "JSON",
			success : function(data) {
				let len =  data.tcsOnlineExamBeanList.length;
				$('#ExamDateDD').children("option").remove();
				$('#ExamDateDD').append("<option value=''  selected>Select Exam Date</option>");
				for(let i =0 ; i < len; i++){
					let examDateList = data.tcsOnlineExamBeanList[i].examDate;

						$('#ExamDateDD').append("<option value='" +examDateList + "'>" + examDateList + "</option>");
				
				}	
			}
		});	
}

function getExamTimeDD(){
	let data = {
			"examYear" : document.getElementById("year").value,
			"examMonth" :  document.getElementById("month").value,
			"centerId": document.getElementById("centerDD").value,
			"examDate" : document.getElementById("ExamDateDD").value
		};
	
	$.ajax({
		type : "POST",
		url : 'getExamStartTimeDropdown',
		contentType : "application/json",
		data : JSON.stringify(data),
		dataType : "JSON",
		success : function(data) {
			let len =  data.tcsOnlineExamBeanList.length;
			$('#examTimeDD').children("option").remove();
			$('#examTimeDD').append("<option value=''  selected>Select Exam Time</option>");
			for(let i =0 ; i < len; i++){
				let examTimeList = data.tcsOnlineExamBeanList[i].examTime;
					$('#examTimeDD').append("<option value='" +examTimeList + "'>" + examTimeList + "</option>");
			}
				
		}
	});	
}


// start multiple update dropdown function 

function getMultipleUpdateExamDateDD(cId){
	$('#mutlipleUpdateExamTimeDD').load(" #mutlipleUpdateExamTimeDD > *");
	let data = {
				"examYear" : document.getElementById("year").value,
				"examMonth" :  document.getElementById("month").value,
				"centerId": cId
			};
		$.ajax({
			type : "POST",
			url : 'getExamDateDropdown',
			contentType : "application/json",
			data : JSON.stringify(data),
			dataType : "JSON",
			success : function(data) {
				let len =  data.tcsOnlineExamBeanList.length;
				$('#mutlipleUpdateExamDateDD').children("option").remove();
				$('#mutlipleUpdateExamDateDD').append("<option value='' disabled selected>Select Exam Date</option>");
				for(let i =0 ; i < len; i++){
					let examDateList = data.tcsOnlineExamBeanList[i].examDate;

						$('#mutlipleUpdateExamDateDD').append("<option value='" +examDateList + "'>" + examDateList + "</option>");
				
				}	
			}
		});	
}

function getMultipleUpdateExamTimeDD(cId){
	let data = {
			"examYear" : document.getElementById("year").value,
			"examMonth" :  document.getElementById("month").value,
			"centerId":cId,
			"examDate" : document.getElementById("mutlipleUpdateExamDateDD").value
		};
	
	$.ajax({
		type : "POST",
		url : 'getExamStartTimeDropdown',
		contentType : "application/json",
		data : JSON.stringify(data),
		dataType : "JSON",
		success : function(data) {
			let len =  data.tcsOnlineExamBeanList.length;
			$('#mutlipleUpdateExamTimeDD').children("option").remove();
			$('#mutlipleUpdateExamTimeDD').append("<option value='' disabled selected>Select Exam Time</option>");
			for(let i =0 ; i < len; i++){
				let examTimeList = data.tcsOnlineExamBeanList[i].examTime;
					$('#mutlipleUpdateExamTimeDD').append("<option value='" +examTimeList + "'>" + examTimeList + "</option>");
			}
				
		}
	});	
}

//end multiple update dropdown function 

	function hideSearchBtn(){
		console.log('call hide');
		$( "#btnSearch" ).replaceWith( "<img id='theImg' src='/exam/resources_2015/gifs/loading-29.gif' style='height:40px' />" );
	}
	
	
	
	function showSearchBtn(){
		$( "#theImg" ).replaceWith( '<input  type="button" class="btn btn-large btn-primary" value="search" id="btnSearch" onclick="displayTCSExamBookingData(1,0)" />' );
	}

	function checkSpecialCharInSapid(){
		let sapidvalue = document.getElementById("sapid").value;
		if(sapidvalue != ''){
		let isValid=/^[0-9, ]+$/.test(sapidvalue)
		if (!isValid) {
            alert("Please Remove Special Characters.");
            $('#btnSearch').attr('disabled', true);
            return false;
        }
        	$('#btnSearch').attr('disabled', false);
		}
		$('#btnSearch').attr('disabled', false);
	}

	function displayTCSExamBookingData(pageNo,loadStatus) {
		 jsonObj.splice(0, jsonObj.length);
         $("input[id='selectAllCheckbox']").prop("checked", false);
         $('#selectedCount').text('');
       
		 	$('#multipleEdit').attr('disabled', true);
		 	$("#testTable > tr").remove();
		 	$('#searchBody').css("display", "none");
		 	$('#downloadBtn').css("display", "none"); 	
		 
		 	$('#clearAllMulipleUpdateBtn').css("display", "none"); 	
		 	
		let examYear  = document.getElementById("year").value;		
		let examMonth = document.getElementById("month").value; 
		let center_id = document.getElementById("centerDD").value; 
		let exam_date = document.getElementById("ExamDateDD").value; 
		let exam_time = document.getElementById("examTimeDD").value; 
		let sapid = document.getElementById("sapid").value; 
		let testTaken = document.getElementById("testTakenId").value; 
		    if (examYear == "" || examMonth == "" )
		   {
		    alert("Please Select Both Year & Month Field ");
		    return false;
		   }
		
	    let status = loadStatus;
	    let x = document.getElementById("testTable")
	
		//console.log('status:'+loadStatus);
		
		if(loadStatus == 1){
		//	console.log("true success");
			x.style.display = "none";
			
		}	
		$('#fileUpdload').css("display", "none"); 	
		hideSearchBtn();

		let data = {
				"year" : examYear,
				"month" : examMonth,
				"centerId" : center_id,
				"examDate" : exam_date,
				"examTime" : exam_time,
				"sapid" : sapid,
				"testTaken" : testTaken
			};
	
		$.ajax({
			type : "POST",
			url : 'displayTCSExamBookingData/'+pageNo,
			contentType : "application/json",
			data : JSON.stringify(data),
			dataType : "JSON",
			success : function(data) {
			if(data.code == 422){
	         	alert(data.message);
	        }else{	
				let html='';
				let pagePanel='';
				let len = data.page.pageItems.length;
				let tcsOnlineExamBeanList = data.page.pageItems;
				let page = data.page;
				let srNo = 0 ;
				let isChecked = '';
				
				$('#totalCount').text(data.page.rowCount);
				for(let i =0 ; i < len; i++){
					srNo++;
					
					let sapId = "'"+tcsOnlineExamBeanList[i].userId+"'"; 
					let subject = "'"+tcsOnlineExamBeanList[i].subject+"'"; 
					let examDate = "'"+tcsOnlineExamBeanList[i].examDate+"'"; 
					let examTime = "'"+tcsOnlineExamBeanList[i].examTime+"'"; 
					let examYear = "'"+tcsOnlineExamBeanList[i].examYear+"'"; 
					let examMonth = "'"+tcsOnlineExamBeanList[i].examMonth+"'"; 
					
					
					html += '<tr class="rowClass"  data-cid="'+tcsOnlineExamBeanList[i].centerId+'" data-uid="'+tcsOnlineExamBeanList[i].examYear+'~'+tcsOnlineExamBeanList[i].examMonth+'~'+tcsOnlineExamBeanList[i].centerId+'~'+tcsOnlineExamBeanList[i].userId+'~'+tcsOnlineExamBeanList[i].subject+'">';
					html += '<td >'+srNo+'</td>';
					html += '<td class="uniqueRequestId" >'+tcsOnlineExamBeanList[i].uniqueRequestId+'</td>';
					html += '<td class="userId" >'+tcsOnlineExamBeanList[i].userId+'</td>';
					html += '<td class="studentName" >'+tcsOnlineExamBeanList[i].firstName+' '+tcsOnlineExamBeanList[i].lastName+'</td>';
					html += '<td class="password">'+tcsOnlineExamBeanList[i].password+'</td>';
					html += '<td class="examYear">'+tcsOnlineExamBeanList[i].examYear+'</td>';
					html += '<td class="examMonth">'+tcsOnlineExamBeanList[i].examMonth+'</td>';
					html += '<td class="program">'+tcsOnlineExamBeanList[i].program+'</td>';
					html += '<td class="subject">'+tcsOnlineExamBeanList[i].subject+'</td>';	
					html += '<td class="subjectId">'+tcsOnlineExamBeanList[i].subjectId+'</td>';	
					html += '<td class="lc">'+tcsOnlineExamBeanList[i].lc+'</td>';	
					html += '<td class="ic">'+tcsOnlineExamBeanList[i].centerName+'</td>';	
					html += '<td class="examCenter" id="showExamCenter-'+srNo+'">'+tcsOnlineExamBeanList[i].examCenterName+'</td>';
					html += '<td class="examDate" id="showExamDate-'+srNo+'" >'+tcsOnlineExamBeanList[i].examDate+'</td>';
					html += '<td class="examTime" id="showExamTime-'+srNo+'">'+tcsOnlineExamBeanList[i].examTime+'</td>';
					html += '<td class="registeredEmailId" id="showRegisteredEmailId-'+srNo+'">'+(tcsOnlineExamBeanList[i].registeredEmailId ? tcsOnlineExamBeanList[i].registeredEmailId : '')+'</td>';
					html += '<td class="testTaken" id="showTestTaken-'+srNo+'">'+(tcsOnlineExamBeanList[i].testTaken ? tcsOnlineExamBeanList[i].testTaken : '-')+'</td>';
					html +='<td class="joinLink" id="showJoinLink-'+srNo+'">'
						+ ( tcsOnlineExamBeanList[i].registeredEmailId ?  '<div class="btn-group btn-group-sm">'
							+ '<button type="button"  class="button-copy-link btn btn-sm btn-default" style="float: none;"  onclick="copyJoinLink('+sapId+','+subject+','+examYear+','+examMonth+')" >'
								+ 'Copy Join Link'
							+ '</button>'
						+ '</div>' : '-') 
					+'</td>';
					html += '<td class="send-email" id="send-email-'+srNo+'">'
						+ '<div class="btn-group btn-group-sm">'
							+ '<button type="button" '
								+ ' data-subject="' + tcsOnlineExamBeanList[i].subject + '" '
								+ ' data-sapid="' + tcsOnlineExamBeanList[i].userId + '" '
								+ ' data-year="' + tcsOnlineExamBeanList[i].examYear + '" '
								+ ' data-month="' + tcsOnlineExamBeanList[i].examMonth + '" '
								+ ' class="button-send-email btn btn-sm btn-default" style="float: none;" >'
									+ 'Send Email'
								+ '</button>'
							+ '</div>'
						+'</td>';

					
					html += '<td >'
				 	+ '<div class="btn-group btn-group-sm">'
								+ '<button type="button"  class="btn btn-sm btn-default" style="float: none;" id="btnDemoExam" onclick="getDemoExamLogBySapid('+tcsOnlineExamBeanList[i].userId+')">'
								+ 'Demo Exam Log'
							+ '</button>'
						+ '</div>' 
					+ '</td>';
					html += '<td >'
					 	+ '<div class="btn-group btn-group-sm">'
									+ '<button type="button"  class="btn btn-sm btn-default" style="float: none;" id="btnDemoExam" onclick="getTestTakenFlagBySapidAndAccessKey('+sapId+','+subject+','+examDate+','+examTime+')">'
									+ 'Show Mettl Status'
								+ '</button>'
							+ '</div>' 
						+ '</td>';
					
					for(let j =0 ; j < jsonObj.length; j++){
							if(jsonObj[j].uniqueRequestId == tcsOnlineExamBeanList[i].uniqueRequestId){
								isChecked = 'Y';
							}
					}
					if(isChecked == 'Y'){
						html += '<td ><input type="checkbox" id="checkBoxForUpdate" name="checkBoxForUpdate"  onclick="onClickCheckBox(this);" style="width: 15px; height: 30px;" checked="checked" /> </td>';
						isChecked = '';
					}else{
						html += '<td ><input type="checkbox" id="checkBoxForUpdate" name="checkBoxForUpdate"  onclick="onClickCheckBox(this);" style="width: 15px; height: 30px;" /> </td>';
					}
					
					
					
					html += '</tr>';
				}
				
				
					
				let prevUrl = page.currentIndex - 1 ;
				let lastUrl = page.totalPages ;
				let nextUrl = page.currentIndex + 1 ;
				
				if(page.totalPages > 1){
				pagePanel += '<div align="center">';
				pagePanel += '<ul class="pagination">';
	
				if(page.currentIndex == 1){	
										
					pagePanel += '<li class="disabled"><a href="#">&lt;&lt;</a></li>';
					pagePanel += '<li class="disabled"><a href="#">&lt;</a></li>';
				}else{
					
					pagePanel += '<li><a onclick="displayTCSExamBookingData(1,true)">&lt;&lt;</a></li>';
					pagePanel += '<li><a onclick="displayTCSExamBookingData('+prevUrl+',1)">&lt;</a></li>';
				}	
	
				for (var i = page.beginIndex; i < page.endIndex; i++ )
				{
					
					if(i == page.currentIndex){	
					pagePanel += '<li class="active"><a onclick="displayTCSExamBookingData('+i+',1)">'+i+'</a></li>';
					}else{
					pagePanel += '<li><a onclick="displayTCSExamBookingData('+i+',1)">'+i+'</a></li>';
					}
				}
				if(page.currentIndex == page.totalPages){
				pagePanel += '<li class="disabled"><a href="#">&gt;</a></li>';
				pagePanel += '<li class="disabled"><a href="#">&gt;&gt;</a></li>';
				}else{
				pagePanel += '<li><a onclick="displayTCSExamBookingData('+nextUrl+',1)">&gt;</a></li>';
				pagePanel += '<li><a onclick="displayTCSExamBookingData('+lastUrl+',1)">&gt;&gt;</a></li>';
				}
				pagePanel += '</ul>';
				pagePanel += '</div>';
				}  
				
				
				
				$('#downloadBtn').css("display", "block");	
				 $('#testTable').html(html);
				 			
				 $('#paginationPanel').html(pagePanel);
				
				 callTableEdit();
				 
				 onChangeExamTimeValidation();
				 onChangeExamDateValidation();
				 let srNoId ;
				$(".tabledit-edit-button").attr("id","click");
				$("button#click").click(function(){
					
					$('.tabledit-save-button.btn.btn-sm.btn-success').attr('disabled', true);
					let str = $(this).closest('tr').data('uid');
				    uniqueId = str.split('~');
				    
				  	 srNoId = $(this).closest('tr').attr('id');
				    
				    getExamCenterDropdown(uniqueId[2],srNoId);
				    getExamDateDropdown(uniqueId[2],srNoId); 
				    getExamTimeDropdown(uniqueId[2],srNoId,0);
					});
				
				 $("select[name='examDate']").change(function() {
					 getExamTimeDropdown(uniqueId[2],srNoId,1);
					});
				
				$('select[name="centerId"]').on('change', function() {
					getExamDateDropdown(uniqueId[2],srNoId);
					});
				 $('#searchBody').css("display", "block");
				 if(loadStatus == 1){
						//console.log("true success");
						x.removeAttribute("style");
				}

//				 $('.button-copy-link').click(function() {
//					navigator.clipboard.writeText($(this).attr('data-link')).then(function() {
//					  alert('Copied!');
//					}, function(err) {
//					  console.error('Error Copying : ', err);
//					});
//				 })
				 $('.button-copy-error').click(function() {
					navigator.clipboard.writeText($(this).attr('data-link')).then(function() {
					  alert('Copied!');
					}, function(err) {
					  console.error('Error Copying : ', err);
					});
				 })
				 $('.button-send-email').click(function() {
					var subject = $(this).attr('data-subject');
					var year = $(this).attr('data-year');
					var month = $(this).attr('data-month');
					var sapid = $(this).attr('data-sapid');
					

					let data = {
						"year" : examYear,
						"month" : examMonth,
						"subject" : subject,
						"sapid" : sapid
					};
			
					$.ajax({
						type : "POST",
						url : '/m/sendExamEmail',
						contentType : "application/json",
						data : JSON.stringify(data),
						dataType : "JSON",
						success : function(data) {
							if(data.status === 'fail') {
								alert("Error sending email : " + data.error)
							} else {
								alert("Mail Sent Successfully!")
							}
							
						},
						error : function(error) {
							alert("Error sending email : " + error)
						}
					})
				 })
	           } 
			},complete: function(){
				showSearchBtn();
				$('#fileUpdload').css("display", "block");	
		      }
		});			
	  
	}
	
	function getDemoExamLogBySapid(sapId){
	
		
			hideSearchBtn();
			
		 	$('#downloadBtn').css("display", "none"); 	
		 	$('#fileUpdload').css("display", "none");
		 	let body = {"userId" :  sapId};
		    
		     $.ajax({
		      type : "POST",
			  url : 'getDemoExamLogBySapid',
			  contentType : "application/json",
			  data : JSON.stringify(body),
			  dataType : "JSON",
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
					showSearchBtn();
					$('#downloadBtn').css("display", "block");
					$('#fileUpdload').css("display", "block");
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
	
	function getTestTakenFlagBySapidAndAccessKey(sapId,subjectName,examDate,examTime){
		
		
		hideSearchBtn();
	
		$('#downloadBtn').css("display", "none"); 	
		$('#fileUpdload').css("display", "none");
		let body = {
				"sapid" :  sapId,
				"subject" : subjectName,
				"examDate" : examDate,
				"examTime" : examTime
				};
		
		$.ajax({
			type : "POST",
			url : 'https://ngasce-content.nmims.edu/exam/m/getExamStatus',
			contentType : "application/json",
			data : JSON.stringify(body),
			dataType : "JSON",
			success : function(data){
				if(data.code == 422){
		         	alert(data.message);
		        }else{
					let htmlTestTaken ='';
					let len = data.examBookingTransactionBeanList.length;
					let examTransactionBeanList = data.examBookingTransactionBeanList;
					let srNo = 0 ;
					
					for(let i =0 ; i < len; i++){
						srNo++;
						gradedJson = examTransactionBeanList[i].gradedJsonResponse;	
						htmlTestTaken += '<tr >';
					
						htmlTestTaken += '<td >'+examTransactionBeanList[i].sapid+'</td>';
						htmlTestTaken += '<td >'+examTransactionBeanList[i].firstName+' '+examTransactionBeanList[i].lastName+'</td>';
						htmlTestTaken += '<td >'+(examTransactionBeanList[i].testTaken ? examTransactionBeanList[i].testTaken : '-')+'</td>';
						
						htmlTestTaken += '<td >'+(examTransactionBeanList[i].startJsonResponse ? '<button class="btn btn-sm btn-primary" onclick="beautifyJson('+examTransactionBeanList[i].startJsonResponse+');"> View Start Details  </button> ' : '-')+'</td>';
						htmlTestTaken += '<td >'+(examTransactionBeanList[i].finishedJsonResponse ? '<button class="btn btn-sm btn-primary" onclick="beautifyJson('+examTransactionBeanList[i].finishedJsonResponse+');"> View Finished Details </button> ' : '-')+'</td>';
						htmlTestTaken += '<td >'+(examTransactionBeanList[i].finish_node ? examTransactionBeanList[i].finish_node : '-')+'</td>';
						htmlTestTaken += '<td >'+(examTransactionBeanList[i].gradedJsonResponse ? '<button class="btn btn-sm btn-primary" onclick="beautifyGradedJson(gradedJson);"> View Graded Details </button> ' : '-')+'</td>';
						htmlTestTaken += '<td >'+(examTransactionBeanList[i].resumeJsonResponse ? '<button class="btn btn-sm btn-primary" onclick="beautifyJson('+examTransactionBeanList[i].resumeJsonResponse+');"> View Resume Details </button> ' : '-')+'</td>';
						
						htmlTestTaken += '</tr>';
					}
					$('#testTakenModalBody').html(htmlTestTaken);
					$('#testTakenModal').modal('show');
					
		        }
				
				
			},error: function (result, status, err) {
				alert("Sorry, there was a Error in processing API!");
				
			},complete: function(){
				showSearchBtn();
				$('#downloadBtn').css("display", "block");
				$('#fileUpdload').css("display", "block");
			}
			
		});
		
	}
	
	function copyJoinLink(sapId,subjectName,examYear,examMonth){
		
		
		hideSearchBtn();
		
		$('#downloadBtn').css("display", "none"); 	
		$('#fileUpdload').css("display", "none");
		let body = {
				"userId" :  sapId,
				"subject" : subjectName,
				"examYear" : examYear,
				"examMonth" : examMonth
		};
		
		$.ajax({
			type : "POST",
			url : 'copyJoinLinkForSapid',
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
				showSearchBtn();
				$('#downloadBtn').css("display", "block");
				$('#fileUpdload').css("display", "block");
			}
			
		});
		
	}
	
	function removeArrayValue(arr) {
	    var what, a = arguments, L = a.length, ax;
	    while (L > 1 && arr.length) {
	        what = a[--L];
	        while ((ax= arr.indexOf(what)) !== -1) {
	            arr.splice(ax, 1);
	        }
	    }
	    return arr;
	}
	
	const getIndex = (findObj, array) => (
			  array.findIndex(obj => (
			    Object.entries(findObj).every(([key, val]) => obj[key] === val)
			  ))
			);
	
	function createJsonObject(pointer){
		 let obj  = {}
	
		 obj["userId"] = $(pointer).closest('tr').find('.userId').text();
		 obj["examYear"] = $(pointer).closest('tr').find('.examYear').text() ;
		 obj["examMonth"] = $(pointer).closest('tr').find('.examMonth').text() ;
		 obj["program"] = $(pointer).closest('tr').find('.program').text() ;
		 obj["subject"] = $(pointer).closest('tr').find('.subject').text() ;
		 obj["examCenterName"] = $(pointer).closest('tr').find('.examCenter').text() ;
		 obj["examDate"] = $(pointer).closest('tr').find('.examDate').text() ;
		 obj["examTime"] = $(pointer).closest('tr').find('.examTime').text() ;
		 obj["centerId"] = $(pointer).closest('tr').data("cid");
	    
		 return obj;    
	}
	
	function createJsonArray(pointer){
					let obj  = {};
					obj  = createJsonObject(pointer);
			        jsonObj.push(obj);			        
	}
	
	function createJsonArrayForAll(){
		$.each($("input[name='checkBoxForUpdate']:checked"),
	              function () {
			let obj  = {}
		
			 obj["userId"] = $(this).closest('tr').find('.userId').text();
			 obj["examYear"] = $(this).closest('tr').find('.examYear').text() ;
			 obj["examMonth"] = $(this).closest('tr').find('.examMonth').text() ;
			 obj["program"] = $(this).closest('tr').find('.program').text() ;
			 obj["subject"] = $(this).closest('tr').find('.subject').text() ;
			 obj["examCenterName"] = $(this).closest('tr').find('.examCenter').text() ;
			 obj["examDate"] = $(this).closest('tr').find('.examDate').text() ;
			 obj["examTime"] = $(this).closest('tr').find('.examTime').text() ;
			 obj["centerId"] = $(this).closest('tr').data("cid");

			 jsonObj.push(obj);	
	              });
	}

	function onClickCheckBox(pointer){
		
		if($('#selectAllCheckbox').is(":checked")){
		 	$('#selectAllCheckbox').prop("checked", false);
		 }
		if($(pointer).is(":checked")){
			createJsonArray(pointer);
		}
		if(!$(pointer).is(":checked")){
			let obj  ={};
			let index ;
			obj  = createJsonObject(pointer);
			index = getIndex(obj, jsonObj);
			removeArrayValue(jsonObj,jsonObj[index]);
		}

		let selectedCheckBoxCount =  jsonObj.length;
		
		let totalCheckboxCount =   $('input[id="checkBoxForUpdate"]').length;
		
		if(selectedCheckBoxCount == totalCheckboxCount){
		 	$('#selectAllCheckbox').prop("checked", true);
		 }
		 if(selectedCheckBoxCount != 0){
			 
		     $('#selectedCount').text(selectedCheckBoxCount);
		     $('#multipleEdit').attr('disabled', false);
		     $('#clearAllMulipleUpdateBtn').css("display", "block"); 
		 }else{
		 	$('#selectedCount').text('');
		 	$('#multipleEdit').attr('disabled', true);
		 	 $('#clearAllMulipleUpdateBtn').css("display", "none"); 
		 }
	}
	
	function onClickSelectAllCheckBox(){
		 if($('#selectAllCheckbox').is(":checked")) {
			 jsonObj.splice(0, jsonObj.length);
            $("input[id='checkBoxForUpdate']").prop("checked", true);
			 createJsonArrayForAll();
            let selectedCheckBoxCount = jsonObj.length; 
		 	$('#selectedCount').text(selectedCheckBoxCount);
		 	$('#multipleEdit').attr('disabled', false);
		 	 $('#clearAllMulipleUpdateBtn').css("display", "block"); 
         } else {
        	 jsonObj.splice(0, jsonObj.length);
            $("input[id='checkBoxForUpdate']").prop("checked", false);
            $('#selectedCount').text('');
		 	$('#multipleEdit').attr('disabled', true);
		 	$('#clearAllMulipleUpdateBtn').css("display", "none"); 
         } 
	}
	
	function clearAllMulipleUpdate(){
		 jsonObj.splice(0, jsonObj.length);
         $("input[id='checkBoxForUpdate']").prop("checked", false);
         $("input[id='selectAllCheckbox']").prop("checked", false);
         $('#selectedCount').text('');
		 $('#multipleEdit').attr('disabled', true);
		 $('#clearAllMulipleUpdateBtn').css("display", "none"); 
	}
	
	function toggleMultipleUpdateModel(){
		$('#multipleUpdateBtn').attr('disabled', true);
		let centerId = jsonObj[0].centerId; 
		$('#multipleUpdateTotal').text(jsonObj.length);
		getMultipleUpdateExamDateDD(centerId);
		
		 $('#mulitpleUpdateModal').modal('show');
	}
		
	function getExamCenterDropdown(tempCenterId,id){
		
		 let data = {
					"examYear" : document.getElementById("year").value,
					"examMonth" :  document.getElementById("month").value
				};
			$.ajax({
				type : "POST",
				url : 'getExamCenterDropdown',
				contentType : "application/json",
				data : JSON.stringify(data),
				dataType : "JSON",
				success : function(data) {
					let len =  data.tcsOnlineExamBeanList.length;
					
				//	console.log('len '+len);
					$("#showExamCenter-"+id).children(".tabledit-input").children("option").remove();
					for(let i =0 ; i < len; i++){
						let centerId = data.tcsOnlineExamBeanList[i].centerId;
						let examCenterName = data.tcsOnlineExamBeanList[i].examCenterName;

						if(tempCenterId == centerId){
							$("#showExamCenter-"+id).children(".tabledit-input").append("<option value='" +centerId + "' selected>" + examCenterName + "</option>");
						}else{	
							$("#showExamCenter-"+id).children(".tabledit-input").append("<option value='" +centerId + "'>" + examCenterName + "</option>");
						}
					}
						
				}
			});	
	}

	
	function getExamDateDropdown(cId,id){
		let data = {
					"examYear" : document.getElementById("year").value,
					"examMonth" :  document.getElementById("month").value,
					"centerId": cId
				};
			$.ajax({
				type : "POST",
				url : 'getExamDateDropdown',
				contentType : "application/json",
				data : JSON.stringify(data),
				dataType : "JSON",
				success : function(data) {
					let len =  data.tcsOnlineExamBeanList.length;
					let prev_val = $("#showExamDate-"+id).children(".tabledit-span").text();
					
					$("#showExamDate-"+id).children(".tabledit-input").children("option").remove();
					for(let i =0 ; i < len; i++){
						let examDateList = data.tcsOnlineExamBeanList[i].examDate;

						if(prev_val == examDateList){
							$("#showExamDate-"+id).children(".tabledit-input").append("<option value='" +examDateList + "' selected>" + examDateList + "</option>");
						}else{	
							$("#showExamDate-"+id).children(".tabledit-input").append("<option value='" +examDateList + "'>" + examDateList + "</option>");
						}
					}
				//	console.log('call')
						
				}
			});	
	}
	
	
	function getExamTimeDropdown(cId,id,isOnchange){
		let examDate ;
		if(parseInt(isOnchange)  == parseInt("1") ){
			 examDate = $("#showExamDate-"+id).children('select[name="examDate"]').val();
		}else{
			 examDate = $("#showExamDate-"+id).children(".tabledit-span").text();
		}
		let data = {
				"examYear" : document.getElementById("year").value,
				"examMonth" :  document.getElementById("month").value,
				"centerId": cId,
				"examDate" : examDate
			};
		
		$.ajax({
			type : "POST",
			url : 'getExamStartTimeDropdown',
			contentType : "application/json",
			data : JSON.stringify(data),
			dataType : "JSON",
			success : function(data) {
				let len =  data.tcsOnlineExamBeanList.length;
				let prev_val = $("#showExamTime-"+id).children(".tabledit-span").text();
				
				
				$("#showExamTime-"+id).children(".tabledit-input").children("option").remove();
				for(let i =0 ; i < len; i++){
					let examTimeList = data.tcsOnlineExamBeanList[i].examTime;
					if(examTimeList != ''){
						if(prev_val == examTimeList){
							$("#showExamTime-"+id).children(".tabledit-input").append("<option value='" +examTimeList+ "' selected>" + examTimeList + "</option>");
						}else{	
							$("#showExamTime-"+id).children(".tabledit-input").append("<option value='" +examTimeList+ "'>" + examTimeList + "</option>");
						}
					}
				}
			//	console.log('call')
					
			}
		});	
	}
	
	function callTableEdit(){
			
	$(".dataTables").Tabledit({
    	columns: {
			  identifier: [0, 'id'],                 
			  editable: [
				  			[12, 'centerId','{}'],
				  			[13, 'examDate','{}'],
				  			[14, 'examTime','{}']
				  			
			  			]
			},
			// link to server script
			// e.g. 'ajax.php'
			url: "",
			// class for form inputs
			inputClass: 'form-control input-sm',
			// // class for toolbar
			toolbarClass: 'btn-toolbar',
			// class for buttons group
			groupClass: 'btn-group btn-group-sm',
			// class for row when ajax request fails
			 dangerClass: 'warning',
			// class for row when save changes
			warningClass: 'warning',
			// class for row when is removed
			mutedClass: 'text-muted',
			// trigger to change for edit mode.
			// e.g. 'dblclick'
			eventType: 'click',
			// change the name of attribute in td element for the row identifier
			rowIdentifier: 'id',
			// activate focus on first input of a row when click in save button
			autoFocus: true,
			// hide the column that has the identifier
			hideIdentifier: false,
			// activate edit button instead of spreadsheet style
			editButton: true,
			// activate delete button
			deleteButton: false,
			// activate save button when click on edit button
			saveButton: true,
			// activate restore button to undo delete action
			restoreButton: true,
			onDraw: function() { 
				/*  $('.dataTables').DataTable(); */  
			},
			onAjax: function(action, serialize) {
				//console.log('data '+uniqueId)
				serialize['examYear'] = uniqueId[0];
				serialize['examMonth'] = uniqueId[1];
				serialize['userId'] = uniqueId[3];
				serialize['subject'] = uniqueId[4];
			//	console.log('action '+action)
				let body = JSON.stringify(serialize);
				let requestURL = '';
				if(action == 'edit'){
					requestURL = 'syncUpdatedExamBookingData';	
				}else{
					requestURL = '';
				}
				
				hideSearchBtn();

				$.ajax({
					type : "POST",
					url : requestURL,
					contentType : "application/json",
					data : body,
					dataType : "json",
					success : function(data) {
						 $('.tabledit-save-button.btn.btn-sm.btn-success').attr('disabled', true);
						if(data.code == 200){
							let messageSuccess = '';
				        	messageSuccess += '<div class="alert alert-success alert-dismissible">';
				    		messageSuccess += '<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>';
				    		messageSuccess += data.message;
				    		messageSuccess += '</div>';
							$('#responseMsg').html(messageSuccess);
							
				         }

						
				     	
				     	if(data.code == 422){
				         //	console.log('error : '+data.message);
				         	
				         	let messageError = '';
				     		messageError += '<div class="alert alert-danger alert-dismissible">';
				    		messageError += '<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>';
				    		messageError += data.message;
				    		messageError += '</div>';
							$('#responseMsg').html(messageError);
				        }
										
						
					},error: function (result, status, err) {
				          alert("Sorry, there was a Error in processing API!");
				          $('.tabledit-save-button.btn.btn-sm.btn-success').attr('disabled', true);
				    },complete: function(){
						showSearchBtn();
					  }
				});
			}
		});
		$(".tabledit-toolbar.btn-toolbar").removeAttr("style");	
		$(".btn-group.btn-group-sm").removeAttr("style");	
		$(".tabledit-toolbar.btn-toolbar").attr("style","margin-right: 60px");
	}
	
	function onChangeExamTimeValidation(){
		$('select[name="examTime"]').on('change', function() {
			 console.log('call onchange examTime')
			 singleAjaxValidation(this);
		});
	}

	function onChangeExamDateValidation(){
		$('select[name="examDate"]').on('change', function() {
			console.log('call onchange examDate')
			singleAjaxValidation(this);
		});
	}
	
	function singleAjaxValidation(pointer){
		singleJsonObj.splice(0, singleJsonObj.length);
		let obj  = {};
		obj  = createJsonObject(pointer);
		singleJsonObj.push(obj);			        

		$.ajax({
		 	url : 'validateUpdateTcsExamBookingData',
		    type : 'POST',
			contentType : "application/json",
			data : JSON.stringify({ tcsOnlineExamList: singleJsonObj,
				examDate : $(pointer).closest('tr').find('select[name="examDate"]').val(),
				examTime : $(pointer).closest('tr').find('select[name="examTime"]').val()
				}),
			dataType : "json",
		  success : function(data){
				if(data.code == 200){
					//console.log('200');
					 $('.tabledit-save-button.btn.btn-sm.btn-success').attr('disabled', false);
		         }
		     	if(data.code == 422){
		         	alert('Selected Slot Already Present In System For Another Subject, Please Change The Slot For Update !!!');
		         	$('.tabledit-save-button.btn.btn-sm.btn-success').attr('disabled', true);
		        }
		     	if(data.code == 421){
		     		 if( confirm(data.message)){
		     			// console.log('proceed');
		     			 $('.tabledit-save-button.btn.btn-sm.btn-success').attr('disabled', false);
		     		 }else{
		     			$('.tabledit-save-button.btn.btn-sm.btn-success').attr('disabled', true);
		     		 }
		     	}	
		
		  },error: function (result, status, err) {
		          alert("Sorry, there was a Error in processing API!");
		          
		  }
		});
	}

$( document ).ready(function() {
	 $('#bulkUploadExcel').click(function(e) {
		 $('#previewModal').modal('hide');
		    //Disable submit button
		    $(this).prop('disabled',true);
		    hideSearchBtn();
		    var form = $('#fileUploadForm')[0];
		    
		    let formData = new FormData(form);
		  //  console.log('abhay formData '+formData)	
		    // Ajax call for file uploaling
		     $.ajax({
		      url : 'excelBulkSyncUpdateTcsExamBookingData',
		      type : 'POST',
		      enctype: 'multipart/form-data',
		      data : formData,
		      cache : false,
		      contentType : false,
		      processData : false,
		      success : function(data){
		    		if(data.code == 200){
						let messageSuccess = '';
			        	messageSuccess += '<div class="alert alert-success alert-dismissible">';
			    		messageSuccess += '<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>';
			    		messageSuccess += data.message;
			    		messageSuccess += '</div>';
						$('#responseMsg').html(messageSuccess);
			         }

					
			     	
			     	if(data.code == 422){
			         	//console.log('error : '+data.message);
			         	
			         	let messageError = '';
			     		messageError += '<div class="alert alert-danger alert-dismissible">';
			    		messageError += '<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>';
			    		messageError += data.message;
			    		messageError += '</div>';
						$('#responseMsg').html(messageError);
			        }
			     	

		      },error: function (result, status, err) {
			          alert("Sorry, there was a Error in processing API!");
			          
			  },complete: function(){
						showSearchBtn();
						$('#fileId').val('');
				      }
		    	
		    });
		    
		    
	 });		 
	 
	 
	 $('#multipleUpdateBtn').click(function(e) {
		 if( confirm('Do you really want to update ?')){
			 $('#mulitpleUpdateModal').modal('hide');
			    //Disable submit button
			    $(this).prop('disabled',true);
			    hideSearchBtn();
		     $.ajax({
	    	 	url : 'multipleUpdateTcsExamBookingData',
			    type : 'POST',
				contentType : "application/json",
				data : JSON.stringify({ tcsOnlineExamList: jsonObj,
					examDate : $('#mutlipleUpdateExamDateDD').val(),
					examTime : $('#mutlipleUpdateExamTimeDD').val()
					}),
				dataType : "json",
		      success : function(data){
		    		if(data.code == 200){
						let messageSuccess = '';
			        	messageSuccess += '<div class="alert alert-success alert-dismissible">';
			    		messageSuccess += '<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>';
			    		messageSuccess += data.message;
			    		messageSuccess += '</div>';
						$('#responseMsg').html(messageSuccess);
						
			         }
			     	if(data.code == 422){
			         //	console.log('error : '+data.message);
			         	
			         	let messageError = '';
			     		messageError += '<div class="alert alert-danger alert-dismissible">';
			    		messageError += '<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>';
			    		messageError += data.message;
			    		messageError += '</div>';
						$('#responseMsg').html(messageError);
			        }
			     	

		      },error: function (result, status, err) {
			          alert("Sorry, there was a Error in processing API!");
			          
			  },complete: function(){
						showSearchBtn();
						jsonObj.splice(0, jsonObj.length);
			            $("input[id='selectAllCheckbox']").prop("checked", false);
			            $("#testTable > tr").remove();
			            $('#selectedCount').text('');
					 	$('#multipleEdit').attr('disabled', true);
					 	$('#searchBody').css("display", "none");
					 	$('#downloadBtn').css("display", "none");
					 	$('#clearAllMulipleUpdateBtn').css("display", "none"); 
				      }
		    	
		    });
		    
		 }
	 });
	 
	 
	 $('#mutlipleUpdateExamDateDD').on('change', function() {
		 $('#multipleUpdateBtn').attr('disabled', true);
		 	let centerId = jsonObj[0].centerId;
			getMultipleUpdateExamTimeDD(centerId);
			
			});
		
		
		
		$('#mutlipleUpdateExamTimeDD').on('change', function() {
			 
			$.ajax({
	    	 	url : 'validateUpdateTcsExamBookingData',
			    type : 'POST',
				contentType : "application/json",
				data : JSON.stringify({ tcsOnlineExamList: jsonObj,
					examDate : $('#mutlipleUpdateExamDateDD').val(),
					examTime : $('#mutlipleUpdateExamTimeDD').val()
					}),
				dataType : "json",
		      success : function(data){
		    		if(data.code == 200){
		    			//console.log('200');
		    			 $('#multipleUpdateBtn').attr('disabled', false);
			         }
			     	if(data.code == 422){
			         	alert(data.message);
			         	$('#multipleUpdateBtn').attr('disabled', true);
			        }
			     	if(data.code == 421){
			     		 if( confirm(data.message)){
			     			// console.log('proceed');
			     			 $('#multipleUpdateBtn').attr('disabled', false);
			     		 }else{
			     			$('#multipleUpdateBtn').attr('disabled', true);
			     		 }
			     	}	

		      },error: function (result, status, err) {
			          alert("Sorry, there was a Error in processing API!");
			          
			  }
		    });
		});
		
});	
	



