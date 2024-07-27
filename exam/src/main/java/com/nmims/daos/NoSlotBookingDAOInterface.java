package com.nmims.daos;

import java.util.List;

import com.nmims.beans.NoSlotBookingBean;
import com.nmims.beans.StudentSubjectConfigExamBean;

public interface NoSlotBookingDAOInterface {
	/**
	 * Gets timebound acadYear, acadMonth, examYear, examMonth and prgm_sem_subj_id details using timeboundId.
	 * @param timeboundId
	 * @return timebound details
	 */
	StudentSubjectConfigExamBean getTimeboundDetails(final Long timeboundId);
	
	/**
	 * Fetches timebound user mapping count using userId, timeboundId and role.
	 * @param userId
	 * @param timeboundId
	 * @param role
	 * @return count of timebound user mapping
	 */
	int getTimeboundMappingCountByUserIdTimeboundIdRole(final String userId, final Long timeboundId, final String role);
	
	/**
	 * Gets consumerProgramStructure ID using sapid and academic year month
	 * @param sapid
	 * @param acadYear
	 * @param acadMonth
	 * @return consumer program structure ID
	 */
	Integer getCPSIdBySapidYearMonth(final String sapid, final String acadYear, final String acadMonth);

	/**
	 * Gets List of sessionPlan IDs for a particular timebound.
	 * @param timeboundId
	 * @return List of sessionPlan IDs
	 */
	List<Integer> getSessionPlanByTimebound(final Long timeboundId);

	/**
	 * Gets status of NoSlot booking using sapid, timeboundId, type and paymentRecordId
	 * @param sapid
	 * @param timeboundId
	 * @param type
	 * @param paymentRecordId
	 * @return status flag Y/N
	 */
	String getNoSlotBookingStatus(final String sapid, final Long timeboundId, final String type, final Long paymentRecordId);

	/**
	 * Gets the List of NoSlot bookings using sapid, timeboundId and type. 
	 * @param timeboundId
	 * @param sapid
	 * @param type
	 * @return List of bookings
	 */
	List<NoSlotBookingBean> getNoSlotBookingBySapidTimeboundType(final Long timeboundId, final String sapid, final String type);

	/**
	 * Inserts noSlot booking record.
	 * @param sapid
	 * @param timeboundId
	 * @param type
	 * @param paymentRecordId
	 * @param status
	 * @return
	 */
	int insertNoSlotBooking(final String sapid, final long timeboundId, final String type, final long paymentRecordId, final String status);

	/**
	 * Updates the noSlot booking status using sapid, timeboundId, type and paymentRecordId.
	 * @param sapid
	 * @param timeboundId
	 * @param type
	 * @param paymentRecordId
	 * @param status
	 * @return no of records updated
	 */
	int updateNoSlotBookingStatus(final String sapid, final String timeboundId, final String type, final long paymentRecordId, final String status);

	/**
	 * Check count of noSlot bookings using sapid, timeboundId, type and status.
	 * @param sapid
	 * @param timeboundId
	 * @param type
	 * @param status
	 * @return count of records found
	 */
	int checkNoSlotBookingStatus(final String sapid, final Long timeboundId, final String type, final String status);

	/**
	 * Gets List of NoSlot bookings by trackId
	 * @param trackId
	 * @return noSlot bookings list
	 */
	List<NoSlotBookingBean> noSlotBookingsByTrackId(final String trackId);

	/**
	 * Updates noSlot booking status using sapid and paymentRecordId.
	 * @param status
	 * @param modifiedByUser
	 * @param sapid
	 * @param paymentRecordId
	 * @return no of rows updated
	 */
	int updateNoSlotBookingStatusBySapidPaymentId(final String status, final String modifiedByUser, final String sapid, final Long paymentRecordId);

	/**
	 * Inserts the noSlot booking transaction conflict record.
	 * @param trackId
	 * @param bookingId
	 * @param userId
	 * @return count of rows inserted
	 */
	int insertNoSlotBookingConflictTransaction(final String trackId, final long bookingId, final String userId);
}