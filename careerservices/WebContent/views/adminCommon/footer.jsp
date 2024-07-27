<div class="container-fluid footerWrapper">
        <footer>
            <div class="col-md-6 footerSection" style="color:white;;margin-top:20px;">
                <h2>Social Connect</h2>
                <h3>Connect with us via Social Media and get all our latest news and upcoming events.</h3>
                
                <div class="row">
                    <div class="col-lg-18">
                        <ul class="footerSocialLinks">
                            <li><a href="https://www.facebook.com/NMIMSSCE" target="_blank" class="facebook"><i class="fa fa-facebook"></i></a></li>
                            <li><a href="https://twitter.com/NMIMS_SCE" target="_blank" class="twitter"><i class="fa fa-twitter"></i></a></li>
                            <li><a href="https://plus.google.com/u/0/116325782206816676798/posts" target="_blank" class="google-plus"><i class="fa fa-google-plus"></i></a></li>
                            <li><a href="#" target="_blank" class="youtube"><i class="fa fa-youtube"></i></a></li>
                        </ul>
                    </div>  
                </div>
            </div>
            
            <div class="col-md-6 footerSection" style="color:white;;margin-top:20px;">
                <h2>Contact NGA SCE</h2>
                
                    Address:<br>
                    V.L.Mehta Road, Vile Parle (W), Mumbai, <br>
                    Maharashtra - 400056
                <br>
                    Email Address: ngasce@nmims.edu
                <br>
                    Tel. No: 7506283418 / 022 65265057
               <br>
                     © 2015 NMIMS. All Rights Reserved.
            </div>
            
            <% double random = Math.random(); %>
            <img alt="" src="/studentportal/resources_2015/images/singlePixel.gif?id=<%=random%>">
            <img alt="" src="/exam/resources_2015/images/singlePixel.gif?id=<%=random%>">
            <img alt="" src="/acads/resources_2015/images/singlePixel.gif?id=<%=random%>">
        </footer>
	</div>
    
    
<script src="resources_2015/js/vendor/bootstrap.min.js"></script>
<script src="resources_2015/js/vendor/jquery-ui.min.js"></script>
<script src="resources_2015/js/vendor/jquery.validate.min.js"></script>
<script src="resources_2015/js/vendor/additional-methods.min.js"></script>
<script src="resources_2015/js/vendor/fileinput.min.js"></script>
<script src="resources_2015/js/vendor/bootstrap-datepicker.min.js"></script>
<script src="resources_2015/js/vendor/scripts.js"></script>
<script src="resources_2015/js/main.js?id=1"></script>
<script src="resources_2015/js/vendor/moment.min.js"></script>
<script src="resources_2015/js/vendor/fullcalendar.min.js"></script>
<script src="resources_2015/js/vendor/bootstrap-combobox.js" ></script>
<script type="text/javascript" src="assets/js/jquery.serializejson.js"></script>


<script type="text/javascript" src="resources_2015/dataTable/datatables.min.js"></script>
<script type="text/javascript" src="resources_2015/dataTable/Buttons-1.5.6/js/dataTables.buttons.js"></script>
<script type="text/javascript" src="resources_2015/dataTable/Buttons-1.5.6/js/buttons.flash.js"></script>
<script type="text/javascript" src="resources_2015/dataTable/JSZip-2.5.0/jszip.js"></script>
<script type="text/javascript" src="resources_2015/dataTable/pdfmake-0.1.36/pdfmake.js"></script>
<script type="text/javascript" src="resources_2015/dataTable/pdfmake-0.1.36/vfs_fonts.js"></script>
<script type="text/javascript" src="resources_2015/dataTable/pdfmake-0.1.36/vfs_fonts.js"></script>
<script type="text/javascript" src="resources_2015/dataTable/Buttons-1.5.6/js/buttons.html5.js"></script>
<script type="text/javascript" src="resources_2015/dataTable/Buttons-1.5.6/js/buttons.print.js"></script>
<script type="text/javascript" src="resources_2015/dataTable/RowGroup-1.1.0/js/dataTables.rowGroup.js"></script>
<script type="text/javascript" src="resources_2015/dataTable/Responsive-2.2.2/js/dataTables.responsive.js"></script>
<script type="text/javascript" src="resources_2015/dataTable/FixedHeader-3.1.4/js/dataTables.fixedHeader.min.js"></script>

 <script type="text/javascript">
	 $(document).ready(function(){
	   $('.combobox').combobox();
 	});
 	function formDataToJSON(formName){
 		
 		if(formName == '' || formName == null || formName == 'undefined'){
 			formName = 'form';
 		}
 		
 		var myFormData = $(formName).serializeArray();
        for(var k in myFormData){
        	if(myFormData[k].value == "true"){
        		myFormData[k].value = true;
            }
        	if(myFormData[k].value == "false"){
        		myFormData[k].value = false;
        	}
       	}
        var data = {};
		myFormData.map(function(x){data[x.name] = x.value;}); 

		return JSON.stringify(data);
 	}
 	
 	function confirmDelete(type, url, id){
 		var txt;
 		var result = confirm("Are you sure you want to delete this " + type + " with ID: " + id + "? \n This action cannot be undone.");
 		if (result == true) {
 		  window.location.href=url;
 		}
 	}
 	
</script>