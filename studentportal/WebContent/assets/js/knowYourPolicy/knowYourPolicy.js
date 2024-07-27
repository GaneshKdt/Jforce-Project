$(document)
		.ready(
				function() {
					// below method will fetch subcategorylist using category
					$(document)
							.on(
									'change',
									'#categoryId',
									function() {
										var categoryId = document
												.getElementById("categoryId").value;
										if (categoryId == "") {
											return false;
										}
										let optionsList = '<option value="" disabled selected>loading</option>';
										var body = {
											"categoryId" : categoryId
										}
										$
												.ajax({
													url : "fetchsubcateogyusingcateogy",
													method : "POST",
													data : JSON.stringify(body),
													contentType : "application/json",
													dataType : "json",
													success : function(response) {
														optionsList = '<option disabled selected value="">-- Select Subcateogory --</option>';
														for (let i = 0; i < response.length; i++) {
															optionsList = optionsList
																	+ '<option value="'
																	+ response[i].subcategoryId
																	+ '">'
																	+ response[i].subcategoryName
																	+ '</form>';
														}
														$('#subcategoryId')
																.html(
																		optionsList);
													},
													error : function(error) {
														alert("Error while getting schedule data");
													}
												});
										$('#subcategoryId').html(optionsList);
									});

					// below method will validate input field while updating
					// policy
					$(document).on("click", "#update", function() {
						if (document.getElementById("groupId").value == 0) {
							alert("Please Select Group Name!")
							return false;
						}
						if (document.getElementById("categoryId").value == 0) {
							alert("Please Select Category Name!")
							return false;
						}
						if (document.getElementById("title").value == "") {
							alert("Please Enter Title!")
							return false;
						}
						if (CKEDITOR.instances.description.getData() == "") {
							alert("Please Enter Description!")
							return false;
						}
						return true;
					})

					// below method will validate input field while saving
					// policy
					$(document).on("click", "#savepolicy", function() {
						if (document.getElementById("groupId").value == 0) {
							alert("Please Select Group Name!")
							return false;
						}
						if (document.getElementById("categoryId").value == 0) {
							alert("Please Select Category Name!")
							return false;
						}
						if (document.getElementById("title").value == "") {
							alert("Please Enter Title!")
							return false;
						}
						if (CKEDITOR.instances.description.getData() == "") {
							alert("Please Enter Description!")
							return false;
						}
						return true;

					})

					// Below method will show alert message while deleting
					// policy
					$(document)
							.on(
									"click",
									"#deletepolicy",
									function() {
										if (confirm("Are You Sure You Want to Delete This Policy?") == true) {
											return true;
										} else {
											return false;
										}
									})

					// Below method will reset CKEDITOR description
					$(document).on("click", "#reset", function() {
						CKEDITOR.instances.description.setData('');
						return true;
					})

					// Below Method will input field Input Details While saving
					// subcategory
					$(document)
							.on(
									"click",
									"#savesubcategory",
									function() {
										if (document
												.getElementById("categoryId").value == 0) {
											alert("Please Select Category Name!");
											return false;
										}
										if (document
												.getElementById("subcategoryName").value == "") {
											alert("Please Enter Sub Category Name!");
											return false;
										} else {
											return true;
										}

									})
					// below method will validate input field during save
					// category name
					$(document)
							.on(
									"click",
									"#savecategory",
									function() {
										if (document
												.getElementById("categoryName").value == "") {
											alert("Please Enter Category Name!");
											return false;
										} else {
											return true;
										}
									})

				});