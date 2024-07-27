
$(document).ready(function(){
	 $('.selectConsumerType').on('change', function() {
						let id = $(this).attr('data-id');
						consumerTypeId = this.value;
						getConsumerTypeData(this.value, false);
					});

					$('.selectProgramStructure').on('change', function() {
						programStructureId = this.value;
						getProgramStructureId(this.value, false);
					});
	  
	  function getProgramStructureId(value,documentReady){
		  let options = "<option>Loading... </option>";
		  $('#programId').html(options);
		   
		  var data = {
		    programStructureId:value,
		    consumerTypeId:$('#consumerTypeId').val()
		  }
		
		  $.ajax({
		   type : "POST",
		   contentType : "application/json",
		   url : "/exam/admin/getDataByProgramStructure",   
		   data : JSON.stringify(data),
		   success : function(data) {
	
		    var programData = data.programData;
		    
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
		  
		    if(documentReady){
		     options = "<option value='"+ allOption +"'>All</option>" + options
		    }else{
		     options = "<option selected value='"+ allOption +"'>All</option>" + options
		    }
		    $('#programId').html(
		      options
		    );
		    //End
		   },
		   error : function(e) {   
			   alert("Please Refresh The Page.")		 
		    
		   }
		  });
		 }
		 

		 
	 function getConsumerTypeData(value,documentReady){
		  let options = "<option>Loading... </option>";
		  $('#programStructure').html(options);
		  $('#program').html(options);

		  
		   
		  var data = {
		    id:value
		  }
		
		  
		  
		  $.ajax({
		   type : "POST",
		   contentType : "application/json",
		   url : "/exam/admin/getDataByConsumerType",   
		   data : JSON.stringify(data),
		   success : function(data) {
		   
		   
		    var programData = data.programData;
		    var programStructureData = data.programStructureData;

		    
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
		    
		   
		    if(documentReady){
		     options = "<option value='"+ allOption +"'>All</option>" + options;
		    }else{
		     options = "<option selected value='"+ allOption +"'>All</option>" + options;
		    }
		    
		    $('#programStructureId').html(
		     options 
		    );
		    //End
		    
		    
		    
		   },
		   error : function(e) {
		    
		    alert("Please Refresh The Page.");
		   }
		  });
	 }
});