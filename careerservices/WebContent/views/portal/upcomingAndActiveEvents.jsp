
	<div id="upcomingEvents" class="card-special">
		<div class="card mb-3">
			<div class="card-header card-special">
				<div class="row px-2 align-items-center">
					<h2 class="text-center material-icon-containter"><i class="material-icons" style="font-size: 110%"> date_range</i> <span class="">&nbsp;&nbsp;Events</span></h2>
				</div>
			</div>
			<div class="card-body p-0">
				<div id="activeEventsList" class="card-special">
				</div>
				<ul id="upcomingEventsList" class="list-unstyled">
					<li>
						<div class="mx-3 py-2">
							<span>You have no upcoming Sessions!</span>
						</div>
					</li>
				</ul>
			</div>
		</div>
	</div>

	<script>


	var allActiveEvents = Array();
	var allViewedEvents = Array();

	function etaToDate(eventDateStr){
		var etaString = "";
		var curDate = new Date();
		var eventDate = new Date(eventDateStr);

		var monthsLeft = eventDate.getMonth() - curDate.getMonth() ;
		var daysLeft = eventDate.getDate() - curDate.getDate();
		var hoursLeft = eventDate.getHours() - curDate.getHours();
		var minutesLeft = eventDate.getMinutes() - curDate.getMinutes();

		if(minutesLeft < 0){
			minutesLeft *= -1;
			minutesLeft = 60 - minutesLeft;
			hoursLeft = hoursLeft - 1;
		}

		if(hoursLeft < 0){
			hoursLeft *= -1;
			hoursLeft = 24 - hoursLeft;
			daysLeft = daysLeft - 1;
		}

		if(daysLeft < 0){
			daysLeft = 0;
		}

		if(curDate > eventDate) {
			return "Started";
		}

		if(monthsLeft > 0) {
			if(monthsLeft > 1) {
				etaString += monthsLeft + " Months ";
			}else{
				etaString += monthsLeft + " Month, ";
			}
		}
		if(daysLeft > 0) {
			if(daysLeft > 2) {
				etaString += daysLeft + " Days ";
			}else if(daysLeft > 1) {
				etaString += daysLeft + " Days, ";
			}else if(monthsLeft == 0 && daysLeft > 0){
				etaString += daysLeft + " Day, ";
			}else{
				etaString += daysLeft + " Day";
			}
		}

		if(daysLeft < 2 && monthsLeft < 1) {
			if(hoursLeft > 4) {
				etaString += hoursLeft + " Hours";
			}else if(hoursLeft > 0){
				etaString += hoursLeft + " Hours, ";
			}
			if(daysLeft == 0 && hoursLeft < 4){
				etaString += minutesLeft + " Minutes";
			}
		}
		return etaString;
	}

	function getDateDay(dateString){
		var days = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
		var d = new Date(dateString);
		var dayName = days[d.getDay()];

		return dayName;
	}
	function getDateMonthName(dateString){
		var days = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
		var d = new Date(dateString);
		var dayName = days[d.getMonth()];

		return dayName;
	}



	var totalUpcoming = 0;
	var upcomingCount = 0;
	function upcomingEvents(upcomingEvents){
		var events = "";
		totalUpcoming = upcomingEvents.length;
		upcomingEvents.forEach(function(event){
			upcomingCount++;
			if(upcomingCount <= 3){
				events = getEventUpcoming(event) + events;
			}
		});
		if(totalUpcoming != 0){
			$("#upcomingEventsList").html(events);
		}
	}

	function getEventUpcoming(event){

		var eventId = event["id"];
		var eventName = event["sessionName"];
		var facultyName = event["facultyName"];
		var facultyId = event["facultyId"];
		var eventImageURL = event["facultyImageURL"];
		var eventDateStr = event["date"] + " " + event["startTime"];
		console.log(eventDateStr);
		var eta = etaToDate(eventDateStr);
		if(eta == "Started"){
			upcomingCount --;
			totalUpcoming --;
			return "";
		}
		var newClass = "";
		if(upcomingCount < 3 && upcomingCount != totalUpcoming ){
			newClass = "border-bottom";
		}
		var event = 
			`
		<li class="media py-3 ` + newClass + `">
			<div class="thumbnail-container col-3">
				<img class="rounded-circle img-thumbnail p-0 w-100" onError="this.onerror=null;this.src='assets/placeholder.png';" src="` + eventImageURL + `">
			</div>
			<div class="media-body col-9">
				<a href="/careerservices/viewScheduledSession?id=` + eventId + `">
					<p style="font-size: larger; font-weight: bold; color: #26a9e0; margin-bottom: 0px;">` + eventName + `</p>
				</a>
				<div class="clearfix"></div>
				<small>
					by
					<a href="/careerservices/speakerProfile?id=` + facultyId + `">
						` + facultyName + `
					</a>
				</small>
				<div class="clearfix"></div>
				<div class="pt-2" >`
					+(getTimeStr(eventDateStr)) + `
				</div>
				<div class="clearfix"></div>
				<small class="text-muted">Starts
					in ` + eta + `
				</small>
			</div>
		</li>`;

		return event;
	}

	function getTimeStr(eventDateStr){
		var date = new Date(eventDateStr);
		var ampm= 'am';
		var h = date.getHours();
		var m = date.getMinutes();
		if(h>= 12){
			if(h>12) h -= 12;
			ampm= 'pm';
		}

		if(m<10) m= '0'+m;
		if(h<10) h= '0'+h;
		return getDateDay(eventDateStr) + ', ' + date.getDate() + ' ' + getDateMonthName(eventDateStr) + ' ' + h + ':' + m + ' ' + ampm;
	}

	var totalActive = 0;
	var activeCount = 0;
	function activeEvents(activeEvents){

		var activeEventTop = `
					<ul id="activeEventsList" class="list-unstyled">
				`;

		var events = "";
		totalActive = activeEvents.length;

		activeEvents.forEach(function(event){
			allActiveEvents.push(event);
			activeCount ++;
			events += getEventActive(event);
		});

			var activeEventBottom = `
				</ul>`;

			if(activeCount != 0){
			$("#activeEventsList").html(activeEventTop + events + activeEventBottom);
			}
	}
	var activationInfos = Array();
	function getEventActive(event){

		console.log(event);

		var eventId = event["id"];
		var eventName = event["sessionName"];
		var facultyName = event["facultyName"];
		var facultyId = event["facultyId"];
		var eventImageURL = event["facultyImageURL"];
		activationInfos[eventId] = event["activationInfo"];
		var newClass = "";

		var event = `
		<li class="row mx-0 my-1 pt-2 rounded-left side-border-card card-secondary ">
			<div class="thumbnail-container col-4">
				<img class="rounded-circle img-thumbnail p-0 w-100" onError="this.onerror=null;this.src='assets/placeholder.png';" src="` + eventImageURL + `">
			</div>
			<div class="col-8">
				<a href="/careerservices/viewScheduledSession?id=` + eventId + `">
					<p style="font-size: larger; font-weight: bold; color: #26a9e0; margin-bottom: 0px;">` + eventName + `</p>
				</a>
				<div class="clearfix"></div>
				<small>
					by
					<a href="/careerservices/speakerProfile?id=` + facultyId + `">
						` + facultyName + `
					</a>
				</small>

			</div>
			<div class="col-12 text-center">
				<button class="btn btn-primary" onclick = activateEvent(` + eventId + `)>Join</button>
			</div>
		</li>`;


		return event;
	}
	function activateEvent(eventId){
		var active = false;
		var viewed = false;
		var hasVideoContent = false;

		var videoContent;
		allActiveEvents.forEach(function(data){
			if(data.id == eventId){
				active = true;
			}
		});
		allViewedEvents.forEach(function(data){
			if(data.id == eventId){
				viewed = true;
			}
			if("videoContent" in data){
				hasVideoContent = true;
				videoContent = data.videoContent;
			}
		});

		activationInfo = activationInfos[eventId];

		if(!viewed){
			var nextActivationDateStr = activationInfo["nextActivationAvailableDate"];
			var nextActivationDate = new Date(nextActivationDateStr);
			activationInfo["nextActivationPossible"] = nextActivationDate.toDateString();

			var nextActivation = "You will get another activation on " + activationInfo.nextActivationPossible;
			if(activationInfo.nextActivationPossible == "Invalid Date"){
				nextActivation = "You will <b>Not</b> get any more activations for this package";
			}
			$.confirm({
				icon: 'fa fa-exclamation-triangle',
				title: 'Confirm Action',
				theme: 'material',
				type: 'orange',
				backgroundDismiss: true,
				columnClass: 'col-lg-6 col-lg-offset-3 col-md-8 col-md-offset-2 col-sm-12',
				content: `
					This is a session of <b>` + activationInfo.featureName + `</b> under your purchased package <b>` + activationInfo.packageName + `</b>.
					<br>Activating this session will <b>consume</b> an activation.
					<br>You currently have <b>` + activationInfo.activationsLeft + `/` + activationInfo.totalActivations + `</b> activations left.
					<br>You can activate <b>` + activationInfo.activationsPossible + `</b> sessions right now.
					<br>` + nextActivation + `.
					<br>
					<br>This package started on <b>` + activationInfo.packageStartDate + `</b> and will be active till <b>` + activationInfo.packageEndDate + `</b>.
				`,
				buttons: {
					Yes: {
						text: 'Proceed',
						btnClass: 'btn btn-primary',
						action: function(){
							if(active){
								var url = "attendScheduledSession?id=" + eventId;
								window.location.href = url;
							}else{
								var url = "addAttendanceForPreviousSession?id=" + eventId;
								window.location.href = url;
							}
						}
					},
					No: {
						text: 'Back',
						btnClass: 'btn btn-secondary',
						action: function(){

						}
					}
				}
			});
		}else{
			if(active){
				var url = "attendScheduledSession?id=" + eventId;
				window.location.href = url;
			}else{
				if(hasVideoContent){
					var url = "viewScheduledSession?id=" + eventId;
					window.location.href = url;
				}
			}
		}
	}
</script>