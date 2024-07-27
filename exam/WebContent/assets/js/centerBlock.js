$('#consumerTypeId')
	.on(
			'change',
			function() {

				let options = "<option>Loading... </option>";

				$('#programStructureId').html(options);

				var data = {
					id : $('#consumerTypeId').val()
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

								$('#programStructureId')
										.html(
												" <option disabled selected value=''> Select Program Structure </option> "
														+ options);

							},
							error : function(e) {

								alert("Failed to get data.")

							}
						});

			});

$('#programStructureId')
	.on(
			'change',
			function() {

				let options = "<option>Loading... </option>";

				$('#programId').html(options);

				var data = {
					programStructureId : $(
							'#programStructureId').val(),
					consumerTypeId : $('#consumerTypeId')
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

								$('#programId')
										.html(
												" <option disabled selected value=''> Select Program </option> "
														+ options);

							},

							error : function(e) {
								alert("Failed to get Data.")

								display(e);
							}
						});

			});