<%--	 <head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<title><%=request.getParameter("title") %></title>
		<meta name="description" content="">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<link rel="shortcut icon" href="resources_2015/images/icons/favicon.png">
		<link rel="apple-touch-icon" href="resources_2015/images/icons/apple-touch-icon.png">

		<link rel="stylesheet" href="resources_2015/css/bootstrap.css">
		<!--<link rel="stylesheet" href="resources_2015/css/bootstrap.min.css">-->
		<!--<link rel="stylesheet" href="resources_2015/css/bootstrap-theme.min.css">-->
		<link rel="stylesheet" href="resources_2015/css/font-awesome.min.css">
		<link rel="stylesheet" href="resources_2015/css/jquery-ui.min.css">
		<link rel="stylesheet" href="resources_2015/css/main.css">
		<link rel="stylesheet" href="resources_2015/css/styles.css">
		<link rel="stylesheet" href="resources_2015/css/demo-styles.css?id=10" />
		<link rel="stylesheet" href="resources_2015/css/fileinput.css" />
		<link rel="stylesheet" href="resources_2015/css/bootstrap-datepicker.css" />
		<link rel="stylesheet" href="resources_2015/css/slick.css" />
		<link rel="stylesheet" href="resources_2015/css/slick-theme.css" />
		
		<!-- Editable element CSS -->
		<link rel="stylesheet" href="resources_2015/css/bootstrap-editable.css" />
		<script src="resources_2015/js/vendor/modernizr-2.8.3-respond-1.4.2.min.js"></script>
		
		<!--[if lt IE 9]>
		 <script src="resources_2015/js/vendor/html5shiv.min.js"></script>
		 <script src="resources_2015/js/vendor/respond.min.js"></script>
		<![endif]-->

	</head> --%>
	
	
<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<title><%=request.getParameter("title") %></title>
		<!-- Bootstrap -->
		<link href="assets/css/bootstrap.css" rel="stylesheet">
		<link href="assets/css/bootstrap-theme.css" rel="stylesheet">
		<link href="assets/fontawesome/css/all.css" rel="stylesheet">
		<link rel="stylesheet" type="text/css" href="assets/css/fullcalendar.min.css">
		<link rel="stylesheet" type="text/css" href="assets/css/fonts.css">		
		<link rel="stylesheet" type="text/css" href="assets/jquery-confirm/jquery-confirm.min.css">		
		<link rel="shortcut icon" href="resources_2015/images/icons/favicon.png">
		
		
		<link rel="stylesheet" type="text/css" href="assets/dataTable/datatables.min.css"/>
		<link rel="stylesheet" type="text/css" href="assets/css/style.css?v=4">
		<link rel="stylesheet" type="text/css" href="assets/css/style.css">
		<link rel="stylesheet" href="resources_2015/css/styles.css?id=1">
		<link href="assets/material-icons/material-icons.css" rel="stylesheet">
				<link rel="stylesheet" href="resources_2015/css/demo-styles.css" />
		
		
		<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
		<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
		<!--[if lt IE 9]>
		 <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
		 <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
		<![endif]-->
		
		
		<style type="text/css">
			:root{
				--nm-red: #d2232a;
			}
			
			.wysiwyg-generated p{
				font-size: medium;
			}
			
			.nm-red{
				color: #d2232a !important;
			}
			
			.bg-nm-red{
				background-color: #d2232a !important;
				color: white !important;
			}
			.p-special p{
			    font-size: 16px;
			    font-family: "Open Sans";
			    font-weight: normal;
    		}
			.embed_container .vertical-center{
			    display: flex;
			    align-items: center;
		    }
			.hover-icon {
				cursor:pointer;
			}
			.section-title h1, .section-title h2, .section-title h3, .section-title h4, .section-title h5 {
			    text-transform: none;
			    color: black;
			}
			
			.section-title h5 {
			    font-size: large;
			}
			
			.section-title h1 {
			    font-size: 24px;
			    text-transform: capitalize;
			    margin: 15px 0;
			    display: inline-block;
			    position: relative;
			    font-weight: 700;
			    /* padding-bottom: 15px; */
			    letter-spacing: 1px;
			    text-transform: uppercase;
			    /* text-decoration: underline; */
			    border-bottom: solid 1px;
			}
    		.wysiwyg-generated p{
    			font-weight: inherit;
    		}
    		
    		.rounded-image-container img{
               	display: block;
                height: 100%;
                width: 100%;
	            min-width: 100%;
	            min-height: 100%;
    		}
    		
    		.rounded-image-container{
                border-radius: 50%;
                overflow: hidden;
    		}
    		
			table li, .wysiwyg-generated li{
			    list-style: inherit;
			    margin-left: 20px;
			}
			.side-border-card {
				padding: 5px;
				border-left: 4px solid;
			}
			.side-border-card.card-primary {
				background-color: #fff8e1;
				border-color: #ffc107;
			}
			.side-border-card.card-secondary {
				background-color: #e8f5e9;
				border-color: #4caf50;
			}
			.side-border-card.card-tertiary {
				background-color: #ffebee;
				border-color: #f44336;
			}
				@keyframes flickerAnimation {
			 0%	{ opacity:1; }
			 50% { opacity:0; }
			 100% { opacity:1; }
			}
			@-o-keyframes flickerAnimation{
			 0%	{ opacity:1; }
			 50% { opacity:0; }
			 100% { opacity:1; }
			}
			@-moz-keyframes flickerAnimation{
			 0%	{ opacity:1; }
			 50% { opacity:0; }
			 100% { opacity:1; }
			}
			@-webkit-keyframes flickerAnimation{
			 0%	{ opacity:1; }
			 50% { opacity:0; }
			 100% { opacity:1; }
			}
			.animate-flicker {
				-webkit-animation: flickerAnimation .75s infinite;
				-moz-animation: flickerAnimation .75s infinite;
				-o-animation: flickerAnimation .75s infinite;
				animation: flickerAnimation .75s infinite;
			}
			
			.newFlag {
				background-image: none;
				margin: 0;
				margin-left: 5px;
				padding: 0;
				position: relative;
				bottom: 2px;
				font-weight: bold;
				color: red;
				font-size: .8em;
			}
			
				
			.table thead th {
				font-size: .95rem;
				font-weight: 500;
			}
				
			.table thead th {
				font-size: .95rem;
				font-weight: 500;
			}
			
			.vertical-center {
				vertical-align: middle !important;
			}
	
			.card .card-header, .card .card-footer {
				padding: 1rem;
				background-color: transparent;
			}
	
			.card {
				border: 0;
				box-shadow: 0 2px 2px 0 rgba(0,0,0,.14), 0 3px 1px -2px rgba(0,0,0,.2), 0 1px 5px 0 rgba(0,0,0,.12);
				overflow-x: auto;
			}
			
			.card-special{
				background: transparent; 
				color: black;
				width:100%; 
				font-size: 1.5vmax;
			}
			button.card-special{
				margin:0px;
			}
			button.card-special:hover{
				color: var(--nm-red);
			}
	
			@media (min-width: 320px) {
				.card-special{
					font-size: 12px;
				}
			}
			@media (min-width: 576px) {
				.card-special{
					font-size: 13px;
				}
			}
	
			@media (min-width: 768px) {
				.card-special{
					font-size: 14px;
				}
			}
	
			@media (min-width: 992px) {
				.card-special{
					font-size: 16px;
				}
			}
			@media (min-width: 1200px) {
				.card-special{
					font-size: 18px;
				}
			}
			
			button{
				text-shadow: none;
			}
			
			.text-center{
				float: none;
			}
			
			.text-manager{
				overflow: hidden;
				text-overflow: ellipsis;
			}
			
			.dataTables_filter input{
				float: inherit;
			}
			.text-vertical-center{
				display: flex;
				justify-content: center;
				align-items: center;
			}
			
			.padding-top{
				padding-top: 25px;
			}
			
			.sz-content h2, .sz-content h1, .sz-content h3, .sz-content h4, .sz-content h5 {
				text-transform: inherit;
			}
			h2 {
				margin: 0;
				color: inherit;
				
			}
			
			.sz-content .header{
				font-size: 2rem;
				padding-bottom: 30px;
				color: var(--nm-red);
			}
			
			.black{
				color: black;
			}
			
			
			.bullet-list li {
				list-style-type: disc;
				margin-left: 20px;
			}
			
			.carousel-inner img{
				max-height: 100%;
				max-width: 100%;
				width: 100%;
			}
			.carousel-inner img {
			 	margin: auto;
			}
			
			.carousel-control-prev-icon {
				background-image: url("data:image/svg+xml;charset=utf8,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='%23989797' viewBox='0 0 8 8'%3E%3Cpath d='M5.25 0l-4 4 4 4 1.5-1.5-2.5-2.5 2.5-2.5-1.5-1.5z'/%3E%3C/svg%3E");
			}
			
			.carousel-control-next-icon {
				background-image: url("data:image/svg+xml;charset=utf8,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='%23989797' viewBox='0 0 8 8'%3E%3Cpath d='M2.75 0l-1.5 1.5 2.5 2.5-2.5 2.5 1.5 1.5 4-4-4-4z'/%3E%3C/svg%3E");
			}
			
			.padding-top, .large-padding-top{
				padding-top: 25px !important;
			}
			
			
			@media (max-width: 768px)
			.sz-main-navigation ul.sz-nav > li.active ul.sz-sub-menu {
			    display: block;
			    position: absolute;
			    right: -250px;
			    top: 34px;
			    z-index: 1;
			    width: 250px;
			    padding: 0;
			    margin: 0;
			    list-style: none;
			}
			
		</style>
		
	</head>