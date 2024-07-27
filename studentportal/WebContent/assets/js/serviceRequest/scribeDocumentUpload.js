removeError=()=>{
		 document.getElementById("messages").style.display = "none";
		 document.getElementById("errorMessage").innerHTML="";
		 document.getElementById("errorButton").style.display = "none";
		 }
    Filevalidation = (event) => {
        const fi = document.getElementById('file');
        // Check if any file is selected.
        if (fi.files.length > 0) {
            for (const i = 0; i <= fi.files.length - 1; i++) {
 
                const fsize = fi.files.item(i).size;
                var fileName = fi.value;
                var ext = fileName.substring(fileName.lastIndexOf('.') + 1);
                const file = Math.round((fsize / 1024));
                // The size of the file.
                if(ext=="pdf"||ext=="PDF"||ext=="Pdf"){
                	   if (file > 10240) {
                          document.getElementById("messages").style.display = "block";
                          document.getElementById("errorButton").style.display = "block";
                          document.getElementById("errorMessage").innerHTML="Please Upload File size of Less than 10MB";
                       	  const file =document.querySelector('#file');
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
                 	  const file =document.querySelector('#file');
                    file.value = '';
                }
             
            }
        }
    }