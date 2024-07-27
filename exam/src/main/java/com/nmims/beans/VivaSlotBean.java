package com.nmims.beans;

import java.io.Serializable;

public class VivaSlotBean implements Serializable {
	private String id;
	private String sapid;
	private String viva_slots_id;
	private String year;
	private String month;
	private String date;
	private String start_time;
	private String end_time;
	private String capacity;
	private String booked;
	private String remaining;
	private String payment_status;
	private String track_id;
	private String reason;
	private String created_by;
	private String updated_by;
	private String meetingLink;
	
	
	public String getMeetingLink() {
		return meetingLink;
	}
	public void setMeetingLink(String meetingLink) {
		this.meetingLink = meetingLink;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getPayment_status() {
		return payment_status;
	}
	public void setPayment_status(String payment_status) {
		this.payment_status = payment_status;
	}
	public String getTrack_id() {
		return track_id;
	}
	public void setTrack_id(String track_id) {
		this.track_id = track_id;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getCreated_by() {
		return created_by;
	}
	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}
	public String getUpdated_by() {
		return updated_by;
	}
	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}
	public String getViva_slots_id() {
		return viva_slots_id;
	}
	public void setViva_slots_id(String viva_slots_id) {
		this.viva_slots_id = viva_slots_id;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public String getCapacity() {
		return capacity;
	}
	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}
	public String getBooked() {
		return booked;
	}
	public void setBooked(String booked) {
		this.booked = booked;
	}
	public String getRemaining() {
		return remaining;
	}
	public void setRemaining(String remaining) {
		this.remaining = remaining;
	}
	
	
}
