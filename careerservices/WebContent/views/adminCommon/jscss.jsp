     <head>
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
        <link rel="stylesheet" href="resources_2015/css/styles.css?id=1">
        <link rel="stylesheet" href="resources_2015/css/demo-styles.css" />
        <link rel="stylesheet" href="resources_2015/css/fileinput.css" />
        <link rel="stylesheet" href="resources_2015/css/bootstrap-datepicker.css" />
		<link rel="stylesheet" href="resources_2015/css/bootstrap-combobox.css" />
		<link rel="stylesheet" href="resources_2015/css/fullcalendar.css">
		<link rel="stylesheet" href="resources_2015/css/fullcalendar.print.css" rel='stylesheet' media='print'>
		
		<!-- 		links for DataTable -->
		<link rel="stylesheet" href="assets/dataTable/datatables.min.css">
		
		<script src="https://code.jquery.com/jquery-1.9.1.js"></script>
        <script src="resources_2015/js/vendor/modernizr-2.8.3-respond-1.4.2.min.js"></script>
        
        <link href="resources_2015/css/bootstrap-editable.css" rel="stylesheet">
        <link rel="stylesheet" href="resources_2015/css/typography.css">
        <link rel="stylesheet" href="assets/css/bootstrap-grid.css">
        
		<link rel="stylesheet" type="text/css" href="resources_2015/dataTable/RowGroup-1.1.0/css/rowGroup.bootstrap4.css"/>
		<link rel="stylesheet" type="text/css" href="resources_2015/dataTable/Responsive-2.2.2/css/responsive.bootstrap4.css"/>
		<link rel="stylesheet" type="text/css" href="resources_2015/dataTable/Buttons-1.5.6/css/buttons.dataTables.min.css"/>
		<link rel="stylesheet" type="text/css" href="resources_2015/dataTable/FixedHeader-3.1.4/css/fixedHeader.dataTables.min.css"/>
		
        <!--[if lt IE 9]>
          <script src="resources_2015/js/vendor/html5shiv.min.js"></script>
          <script src="resources_2015/js/vendor/respond.min.js"></script>
        <![endif]-->

		<style>
			.dt-button-collection.dropdown-menu{
				z-index:9999;
			}
			
			.btn-rounded{
			    font-size: inherit;
    			border-radius: .45rem!important;
			    height: auto;
			    border: 0;
			    padding: 0.5em 2em;
			    margin: 1em 0;
    		}
    		.btn-rounded, .btn-rounded:hover, .btn-rounded:focus, .btn-rounded:active {
			    color: #fff;
			    background: #404041;
			}
			
			.form{
			    padding-left: 15px;
			    padding-right: 15px;
			    background: #fff;
			    -webkit-box-shadow: 0 0 5px 0 rgba(0,0,0,.2);
			    box-shadow: 0 0 5px 0 rgba(0,0,0,.2);
			    margin-bottom: 20px;
			    padding: 15px;
			}
			
			.ml-3, .mx-3 {
			    margin-left: 1rem !important;
			}
			.mr-3, .mx-3 {
			    margin-right: 1rem !important;
			}
			
			.px {
			    padding-left: 15px;
			    padding-right: 15px;
			}
			
			.dataTables_filter label{
   				text-align: right !important;
			}
			
			.dataTables_filter input{
   				float: right;
			}
			
			.dataTables_wrapper.form-inline.dt-bootstrap.no-footer .row .col-sm-7{
				float: right !important;
			}
		</style>
    </head>