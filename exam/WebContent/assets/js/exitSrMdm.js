$(document)
		.ready(
				function() {

					

					$('#consumer')
							.on(
									'change',
									function() {

										let options = "<option>Loading... </option>";

										$('#programStructure').html(options);

										var data = {
											id : $('#consumer').val()
										}

										$
												.ajax({
													type : "POST",
													contentType : "application/json",
													url : "getDataByConsumerTypeForEMBA",
													data : JSON.stringify(data),
													success : function(data) {

														var programStructureData = data.programStructureData;
														options = "";

														for (let i = 0; i < programStructureData.length; i++) {
															options = options
																	+ "<option value='"
																	+ programStructureData[i].id
																	+ "'> "
																	+ programStructureData[i].name
																	+ " </option>";
														}

														$('#programStructure')
																.html(
																		" <option disabled selected value=''> Select programStructure </option> "
																				+ options);

													},
													error : function(e) {

														alert("Failed to get data.")

													}
												});

									});

					$('#programStructure')
							.on(
									'change',
									function() {

										let options = "<option>Loading... </option>";

										$('#PrgrmName').html(options);

										var data = {
											programStructureId : $(
													'#programStructure').val(),
											consumerTypeId : $('#consumer')
													.val()
										}
										$
												.ajax({
													type : "POST",
													contentType : "application/json",
													url : "getDataByProgramStructureForEMBA",
													data : JSON.stringify(data),
													success : function(data) {

														var programData = data.programData;
														options = "";

														for (let i = 0; i < programData.length; i++) {
															options = options
																	+ "<option value='"
																	+ programData[i].id
																	+ "'> "
																	+ programData[i].name
																	+ " </option>";
														}

														$('#PrgrmName')
																.html(
																		" <option disabled selected value=''> Select programStructure </option> "
																				+ options);

													},

													error : function(e) {
														alert("Failed to get Data.")

														display(e);
													}
												});

									});

					$('#PrgrmName')
							.on(
									'change',
									function() {

										let options = "<option>Loading... </option>";
										$('#Sem').html(options);

										var data = {
											programStructure : $(
													'#programStructure').val(),
											programname : $('#PrgrmName').val(),
											consumerType : $('#consumer').val()
										}
										$
												.ajax({
													type : "POST",
													contentType : "application/json",
													url : "getTotalSemByProgramName",
													data : JSON.stringify(data),
													success : function(data) {
														options = "";
														if (data.code > 0) {
															for (let i = 1; i < data.code; i++) {

																options = options
																		+ "<option  value='"
																		+ i
																		+ "'> "
																		+ i
																		+ " </option>";
															}

															$('#Sem')
																	.html(
																			" <option disabled selected value=''> Select Sem   </option> "
																					+ options);
														} else {
															$('#Sem')
																	.html(
																			" <option disabled selected value=''> No Sem....  </option> "
																					+ options);
														}

													},
													error : function(e) {
														alert("Failed To get sem!");
													}
												});

									});
					
					
					$('#Sem')
					.on(
							'change',
							function() {
								let options = "<option>Loading... </option>";
								$('#newMasterkey').html(options);
								var data = {
									sem : $('#Sem').val(),
								}
								$
										.ajax({
											type : "POST",
											contentType : "application/json",
											url : "getProgramBySem",
											data : JSON.stringify(data),
											success : function(data) {
											options = "";
												for (let i = 0; i < data.length; i++) {
															options = options
																	+ "<option value='"
																	+ data[i].id
																	+ "'> "
																	+ data[i].programStructure+" - "+ data[i].consumerType+" - "+data[i].program
																	+ " </option>";
													}
												
												$('#newMasterkey')
												.html(
														" <option disabled selected value=''> Select program  </option> "
																+ options);
												
												$('#newMasterkey').select2();
												
												 
											},
											error : function(e) {
												alert("Failed To get Program!");
											}
										});
								
								

							});
					
				});


		
