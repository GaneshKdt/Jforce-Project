 removeError=()=>{
		 document.getElementById("messages").style.display = "none";
		 document.getElementById("errorMessage").innerHTML="";
		 document.getElementById("errorButton").style.display = "none";
		 } 
    Filevalidation = (event) => {
        var fi = document.getElementById('file');
        // Check if any file is selected.
            
        if (fi.files.length > 0) {
            for (var i = 0; i <= fi.files.length - 1; i++) {
 
                var fsize = fi.files.item(i).size;
                var fileName = fi.value;
                var ext = fileName.substring(fileName.lastIndexOf('.') + 1);
                var file = Math.round((fsize / 1024));
                // The size of the file.
                if(ext=="pdf"||ext=="PDF"||ext=="Pdf"){
                	   if (file > 10240) {
                          document.getElementById("messages").style.display = "block";
                          document.getElementById("errorButton").style.display = "block";
                          document.getElementById("errorMessage").innerHTML="Please Upload File size of Less than 10MB";
                       	  var file =document.querySelector('#file');
                          file.value = '';
                       }
                	   else{
                		   document.getElementById("errorButton").style.display = "none";
                		   document.getElementById("messages").style.display = "none";
                           document.getElementById("errorMessage").innerHTML="";
                    	   } 
                }else{
                    document.getElementById("messages").style.display = "block";
                    document.getElementById("errorButton").style.display = "block";
                    document.getElementById("errorMessage").innerHTML="Please Upload only PDF files";
                 	  var file =document.querySelector('#file');
                    file.value = '';
                }
             
            }
        }
        }