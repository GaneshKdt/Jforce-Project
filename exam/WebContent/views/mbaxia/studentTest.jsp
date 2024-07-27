<!DOCTYPE html>


<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<html lang="en">


<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')"
	var="server_path" />


<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Start Assignment" name="title" />
</jsp:include>

<%
	
%>
<!-- Roboto Web Font  fgfg-->

<link
	href="https://fonts.googleapis.com/css?family=Roboto:regular,bold,italic,thin,light,bolditalic,black,medium&amp;lang=en"
	rel="stylesheet">
	
	<!-- Include CSS for icons. -->
<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.4.0/css/font-awesome.min.css" rel="stylesheet" type="text/css" />

<!-- Include Editor style. -->
<link href="https://cdnjs.cloudflare.com/ajax/libs/froala-editor/2.5.1/css/froala_editor.pkgd.min.css" rel="stylesheet" type="text/css" />
<link href="https://cdnjs.cloudflare.com/ajax/libs/froala-editor/2.5.1/css/froala_style.min.css" rel="stylesheet" type="text/css" />
<link data-require="sweet-alert@*" data-semver="0.4.2" rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/sweetalert/1.1.3/sweetalert.min.css" />
	

<style>
body{
-webkit-user-select: none;
-khtml-user-select: none;
-moz-user-select: none;
-ms-user-select: none;
-o-user-select: none;
user-select: none;
}


.tooltip {
  font-size: 16px;
}
#questionsPreviewSection{
	overflow:scroll; 
	height:500px !important;
	margin-top:10px;
}
#questionsPreviewSection::-webkit-scrollbar {
    width: 8px;
}
#questionsPreviewSection::-webkit-scrollbar-thumb{
    background-color:#d2232a;
    border-radius:15px;
}
.questionImage{
	width : 100%;
	margin : 5px 10px;
}
.questionImageContainer{
	margin : 0px 0px 15px 0px;
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
	background-repeat: no-repeat;
	background-position: center center;
	background-size: 50% 50%;
	background-color: lightgrey;
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

.blackBorder {
	border: 2px solid black;
}

.questionSpan {
	font-size: 20px;
}

.testDiv {
	
}

.textAlignCenter {
	text-align: center;
}

.questionMainDiv {
	display: none;
}

.showQuestionMainDiv {
	display: block;
	webkit-animation: fadein 0.5s;
	animation: fadein 0.5s;
}

.hideQuestionMainDiv {
	display: none;
	webkit-animation: fadeout 0.5s 2.5s;
	animation: fadeout 0.5s 2.5s;
}

.showElement {
	display: block !important;
}

.hideElement {
	display: none !important;
}

.visibilityHidden {
	visibility: hidden;
}

.visibilityVisible {
	visibility: visible;
}

#testTimerDiv {
	
}

.vcenter {
	display: inline-block !important;
	vertical-align: middle !important;
	float: none;
}

.timerNumbers {
	font-family: "Open Sans";
	font-weight: 600;
	color: #000;
}


#subQuestionsContainer1{
	overflow:scroll; 
	height:350px; !important;
}
#subQuestionsContainer1::-webkit-scrollbar {
    width: 12px;
}
#subQuestionsContainer1::-webkit-scrollbar-thumb{
    background-color:#d2232a;
    border-radius:15px;
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
.questionPrev {
	color: black !important;
}

.questionPrev:hover {
	background-color: #ccc !important;
}
.orangeLeftBorder{
	padding-left : 10px;
	border-left : 5px solid #337ab7;
	border-radius : 2px;
	
}
.greenLeftBorder{
	padding-left : 10px;
	border-left : 5px solid #49a54e;
	border-radius : 2px;
}

/* CUSTOM RADIO BUTTON START */

/* The inputContainer */
.inputContainer {
	display: block;
	position: relative;
	padding-left: 35px;
	margin-bottom: 12px;
	cursor: pointer;
	font-size: 16px;
	-webkit-user-select: none;
	-moz-user-select: none;
	-ms-user-select: none;
	user-select: none;
}

/* Hide the browser's default radio button */
.inputContainer input {
	position: absolute;
	opacity: 0;
	cursor: pointer;
}

/* Create a custom radio button */
.radioCheckmark {
	position: absolute;
	top: 0;
	left: 0;
	height: 20px;
	width: 20px;
	background-color: #ccc;
	border-radius: 50%;
}

/* On mouse-over, add a grey background color */
.inputContainer:hover input ~.radioCheckmark {
	background-color: #ccc;
}

/* When the radio button is checked, add a custom background */
.inputContainer input:checked ~.radioCheckmark {
	background-color: #d2232a;
}

/* Create the indicator (the dot/circle - hidden when not checked) */
.radioCheckmark:after {
	content: "";
	position: absolute;
	display: none;
}

/* Show the indicator (dot/circle) when checked */
.inputContainer input:checked ~.radioCheckmark:after {
	display: block;
}

/* Style the indicator (dot/circle) */
.inputContainer .radioCheckmark:after {
	top: 7px;
	left: 7px;
	width: 6px;
	height: 6px;
	border-radius: 50%;
	background: white;
}

/* CUSTOMM RADIO BUTTON END */

/* CUSTOM CHECKBOX START */

/* Create a custom checkbox */
.checkmark {
	position: absolute;
	top: 0;
	left: 0;
	height: 20px;
	width: 20px;
	background-color: #ccc !important;
}

/* On mouse-over, add a grey background color */
.inputContainer:hover input ~.checkmark {
	
	background-color: #ccc !important;
}

/* When the checkbox is checked, add a blue background */
.inputContainer input:checked ~.checkmark {
	background-color: #d2232a;
}

/* Create the checkmark/indicator (hidden when not checked) */
.checkmark:after {
	content: "";
	position: absolute;
	display: none;
}

/* Show the checkmark when checked */
.inputContainer input:checked ~.checkmark:after {
	display: block;
}

/* Style the checkmark/indicator */
.inputContainer .checkmark:after {
	left: 7px;
	top: 3px;
	width: 10px;
	height: 18px;
	border: solid white;
	border-width: 0 3px 3px 0;
	-webkit-transform: rotate(45deg);
	-ms-transform: rotate(45deg);
	transform: rotate(45deg);
}
/
*

 

CUSTOM

 

CHEKCBOX

 

END

 

*


?
</style>
<body>
	<div id="toTop"></div>
	<%@ include file="../common/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Exam;Online Assignment;Start Assignment" name="breadcrumItems" />
		</jsp:include>

		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">

				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Test" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper dashBoard demoExam">
					<%@ include file="../common/studentInfoBar.jsp"%>

					<div class="sz-content">


						<h2 class="red text-capitalize">${test.subject} Assignment</h2>
						<div class="clearfix"></div>
						<div>

							<!-- Code for page goes here start -->
							
							<div class="row">
								<div class="col-sm-12 ">

									<!-- Content -->
									<div class="layout-content" data-scrollable>
										<div class="container-fluid">
											<div class="card-group">
												<div class="card">
													<div class="card-block center">
														<h4 class="m-b-0">
															<strong id="attemptsCountSpan">${studentsTestDetails.attempt} / ${test.maxAttempt}</strong>
														</h4>
														<small class="text-muted-light">ATTEMPT</small>
													</div>
												</div>
												
												<div class="card">
													<div class="card-block center">
														<h4 class="m-b-0">
															<strong id="noOfQuestionsSpan"></strong>
														</h4>
														<small class="text-muted-light">TOTAL QUESTIONS</small>
													</div>
												</div>

												<div class="card">
													<div class="card-block center">
														<h4 class="text-success m-b-0">
															<strong id="attemptsSpan"></strong>
														</h4>
														<small class="text-muted-light">ANSWERED</small>
													</div>
												</div>
												<div class="card">
													<div class="card-block center">
														<h4 class="text-primary m-b-0">
															<strong id="attemptsLeft"></strong>
														</h4>
														<small class="text-muted-light">LEFT</small>
													</div>
												</div>
												<div class="card">
													<div class="card-block center">
														<h4 class="text-primary m-b-0 " style="color: black; font-size:18px;">
															<strong id="testTime"> <span id="hoursSpan"
																class="timerNumbers"></span>h : <span id="minutesSpan"
																class="timerNumbers"></span>m : <span id="secondsSpan"
																class="timerNumbers"></span>s
															</strong>
														</h4>
														<small class="text-muted-light">Time left</small>
													</div>
												</div>
											</div>
											
											
					
											<div class="row " >
											
											<!-- questions section -->
											<div class="col-md-10" >
											<div id="progressDiv"
												style="width: 0px; height: 10px; background-color: #4bb543; border-radius: 5px; padding-left: 10px">
											</div>
											
											
											
											
											
											<c:forEach var="testQuestion" items="${testQuestions}"
												varStatus="status">
												
													<form id="studentTestForm-${status.count}">
													<div id="questionMainDiv-${status.count}"
													class="questionMainDiv">

													<div class="card">
														<div class="card-header bg-white p-a-1"
															style="background-color: #ffffff">
															<div id="prevNextDiv" class="">
																					<button type="submit"
																					class="prevnext next  nextQuestionButton"
																					style="text-transform: none; 
																							margin:0px 0px 0px 5px;
																							padding: 8px 16px;
																							font-size: 0.9rem;
																							font-weight: unset;
																							"
																						onclick="disableNextButton(${status.count});"
																						<c:if test="${status.count==noOfQuestions}">disabled</c:if>
																					>Next &raquo;</button>
																			
																				<%-- <a href="#" id="nextQuestionButton-${status.count}"
																					class="prevnext next  nextQuestionButton">
																				Skip &raquo;
																				</a> --%>
																				<button
																					id="previousQuestionButton-${status.count}"
																					style="text-transform: none; 
																							margin:0px;
																							padding: 8px 16px;
																							font-size: 0.9rem;
																							font-weight: unset;
																							"
																					<c:if test="${status.count==1}">disabled</c:if>
																					class="prevnext previous  previousQuestionButton" onclick="showNextButtonOnClickOfPreviousButton();">
																				&laquo; Previous
																				</button>
																				<a href="#"
																					onclick="markForReview(${testQuestion.id})"
																					data-toggle="tooltip"
																					class="prevnext previous round"
																					title="Tag For Review"
																					>
																					<i id="bookmarkIcon-${testQuestion.id}" class="fa fa-bookmark-o" style = "font-size : 25px;" ></i>
																				</a>
															</div>
															<div class="media">
																<div class="media-body  media-middle">
																	<h4 class="card-title"
																		id="questionMainDiv-${status.count}"
																		class="questionMainDiv">
																		
							        									<i id="answerAttemptedTick-${testQuestion.id}" class="" style=" color: green; "></i>
																		<span class="questionNoSpan">Q${status.count}.
																		</span> <span>${testQuestion.question}</span>
																	</h4>
																	<!-- display name of type of question start -->
																	<small style = "color: #708090;">Question Type :
																		<c:choose>
																		<c:when test="${testQuestion.type == 1}">
																			Single Select
																		</c:when>
																		<c:when test="${testQuestion.type == 2}">
																			Multiple Select  (Can select one or more than one right option)
																		</c:when>
																		<c:when test="${testQuestion.type == 3}">
																			Case Study
																		</c:when>
																		<c:when test="${testQuestion.type == 4}">
																			Descriptive
																		</c:when>
																		<c:when test="${testQuestion.type == 5}">
																			True Or False
																		</c:when>
																		<c:otherwise>
																			NA
																		</c:otherwise>
																		</c:choose>
																	 </small>
																	<!-- display name of type of question end -->
																	
																</div>
															</div>
														</div>

														<c:choose>
															<c:when test="${testQuestion.type == 7}">
																
																	<input type="hidden" value="${testQuestion.id}"
																		id="hiddenQuestionId-${status.count}">
																	
																	
																	<div class="card-block p-a-2"
																		style="background-color: #ffffff">
																		<div class="row">
																			
																			<div class="col-sm-3" style=" "></div>
																			<div class="col-sm-6" style=" ">
													                      	   <iframe 
													                      	   		style="width:100%; min-height: 400px; "
													                      	   		src='${testQuestion.url}' 
													                      	   		frameborder='0'
																			      	webkitAllowFullScreen mozallowfullscreen allowFullScreen
																			   ></iframe>
																			 </div>
																			
																			<div class="col-sm-3" style=" "></div>
																		</div>


																	</div>
																	
																	<div class="card-footer clearfix"  
																		id="options${testQuestion.id}">
																		<div class="form-group">

																			<c:forEach var="tempOption"
																				items="${testQuestion.optionsList}"
																				varStatus="optionStatus">
																				
																						<label class="c-input c-checkbox">${tempOption.optionData}

																							<input type="checkbox"
																							id="checkField${status.count}1"
																							value="${tempOption.id}"
																							<c:if test = "${tempOption.selected == 'Y'}">
																							 checked 
																							</c:if>
																							onclick="checkFields('${testQuestion.id}',false)">
																							<span class="c-indicator"></span>
																						</label>
																						<br>
																						<br>
																			</c:forEach>


																		</div>


																	</div>
															</c:when>
															<c:when test="${testQuestion.type == 6}">
																
																	<input type="hidden" value="${testQuestion.id}"
																		id="hiddenQuestionId-${status.count}">
																	
																	
																	<div class="card-block p-a-2"
																		style="background-color: #ffffff">
																		<div class="col-sm-12 questionImageContainer">
																			<img class="questionImage"  alt="Question Picture" src="${testQuestion.url}">

																		</div>


																	</div>
																	
																	<div class="card-footer clearfix"  
																		id="options${testQuestion.id}">
																		<div class="form-group">

																			<c:forEach var="tempOption"
																				items="${testQuestion.optionsList}"
																				varStatus="optionStatus">
																				
																				
																						<label class="c-input c-checkbox">${tempOption.optionData}

																							<input type="checkbox"
																							id="checkField${status.count}1"
																							value="${tempOption.id}"
																							<c:if test = "${tempOption.selected == 'Y'}">
																							 checked 
																							</c:if>
																							onclick="checkFields('${testQuestion.id}',false)">
																							<span class="c-indicator"></span>
																						</label>
																						<br>
																						<br>
																				
																			</c:forEach>


																		</div>


																	</div>
															</c:when>
															<c:when test="${testQuestion.type == 5}">
																
																	<input type="hidden" value="${testQuestion.id}"
																		id="hiddenQuestionId-${status.count}">

																	<div class="card-block p-a-2"
																		id="options${testQuestion.id}">
																		<div class="form-group">

																			<c:forEach var="tempOption"
																				items="${testQuestion.optionsList}"
																				varStatus="optionStatus">
																				<c:choose>
																					<c:when test="${testQuestion.type == 5}">

																						<label class="inputContainer">${tempOption.optionData}


																							<input type="radio"
																							name="selectedOption${testQuestion.id}"
																							id="checkField${status.count}1"
																							value="${tempOption.id}"
																							<c:if test = "${tempOption.selected == 'Y' }">
																							 checked 
																							</c:if>
																							
																							onclick="checkFields('${testQuestion.id}',false)">

																							<span class="radioCheckmark"></span>
																						</label>

																					</c:when>
																				</c:choose>
																			</c:forEach>


																		</div>


																	</div>
																	<div class="card-footer clearfix "
																		style="background-color: #ffffff">
																		<div class="col-sm-12">

																			<div class="col-sm-6">
																			</div>


																		</div>


																	</div>
															</c:when>
															<c:when test="${testQuestion.type == 4}">
																<div class="" >
																	<input type="hidden" value="${testQuestion.id}"
																		id="hiddenQuestionId-${status.count}">
																	
																	<div class="card-block p-a-2">
																		<div class="form-group">
																			<textarea id="textArea-${testQuestion.id}" rows="20"   wrap="hard" required></textarea>
																		</div>
																	</div>																	
																	<div class="card-footer clearfix "
																		style="background-color: #ffffff">
																		<div class="col-sm-12">

																			<div class="col-sm-6">
																			</div>


																		</div>


																	</div>
				 												</div>
															</c:when>
															<c:when test="${testQuestion.type == 3}">

																<!-- Display subQuestions if question type is 3 i.e. caseStudy -->
																<div id="subQuestionsContainer">
																<div class="well">
																	<p style="font-size: 16px;">
																	${testQuestion.description}
																	</p>
																</div>

																<div class="well" >
																	<h5>Questions :</h5>
																	<input type="hidden" value="${testQuestion.id}"
																		id="hiddenQuestionId-${status.count}">
																	
																	<!-- literate through casestudy questions start -->
																	<c:forEach var="subQuestion"
																				items="${testQuestion.subQuestionsList }"
																				varStatus="sQStatus">
																	
																	
																	<div class="card">
														<div class="card-header bg-white p-a-1 orangeLeftBorder" id="subQuestionH4-${subQuestion.id}"
															style="background-color: #ffffff">
															<div class="media">
																<div class="media-body  media-middle">
																	<h4 class="card-title"
																		
																		class="questionMainDiv">
																		
																		<a href="#demo${subQuestion.id}" class="" data-toggle="collapse"
																		style = "color: black; font-size: 18px;"
																		>
																			<b>Q${sQStatus.count} : </b>
																			${subQuestion.question }
																		</a><br>
																	
																	</h4>
																</div>
															</div>
														</div>
																	</div>
																	
																	  <div id="demo${subQuestion.id}" class="collapse in">
																	
																	<!-- start -->
																	<div class="card-block p-a-2"
																		id="options${testQuestion.id}-${subQuestion.id}">
																		<div class="form-group">

																			<c:forEach var="tempOption"
																				items="${subQuestion.optionsList}"
																				varStatus="optionStatus">
																				<c:choose>
																					<c:when test="${subQuestion.type == 1}">

																						<label class="inputContainer">${tempOption.optionData}


																							<input type="radio"
																							name="selectedOption${subQuestion.id}"
																							id="checkField${status.count}1"
																							value="${tempOption.id}"
																							<c:if test = "${tempOption.selected == 'Y'}">
																							 checked 
																							</c:if>
																							onclick="checkFields('#subQuestionH4-${subQuestion.id}',true)">

																							<span class="radioCheckmark"></span>
																						</label>

																					</c:when>
																					<c:when test="${subQuestion.type == 2}">
																						<label class="c-input c-checkbox">${tempOption.optionData}

																							<input type="checkbox"
																							id="checkField${status.count}1"
																							value="${tempOption.id}"
																							<c:if test = "${tempOption.selected == 'Y'}">
																							 checked 
																							</c:if>
																							onclick="checkFields('#subQuestionH4-${subQuestion.id}',true)">
																							<span class="c-indicator"></span>
																						</label>
																						<br>
																						<br>

																					</c:when>
																				</c:choose>
																			</c:forEach>


																		</div>


																	</div>
																	<!-- end -->
																	</div>
																	  </c:forEach>
																	
																	<!-- literate through casestudy questions end -->
																	
																	<div class="card-footer clearfix "
																		style="background-color: #ffffff">
																		<div class="col-sm-12">

																			<div class="col-sm-6">
																			</div>


																		</div>


																	</div>
																

																</div>
																
																
																</div>


															</c:when>
															<c:otherwise>
																	<input type="hidden" value="${testQuestion.id}"
																		id="hiddenQuestionId-${status.count}">

																	<div class="card-block p-a-2"
																		id="options${testQuestion.id}">
																		<div class="form-group">

																			<c:forEach var="tempOption"
																				items="${testQuestion.optionsList}"
																				varStatus="optionStatus">
																				<c:choose>
																					<c:when test="${testQuestion.type == 1}">

																						<label class="inputContainer">${tempOption.optionData}


																							<input type="radio"
																							name="selectedOption${testQuestion.id}"
																							id="checkField${status.count}1"
																							value="${tempOption.id}"
																							<c:if test = "${tempOption.selected == 'Y' }">
																							 checked 
																							</c:if>
																							
																							onclick="checkFields('${testQuestion.id}',false)">

																							<span class="radioCheckmark"></span>
																						</label>

																					</c:when>
																					<c:when test="${testQuestion.type == 2}">
																						<label class="c-input c-checkbox">${tempOption.optionData}

																							<input type="checkbox"
																							id="checkField${status.count}1"
																							value="${tempOption.id}"
																							<c:if test = "${tempOption.selected == 'Y'}">
																							 checked 
																							</c:if>
																							onclick="checkFields('${testQuestion.id}',false)">
																							<span class="c-indicator"></span>
																						</label>
																						<br>
																						<br>

																					</c:when>
																				</c:choose>
																			</c:forEach>


																		</div>


																	</div>
																	<div class="card-footer clearfix "
																		style="background-color: #ffffff">
																		<div class="col-sm-12">

																			<div class="col-sm-6">
																			</div>


																		</div>


																	</div>
															</c:otherwise>
														</c:choose>


													</div>

												</div>
												</form>

											</c:forEach>

											<div class="col-sm-12">
												<button id="submitTest" class="btn btn-rounded pull-right"
													style="background-color: #d2232a">Submit Assignment</button>
											</div>
											
											</div>
											<!-- End of questions section -->
											<div   class="col-md-2">
										
						<div class="col-sm-12">
							<div class="card" id="questionsPreviewSection">
								<ul class="list-group m-b-0">

								<li class="list-group-item" style="font-size : 12px !important;">
										<div class="">
											<div class="media-left">Question No.</div>
											<div class="media-body textAlignCenter">
												<span class="textAlignCenter">Status</span>
											</div>
										</div>
									</li>


									<c:forEach var="testQuestion" items="${testQuestions}"
										varStatus="status">

										<li class="list-group-item" style="cursor: pointer"
											onclick="jumpToQuestion(${status.count})">
											<div class="media">
												<div class="media-body">
													<div class="text-muted-light" style = "color:black; font-size : 16px;">
													<a href="#"	
													 onclick="jumpToQuestion(${status.count})">
													Q${status.count}
													</a>
													</div>
												</div>
												<%-- <div class="media-body" style = "color:black; font-size : 16px;">${testQuestion.question}</div>--%>
												<div class="media-right">
												<i id="bookmarkIconInQuestionStatus-${testQuestion.id}" class="fa fa-bookmark-o" style = "display: inline ; font-size : 18px;" ></i>
												</div> 
												<div class="media-right">
													<span id="statusQuestion${status.count}"
														class="label left_status">Unattempted</span>
												</div>
											</div>
										</li>
									</c:forEach>
								</ul>
							</div>
						</div>
											</div>
											
										</div>
									</div>
								</div>


							</div>
						</div>
						

						

						<!-- Code for page goes here end -->
					</div>
				</div>
			</div>
		</div>
	</div>


	<div id="snackbar">Some text some message..</div>
	<jsp:include page="../common/footer.jsp" />


<script src="https://unpkg.com/sweetalert/dist/sweetalert.min.js"></script>



<script type="text/javascript">

function testHide(){
console.log('call')
	$("#questionMainDiv-1").removeClass("showQuestionMainDiv");
	$("#questionMainDiv-1").addClass("hideQuestionMainDiv");	
}
function testShwo(){
	$("#questionMainDiv-1").removeClass("hideQuestionMainDiv");
	$("#questionMainDiv-1").addClass("showQuestionMainDiv");
}



	$(document).ready(function() {
	  	   console.log("testSubmitted");

   	   console.log(localStorage.getItem("testSubmitted"));
  	   if(localStorage.getItem("testSubmitted") === "Yes"){

  	   	//alert(localStorage.getItem("testSubmitted"));
       	window.location.href = "/exam/viewTestsForStudent";
  	   }
 
			
		var noOfQuestions = ${noOfQuestions};
		var testId = ${test.id};
		var sapid = ${userId};
		var currentQuestion = 1;
		var noOfQuestionsAttemptted = 0;
		var attemptedQuestions= Array(noOfQuestions).fill();
	   	var duration = ${test.duration};
		var noOfQuestionsSkipped = 0;
		var isTestOver = false;
		var totalAttempt = ${studentsTestDetails.attempt};
		var questionUpdated = false;
		var lastQuestionUpdated = false;
		var jumpToQuestionPlaceholder = 1;
		<c:if test = "${continueAttempt == 'Y'}">
			noOfQuestionsAttemptted= ${studentsTestDetails.noOfQuestionsAttempted};

			<c:forEach var="testQuestion" items="${testQuestions}"	varStatus="status">
			<c:if test = "${testQuestion.isAttempted == 'Y'}">

			$("#statusQuestion${status.count}").text("Answered");
			$('#statusQuestion${status.count}').addClass('btn-success');
			</c:if>
			</c:forEach>
		</c:if>
		
		console.log("totalAttempt: "+totalAttempt+" noOfQuestions: "+noOfQuestions+". testId: "+testId+". sapid: "+sapid+" noOfQuestionsAttemptted: "+noOfQuestionsAttemptted+" duration:"+duration);
		
		var questionsDetails = new Array();
		

		<c:forEach var="testQuestion" items="${testQuestions}"	varStatus="status">
			questionsDetails.push( {
								"id": ${testQuestion.id},
								"type": ${testQuestion.type},
								"divId": ${status.count},
								"subQuestions": [
												<c:forEach var="subQuestion" items="${testQuestion.subQuestionsList}"	varStatus="status">
													{
														"id":${subQuestion.id},
														"type":${subQuestion.type}
													},
												</c:forEach>
												]
									}
								);
		</c:forEach>
		
		console.log("Got questionsDetails =====>");
		console.log(questionsDetails);
		console.log("Got questionsDetails <=====");
		
		//show first question on page load
		$("#questionMainDiv-1").addClass("showQuestionMainDiv");
		$("#attemptsSpan").text(noOfQuestionsAttemptted);
		$("#noOfQuestionsSpan").text(noOfQuestions);
		$("#attemptsLeft").text(noOfQuestions);
		$("#skippedSpan").text(noOfQuestionsSkipped);
		
		calcProgress()
		
		<c:forEach var="testQuestion" items="${testQuestions}"	varStatus="status">
			try {
				$("#studentTestForm-${status.count}").submit(function(e){
					console.log('Called Submit')
				    e.preventDefault();
					checkIsTestOver("");
					console.log('questionUpdated = '+questionUpdated);

					questionId=$("#hiddenQuestionId-${status.count}").val();
					submitTestQuestionForm(questionId,'nextQuestion');
				});
			}
			catch(err) {
			    console.log("Catch error : "+err.message);
			}
		</c:forEach>
		
		function submitTestQuestionForm(qId,actionTo){

			if(!questionUpdated){
				if('nextQuestion' === actionTo){
					nextQuestion();
				}
				else if('previousQuestion' === actionTo){
					previousQuestion();
				}
				else if('submitTest' === actionTo){
				}
				else{
					jumpToQuestoinSubFunction(currentQuestion);
				}
				return;
			}
			
		    let saved = validateAndSaveAnswer(qId,actionTo);
		    if(saved){
		    	console.log("Saved Answer.");
		    	questionUpdated= false;
	 	    }else{
		    	console.log("Error in saving answer.");
	 	    }
		}
		
		//validateAndSaveAnswer Start
		function validateAndSaveAnswer(questionId,actionTo){
			console.log("In validateAndSaveAnswer() questionId : "+questionId+" actionTo : "+actionTo);
			
			questionDetails = getQuestionDetails(questionId);
		 	console.log("In validateAndSaveAnswer() got questionDetails ====> ");
		 	console.log(questionDetails);
		 	console.log("In validateAndSaveAnswer() got questionDetails <==== ");
			
			if(questionDetails==null){
				showSnackBar("Error in getting QuestionDetails, Please try again");
			}else{
			//logic to get given answer start
			var inputArraySelector;
			if(questionDetails.type==4){
				return answuerOftype4(questionDetails,actionTo);
			}else if(questionDetails.type==3){
				return answuerOftype3(questionDetails,actionTo);
			}else{
				inputArraySelector = "#options"+questionDetails.id+" :input";
				let saved= answuerOftype1n2(questionDetails,inputArraySelector,false,actionTo);
				if(saved){
		 	    	return true;
		 	    }else{
		 	    	return false;
		 	    }
			}
			
			//logic to get given answer end
			}
		}
		//validateAndSaveAnswer end
		
		 function answuerOftype1n2(questionDetails,inputArraySelector,isSubQuestion,actionTo){
				 return new Promise(resolve => {
					      var answer;
							tempOptionCount=1;
						    console.log("IN answerOftype1n2() inputArraySelector : "+inputArraySelector);
						    inputArray = $(inputArraySelector);
						    console.log(inputArray);

						    console.log("inputArray: "+inputArray.length);
						 	for(let i = 0; i < inputArray.length; i++){
						    	console.log("Option button Option: "+i+" . Value: "+$(inputArray[i]).val());
						    	optionInput =inputArray[i];
						    	if($(optionInput).is(":checked")){
						    		console.log("Selected Option: "+$(optionInput).attr("name")+" . Value: "+$(optionInput).val()+" . Sapid: "+sapid);
						    		if(tempOptionCount==1){
						    			answer= ""+$(optionInput).val();
						    		}else{
						    			answer= answer+"~"+$(optionInput).val();
						    		}
							    	tempOptionCount++;
						    	}
							}
						 	if(answer!=null){
						 		var saved = saveAnswerAjax(questionDetails.id,answer,sapid,testId,questionDetails.type,isSubQuestion,actionTo)
						 						.then(success, failure)
											 		function success(data){
												 	    	resolve(true);
											 			}
											 		function failure(data){
													      resolve(false);
											 			}
						 			
						 	}else{
						 			//return false;
								    resolve(true);
						 		
						 		
						 	}
			});
			
		}
		
		function answuerOftype3(questionDetails,actionTo){ // if question is of type 3 i.e. Case Study
			var csId = questionDetails.id;
			var csSubQuestions = questionDetails.subQuestions;
			var tempQuestion;
			var inputArraySelector;
			var noOfCSQuestionNotAnswered=0;

		    	var tempNoOfSubQuestionsSaved =0;
				async function asyncCall() {
					 for(var i = 0; i< csSubQuestions.length;i++){
						 tempQuestion=csSubQuestions[i];
						if(tempQuestion.type==1 || tempQuestion.type==2){
							inputArraySelector = "#options"+csId+"-"+tempQuestion.id+" :input";
							let saved = await answuerOftype1n2(tempQuestion,inputArraySelector,true).then(function(){
								console.log("------------------------>CAlled saved")
									tempNoOfSubQuestionsSaved++;			
							});
								
						}
					}
				}
				asyncCall().then(function(){
					console.log("------------------------>CAlled Then")
					if(tempNoOfSubQuestionsSaved == csSubQuestions.length){
						// asyncCallToSavedCSQuestionAnswer start
						async function asyncCallToSavedCSQuestionAnswer(){
							let saved = await saveAnswerAjax(csId,"attempted",sapid,testId,questionDetails.type,true)
	 						.then(success, failure)
						 		function success(data){
	 							console.log("In SUCCESS of AJAX CALL of Case Study Question =====> ");
								postAjaxSuccessOperations(actionTo);
								return true;
							 		}
						 		function failure(data){
									showSnackBar("Error in saving all sub questions, Please try again . ");
									return false;
								 	}
						}
						//asyncCallToSavedCSQuestionAnswer end
						asyncCallToSavedCSQuestionAnswer();
 			
 	
					}else{
						showSnackBar("Error in saving all sub questions, Please try again . ");
						return false;
					}
				});
				console.log("tempNoOfSubQuestionsSaved" + tempNoOfSubQuestionsSaved)
				
			
			return false;
		}
		
		//answuerOftype4 start
		function answuerOftype4(questionDetails,actionTo){ //type 4 is of Descriptive
			var dQuestionId = questionDetails.id;
			var textAreaSelector = "#textArea-"+dQuestionId;
			var dAnswuer = $(textAreaSelector).val();
			console.log("In answuerOftype4 got dQuestionId: "+dQuestionId+" textAreaSelector: "+textAreaSelector+" dAnswuer: "+dAnswuer);
			async function asyncCallToSaveDescriptiveAnswer(){
				let saved = await  saveAnswerAjax(dQuestionId,dAnswuer,sapid,testId,questionDetails.type,false,actionTo)
					.then(success, failure)
			 		function success(data){
						console.log("In SUCCESS of AJAX CALL of Descriptive =====> ");
					return true;
				 		}
			 		function failure(data){
						showSnackBar("Error in saving answer, Please try again . ");
						return false;
					 	} 
			}
			asyncCallToSaveDescriptiveAnswer();
		}
		//answuerOftype4 end
		
		//getQuestionDetails(questionId) start
		function getQuestionDetails(questionId){
			for(var i=0;i<questionsDetails.length;i++){
				tempDetails = questionsDetails[i];
				if(tempDetails.id == questionId){
					return tempDetails;
				}
			}
			return null;
		}
		//getQuestionDetails(questionId) end
		
		//getQuestionDetailsByDivId(questionId) start
		function getQuestionDetailsByDivId(divId){
			for(var i=0;i<questionsDetails.length;i++){
				tempDetails = questionsDetails[i];
				if(tempDetails.divId == divId){
					return tempDetails;
				}
			}
			return null;
		}
		//getQuestionDetails(questionId) end
		
		//saveAnswerAjax start
		 function saveAnswerAjax(questionId,answer,sapid,testId,type,isSubQuestion,actionTo){
			 var promiseObj = new Promise(function(resolve, reject){
			console.log("In saveAnswerAjax() ENTERED...");
			var methodRetruns = false;
			//ajax to save question reponse start
    		var body = {
    			'questionId' : questionId,
    			'answer' : answer,
    			'sapid' : sapid,
    			'testId' : testId,
    			'type' : type,
    			'attempt' : totalAttempt
    			
    		};
    		console.log(body);
    		$.ajax({
    			type : 'POST',
    			url : '/exam/m/addStudentsQuestionResponse',
    			data: JSON.stringify(body),
                contentType: "application/json",
                dataType : "json",
                
    		}).done(function(data) {
				  console.log("iN AJAX SUCCESS");
              	console.log(data);
              	if(!isSubQuestion){ //Do below operations only if Main question and not of type 3
              		showTickForAnswerSaved(questionId);	
              		postAjaxSuccessOperations(actionTo);
              	}
              	methodRetruns= true;
      			console.log("In saveAnswerAjax() EXIT... got methodRetruns: "+methodRetruns);
      			resolve(methodRetruns);
    		}).fail(function(xhr) {
    			console.log("iN AJAX eRROR");
				showSnackBar("Error in saving answer. Try Again.");
				methodRetruns= false;
				console.log("In saveAnswerAjax() EXIT... got methodRetruns: "+methodRetruns);
			    console.log('error', xhr);
			    reject(methodRetruns);
			  });
			//ajax to save question reponse end
			//console.log("In saveAnswerAjax() EXIT... got methodRetruns: "+methodRetruns);
			 })
			 return promiseObj;

    	}
		//saveAnswerAjaxr end
		
		//postAjaxSuccessOperations start
		function postAjaxSuccessOperations(actionTo){
			updateAttemptedQuestions(currentQuestion);
			if('nextQuestion' === actionTo){
				nextQuestion();
			}
			else if('previousQuestion' === actionTo){
				previousQuestion();
			}
			else if('submitTest' === actionTo){
			}
			else{
				jumpToQuestoinSubFunction(jumpToQuestionPlaceholder);
			}
        	showSnackBar("Answer Saved!");
        	calcProgress();
        }
		//postAjaxSuccessOperations end
		
		//submitTest Start
		$("#submitTest").click(function(e){

            console.log("Clicked submit Test +++++++++> ");
			event.preventDefault();
			
			if(lastQuestionUpdated){
				questionData = getQuestionDetailsByDivId(noOfQuestions);
				if(questionData !=null){
		            console.log("Saving Last Question. ");
					submitTestQuestionForm(questionData.id,'submitTest'); 
				}else{
					showSnackBar('Error in going to question try again.');
					return;
				}
			}

            swal({
                title: "Are you sure?",
                text: "  Test would be submitted and attempt will be exhausted! \n Questions Attempted: "+noOfQuestionsAttemptted+" \n  Questions Left: "+(noOfQuestions-noOfQuestionsAttemptted)+" ",
                icon: "warning",
                buttons: [
                  'No, cancel it!',
                  'Yes, I am sure!'
                ],
                dangerMode: true,
              }).then(function(isConfirm) {
                if (isConfirm) {
    	            event.preventDefault();
    	        	submitTest("testEnded");
                } else {
    	            event.preventDefault();
                }
              });
            
		});
		
		function submitTestSubFunction(){
			
		}
		
		$(".nextQuestionButtonNOTUSED").click(function(){
			console.log("Clicked nextQuestionButton ");
			nextQuestion();
		});
		

		$(".previousQuestionButton").click(function(e){
			console.log("Clicked previousQuestionButton ");

		    e.preventDefault();
		    if(questionUpdated){
				questionData = getQuestionDetailsByDivId(currentQuestion);
				if(questionData !=null){
					submitTestQuestionForm(questionData.id,'previousQuestion')
				}else{
					showSnackBar('Error in going to question try again.');
				}
		    
		    	return;	
			}
			previousQuestion();
		});
		
		function showTickForAnswerSaved(qId){
			var selector = "#answerAttemptedTick-"+qId;
			console.log("In showTickForAnswerSaved() got selector: "+selector);
			$(selector).addClass("fa");
			$(selector).addClass("fa-check-circle");
		}
		
		function updateAttemptedQuestions(question){
			console.log("In updateAttemptedQuestions() ");
			var arrayLength =attemptedQuestions.length;
			var temp=0;
			if(arrayLength==0){
				index = question - 1;
				item = 
						{
							qId : question,
							answered : true
						}		
					
				attemptedQuestions.splice(index, 1, item);
				noOfQuestionsAttemptted++;
				statusQuestion${status.count}
				$("#statusQuestion" + question).text("Answered");
				$('#statusQuestion' + question).addClass('btn-success');

			}else{
				if(attemptedQuestions[question-1] != null){
					console.log("Question "+question+" is Attempted");
					if(!attemptedQuestions[question-1].answered){
						console.log("Question "+question+" is Skipped");
						attemptedQuestions[question-1].answered = true;
						noOfQuestionsAttemptted++;
						noOfQuestionsSkipped--;
						$("#statusQuestion" + question).text("Answered");
						$('#statusQuestion' + question).addClass('btn-success');
					}
					
				}else{
					console.log("Question "+question+" is NOT Attempted");
					index = question - 1;
					item = 
							{
								qId : question,
								answered : true
							}		
						
					attemptedQuestions.splice(index, 1, item);
					console.log("Pushed "+question+" in attemptedQuestions[]: "+attemptedQuestions);
					noOfQuestionsAttemptted++;
					$("#statusQuestion" + question).text("Answered");
					$('#statusQuestion' + question).addClass('btn-success');
					}
				}
			
			console.log("setting noOfQuestionsAttemptted "+noOfQuestionsAttemptted+" to div ");
			$("#attemptsSpan").text(noOfQuestionsAttemptted);
			
			$("#attemptsLeft").text(noOfQuestions - noOfQuestionsAttemptted);
			$("#skippedSpan").text(noOfQuestionsSkipped);

		}
		
		function nextQuestion(){
			console.log("Entering nextQuestion() currentQuestion: "+currentQuestion);
			//updateSkippedQuestions(currentQuestion);
			$("#questionMainDiv-"+currentQuestion).addClass("hideQuestionMainDiv");
			currentQuestion=currentQuestion+1;
			if(currentQuestion>noOfQuestions){
				currentQuestion=1;
			}
			$("#questionMainDiv-"+currentQuestion).removeClass("hideQuestionMainDiv");
			$("#questionMainDiv-"+currentQuestion).addClass("showQuestionMainDiv");
			console.log("Exiting nextQuestion() currentQuestion: "+currentQuestion);
		}
		//NOT USED KEPT FOR REFERENCE
		function updateSkippedQuestions(question){
			console.log("Question Skipped" + question )
			if(attemptedQuestions.length == 0){
				index = question - 1;
				item = 
						{
							qId : question,
							answered : false
						}		
					
				attemptedQuestions.splice(index, 1, item);
console.log(attemptedQuestions)

					noOfQuestionsSkipped++;
$("#statusQuestion" + question).text("Skipped");
$('#statusQuestion' + question).addClass('skipped_status');
			}else{
				if(attemptedQuestions[question - 1] != null){
					console.log(attemptedQuestions)

				} else{
					index = question - 1;
					item = 
							{
								qId : question,
								answered : false
							}		
						
					attemptedQuestions.splice(index, 1, item);
					console.log(attemptedQuestions)

					noOfQuestionsSkipped++;
					$("#statusQuestion" + question).text("Skipped");
					$('#statusQuestion' + question).addClass('skipped_status');

				}
			}
			$("#skippedSpan").text(noOfQuestionsSkipped);

		}
		
		function previousQuestion(){
			console.log("Entering previousQuestion() currentQuestion: "+currentQuestion);
			
			
			
			$("#questionMainDiv-"+currentQuestion).addClass("hideQuestionMainDiv");
			currentQuestion=currentQuestion-1;
			if(currentQuestion < 1){
				currentQuestion=noOfQuestions;
			}
			$("#questionMainDiv-"+currentQuestion).removeClass("hideQuestionMainDiv");
			$("#questionMainDiv-"+currentQuestion).addClass("showQuestionMainDiv");
			console.log("Exiting previousQuestion() currentQuestion: "+currentQuestion);
		}
		
		window.jumpToQuestion = function jumpToQuestion(question){
			
			if(questionUpdated){
				questionData = getQuestionDetailsByDivId(currentQuestion);
				if(questionData !=null){
					jumpToQuestionPlaceholder = question;
					submitTestQuestionForm(questionData.id,'jumpToQuestion')
				}else{
					showSnackBar('Error in going to question try again.');
				}
				return;
			}
			
			jumpToQuestoinSubFunction(question);
		}
		function jumpToQuestoinSubFunction(question){
			console.log("Entering jumpToQuestion() Jumping to: "+question);
			$("#questionMainDiv-"+currentQuestion).addClass("hideQuestionMainDiv");
			currentQuestion=question;
			
			$("#questionMainDiv-"+currentQuestion).removeClass("hideQuestionMainDiv");
			$("#questionMainDiv-"+currentQuestion).addClass("showQuestionMainDiv");
			//Jump to top
			$("html,body").animate({
		        scrollTop: $("#toTop").offset().top},
		        'slow');
			console.log("Exiting jumpToQuestion() currentQuestion: "+currentQuestion);
		
		}
		
		window.markForReview = function markForReview(question){
			console.log("Entering markForReview() got question "+question);
			//bookmarkIconInQuestionStatus
			
			if($("#bookmarkIcon-"+question).hasClass("fa-bookmark-o")){

				$("#bookmarkIcon-"+question).removeClass("fa-bookmark-o");
				$("#bookmarkIcon-"+question).addClass("fa-bookmark");
				$("#bookmarkIconInQuestionStatus-"+question).removeClass("fa-bookmark-o");
				$("#bookmarkIconInQuestionStatus-"+question).addClass("fa-bookmark");
			
			}else{
				$("#bookmarkIcon-"+question).removeClass("fa-bookmark");
				$("#bookmarkIcon-"+question).addClass("fa-bookmark-o");
				if($("#bookmarkIconInQuestionStatus-"+question).hasClass("fa-bookmark")){
					$("#bookmarkIconInQuestionStatus-"+question).removeClass("fa-bookmark");
					$("#bookmarkIconInQuestionStatus-"+question).addClass("fa-bookmark-o");
				}
				
			}
			
			
			console.log("Exiting markForReview() question "+question);
		}
		
		function calcProgress(){
			var progressPercentage = ""+ ((noOfQuestionsAttemptted /noOfQuestions)*100) + "%";
			console.log("In calcProgress progressPercentage: "+progressPercentage);
			$("#progressDiv").animate({
				width: progressPercentage},
		        'slow');
		}
		
		//NOT USED KEPT FOR FUTURE USE
		function shouldShowPreviousQuestionButton(){
			if(currentQuestion == 1){
				$("#previousQuestionButton-"+currentQuestion).hide();
				console.log("Hiding previousQuestionButton");
			}else{
				$("#previousQuestionButton-"+currentQuestion).shwo();
				console.log("Showing previousQuestionButton");
			}
		}

		//NOT USED KEPT FOR FUTURE USE
		function shouldShowNextQuestionButton(){
			if(currentQuestion == noOfQuestions){
				$("#nextQuestionButton-"+currentQuestion).hide();
				console.log("Hiding nextQuestionButton");
			}else{
				$("#nextQuestionButton-"+currentQuestion).shwo();nextQuestionButton
			}
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
		

		//submitTest end
		
		function submitTest(message){
			 console.log("SUBMITING TEST got message: "+message);
			 //ajax to save test start
	    		var body = {
	    			'testId' : testId,
	    			'sapid' : sapid,
	    			'attempt' : totalAttempt
	    		};
	    		console.log(body);
	    		$.ajax({
	    			type : 'POST',
	    			url : '/exam/m/saveStudentsTestDetails',
	    			data: JSON.stringify(body),
	                contentType: "application/json",
	                dataType : "json",
	                success : function(data) {
	                	console.log("iN AJAX SUCCESS");
	                	console.log(data);
	                	showSnackBar("Test  Submitted!");
	                	// Store
	                	  localStorage.setItem("testSubmitted", "Yes");
	                	 
	             			
	             		
	             
	                	// Retrieve
	                	isTestOver=true;
	                	checkIsTestOver(message);
			    	},
	    			error : function(result) {
	                	console.log("iN AJAX eRROR");
	    				console.log(result);
	    				showSnackBar("Error in submitting Test. Try Again.");
		    		}
	    		});
	    		//ajax to save question reponse end
			 console.log("SUBMITED TEST");
			 
		 
		}
		
		function checkIsTestOver(message){
			console.log("IN checkIsTestOver() got message: "+message);
			if(isTestOver){

		  	   	//alert(localStorage.getItem("testSubmitted"));
				window.location.href = "/exam/viewTestDetailsForStudents?id="+testId+"&message="+message;
			}
		}
		
		function saveStudentsQuestionResponse(testId,questionId){
			console.log("In saveStudentsQuestionResponse got testId: "+testId+" questionId: "+questionId);
			//alert("In saveStudentsQuestionResponse got testId: "+testId+" questionId: "+questionId);
			
		} 
		
		window.checkFields = function checkFields(h4Id,isCaseStudy) {
			if(isCaseStudy){
				$(h4Id).addClass("greenLeftBorder");
				console.log("In Checkfields() setting greenBottomBorder to h4Id:"+h4Id);
			}
			questionUpdated = true;
			if(currentQuestion == noOfQuestions){
	            console.log("Updating lastQuestionUpdated var. ");
				lastQuestionUpdated= true;
			}
			console.group("checkFields");
			if($("input[type=radio]").is(":checked") || $("input[type=checkbox]").is(":checked")){
				postAjaxSuccessOperations('submitTest');
				console.debug("field checked");	
			}else{
				console.debug("field not checked");
			}
			
			
			console.groupEnd();
		}
		
		console.log("Page Ready!");
		
		// Get todays date and time
		  var now = new Date().getTime();
		  
		//Add test's duration to current time
		var countDownDate = now + duration*60*1000;
		var remainingTimeCounter = 0; 
		
		// Update the count down every 1 second
		var x = setInterval(function() {

		  // Get todays date and time
		  var now = new Date().getTime();
		  
		  
		  // Find the distance between now an the count down date
		  var distance = countDownDate - now;
		  // Time calculations for days, hours, minutes and seconds
		  var days = Math.floor(distance / (1000 * 60 * 60 * 24));
		  var hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
		  var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
		  var seconds = Math.floor((distance % (1000 * 60)) / 1000);

			
		  $("#hoursSpan").text(hours);
		  $("#minutesSpan").text(minutes);
		  $("#secondsSpan").text(seconds);
		  
		  // If the count down is finished, write some text
		  if (distance <= 0) {
			  console.log("BEFORE TIMEOVER");
			  submitTest("testTimeOut");
			  console.log("AFTER TIMEOVER");
		  }
		  
		  if(minutes % 5 ==0){
			  if(remainingTimeCounter == 0){
			  	console.log('Updating remaining time');
			  	updateRemainingTime();
			  	remainingTimeCounter = 1;
			  }
		  }else{
			  remainingTimeCounter = 0;
		  }
		  
		}, 1000);

		//ajax call to update remaining time start
		function updateRemainingTime(){
	    		var body = {
	    			'testId' : testId,
	    			'sapid' : sapid,
	    			'attempt' : totalAttempt
	    		};
	    		console.log(body);
	    		$.ajax({
	    			type : 'POST',
	    			url : '/exam/updateRemainingTime',
	    			data: JSON.stringify(body),
	                contentType: "application/json",
	                dataType : "json",
	                success : function(data) {
	                	console.log("iN AJAX SUCCESS");
	                	console.log(data);
	                	
			    	},
	    			error : function(result) {
	                	console.log("iN AJAX eRROR");
	    				console.log(result);
	    				showSnackBar("Error in updating remaining time.");
		    		}
	    		});
		}
		
		//ajax call to update ramining time end
 
	}); //end of doc ready()
	
</script>


<!-- FOR Custome text area -->

<!-- Include jQuery lib. -->
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>

<!-- Include Editor JS files. -->
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/froala-editor/2.5.1//js/froala_editor.pkgd.min.js"></script>

<!-- Initialize the editor. -->
<script>
  $(function() {
    $('textarea').froalaEditor();
  });
  
  $('textarea').on('froalaEditor.initialized', function (e, editor) {
	  editor.events.on('drop', function () { return false; }, true);
	});
</script>
<!-- end -->


<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>  
  <script>
$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip(); 
});

</script>

<script>

// Hiding Next Button
 
 function disableNextButton(questionCurrentCount){
	console.group("disableNextButton");
	
	questionCurrentCount ++;
	console.debug("questionCurrentCount = "+questionCurrentCount);
	
	var testQuestionsCount = <c:out value="${empty fn:length(testQuestions) ? '0' : fn:length(testQuestions)}" />;

	console.debug(testQuestionsCount);

	if(testQuestionsCount == questionCurrentCount){// Last question reached
		console.debug("last question reached");
		$('.nextQuestionButton').hide();
	}else{
		console.debug("last question not reached");
		$('.nextQuestionButton').show();
	}
	console.groupEnd();
}


// Showing next button on click of previous button
function showNextButtonOnClickOfPreviousButton(){
	$('.nextQuestionButton').show();
}


</script>
</body>
</html>