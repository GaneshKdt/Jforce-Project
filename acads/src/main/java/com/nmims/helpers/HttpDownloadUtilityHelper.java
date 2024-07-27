package com.nmims.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.IOUtils;

import com.nmims.beans.SessionDayTimeAcadsBean;

public class HttpDownloadUtilityHelper {

	private static final int BUFFER_SIZE = 4096;

	/**
	 * Downloads a file from a URL
	 * @param fileURL HTTP URL of the file to be downloaded
	 * @param saveDir path of the directory to save the file
	 * @throws IOException
	 */
	
	public static void downloadFile(String fileURL, SessionDayTimeAcadsBean sessionDayTimeBean)
			throws IOException {
		
		String videoName = sessionDayTimeBean.getSubject() + "-" + sessionDayTimeBean.getSessionName();
		String meetingId = sessionDayTimeBean.getMeetingKey();
		URL url = new URL(fileURL);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setInstanceFollowRedirects(true);  //you still need to handle redirect manully.
		HttpURLConnection.setFollowRedirects(true);
		int responseCode = httpConn.getResponseCode();

		boolean redirect = false;

		// normally, 3xx is redirect
		if (responseCode != HttpURLConnection.HTTP_OK) {
			if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP
				|| responseCode == HttpURLConnection.HTTP_MOVED_PERM
					|| responseCode == HttpURLConnection.HTTP_SEE_OTHER)
			redirect = true;
		}		

		if (redirect) {

			// get redirect url from "location" header field
			String newUrl = httpConn.getHeaderField("Location");

			// get the cookie if need, for login
			String cookies = httpConn.getHeaderField("Set-Cookie");

			// open the new connnection again
			httpConn = (HttpURLConnection) new URL(newUrl).openConnection();
			httpConn.setRequestProperty("Cookie", cookies);
//			httpConn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
//			httpConn.addRequestProperty("User-Agent", "Mozilla");
//			httpConn.addRequestProperty("Referer", "google.com");
									
		}
		
		// always check HTTP response code first
		if (responseCode == HttpURLConnection.HTTP_OK) {
			String fileName = "";
			String disposition = httpConn.getHeaderField("Content-Disposition");
			String contentType = httpConn.getContentType();
			int contentLength = httpConn.getContentLength();

			if (disposition != null) {
				// extracts file name from header field
				int index = disposition.indexOf("filename=");
				if (index > 0) {
					fileName = disposition.substring(index + 10,
							disposition.length() - 1);
				}
			} else {
				// extracts file name from URL
				fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
						fileURL.length());
			}

			String saveDir = "E:/Download";
			// opens input stream from the HTTP connection
			InputStream inputStream = httpConn.getInputStream();
			String saveFilePath = saveDir + File.separator + videoName + "-" +meetingId + ".mp4";
			
			// opens an output stream to save into file
			FileOutputStream outputStream = new FileOutputStream(saveFilePath);

			int bytesRead = -1;
			byte[] buffer = new byte[BUFFER_SIZE];
			//System.setProperty("https.protocols", "TLSv1.1");			
			System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");

			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			outputStream.close();
			inputStream.close();

		}
		httpConn.disconnect();
	}
	
	public String getRedirectUrl(String url) {
		URL urlTmp = null;
		String redUrl = null;
		HttpURLConnection connection = null;

		try {
			urlTmp = new URL(url);
		} catch (Exception e1) {
			
		}

		try {
			connection = (HttpURLConnection) urlTmp.openConnection();
		} catch (IOException e) {
			  
		}
		try {
			connection.getResponseCode();
		} catch (IOException e) {
			  
		}

		redUrl = connection.getURL().toString();
		connection.disconnect();

		return redUrl;
	}
	
	public String getContentFromTranscriptUrlWithHeader(String transcriptUrl) throws Exception {
		URL url = new URL(transcriptUrl);
		URLConnection con = url.openConnection();
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		InputStream in = con.getInputStream();
		String encoding = con.getContentEncoding();
		encoding = encoding == null ? "UTF-8" : encoding;
		String body = IOUtils.toString(in, encoding);		
		return body;
	}
	
	public String getContentFromTranscriptUrl(String transcriptUrl) throws Exception {
		URL url = new URL(transcriptUrl);
		URLConnection con = url.openConnection();
		InputStream in = con.getInputStream();
		String encoding = con.getContentEncoding();
		encoding = encoding == null ? "UTF-8" : encoding;
		String body = IOUtils.toString(in, encoding);		
		return body;
	}
	
	public String downloadFileAndSave(String sourceURL,String filePath,String fileName) {
		try {
			URL url = new URL(sourceURL);
			if(fileName == null) {
				fileName = sourceURL.substring(sourceURL.lastIndexOf('/') + 1, sourceURL.length());
			}
			Path targetPath = new File(filePath + File.separator + fileName).toPath();
			Files.copy(url.openStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
			return filePath + "/" + fileName;
		}
		catch (Exception e) {
			// TODO: handle exception
			  
			return null;
		}
	}
}
