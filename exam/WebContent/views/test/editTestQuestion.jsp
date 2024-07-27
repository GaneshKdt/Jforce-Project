
<!DOCTYPE html>
<html lang="en">

<%@page import="com.nmims.beans.Person"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Edit Test Question" name="title" />
</jsp:include>


<link
	href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css"
	rel="stylesheet">
<script src="https://code.jquery.com/jquery-3.3.1.js"
	integrity="sha256-2Kok7MbOyxpgUVvAk/HJ2jigOSYS2auK4Pfzbm7uH60="
	crossorigin="anonymous"></script>
<script
	src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>

<!-- Roboto Web Font -->
<link
	href="https://fonts.googleapis.com/css?family=Roboto:regular,bold,italic,thin,light,bolditalic,black,medium&amp;lang=en"
	rel="stylesheet">

<!-- Include CSS for icons. -->
<link
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.4.0/css/font-awesome.min.css"
	rel="stylesheet" type="text/css" />

<!-- Include Editor style. -->
<link
	href="https://cdnjs.cloudflare.com/ajax/libs/froala-editor/2.5.1/css/froala_editor.pkgd.min.css"
	rel="stylesheet" type="text/css" />
<link
	href="https://cdnjs.cloudflare.com/ajax/libs/froala-editor/2.5.1/css/froala_style.min.css"
	rel="stylesheet" type="text/css" />
<link data-require="sweet-alert@*" data-semver="0.4.2" rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/sweetalert/1.1.3/sweetalert.min.css" />


<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/js/ckeditor/ckeditor_responsive_table/plugin.css">


<style>
body {
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

#questionsPreviewSection {
	overflow: scroll;
	height: 500px !important;
	margin-top: 10px;
}

#questionsPreviewSection::-webkit-scrollbar {
	width: 8px;
}

#questionsPreviewSection::-webkit-scrollbar-thumb {
	background-color: #d2232a;
	border-radius: 15px;
}

.questionImage {
	width: 100%;
	margin: 5px 10px;
}

.questionImageContainer {
	margin: 0px 0px 15px 0px;
}

.swal-button--confirm {
	background-color: #d2232a;
}

.fa-bookmark-o, .fa-bookmark {
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
	float: right;
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
	font-size: 16px;
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

.questionMainDiv { //
	display: none;
}

#question-textArea {
	width: 100%;
}

.optionDataInput {
	width: 100%;
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

#subQuestionsContainer1 {
	overflow: scroll;
	height: 350px;
	!
	important;
}

#subQuestionsContainer1::-webkit-scrollbar {
	width: 12px;
}

#subQuestionsContainer1::-webkit-scrollbar-thumb {
	background-color: #d2232a;
	border-radius: 15px;
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

.orangeLeftBorder {
	padding-left: 10px;
	border-left: 5px solid #337ab7;
	border-radius: 2px;
}

.greenLeftBorder {
	padding-left: 10px;
	border-left: 5px solid #49a54e;
	border-radius: 2px;
}

/* CUSTOM RADIO BUTTON START */

/* The inputContainer */
.inputContainer {
	display: block;
	position: relative;
	padding-left: 0px;
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

	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<!-- breadcrums as per add/edit test start -->
		<div class="sz-breadcrumb-wrapper">
			<div class="container-fluid">
				<ul class="sz-breadcrumbs">
					<li><a href="/exam/">Exam</a></li>
					<li><a href="/exam/viewTestsForFaculty">Tests</a></li>
					<li><a
						href="/exam/addTestQuestionsForFacultyForm?id=${test.id}">Manage
							Questions</a></li>
					<li><a href="#">Edit Question</a></li>

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

		<!-- breadcrums as per add/edit test start -->

		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">



						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>

							<!-- Code For Page Goes in Here Start -->
							<div class="container">
								<!-- <div class="well" style="text-align:center;">
														<ul class="pagination pagination-lg">
																<li	
																	<c:if test="${testQuestions[0].id == testQuestion.id }">
																 		class="disabled"
																 	</c:if>
																 >
																 	<c:forEach var="question" items="${testQuestions}" varStatus="status">
																	<c:if test="${question.id == testQuestion.id }">
																 	
																 	<a href="/exam/editTestQuestion?id=${testQuestions[status.count - 1].id}"	
																 	>
																		<i class="fa fa-angle-double-left" style="font-size:24px"></i>
																 	</a>
																 	</c:if>
																 	
														  			</c:forEach>
																 	
																 </li>
															<c:forEach var="question" items="${testQuestions}" varStatus="status">
																 <li
																 	<c:if test="${question.id == testQuestion.id }">
																 		class="active"
																 	</c:if>
																 >
																 	<a href="/exam/editTestQuestion?id=${question.id}"	style="z-index : 1;"
																 	>
																 		${status.count}
																 	</a>
																 	
																 </li>
														  	</c:forEach>
														  	<li	
																	<c:if test="${testQuestions[fn:length(testQuestions)].id == testQuestion.id }">
																 		class="disabled"
																 	</c:if>
																 >
																 	<a href="/exam/editTestQuestion?id=${testQuestion.id + 1}"	
																 	>
																		<i class="fa fa-angle-double-right" style="font-size:24px"></i>
																 	</a>
																 	
																 </li>
														  	
														</ul>
													
													</div> -->

								<form id="studentTestForm-1">
									<div id="questionMainDiv-1" class="questionMainDiv">

										<div class="card">
											<div class="card-header bg-white p-a-1"
												style="background-color: #ffffff">
												<div id="" class="">
													<!-- display name of type of question start -->
													<legend>Edit Question Details</legend>
													<small style="color: #708090;">Question Type : <c:choose>
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


												<div class="media">
													<div class="media-body  ">

														<div class="form-group">
															<label for="section">Section : </label> <select
																id="sectionId" name="sectionId" required>
																<option value="">Select Section</option>

																<c:forEach var="sectionbean" items="${sectionList}" varStatus="status">
																	<c:if test="${sectionbean.id == testQuestion.sectionId}">
																		<option value="${sectionbean.id}" selected>${sectionbean.sectionName}</option>
																	</c:if>
																	<c:if test="${sectionbean.id != testQuestion.sectionId}">
																		<option value="${sectionbean.id}">${sectionbean.sectionName}</option>
																	</c:if>
																</c:forEach>

															</select>
														</div>

													</div>
												</div>

												<div class="media">
													<div class="media-body  ">

														<div class="form-group">
															<label for="marks">Question Marks : </label> <select
																id="marks" name="marks" required>
																<option value="">Select Question Marks</option>

																<c:forEach var="i" begin="1" end="10">
																		<option value="${i-0.5}">${i-0.5}</option>
																	<c:if test="${i == testQuestion.marks}">
																		<option value="${i}" selected>${i}</option>
																	</c:if>
																	<c:if test="${i != testQuestion.marks}">
																		<option value="${i}">${i}</option>
																	</c:if>
																</c:forEach>

															</select>
														</div>

													</div>
												</div>


												<div class="media">
													<div class="media-body  ">

														<div class="form-group">
															<label for="copyCaseThreshold">Question
																CopyCaseThreshold : </label> <select id="copyCaseThreshold"
																name="copyCaseThreshold" required>
																<option value="">Select Question
																	CopyCaseThreshold</option>

																<c:forEach var="i" begin="1" end="100">
																	<c:if test="${i == testQuestion.copyCaseThreshold}">
																		<option value="${i}" selected>${i}</option>
																	</c:if>
																	<c:if test="${i != testQuestion.copyCaseThreshold}">
																		<option value="${i}">${i}</option>
																	</c:if>
																</c:forEach>

															</select>
														</div>

													</div>
												</div>

												<div class="media">
													<div class="media-body  ">

														<div class="form-group">
															<label for="question-textArea">Question : </label>
															<textarea id="question-textArea" class="form-control"
																required>${testQuestion.question}</textarea>
														</div>

													</div>
												</div>
											</div>

											<c:choose>
												<c:when test="${testQuestion.type == 7}">

													<input type="hidden" value="${testQuestion.id}"
														id="hiddenQuestionId-1">


													<div class="card-block p-a-2"
														style="background-color: #ffffff">
														<div class="row">

															<div class="col-sm-3" style=""></div>
															<div class="col-sm-6" style="">
																<iframe style="width: 100%; min-height: 400px;"
																	src='${testQuestion.url}' frameborder='0'
																	webkitAllowFullScreen mozallowfullscreen
																	allowFullScreen></iframe>
															</div>

															<div class="col-sm-3" style=""></div>
														</div>


													</div>

													<div class="card-footer clearfix"
														id="options${testQuestion.id}">
														<div class="form-group">

															<c:forEach var="tempOption"
																items="${testQuestion.optionsList}"
																varStatus="optionStatus">
																<div class="row well"
																	style="margin-bottom: 10px !important;">
																	<div class="col-xs-1">

																		<label class="c-input c-checkbox"> <input
																			type="checkbox" id="checkField11"
																			value="${tempOption.id}"
																			<c:if test = "${tempOption.isCorrect == 'Y'}">
																							 checked 
																							</c:if>
																			onclick="checkFields('${testQuestion.id}')">
																			<span class="c-indicator"></span>
																		</label>
																	</div>
																	<div class="col-xs-10">
																		<input type="text" value="${tempOption.optionData}"
																			id="optionData-${tempOption.id}"
																			class="optionDataInput"
																			style="color: black !important;">
																	</div>
																	<div class="col-xs-1">
																		<a
																		class="deleteOptionLink"
																		data-qid="${testQuestion.id}" data-oid="${tempOption.id}" 
																		href="/exam/deleteTestQuestionOptionById?id=${tempOption.id}&testId=${test.id}&questionId=${testQuestion.id}&questionId=${testQuestion.id}">
																			<i class="fa-solid fa-trash" title="delete option"></i>
																		</a>
																	</div>
																</div>
																<br>
																<br>
															</c:forEach>


														</div>


													</div>
												</c:when>
												<c:when test="${testQuestion.type == 6}">

													<input type="hidden" value="${testQuestion.id}"
														id="hiddenQuestionId-1">


													<div class="card-block p-a-2"
														style="background-color: #ffffff">
														<div class="col-sm-12 questionImageContainer">
															<img class="questionImage" alt="Question Picture"
																src="${testQuestion.url}">

														</div>


													</div>

													<div class="card-footer clearfix"
														id="options${testQuestion.id}">
														<div class="form-group">

															<c:forEach var="tempOption"
																items="${testQuestion.optionsList}"
																varStatus="optionStatus">


																<div class="row well"
																	style="margin-bottom: 10px !important;">
																	<div class="col-xs-1">

																		<label class="c-input c-checkbox"> <input
																			type="checkbox" id="checkField11"
																			value="${tempOption.id}"
																			<c:if test = "${tempOption.isCorrect == 'Y'}">
																							 checked 
																							</c:if>
																			onclick="checkFields('${testQuestion.id}')">
																			<span class="c-indicator"></span>
																		</label>
																	</div>
																	<div class="col-xs-10">
																		<input type="text" value="${tempOption.optionData}"
																			id="optionData-${tempOption.id}"
																			class="optionDataInput"
																			style="color: black !important;">
																	</div>
																	<div class="col-xs-1">
																		<a class="deleteOptionLink" data-qid="${testQuestion.id}" data-oid="${tempOption.id}" href="/exam/deleteTestQuestionOptionById?id=${tempOption.id}&testId=${test.id}&questionId=${testQuestion.id}&questionId=${testQuestion.id}">
																			<i class="fa-solid fa-trash" title="delete option"></i>
																		</a>
																	</div>
																</div>

																<br>
																<br>

															</c:forEach>


														</div>


													</div>
												</c:when>
												<c:when test="${testQuestion.type == 5}">

													<input type="hidden" value="${testQuestion.id}"
														id="hiddenQuestionId-1">

													<div class="card-block p-a-2"
														id="options${testQuestion.id}">
														<div class="form-group">

															<c:forEach var="tempOption"
																items="${testQuestion.optionsList}"
																varStatus="optionStatus">
																<c:choose>
																	<c:when test="${testQuestion.type == 5}">
																		<div class="row well"
																			style="margin-bottom: 10px !important;">
																			<div class="col-xs-1">
																				<label class="inputContainer"> <input
																					type="radio"
																					name="selectedOption${testQuestion.id}"
																					id="checkField11" value="${tempOption.id}"
																					<c:if test = "${tempOption.isCorrect == 'Y' }">
																							 checked 
																							</c:if>
																					onclick="checkFields('${testQuestion.id}')">

																					<span class="radioCheckmark"></span>
																				</label>
																			</div>
																			<div class="col-xs-11">
																				<input type="text" value="${tempOption.optionData}"
																					id="optionData-${tempOption.id}"
																					class="optionDataInput"
																					style="color: black !important;">
																			</div>
																			<!-- 
																			<div class="col-xs-1">
																				<a class="deleteOptionLink" data-qid="${testQuestion.id}" data-oid="${tempOption.id}" href="/exam/deleteTestQuestionOptionById?id=${tempOption.id}&testId=${test.id}&questionId=${testQuestion.id}&questionId=${testQuestion.id}">
																					<i class="fa-solid fa-trash" title="delete option"></i>
																				</a>
																			</div> 
																			 -->
																		</div>

																	</c:when>
																</c:choose>
															</c:forEach>


														</div>


													</div>
													<div class="card-footer clearfix "
														style="background-color: #ffffff">
														<div class="col-sm-12">

															<div class="col-sm-6"></div>


														</div>


													</div>
												</c:when>
												<c:when test="${testQuestion.type == 4}">
													<div class="">
														<input type="hidden" value="${testQuestion.id}"
															id="hiddenQuestionId-1">

														<div class="card-block p-a-2">
															<div class="form-group">
																<%-- 	<textarea id="textArea-${testQuestion.id}" rows="20"   wrap="hard" required></textarea>
																		 --%>
															</div>
														</div>
														<div class="card-footer clearfix "
															style="background-color: #ffffff">
															<div class="col-sm-12">

																<div class="col-sm-6"></div>


															</div>


														</div>
													</div>
												</c:when>
												<c:when test="${testQuestion.type == 3}">

													<!-- Display subQuestions if question type is 3 i.e. caseStudy -->
													<div id="subQuestionsContainer">
														<div class="well">

															<h5>Case-Study Passage :</h5>
															<div class="form-group">
																<textarea id="description-textArea" class="form-control"
																	style="text-align: left!importan; width: 100%;"
																	required>${testQuestion.description}</textarea>
															</div>
														</div>

														<div class="well">
															<h5>Questions :</h5>
															<input type="hidden" value="${testQuestion.id}"
																id="hiddenQuestionId-1">

															<!-- literate through casestudy questions start -->
															<c:forEach var="subQuestion"
																items="${testQuestion.subQuestionsList }"
																varStatus="sQStatus">


																<div class="card">
																	<div
																		class="card-header bg-white p-a-1 orangeLeftBorder"
																		id="subQuestionH4-${subQuestion.id}"
																		style="background-color: #ffffff">
																		<div class="media">
																			<div class="media-body  media-middle">
																				<div class="row " style="">
																					<div class="col-xs-1">
																						<h6>Q${sQStatus.count}</h6>
																					</div>
																					<div class="col-xs-11">
																						<div class="form-group">
																							<textarea
																								id="subQuestion-textArea-${subQuestion.id}"
																								style="text-align: left!importan; width: 100%;"
																								required>${subQuestion.question }</textarea>
																						</div>
																					</div>
																				</div>
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
																						<div class="row well"
																							style="margin-bottom: 10px !important;">
																							<div class="col-xs-1">
																								<label class="inputContainer"> <input
																									type="radio"
																									name="selectedOption${subQuestion.id}"
																									id="checkField11" value="${tempOption.id}"
																									<c:if test = "${tempOption.isCorrect == 'Y' }">
																							 checked 
																							</c:if>
																									onclick="checkFields('${testQuestion.id}')">

																									<span class="radioCheckmark"></span>
																								</label>
																							</div>
																							<div class="col-xs-10">
																								<input type="text"
																									value="${tempOption.optionData}"
																									id="optionData-${tempOption.id}"
																									class="optionDataInput"
																									style="color: black !important;">
																							</div>
																							<div class="col-xs-1">
																								<a class="deleteOptionLink" data-qid="${testQuestion.id}" data-oid="${tempOption.id}" href="/exam/deleteTestQuestionOptionById?id=${tempOption.id}&testId=${test.id}&questionId=${testQuestion.id}&questionId=${testQuestion.id}">
																									<i class="fa-solid fa-trash" title="delete option"></i>
																								</a>
																							</div>
																						</div>




																					</c:when>
																					<c:when test="${subQuestion.type == 2}">
																						<div class="row well"
																							style="margin-bottom: 10px !important;">
																							<div class="col-xs-1">

																								<label class="c-input c-checkbox"> <input
																									type="checkbox"
																									id="checkFieldSubQuesiton-${tempOption.id}"
																									value="${tempOption.id}"
																									<c:if test = "${tempOption.isCorrect == 'Y'}">
																							 checked 
																							</c:if>
																									onclick="checkFields('${testQuestion.id}')">
																									<span class="c-indicator"></span>
																								</label>
																							</div>
																							<div class="col-xs-10">
																								<input type="text"
																									value="${tempOption.optionData}"
																									id="optionData-${tempOption.id}"
																									class="optionDataInput"
																									style="color: black !important;">
																							</div>
																							<div class="col-xs-1">
																								<a class="deleteOptionLink" data-qid="${testQuestion.id}" data-oid="${tempOption.id}" href="/exam/deleteTestQuestionOptionById?id=${tempOption.id}&testId=${test.id}&questionId=${testQuestion.id}&questionId=${testQuestion.id}">
																									<i class="fa-solid fa-trash" title="delete option"></i>
																								</a>
																							</div>
																						</div>

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

																	<div class="col-sm-6"></div>


																</div>


															</div>


														</div>


													</div>


												</c:when>
												<c:otherwise>
													<input type="hidden" value="${testQuestion.id}"
														id="hiddenQuestionId-1">

													<div class="card-block p-a-2"
														id="options${testQuestion.id}">
														<label>Options : </label>
														<div class="form-group">

															<c:forEach var="tempOption"
																items="${testQuestion.optionsList}"
																varStatus="optionStatus">
																<c:choose>
																	<c:when test="${testQuestion.type == 1}">
																		<div class="row well"
																			style="margin-bottom: 10px !important;">
																			<div class="col-xs-1">
																				<label class="inputContainer"> <input
																					type="radio"
																					name="selectedOption${testQuestion.id}"
																					id="checkField11" value="${tempOption.id}"
																					<c:if test = "${tempOption.isCorrect == 'Y' }">
																							 checked 
																							</c:if>
																					onclick="checkFields('${testQuestion.id}')">

																					<span class="radioCheckmark"></span>
																				</label>
																			</div>
																			<div class="col-xs-10">
																				<input type="text" value="${tempOption.optionData}"
																					id="optionData-${tempOption.id}"
																					class="optionDataInput"
																					style="color: black !important;">
																			</div>
																			<div class="col-xs-1">
																				<a class="deleteOptionLink" data-qid="${testQuestion.id}" data-oid="${tempOption.id}" href="/exam/deleteTestQuestionOptionById?id=${tempOption.id}&testId=${test.id}&questionId=${testQuestion.id}&questionId=${testQuestion.id}">
																					<i class="fa-solid fa-trash" title="delete option"></i>
																				</a>
																			</div>
																		</div>

																	</c:when>
																	<c:when test="${testQuestion.type == 2}">
																		<div class="row well"
																			style="margin-bottom: 10px !important;">
																			<div class="col-xs-1">

																				<label class="c-input c-checkbox"> <input
																					type="checkbox" id="checkField11"
																					value="${tempOption.id}"
																					<c:if test = "${tempOption.isCorrect == 'Y'}">
																							 checked 
																							</c:if>
																					onclick="checkFields('${testQuestion.id}')">
																					<span class="c-indicator"></span>
																				</label>
																			</div>
																			<div class="col-xs-10">
																				<input type="text" value="${tempOption.optionData}"
																					id="optionData-${tempOption.id}"
																					class="optionDataInput"
																					style="color: black !important;">
																			</div>
																			<div class="col-xs-1">
																				<a class="deleteOptionLink" data-qid="${testQuestion.id}" data-oid="${tempOption.id}" href="/exam/deleteTestQuestionOptionById?id=${tempOption.id}&testId=${test.id}&questionId=${testQuestion.id}&questionId=${testQuestion.id}">
																					<i class="fa-solid fa-trash" title="delete option"></i>
																				</a>
																			</div>
																		</div>
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

															<div class="col-sm-6"></div>


														</div>


													</div>
												</c:otherwise>
											</c:choose>

											<button type="submit" class="btn btn-warning"
												style="text-transform: none; margin: 0px 0px 0px 5px; padding: 8px 16px; font-size: 0.9rem; font-weight: unset; cursor: pointer;">Update</button>
											<button class="btn btn-warning"
												style="text-transform: none; margin: 0px 0px 0px 5px; padding: 8px 16px; font-size: 0.9rem; font-weight: unset; cursor: pointer; background-color: green;"
												onclick="window.history.back();">Back</button>


										</div>

									</div>
								</form>

							</div>
							<!-- Code For Page Goes in Here End -->
						</div>

					</div>

				</div>
			</div>
		</div>
	</div>

	<div id="snackbar">Some text some message..</div>
	<jsp:include page="../adminCommon/footer.jsp" />

	<script src="https://unpkg.com/sweetalert/dist/sweetalert.min.js"></script>

	<script type="text/javascript"
		src="//cdn.ckeditor.com/4.14.0/standard-all/ckeditor.js"></script>
	<script type="text/javascript"
		src="//cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.0/MathJax.js?config=TeX-AMS_HTML"></script>


	<script src="${pageContext.request.contextPath}/assets/js/ckeditor/ckeditor_responsive_table/plugin.js"></script>

	<script type="text/javascript">

questionsTextEditorObj =  CKEDITOR.replace(
		'question-textArea',
		{
			extraPlugins : 'uploadimage,image2,mathjax,autogrow,colorbutton,font,justify,print,tableresize,uploadfile,pastefromword,liststyle,pagebreak',
			//      extraPlugins: 'colorbutton,font,justify,print,tableresize,uploadimage,uploadfile,pastefromword,liststyle,pagebreak',

			
			mathJaxLib : 'https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.0/MathJax.js?config=TeX-AMS_HTML',
			uploadUrl : '/exam/ckeditorFileUpload',

		      // Configure your file manager integration. This example uses CKFinder 3 for PHP.
		      filebrowserBrowseUrl: '/exam/ckeditorFileUpload',
		      filebrowserImageBrowseUrl: '/exam/ckeditorFileUpload',
		      filebrowserUploadUrl: '/exam/ckeditorFileUpload',
		      filebrowserImageUploadUrl: '/exam/ckeditorFileUpload',
			

		      // Load the default contents.css file plus customizations for this sample.
		      contentsCss: [
		        'https://cdn.ckeditor.com/4.14.0/full-all/contents.css',
		        'https://ckeditor.com/docs/vendors/4.14.0/ckeditor/assets/css/pastefromgdocs.css'
		        
		      ],

		      // Configure the Enhanced Image plugin to use classes instead of styles and to disable the
		      // resizer (because image size is controlled by widget styles or the image takes maximum
		      // 100% of the editor width).
		      image2_alignClasses: ['image-align-left', 'image-align-center', 'image-align-right'],
		      image2_disableResizer: true,
		      
		      autoGrow_minHeight: 200,
		      autoGrow_maxHeight: 600,
		      autoGrow_bottomSpace: 50,

		}
		
	);

	/*
	 let editor = CKEDITOR.replace(
						'question-textArea',
						{
							extraPlugins : 'mathjax',
							mathJaxLib : 'https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.0/MathJax.js?config=TeX-AMS_HTML'
						}); 
	*/	
	 /*
	 editor.on( 'change', function( evt ) {
		    // getData() returns CKEditor's HTML content.
		    console.log( 'Total bytes: ' + evt.editor.getData() );
		    $('#description-singlemulti').html(evt.editor.getData());
		});
	 */
</script>


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
		var questionTextArea = $("#question-textArea").val();
 		var trimmedQuestionTextArea =  $.trim(questionTextArea);
 		$("#question-textArea").val(trimmedQuestionTextArea);
		var testId = ${test.id};
		var sapid = '${userId}';
		var currentQuestion = 1;
		var noOfQuestionsAttemptted = 0;
	   	var duration = ${test.duration};
		var noOfQuestionsSkipped = 0;
		var isTestOver = false;
		var questionUpdated = false;
		var lastQuestionUpdated = false;
		var jumpToQuestionPlaceholder = 1;
		
		var questionDetails ={};
		
		tempQuestion='';
		questionDetails = {
								"id": ${testQuestion.id},
								"question": "",
								"description": "${testQuestion.description}",
								"type": ${testQuestion.type},
								"divId": 1,
								"marks": ${testQuestion.marks},
								"sectionId": ${testQuestion.sectionId},
								"copyCaseThreshold": ${testQuestion.copyCaseThreshold},
								"optionsList": [
									<c:forEach var="option" items="${testQuestion.optionsList}"	varStatus="oStatus">
									{
										"id" : ${option.id},
										"questionId" : ${option.questionId},
										"optionData" : '',
										"isCorrect" : '${option.isCorrect}', 
										"selected" : '${option.selected}', 
									},
									</c:forEach>
									],
								"isSubQuestion": ${testQuestion.isSubQuestion}, 
								"marks" : ${testQuestion.marks},
								"url": "${testQuestion.url}",
								"subQuestionsList": [
												<c:forEach var="subQuestion" items="${testQuestion.subQuestionsList}"	varStatus="status">
													{
														"id":${subQuestion.id},
														"type":${subQuestion.type},
														"question": "${subQuestion.question}",
														"marks":${subQuestion.marks},
														"description": "  ",
														"optionsList": [
															<c:forEach var="option" items="${subQuestion.optionsList}"	varStatus="oStatus">
															{
																"id" : ${option.id},
																"questionId" : ${option.questionId},
																"optionData" : '',
																"isCorrect" : '${option.isCorrect}', 
																"selected" : '${option.selected}', 
															},
															</c:forEach>
															],
														"isSubQuestion": ${subQuestion.isSubQuestion},
														"mainQuestionId" :  ${testQuestion.id},
													},
												</c:forEach>
												]
									};
		
		console.log("Got questionDetails =====>");
		console.log(questionDetails);
		console.log("Got questionDetails <=====");
		
			try {
				$("#studentTestForm-1").submit(function(e){
					console.log('Called Submit')
				    e.preventDefault();

					questionId=$("#hiddenQuestionId-1").val();
					submitTestQuestionForm(questionId,'nextQuestion');
				});
			}
			catch(err) {
			    console.log("Catch error : "+err.message);
			}
		
		function submitTestQuestionForm(qId,actionTo){


			
		    let saved = validateAndSaveAnswer(qId,actionTo);
		    if(saved){
		    	console.log("Saved Answer.");
	 	    }else{
		    	console.log("Error in saving answer.");
	 	    }
		}
		
		//validateAndSaveAnswer Start
		function validateAndSaveAnswer(questionId,actionTo){
			console.log("In validateAndSaveAnswer() questionId : "+questionId+" actionTo : "+actionTo);
			
			//get question's text for richtext editorstart
			
		 	console.log("CKEDITOR.instances : ",CKEDITOR.instances);
			let key = 'question-textArea';
			let questionsTextEditorsText = CKEDITOR.instances[key].getData();
			
		 	console.log("questionsTextEditorsText : ",questionsTextEditorsText);
			
		 	questionDetails.question = questionsTextEditorsText;

		 	let sectionId = $('#sectionId').val();
			
		 	questionDetails.sectionId = sectionId;
		 	
			//get question's text for richtexteditor end
		 	
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
						 		console.log("Before for loop");
						 		for(let i = 0; i < inputArray.length; i++){
						 			console.log("Option button Option: "+i+" . Value: "+$(inputArray[i]).val());
							    	optionInput =inputArray[i];
							    	questionDetails = getUpdatedOptionsDetails(questionDetails,optionInput,isSubQuestion);
								}
						 		console.log("After for loop");	
						 		
						 			
						 		
						 		var saved = saveAnswerAjax(questionDetails)
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
		
		function getUpdatedOptionsDetails(questionDetails,optionInput,isSubQuestion){
			let optionId = $(optionInput).val(); 
			console.log("In getUpdatedOptionsDetails got optionId: "+optionId+" $(optionInput).val() : "+$(optionInput).val() );
    		console.log("In getUpdatedOptionsDetails got optionsList");
    		console.log(questionDetails.optionsList);
    		
			for(var o=0; o < questionDetails.optionsList.length; o++){
    			optionDetails = questionDetails.optionsList[o];
    			console.log("In getUpdatedOptionsDetails got optionDetails: ");
    			console.log(optionDetails);
    			if( optionId == optionDetails.id ){
    				console.log("Got escapeHtmlCharacters($(optionData-optionId).val()) : ");
    				console.log(escapeHtmlCharacters($("#optionData-"+optionId).val()));
    				questionDetails.optionsList[o].optionData = escapeHtmlCharacters($("#optionData-"+optionId).val());
    				if($(optionInput).is(":checked")){
    						console.log("Selected Option: "+$(optionInput).attr("name")+" . Value: "+$(optionInput).val());
    						questionDetails.optionsList[o].isCorrect = "Y";
    				}else{
    					questionDetails.optionsList[o].isCorrect = "N";
        			}
    				return questionDetails;
    			}
    		}
			
			
			return questionDetails;
		}
		function escapeHtmlCharacters(text) {
			console.log("In escapeHtmlCharacters got text : "+text);
			if(text !=null){ 
			return text
			      .replace(/&/g, "&amp;")
			      .replace(/</g, "&lt;")
			      .replace(/>/g, "&gt;")
			      .replace(/"/g, "&quot;")
			      .replace(/'/g, "&#039;");
				}
			}
		
		function answuerOftype3(questionDetails,actionTo){ // if question is of type 3 i.e. Case Study
			var csId = questionDetails.id;
			var csSubQuestions = questionDetails.subQuestionsList;
			tempOptionCount=0;
			questionDetails.question = escapeHtmlCharacters($("#question-textArea").val());
			questionDetails.description = escapeHtmlCharacters($("#description-textArea").val());
			
			for(var sq=0; sq < questionDetails.subQuestionsList.length; sq++){
				console.log("#subQuestion-textArea-"+questionDetails.subQuestionsList[sq].id);
				questionDetails.subQuestionsList[sq].question = escapeHtmlCharacters($("#subQuestion-textArea-"+questionDetails.subQuestionsList[sq].id).val());
				

			    	var answer;
					tempOptionCount=1;
					inputArraySelector = "#options"+questionDetails.id+"-"+questionDetails.subQuestionsList[sq].id+" :input";
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
				 		console.log("Before for loop");
				 		for(let i = 0; i < inputArray.length; i++){
				 			console.log("Option button Option: "+i+" . Value: "+$(inputArray[i]).val());
					    	optionInput =inputArray[i];
					    	let optionId = $(optionInput).val(); 
							
							for(var o=0; o < questionDetails.subQuestionsList.length; o++){
				    			optionDetails = questionDetails.subQuestionsList[sq].optionsList[o];
				    			if( optionId == optionDetails.id ){
				    				questionDetails.subQuestionsList[sq].optionsList[o].optionData = escapeHtmlCharacters($("#optionData-"+optionId).val());
				    				if($(optionInput).is(":checked")){
				    					questionDetails.subQuestionsList[sq].optionsList[o].isCorrect = "Y";
				    				}else{
				    					questionDetails.subQuestionsList[sq].optionsList[o].isCorrect = "N";
				        			}
				    				continue;
				    			}
				    		}
				 		}
				 		console.log("After for loop");	
				 	}else{	
							showSnackBar("Select atleast one correct answer for subquestion no: "+(sq+1)+". ");
				 			return false;
				 	}
				
			}//end of for

	 		var saved = saveAnswerAjax(questionDetails)
	 						.then(success, failure)
						 		function success(data){
	 								return true;
						 			}
						 		function failure(data){
						 			return false;
						 			}
			
			
		}
		
		//answuerOftype4 start
		function answuerOftype4(questionDetails,actionTo){ //type 4 is of Descriptive

	 		var saved = saveAnswerAjax(questionDetails)
	 						.then(success, failure)
						 		function success(data){
	 								return true;
						 			}
						 		function failure(data){
						 			return false;
						 			}	
		}
		//answuerOftype4 end
		
		//getQuestionDetails(questionId) start
		function getQuestionDetails(questionId){
			for(var i=0;i<questionDetails.length;i++){
				tempDetails = questionDetails[i];
				if(tempDetails.id == questionId){
					return tempDetails;
				}
			}
			return null;
		}
		//getQuestionDetails(questionId) end
		
		//getQuestionDetailsByDivId(questionId) start
		function getQuestionDetailsByDivId(divId){
			return questionDetails;
		}
		//getQuestionDetails(questionId) end
		
		//saveAnswerAjax start
		 function saveAnswerAjax(questionDetails){
			 var promiseObj = new Promise(function(resolve, reject){
			console.log("In saveAnswerAjax() ENTERED...");
			var methodRetruns = false;
			//ajax to save question reponse start
			//console.log($("#question-textArea").val());
			//questionDetails.question = escapeHtmlCharacters($("#question-textArea").val());
			

			questionDetails.marks = $("#marks").val();
			questionDetails.copyCaseThreshold = $("#copyCaseThreshold").val();
			
    		var body = questionDetails;
    		console.log(body);
    		$.ajax({
    			type : 'POST',
    			url : '/exam/m/updateTestQusetion',
    			data: JSON.stringify(body),
                contentType: "application/json",
                dataType : "json",
                
    		}).done(function(data) {
				  console.log("iN AJAX SUCCESS");
              	console.log(data);
              	//if(!isSubQuestion){ //Do below operations only if Main question and not of type 3
              		postAjaxSuccessOperations();
              	//}
              	methodRetruns= true;
              	showSnackBar("Question Saved !");
				
      			console.log("In saveAnswerAjax() EXIT... got methodRetruns: "+methodRetruns);
      			resolve(methodRetruns);
    		}).fail(function(xhr) {
    			console.log("iN AJAX eRROR");
				showSnackBar("Error in saving question. Try Again.");
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
		function postAjaxSuccessOperations(){
			
			setTimeout(showSnackBar("Updated Questions!"), 3000);
			//console.debug(document.referrer);
			//window.location.replace(document.referrer);
			//window.history.back();
        }
		//postAjaxSuccessOperations end
		
		
		
		
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
		
		$('.deleteOptionLink').click(function(evt) {
			  console.log("deleteOptionLink click questionId",this.getAttribute('data-qid'));
			  console.log("deleteOptionLink click optionId",this.getAttribute('data-oid'));
			  
			  let questionId=this.getAttribute('data-qid');
			  let optionId=this.getAttribute('data-oid');
			  
				
				try{
					  console.log("IN checkIfOptionCanBeDeleted() questionId : "+questionId+" n optionId : "+optionId);
					  
					  let answer;
				      let inputArraySelector = "#options"+questionId+" :input";
					  let tempOptionCount=1;
					    console.log("IN checkIfOptionCanBeDeleted() inputArraySelector : "+inputArraySelector);
					  let inputArray = $(inputArraySelector);
					    console.log(inputArray);

					    console.log("inputArray: "+inputArray.length);
					    if(inputArray.length == 2){//as 2 input present in option div
					    	showSnackBar("Cannot delete option as it is only one left, Please delete whole question and add new one if needed.");

							  evt.preventDefault();
					    }
					 	for(let i = 0; i < inputArray.length; i++){
					    	console.log("Option button Option: "+i+" . Value: "+$(inputArray[i]).val());
					    	optionInput =inputArray[i];
					    	if($(optionInput).is(":checked")){
					    		console.log("Selected Option: "+$(optionInput).attr("name")+" . Value: "+$(optionInput).val()+".");
					    		if(optionId == $(optionInput).val()){
					    			showSnackBar("Cannot delete option selected as correct choice, Please unmark and retry to delete.");

					  			  evt.preventDefault();
					    		}
					    	}
						} 
				}catch(err){
					console.log("IN checkIfOptionCanBeDeleted catch err : ",err);
					showSnackBar("Cannot delete option, Got an error. Please check n retry ");

					  evt.preventDefault();
				}
				
				

			    

        });
		
 
	}); //end of doc ready()
	
</script>

</body>
</html>