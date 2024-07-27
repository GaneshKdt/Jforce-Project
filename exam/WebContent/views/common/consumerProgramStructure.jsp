 <%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<script>
$(document).ready(function(){
	 
	 function getConsumerTypeData(value,documentReady){
	  let options = "<option>Loading... </option>";
	  $('#programStructureId').html(options);
	  $('#programId').html(options);
	  $('.selectSubject').html(options);
	  
	   
	  var data = {
	    id:value
	  }
	 console.log(this.value)
	  
	  
	  $.ajax({
	   type : "POST",
	   contentType : "application/json",
	   url : "/exam/admin/getDataByConsumerType",   
	   data : JSON.stringify(data),
	   success : function(data) {
	    console.log("SUCCESS Program Structure: ", data.programStructureData);
	    console.log("SUCCESS Program: ", data.programData);
	    console.log("SUCCESS Subject: ", data.subjectsData);
	    var programData = data.programData;
	    var programStructureData = data.programStructureData;
	    var subjectsData = data.subjectsData;
	    
	    options = "";
	    let allOption = "";
	    
	    //Data Insert For Program List
	    //Start
	    
	    
	    for(let i=0;i < programData.length;i++){
	     allOption = allOption + ""+ programData[i].id +",";
	     if(programData[i].id == programId && documentReady){
	     
	      options = options + "<option selected value='" + programData[i].id + "'> " + programData[i].name + " </option>"; 
	     }else{
	      options = options + "<option value='" + programData[i].id + "'> " + programData[i].name + " </option>";
	     }
	     
	    }
	    allOption = allOption.substring(0,allOption.length-1);
	    
	    if(documentReady){
	     options = "<option value='"+ allOption +"'>All</option>" + options;
	    }else{
	     options = "<option selected value='"+ allOption +"'>All</option>" + options;
	    }
	   
	    console.log("==========> options\n" + options);
	    $('#programId').html(
	     options 
	    );
	    //End
	    options = ""; 
	    allOption = "";
	    //Data Insert For Program Structure List
	    //Start
	    
	    for(let i=0;i < programStructureData.length;i++){
	     allOption = allOption + ""+ programStructureData[i].id +",";
	     if(programStructureData[i].id == programStructureId && documentReady){
	     
	      options = options + "<option selected value='" + programStructureData[i].id + "'> " + programStructureData[i].name + " </option>"; 
	     }else{
	      options = options + "<option value='" + programStructureData[i].id + "'> " + programStructureData[i].name + " </option>";
	     }
	    }
	    allOption = allOption.substring(0,allOption.length-1);
	    
	    console.log("==========> options\n" + options);
	    if(documentReady){
	     options = "<option value='"+ allOption +"'>All</option>" + options;
	    }else{
	     options = "<option selected value='"+ allOption +"'>All</option>" + options;
	    }
	    
	    $('#programStructureId').html(
	     options 
	    );
	    //End
	    
	    options = ""; 
	    allOption = "";
	    //Data Insert For Subjects List
	    //Start
	    for(let i=0;i < subjectsData.length;i++){
	     if(g_subject== subjectsData[i].name.replace(/'/g, "&#39;") ){
	      options = options + "<option selected value='" + subjectsData[i].name.replace(/'/g, "&#39;") + "'> " + subjectsData[i].name + " </option>"; 
	     }else{
	      options = options + "<option value='" + subjectsData[i].name.replace(/'/g, "&#39;") + "'> " + subjectsData[i].name + " </option>";
	     }
	     //options = options + "<option value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";
	    }
	    
	    
	    console.log("==========> options22\n" + options);
	    $('.selectSubject').html(
	      " <option value=''> Select Subject </option> " + options
	    );
	    //End
	    
	    if(documentReady){
	     console.log("-----------> inside function call...");
	     getProgramStructureId(programStructureId,true);
	    }
	    
	   },
	   error : function(e) {
	    
	    alert("Please Refresh The Page.")
	    
	    console.log("ERROR: ", e);
	    display(e);
	   }
	  });
	 }
	 
	 function getProgramStructureId(value,documentReady){
	  let options = "<option>Loading... </option>";
	  $('#programId').html(options);
	  $('.selectSubject').html(options);
	  
	   
	  var data = {
	    programStructureId:value,
	    consumerTypeId:$('#consumerTypeId').val()
	  }
	  console.log(this.value)
	  
	  console.log("===================> data id : " + $('#consumerTypeId').val());
	  $.ajax({
	   type : "POST",
	   contentType : "application/json",
	   url : "/exam/admin/getDataByProgramStructure",   
	   data : JSON.stringify(data),
	   success : function(data) {
	    
	    console.log("SUCCESS: ", data.programData);
	    var programData = data.programData;
	    var subjectsData = data.subjectsData;
	    
	    options = "";
	    let allOption = "";
	    
	    //Data Insert For Program List
	    //Start
	    
	    for(let i=0;i < programData.length;i++){
	     allOption = allOption + ""+ programData[i].id +",";
	     if(programData[i].id == programId && documentReady){
	     
	      options = options + "<option selected value='" + programData[i].id + "'> " + programData[i].name + " </option>"; 
	     }else{
	      options = options + "<option value='" + programData[i].id + "'> " + programData[i].name + " </option>";
	     }
	     //options = options + "<option value='" + programData[i].id + "'> " + programData[i].name + " </option>";
	    }
	    allOption = allOption.substring(0,allOption.length-1);
	    
	    console.log("==========> options\n" + options);
	    if(documentReady){
	     options = "<option value='"+ allOption +"'>All</option>" + options
	    }else{
	     options = "<option selected value='"+ allOption +"'>All</option>" + options
	    }
	    $('#programId').html(
	      options
	    );
	    //End
	    
	    options = ""; 
	    allOption = "";
	    //Data Insert For Subjects List
	    //Start
	    for(let i=0;i < subjectsData.length;i++){
	     if(g_subject == subjectsData[i].name.replace(/'/g, "&#39;")){
	      options = options + "<option selected value='" + subjectsData[i].name.replace(/'/g, "&#39;") + "'> " + subjectsData[i].name + " </option>"; 
	     }else{
	      options = options + "<option value='" + subjectsData[i].name.replace(/'/g, "&#39;") + "'> " + subjectsData[i].name + " </option>";
	     }
	     //options = options + "<option value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";
	    }
	    
	    
	    console.log("==========> options\n" + options);
	    $('.selectSubject').html(
	      " <option value=''> Select Subject </option> " + options
	    );
	    //End
	    
	    if(documentReady){
	     console.log("-----------> inside function call...");
	     getProgramIdData(programId,true);
	    }
	    
	    
	   },
	   error : function(e) {
	    
	    alert("Please Refresh The Page.")
	    
	    console.log("ERROR: ", e);
	    display(e);
	   }
	  });
	 }
	 
	 function getProgramIdData(value,documentReady){
	  let options = "<option>Loading... </option>";
	  $('.selectSubject').html(options);
	  
	   
	  var data = {
	    programId:value,
	    consumerTypeId:$('#consumerTypeId').val(),
	    programStructureId:$('#programStructureId').val()
	  }
	  console.log(this.value)
	  
	  
	  $.ajax({
	   type : "POST",
	   contentType : "application/json",
	   url : "/exam/admin/getDataByProgram",   
	   data : JSON.stringify(data),
	   success : function(data) {
	    
	    console.log("SUCCESS: ", data.subjectsData);
	    
	    var subjectsData = data.subjectsData;
	    
	    
	    
	    
	    options = ""; 
	    //Data Insert For Subjects List
	    //Start
	    for(let i=0;i < subjectsData.length;i++){
	     if(g_subject== subjectsData[i].name.replace(/'/g, "&#39;") ){
	      options = options + "<option selected value='" + subjectsData[i].name.replace(/'/g, "&#39;") + "'> " + subjectsData[i].name + " </option>"; 
	     }else{
	      options = options + "<option value='" + subjectsData[i].name.replace(/'/g, "&#39;") + "'> " + subjectsData[i].name + " </option>";
	     }
	    }
	    
	    
	    console.log("==========> options\n" + options);
	    $('.selectSubject').html(
	      " <option value=''> Select Subject </option> " + options
	    );
	    //End
	    
	    
	    
	    
	   },
	   error : function(e) {
	    
	    alert("Please Refresh The Page.")
	    
	    console.log("ERROR: ", e);
	    display(e);
	   }
	  });
	 }
	 
	 if(programStructureId != '' && consumerTypeId != ''){
	  getConsumerTypeData(consumerTypeId,true);
	 }
	 
	 
	 $('.selectConsumerType').on('change', function(){
	  console.log("-----> inside oncall");
	  
	  let id = $(this).attr('data-id');
	  
	  consumerTypeId=this.value;
	 
	  getConsumerTypeData(this.value,false);
	  
	  
	 });
	  
	  ///////////////////////////////////////////////////////
	  
	  
	   $('.selectProgramStructure').on('change', function(){
	  
		   programStructureId=this.value;
	    console.log("-----> inside oncall");
	    getProgramStructureId(this.value,false);
	  
	  
	  
	  
	  
	 });

	 /////////////////////////////////////////////////////////////

	  
	   $('.selectProgram').on('change', function(){
		   programId=this.value;
	    console.log("-----> inside oncall");
	    getProgramIdData(this.value,false);
	  
	  
	  
	   });
});
</script>