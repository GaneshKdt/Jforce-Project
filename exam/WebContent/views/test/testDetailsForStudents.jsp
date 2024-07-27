<!DOCTYPE html>


<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<html lang="en">

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')"
	var="server_path" />


<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Assignment Details" name="title" />
</jsp:include>

<%
	
%>
<link
	href="https://fonts.googleapis.com/css?family=Roboto:regular,bold,italic,thin,light,bolditalic,black,medium&amp;lang=en"
	rel="stylesheet">

<style>

.swal-button--confirm {
      background-color: #d2232a;
}
    
.fa-bookmark-o,.fa-bookmark{
	color: #708090 !important;
}

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
	font-size : 16px;
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
	color: #9E9E9E;
	font-size: 16px;
}



.orangeLeftBorder{
	border-left : 10px solid #ff9900 !important;
	border-radius : 2px;
	
}
.greenBack{
	background-color : #9CCC65 !important;
}
</style>
<body>

	<%@ include file="../common/header.jsp"%>



	<div class="sz-main-content-wrapper">

		<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Exam;Assignments;Assignment Details" name="breadcrumItems" />
		</jsp:include>

		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">

				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Test Details" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper dashBoard demoExam">
					<%@ include file="../common/studentInfoBar.jsp"%>

					<div class="sz-content">
              			<%@ include file="../common/messages.jsp" %>
	

						<h2 class="red text-capitalize">Assignment Details</h2>
						<div class="clearfix"></div>
						<div class="layout-container">
							<%
								try {
							%>
							<!-- Code for page goes here start -->
							
							<div class="row">
											<div class="col-md-5 col-sm-6 col-xs-12">
												<div class="card">
													<div class="card-block center">
														<small class="text-muted-light">Subject</small>
														<h4 class="m-b-0">
															<strong id="">${test.subject}</strong>
														</h4>
													</div>
												</div>
												</div>
											<div class="col-md-2 col-sm-6 col-xs-12">
												<div class="card">
													<div class="card-block center">
														<small class="text-muted-light">Month / Year</small>
														<h4 class="m-b-0">
															<strong id="">${test.month} / ${test.year}</strong>
														</h4>
													</div>
												</div>
												</div>
												
											<div class="col-md-2 col-sm-6 col-xs-12">

												<div class="card">
													<div class="card-block center">
														
														<small class="text-muted-light">Duration</small>
														<h4 class="text-success m-b-0">
															<strong id="attemptsSpan">
															<c:out value="${fn:replace(test.duration,'T', ' ')}${' Minutes'}"></c:out>
															</strong>
														</h4>
													</div>
												</div>
											</div>
											
											<div class="col-md-2 col-sm-6 col-xs-12">
												<div class="card">
												
													<div class="card-block center">
														<small class="text-muted-light">Score Out Of</small>
														<h4 class="text-primary m-b-0 " style="color: black; font-size:18px;">
															<strong id="testTime"> 
																${test.maxScore}
															</strong>
														</h4>
													</div>
													
												</div>
											</div>
											<div class="col-md-3 col-sm-6 col-xs-12">
												<div class="card">
													<div class="card-block center">
														<small class="text-muted-light">Start Date</small>
														<h4 class="text-primary m-b-0">
															<strong id="">
																<fmt:parseDate 
																	value="${fn:replace(test.startDate, 'T', ' ')}" 
																	pattern="yyyy-MM-dd HH:mm:ss" var="sDate"/>
																<fmt:formatDate 
																	type="both"
																	dateStyle="medium"
																	timeStyle="medium"
																	value="${sDate}" />
																
															</strong>
														</h4>
													</div>
												</div>
												</div>
											
											
											<div class="col-md-3 col-sm-6 col-xs-12">
												<div class="card">
													<div class="card-block center">
														<small class="text-muted-light">End Date</small>
														<h4 class="text-primary m-b-0">
															<strong id="">
																<fmt:parseDate 
																	value="${fn:replace(test.endDate, 'T', ' ')}" 
																	pattern="yyyy-MM-dd HH:mm:ss" var="eDate"/>
																<fmt:formatDate 
																	type="both"
																	dateStyle="medium"
																	timeStyle="medium"
																	value="${eDate}" />
														</strong>
														</h4>
													</div>
												</div>
												</div>
												
												
											<div class="col-md-3 col-sm-6 col-xs-12">
												<div class="card">
													<div class="card-block center">
														<small class="text-muted-light">Your Attempts / Max Attempts</small>
														<h4 class="text-primary m-b-0 " style="color: black; font-size:18px;">
															<strong id=""> 
																${studentsTestDetails.attempt} / ${test.maxAttempt}
															</strong>
														</h4>
													</div>
													</div>
												</div>
												
											<div class="col-md-3 col-sm-6 col-xs-12">
												<c:if test="${studentsTestDetails.showResult eq 'Y'}">
												<div class="card">
													<div class="card-block center">
														<small class="text-muted-light">Your Score</small>
														<h4 class="text-primary m-b-0 " style="color: black; font-size:18px;">
															<strong id=""> 
																${studentsTestDetails.score}
															</strong>
														</h4>
													</div>
												</div>
												</c:if>
												</div>
												</div>
												
<%-- 							<div class="row">

								<div class="col-md-4 col-sm-6 col-xs-12 column">
									<div class="form-group">
										<b>Subject : </b> ${test.subject}
									</div>
								</div>
								<div class="col-md-4 col-sm-6 col-xs-12 column">
									<div class="form-group">
										<b>Month : </b> ${test.month}
									</div>
								</div>
								<div class="col-md-4 col-sm-6 col-xs-12 column">
									<div class="form-group">
										<b>Year : </b> ${test.year}
									</div>
								</div>
								<div class="col-md-4 col-sm-6 col-xs-12 column">
									<div class="form-group">
										<b>Name : </b> ${test.testName}
									</div>
								</div>
								<div class="col-md-4 col-sm-6 col-xs-12 column">
									<div class="form-group">
										<b>Duration : </b>
										<c:out
											value="${fn:replace(test.duration,'T', ' ')}${' Minutes'}"></c:out>
									</div>
								</div>
								<div class="col-md-4 col-sm-6 col-xs-12 column">
									<div class="form-group">
										<b>Start Date</b>
										<c:out value="${fn:replace(test.startDate, 'T', ' ')}"></c:out>
									</div>
								</div>
								<div class="col-md-4 col-sm-6 col-xs-12 column">
									<div class="form-group">
										<b>End Date</b>
										<c:out value="${fn:replace(test.endDate,'T', ' ')}"></c:out>
									</div>
								</div>
								<div class="col-md-4 col-sm-6 col-xs-12 column">
									<div class="form-group">
										<b>Score out of : </b> ${test.maxScore}
									</div>
								</div>
								<c:if test="${test.passScore > 0 }">
								<div class="col-md-4 col-sm-6 col-xs-12 column">
									<div class="form-group">
										<b>Score to pass : </b> ${test.passScore}
									</div>
								</div>
								</c:if>
								<div class="col-md-4 col-sm-6 col-xs-12 column">
									<div class="form-group">
										<b> Your Attempts / Max Attempts  : </b> 
										${studentsTestDetails.attempt} / ${test.maxAttempt} 
									</div>
								</div>

								<c:if test="${studentsTestDetails.showResult eq 'Y'}">
									<div class="col-md-4 col-sm-6 col-xs-12 column">
										<div class="form-group">
											<b>Your Score : </b> ${studentsTestDetails.score}
										</div>
									</div>

								</c:if>

							</div> --%>

							<c:if test="${assignmentPaymentPending == 'true'}">
								<c:url value="selectTestPaymentForSecondAttemptSubjectsForm" var="paymentUrl">
									<c:param name="subject">${test.subject}</c:param>
									<c:param name="testId">${test.id}</c:param>
									<c:param name="testAttempt">${studentsTestDetails.attempt + 1}</c:param>
								</c:url>
								<a href="${paymentUrl}"
									class="btn btn-primary">Proceed to Payment</a> <span>  (<b>Note:</b> Payment is applicable before starting test as this is your 3rd or higher attempt of subject ${test.subject})</span>
							</c:if>
							
							<c:if test="${assignmentPaymentPending == 'false' && paymentPendingForSecondOrHigherAttempt == 'Y'}">
								<c:url value="selectTestPaymentForSecondAttemptSubjectsForm" var="paymentUrl">
									<c:param name="subject">${test.subject}</c:param>
									<c:param name="testId">${test.id}</c:param>
									<c:param name="testAttempt">${studentsTestDetails.attempt + 1}</c:param>
								</c:url>
								<a href="${paymentUrl}"
									class="btn btn-primary">Proceed to Payment</a> <span>  (<b>Note:</b> Payment is applicable before starting test as this is your 2nd or higher attempt of subject ${test.subject} for this academic cycle. )</span>
							
							</c:if>
							<c:if test="${assignmentPaymentPending == 'false' && paymentPendingForSecondOrHigherAttempt != 'Y'}">
							<c:if test="${ (test.maxAttempt > studentsTestDetails.attempt) && (continueAttempt != 'Y')}">
								<a href="#" title="Start Assignment" id="goToTest"
									class="btn btn-primary"> Start Assignment </a>
							</c:if>
							
							<c:if test="${ (test.maxAttempt > studentsTestDetails.attempt) && (continueAttempt == 'Y')}">
								<a href="#" title="Continue Test" id="goToTest"
									class="btn btn-primary"> Continue Assignment </a>
							</c:if>
							</c:if>
		
							
							
							
							<!-- code for questions review start -->
							<c:if test="${fn:length(attemptsDetails) > 0}">
							<div class="well">
							
							<h2 class="red text-capitalize">Attempt Details</h2>
							<div class="row">
							<div class="col-xs-12">
							<c:if test="${test.showResultsToStudents == 'Y'}">
							<span>Your Selection : </span><div style="display:inline-block; width:15px; height:15px; background-color:#ff9900; content:''; margin-right:20px;" ></div>
							<span>Correct Option : </span><div style="display:inline-block; width:15px; height:15px; background-color:#9CCC65; content:'';" ></div>
							</c:if>
							 <div class="panel-group">
							    <c:forEach items="${attemptsDetails}" var="attempt" varStatus="aStatus">
							    <div class="panel panel-default">
							      <div class="panel-heading">
							        <h4 class="panel-title">
  										<a class="" data-toggle="collapse" href="#collapseExample${attempt.id}" role="button" aria-expanded="false" aria-controls="collapseExample" style="margin-left: 20px;">
							          		Details Of Attempt No. ${attempt.attempt}
							          	</a>
							        </h4>
							      </div>
							      
<div class="col-xs-12 panel-collapse collapse " id="collapseExample${attempt.id}">
							        <div class="panel-body">
							        <span>Attempted Questions: <b>${attempt.noOfQuestionsAttempted}</b></span>
							        <span>Total Questions: <b>${fn:length(attemptNoNQuestionsMap[attempt.attempt])}</b></span>
							        	<c:choose>
							        	<c:when test="${attempt.showResult eq 'Y' }">
							  			<c:forEach items="${attemptNoNQuestionsMap[attempt.attempt]}" var="question" varStatus="qStatus">
							        	<c:if test="${question.type == 1 || question.type == 2}">
							        	<div class="well">
							        		<div class="row">
							        			<div class="col-xs-12">
							        				<span>
							        				<b>Q${qStatus.count}.</b>
							        				${question.question}
							        				
							        				</span>
							        				
							        			</div>
							        			
							        			<h4 style="margin-left: 15px;">Options:</h4>
							        			<div class="container">
							        			
							        			<c:forEach var="option" varStatus="oStatus" items="${question.optionsList}">
							        			<div style=" padding:5px 10px !important;"
							        				<c:if test="${option.selected == 'Y' && option.isCorrect != 'Y'}">
							        					class="panel panel-default orangeLeftBorder"
							        				</c:if>
							        				<c:if test="${option.selected != 'Y' && option.isCorrect != 'Y'}">
							        					class="panel panel-default"
							        				</c:if>
							        				<c:if test="${option.selected != 'Y' && option.isCorrect == 'Y'}">
							        					class="panel panel-default greenBack"
							        				</c:if>
							        				<c:if test="${option.selected == 'Y' && option.isCorrect == 'Y'}">
							        					class="panel panel-default greenBack orangeLeftBorder"
							        				</c:if>
							        				
							        			>
							        				<b>${oStatus.count}. </b> ${option.optionData}
							        			</div>
							        			</c:forEach>
							        			
							        			</div>
							        		</div>
							        	</div>
							        	</c:if>
							        	
							        	<c:if test="${question.type == 3 }">
							        	
							        	<div class="well">
							        		<div class="row">
							        			<div class="col-xs-12">
							        				<span>
							        				<b>Q${qStatus.count}.</b>
							        				${question.question}
							        				
							        				</span>
							        				
							        			</div>
							        			
							        			<div class="col-xs-12">
							        				<div class="well">
							        					<p style="">
							        						${question.description}
							        					</p>
							        				</div>
							        			</div>
							        			
							        			<h4 style="margin-left: 15px;">Sub-Questions:</h4>
							        			<div class="container">
							        			<div class="well">
							        			
							        			<c:forEach var="subQuestion"
																				items="${question.subQuestionsList }"
																				varStatus="sQStatus">
																
												<div class="col-xs-12">
							        				<span>
							        				<b>Q${sQStatus.count}.</b>
							        				${subQuestion.question}
							        				
							        				</span>
							        				
							        			</div>
												<h4 style="margin-left: 13px;">Options:</h4>
							        			<div class="container">
							        							
							        			<c:forEach var="option" varStatus="oStatus" items="${subQuestion.optionsList}">
							        			<div style=" padding:5px 10px !important;"
							        				<c:if test="${option.selected == 'Y' && option.isCorrect != 'Y'}">
							        					class="panel panel-default orangeLeftBorder"
							        				</c:if>
							        				<c:if test="${option.selected != 'Y' && option.isCorrect != 'Y'}">
							        					class="panel panel-default"
							        				</c:if>
							        				<c:if test="${option.selected != 'Y' && option.isCorrect == 'Y'}">
							        					class="panel panel-default greenBack"
							        				</c:if>
							        				<c:if test="${option.selected == 'Y' && option.isCorrect == 'Y'}">
							        					class="panel panel-default greenBack orangeLeftBorder"
							        				</c:if>
							        				
							        			>
							        				<b>${oStatus.count}. </b> ${option.optionData}
							        			</div>
							        			</c:forEach>
							        			</div>
							        			</c:forEach>
							        			</div>
							        			</div>
							        		</div>
							        	</div>
							        	
							        	</c:if>
							        	
							        	</c:forEach>
							        	
							        			</c:when>
							        			<c:otherwise>
							        				<h5>Questions will be available after results are declared.</h5>
							        			</c:otherwise>
							        			</c:choose>
							        </div>
							        <div class="panel-footer"></div>
							      </div>
							    </div>
							    </c:forEach>
							  </div>
							  </div>
							  </div>

							
							
							</div>
							</c:if>
							<!-- code for questions review end -->
							
							
							
							<!-- code for modal starts -->
							<c:if test="${messageDetails != 'openTestDetails' && empty messageDetails}">
							<div class="modal fade " id="messageModal" tabindex="-1"
								role="dialog">
								<div class="modal-dialog" role="document">
									<div class="modal-content">
										<div class="modal-header">
											<button type="button" class="close" data-dismiss="modal"
												aria-label="Close">
												<span aria-hidden="true">&times;</span>
											</button>
											<h4 align="center" class="modal-title">${messageDetails}</h4>
										</div>
										<div class="modal-body">
										<c:if test="${studentsTestDetails.showResult eq 'Y'}">
											<c:choose>
											<c:when test="${studentsTestDetails.score >= test.passScore}">
											
											<div class=" col-xs-12 ">
												<div class="">
													<h5>Congratulations! </h5> Your Score : <b> ${studentsTestDetails.score}</b>
												</div>
											</div>
											</c:when>
											<c:otherwise>
												
											<div class=" col-xs-12 ">
												<div class="">
													Your Score : <b> ${studentsTestDetails.score} </b>
												</div>
											</div>
											</c:otherwise>
											
										</c:choose>
										</c:if>
										
										<c:if test="${studentsTestDetails.showResult eq 'N'}">
											<h6>Score will be displayed after results are live.</h6>
										</c:if>
										</div>
										<div class="modal-footer">
											<button type="button" class="btn btn-default"
												data-dismiss="modal">Done</button>
										</div>
									</div>
								</div>
							</div>
							</c:if>
							<!-- code for modal ends -->

							<!-- Code for page goes here end -->
							<%
								} catch (Exception e) {
									
								}
							%>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<jsp:include page="../common/footer.jsp" />

	<script>
		$(document)
				.ready(
						function() {
							console.log(localStorage.getItem("testSubmitted"));
							$("#goToTest")
									.click(
											function(e) {
												e.preventDefault();
												if (confirm('Are you ready to take the test?')) {
													localStorage.setItem(
															"testSubmitted",
															"No");

													//alert(localStorage.getItem("testSubmitted"));
													window.location.href = "/exam/assignmentGuidelines?testId=${test.id}";
												}
											});

							console.log(localStorage.getItem("testSubmitted"));

							console.log("pAGE READY");

						});
		<c:if test="${messageDetails !='openTestDetails'}">
		
		$(window).on('load', function() {
			$('#messageModal').modal('show');
		});
		
		</c:if>
	</script>

</body>


</html>
