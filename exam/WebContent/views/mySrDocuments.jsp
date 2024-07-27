
<c:if test="${not empty listOfSRDocumentsBasedOnSapid}">
	<div class="panel-heading" role="tab" id="">
		<h2>My SR Documents</h2>
		<div class="clearfix"></div>
	</div>
	
	<div class="table-responsive">
		<table class="table table-striped" style="font-size: 12px"
			id="mySrDocumentsTable">
			<thead>
				<tr>
					<th>Sr. No.</th>
					<th>SAPID</th>
					<th>SERVICE REQUEST TYPE</th>
					<th>DOWNLOAD SR DOCUMENTS</th>
				</tr>
			</thead>
			<tbody>	
				<c:forEach var="srDoc" items="${listOfSRDocumentsBasedOnSapid}" varStatus="docIndex">
					<tr>
					    <td>${docIndex.count}</td>
						<td>${srDoc.sapid}</td>
						<td>
							<c:choose>
								<c:when test="${srDoc.documentType eq 'SR E-Bonafide'}">
									Issuance of Bonafide
								</c:when>
								<c:otherwise>
									${srDoc.documentType}
								</c:otherwise>
							</c:choose>
						</td>
						<td><a target="_blank" id="srDocument" href="${srDoc.filePath}">Download</a></td> 
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</c:if>

