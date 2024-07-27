var flowTabPane = document.getElementById("flow-tab-pane");
var flowVideoTabPane = document.getElementById("flowVideo-tab-pane");
var flowQuizTabPane = document.getElementById("flowQuiz-tab-pane");

function goBack() {
	// Go back to the flow page
	flowTabPane.classList.remove("d-none");
	flowVideoTabPane.classList.add("d-none");
	flowQuizTabPane.classList.add("d-none");
	localStorage.removeItem('moduleIdPg');
	var url = window.location.href;

	// Parse the URL
	var urlObj = new URL(url);
	var urlParams = new URLSearchParams(urlObj.search);

	// Update the parameter value
	urlParams.set('activeMenu', 'flow');

	// Generate the updated URL
	urlObj.search = urlParams.toString();
	var updatedUrl = urlObj.toString();

	window.history.replaceState(null, '', updatedUrl);
}

function showAllVideos(id) {
	var url = window.location.href;
	localStorage.setItem('moduleIdPg', id);
	// Parse the URL
	var urlObj = new URL(url);
	var urlParams = new URLSearchParams(urlObj.search);

	// Update the parameter value
	urlParams.set('activeMenu', 'flowVideo');

	// Generate the updated URL
	urlObj.search = urlParams.toString();
	var updatedUrl = urlObj.toString();

	window.history.replaceState(null, '', updatedUrl);

	flowTabPane.classList.add("d-none");
	flowVideoTabPane.classList.remove("d-none");
	flowQuizTabPane.classList.add("d-none");

	updateCards(id);

}

function showAllQuizes(id) {
	var url = window.location.href;
	localStorage.setItem('moduleIdPg', id);
	// Parse the URL
	var urlObj = new URL(url);
	var urlParams = new URLSearchParams(urlObj.search);

	// Update the parameter value
	urlParams.set('activeMenu', 'flowQuiz');

	// Generate the updated URL
	urlObj.search = urlParams.toString();
	var updatedUrl = urlObj.toString();

	window.history.replaceState(null, '', updatedUrl);

	flowTabPane.classList.add("d-none");
	flowVideoTabPane.classList.add("d-none");
	flowQuizTabPane.classList.remove("d-none");

	updateQuiz(id);
}

window.onload = function() {
	
	if (activeMenuClass === "flowVideo") {
		flowVideoTabPane.classList.remove("d-none");
	}else if (activeMenuClass === "flowQuiz") {
		flowQuizTabPane.classList.remove("d-none");
	}else{
		fetchDataAndProcess();
        flowTabPane.classList.remove("d-none");
        localStorage.removeItem('moduleIdPg');
	}
}

function updateCards(id) {
	var xhr = new XMLHttpRequest();
	xhr.open('GET', '/acads/m/getSessionPlanModuleVideos/' + id, true);
	xhr.onreadystatechange = function() {
		if (xhr.readyState === 4 && xhr.status === 200) {
			var response = JSON.parse(xhr.responseText);
			updateCardsUI(response); // Update the cards with the response data
		}
	};
	xhr.send();

	function updateCardsUI(data) {
		var cardContainer = document.getElementById('cardView');
		cardContainer.innerHTML = '';

		if (data.length === 0) {
			// Show the "No Video Content Available" message
			var noDataMessage = document.createElement('div');
			noDataMessage.classList.add('no-data-wrapper');

			var noDataText = document.createElement('h6');
			noDataText.classList.add('no-data', 'nodata', 'fw-bold', 'mb-5',
					'mt-5');
			noDataText.innerHTML = '<i class="far fa-circle-play"></i> No Video Content Available.';

			noDataMessage.appendChild(noDataText);
			cardContainer.appendChild(noDataMessage);
		} else {
			// Generate cards for each video item
			for (var i = 0; i < data.length; i++) {
				var item = data[i];

				var card = document.createElement('div');
				card.classList.add('col-md-6', 'col-sm-6', 'col-lg-6', 'col-xl-3', 'py-4');

				var cardLink = document.createElement('a'); // Create the anchor tag
				cardLink.href = '/acads/student/watchPGSessionVideos?id=' + item.id +
				                '&programSemSubjectId=' + programSemSubjectId +
				                '&sessionPlanModuleId=' + item.sessionPlanModuleId;
				cardLink.target = '_blank';
				card.appendChild(cardLink); // Append the anchor tag as a parent of the card

				var cardContent = document.createElement('div');
				cardContent.classList.add('d-flex', 'align-items-center', 'justify-content-center');

				var cardWrapper = document.createElement('div');
				cardWrapper.classList.add('card', 'shadow', 'bg-body', 'w-80', 'h-140');
				cardWrapper.style.width = '18rem';

				var cardImage = document.createElement('img');
				cardImage.classList.add('card-img-top');
				cardImage.src = item.thumbnailUrl;
				cardImage.alt = '...';
				cardImage.style.height = '180px';

				var cardBody = document.createElement('div');
				cardBody.classList.add('card-body');

				var rowDiv = document.createElement('div');
				rowDiv.classList.add('row');

				var colDiv1 = document.createElement('div');
				colDiv1.classList.add('col-md-10');

				var cardTitle = document.createElement('h4');
				var cardTitleLink = document.createElement('a'); // Create the anchor tag for card title
				cardTitleLink.href = '/acads/student/watchPGSessionVideos?id=' + item.id +
				                    '&programSemSubjectId=' + programSemSubjectId +
				                    '&sessionPlanModuleId=' + item.sessionPlanModuleId;
				cardTitleLink.target = '_blank';
				var cardTitleText = document.createElement('b');
				cardTitleText.textContent = item.fileName;
				cardTitleLink.appendChild(cardTitleText);
				cardTitle.appendChild(cardTitleLink);

				var cardFaculty = document.createElement('h6');
				cardFaculty.textContent = item.facultyName;

				var cardAddedOn = document.createElement('h6');
				cardAddedOn.textContent = item.addedOn;

				var colDiv2 = document.createElement('div');
				colDiv2.classList.add('col-md-2', 'mt-3');

				colDiv1.appendChild(cardTitle);
				colDiv1.appendChild(cardFaculty);
				colDiv1.appendChild(cardAddedOn);

				rowDiv.appendChild(colDiv1);
				rowDiv.appendChild(colDiv2);

				cardBody.appendChild(rowDiv);

				cardWrapper.appendChild(cardImage);
				cardWrapper.appendChild(cardBody);

				cardContent.appendChild(cardWrapper);

				cardLink.appendChild(cardContent); // Append the card content as a child of the anchor tag

				cardContainer.appendChild(card);
			}
		}
	}
}

document.addEventListener('DOMContentLoaded', function() {
	var moduleIdPg = localStorage.getItem('moduleIdPg');
	updateCards(moduleIdPg);
	updateQuiz(moduleIdPg);
});

$(document)
		.ready(
				function() {
					$(
							'.nav-link[data-bs-target="#querise-tab-pane"],.nav-link[data-bs-target="#sessions-tab-pane"], .nav-link[data-bs-target="#tool-tab-pane"], .nav-link[data-bs-target="#resources-tab-pane"], .nav-link[data-bs-target="#assignment-tab-pane"], .nav-link[data-bs-target="#results-tab-pane"],.nav-link[data-bs-target="#fourm-tab-pane"],.nav-link[data-bs-target="#casestudy-tab-pane"]')
							.on('shown.bs.tab', function() {
								$('#flowVideo-tab-pane').hide();
								$('#flowQuiz-tab-pane').hide();
							});

					$('.nav-link[data-bs-target="#flow-tab-pane"]').on(
							'shown.bs.tab', function() {
								// Display an alert when the flowVideo-tab-pane
								// is toggled
								// Show the flowVideo-tab-pane
								$('#flowVideo-tab-pane').show();
								$('#flowQuiz-tab-pane').show();
							});
				});
 

function updateQuiz(id) {
	 var xhr = new XMLHttpRequest();
	    xhr.open('GET', '/internal-assessment/m/student/moduleQuizDetails?sessionPlanModuleId=' +
	        id + '&programSemSubjectId=' + programSemSubjectId, true);
	    xhr.onreadystatechange = function () {
	        if (xhr.readyState === 4) {
	            if (xhr.status === 200) {
	                var response = JSON.parse(xhr.responseText);
	                if (response.data.quiz.length !== 0 && response.data.eligible === true) {
	                	updateQuizRows(response); // Update the cards with the response data
	                }else{
	                	handleNoQuizAvailable();
	                }
	            } else {
	                handleNoQuizAvailable();
	            }
	        }
	    };
	    xhr.onerror = function () {
	        handleNoQuizAvailable();
	    };
	    xhr.send();

	    function handleNoQuizAvailable() {
	    	var quizDiv = document.getElementById('quiz');
	    	quizDiv.innerHTML = '';
	    	
	    	 var tableBody = document.getElementById('quizBody');
	         tableBody.innerHTML = '';
	        var noDataMessage = document.createElement('div');
	        noDataMessage.classList.add('no-data-wrapper');

	        var noDataText = document.createElement('h6');
	        noDataText.classList.add('no-data', 'nodata', 'fw-bold', 'mb-5', 'mt-5');
	        noDataText.innerHTML = '<i class="fa-solid fa-triangle-exclamation"></i> Data Unavailable - Please try again later.';
	        noDataMessage.appendChild(noDataText);
	        quizDiv.appendChild(noDataMessage);

	    }


    function updateQuizRows(data) {
        var tableBody = document.getElementById('quizBody');
        tableBody.innerHTML = ''; // Clear existing table rows
       
        var quizDiv = document.getElementById('quiz');
        quizDiv.innerHTML = '';
            // Generate table rows for each quiz item
            var tableRow = document.createElement("tr");

            var cell1 = document.createElement("td");
            cell1.textContent = data.data.quiz[0].name;
            tableRow.appendChild(cell1);

            var cell2 = document.createElement("td");
            cell2.textContent = data.data.quiz[0].startDate.replace('T', ' ');;
            tableRow.appendChild(cell2);

            var cell3 = document.createElement("td");
            cell3.textContent = data.data.quiz[0].endDate.replace('T', ' ');
            tableRow.appendChild(cell3);

            var cell4 = document.createElement("td");
            cell4.textContent = data.data.quiz[0].maxAttempt;
            tableRow.appendChild(cell4);

            var cell5 = document.createElement("td");
            var starIcon = document.createElement("i");
            starIcon.className = "fa-sharp fa-solid fa-star";

            var starLink = document.createElement("a");
            starLink.href = "/internal-assessment/viewTestDetailsForStudentsForAllViews?userId=" + userId + "&id=" + data.data.quiz[0].id + "&message=" + data.message + "&consumerProgramStructureId=" + consumerProgramStructureId;
            starLink.target = "_blank";
            starLink.appendChild(starIcon);

            cell5.appendChild(starLink);
            tableRow.appendChild(cell5);

            // Append the table row to the table body
            tableBody.appendChild(tableRow);
    }
}

function fetchDataAndProcess() {
    const flowTab = document.querySelector('[data-bs-target="#flow-tab-pane"]');
    const divElement = document.querySelector('div#video');
        fetch('/acads/m/getSessionPlanPgWatchedVideosData?sessionPlanId='+sessionPlanId+'&sapId='+userId)
            .then(response => response.json())
            .then(data => {
                data.forEach(item => {
                	var div = document.getElementById(item.sessionPlanModuleId);
                    var attendedCount = item.watchedVideosCount !== null ? item.watchedVideosCount : '0';
                    var totalCount = item.totalVideosCount !== null ? item.totalVideosCount : 'N/A';
                    var div = document.getElementById(item.sessionPlanModuleId);
                    div.textContent = totalCount == 'N/A' ? 'Watched - ' + totalCount : 'Watched - ' + attendedCount + '/' + totalCount;
                });

            })
            .catch(error => {
                console.log("Error occurred during the AJAX call:", error);
            });
}


