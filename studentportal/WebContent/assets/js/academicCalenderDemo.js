function notifyLead(){
	alert("Please enrole for the complete program...");
}


var elements = document.querySelectorAll(".myField");
for (var i=0; i < elements.length; i++) {
	document.getElementsByClassName("myDate")[i].innerHTML = moment(elements[i].value).format("DD-MMM-YYYY");
	document.getElementsByClassName("myTime")[i].innerHTML = moment(elements[i].value).format("hh:mm:ss A");
}
