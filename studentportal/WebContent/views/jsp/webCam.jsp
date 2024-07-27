<html>
  <head>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Roboto:wght@100;400&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="assets/css/webcam.css?t=1122" />
    <style>
    	body{
    		font-family: 'Roboto', sans-serif;
    	}
    </style>
  </head>
  <body>
    <div id="myModal" class="modal">
      <!-- Modal content -->
      <div class="modal-content">
        <div class="c_loading_modal">
          <center>
            <div class="c_loading_div" style="max-width: 300px;">
              <div class="c_progress">
                <div
                  class="c_progress-bar c_progress-bar-info c_progress-bar-striped js_progress"
                  role="progressbar"
                  style="width: 0%"
                >
                  0%
                </div>
              </div>
            </div>
          </center>
        </div>
        <div class="modal-header">
          <span class="close" style="color: black">&times;</span>
          <h3>Student photo</h3>
        </div>
        <div class="modal-body" id="modalBody">
          <div class="c_alert c_alert_danger c_hide"></div>
          <div class="row">
            <div class="column">
              <div style="padding:0px">
                <center>
                  <div
                    class="photo_viewer"
                    id="result"
                    style="display: none"
                  ></div>
                  <div
                    class="photo_viewer"
                    id="my_camera"
                    style="display: none;"
                  >
                  <div id="camera" class="c_camera">

                  </div>
                </div>
                </center>
              </div>
              <center><small>Camera frame</small></center>
              <br />
              <center>OR</center>
              <br />
              <center>
              	
                <label for="fileUpload" class="c_btn c_btn-primary"
                  >Click here to pick photo from album</label
                >
              </center>
              <input type="file" id="fileUpload" style="display: none" />
            </div>
            <div class="column">
              <div style="font-size: 15px">
                <p>Guidelines</p>
                <ul>
                  <li>Keep a neutral expression</li>
                  <li>Ensure lips are sealed</li>
                  <li>Look directly into the camera</li>
                  <li>Make sure there are no distracting shadows</li>
                  <li>Use a white or off white(grey) background</li>
                  <li>
                    Fit the crown(top) of your head between the top of the two
                    lines
                  </li>
                  <li>Fit your chin between the bottom of the two lines</li>
                </ul>
                <label
                  ><input type="checkbox" class="js_guideLine" /> I agree, Image capture
                  as per mentioned guidelines</label
                >
                <div style="margin-top: 10px">
                  <button class="c_btn c_btn-primary js_click_photo_btn c_hide">
                    Click Photo
                  </button>
                  <button
                    class="c_btn c_btn-danger js_re_take_photo_btn c_hide"
                  >
                    Re-take Photo
                  </button>
                  <button class="c_btn c_btn-primary js_save_photo_btn c_hide">
                    Save Photo
                  </button>
                </div>
              </div>
            </div>
          </div>
          <br />
        </div>
      </div>
    </div>
    <script
      src="https://code.jquery.com/jquery-3.5.1.min.js"
      integrity="sha256-9/aliU8dGd2tb6OSsuzixeV4y/faTqgFtohetphbbj0="
      crossorigin="anonymous"
    ></script>
    <script type="text/javascript" src="assets/js/webcam.min.js"></script>
    <script language="JavaScript">
      var status = "camera_start"; // undefined,null,pick,uplaoding
      var typeOfImage = "camera";
      var mainHtmlOfModal = $('#modalBody').html();
      var url = new URL(window.location.href);
      var registrationNumber = url.searchParams.get("registrationNo");

      function showButtonList() {
        console.log("ShowButtonList fucntion call: " + status);
        if (status == "camera_start") {
          console.log("----->>>> initiate stage");
          $(".js_click_photo_btn").removeAttr("disabled").show();
          $(".js_re_take_photo_btn").removeAttr("disabled").hide();
          $(".js_save_photo_btn").removeAttr("disabled").hide();
          $(".js_guideLine").attr("disabled", true);
          $("#result").hide();
          $("#my_camera").show();
        } else if (status == "pick") {
          $(".js_click_photo_btn").removeAttr("disabled").hide();
          $(".js_re_take_photo_btn").removeAttr("disabled").show();
          $(".js_save_photo_btn").attr("disabled", true).show();
          $(".js_guideLine").removeAttr("disabled");
          $("#result").show();
          $("#my_camera").hide();
        } else if (status == "ready") {
          $(".js_click_photo_btn").removeAttr("disabled").hide();
          $(".js_re_take_photo_btn").attr("disabled", true).show();
          $(".js_save_photo_btn").removeAttr("disabled").show();
          $(".js_guideLine").removeAttr("disabled");
          $("#result").show();
          $("#my_camera").hide();
        } else {
          $(".js_click_photo_btn").hide();
          $(".js_re_take_photo_btn").hide();
          $(".js_save_photo_btn").hide();
          $(".js_guideLine").attr("disabled");
        }
      }
      function take_snapshot() {
        // take snapshot and get image data
        Webcam.snap(function (data_uri) {
          // display results in page
          status = "pick";
          typeOfImage = "camera";
          showButtonList();
          document.getElementById("result").innerHTML =
            '<img id="result_img" src="' + data_uri + '"/>';
        });
        Webcam.reset();
      }

      function readURL(input) {
        if (input.files && input.files[0]) {
          var reader = new FileReader();

          reader.onload = function (e) {
            status = "pick";
            showButtonList();
            document.getElementById("result").innerHTML =
              '<img id="result_img" style="max-height:240px;max-width:320px;" src="' +
              e.target.result +
              '"/>';
          };
          $(".js_guideLine").prop("checked", false);
          reader.readAsDataURL(input.files[0]);
          Webcam.reset();
        }
      }
      
      function showSuccessModal(){
    	let html = "<div><center><img src='https://i.ibb.co/3h4Wjkb/success.png' style='width:100%;max-width:200px;max-height:300px;margin-top:10px;margin-bottom:10px;' /><h2>Successfully image uploaded to re-upload <a class='btn btn-link' href='javascript:void(0)' id='js_reloadPage'>Click here</a></h2></center></div>";
    	$('#modalBody').html(html);
      }
      
      function showErrorModal(){
      	let html = "<div><center><img src='https://i.ibb.co/KFkNhTv/error.png' style='width:100%;max-width:200px;max-height:300px;margin-top:10px;margin-bottom:10px;' /><h2>Failed to upload image. <a class='btn btn-link' href='javascript:void(0)' id='js_reloadPage'>Click here</a> to try again</h2></center></div>";
      	$('#modalBody').html(html);
      }
      
      function showCustomErrorModal(errorMessage){
        	let html = "<div><center><img src='https://i.ibb.co/KFkNhTv/error.png' style='width:100%;max-width:200px;max-height:300px;margin-top:10px;margin-bottom:10px;' /><br/>"+ errorMessage +"</center></div>";
        	$('#modalBody').html(html);
      }

      function startWebcam() {
        status = "camera_start";
        showButtonList();
        Webcam.attach("#camera");
        $('#camera video').css("height","230px !important;");
        $('#camera').append('<div class="transparent_img" style="background-image:url(\'assets/images/transparent.png\');"></div>');
      }
      
      

      function showLoading() {
    	let height = $(".c_loading_modal").height();
    	height = (height/2) - $(".c_progress").height();
    	$(".c_progress").css("margin-top",height + "px");
        $(".c_loading_modal").show();
        Webcam.reset();
      }

      function hideLoading() {
        $(".c_loading_modal").hide();
        $(".js_guideLine").prop("checked", false);
        //startWebcam();
      }
      
      

      function uploadProgressHandler(event) {
        if (event.lengthComputable) {
          var percentComplete = event.loaded / event.total;
          percentComplete = parseInt(percentComplete * 100);
          console.log("percentComplete : " + percentComplete);
          $(".js_progress")
            .width(percentComplete + "%")
            .html(percentComplete + "%");
        } else {
          console.log("inside else");
        }
      }

      function dataURLtoFile(dataurl, filename) {
        var arr = dataurl.split(","),
          mime = arr[0].match(/:(.*?);/)[1],
          bstr = atob(arr[1]),
          n = bstr.length,
          u8arr = new Uint8Array(n);

        while (n--) {
          u8arr[n] = bstr.charCodeAt(n);
        }

        return new File([u8arr], filename, { type: mime });
      }

      function b64toBlob(b64Data, contentType, sliceSize) {
        contentType = contentType || "";
        sliceSize = sliceSize || 512;

        var byteCharacters = atob(b64Data);
        var byteArrays = [];

        for (
          var offset = 0;
          offset < byteCharacters.length;
          offset += sliceSize
        ) {
          var slice = byteCharacters.slice(offset, offset + sliceSize);

          var byteNumbers = new Array(slice.length);
          for (var i = 0; i < slice.length; i++) {
            byteNumbers[i] = slice.charCodeAt(i);
          }

          var byteArray = new Uint8Array(byteNumbers);

          byteArrays.push(byteArray);
        }

        var blob = new Blob(byteArrays, { type: contentType });
        return blob;
      }

      function generateString(length) {
        const characters ='ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        let result = " ";
        const charactersLength = characters.length;
        for (let i = 0; i < length; i++) {
          result += characters.charAt(
            Math.floor(Math.random() * charactersLength)
          );
        }

        return result;
      }

      $(document).ready(function () {
    	  if(!registrationNumber || registrationNumber == null || registrationNumber == '' || registrationNumber == undefined){
    		  showCustomErrorModal("<h3>404, Invalid url found</h3>");
    		  return false;
    	  }
    	  showLoading();
    	  $(".js_progress").width("50%").html("50%");
    	  
          $.ajax({
        	  url: "/studentportal/checkCanUploadStudentPhoto?registrationNo=" + registrationNumber ,
              method: "GET",
              success: function(response){
            	  console.log("Inside success");
            	  $(".js_progress").width("100%").html("100%");
            	  hideLoading();
            	  if(!response){
            		  showCustomErrorModal("<h3>Invalid response from server</h3>");
            		  return false;
            	  }
            	  if(response && response.status == "error"){
            		  showCustomErrorModal("<h3>"+ response.message +"</h3>");
            		  return false;
            	  }
            	  Webcam.set({
            	        width: 320,
            	        height: 240,
            	        image_format: "jpeg",
            	        jpeg_quality: 90,
            	  });
            	  startWebcam();
              },
              error:function(error){ 
            	  $(".js_progress").width("100%").html("100%");
            	  console.log("inside error");
            	  console.log(error);
            	  hideLoading();
            	  showCustomErrorModal("<h3>Something went wrong,Failed to parse response</h3>");
        		  return false;
              }
          });
    	  
    	$(document).on('click','#js_reloadPage',function(){
    		window.location.href = window.location.href;	
    	});
    	
        $(document).on("click", ".js_click_photo_btn", function () {
          take_snapshot();
        });
        $(document).on("click", ".js_re_take_photo_btn", function () {
          startWebcam();
        });
        $(document).on("change", ".js_guideLine", function () {
          if ($(this).prop("checked") == true) {
            status = "ready";
            showButtonList();
          } else {
            status = "pick";
            showButtonList();
          }
        });
        $(document).on("change", "#fileUpload", function () {
          console.log("file change ");
          readURL(this);
          typeOfImage = "file";
          $(this).val("");
        });
        $(document).on("click", ".js_save_photo_btn", function () {
        	showLoading();
      	  $(".js_progress").width("50%").html("50%");
          var fd = new FormData();
          var ImageURL = $("#result_img").attr("src");
          var block = ImageURL.split(";");
          // Get the content type of the image
          var contentType = block[0].split(":")[1]; // In this case "image/gif"
          // get the real base64 content of the file
          var realData = block[1].split(",")[1]; // In this case "R0lGODlhPQBEAPeoAJosM...."

          // Convert it to a blob to upload
          var blob = b64toBlob(realData, contentType);
          var fileName = generateString(10) + "-" + generateString(5) + "-" + new Date().getTime() +  "." + contentType.split("/")[1];
          console.log("fileName: " + contentType);
          fd.append("fileData", blob);
          fd.append("fileName", fileName);
          $.ajax({
            url: "/studentportal/uploadStudentPhoto?registrationNo=" + registrationNumber ,
            method: "POST",
            data: fd,
            contentType: false,
            processData: false,
            //dataType: "json",
            xhr: function () {
              var xhr = new window.XMLHttpRequest();
              xhr.upload.addEventListener(
                "progress",
                function (event) {
                  uploadProgressHandler(event);
                },
                false
              );
              return xhr;
            },
            success: function (result) {
            	
              hideLoading();
              console.log("--->>>> result");
              console.log(result);
              try{
              	if(result.status == 'success'){
              		showSuccessModal();
                	//successfullyUploaded(result.fileUrl,classId);
                }else{
                	showErrorModal();
                	console.error("error while uploading");
                   	//showErrorWhileUploading(classId,file.name,result.message);
               	}
             }
             catch(e){
            	 showErrorModal();
             	//showErrorWhileUploading(classId,file.name);
             }
            },
            error: function (error) {
            
              hideLoading();
              console.log(error);
              showErrorModal();
              //showErrorWhileUploading(classId,file.name,"Failed to upload image server error");
            },
          });
        });
      });
      
    </script>
  </body>
</html>
