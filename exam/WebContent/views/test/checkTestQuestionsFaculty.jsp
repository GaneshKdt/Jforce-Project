
<!DOCTYPE html>
<html lang="en">

<%@page import="com.nmims.beans.Person"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Check Test Questions" name="title" />
</jsp:include>


<link
	href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css"
	rel="stylesheet">
<script src="https://code.jquery.com/jquery-3.3.1.js"
	integrity="sha256-2Kok7MbOyxpgUVvAk/HJ2jigOSYS2auK4Pfzbm7uH60="
	crossorigin="anonymous"></script>
<script
	src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>

<style>
.label-success {
	background-color: #66BB6A;
}

.label-success[href]:focus, .label-success[href]:hover {
	background-color: #49a54e;
}

.left_status {
	background-color: #337ab7;
}

.skipped_status {
	background-color: #a94442;
}

.prevnext {
	float:right;
	text-decoration: none;
	display: inline-block;
	padding: 8px 16px;
	
}

.prevnext:hover {
	background-color: #ddd;
	color: black;
}

.previous {
	background-color: white;
	color: black;
	margin-right: 15px;
}

.next {
	background-color: #a94442;
	color: white;
}

.round {
	border-radius: 50%;
}

.list-group {
	padding-left: 0;
	margin-bottom: 0;
}

.list-group-item {
	position: relative;
	display: block;
	padding: .75rem 1.25rem;
	margin-bottom: -1px;
	background-color: #fff;
	border: 1px solid #eee;
}

.card-title {
	margin-bottom: 0.75rem;
}

body {
	font-family: Roboto, "Helvetica Neue", Arial, Helvetica, sans-serif;
	font-size: 1rem;
	line-height: 1.5;
	color: #373a3c;
	background-color: #f9f9f9;
}

.btn {
	display: inline-block;
	font-weight: normal;
	text-align: center;
	white-space: nowrap;
	vertical-align: middle;
	cursor: pointer;
	-webkit-user-select: none;
	-moz-user-select: none;
	-ms-user-select: none;
	user-select: none;
	border: 1px solid transparent;
	padding: 4px 10px;
	font-size: 1rem;
	line-height: 1.5;
	border-radius: 0;
}

.btn:focus, .btn.focus, .btn:active:focus, .btn:active.focus, .btn.active:focus,
	.btn.active.focus {
	outline: thin dotted;
	outline: 5px auto -webkit-focus-ring-color;
	outline-offset: -2px;
}

.btn:focus, .btn:hover {
	text-decoration: none;
}

.btn.focus {
	text-decoration: none;
}

.btn:active, .btn.active {
	background-image: none;
	outline: 0;
}

.btn.disabled, .btn:disabled {
	cursor: not-allowed;
	opacity: .65;
}

.btn-rounded {
	border-radius: 5px;
}

.pull-xs-right {
	float: right !important;
}

.btn-success {
	color: #fff;
	background-color: #66BB6A;
	border-color: #66BB6A;
}

.btn-success:hover {
	color: #fff;
	background-color: #49a54e;
	border-color: #469e4a;
}

.btn-success:focus, .btn-success.focus {
	color: #fff;
	background-color: #49a54e;
	border-color: #469e4a;
}

.btn-success:active, .btn-success.active, .open>.btn-success.dropdown-toggle
	{
	color: #fff;
	background-color: #49a54e;
	border-color: #469e4a;
	background-image: none;
}

.btn-success:active:hover, .btn-success:active:focus, .btn-success:active.focus,
	.btn-success.active:hover, .btn-success.active:focus, .btn-success.active.focus,
	.open>.btn-success.dropdown-toggle:hover, .open>.btn-success.dropdown-toggle:focus,
	.open>.btn-success.dropdown-toggle.focus {
	color: #fff;
	background-color: #3e8c42;
	border-color: #327035;
}

.btn-success.disabled:focus, .btn-success.disabled.focus, .btn-success:disabled:focus,
	.btn-success:disabled.focus {
	background-color: #66BB6A;
	border-color: #66BB6A;
}

.btn-success.disabled:hover, .btn-success:disabled:hover {
	background-color: #66BB6A;
	border-color: #66BB6A;
}

.btn-white, .btn-default {
	color: #818a91;
	background-color: #fff;
	border-color: #eceeef;
}

.card-footer {
	padding: 0.75rem 1.25rem;
	background-color: #f5f5f5;
	border-top: 1px solid #eee;
}

.card .card-header, .card .card-footer {
	padding: .625rem;
	color: rgba(0, 0, 0, 0.84);
}

.card .card-header .media, .card .card-footer .media {
	line-height: inherit;
}

.card-footer:last-child {
	border-radius: 0 0 3px 3px;
}

.card .card-title {
	font-size: 1.25rem;
	margin-bottom: .625rem;
}

.card .card-subtitle {
	color: #818a91;
}

.card .card-title+.card-subtitle {
	margin-top: -.625rem;
}

.card .card-header .card-title, .card .card-header .card-subtitle {
	margin: 0 !important;
}

.card .card-header .media-body {
	line-height: 1.5;
}

.card-red {
	background-color: #F44336;
	border-color: #F44336;
}

.card-success .progress[value]::-webkit-progress-value {
	background-color: #469e4a;
}

.card-success .progress[value]::-moz-progress-bar {
	background-color: #469e4a;
}

.c-input {
	position: relative;
	display: inline;
	padding-left: 1.5rem;
	color: #555;
	cursor: pointer;
}

.c-input>input {
	position: absolute;
	z-index: -1;
	opacity: 0;
}

.c-input>input:checked ~.c-indicator {
	color: #fff;
	background-color: #d2232a;
}

.c-input>input:focus ~.c-indicator {
	box-shadow: 0 0 0 .075rem #fff, 0 0 0 .2rem #0074d9;
}

.c-input>input:active ~.c-indicator {
	color: #fff;
	background-color: #84c6ff;
}

.c-input+.c-input {
	margin-left: 1rem;
}

.c-indicator {
	position: absolute;
	top: 0;
	left: 0;
	display: block;
	width: 1rem;
	height: 1rem;
	font-size: 65%;
	line-height: 1rem;
	color: #eee;
	text-align: center;
	-webkit-user-select: none;
	-moz-user-select: none;
	-ms-user-select: none;
	user-select: none;
	background-color: #eee;
	background-repeat: no-repeat;
	background-position: center center;
	background-size: 50% 50%;
}

.c-checkbox .c-indicator {
	border-radius: .25rem;
}

.c-checkbox input:checked ~.c-indicator {
	background-image:
		url(data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxNy4xLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjwhRE9DVFlQRSBzdmcgUFVCTElDICItLy9XM0MvL0RURCBTVkcgMS4xLy9FTiIgImh0dHA6Ly93d3cudzMub3JnL0dyYXBoaWNzL1NWRy8xLjEvRFREL3N2ZzExLmR0ZCI+DQo8c3ZnIHZlcnNpb249IjEuMSIgaWQ9IkxheWVyXzEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHg9IjBweCIgeT0iMHB4Ig0KCSB2aWV3Qm94PSIwIDAgOCA4IiBlbmFibGUtYmFja2dyb3VuZD0ibmV3IDAgMCA4IDgiIHhtbDpzcGFjZT0icHJlc2VydmUiPg0KPHBhdGggZmlsbD0iI0ZGRkZGRiIgZD0iTTYuNCwxTDUuNywxLjdMMi45LDQuNUwyLjEsMy43TDEuNCwzTDAsNC40bDAuNywwLjdsMS41LDEuNWwwLjcsMC43bDAuNy0wLjdsMy41LTMuNWwwLjctMC43TDYuNCwxTDYuNCwxeiINCgkvPg0KPC9zdmc+DQo=);
}

.c-checkbox input:indeterminate ~.c-indicator {
	background-color: #0074d9;
	background-image:
		url(data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxNy4xLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjwhRE9DVFlQRSBzdmcgUFVCTElDICItLy9XM0MvL0RURCBTVkcgMS4xLy9FTiIgImh0dHA6Ly93d3cudzMub3JnL0dyYXBoaWNzL1NWRy8xLjEvRFREL3N2ZzExLmR0ZCI+DQo8c3ZnIHZlcnNpb249IjEuMSIgaWQ9IkxheWVyXzEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHg9IjBweCIgeT0iMHB4Ig0KCSB3aWR0aD0iOHB4IiBoZWlnaHQ9IjhweCIgdmlld0JveD0iMCAwIDggOCIgZW5hYmxlLWJhY2tncm91bmQ9Im5ldyAwIDAgOCA4IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxwYXRoIGZpbGw9IiNGRkZGRkYiIGQ9Ik0wLDN2Mmg4VjNIMHoiLz4NCjwvc3ZnPg0K);
}

.c-radio .c-indicator {
	border-radius: 50%;
}

.c-radio input:checked ~.c-indicator {
	background-image:
		url(data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxNy4xLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjwhRE9DVFlQRSBzdmcgUFVCTElDICItLy9XM0MvL0RURCBTVkcgMS4xLy9FTiIgImh0dHA6Ly93d3cudzMub3JnL0dyYXBoaWNzL1NWRy8xLjEvRFREL3N2ZzExLmR0ZCI+DQo8c3ZnIHZlcnNpb249IjEuMSIgaWQ9IkxheWVyXzEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHg9IjBweCIgeT0iMHB4Ig0KCSB2aWV3Qm94PSIwIDAgOCA4IiBlbmFibGUtYmFja2dyb3VuZD0ibmV3IDAgMCA4IDgiIHhtbDpzcGFjZT0icHJlc2VydmUiPg0KPHBhdGggZmlsbD0iI0ZGRkZGRiIgZD0iTTQsMUMyLjMsMSwxLDIuMywxLDRzMS4zLDMsMywzczMtMS4zLDMtM1M1LjcsMSw0LDF6Ii8+DQo8L3N2Zz4NCg==);
}

.c-inputs-stacked .c-input {
	display: inline;
}

.c-inputs-stacked .c-input::after {
	display: block;
	margin-bottom: .25rem;
	content: "";
}

.c-inputs-stacked .c-input+.c-input {
	margin-left: 0;
}

.c-select {
	display: inline-block;
	max-width: 100%;
	padding: .375rem 1.75rem .375rem .75rem;
	padding-right: .75rem \9;
	color: #55595c;
	vertical-align: middle;
	background: #fff
		url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAUCAMAAACzvE1FAAAADFBMVEUzMzMzMzMzMzMzMzMKAG/3AAAAA3RSTlMAf4C/aSLHAAAAPElEQVR42q3NMQ4AIAgEQTn//2cLdRKppSGzBYwzVXvznNWs8C58CiussPJj8h6NwgorrKRdTvuV9v16Afn0AYFOB7aYAAAAAElFTkSuQmCC)
		no-repeat right 0.75rem center;
	background-image: none \9;
	background-size: 8px 10px;
	border: 1px solid #eceeef;
	-moz-appearance: none;
	-webkit-appearance: none;
}

.c-select:focus {
	border-color: #51a7e8;
	outline: none;
}

.c-select::-ms-expand {
	opacity: 0;
}

.c-select-sm {
	padding-top: 3px;
	padding-bottom: 3px;
	font-size: 12px;
}

.c-select-sm
:not
 
(
[
multiple
]
 
)
{
height
:
 
26
px
;

	
min-height
:
 
26
px
;


}
.form-group {
	margin-bottom: 1rem;
}

.media-left, .media-right, .media-body {
	display: table-cell;
	vertical-align: top;
}

.media-left {
	padding-right: 10px;
}

.media-middle {
	vertical-align: middle;
}

.m-b-0 {
	margin-bottom: 0 !important;
}

.bg-white {
	background-color: #ffffff;
}

.p-a-1 {
	padding: 1rem 1rem !important;
}

.card-header {
	padding: 0.75rem 1.25rem;
	background-color: #f5f5f5;
	border-bottom: 1px solid #eee;
}

.container-fluid {
	margin-left: auto;
	margin-right: auto;
	padding-left: 0.625rem;
	padding-right: 0.625rem;
}

.card {
	position: relative;
	display: block;
	margin-bottom: 0.75rem;
	background-color: #fff;
	border: 1px solid #eee;
	border-radius: 3px;
}

.card-block {
	padding: 1.25rem;
}

.center {
	text-align: center;
}

@media ( min-width : 544px) {
	.card-group {
		display: table;
		width: 100%;
		table-layout: fixed;
	}
	.card-group .card {
		display: table-cell;
		vertical-align: top;
	}
	.card-group .card+.card {
		margin-left: 0;
		border-left: 0;
	}
	.card-group .card:first-child {
		border-bottom-right-radius: 0;
		border-top-right-radius: 0;
	}
	.card-group .card:first-child .card-img-top {
		border-top-right-radius: 0;
	}
	.card-group .card:first-child .card-img-bottom {
		border-bottom-right-radius: 0;
	}
	.card-group .card:last-child {
		border-bottom-left-radius: 0;
		border-top-left-radius: 0;
	}
	.card-group .card:last-child .card-img-top {
		border-top-left-radius: 0;
	}
	.card-group .card:last-child .card-img-bottom {
		border-bottom-left-radius: 0;
	}
	.card-group
	 
	.card
	:not
	 
	(
	:first-child
	 
	)
	:not
	 
	(
	:last-child
	 
	)
	{
	border-radius
	:
	 
	0;
}

.card-group .card:not (:first-child ):not (:last-child ) .card-img-top,
	.card-group .card:not (:first-child ):not (:last-child )
	.card-img-bottom {
	border-radius: 0;
}

}
.card-group {
	margin-bottom: 1.25rem;
}

.m-b-0 {
	margin-bottom: 0 !important;
}

.text-muted-light {
	color: #b7bdc1;
}

.answerDiv {
	display: none;
}

.showAnswerDiv {
	display: block;
	webkit-animation: fadein 0.5s;
	animation: fadein 0.5s;
}

.hideAnswerDiv {
	display: none;
	webkit-animation: fadeout 0.5s 2.5s;
	animation: fadeout 0.5s 2.5s;
}
/* The snackbar - position it at the bottom and in the middle of the screen */
#snackbar {
	visibility: hidden;
	/* Hidden by default. Visible on click */
	min-width: 250px;
	/* Set a default minimum width */
	margin-left: -125px;
	/* Divide value of min-width by 2 */
	background-color: #333;
	/* Black background color */
	color: #fff;
	/* White text color */
	text-align: center;
	/* Centered text */
	border-radius: 2px;
	/* Rounded borders */
	padding: 16px;
	/* Padding */
	position: fixed;
	/* Sit on top of the screen */
	z-index: 1;
	/* Add a z-index if needed */
	left: 50%;
	/* Center the snackbar */
	bottom: 30px;
	/* 30px from the bottom */
}

/* Show the snackbar when clicking on a button (class added with JavaScript) */
#snackbar.show {
	visibility: visible;
	/* Show the snackbar */
	/* Add animation: Take 0.5 seconds to fade in and out the snackbar. 
However, delay the fade out process for 2.5 seconds */
	-webkit-animation: fadein 0.5s, fadeout 0.5s 2.5s;
	animation: fadein 0.5s, fadeout 0.5s 2.5s;
}

/* Animations to fade the snackbar in and out */
@
-webkit-keyframes fadein {from { bottom:0;
	opacity: 0;
}

to {
	bottom: 30px;
	opacity: 1;
}

}
@
keyframes fadein {from { bottom:0;
	opacity: 0;
}

to {
	bottom: 30px;
	opacity: 1;
}

}
@
-webkit-keyframes fadeout {from { bottom:30px;
	opacity: 1;
}

to {
	bottom: 0;
	opacity: 0;
}

}
@
keyframes fadeout {from { bottom:30px;
	opacity: 1;
}

to {
	bottom: 0;
	opacity: 0;
}

}

.redLeftBorder{
	padding-left : 10px;
	border-left : 5px solid #C72033;
	
}
.greenLeftBorder{
	padding-left : 10px;
	border-left : 5px solid #49a54e;
	}

</style>

<body>

	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">


		<!-- Custom breadcrumbs as requirement is diff. Start -->
		<div class="sz-breadcrumb-wrapper">
			<div class="container-fluid">
				<ul class="sz-breadcrumbs">
					<li><a href="/exam/">Exam</a></li>
					<li><a href="#">Evaluate Test Answers</a></li>

				</ul>
				<ul class="sz-social-icons">
					<li><a href="https://www.facebook.com/NMIMSSCE"
						class="icon-facebook" target="_blank"></a></li>
					<li><a href="https://twitter.com/NMIMS_SCE"
						class="icon-twitter" target="_blank"></a></li>
					<!-- <li><a href="https://plus.google.com/u/0/116325782206816676798/posts" class="icon-google-plus" target="_blank"></a></li> -->

				</ul>
			</div>
		</div>
		<!-- Custom breadcrumbs as requirement is diff. End -->



		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">

						<h2 class="red text-capitalize">Evaluate Test Answers</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>

							<!-- Code For Page Goes in Here Start -->

									<c:choose>
									<c:when test="${fn:length(testsDetails) > 0}">

							<div class="container">
								
								<div class="panel-group" id="accordion">
									<c:forEach var="test" items="${testsDetails}" varStatus="tStatus" >
									<div class="panel panel-default">
										<div class="panel-heading" >
											<span class="panel-title">
												<a data-toggle="collapse" data-parent="#accordion"
													href="#collapse${test.id}"><b>${tStatus.count}.</b> ${test.testName} </a>
											</span>
											<span class="badge" id ="tBadge-${test.id}" style="margin-left: 10px;  padding : 5px;">
												${fn:length(testIdAndAnswersToCheckMap[test.id])}
											</span>
						    
										</div>
										<div id="collapse${test.id}" class="panel-collapse collapse">
											<div class="panel-body">
														
											<c:forEach var="answer" items="${testIdAndAnswersToCheckMap[test.id]}" varStatus="aStatus" >
												<div id="answerDiv-${test.id}-${aStatus.count}" class="answerDiv">
												<div class="card">
														<div id="title-${test.id}-${aStatus.count}" class="card-header bg-white p-a-1 redLeftBorder"
															style="background-color: #ffffff">
														<div id="prevNextDiv" class="">
																				
																				<a href="#" 
																					onclick="goToNext(${test.id},${aStatus.count})"
																					class="prevnext next  nextQuestionButton">
																				Next &raquo;
																				</a>
																				<a href="#"
																					onclick="goToPrevious(${test.id},${aStatus.count})"
																					class="prevnext previous  previousQuestionButton">
																				&laquo; Previous
																				</a>
															</div>
															<div  class="media ">
																<div class="media-body  media-middle">
																	<h4 class="card-title"
																		
																		class="">
																		<span class="">${aStatus.count}.
																		</span> <span>${answer.question}</span>
																	</h4>
																</div>
															</div>
														
														</div>
														
																	<div class="card-block p-a-2">
																		<div class="Jumbotron">
																			<h6>Answer:</h6>
																			<span>
																				${answer.answer}
																			</span>
																		</div>
																	</div>
														
																	<div class="card-footer clearfix "
																		style="background-color: #ffffff">
																		<div class="col-sm-12">
																			
																			<div class="col-sm-6">
																				 <div class="col-sm-4">
																				      <label for="sel1">Select Marks :</label>
																				      <select class="form-control" id="select-${test.id}-${aStatus.count}">
																				        
																					      <c:forEach var = "i" begin = "0" end = "${answer.maxMarks}">
																					         <option value="${i}">${i}</option>
																					      </c:forEach>
																				      </select>
																				</div>
																				<div class="col-sm-8">
																				 
																				<button
																					onclick="saveMarks(${answer.sapid}, ${answer.questionId}, ${test.id},${aStatus.count})"
																					class="btn btn-success btn-rounded pull-left"
																					style="margin-top: 25px !important; ">Submit &amp; Next </button>
																				   
																				     </div>
																			</div>
																			

																		</div>


																	</div>
														
												</div>
												</div>
											</c:forEach>
											</div>
										</div>
									</div>
									</c:forEach>
								</div>
							</div>
							
									</c:when>
									<c:otherwise>
										<h4>No Answers To Evaluate</h4>
									</c:otherwise>
									</c:choose>

							<!-- Code For Page Goes in Here End -->
						</div>

					</div>
				</div>
			</div>
		</div>
	</div>
	
	<div id="snackbar">Some text some message..</div>
	<jsp:include page="../adminCommon/footer.jsp" />

	<script type = "text/javascript">
	 

	$(document).ready(function() {
		
		var facultyId = "${userId}";
		console.log("facultyId"+facultyId);
		
		var noOfAnswersPerTest = new Array();
		<c:forEach var="test" items="${testsDetails}" varStatus="tStatus" >
		noOfAnswersPerTest.push(
								{
								"id" : ${test.id},
								"size" :  ${fn:length(testIdAndAnswersToCheckMap[test.id])}
								}						
		);
		var currentDivIdToShow = "#answerDiv-${test.id}-"+1;
		$(currentDivIdToShow).addClass("showAnswerDiv");
		
		</c:forEach>
		console.log("noOfAnswersPerTest ====> ");
		console.log(noOfAnswersPerTest);
		
		window.saveMarks = function saveMarks(sapid,questionId,testId,aIndex){
			var selectDivId = "#select-"+testId+"-"+aIndex;
			var marks = $(selectDivId).val();
			console.log("In saveMarks() got marks:"+marks+" sapId: "+sapid+" q: "+questionId+" t:"+testId+" aI:"+aIndex);
			saveMarksAjax(questionId,marks,sapid,testId,aIndex);
		}
		
		window.goToNext = function goToNext(testId, aIndex){
			console.log("IN goToNext() got testId: "+testId+" aIndex:"+ aIndex);
			var currentDivId = "#answerDiv-"+testId+"-"+aIndex;
			var nextDivId="";
			var size = getNoOfAnswers(testId);
			if(aIndex === size){
				nextDivId = "#answerDiv-"+testId+"-1";
			}else{
				nextDivId = "#answerDiv-"+testId+"-"+(aIndex+1);
			}
			console.log("IN goToNext() got currentDivId: "+currentDivId+" nextDivId:"+ nextDivId);
			$(currentDivId).removeClass("showAnswerDiv");
			$(currentDivId).addClass("hideAnswerDiv");
			$(nextDivId).removeClass("hideAnswerDiv");
			$(nextDivId).addClass("showAnswerDiv");
		}
		
		window.goToPrevious = function goToPrevious(testId, aIndex){
			console.log("IN goToPrevious() got testId: "+testId+" aIndex:"+ aIndex);
			
			var currentDivId = "#answerDiv-"+testId+"-"+aIndex;
			var nextDivId="";
			var size = getNoOfAnswers(testId);
			if(aIndex === size){
				nextDivId = "#answerDiv-"+testId+"-1";
			}else{
				nextDivId = "#answerDiv-"+testId+"-"+(aIndex+1);
			}
			console.log("IN goToNext() got currentDivId: "+currentDivId+" nextDivId:"+ nextDivId);
			$(currentDivId).removeClass("showAnswerDiv");
			$(currentDivId).addClass("hideAnswerDiv");
			$(nextDivId).removeClass("hideAnswerDiv");
			$(nextDivId).addClass("showAnswerDiv");
		}

		function showSnackBar(message) {
		    // Get the snackbar DIV
		    var x = document.getElementById("snackbar");
			console.log("In showSnackBar() got message "+message);
		    x.innerHTML = message;
		    
		    // Add the "show" class to DIV
		    x.className = "show";

		    // After 3 seconds, remove the show class from DIV
		    setTimeout(function(){ x.className = x.className.replace("show", ""); }, 3000);
			console.log("Exiting showSnackBar()");
		}
		
		function getNoOfAnswers(testId){
			for(var i=0;i<noOfAnswersPerTest.length;i++){
				tempDetails = noOfAnswersPerTest[i];
				if(tempDetails.id == testId){
					return tempDetails.size;
				}
			}
			return 0;
		}
	
		//saveMarksAjax start
		 function saveMarksAjax(questionId,marks,sapid,testId,aIndex){
			 var promiseObj = new Promise(function(resolve, reject){
			console.log("In saveMarksAjax() ENTERED...");
			var methodReturns = false;
			//ajax to save question reponse start
   		var body = {
   			'questionId' : questionId,
   			'marks' : marks,
   			'sapid' : sapid,
   			'facultyId' : facultyId
   		};
   		console.log(body);
   		$.ajax({
   			type : 'POST',
   			url : '/exam/m/saveTestAnswersMarks',
   			data: JSON.stringify(body),
               contentType: "application/json",
               dataType : "json",
               
   		}).done(function(data) {
				  console.log("iN AJAX SUCCESS");
             	console.log(data);
             	showSnackBar("Marks Saved Successfully!");
            	goToNext(testId, aIndex);
        		setAnsweredAndUpdateBadge(testId, aIndex);
             	methodReturns= true;
     			console.log("In saveMarksAjax() EXIT... got methodReturns: "+methodReturns);
     			resolve(methodReturns);
   		}).fail(function(xhr) {
   			console.log("iN AJAX eRROR");
				console.log(result);
				showSnackBar("Error in saving marks. Try Again.");
				methodReturns= false;
				console.log("In saveMarksAjax() EXIT... got methodReturns: "+methodReturns);
			    console.log('error', xhr);
			    reject(methodReturns);
			  });
			//ajax to save question reponse end
			//console.log("In saveMarksAjax() EXIT... got methodReturns: "+methodReturns);
			 })
			 return promiseObj;

   	}
		//saveMarksAjaxr end
		
	function setAnsweredAndUpdateBadge(testId, aIndex){
			console.log("IN setAnsweredAndUpdateBadge got testId:"+testId+" aIndex:"+aIndex);
			var titleId = "#title-"+testId+"-"+aIndex;
			var badgeId = "#tBadge-"+testId;
			$(titleId).addClass("greenLeftBorder");
			var answersLeft = $(badgeId).text();
			console.log("IN setAnsweredAndUpdateBadge got answersLeft:"+answersLeft	);
			if(answersLeft > 0){
				$(badgeId).text(""+(answersLeft-1));
			}
		}
		
	}); //ready() ends
	
    </script>

</body>
</html>