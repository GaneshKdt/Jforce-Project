
<script src="resources_2015/multiple-select-wenzhixin/multiple-select.js"></script>

<script>
	$(document).ready(function () {
		
		
	    $('#consumerTypeId').multipleSelect({
	        filter: true,
	        //on checking all options from 
	        onCheckAll: function () {
	        	filterForProgramStructures();
	        	filterForPrograms();
	        },
	        onUncheckAll: function () {
	        	filterForProgramStructures();
	        	filterForPrograms();
	        },
	        onClick: function(view){
	        	filterForProgramStructures();
	        	filterForPrograms();
	        }
	        
	    });
	
	    $('#programStructureId').multipleSelect({
	        filter: true,
	
	        onCheckAll: function () {
	            filterForPrograms();
	        },
	        onUncheckAll: function () {
	            filterForPrograms();
	        },
	        onClick: function(view){
	        	filterForPrograms();
	        }
	    });
	
	    $('#programId').multipleSelect({
	        filter: true
	    });
	
	    initializeProgramStructureData();
	});

	function filterForPrograms(){
		
		//disable all
    	$.each($('#programId option'),function(){
    		$(this).prop('disabled', true);
    	});
		
		//get selected elements
    	var consumerProgramStructures = [];
		//to set selected as false
    	var consumerProgramStructuresNotPresent = [];
    	
    	if($('#programStructureId').val() != null && $('#programStructureId').val().length > 0){
        	$('#programStructureId').multipleSelect('setSelects', $('#programStructureId').val());
    	}else{
    		$('#programStructureId').multipleSelect('setSelects', []);
    	}
    	
    	var selectedConsumerTypes = $('#consumerTypeId').multipleSelect('getSelects');
    	var selectedProgramStructures = $('#programStructureId').multipleSelect('getSelects');
    	
    	if(selectedConsumerTypes.length > 0 && selectedProgramStructures.length > 0){
        	//loop through the list of all elements
        	consumerProgramStructureDetails.forEach(function(consumerProgramStructureDetail){
        		var consumerTypeId = consumerProgramStructureDetail.consumerTypeId;
				var programStructureId = consumerProgramStructureDetail.programStructureId;
				
        		//if this consumer type id and programStructureId is in selected list of selected elements
        	    if(
        	    		($.inArray(consumerTypeId, selectedConsumerTypes) !== -1) && 
        	    		($.inArray(programStructureId, selectedProgramStructures) !== -1)
      	    		){
        	    	consumerProgramStructures[consumerProgramStructureDetail.consumerProgramStructureId] = consumerProgramStructureDetail.programCode;
        	    }
        	});
    	}
    	
    	consumerProgramStructures.forEach(function(key, value){
    	    $('#programId option[value="'+ value + '"').prop('disabled', false);
    	})

    	if($('#programId').val() != null && $('#programId').val().length > 0){
        	$('#programId').multipleSelect('setSelects', $('#programId').val());
    	}else{
    		$('#programId').multipleSelect('setSelects', []);
    	}
    	
	    $('#programId').multipleSelect('refresh');
	
	    $.each($('li label'), function () {
	        $(this).parent().show();
	    });
	    $.each($('li label[class="disabled"]'), function () {
	        $(this).parent().hide();
	    });

	}
	
    function filterForProgramStructures(){
    	
    	//disable all 
    	$.each($('#programStructureId option'),function(){
    		$(this).prop('disabled', true);
    	});
    	var programStructures = [];
    	
    	if($('#consumerTypeId').val() != null && $('#consumerTypeId').val().length > 0){
        	$('#consumerTypeId').multipleSelect('setSelects', $('#consumerTypeId').val());
    	}else{
    		$('#consumerTypeId').multipleSelect('setSelects', []);
    	}
    	
    	var selectedConsumerTypes = $('#consumerTypeId').multipleSelect('getSelects');
    	
    	if(selectedConsumerTypes.length > 0){
        	//loop through the list of all elements
        	consumerProgramStructureDetails.forEach(function(consumerProgramStructureDetail){
        		var consumerTypeId = consumerProgramStructureDetail.consumerTypeId;
				
        		//if this consumerTypeId is in selected list
        	    if($.inArray(consumerTypeId, selectedConsumerTypes) >= 0){
        	    	programStructures[consumerProgramStructureDetail.programStructureId] = consumerProgramStructureDetail.programStructureName;
        	    }  		
        	});
    	}
    	
    	programStructures.forEach(function(key, value){
    	    $('#programStructureId option[value="'+ value + '"').prop('disabled', false);
    	})

    	
	    $('#programStructureId').multipleSelect('refresh');
    	
    	if($('#programStructureId').val() != null && $('#programStructureId').val().length > 0){
        	$('#programStructureId').multipleSelect('setSelects', $('#programStructureId').val());
    	}else{
    		$('#programStructureId').multipleSelect('setSelects', []);
    	}
    	
	    $.each($('li label'), function () {
	        $(this).parent().show();
	    });
	    $.each($('li label[class="disabled"]'), function () {
	        $(this).parent().hide();
	    });
    }
    
	
	function refreshConsumerPackages() {
		console.log("refreshing");
	    $('#programId').multipleSelect('refresh');
	    $('#consumerTypeId').multipleSelect('refresh');
	    $('#programStructureId').multipleSelect('refresh');
	
	    $.each($('li label'), function () {
	        $(this).parent().show();
	    });
	    $.each($('li label[class="disabled"]'), function () {
	        $(this).parent().hide();
	    });
	}
	
	function initializeProgramStructureData() {
	    consumerProgramStructureDetails.forEach(function (item) {
	        //add items to "programId"
			var itemName = 	item["programCode"] 
    			+ " | " + 	item["programStructureName"] 
    			+ " | " + 	item["consumerTypeName"] 
    			+ " | " + 	item["programName"];
	        
	        addOption(itemName, item["consumerProgramStructureId"], "#programId");
	
	    });
	
	    consumerTypeDetails.forEach(function (item) {
	        //add items to "consumerTypeId"
	        addOption(item["consumerTypeName"], item["consumerTypeId"], "#consumerTypeId");
	    });
	
	
	    programStructureDetails.forEach(function (item) {
	        //add items to "programStructureId"
	        addOption(item["programStructureName"], item["programStructureId"], "#programStructureId");
	    });
	
	
	    $('#consumerTypeId').multipleSelect('refresh');
	    $('#consumerTypeId').multipleSelect('checkAll');
	
	
	    $('#programStructureId').multipleSelect('refresh');
	    $('#programStructureId').multipleSelect('checkAll');
	
	    $('#programId').multipleSelect('refresh');
	    
		$('#programId').multipleSelect('setSelects', initiallySelectedItems);
	}
	
	function addOption(name, value, select) {
	    var o = new Option(name, value);
	
	    $(o).html(name);
	    $(select).append(o);
	}
</script>