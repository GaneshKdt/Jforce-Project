

var id = "";
$(".tables").on('click', 'tr', function(e) {
	 e.preventDefault();	
	var str = $(this).attr('value');
	id = str.split('~');


});

$('.tables')
		.Tabledit(
				{
					columns : {
						identifier : [ 0, 'id' ],
						editable : [								
								[ 6, 'price'], 	
									 
								]
					},

					// link to server script
					// e.g. 'ajax.php'
					url : "",
					// class for form inputs
					inputClass : 'form-control input-sm',
					// // class for toolbar
					toolbarClass : 'btn-toolbar',
					// class for buttons group
					groupClass : 'btn-group btn-group-sm',
					// class for row when ajax request fails
					dangerClass : 'warning',
					// class for row when save changes
					warningClass : 'warning',
					// class for row when is removed
					mutedClass : 'text-muted',
					// trigger to change for edit mode.
					// e.g. 'dblclick'
					eventType : 'click',
					// change the name of attribute in td element for the row identifier
					rowIdentifier : 'id',
					// activate focus on first input of a row when click in save button
					autoFocus : true,
					// hide the column that has the identifier
					hideIdentifier : false,
					// activate edit button instead of spreadsheet style
					editButton : true,
					// activate delete button
					deleteButton : false,
					// activate save button when click on edit button
					saveButton : true,
					// activate restore button to undo delete action
					restoreButton : true,
					// custom action buttons
					// executed after draw the structure
					onDraw : function() {
						$('.tables').DataTable();
					},

					// executed when the ajax request is completed
					// onSuccess(data, textStatus, jqXHR)
					onSuccess : function() {

						return;
					},

					// executed when occurred an error on ajax request
					// onFail(jqXHR, textStatus, errorThrown)
					onFail : function() {
						return;
					},

					// executed whenever there is an ajax request
					onAlways : function() {
						return;
					},

					// executed before the ajax request
					// onAjax(action, serialize)

					onAjax : function(action, serialize) {
						setTimeout(()=>{
						//serialize['id'] = id[0];			
						//serialize['price'] = id[4];
						body = JSON.stringify(serialize)
					
						let data = JSON.parse(body);
						let priceNumber=data.price;
						
						 if(priceNumber === ""){
								 alert("Price cannot be Empty");
							 }
						 else if(isNaN(priceNumber)){
							 alert(priceNumber + " is not a number");
							 }
						 else{
								
							 
						$.ajax({
							type : "POST",
							url : 'updateCurrencyDetails',
							contentType : "application/json",
							data : body,
							dataType : "json",
							success : function(response) {
								

								if (response.Status == "Success") {
									$('#messageBox').prop("hidden",true);
									alert('Entries Saved Successfully');
								} else {
									alert('Entries Failed to update. Reload page and retry');
								}

							},
							error : function(error){
								alert('Entries Failed to update. Reload page and retry');
							}
						});
							 }
						}, 100);
					}

				});

function formValidation(){

    var price = document.getElementById('price').value;
    if(isNaN(price)){
		 alert(price + " is not a number");
        return false;    // in failure case
    }        
    return true;    // in success case
}
