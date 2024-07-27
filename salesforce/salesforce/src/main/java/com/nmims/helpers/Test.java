package com.nmims.helpers;

import java.util.Date;

import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class Test {

	public static void main(String[] args) {
		Test.queryRecords();

	}


	public static void queryRecords() {
		QueryResult qResult = null;
		System.out.println("Querying Account details from SFDC");

		ConnectorConfig config = new ConnectorConfig();
		config.setUsername("rajiv.shah@nmims.edu");
		config.setPassword("ngsce@2015LRzDdr5Igxw5fl5FEwzhnug4");


		try {
			PartnerConnection connection = Connector.newConnection(config);
			String soqlQuery = "SELECT PersonEmail, nm_StudentNo__c, PersonMobilePhone, FirstName, LastName FROM Account"
					+ " where nm_StudentStatus__c = 'Confirmed' ";
			qResult = connection.query(soqlQuery);
			boolean done = false;
			if (qResult.getSize() > 0) {
				System.out.println("Logged-in user can see a total of "
						+ qResult.getSize() + " lead records.");
				while (!done) {
					SObject[] records = qResult.getRecords();
					for (int i = 0; i < records.length; ++i) {
						SObject s = (SObject) records[i];
						String fName = (String)s.getField("FirstName");
						String lName = (String)s.getField("LastName");
						String studentNo = (String)s.getField("nm_StudentNo__c");
						String email = (String)s.getField("PersonEmail");
						String mobile = (String)s.getField("PersonMobilePhone");

						System.out.println("Account " + (i + 1) + ": " + fName + " " +lName + " " + " " + studentNo + " " + email + " " + mobile);
					}
					if (qResult.isDone()) {
						done = true;
					} else {
						System.out.println("Querying more.....");
						qResult = connection.queryMore(qResult.getQueryLocator());
					}
				}
			} else {
				System.out.println("No records found.");
			}
			System.out.println("\nQuery succesfully executed.");
		} catch (ConnectionException ce) {
			ce.printStackTrace();
		}
	}

}
