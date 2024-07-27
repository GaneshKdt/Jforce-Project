<!DOCTYPE html>


<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@page pageEncoding="UTF-8" %>


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
<link
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.4.0/css/font-awesome.min.css"
	rel="stylesheet" type="text/css" />

<!-- JQuery & Boostrap related CDNS -->
<!-- 
<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
 -->
<!-- JQuery & Boostrap related CDNS end -->

<!-- Include Editor style. -->
<link
	href="https://cdnjs.cloudflare.com/ajax/libs/froala-editor/2.5.1/css/froala_editor.pkgd.min.css"
	rel="stylesheet" type="text/css" />
<link
	href="https://cdnjs.cloudflare.com/ajax/libs/froala-editor/2.5.1/css/froala_style.min.css"
	rel="stylesheet" type="text/css" />
<link data-require="sweet-alert@*" data-semver="0.4.2" rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/sweetalert/1.1.3/sweetalert.min.css" />


<style>
body {
	-webkit-user-select: none;
	-khtml-user-select: none;
	-moz-user-select: none;
	-ms-user-select: none;
	-o-user-select: none;
	user-select: none;
	background-color: #ECE9E7 !important;
}

#parentSpinnerDiv {
	background-color: transparent !important;
	z-index: 999;
	width: 100%;
	height: 100vh;
	position: fixed;
}

#childSpinnerDiv {
	color: black;
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
}

#submitTest :hover {
	color: white !important;
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
	width: 2px;
	height: 2px;
}

#questionsPreviewSection::-webkit-scrollbar-thumb {
	background-color: grey;
	border-radius: 1px;
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

.swal2-styled.swal2-confirm {
	font-size: 0.85em !important;
}

.swal2-styled.swal2-cancel {
	font-size: 0.85em !important;
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

.prevnext { //
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

.snackbarSuccess {
	background-color: #388E3C;
}

.snackbarError {
	background-color: #F44336;
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
	display: inline;
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
<body id="element" style="background-color: #ECE9E7;">
	<div id="toTop"></div>

	<div id="parentSpinnerDiv">

		<div id="childSpinnerDiv">
			<i class="fa fa-refresh fa-spin"
				style="font-size: 50px; color: grey;"></i>
		</div>

	</div>
	<%-- 
	<%@ include file="../common/header.jsp"%>
	 --%>


	<%-- <jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Exam;Online Assignment;Start Assignment" name="breadcrumItems" />
		</jsp:include> --%>


	<%-- <jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Test" name="activeMenu" />
				</jsp:include> --%>



	<%-- 
					<%@ include file="../common/studentInfoBar.jsp"%> --%>

	<div id="pageContainer" class="">


		<div id="element" style="width: 100%;"></div>


		<h2 style="margin-left: 15px; color: grey;" class="text-capitalize">${test.testName}
			- Subject : ${test.subject}</h2>
		<div class="clearfix"></div>
		<div>

			<!-- Code for page goes here start -->

			<div class="row" style="margin: 0px 0px;">
				<div class="col-sm-12 " style="margin: 0px 0px;">

					<!-- Content -->
					<div class="layout-content" data-scrollable>
						<div class="container-fluid">

							<div class="row" style="width: 100%;">
								<span title="Toggle Fullscreen" id="go-button"
									style="float: right; margin: 0px 15px; padding: 2px 5px 0px 5px; border: 0px solid black; border-radius: 5px; background-color: white; cursor: pointer;">
									<i class="fa-solid fa-maximize" style="font-size: 20px;"></i>
								</span>
							</div>

							<div class="row" style="width: 100%;">
								<div class="col-xs-6 col-sm-6 col-md-2  col-lg-2"
									style="padding-right: 5px; padding-left: 5px;">
									<div class="card">
										<div class="card-block center"
											style="padding-right: 5px; padding-left: 5px; padding-bottom: 5px; padding-top: 5px;">
											<h4 class="m-b-0">
												<strong id="attemptsCountSpan">${studentsTestDetails.attempt}
													/ ${test.maxAttempt}</strong>
											</h4>
											<small class="text-muted-light">ATTEMPT</small>
										</div>
									</div>
								</div>
								<div class="col-xs-6 col-sm-6 col-md-2  col-lg-2"
									style="padding-right: 5px; padding-left: 5px;">
									<div class="card">
										<div class="card-block center"
											style="padding-right: 5px; padding-left: 5px; padding-bottom: 5px; padding-top: 5px;">
											<h4 class="m-b-0">
												<strong id="noOfQuestionsSpan"></strong>
											</h4>
											<small class="text-muted-light">QUESTIONS</small>
										</div>
									</div>
								</div>

								<div class="col-xs-6 col-sm-6 col-md-2  col-lg-2"
									style="padding-right: 5px; padding-left: 5px;">

									<div class="card">
										<div class="card-block center"
											style="padding-right: 5px; padding-left: 5px; padding-bottom: 5px; padding-top: 5px;">
											<h4 class="text-success m-b-0">
												<strong id="attemptsSpan"></strong>
											</h4>
											<small class="text-muted-light">ANSWERED</small>
										</div>
									</div>
								</div>

								<div class="col-xs-6 col-sm-6 col-md-2  col-lg-2"
									style="padding-right: 5px; padding-left: 5px;">
									<div class="card">
										<div class="card-block center"
											style="padding-right: 5px; padding-left: 5px; padding-bottom: 5px; padding-top: 5px;">
											<h4 class="text-primary m-b-0">
												<strong id="attemptsLeft"></strong>
											</h4>
											<small class="text-muted-light">LEFT</small>
										</div>
									</div>
								</div>
								<div class="col-xs-12 col-sm-12 col-md-4  col-lg-4"
									style="padding-right: 5px; padding-left: 5px;">
									<!-- <div class="card">
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
												</div> -->

									<div class="card">
										<div class="card-block center"
											style="padding-right: 5px; padding-left: 5px; padding-top: 5px; padding-bottom: 5px;">
											<h4 class="text-primary m-b-0 "
												style="color: black; font-size: 18px; margin-top: 5px;">
												<strong id="testTime"> <i
													id="bookmarkIcon-${testQuestion.id}" class="fa-regular fa-clock"
													style="font-size: 25px;"></i> <span id="hoursSpan"
													class="timerNumbers"></span>h : <span id="minutesSpan"
													class="timerNumbers"></span>m : <span id="secondsSpan"
													class="timerNumbers"></span>s
												</strong>

												<button id="submitTest" class="btn btn-rounded "
													style="background-color: #d2232a;" disabled>Finish
													Assignment</button>

											</h4>

										</div>
									</div>
								</div>
							</div>



							<div class="row" style="width: 100%;">

								<!-- questions section -->
								<div class="col-md-10">
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
															<%--<button type="submit"
																					class="prevnext next  nextQuestionButton"
																					style="text-transform: none; 
																							margin:0px 0px 0px 5px;
																							padding: 8px 16px;
																							font-size: 0.9rem;
																							font-weight: unset;
																							"
																						<c:if test="${status.count==noOfQuestions}">disabled</c:if>
																					>Next &raquo;</button>--%>

															<%-- <a href="#" id="nextQuestionButton-${status.count}"
																					class="prevnext next  nextQuestionButton">
																				Skip &raquo;
																				</a> --%>
															<%-- <button
																					id="previousQuestionButton-${status.count}"
																					style="text-transform: none; 
																							margin:0px;
																							padding: 8px 16px;
																							font-size: 0.9rem;
																							font-weight: unset;
																							"
																					<c:if test="${status.count==1}">disabled</c:if>
																					class="prevnext previous  previousQuestionButton" 
																					>
																				&laquo; Previous
																				</button> --%>
															<a onclick="markForReview(${testQuestion.id})"
																data-toggle="tooltip" class="prevnext previous round"
																style="float: right;" title="Tag For Review">
																<i id="bookmarkIcon-${testQuestion.id}"
																class="fa fa-bookmark-o" style="font-size: 25px;"></i>
															</a>
														</div>
														<br />
														<div class="media" style="width: 100%">
															<div class="media-body  media-middle">
																<h4 class="card-title"
																	id="questionMainDiv-${status.count}"
																	class="questionMainDiv">

																	<i id="answerAttemptedTick-${testQuestion.id}" class=""
																		style="color: green;"></i> <span
																		class="questionNoSpan">Q${status.count}. </span> <span>${testQuestion.question}</span>
																</h4>
																<!-- display name of type of question start -->
																<small style="color: #708090;">Question Type : <c:choose>
																		<c:when test="${testQuestion.type == 1}">
																			Single Select, Weightage : ${testQuestion.marks}
																		</c:when>
																		<c:when test="${testQuestion.type == 2}">
																			Multiple Select  (Can select one or more than one right option), Weightage : ${testQuestion.marks}
																		</c:when>
																		<c:when test="${testQuestion.type == 3}">
																			Case Study, Weightage : ${testQuestion.marks}
																		</c:when>
																		<c:when test="${testQuestion.type == 4}">
																			Descriptive, Weightage : ${testQuestion.marks} 
																		</c:when>
																		<c:when test="${testQuestion.type == 5}">
																			True Or False, Weightage : ${testQuestion.marks}
																		</c:when>
																		<c:when test="${testQuestion.type == 8}">
																			Assignment, Weightage : ${testQuestion.marks}
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
														<c:when test="${testQuestion.type == 8}">

															<input type="hidden" value="${testQuestion.id}"
																id="hiddenQuestionId-${status.count}">
															<input type="hidden" value="" id="hiddenAssngUrl">
															<input type="hidden" value="${testQuestion.uploadType}" id="hiddenUploadType">
															<div class="card-block p-a-2"
																style="background-color: #ffffff padding-bottom: 0px;">

																	<!--   <div class="col-sm-1" style=" "></div>  -->
																	<%-- <div class="col-sm-12" style=" "> 

																			
													                      	   <iframe 
													                      	   		style="width:100%; min-height: 500px; "
													                      	   		src='${testQuestion.url}' 
													                      	   		frameborder='0'
																			      	webkitAllowFullScreen mozallowfullscreen allowFullScreen
																			   ></iframe>
																			
																			</div> --%>
																	<!--  	<div class="col-sm-1" style=" "></div>   -->
																	<!--  <div class="col-sm-1" style=" "></div>  -->
																	<c:if test="${testQuestion.question ne '' && testQuestion.uploadType == 'mp4' && testQuestion.url  == ''}">
																		<div class="row p-a-1"> 
																			<label for="link-location-text-link">Question:</label>
																				<p id="questionLink1" >${testQuestion.question}</p>
																		</div>
																	</c:if>
																	
																	<c:if test="${testQuestion.url  ne ''}">
																	<div class="row p-a-1"> 
																		<label for="link-location-text-link">Question
																			File: </label>
																			<a id="questionLink2"
																				href="${testQuestion.url}" target="_blank">Download
																				Question File</a>
																	</div>
																	</c:if>
																	
															</div>

															<div class="card-block p-a-2"
																style="background-color: #ffffff padding-bottom: 0px;">
																<div class="row">
																	<div>
																		<label for="link-location-text-link"
																			class="col-xs-3 col-md-1 col-sm-1 control-label">File
																			Upload: </label>
																		<div class="col-md-5 col-sm-10 col-xs-12">

																			<input type="file" class="input-file-btn"
																				id="file-link"
																				style="color: black; background-color: lightgrey" /><span id="uploadingAnswerMessage"></span>

																		</div> 
																	</div>
																</div>
																<br>

																<div class="row">
																	<!-- 	<div class="col-sm-3" style=" "></div> -->
																	<!-- 	<div id="answerFile" style="display:none" > -->
																	<!--  <label for="link-location-text-link" class="col-xs-3 col-md-1 col-sm-1 control-label">Check file: 
																					</label>  -->
																	<div class="col-sm-10" id="answerFile"
																		style="display: none">
																		<!-- 		 <input type="text" name="link-location-text" id="answerLink" class="form-control"
																						 placeholder="Enter File Location URL" readonly> -->
																		<!-- <a id="answerLink" href="">Download Answer File</a>
																						 <div class="col-sm-12" style=" ">  -->
																	<label >Check file uploaded: 
																					</label>
																	<c:if test="${testQuestion.uploadType == 'pdf'}">			
																		<iframe id="answerLink"
																			style="width: 100%; min-height: 500px;" src=''
																			frameborder='0' webkitAllowFullScreen
																			mozallowfullscreen allowFullScreen></iframe>
																		</c:if>
																		<c:if test="${testQuestion.uploadType == 'mp4'}">	
																		
																			  <embed id="answerLink" src="" showcontrols="true" style="width: 100%; min-height: 500px;" type="video/webm" ></embed>
																		</c:if>	
																	</div>
																	<!-- 	</div>  -->
																</div>


															</div>
											<!-- 	</div> -->


												</c:when>
												<c:when test="${testQuestion.type == 7}">

													<input type="hidden" value="${testQuestion.id}"
														id="hiddenQuestionId-${status.count}">


													<div class="card-block p-a-2"
														style="background-color: #ffffff padding-bottom: 0px;">
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

																<label class="c-input c-checkbox">${tempOption.optionData}

																	<input type="checkbox" id="checkField${status.count}1"
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
														style="background-color: #ffffff padding-bottom: 0px;">
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


																<label class="c-input c-checkbox">${tempOption.optionData}

																	<input type="checkbox" id="checkField${status.count}1"
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

													<div class="card-block p-a-2" style="padding-bottom: 0px;"
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
																		<br />
																		<br />

																	</c:when>
																</c:choose>
															</c:forEach>


														</div>


													</div>
													<!-- <div class="card-footer clearfix "
																		style="background-color: #ffffff">
																		<div class="col-sm-12">

																			<div class="col-sm-6">
																			</div>


																		</div>


																	</div>-->
												</c:when>
												<c:when test="${testQuestion.type == 4}">
													<div class="">
														<input type="hidden" value="${testQuestion.id}"
															id="hiddenQuestionId-${status.count}">

														<div class="card-block p-a-2" style="padding-bottom: 0px;">

															<span class="text-muted" style=""
																id="characterCount-${testQuestion.id}"></span>

															<span class="text-muted" style=""
																id="saveDescriptiveAlert-${testQuestion.id}"></span>
															<div class="form-group">
																<textarea
																	style="border: 1px solid grey; padding: 5px 10px; width: 100%;"
																	id="textArea-${testQuestion.id}" rows="5" wrap="hard"
																	maxlength="4000" onDrop="blur(); return false;"
																	required>${testQuestion.answer}</textarea>
															</div>
														</div>
														<!-- <div class="card-footer clearfix "
																		style="background-color: #ffffff">
																		<div class="col-sm-12">

																			<div class="col-sm-6">
																			</div>


																		</div>


																	</div> -->
													</div>
												</c:when>
												<c:when test="${testQuestion.type == 3}">

													<!-- Display subQuestions if question type is 3 i.e. caseStudy -->
													<div id="subQuestionsContainer">
														<div class="well">
															<p style="font-size: 16px;">
																${testQuestion.description}</p>
														</div>

														<div class="well">
															<h5>Questions :</h5>
															<input type="hidden" value="${testQuestion.id}"
																id="hiddenQuestionId-${status.count}">

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
																				<h4 class="card-title" class="questionMainDiv">

																					<a href="#demo${subQuestion.id}" class=""
																						data-toggle="collapse"
																						style="color: black; font-size: 18px;">
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
																		style="padding-bottom: 0px;"
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
																						<br />
																						<br />

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

															<!-- <div class="card-footer clearfix "
																		style="background-color: #ffffff">
																		<div class="col-sm-12">

																			<div class="col-sm-6">
																			</div>


																		</div>


																	</div> -->


														</div>


													</div>


												</c:when>
												<c:otherwise>
													<input type="hidden" value="${testQuestion.id}"
														id="hiddenQuestionId-${status.count}">

													<div class="card-block p-a-2" style="padding-bottom: 0px;"
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
																		<br />
																		<br />

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
												</c:otherwise>
												</c:choose>



												<div class="card-footer clearfix "
													style="background-color: #ffffff padding-top: 0px;">
													<div class="col-sm-12">

														<div class="col-sm-6">

															<%-- <button
																							id="previousQuestionButton-${status.count}"
																							style="text-transform: none; 
																									margin:0px;
																									padding: 8px 16px;
																									font-size: 0.9rem;
																									font-weight: unset;
																									"
																							<c:if test="${status.count==1}">disabled</c:if>
																							class="prevnext previous  previousQuestionButton" 
																							>
																						&laquo; Previous
																						</button> --%>


															<button type="submit" class="btn-success"
																style="text-transform: none; margin: 0px 0px 0px 5px; padding: 8px 16px; font-size: 0.9rem; font-weight: unset;">Save</button>
															<c:if test="${testQuestion.type ne 8}"> 

																<button class="btn-success saveNNextButton"
																	style="text-transform: none; margin: 0px 0px 0px 5px; padding: 8px 16px; font-size: 0.9rem; font-weight: unset;">Save
																	&amp; Next</button>
															</c:if> 
														</div>



													</div>



												</div>



											</div>
								</div>
								</form>

								</c:forEach>

	
							</div>
							<!-- End of questions section -->
							<div class=" col-md-2" style="padding: 5px 2px;">
								<div class="card">

									<div class="">
										<div class="" id="questionsPreviewSection">

											<div class="">
												<div class="row ">
													<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1"
														style="font-size: 12px;"></div>
													<div class="col-xs-5 col-sm-5 col-md-5 col-lg-5"
														style="font-size: 12px;">Question No.</div>
													<div class="col-xs-4 col-sm-4 col-md-4 col-lg-4"
														style="font-size: 12px;">Status</div>
													<div class="col-xs-1" style="font-size: 12px;"></div>
												</div>
											</div>




											<c:forEach var="testQuestion" items="${testQuestions}"
												varStatus="status">

												<div class="col-xs-12"
													style="cursor: pointer; padding-bottom: 5px;; padding-top: 5px;"
													onclick="jumpToQuestion(${status.count})">
													<div class="row">

														<div class="col-xs-1">
															<div class="">
																<i id="bookmarkIconInQuestionStatus-${testQuestion.id}"
																	class="fa fa-bookmark-o"
																	style="display: inline; font-size: 18px;"></i>
															</div>
														</div>
														<div class="col-xs-3">

															<div class="text-muted-light"
																style="color: black; font-size: 16px;">
																<a href="#" onclick="jumpToQuestion(${status.count})">
																	Q${status.count} </a>
															</div>

														</div>

														<div class="col-xs-6">
															<div id="statusQuestion${status.count}"
																class="label left_status">Unattempted</div>

														</div>

														<div class="col-xs-2"></div>



													</div>

												</div>
											</c:forEach>

											<%-- 	<ul class="list-group m-b-0">

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
												<div class="media-body" style = "color:black; font-size : 16px;">${testQuestion.question}</div>
												<div class="media-right">
												<i id="bookmarkIconInQuestionStatus-${testQuestion.id}" class="fa fa-bookmark-o" style = "display: inline ; font-size : 18px;" ></i>
												</div> 
												<div class="media-right">
													<span id="statusQuestion${status.count}"
														class=" left_status">Unattempted</span>
												</div>
											</div>
										</li>
									</c:forEach>
								</ul> --%>
										</div>
									</div>
								</div>
							</div>
							</div>

						</div>


						<!-- <div class="row">
											<div class="col-lg-9 col-md-9  col-sm-12" style="">
												<button id="submitTest" class="btn btn-rounded pull-right"
													style="background-color: #d2232a; ">Finish Assignment</button>
											</div>
											</div> -->

					</div>
				</div>


			</div>
		</div>




		<!-- Code for page goes here end -->
	</div>
	</div>

	<div id="snackbar">Some text some message..</div>

	<%-- 
	<jsp:include page="../common/footer.jsp" />
 --%>

	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>

	<!-- Commented sweetalert1 js 
<script src="https://unpkg.com/sweetalert/dist/sweetalert.min.js"></script>

<script src="https://cdn.jsdelivr.net/npm/sweetalert2@9"></script>
-->

	<script src="/exam/resources_2015/sweatalert/sweetalert2_v9.js"></script>

<script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.0/MathJax.js?config=TeX-AMS_HTML"></script>



	<script type="text/javascript">

function testHide(){
////console.log('call')
	$("#questionMainDiv-1").removeClass("showQuestionMainDiv");
	$("#questionMainDiv-1").addClass("hideQuestionMainDiv");	
}
function testShwo(){
	$("#questionMainDiv-1").removeClass("hideQuestionMainDiv");
	$("#questionMainDiv-1").addClass("showQuestionMainDiv");
}



	$(document).ready(function() {


	  	   //console.log("testSubmitted");
	  	  
   	   //console.log(localStorage.getItem("testSubmitted"));

	  	   ////console.log("testSubmitted");

   	   ////console.log(localStorage.getItem("testSubmitted"));


	  	   ////console.log("testSubmitted");

   	   ////console.log(localStorage.getItem("testSubmitted"));

  	    if(localStorage.getItem("testSubmitted") === "Yes"){

  	   	//alert(localStorage.getItem("testSubmitted"));
       	//window.location.href = "/exam/testOverPage";
//        	commenting following line for faculty preview
//        	window.location.href = "/exam/mbax/ia/s/viewTestDetailsForStudentsForAllViews?userId=${userId}&id=${test.id}&message=''";
       	
       	
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
		
		var facultyIdForPostRequest = '${test.facultyId}';
		
		var isProcessing= false;
		
		var autoSaveCurrentDescriptiveAnswer = false;
		
		var showFiveMinsLeftReminder = true;

		var isLastQuestionVisited= false;
		
		var saveAnswerAjaxRetryCounter = 0;
		var submitTestRetryCounter = 0;
		var updateRemainingTimeCounter = 0;
		
		var saveDescriptiveAlertCountdown = 5 * 60 * 1000; // 5min
		var isSaveDescriptiveAlertLabelSet = false;
		/* var serverPath = '${serverPath}';
		alert('serverPath' +serverPath); */
		<c:if test = "${continueAttempt != 'Y'}">
		//console.log("test question not continueAttempt");
			<c:forEach var="testQuestion" items="${testQuestions}"	varStatus="status">
				<c:if test="${testQuestion.type == 8}" >
					//console.log("test question type 8");
					$('.btn-success').attr("disabled",true);
				</c:if>
			</c:forEach> 
		</c:if>
		<c:if test = "${continueAttempt == 'Y'}">
			noOfQuestionsAttemptted= ${studentsTestDetails.noOfQuestionsAttempted};
			//console.log("test question continueAttempt");
			
			<c:forEach var="testQuestion" items="${testQuestions}"	varStatus="status">
			
			<c:if test="${testQuestion.type == 8}" >
				//console.log("test question type 8");
				$('.btn-success').attr("disabled",true);
			</c:if>

			//populate attemptedQuestions start
			indexPA${status.index} = ${status.index};
			itemPA${status.index} = 
					{
						qId : ${status.index},
						<c:if test = "${testQuestion.isAttempted == 'Y'}">
							answered : true
						</c:if>
						<c:if test = "${testQuestion.isAttempted != 'Y'}">
							answered : false
						</c:if>
					}		
				
			attemptedQuestions.splice(indexPA${status.index}, 1, itemPA${status.index});
			//populate attemptedQuestions end
			
			<c:if test = "${testQuestion.isAttempted == 'Y'}">
			//console.log("test question attempted");
			showTickForAnswerSaved(${testQuestion.id});
			$("#statusQuestion${status.count}").text("Answered");
			$('#statusQuestion${status.count}').addClass('btn-success');
			<c:if test = "${testQuestion.type == 8}">
					$('#answerFile').show();
					$('#answerLink').attr("src","${testQuestion.answer}");
					//$('#answerLink').attr("src",serverPath+"/exam/${testQuestion.answer}");
					$('#hiddenAssngUrl').val('${testQuestion.answer}');
					$('.btn-success').attr('disabled', false);
					//$('#submitTest').attr('disabled', false);
			</c:if>
			</c:if>
			</c:forEach>
		</c:if>
		////console.log("attemptedQuestions : ");
		////console.log(attemptedQuestions);
		////console.log("totalAttempt: "+totalAttempt+" noOfQuestions: "+noOfQuestions+". testId: "+testId+". sapid: "+sapid+" noOfQuestionsAttemptted: "+noOfQuestionsAttemptted+" duration:"+duration);
		
		var questionsDetails = new Array();
		

		<c:forEach var="testQuestion" items="${testQuestions}"	varStatus="status">
				
				var setIntervalVar${testQuestion.id} ;
				
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
		
		////console.log("Got questionsDetails =====>");
		////console.log(questionsDetails);
		////console.log("Got questionsDetails <=====");
		
		//show first question on page load
		$("#questionMainDiv-1").addClass("showQuestionMainDiv");
		$("#attemptsSpan").text((noOfQuestionsAttemptted > noOfQuestions) ? noOfQuestions : noOfQuestionsAttemptted);
		$("#noOfQuestionsSpan").text(noOfQuestions);
		$("#attemptsLeft").text(( (noOfQuestions-noOfQuestionsAttemptted) < 0 ) ? 0 : (noOfQuestions-noOfQuestionsAttemptted) );
		$("#skippedSpan").text(noOfQuestionsSkipped);
		
		calcProgress()
		
		<c:forEach var="testQuestion" items= "${testQuestions}"	varStatus="status">
		//console.log('Entered testQuestion forEach')
			try {
				$("#studentTestForm-${status.count}").submit(function(e){
					////console.log('Called Submit')
				    e.preventDefault();
					////console.log("isProcessing: "+isProcessing);
					checkIsTestOver("");
					////console.log('questionUpdated = '+questionUpdated);
					
					if(isProcessing){
						return;
					}else{

						isProcessing = true;
					}

					
					questionId=$("#hiddenQuestionId-${status.count}").val();
					submitTestQuestionForm(questionId,'stayHere');
				});
			}
			catch(err) {
			    ////console.log("Catch error : "+err.message);
			}
			
			<c:if test="${testQuestion.type == 4}" >
		    $("#textArea-${testQuestion.id}").on('change keydown input', function(){
		    	async function asyncCallToChangedText(){
					let saved = await  changedText(${testQuestion.id})
						.then(success, failure)
				 		function success(data){
							////console.log("In SUCCESS of changedText() =====> ");
							return true;
					 		}
				 		function failure(data){
							showSnackBar("Error in saving answer, Please try again . ", "error");
							return false;
						 	} 
				}
		    	asyncCallToChangedText();
				
		    	//changedText(${testQuestion.id});
			});
			
		    $('#textArea-${testQuestion.id}').bind('copy paste cut',function(e) { 
		    	 e.preventDefault(); //disable cut,copy,paste
		    	 alert('cut,copy & paste options are disabled !!');
		    	 }); 
			</c:if>
		</c:forEach>
		
		function submitTestQuestionForm(qId,actionTo){
			

			$('#parentSpinnerDiv').show();
			
	    	saveAnswerAjaxRetryCounter = 0;
			
			/* if(!questionUpdated){
				if('nextQuestion' === actionTo || 'saveNNext' === actionTo ){
					
					async function asyncCallToNextQuestion(){
						let toNextQuestion = await  nextQuestion()
							.then(success, failure)
					 		function success(data){
								isProcessing = false;
								
								////console.log("In SUCCESS nextQuestion() =====> ");
							return true;
						 		}
					 		function failure(data){
								showSnackBar("Error , Please try again . ", "error");
								return false;
							 	} 
					}
					asyncCallToNextQuestion();
					
					
					//nextQuestion();
				}
				else if('previousQuestion' === actionTo){

					async function asyncCallToPreviousQuestion(){
						let toPreviousQuestion = await  previousQuestion()
							.then(success, failure)
					 		function success(data){
								isProcessing = false;
								////console.log("In SUCCESS previousQuestion() =====> ");
							return true;
						 		}
					 		function failure(data){
								showSnackBar("Error , Please try again . ", "error");
								return false;
							 	} 
					}
					asyncCallToPreviousQuestion();
					
					//previousQuestion();
				}
				else if('submitTest' === actionTo || 'stayHere' === actionTo){

					isProcessing = false;

					$('#parentSpinnerDiv').hide();
					return;
				}
				else{

					async function asyncCallToJumpToQuestoinSubFunction(){
						let jumpToQuestoinSubFunction = await  jumpToQuestoinSubFunction(currentQuestion)
							.then(success, failure)
					 		function success(data){
								////console.log("In SUCCESS jumpToQuestoinSubFunction() =====> ");
								isProcessing = false;

							return true;
						 		}
					 		function failure(data){
								showSnackBar("Error , Please try again . ", "error");
								return false;
							 	} 
					}
					asyncCallToJumpToQuestoinSubFunction();
					
					//jumpToQuestoinSubFunction(currentQuestion);
				}

			}
			else{
		    let saved = validateAndSaveAnswer(qId,actionTo);
		    if(saved){
		    	////console.log("Saved Answer.");
		    	questionUpdated= false;
				isProcessing = false;
	 	    }else{
		    	////console.log("Error in saving answer.");
				isProcessing = false;
	 	    }
			} */
			

		    let saved = validateAndSaveAnswer(qId,actionTo);
		    if(saved){
		    	////console.log("Saved Answer.");
		    	questionUpdated= false;
				isProcessing = false;
	 	    }else{
		    	////console.log("Error in saving answer.");
				isProcessing = false;
	 	    }
		}
		
		//validateAndSaveAnswer Start
		function validateAndSaveAnswer(questionId,actionTo){
			////console.log("In validateAndSaveAnswer() questionId : "+questionId+" actionTo : "+actionTo);
			

			
			questionDetails = getQuestionDetails(questionId);
		 	////console.log("In validateAndSaveAnswer() got questionDetails ====> ");
		 	////console.log(questionDetails);
		 	////console.log("In validateAndSaveAnswer() got questionDetails <==== ");
			
			if(questionDetails==null){
				showSnackBar("Error in getting QuestionDetails, Please try again", "error");
			}else{
			//logic to get given answer start
			var inputArraySelector;
			if(questionDetails.type==4){
				return answuerOftype4(questionDetails,actionTo);
			}else if(questionDetails.type==3){
				return answuerOftype3(questionDetails,actionTo);
			}else if(questionDetails.type==8){
				return answuerOftype8(questionDetails,actionTo);
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
						    ////console.log("IN answerOftype1n2() inputArraySelector : "+inputArraySelector);
						    inputArray = $(inputArraySelector);
						    ////console.log(inputArray);

						    ////console.log("inputArray: "+inputArray.length);
						 	for(let i = 0; i < inputArray.length; i++){
						    	////console.log("Option button Option: "+i+" . Value: "+$(inputArray[i]).val());
						    	optionInput =inputArray[i];
						    	if($(optionInput).is(":checked")){
						    		////console.log("Selected Option: "+$(optionInput).attr("name")+" . Value: "+$(optionInput).val()+" . Sapid: "+sapid);
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
						 		hideTickForAnswerSaved(questionDetails.id);
						 		if(!isSubQuestion){


							 		async function asyncCallToDeleteAnswerBySapIdQuestionId(){
										let deleteAns = await  deleteAnswerBySapIdQuestionId(questionDetails.id,sapid)
											.then(success, failure)
									 		function success(data){
												////console.log("In SUCCESS of AJAX CALL of Descriptive =====> ");

										 		unAttemptAttemptedQuestion(currentQuestion);
										 		}
									 		function failure(data){
												showSnackBar("Error in saving answer, Please try again . ", "error");
												
											 	} 
									}
							 		asyncCallToDeleteAnswerBySapIdQuestionId();
							 		

						 		}
						 		
						 		//deleteAnswerBySapIdQuestionId
						 		
						 		
								questionUpdated=false;
								$('#parentSpinnerDiv').hide();
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

		    	var tempNoOfSubQuestionsSaved = 0;
		    	var noOfSelectedCSSubQuestions=0;
				async function asyncCall() {
					 for(var i = 0; i< csSubQuestions.length;i++){
						 tempQuestion=csSubQuestions[i];
						
						//check if inputs are select to show unattempted if none are Start
						 		let inputArraySelectorForCheck = "#options"+csId+"-"+tempQuestion.id+" :input";
						        let inputArrayForCheck = $(inputArraySelectorForCheck);
							    ////console.log(inputArrayForCheck);
								let noOfSubQuestionsOptionsSelected = 0;
							    ////console.log("inputArrayForCheck: "+inputArrayForCheck.length);
							 	for(let i = 0; i < inputArrayForCheck.length; i++){
							    	////console.log("Option button Option: "+i+" . Value: "+$(inputArrayForCheck[i]).val());
							    	optionInputForCheck =inputArrayForCheck[i];
							    	if($(optionInputForCheck).is(":checked")){
							    		////console.log("Selected Option: "+$(optionInputForCheck).attr("name")+" . Value: "+$(optionInputForCheck).val()+" . Sapid: "+sapid);
							    		noOfSelectedCSSubQuestions++;
							    		noOfSubQuestionsOptionsSelected++;
							    	}
								}
							 	////console.log(" noOfSubQuestionsOptionsSelected : "+noOfSubQuestionsOptionsSelected);
							 	if(noOfSubQuestionsOptionsSelected == 0){
							 		let h4SelectorForSubQuestion = "#subQuestionH4-"+tempQuestion.id;
							 		////console.log(" h4SelectorForSubQuestion : "+h4SelectorForSubQuestion);
							 		$(h4SelectorForSubQuestion).removeClass("greenLeftBorder");
							 	}
						//check if inputs are select to show unattempted if none are End
						 
						if(tempQuestion.type==1 || tempQuestion.type==2){
							inputArraySelector = "#options"+csId+"-"+tempQuestion.id+" :input";
							let saved = await answuerOftype1n2(tempQuestion,inputArraySelector,true).then(function(){
								////console.log("------------------------>CAlled saved")
									tempNoOfSubQuestionsSaved++;			
							});
								
						}
						
						
						
					}
				}
				asyncCall().then(function(){
					////console.log("------------------------>CAlled Then")
					if(tempNoOfSubQuestionsSaved == csSubQuestions.length){
						
						// asyncCallToSavedCSQuestionAnswer start
						async function asyncCallToSavedCSQuestionAnswer(){
							let saved = await saveAnswerAjax(csId,"attempted",sapid,testId,questionDetails.type,true)
	 						.then(success, failure)
						 		function success(data){
	 							////console.log("In SUCCESS of AJAX CALL of Case Study Question =====> ");
								postAjaxSuccessOperations(actionTo);
								


								//check if inputs are select to show unattempted if none are Start
								if(noOfSelectedCSSubQuestions == 0){
									
									async function asyncCallToDeleteAnswerBySapIdQuestionId(){
										let deleteAns = await  deleteAnswerBySapIdQuestionId(questionDetails.id,sapid)
											.then(success, failure)
									 		function success(data){
												////console.log("In SUCCESS of AJAX CALL of Descriptive =====> ");
											
										 		}
									 		function failure(data){
												showSnackBar("Error in saving answer, Please try again . ", "error");
												
											 	} 
									}
							 		asyncCallToDeleteAnswerBySapIdQuestionId();
									
									questionUpdated = false;

							 		hideTickForAnswerSaved(questionDetails.id);
							 		unAttemptAttemptedQuestion(currentQuestion);
								}else{

							 		showTickForAnswerSaved(questionDetails.id);
								}
								//check if inputs are select to show unattempted if none are End
								
								return true;
							 		}
						 		function failure(data){
									showSnackBar("Error in saving all sub questions, Please try again . ", "error");
									return false;
								 	}
						}
						//asyncCallToSavedCSQuestionAnswer end
						asyncCallToSavedCSQuestionAnswer();
 			
 	
					}else{
						showSnackBar("Error in saving all sub questions, Please try again . ", "error");
						return false;
					}
					
				});
				////console.log("tempNoOfSubQuestionsSaved" + tempNoOfSubQuestionsSaved)
				
			
			return false;
		}
		
		//answuerOftype4 start
		function answuerOftype4(questionDetails,actionTo){ //type 4 is of Descriptive
			var dQuestionId = questionDetails.id;
			var textAreaSelector = "#textArea-"+dQuestionId;
			var dAnswuer = $(textAreaSelector).val();
			////console.log("In answuerOftype4 got dQuestionId: "+dQuestionId+" textAreaSelector: "+textAreaSelector+" dAnswuer: "+dAnswuer);
			
			if(dAnswuer){
			if(dAnswuer.length == 0 ){

				goToQuestionAsPerAction(actionTo);
			}else{
				async function asyncCallToSaveDescriptiveAnswer(){
					let saved = await  saveAnswerAjax(dQuestionId,dAnswuer,sapid,testId,questionDetails.type,false,actionTo)
						.then(success, failure)
				 		function success(data){
							////console.log("In SUCCESS of AJAX CALL of Descriptive =====> ");
					    	questionUpdated= false;
						return true;
					 		}
				 		function failure(data){
							showSnackBar("Error in saving answer, Please try again . ", "error");
							return false;
						 	} 
				}
				asyncCallToSaveDescriptiveAnswer();
			}
			}else{
				goToQuestionAsPerAction(actionTo);
			}
			
		}
		//answuerOftype4 end
		
		function goToQuestionAsPerAction(actionTo){
			if('nextQuestion' === actionTo){
				nextQuestion();
			}
			else if('previousQuestion' === actionTo){
				previousQuestion();
			}
			else if('submitTest' === actionTo || 'stayHere' === actionTo){
			}
			else{
				jumpToQuestoinSubFunction(jumpToQuestionPlaceholder);
			}
        	calcProgress();
			
		}
		

		
		//answuerOftype8 start
		function answuerOftype8(questionDetails,actionTo){ //type 8 is of pdf
			var dQuestionId = questionDetails.id;
			var dAnswuer = $('#hiddenAssngUrl').val();
			//console.log("In answuerOftype8 got dQuestionId: "+dQuestionId+" dAnswuer: "+dAnswuer+" actionTo: "+actionTo);
			
			if(dAnswuer){
				if(dAnswuer.length == 0 ){
					showSnackBar("Error in saving answer, Invalid file type uploaded ", "error");
					goToQuestionAsPerAction(actionTo);
					$('#parentSpinnerDiv').hide();
				}else{
					async function asyncCallToSaveAnswer(){
						let saved = await  saveAnswerAjax(dQuestionId,dAnswuer,sapid,testId,questionDetails.type,false,actionTo)
							.then(success, failure)
					 		function success(data){
								//console.log("In SUCCESS of AJAX CALL of Link =====> ");
						    	questionUpdated= false;
						    	 $('.btn-success').attr('disabled',false);
						    	 $('#parentSpinnerDiv').hide();
						    	 $('#answerFile').show();
								 $('#answerLink').attr("src",dAnswuer);
									$('#submitTest').attr('disabled', false);
									$('#uploadingAnswerMessage').text("");
							return true;
						 		}
					 		function failure(data){
					 			//console.log("data =====> ");
					 			//console.log(data);
					 			$('#uploadingAnswerMessage').text("Unable to verify your file. Please select file again using choose file.");
								showSnackBar("Error in saving answer, Please try again . ", "error");
								
								return false;
							 	} 
					}
					asyncCallToSaveAnswer();
				}
				 }else{
					goToQuestionAsPerAction(actionTo);
				} 
		}
		//answuerOftype8 end

	
		

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
			////console.log("In saveAnswerAjax() ENTERED...");
			var methodRetruns = false;
			//ajax to save question reponse start
    		var body = {
    			'questionId' : questionId,
    			'answer' : answer,
    			'sapid' : sapid,
    			'testId' : testId,
    			'type' : type,
    			'attempt' : totalAttempt,
    			'facultyId' : facultyIdForPostRequest
    			
    		};
    		////console.log(body);
    		$.ajax({
    			type : 'POST',
    			url : '/exam/mbax/ia/sm/m/addStudentsQuestionResponse',
    			data: JSON.stringify(body),
                contentType: "application/json",
                dataType : "json",
                
    		}).done(function(data) {
				  ////console.log("iN AJAX SUCCESS");
              	////console.log(data);
              	if(type == 4){
              		hideAlertLabelToSaveDescriptive();
              	}
              	
              	if(!isSubQuestion){ //Do below operations only if Main question and not of type 3
              		showTickForAnswerSaved(questionId);	
              		
              		postAjaxSuccessOperations(actionTo);
              	}
              	
              	
              	methodRetruns= true;
      			////console.log("In saveAnswerAjax() EXIT... got methodRetruns: "+methodRetruns);
      			resolve(methodRetruns);
    		}).fail(function(xhr) {
    			////console.log("iN AJAX eRROR");
    			
    			//Retry once if got error start
    			if(saveAnswerAjaxRetryCounter === 0 ){
    				showSnackBar("Retrying to save answer, Please wait. ", "error");

			    	saveAnswerAjaxRetryCounter = 1;
    				async function asyncCallToSaveAnswerAjaxRetry(){
    					let saved = await  saveAnswerAjax(questionId,answer,sapid,testId,type,isSubQuestion,actionTo)
    						.then(success, failure)
    				 		function success(data){
    							////console.log("In SUCCESS of AJAX CALL of asyncCallToSaveAnswerAjaxRetry =====> ");
    					    	questionUpdated= false;
    							}
    				 		function failure(data){
    							} 
    				}
    				asyncCallToSaveAnswerAjaxRetry();
    			}else{

    			    showSnackBar("Error in saving answer. Try Again.", "error");
    				methodRetruns= false;
    				////console.log("In saveAnswerAjax() EXIT... got methodRetruns: "+methodRetruns);
    			    ////console.log('error', xhr);
    			    logErrorAjax(xhr,body);
    				questionUpdated=false;
    				$('#parentSpinnerDiv').hide();
    			    reject(methodRetruns);	
    			}
    			//Retry once if got error end
    			
			  });
			//ajax to save question reponse end
			//////console.log("In saveAnswerAjax() EXIT... got methodRetruns: "+methodRetruns);
			 })
			 return promiseObj;

    	}
		//saveAnswerAjaxr end
		
			
			//deleteAnswerBySapIdQuestionId start
			 function deleteAnswerBySapIdQuestionId(questionId,sapid){
				var promiseObj = new Promise(function(resolve, reject){
				////console.log("In deleteAnswerBySapIdQuestionId() ENTERED...");
				var methodRetruns = false;
				var sQTSelector = "#statusQuestion"+currentQuestion;
				////console.log("sQTSelector : "+sQTSelector);
				var statusQuestionText = $(sQTSelector).text();
				////console.log("statusQuestionText : "+statusQuestionText );
				
				if(statusQuestionText == 'Answered'){
				
				//ajax to deleteAnswerBySapIdQuestionId start
	    		var body = {
	    			'questionId' : questionId,
	    			'sapid' : sapid,
	    			'testId' : testId,
	    			'attempt' : totalAttempt
	    			
	    		};
	    		////console.log(body);
	    		$.ajax({
	    			type : 'POST',
	    			url : '/exam/mbax/ia/s/deleteAnswerBySapIdQuestionId',
	    			data: JSON.stringify(body),
	                contentType: "application/json",
	                dataType : "json",
	                
	    		}).done(function(data) {
					////console.log("iN deleteAnswerBySapIdQuestionId AJAX SUCCESS");
	              	////console.log(data);
	              	
	              	methodRetruns= true;
					questionUpdated=false;
					$('#parentSpinnerDiv').hide();
	      			////console.log("In deleteAnswerBySapIdQuestionId() EXIT... got methodRetruns: "+methodRetruns);
	      			resolve(methodRetruns);
	    		}).fail(function(xhr) {
	    			////console.log("iN deleteAnswerBySapIdQuestionId AJAX eRROR");
					showSnackBar("Error in saving answer. Try Again.", "error");
					methodRetruns= false;
					////console.log("In deleteAnswerBySapIdQuestionId() EXIT... got methodRetruns: "+methodRetruns);
				    ////console.log('error', xhr);
				    logErrorAjax(xhr,body);

					questionUpdated=false;
					$('#parentSpinnerDiv').hide();
				    reject(methodRetruns);
				  });
				//ajax to  end
				}else{
					questionUpdated=false;
					$('#parentSpinnerDiv').hide();
	      			////console.log("In deleteAnswerBySapIdQuestionId() exit...");
	      			resolve(true);
	    		}
				//////console.log("In deleteAnswerBySapIdQuestionId() EXIT... got methodRetruns: "+methodRetruns);
				
				})
				 return promiseObj;

	    	}
			//deleteAnswerBySapIdQuestionId end
		
		//postAjaxSuccessOperations start
		function postAjaxSuccessOperations(actionTo){
			 var promiseObj = new Promise(function(resolve, reject){
			
			////console.log("Entered postAjaxSuccessOperations() ");
			
			updateAttemptedQuestions(currentQuestion);

			if('nextQuestion' === actionTo || 'saveNNext' === actionTo ){
				
				async function asyncCallToNextQuestion(){
					let toNextQuestion = await  nextQuestion()
						.then(success, failure)
				 		function success(data){
							isProcessing = false;
							////console.log("In SUCCESS nextQuestion() =====> ");
				        	showSnackBar("Answer Saved!", "success");
				        	calcProgress();

							////console.log("Exiting postAjaxSuccessOperations() ");
							resolve(true);
					 		}
				 		function failure(data){
							showSnackBar("Error , Please try again . ", "error");
							resolve(true);
							} 
				}
				asyncCallToNextQuestion();
				
				
				//nextQuestion();
			}
			else if('previousQuestion' === actionTo){

				async function asyncCallToPreviousQuestion(){
					let toPreviousQuestion = await  previousQuestion()
						.then(success, failure)
				 		function success(data){
							isProcessing = false;
							////console.log("In SUCCESS previousQuestion() =====> ");
				        	showSnackBar("Answer Saved!", "success");
				        	calcProgress();

							////console.log("Exiting postAjaxSuccessOperations() ");
							resolve(true);
					 		}
				 		function failure(data){
							showSnackBar("Error , Please try again . ", "error");
							resolve(true);
							} 
				}
				asyncCallToPreviousQuestion();
				
				//previousQuestion();
			}
			else if('submitTest' === actionTo || 'stayHere' === actionTo){

				isProcessing = false;

				$('#parentSpinnerDiv').hide();
	        	showSnackBar("Answer Saved!", "success");
	        	calcProgress();

				////console.log("Exiting postAjaxSuccessOperations() ");
				resolve(true);
			}
			else{
				//alert(actionTo);
				async function asyncCallToJumpToQuestoinSubFunction(){
					
					var jumpToQuestoinSubFunction2 = await  jumpToQuestoinSubFunction(jumpToQuestionPlaceholder)
						.then(success, failure)
				 		function success(data){
							////console.log("In SUCCESS jumpToQuestoinSubFunction() =====> ");
							isProcessing = false;
							
				        	showSnackBar("Answer Saved!", "success");
				        	calcProgress();

							////console.log("Exiting postAjaxSuccessOperations() ");
							resolve(true);
							
					 		}
				 		function failure(data){
							showSnackBar("Error , Please try again . ", "error");
							resolve(true);
							} 
				}
				asyncCallToJumpToQuestoinSubFunction();
				
				//jumpToQuestoinSubFunction(jumpToQuestionPlaceholder);
			}

		
			
			/* if('nextQuestion' === actionTo){
				nextQuestion();
			}
			else if('previousQuestion' === actionTo){
				previousQuestion();
			}
			else if('submitTest' === actionTo || 'stayHere' === actionTo){
			}
			else{
				jumpToQuestoinSubFunction(jumpToQuestionPlaceholder);
			} */
			

    		
			 })
			 return promiseObj;
        }
		//postAjaxSuccessOperations end
		
		//submitTest Start
		$("#submitTest").click(function(e){

            ////console.log("Clicked submit Test +++++++++> ");
			event.preventDefault();
			
			/* if(lastQuestionUpdated){
				questionData = getQuestionDetailsByDivId(noOfQuestions);
				if(questionData !=null){
		            ////console.log("Saving Last Question. ");
					submitTestQuestionForm(questionData.id,'submitTest'); 
				}else{
					showSnackBar('Error in going to question try again.', "error");
					return;
				}
			} */
			

				   if(questionUpdated){
						questionData = getQuestionDetailsByDivId(currentQuestion);
						if(questionData !=null && questionData.type == 4 ){
							//alert("autosavng...");
							submitTestQuestionForm(questionData.id,'stayHere')
						}else{
							//showSnackBar('Error in going to question try again.', "error");
						}
					} 
			
				if(currentQuestion !== noOfQuestions){

					if(isLastQuestionVisited){
					}else{
						return false;
					}				
				}else{ 
					isLastQuestionVisited = true;
				}
			
            /* Commented sweetalert1 code start
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
			    	submitTestRetryCounter = 0;
    	        	submitTest("testEnded");
                } else {
    	            event.preventDefault();
                }
              }); 
             Commented sweetalert1 code end
            */
            
            Swal.fire({
		                title: "Are you sure?",
		                text: "  Test would be submitted and attempt will be exhausted! \n Questions Attempted: "+((noOfQuestionsAttemptted > noOfQuestions) ? noOfQuestions : noOfQuestionsAttemptted)+" \n  Questions Left: "+(( (noOfQuestions-noOfQuestionsAttemptted) < 0 ) ? 0 : (noOfQuestions-noOfQuestionsAttemptted))+" ",
		                icon: "warning",
		                showCancelButton: true,
		                confirmButtonColor: '#3085d6',
		                cancelButtonColor: '#d33',
		                cancelButtonText: 'No, cancel!',
		                confirmButtonText: 'Yes, I am sure!',
		                 
		              }).then(function(isConfirm) {
		            	   
		                if (isConfirm.value && isConfirm.value === true ) {
		    	            event.preventDefault();
					    	submitTestRetryCounter = 0;
		    	        	submitTest("testEnded");
		                } else {
		    	            event.preventDefault();
		                }
		              }); 
            
		});
		
		function submitTestSubFunction(){
			
		}
		
		$(".nextQuestionButtonNOTUSED").click(function(){

			if(isProcessing){
				return;
			}
			////console.log("Clicked nextQuestionButton ");
			nextQuestion();
		});
		

		$(".previousQuestionButton").click(function(e){
			////console.log("Clicked previousQuestionButton ");

		    e.preventDefault();

			if(isProcessing){
				return;
			}
			
		    /* if(questionUpdated){
				questionData = getQuestionDetailsByDivId(currentQuestion);
				if(questionData !=null){
					submitTestQuestionForm(questionData.id,'previousQuestion')
				}else{
					showSnackBar('Error in going to question try again.', "error");
				}
		    
		    	return;	
			} */
			previousQuestion();
		});
		

		$(".saveNNextButton").click(function(e){
			////console.log("Clicked saveNNextButton ");

		    e.preventDefault();

			if(isProcessing){
				return;
			}
			
				questionData = getQuestionDetailsByDivId(currentQuestion);
				if(questionData !=null){
					submitTestQuestionForm(questionData.id,'nextQuestion')
				}else{
					showSnackBar('Error in going to question try again.', "error");
				}
		    
		    	return;	
			 
		});
		
		function showTickForAnswerSaved(qId){
			var selector = "#answerAttemptedTick-"+qId;
			////console.log("In showTickForAnswerSaved() got selector: "+selector);
			$(selector).addClass("fa");
			$(selector).addClass("fa-check-circle");
		}
		
		function hideTickForAnswerSaved(qId){
			var selector = "#answerAttemptedTick-"+qId;
			////console.log("In hideTickForAnswerSaved() got selector: "+selector);
			$(selector).removeClass("fa-check-circle");
			$(selector).removeClass("fa");
		}
		
		function updateAttemptedQuestions(question){
			////console.log("In updateAttemptedQuestions() ");
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
					////console.log("Question "+question+" is Attempted");
					if(!attemptedQuestions[question-1].answered){
						////console.log("Question "+question+" is Skipped");
						attemptedQuestions[question-1].answered = true;
						noOfQuestionsAttemptted++;
						noOfQuestionsSkipped--;
						$("#statusQuestion" + question).text("Answered");
						$('#statusQuestion' + question).addClass('btn-success');
					}
					
				}else{
					////console.log("Question "+question+" is NOT Attempted");
					index = question - 1;
					item = 
							{
								qId : question,
								answered : true
							}		
						
					attemptedQuestions.splice(index, 1, item);
					////console.log("Pushed "+question+" in attemptedQuestions[]: "+attemptedQuestions);
					noOfQuestionsAttemptted++;
					$("#statusQuestion" + question).text("Answered");
					$('#statusQuestion' + question).addClass('btn-success');
					}
				}
			
			////console.log("setting noOfQuestionsAttemptted "+noOfQuestionsAttemptted+" to div ");
			$("#attemptsSpan").text((noOfQuestionsAttemptted > noOfQuestions) ? noOfQuestions : noOfQuestionsAttemptted);
			
			$("#attemptsLeft").text(((noOfQuestions - noOfQuestionsAttemptted) < 0) ? 0 : (noOfQuestions-noOfQuestionsAttemptted));
			$("#skippedSpan").text(noOfQuestionsSkipped);

		}
		
		function unAttemptAttemptedQuestion(question){
			////console.log("In unAttemptAttemptedQuestion() ");
			var arrayLength =attemptedQuestions.length;
			var temp=0;
			if(arrayLength==0){
				

			}else{
				if(attemptedQuestions[question-1] != null){
					////console.log("Question "+question+" is Attempted");
					if(attemptedQuestions[question-1].answered){
						////console.log("Question "+question+" is Skipped");
						attemptedQuestions[question-1].answered = false;
						noOfQuestionsAttemptted--;
						$("#statusQuestion" + question).text("Unattempted");
						$('#statusQuestion' + question).removeClass('btn-success');
					}
					
				}else{
					////console.log("Question "+question+" is NOT Attempted");
					}
				}
			
			////console.log("setting noOfQuestionsAttemptted "+noOfQuestionsAttemptted+" to div ");
			$("#attemptsSpan").text((noOfQuestionsAttemptted > noOfQuestions) ? noOfQuestions : noOfQuestionsAttemptted);
			
			$("#attemptsLeft").text(((noOfQuestions - noOfQuestionsAttemptted) < 0) ? 0 : (noOfQuestions-noOfQuestionsAttemptted) );
			calcProgress();

		}
		
		function nextQuestion(){
			 var promiseObj = new Promise(function(resolve, reject){
					
			////console.log("Entering nextQuestion() currentQuestion: "+currentQuestion);
			//updateSkippedQuestions(currentQuestion);
			$("#questionMainDiv-"+currentQuestion).addClass("hideQuestionMainDiv");
			currentQuestion=currentQuestion+1;
			if(currentQuestion>noOfQuestions){
				currentQuestion=1;
			}
			$("#questionMainDiv-"+currentQuestion).removeClass("hideQuestionMainDiv");
			$("#questionMainDiv-"+currentQuestion).addClass("showQuestionMainDiv");
			
			isProcessing = false;

			////console.log("Exiting nextQuestion() currentQuestion: "+currentQuestion);
			
			if(currentQuestion === noOfQuestions){
				$('#submitTest').attr('disabled',false);

				isLastQuestionVisited = true;
			}

			
			resolve(true);
			questionUpdated= false;
			$('#parentSpinnerDiv').hide();
			
			 })
			return promiseObj;
		}
			 
			 
		//NOT USED KEPT FOR REFERENCE
		function updateSkippedQuestions(question){
			////console.log("Question Skipped" + question )
			if(attemptedQuestions.length == 0){
				index = question - 1;
				item = 
						{
							qId : question,
							answered : false
						}		
					
				attemptedQuestions.splice(index, 1, item);
////console.log(attemptedQuestions)

					noOfQuestionsSkipped++;
$("#statusQuestion" + question).text("Skipped");
$('#statusQuestion' + question).addClass('skipped_status');
			}else{
				if(attemptedQuestions[question - 1] != null){
					////console.log(attemptedQuestions)

				} else{
					index = question - 1;
					item = 
							{
								qId : question,
								answered : false
							}		
						
					attemptedQuestions.splice(index, 1, item);
					////console.log(attemptedQuestions)

					noOfQuestionsSkipped++;
					$("#statusQuestion" + question).text("Skipped");
					$('#statusQuestion' + question).addClass('skipped_status');

				}
			}
			$("#skippedSpan").text(noOfQuestionsSkipped);
			

		}
		
		function previousQuestion(){
			var promiseObj = new Promise(function(resolve, reject){
				
			////console.log("Entering previousQuestion() currentQuestion: "+currentQuestion);
			
			$("#questionMainDiv-"+currentQuestion).addClass("hideQuestionMainDiv");
			currentQuestion=currentQuestion-1;
			if(currentQuestion < 1){
				currentQuestion=noOfQuestions;
			}
			$("#questionMainDiv-"+currentQuestion).removeClass("hideQuestionMainDiv");
			$("#questionMainDiv-"+currentQuestion).addClass("showQuestionMainDiv");
			
			isProcessing = false;
			
			////console.log("Exiting previousQuestion() currentQuestion: "+currentQuestion);
			
			resolve(true);
			

			if(currentQuestion === noOfQuestions){
				$('#submitTest').attr('disabled',false);

				isLastQuestionVisited = true;
			}
			
			questionUpdated= false;
			$('#parentSpinnerDiv').hide();
    		
			 })
			return promiseObj;
		}
		
		window.jumpToQuestion = function jumpToQuestion(question){
			

			if(isProcessing){
				return;
			}
			
			if(questionUpdated){
			/* 	
            swal({
                title: "Answer updated",
                text: " You have not saved current answer. ",
                icon: "warning",
                buttons: [
                  'Do Not Proceed!',
                  'Proceed, Without Saving'
                ],
                dangerMode: true,
              }).then(function(isConfirm) {
                if (isConfirm) {
    	            event.preventDefault();
    				jumpToQuestoinSubFunction(question);
                } else {
    	            event.preventDefault();
    	            return false;
                }
              });
             */
             

	            Swal.fire({
	                		title: "Answer updated",
	                		text: " You have not saved current answer. ",
			                icon: "warning",
			                showCancelButton: true,
			                confirmButtonColor: '#3085d6',
			                cancelButtonColor: '#d33',
			                cancelButtonText: 'Do Not Proceed!',
			                confirmButtonText: 'Proceed, Without Saving',
			                 
			              }).then(function(isConfirm) {
			            	   ////console.log(isConfirm);
			                if (isConfirm.value && isConfirm.value === true ) {
			    	            event.preventDefault();
			    				jumpToQuestoinSubFunction(question);
			                } else {
			    	            event.preventDefault();
			    	            return false;
			                }
			              });
            
				}else{

					jumpToQuestoinSubFunction(question);
				}
			
			
			/* if(questionUpdated){
				questionData = getQuestionDetailsByDivId(currentQuestion);
				if(questionData !=null){
					jumpToQuestionPlaceholder = question;
					submitTestQuestionForm(questionData.id,'jumpToQuestion')
				}else{
					showSnackBar('Error in going to question try again.', "error");
				}
				return;
			} */
			
		}
		function jumpToQuestoinSubFunction(question){
			var promiseObj = new Promise(function(resolve, reject){
				
			////console.log("Entering jumpToQuestion() Jumping to: "+question);
			$("#questionMainDiv-"+currentQuestion).addClass("hideQuestionMainDiv");
			currentQuestion=question;
			
			$("#questionMainDiv-"+currentQuestion).removeClass("hideQuestionMainDiv");
			$("#questionMainDiv-"+currentQuestion).addClass("showQuestionMainDiv");
			//Jump to top
			$("html,body").animate({
		        scrollTop: $("#toTop").offset().top},
		        'slow');
			
			
			isProcessing = false;
			////console.log("Exiting jumpToQuestion() currentQuestion: "+currentQuestion);
			

			resolve(true);
			

			if(currentQuestion === noOfQuestions){
				$('#submitTest').attr('disabled',false);

				isLastQuestionVisited = true;
			}
			

			questionUpdated= false;
			$('#parentSpinnerDiv').hide();
			 })
			return promiseObj;
		}
		
		window.markForReview = function markForReview(question){
			////console.log("Entering markForReview() got question "+question);
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
			
			
			////console.log("Exiting markForReview() question "+question);
		}
		
		function calcProgress(){
			var progressPercentage = ""+ ((noOfQuestionsAttemptted /noOfQuestions)*100) + "%";
			if(((noOfQuestionsAttemptted /noOfQuestions)*100) > 100){
				progressPercentage = "100%";
			}
			////console.log("In calcProgress progressPercentage: "+progressPercentage);
			$("#progressDiv").animate({
				width: progressPercentage},
		        'slow');
		}
		
		//NOT USED KEPT FOR FUTURE USE
		function shouldShowPreviousQuestionButton(){
			if(currentQuestion == 1){
				$("#previousQuestionButton-"+currentQuestion).hide();
				////console.log("Hiding previousQuestionButton");
			}else{
				$("#previousQuestionButton-"+currentQuestion).shwo();
				////console.log("Showing previousQuestionButton");
			}
		}

		//NOT USED KEPT FOR FUTURE USE
		function shouldShowNextQuestionButton(){
			if(currentQuestion == noOfQuestions){
				$("#nextQuestionButton-"+currentQuestion).hide();
				////console.log("Hiding nextQuestionButton");
			}else{
				$("#nextQuestionButton-"+currentQuestion).shwo();nextQuestionButton
			}
		}
		
		function showSnackBar(message,messageType) {
		    // Get the snackbar DIV
		    var x = document.getElementById("snackbar");
			////console.log("In showSnackBar() got message "+message);
		    x.innerHTML = message;
		    
		if(messageType == 'success'){
			// Add the "show snackbarSuccess" class to DIV
		    x.className = "show snackbarSuccess";
			
		}else if(messageType == 'error'){
			// Add the "show snackbarError" class to DIV
		    x.className = "show snackbarError";
			
		}else {
			// Add the "show snackbarError" class to DIV
		    x.className = "show snackbarError";
			
		}
			
		    
			

		    // After 1 seconds, remove the show class from DIV
		    setTimeout(function(){ x.className = ""; }, 1000);
			////console.log("Exiting showSnackBar()");
		}
		

		//submitTest end
		
		function submitTest(message){
			 ////console.log("SUBMITING TEST got message: "+message);
			 //ajax to save test start
			var testEndedStatus = "Manually Submitted";
	    		var body = {
	    			'testId' : testId,
	    			'sapid' : sapid,
	    			'attempt' : totalAttempt,
	    			'testEndedStatus' : testEndedStatus
	    		};
	    		////console.log(body);
	    		$.ajax({
	    			type : 'POST',
	    			url : '/exam/mbax/ia/sm/m/saveStudentsTestDetails',
	    			data: JSON.stringify(body),
	                contentType: "application/json",
	                dataType : "json",
	                success : function(data) {
	                	////console.log("iN AJAX SUCCESS");
	                	////console.log(data);
	                	showSnackBar("Test  Submitted!", "success");
	                	// Store
	                	  localStorage.setItem("testSubmitted", "Yes");
	                	 
	             			
	             		
	             
	                	// Retrieve
	                	isTestOver=true;

						questionUpdated=false;
						$('#parentSpinnerDiv').hide();
	                	checkIsTestOver(message);
			    	},
	    			error : function(result) {
	                	////console.log("iN AJAX eRROR");
	    				////console.log(result);
	    			    
	    			    //submitTestRetry Start
	    			    if(submitTestRetryCounter === 0){
	    			    	submitTestRetryCounter = 1;
	    			    	submitTest(message);
	    			    	
	    			    	
	    			    }else{

		    			    logErrorAjax(result,body);
							questionUpdated=false;
							$('#parentSpinnerDiv').hide();
		    				showSnackBar("Error in submitting Test. Try Again.", "error");	
	    			    }
	    			    //submitTestRetry End
	    			    
		    		}
	    		});
	    		//ajax to save question reponse end
			 ////console.log("SUBMITED TEST");
			 
		 
		}
		
		function checkIsTestOver(message){
			////console.log("IN checkIsTestOver() got message: "+message);
			if(isTestOver){

		  	   	//alert(localStorage.getItem("testSubmitted"));
				//window.location.href = "/exam/testOverPage";
				window.location.href = "/exam/mbax/ia/s/viewTestDetailsForStudentsForAllViews?userId=${userId}&id=${test.id}&message=testEnded";
		       	
			}
		}
		
		function saveStudentsQuestionResponse(testId,questionId){
			////console.log("In saveStudentsQuestionResponse got testId: "+testId+" questionId: "+questionId);
			//alert("In saveStudentsQuestionResponse got testId: "+testId+" questionId: "+questionId);
			
		} 
		
		window.checkFields = function checkFields(h4Id,isCaseStudy) {
			if(isCaseStudy){
				$(h4Id).addClass("greenLeftBorder");
				////console.log("In Checkfields() setting greenBottomBorder to h4Id:"+h4Id);
			}
			questionUpdated = true;
			if(currentQuestion == noOfQuestions){
	            ////console.log("Updating lastQuestionUpdated var. ");
				lastQuestionUpdated= true;
			}
			console.group("checkFields");
			/* if($("input[type=radio]").is(":checked") || $("input[type=checkbox]").is(":checked")){
				postAjaxSuccessOperations('submitTest');
				console.debug("field checked");	
			}else{
				console.debug("field not checked");
			} */
			
			/* if(questionUpdated){
				questionData = getQuestionDetailsByDivId(currentQuestion);
				if(questionData !=null){
					submitTestQuestionForm(questionData.id,'stayHere')
				}else{
					showSnackBar('Error in going to question try agin.', "error");
				}
					    
					    	return;	      
			} */
			console.groupEnd();
		}
		

		window.changedText = function changedText(textAreaId) {
			 var promiseObj = new Promise(function(resolve, reject){
					
			////console.log(" IN changedText() got textAreaId :"+textAreaId);
			let textSelector = '#textArea-'+textAreaId;
			let characterCountSelector = '#characterCount-'+textAreaId;
			
			////console.log(" IN changedText() got textSelector :"+textSelector);
			////console.log(" IN changedText() got characterCountSelector : "+characterCountSelector);
			let textInput = $(textSelector).val();
			////console.log(" IN changedText() got textInput :"+textInput);
			
			if(textInput){
				
				//code to set characterCount start
				 var words = textInput.match(/\S+/g).length;
					if (words > 1000) {
						// Split the string on first 200 words and rejoin on spaces
						//var trimmed = textInput.split(/\s+/, 5).join(" ");
						// Add a space at the end to keep new typing making new words
						//$(textSelector).val(trimmed + " ");
						
						$(characterCountSelector).html("<b> "+words+" / 1000 Words. (Number of words beyond recommended limit) </b>");
					}
					else {
						$(characterCountSelector).html("<b> "+words+" / 1000 Words </b>");
					}
				//code to set characterCount end
				
				if(currentQuestion == noOfQuestions){
		            ////console.log("Updating lastQuestionUpdated var. ");
					lastQuestionUpdated= true;
				}
				
				questionUpdated = true;
				
				////console.log(" IN changedText() got textInput.length :"+textInput.length);
				/* if(textInput.length % 50 == 0 ){
					//checkFields(textAreaId,false);
					questionData = getQuestionDetailsByDivId(currentQuestion);
					if(questionData !=null){
						submitTestQuestionForm(questionData.id,'stayHere')
					}else{
						showSnackBar('Error in going to question try agin.', "error");
					}
				} */
				
				if(textInput.length != 0 ){
					showTickForAnswerSaved(textAreaId);	
	          	}else if(textInput.length == 0 ){
	          		async function asyncCallToDeleteAnswerBySapIdQuestionId(){
						let deleteAns = await  deleteAnswerBySapIdQuestionId(textAreaId,sapid)
							.then(success, failure)
					 		function success(data){
								////console.log("In SUCCESS of AJAX CALL of Descriptive =====> ");
							
						 		}
					 		function failure(data){
								showSnackBar("Error in saving answer, Please try again . ", "error");
								
							 	} 
					}
			 		asyncCallToDeleteAnswerBySapIdQuestionId();
			 		
	          		hideTickForAnswerSaved(textAreaId);	
			 		unAttemptAttemptedQuestion(currentQuestion);
	          	}
				
			}else if(textInput == '' ){
				$(characterCountSelector).html("<b> 0 / 1000 Words </b>");
				
				async function asyncCallToDeleteAnswerBySapIdQuestionId(){
					let deleteAns = await  deleteAnswerBySapIdQuestionId(textAreaId,sapid)
						.then(success, failure)
				 		function success(data){
							////console.log("In SUCCESS of AJAX CALL of Descriptive =====> ");
						
					 		}
				 		function failure(data){
							//showSnackBar("Error in saving answer, Please try again . ", "error");
							
						 	} 
				}
		 		asyncCallToDeleteAnswerBySapIdQuestionId();
		 		
				hideTickForAnswerSaved(textAreaId);	
		 		unAttemptAttemptedQuestion(currentQuestion);
          	}else {
				hideTickForAnswerSaved(textAreaId);	
		 		unAttemptAttemptedQuestion(currentQuestion);
          	}
			
			resolve(true);

			$('#parentSpinnerDiv').hide();
			 })
			return promiseObj;
			
		} // changedText end 
		
		////console.log("Page Ready!");
		
		// Get todays date and time
		  var now = new Date().getTime();
		  
		//Add test's duration to current time
		var countDownDate = now + duration*60*1000;
		var remainingTimeCounter = 0; 
		
		var countdown = duration * 60 * 1000;
		
		
		//New Timer
		var timerId = setInterval(function(){
			  countdown -= 1000; 
				
			
			  var days = Math.floor(countdown / (1000 * 60 * 60 * 24));
			  var hours = Math.floor((countdown % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
			  var min = Math.floor((countdown % (1000 * 60 * 60)) / (1000 * 60));
			  var sec = Math.floor((countdown % (1000 * 60)) / 1000);
			  
			  
			  //var min = Math.floor(countdown / (60 * 1000));
			//  var hours = Math.floor(min / 60);
			  //var sec = Math.floor(countdown - (min * 60 * 1000));  // wrong
			  //var sec = Math.floor((countdown - (min * 60 * 1000)) / 1000);  //correct
			  
			  
			  // If the count down is finished, write some text
			  if (countdown <= 0) {
				  
				  hours=0;
				  min=0;
				  sec=0;
				  days=0;
				  
				  $('#parentSpinnerDiv').show();
				  ////console.log("BEFORE TIMEOVER");
				  
				   if(questionUpdated){
						questionData = getQuestionDetailsByDivId(currentQuestion);
						if(questionData !=null && questionData.type == 4 ){
							//alert("autosavng...");
							
							submitTestQuestionForm(questionData.id,'stayHere')
						}else{
							//showSnackBar('Error in going to question try again.', "error");
						}
					}
					$('#parentSpinnerDiv').show();

			    	submitTestRetryCounter = 0;
				  submitTest("testTimeOut");
				  ////console.log("AFTER TIMEOVER");
				  


			  }
			
			  //Shifted below 3 lines here to set 0 if timeover.
			  if(days>0){
				  $("#hoursSpan").text(days+"d "+hours);
			  }else{
			  	  $("#hoursSpan").text(hours);
			  }
			  $("#minutesSpan").text(min);
			  $("#secondsSpan").text(sec);
			  

			   if(questionUpdated){
					questionData = getQuestionDetailsByDivId(currentQuestion);
					if(questionData !=null && questionData.type == 4 ){
						saveDescriptiveAlertCountdown -= 1000; 
						
						
						if(saveDescriptiveAlertCountdown < 1 && !isSaveDescriptiveAlertLabelSet) {
							setAlertLabelToSaveDescriptive();
						}
					}
				}else{
					saveDescriptiveAlertCountdown = 5 * 60 * 1000  ;
					isSaveDescriptiveAlertLabelSet = false;
				}
			  
			  if(min % 5 ==0){
				  if(remainingTimeCounter == 0){
				  	////console.log('Updating remaining time');
				  	updateRemainingTimeCounter = 0;
				  	updateRemainingTime();
				  	remainingTimeCounter = 1;
				  }
			  }else{
				  remainingTimeCounter = 0;
			  }
			  
			  
	 		  if(min === 4 && hours === 0 && days === 0){
				  if(showFiveMinsLeftReminder){

					 try{
					  //Start sweet alert
					  /* 
					  swal("Last 5 Minutes Left!", "  Kindly Verify and Save all your answers. \n Questions Attempted: "+noOfQuestionsAttemptted+" \n  Questions Left: "+(noOfQuestions-noOfQuestionsAttemptted)+" ");
					   */
					  Swal.fire("Last 5 Minutes Left!", "  Kindly Verify and Save all your answers. \n Questions Attempted: "+noOfQuestionsAttemptted+" \n  Questions Left: "+(noOfQuestions-noOfQuestionsAttemptted)+" ");
						  
					  //end sweet alert
					 }catch(err){
						 alert("Last 5 Minutes Left! Kindly Verify and Save all your answers.  Questions Attempted: "+noOfQuestionsAttemptted+".  Questions Left: "+(noOfQuestions-noOfQuestionsAttemptted)+". ");
					 }
						showFiveMinsLeftReminder = false;
					} 				  
				  }


			/*   if (countdown <= 0) {
			     alert("30 min!");
			     clearInterval(timerId);
			     //doSomething();
			  } else {
			     $("#countTime").html(min + " : " + sec);
			  } */

			}, 1000); //1000ms. = 1sec.

			
			

			function setAlertLabelToSaveDescriptive(){

				qtnData = getQuestionDetailsByDivId(currentQuestion);
				
				if(qtnData !=null && qtnData.type == 4 ){
					let qtnId = qtnData.id;
					let alertSpanSelector = "#saveDescriptiveAlert-"+qtnId;
					let alertMessage = '<span style="padding: 5px 5px;" class="label label-danger"> Save Your Answer! </span>';
					
					$(alertSpanSelector).html(alertMessage);

					isSaveDescriptiveAlertLabelSet = true;
					
					function blinker() {
						////console.log("****** blinker() called..."+window["setIntervalVar-"+qtnId]);
						$(alertSpanSelector).fadeOut(500);
						$(alertSpanSelector).fadeIn(500);
					}
					window["setIntervalVar"+qtnId] =  setInterval(blinker, 1000);
					
					isSaveDescriptiveAlertLabelSet = true;
				}
				
			}
			
			function hideAlertLabelToSaveDescriptive(){

				qtnData = getQuestionDetailsByDivId(currentQuestion);
				
				if(qtnData !=null && qtnData.type == 4 ){
					let qtnId = qtnData.id;
					let alertSpanSelector = "#saveDescriptiveAlert-"+qtnId;
					let alertMessage = '';
					$(alertSpanSelector).html(alertMessage);
					isSaveDescriptiveAlertLabelSet = false;
					clearInterval(window["setIntervalVar"+qtnId]);
				}
				
			}
		
		//ajax call to update remaining time start
		function updateRemainingTime(){
	    		var body = {
	    			'testId' : testId,
	    			'sapid' : sapid,
	    			'attempt' : totalAttempt
	    		};
	    		////console.log(body);
	    		$.ajax({
	    			type : 'POST',
	    			url : '/exam/mbax/ia/s/updateRemainingTime',
	    			data: JSON.stringify(body),
	                contentType: "application/json",
	                dataType : "json",
	                success : function(data) {
	                	////console.log("iN AJAX SUCCESS");
	                	////console.log(data);
	                	
			    	},
	    			error : function(result) {
	                	////console.log("iN AJAX eRROR");
	    				////console.log(result);
	    				
	    				// retryUpdateRemainingTime start
	    				if(updateRemainingTimeCounter === 0){
	    					updateRemainingTimeCounter = 1;
	    					updateRemainingTime();
	    				}else{

		    				logErrorAjax(result,body);
		    			    questionUpdated=false;
							$('#parentSpinnerDiv').hide();
		    				showSnackBar("Error in updating remaining time.", "error");	
	    				}
	    				// retryUpdateRemainingTime end
	    				
		    		}
	    		});
		}
		
		//ajax call to update ramining time end
		
		
		//code to save error log start

		//logErrorAjax start
		 function logErrorAjax(stackTrace,body){
			////console.log("In logErrorAjax() ENTERED...");
			var methodRetruns = false;
			//ajax to save logErrorAjax reponse start
    		var body = {
    			'sapid' : sapid,
    			'module' : 'test',
    			'stackTrace' : JSON.stringify(body)+' - '+JSON.stringify(stackTrace),
    			
    		};
    		////console.log(body);
    		$.ajax({
    			type : 'POST',
    			url : '/studentportal/m/logError',
    			data: JSON.stringify(body),
                contentType: "application/json",
                dataType : "json",
                
    		}).done(function(data) {
				  ////console.log("iN logError AJAX SUCCESS");
              	////console.log(data);
              	
    		}).fail(function(xhr) {
    			////console.log("iN logError AJAX eRROR");
				////console.log('error', xhr);

			  });
			//ajax to save question reponse end
			//////console.log("In saveAnswerAjax() EXIT... got methodRetruns: "+methodRetruns);
			 

    	}
		//saveAnswerAjaxr end
		
		//code to save error log end
 
	}); //end of doc ready()
	
</script>


	<!-- FOR Custome text area -->

	<!-- Include jQuery lib. -->
	<script type="text/javascript"
		src="https://cdnjs.cloudflare.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>

	<!-- Include Editor JS files. -->
	<script type="text/javascript"
		src="https://cdnjs.cloudflare.com/ajax/libs/froala-editor/2.5.1//js/froala_editor.pkgd.min.js"></script>

	<!-- Initialize the editor. -->
	<script>
  $(function() {
    $('textareaNU').froalaEditor();
  });
  
  $('textareaNU').on('froalaEditor.initialized', function (e, editor) {
	  editor.events.on('drop', function () { return false; }, true);
	});
</script>
	<!-- end -->


	<script
		src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
	<script>
$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip(); 
});

</script>

	<script>




// Showing next button on click of previous button
/* function showNextButtonOnClickOfPreviousButton(){
	////console.log('IN showNextButtonOnClickOfPreviousButton()');
	$('.nextQuestionButton').show();
} */

$('#parentSpinnerDiv').hide();

</script>

	<script>
try{
parent && parent.window.setHideShowHeaderSidebarBreadcrumbs ? parent.window.setHideShowHeaderSidebarBreadcrumbs(true) : null
}catch(err){
	////console.log(err);
}
</script>


	<!-- script to disable back button -->
	<script>
try{
	history.pushState(null, null, location.href);
    window.onpopstate = function () {
        history.go(1);
    };
}catch(err){
	////console.log(err);
}
</script>


	<!-- script to go full screen 
<script>
try{

	////console.log("Calling GoInFullscreen() ");
	GoInFullscreen($("#pageContainer"));
	
/* Get into full screen */
function GoInFullscreen(element) {
	////console.log("Calling GoInFullscreen() element ");
	////console.log(element);
	if(element.requestFullscreen)
		element.requestFullscreen();
	else if(element.mozRequestFullScreen)
		element.mozRequestFullScreen();
	else if(element.webkitRequestFullscreen)
		element.webkitRequestFullscreen();
	else if(element.msRequestFullscreen)
		element.msRequestFullscreen();
}


	
}catch(err){
	////console.log(err);
}
</script>
-->


	<script>

/* Get into full screen */
function GoInFullscreen(element) {
	if(element.requestFullscreen)
		element.requestFullscreen();
	else if(element.mozRequestFullScreen)
		element.mozRequestFullScreen();
	else if(element.webkitRequestFullscreen)
		element.webkitRequestFullscreen();
	else if(element.msRequestFullscreen)
		element.msRequestFullscreen();
}

/* Get out of full screen */
function GoOutFullscreen() {
	if(document.exitFullscreen)
		document.exitFullscreen();
	else if(document.mozCancelFullScreen)
		document.mozCancelFullScreen();
	else if(document.webkitExitFullscreen)
		document.webkitExitFullscreen();
	else if(document.msExitFullscreen)
		document.msExitFullscreen();
}

/* Is currently in full screen or not */
function IsFullScreenCurrently() {
	var full_screen_element = document.fullscreenElement || document.webkitFullscreenElement || document.mozFullScreenElement || document.msFullscreenElement || null;
	
	// If no element is in full-screen
	if(full_screen_element === null)
		return false;
	else
		return true;
}

$("#go-button").on('click', function() {
	if(IsFullScreenCurrently())
		GoOutFullscreen();
	else
		GoInFullscreen($("#element").get(0));
});

$(document).on('fullscreenchange webkitfullscreenchange mozfullscreenchange MSFullscreenChange', function() {
	if(IsFullScreenCurrently()) {
		$("#element #spanDiv").text('Full Screen Mode Enabled');
		
		$("#go-button i").removeClass("fa-arrows-alt");
		
		$("#go-button i").addClass("fa-compress");	
		
	}
	else {
		$("#element #spanDiv").text('Full Screen Mode Disabled');

		$("#go-button i").removeClass("fa-compress");
		
		$("#go-button i").addClass("fa-arrows-alt");
	}
});





//Image, Video size and type validation
$('.input-file-btn').change(function (e) {
	//$('#answerFile').hide();
	
	$('.btn-success').attr('disabled', true);
	$('#parentSpinnerDiv').show();
	$('#uploadingAnswerMessage').text("Verifying File. Please wait...");
	////console.log("e.target.files[0]");
	////console.log(e.target.files[0]);
	if($('#file-link').get(0).files.length === 0){
		$('#parentSpinnerDiv').hide();
		$('#uploadingAnswerMessage').text("Please select file again using choose file.");
		alert("FILE NOT UPLOADED . PLEASE CHECK !");
		//console.log($('#hiddenAssngUrl').length);
		if($('#hiddenAssngUrl').length === 0){
			
		}else{
			//console.log("button is enabled");
			$('.btn-success').attr('disabled', false);
		}
		return false;
	}
	var filename = e.target.files[0].name;
	$('#file-name').val(filename);
	var extension = filename.split('.')[1];
	var file = e.target.files[0];
	var fileType = file["type"];

	var ValidTypes = [];
	var filesize = this.files[0].size;
	var acceptedSize;
	flag = false;
	//console.log("filename : "+filename);
	//console.log(extension);
	//console.log(file);
	//console.log(fileType);
	if($('#hiddenUploadType').val()=="pdf"){
		acceptedSize =  70000000;
		ValidTypes = ["application/pdf"];
	}else{
		acceptedSize =  70000000;
		ValidTypes = ["video/mp4"];
	};
	

	if ($.inArray(fileType, ValidTypes) < 0) {
		// invalid file type code goes here
			$('#parentSpinnerDiv').hide();
			$('#uploadingAnswerMessage').text("Invalid file. Please select file again using choose file.");
		//alert('Invalid File Type Error \n Valid types are : ' + ValidTypes + '\n  Given file type : ' + fileType);
		alert('INVALID FILE TYPE ERROR. \n Valid types are : ' + ValidTypes + '\n  Given file type : ' + fileType);
		return false;
	}else{
		flag = true;
	}
	if (filesize > acceptedSize) {
		$('#parentSpinnerDiv').hide();
		$('#uploadingAnswerMessage').text("Invalid file. Please select file again using choose file.");
		alert('file size exceeded the ' + acceptedSize / 1000000 + 'MB limit');
		return false;
	}else{
		flag = true;
	}
	
	if(flag){
		saveMedia(file);	
	}
	

});



function saveMedia(myFile) {
	try{
		var myFileInput = $('#file-link').prop('files')[0];
		//console.log("myFileInput : ");
		//console.log(myFileInput);
		
		var formData = new FormData();
		formData.append('image', myFileInput); 
		 $.ajax({
			url : '/exam/mbax/ia/sm/m/uploadTestAnswerFile?testId=${test.id}&userId=${userId}',
			data : formData,
			processData : false,
			contentType : false,
			type : 'POST',
			success : function(data) {
				//console.log("File uploaded : IN uploadTestQuestionImage got data : ");
				//console.log(data);
				//$('#link-location-text-link').val(data["imageUrl"]);
				//$('#link-location-text-link').attr('readonly', true);
				$('#hiddenAssngUrl').val(data["imageUrl"]);
				$('.btn-success').attr('disabled', false);
				$('#parentSpinnerDiv').hide();
				$('#uploadingAnswerMessage').text("Please click on save button to submit your answer.");
				$('#submitTest').attr('disabled', false);
				setTimeout(function(){ alert("YOUR FILE IS UPDATED. PLEASE CLICK ON SAVE TO SUBMIT IT !"); }, 1000);
			},
			error : function(err) {
				//console.log("File uploaded : IN uploadTestQuestionImage got error : ");
				$('#parentSpinnerDiv').hide();
				$('#uploadingAnswerMessage').text("Unable to verify your file. Please select file again using choose file.");
				//alert('File size should be less than 70Mb');
			}
		}); 
	}catch(error){
		alert('Following Error Occurred:'+error);
	}
}

</script>



	<!-- Comment below script to enable right click
  -->
  <script>
document.oncontextmenu = new Function("return false;");
</script>  

</body>
</html>