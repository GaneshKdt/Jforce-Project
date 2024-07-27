package com.nmims.daos;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.nmims.beans.RevenueReportConstants;
import com.nmims.beans.RevenueReportField;


public class RevenueReportsDAO {
	

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		System.out.println("Setting Data Source " + dataSource);
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
		System.out.println("jdbcTemplate = " + jdbcTemplate);
	}

	public JdbcTemplate getJdbCTempalte() {
		return jdbcTemplate;
	}
	
	public RevenueReportField getRevenueReportForSR(String date) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		RevenueReportField srRevenue = new RevenueReportField();
		srRevenue.setDate(date);
		srRevenue.setType(RevenueReportConstants.REVENUE_TYPE_SERVICE_REQUEST);
		
		try {
			String sql = ""
				+ " SELECT COALESCE(SUM(`amount`), 0) FROM ( "
					+ " SELECT `sr`.`amount` FROM `portal`.`service_request` `sr` "
				
					+ " WHERE "
						+ " `sr`.`transtatus` = 'Payment Successful' "
						+ " AND DATE(`sr`.`tranDateTime`) = ? "
					
					+ " GROUP BY `trackId` "
				+ " ) a ";
			
			String amount = jdbcTemplate.queryForObject(
				sql, 
				new Object[] { date },
				String.class
			);
			srRevenue.setAmount(amount);
			srRevenue.setActualPaymentAmount(amount);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error :" + e.getMessage());
			srRevenue.setErrorMessage(e.getMessage());
		}

		return srRevenue;
	}
	
	
	public RevenueReportField getRevenueReportForAdHoc(String date) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		RevenueReportField ahRevenue = new RevenueReportField();
		ahRevenue.setDate(date);
		ahRevenue.setType(RevenueReportConstants.REVENUE_TYPE_ADHOC_PAYMENT);
		
		try {
			String sql = ""
				+ " SELECT COALESCE(SUM(`amount`), 0) FROM ( "
					+ " SELECT `adp`.`amount` "
					+ " FROM `portal`.`ad_hoc_payment` `adp` " 
					+ " WHERE "
						+ " `adp`.`transtatus` = 'Payment Successful' "
						+ " AND DATE(`adp`.`tranDateTime`) = ? "
					+ " GROUP BY trackId "
				+ " ) a ";
			String amount = jdbcTemplate.queryForObject(
				sql, 
				new Object[] { date },
				String.class
			);
			ahRevenue.setAmount(amount);
			ahRevenue.setActualPaymentAmount(amount);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error :" + e.getMessage());
			ahRevenue.setErrorMessage(e.getMessage());
		}
		return ahRevenue;
	}

	public RevenueReportField getRevenueReportForAssignment(String date) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		RevenueReportField assignmentRevenue = new RevenueReportField();
		assignmentRevenue.setDate(date);
		assignmentRevenue.setType(RevenueReportConstants.REVENUE_TYPE_ASSIGNMENT);

		try {
			String sql = ""
				+ " SELECT COALESCE(SUM(`amount`), 0) FROM ( "
					+ " SELECT `amount` "
					+ " FROM `exam`.`assignmentpayment` "
					+ " WHERE "
						+ " `booked` = 'Y' "
						+ " AND DATE(`tranDateTime`) = ? "
				+ " ) a ";
			String amount = jdbcTemplate.queryForObject(
					sql, 
					new Object[] { date },
					String.class
				);
			assignmentRevenue.setAmount(amount);
			assignmentRevenue.setActualPaymentAmount(amount);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error :" + e.getMessage());
			assignmentRevenue.setErrorMessage(e.getMessage());
		}
		return assignmentRevenue;
	}
	

	public RevenueReportField getRevenueReportForExamBookingPG(String date) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		RevenueReportField ahRevenue = new RevenueReportField();
		ahRevenue.setDate(date);
		ahRevenue.setType(RevenueReportConstants.REVENUE_TYPE_EXAM);
		
		try {
			String sql = ""
				+ " SELECT COALESCE(SUM(`amount`), 0) FROM ( "
					+ " SELECT `eb`.amount"
					+ "	FROM `exam`.`exambookings` `eb` " 
					+ " WHERE "
						+ " `eb`.`booked` IN ('Y', 'RL') "
						+ " AND DATE(`tranDateTime`) = ? "
					+ " GROUP BY `trackId` "
				+ " UNION ALL "
					+ " SELECT `eb`.amount"
					+ "	FROM `exam`.`exambookings_history` `eb` " 
					+ " WHERE "
						+ " `eb`.`booked` IN ('Y', 'RL') "
						+ " AND DATE(`tranDateTime`) = ? "
					+ " GROUP BY `trackId` "
				+ " ) a ";


			String amount = jdbcTemplate.queryForObject(
				sql, 
				new Object[] { date, date },
				String.class
			);
			ahRevenue.setAmount(amount);
			ahRevenue.setActualPaymentAmount(amount);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error :" + e.getMessage());
			ahRevenue.setErrorMessage(e.getMessage());
		}
		return ahRevenue;
	}
	
	public RevenueReportField getRevenueReportForExamBookingMBAX(String date) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		RevenueReportField ebMBAXRevenue = new RevenueReportField();
		ebMBAXRevenue.setDate(date);
		ebMBAXRevenue.setType(RevenueReportConstants.REVENUE_TYPE_EXAM_MBAX);
		
		try {
			String sql = ""
				+ " SELECT COALESCE(SUM(`amount`), 0) FROM ( "
					+ " SELECT `mwp`.`amount` "
					+ "	FROM `exam`.`mba_x_bookings` `eb` " 
					
					+ " INNER JOIN `exam`.`mba_x_payment_records` `mwp` "
					+ " ON `eb`.`paymentRecordId` = `mwp`.`id` and `eb`.`sapid` = `mwp`.`sapid` "
					
					+ " WHERE "
						+ " `eb`.`bookingStatus` IN ('Y', 'RL') "
						+ " AND DATE(`mwp`.`tranDateTime`) = ? "
					
					+ " GROUP BY `mwp`.`trackId` "
				+ " ) a ";

			String amount = jdbcTemplate.queryForObject(
				sql, 
				new Object[] { date },
				String.class
			);
			ebMBAXRevenue.setAmount(amount);
			ebMBAXRevenue.setActualPaymentAmount(amount);
		} catch (Exception e) {
			System.out.println("Error :" + e.getMessage());
			e.printStackTrace();
			ebMBAXRevenue.setErrorMessage(e.getMessage());
		}
		return ebMBAXRevenue;
	}
	

	public RevenueReportField getRevenueReportForExamBookingMBAWX(String date) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		RevenueReportField ebMBAWXRevenue = new RevenueReportField();
		ebMBAWXRevenue.setDate(date);
		ebMBAWXRevenue.setType(RevenueReportConstants.REVENUE_TYPE_EXAM_MBAWX);
		
		try {
			String sql = ""
				+ " SELECT COALESCE(SUM(`amount`), 0) FROM ( "
					+ " SELECT `mwp`.`amount` "
					+ "	FROM `exam`.`mba_wx_bookings` `eb` " 
					
					+ " INNER JOIN `exam`.`mba_wx_payment_records` `mwp` "
					+ " ON `eb`.`paymentRecordId` = `mwp`.`id` and `eb`.`sapid` = `mwp`.`sapid` "
					
					+ " WHERE "
						+ " `eb`.`bookingStatus` IN ('Y', 'RL') "
						+ " AND DATE(`mwp`.`tranDateTime`) = ? "
					
					+ " GROUP BY `mwp`.`trackId` "
				+ " ) a ";

			String amount = jdbcTemplate.queryForObject(
				sql, 
				new Object[] { date },
				String.class
			);
			ebMBAWXRevenue.setAmount(amount);
			ebMBAWXRevenue.setActualPaymentAmount(amount);
		} catch (Exception e) {
			System.out.println("Error :" + e.getMessage());
			e.printStackTrace();
			ebMBAWXRevenue.setErrorMessage(e.getMessage());
		}
		return ebMBAWXRevenue;
	}
	

	public RevenueReportField getRevenueReportForPCPBookings(String date) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		RevenueReportField pcpRevenue = new RevenueReportField();
		pcpRevenue.setDate(date);
		pcpRevenue.setType(RevenueReportConstants.REVENUE_TYPE_PCP_PAYMENT);
		
		try {
			String sql = ""
					+ " SELECT COALESCE(SUM(`amount`), 0) FROM ( "
						+ " SELECT `pcp`.`amount` "
						+ " FROM `acads`.`pcpbookings` `pcp` " 
						+ " WHERE "
							+ " `booked` = 'Y' "
							+ " AND DATE(`tranDateTime`) = ? "
						+ " group by `trackId`"
					+ " ) a";

			String amount = jdbcTemplate.queryForObject(
				sql, 
				new Object[] { date },
				String.class
			);
			pcpRevenue.setAmount(amount);
			pcpRevenue.setActualPaymentAmount(amount);
		} catch (Exception e) {
			System.out.println("Error :" + e.getMessage());
			e.printStackTrace();
			pcpRevenue.setErrorMessage(e.getMessage());
		}
		return pcpRevenue;
	}
	


	public void getRefundAmountForDateAndType(RevenueReportField revenue, String type, String date) {
		
		try {
			String sql = ""
					+ " SELECT COALESCE(SUM(`amount`), 0) FROM ( "
						+ " SELECT `amount` "
						+ " FROM `exam`.`ad_hoc_refund` " 
						+ " WHERE "
							+ " `status` = 'success' "
							+ " AND DATE(`lastModifiedDate`) = ? "
							+ " AND `feesType` = ? "
						+ " group by `merchantRefNo`"
					+ " ) a";

			String refundedAmount = jdbcTemplate.queryForObject(
				sql, 
				new Object[] { date, type },
				String.class
			);
			revenue.setRefundedAmount(refundedAmount);
		} catch (Exception e) {
			System.out.println("Error :" + e.getMessage());
			e.printStackTrace();
			revenue.setErrorMessage(e.getMessage());
		}
	}
}
