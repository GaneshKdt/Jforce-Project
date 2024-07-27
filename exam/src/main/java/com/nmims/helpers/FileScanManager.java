package com.nmims.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


public class FileScanManager {
	
	
	public static void main(String[] args) throws Exception{
		/*String[] params = new String[3];
		FileScanner scanner = new FileScanner();
		params[0] = "-file:D:\\Marksheets\\77101090006_Booking.PDF1";
		//args[1] = "-server:localhost:1344";
		params[1] = "-policy:scandelete";
		params[2] = "-api:2";
		
		String scanResult = scanner.scanFile(params);
		 * */
		
		
		
		//File file = new FileInputStream("C://test.txt");
		InputStream tempInputStream = new FileInputStream("C:\\Users\\Sanket\\Desktop\\PhotocopyApplication_Dec2014.pdf");
		byte[] initialbytes = new byte[4];   
		tempInputStream.read(initialbytes);
		tempInputStream.close();
	}
	
	
}
