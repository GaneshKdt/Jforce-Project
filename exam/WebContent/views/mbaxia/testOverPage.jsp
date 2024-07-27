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

#parentSpinnerDiv {

background-color: transparent !important;
	z-index:999;
  width : 100%;
  height : 100vh;
  position: fixed;
  
}
#childSpinnerDiv {
	color: black;
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
}
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
<body style=" background-color: #ECE9E7;">

	<%-- <%@ include file="../common/header.jsp"%> --%>



	<div class="">

		<%-- <jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Exam;Assignments;Assignment Details" name="breadcrumItems" />
		</jsp:include> --%>

		<div class="">
			<div class="">

				<%-- <jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Test Details" name="activeMenu" />
				</jsp:include> --%>


				<div class="">
					<%-- <%@ include file="../common/studentInfoBar.jsp"%> --%>

					<div class="layout-container container">
              			<%@ include file="../common/messages.jsp" %>
	

						<div class="clearfix"></div>
						<div class="">
							<%
								try {
							%>
							<!-- Code for page goes here start -->
							
							<div class="jumbotron"  >
							
							<div id="parentSpinnerDiv"  >
								
							<div id="childSpinnerDiv"  >
								
								<h4 align="center"> Test Has Been Ended, Kindly use breadcrumb links to goto test details </h4>
							</div>
							</div>
							</div>
							
							<!-- code for questions review end -->
							
							
							

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

	<%-- <jsp:include page="../common/footer.jsp" /> --%>
	
<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>
	
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
													window.location.href = "/exam/assignmentGuidelinesForAllViews?testId=${test.id}&userId=${userId}";
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
