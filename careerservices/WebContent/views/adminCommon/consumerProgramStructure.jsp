<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="row">

	<div class="col-md-8 column">
		<div class="">
			<label for="consumerType">Consumer Type</label>
			<select data-id="consumerTypeDataId" id="consumerTypeId" name="consumerTypeId" class="selectConsumerType" multiple="multiple">
			</select>
		</div>
	</div>
	
	<div class="col-md-8 column">
		<div class="">
			<label for="programStructure">Program Structure</label>
			<select data-id="programStructureDataId" id="programStructureId" name="programStructureId"
				class="selectProgramStructure" multiple="multiple">
			</select>
		</div>
	</div>
	
	<div class="col-md-8 column">
		<div class="">
			<label for="Program">Program</label>
			<div class="">
				<select multiple="multiple" id="programId" name="programId[]" required>
				</select>
			</div>
			
		</div>
	</div>
	<!-- /////////////////////////////////////////////////////////////////// -->
</div>