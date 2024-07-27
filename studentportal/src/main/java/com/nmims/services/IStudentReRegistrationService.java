package com.nmims.services;

public interface IStudentReRegistrationService {
	/**
	 * Get student re-registration payment link.
	 * @param sapId - Contains student number.
	 * @param dob - The student date of birth
	 * @return Payment form link for student Re-Registration.
	 * @throws Exception If any exception occurs which process the re-registration applicable details.
	 */
	public String getReRegistrationPaymentLink(String sapId, String dob) throws Exception;

}
