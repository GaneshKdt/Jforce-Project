/*<![CDATA[*/
//----------*****---------- Time Spent Charts ---------*****-----------//
var myActivityJsonData = $('#my-activity-data').text();
var myActivityData = JSON.parse(myActivityJsonData);
	
const myActivityThisWeek = [];
const myActivityCurrentMonth = [];
const myActivityLastMonth = [];

const thisWeekLabels = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
const currentAndLastMonthLabels = ["Week 1", "Week 2", "Week 3", "Week 4", "Week 5"];
const thisWeek = Array(7).fill(0);
const currentAndLastMonth = Array(5).fill(0);
		
//This week hours set 0 if not prensent
myActivityData.This_Week.forEach(day => {
	const index = thisWeekLabels.indexOf(day.day_name);
	if (index !== -1) {
		thisWeek[index] = day.hours+'.'+day.minutes;
	}
});
		
myActivityThisWeek.push(...thisWeek);

//Current month hours set 0 if not prensent
myActivityData.Current_Month.forEach(week => {
	const index = currentAndLastMonthLabels.indexOf(week.week_name);
	if (index !== -1) {
		currentAndLastMonth[index] = week.hours+'.'+week.minutes;
	}
});

myActivityCurrentMonth.push(...currentAndLastMonth);
		
//Last month hours set 0 if not prensent
myActivityData.Last_Month.forEach(week => {
	const index = currentAndLastMonthLabels.indexOf(week.week_name);
	if (index !== -1) {
		currentAndLastMonth[index] = week.hours+'.'+week.minutes;
	}
});

myActivityLastMonth.push(...currentAndLastMonth);
	
//Current week time spent chart
const weekctx = document.getElementById("week-chart").getContext('2d');
const weekChart = new Chart(weekctx, {
	type: 'bar',
    data: {
    	labels: thisWeekLabels,
        datasets: [{
	        label: 'This week',
	        backgroundColor: 'rgba(17, 141, 255, 0.8)',
	        borderColor: 'rgb(18, 35, 158)',
	        data: myActivityThisWeek,
        }]
    },
	options: {
		title: {
			position: 'left',
            display: true,
            text: 'Time Spent Hrs'
        },
        scales: {
          	yAxes: [{
            	ticks: {
              		beginAtZero: true,
            	}
          	}]
        },
        tooltips: {
            callbacks: {
              	label: function (tooltipItem, data) {
                	const value = data.datasets[tooltipItem.datasetIndex].data[tooltipItem.index];
                	const timeParts = value.split('.');
                	const hours = parseInt(timeParts[0]);
                	const minutes = timeParts.length > 1 ? parseInt(timeParts[1]) : 0;
                	return hours + "hr " + minutes + "min";
              	}
            }
          }
	}
});
    
//Current and previous month time spent chart
const monthctx = document.getElementById("month-chart").getContext('2d');
const monthChart = new Chart(monthctx, {
	type: 'line',
    data: {
        labels: currentAndLastMonthLabels,
        datasets: [{
        	label: 'This Month',
            backgroundColor: 'rgba(0, 0, 0, 0)',
            borderColor: 'rgb(47, 128, 237)',
            data: myActivityCurrentMonth,
            tension: 0,
        },
        {
        	label: 'Previous Month',
	        backgroundColor: 'rgba(0, 0, 0, 0)',
	        borderColor: 'rgb(255, 77, 115)',
	        data: myActivityLastMonth,
	        tension: 0,
        }]
    },
    options: {
    	title: {
    		position: 'left',
            display: true,
            text: 'Time Spent Hrs'
        },
        scales: {
          	y: {
            	beginAtZero: true
          	}
        },
    	tooltips: {
        	callbacks: {
          		label: function (tooltipItem, data) {
            		const value = data.datasets[tooltipItem.datasetIndex].data[tooltipItem.index];
            		const timeParts = value.split('.');
            		const hours = parseInt(timeParts[0]);
            		const minutes = timeParts.length > 1 ? parseInt(timeParts[1]) : 0;
            		return hours + "hr " + minutes + "min";
          		}
        	}
      	}
    }
});
//----------*****---------- End Time Spent Charts ---------*****-----------//
    
//----------*****---------- Session Attendance Chart ---------*****-----------//
var sessionAttendanceJsonData = $('#session-attendance-count').text();
var sessionAttendanceData = '';

//If student from current cycle
sessionAttendanceData = JSON.parse(sessionAttendanceJsonData);
	
var trackDetails = $('#track_details').text();
var trackColors = JSON.parse(trackDetails);
var track = {};
var labels = [];
	
const sessionAttendance = [];
const videoAttendance = Array(sessionAttendanceData.length).fill(0);
const subjectLabels = [];
const datasets = [];
	
//Set track wise data
sessionAttendanceData.forEach(attendance => {
	const attendanceEntry = {};
	attendanceEntry[attendance.track] = {
		subject_name: attendance.subject_name,
		subject_count: attendance.subject_count
	};
	sessionAttendance.push(attendanceEntry);
});
	
//Create subject count for perticuler subject
$.each(sessionAttendance, function(index, subject) {
	var trackName = Object.keys(subject)[0];
	var subjectName = subject[trackName]["subject_name"];
	var subjectCount = subject[trackName]["subject_count"];
	
	if (!(trackName in track)) {
	   track[trackName] = [];
	}
	
	var existingSubject = track[trackName].find(function(s) {
	    return s.subject_name === subjectName;
	});
	
	if (existingSubject === undefined) {
	   track[trackName].push({
	       subject_name: subjectName,
	       subject_count: subjectCount
	   });
	} else {
	    existingSubject.subject_count += subjectCount;
	}
});
	
//Create dataset for session attendance bar chart
$.each(track, function(trackName, subjects) {
	const data = [];
	
	//Create unique subject labels
	$.each(subjects, function(index, subject) {
	    if (!subjectLabels.includes(subject.subject_name)) {
	    	subjectLabels.push(subject.subject_name);
	    }
	});
	
	// If subject is not found, push 0 as the default value
	$.each(subjectLabels, function(index, label) {
		var subject = subjects.find(function(subject) {
	        return subject.subject_name === label;
	    });
	
	    //Set 0 if subject count not present
	    if (subject) {
	        data.push(subject.subject_count);
	    } else {
	        data.push(0);
	    }
	});
	
	//Create datasets details
	var backgroundColor;
	var borderColor;
	    
	//Convert track color hexcode to rgba format
	$.each(trackColors, function(index, obj) {
	    var track = obj.track;
	    var hexCode = obj.hexCode;
	    	
	    if(trackName === track){
	        hexCode = hexCode.replace("#", "");
	        	
	        var red = parseInt(hexCode.substring(0, 2), 16);
	        var green = parseInt(hexCode.substring(2, 4), 16);
	        var blue = parseInt(hexCode.substring(4, 6), 16);
	        var alpha = 0.8;
	        backgroundColor = `rgba(${red}, ${green}, ${blue}, ${alpha})`;
	        	
	        var darkerRed = Math.floor(red * alpha);
	        var darkerGreen = Math.floor(green * alpha);
	        var darkerBlue = Math.floor(blue * alpha);
	        borderColor = `rgb(${darkerRed}, ${darkerGreen}, ${darkerBlue})`;
	    }
	});
	
	//If not attend any session
	if(trackName === 'Not Attened'){
		var alpha = 0.8;
		backgroundColor = `rgba(255, 255, 255, ${alpha})`;
			
		var commonDarkerRed = Math.floor(255 * alpha);
	    var commonDarkerGreen = Math.floor(255 * alpha);
	    var commonDarkerBlue = Math.floor(255 * alpha);
	    borderColor = `rgb(${commonDarkerRed}, ${commonDarkerGreen}, ${commonDarkerBlue})`;
	}
	
	//If track name is not present
	if(trackName === ''){
	    trackName = 'Common For All Batches';
			
		var alpha = 0.8;
		backgroundColor = `rgba(157, 181, 251, ${alpha})`;
			
		var commonDarkerRed = Math.floor(157 * alpha);
	    var commonDarkerGreen = Math.floor(181 * alpha);
	    var commonDarkerBlue = Math.floor(251 * alpha);
	    borderColor = `rgb(${commonDarkerRed}, ${commonDarkerGreen}, ${commonDarkerBlue})`;
	}
	    
	//Data set content
	const dataset = {
		label: trackName,
	    backgroundColor: backgroundColor,
	    borderColor: borderColor,
	    data: data
	};
	
	datasets.push(dataset);
});
	
//Break subject labels for responsive names
$.each(subjectLabels, function(index, item){
	labels.push(item.split(/[\s/]+/));
});
	
//Session attendance bar chart
const sessionAttendancectx = document.getElementById("session-attendance-chart").getContext('2d');
const sessionAttendanceChart = new Chart(sessionAttendancectx, {
	type: 'bar',
	data: {
	    labels: labels,
	    datasets: datasets
	},
	options: {
	  	title: {
	        position: 'left',
	        display: true,
	        text: 'Total Count'
	    },
	    scales: {
	      	yAxes: [{
	        	ticks: {
	          		beginAtZero: true,
	        	},
	        	stacked: true,
	      	}],
	      	xAxes: [{
	            stacked: true,
	        }]
	    },
		tooltips: {
	    	callbacks: {
	      		label: function (tooltipItem, data) {
	        		return tooltipItem.yLabel;
	      		}
	    	}
	  	}
	}
});
//----------*****---------- End Session Attendance Chart ---------*****-----------//

//----------*****---------- Video Attempt Chart ----------*****----------//
var videoAttemptJsonData = $('#video-attempt-count').text();
var videoAttemptData = '';
var subjectNames = [];
var totalAttempts = [];
var labels = [];
var label;
var backgroundColor;
var borderColor;

//If student from current cycle
videoAttemptData = JSON.parse(videoAttemptJsonData);

$.each(videoAttemptData, function(subjectName, subjectData) {
	subjectNames.push(subjectName);
	totalAttempts.push(subjectData.subject_total_attempt);
});
	
//Break subject labels for responsive names
$.each(subjectNames, function(index, item){
	labels.push(item.split(/[\s/]+/));
});

// Function to check if all values in the array are zero
function checkAllZeros(attempt) {
    for (var i = 0; i < attempt.length; i++) {
        if (attempt[i] !== "0") {
            return false;
        }
    }
    return true;
}

var allZeros = checkAllZeros(totalAttempts);

//Data set content
if(!allZeros){
	label = 'Total View';
	backgroundColor = 'rgba(157, 181, 251, 1)';
	borderColor = 'rgb(18, 35, 158)';
}else{
	label = 'Data Not Found';
	backgroundColor = 'rgba(229,229,229, 1)';
	borderColor = 'rgb(206,206,206)';
}
	
//Video attempt count chart
const videoctx = document.getElementById("video-attempt-chart").getContext('2d');
const videoChart = new Chart(videoctx, {
	type: 'bar',
	data: {
	   labels: labels,
	   datasets: [{
		   label: label,
		   backgroundColor: backgroundColor,
		   borderColor: borderColor,
		   data: totalAttempts,
	   }]
	},
	options: {
		title: {
			position: 'left',
	        display: true,
	        text: 'Total View Count'
	    },
	    scales: {
	    	yAxes: [{
	    		ticks: {
	              	beginAtZero: true,
	            }
	        }]
	    },
	    tooltips: {
	    	callbacks: {
	      		label: function (tooltipItem, data) {
	        		return tooltipItem.yLabel;
	      		}
	    	}
	  	}
	}
});
//----------*****---------- End Video Attempt Chart ----------*****----------//

//----------*****---------- Learning Resources Report ----------*****----------//
$(document).ready(function(){
	$('.contentFileReport').DataTable();
});
//----------*****---------- End Learning Resources Report ----------*****----------//
/*]]>*/