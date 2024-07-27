$(document).ready(function(){$(document).on('change','#programType',function(){
				var programType = document.getElementById("programType").value;
				if(programType == ""){
					return false;
				}
				let optionsList = '<option value="" disabled selected>loading</option>';
				var body={"programType":programType}
				$.ajax({
					url:"getProgramNameByProgramType",
					method:"POST",
					data:JSON.stringify(body),
					contentType: "application/json",
					dataType: "json",
					success:function(response){
						optionsList = '<option disabled selected value="">-- Select Program Name --</option>';
						for(let i=0;i < response.length;i++){
							optionsList = optionsList + '<option value="'+ response[i] +'">'+ response[i]+'</option>';
						}
						$('#programName').html(optionsList);
						$('#semTerm').html("<option disabled selected value=''>-- Select Sem/Term --</option>")
					},
					error:function(error){
						alert("Error while getting schedule data");
					}
				});
				$('#programName').html(optionsList);
				$('#semTerm').html("<option disabled selected value=''>-- Select Sem/Term --</option>")
			});

			$(document).on('change','#programName',function(){
				var programName = document.getElementById("programName").value;
				if(programType == ""){
					return false;
				}
				let optionsList = '<option value="" disabled selected>loading</option>';
				var body={"programName":programName}
				$.ajax({
					url:"getSemTermByProgram",
					method:"POST",
					data:JSON.stringify(body),
					contentType: "application/json",
					dataType: "json",
					success:function(response){
						optionsList = '<option disabled selected value="">-- Select Sem/Term --</option>';
						for(let i=0;i < response.length;i++){
							optionsList = optionsList + '<option value="'+ response[i] +'">'+ response[i]+'</option>';
						}
						$('#semTerm').html(optionsList);
					},
					error:function(error){
						alert("Error while getting schedule data");
					}
				});
				$('#semTerm').html(optionsList);
			});

			$(document).on('click','#generate',function(){
				var body={"programType":document.getElementById("programType").value,
						"enrollmentmonth":document.getElementById("enrollmentmonth").value,
						"enrollmentyear":document.getElementById("enrollmentyear").value,
						"dataOfSubmission":document.getElementById("dataOfSubmission").value,
						"programName":document.getElementById("programName").value,
						"semTerm":document.getElementById("semTerm").value
						}
				document.getElementById("loading").style.display="block";
				document.getElementById("generate").style.display="none";
				$.ajax({
					url:"genratelouReport",
					method:"POST",
					data:JSON.stringify(body),
					contentType:"application/json",
					dataType:'json',
					success:function(response){
						document.getElementById("loading").style.display="none";
						document.getElementById("generate").style.display="block";
						document.getElementById("generatedResult").innerHTML="LOU Confirmed Student Count:"+response;
						if(response>0){
							document.getElementById("downloadlouReport").style.display="block";
						}else{
							document.getElementById("downloadlouReport").style.display="none";
						}
						}
					})

				});
		});