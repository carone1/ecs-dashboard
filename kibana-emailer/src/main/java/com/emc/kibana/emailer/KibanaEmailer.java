/*
 * Copyright (c) 2016, EMC Corporation.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *     + Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     + Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     + The name of EMC Corporation may not be used to endorse or promote
 *       products derived from this software without specific prior written
 *       permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */


package com.emc.kibana.emailer;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FileUtils;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;



/**
 * Utility to grab Kibana web sites' content and email them.
 */
public class KibanaEmailer {

	//================================
	// Final members
	//================================
	
	private static final String CONFIG_FILE_CONFIG_ARGUMENT  = "--config-file";
	
	private static final String KIBANA_URLS_CONFIG = "kibana.urls";
	
	private static final String CHROME_DRIVER_CONFIG = "chrome.driver";
	private static final String CHROME_BROWSER_CONFIG = "chrome.browser";
	private static final String SCREEN_CAPTURE_DELAY = "screen.capture.delay";
	
	private static final String DESTINATION_PATH_CONFIG = "destination.path";
	
	private static final String SMTP_HOST_CONFIG = "smtp.host";
	
	private static final String SMTP_PORT_CONFIG = "smtp.port";
	
	private static final String SMTP_USERNAME_CONFIG = "smtp.username";
	private static final String SMTP_PASSWORD_CONFIG = "smtp.password";
	private static final String SMTP_SECURITY_CONFIG = "smtp.security";
	private static final String SMTP_SECURITY_TLS = "tls";
	private static final String SMTP_SECURITY_SSL = "ssl";

	private static final String SOURCE_HOST_CONFIG = "source.host";
		

	private static final String SOURCE_ADDRESS_CONFIG = "source.address";
	private static final String DESTINATION_ADDRESS_CONFIG = "destination.addresses";
	
	private static final String MAIL_TITLE_CONFIG = "mail.title";
	
	private static final String MAIL_BODY_CONFIG = "mail.body";
	
	private static final String ABSOLUE_FILE_NAME_KEY = "absolute-file-name";
	private static final String FILE_NAME_KEY = "file-name";
	
	private static final String NAME_KEY = "name";
	private static final String URL_KEY = "url";
	
	
	private static final String            DATA_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
	
	
	//================================
	// Private Members
	//================================
	
	private static final SimpleDateFormat  DATA_DATE_FORMAT = new  SimpleDateFormat(DATA_DATE_PATTERN);
		
	private static String  chromeDriverPath = "";
	private static String  chromeBrowserPath = "";
	private static Integer screenCaptureDelay = 20; 
	private static String  kibanaConfigFile = "../config/kibana-emailer.yml";
	private static String  destinationPath = ".";

	private static ArrayList<Map<String, String>> kibanaUrls = new ArrayList<Map<String, String>>();
	
	private static List<Map<String, String>> kibanaScreenCaptures = new ArrayList<Map<String, String>>();
	
	private static String       smtpHost = "localhost";
	private static String       smtpPort = "587"; 
	private static String       smtpUsername = "username";
	private static String       smtpPassword = "password"; 
	private static String       smtpSecurity = SMTP_SECURITY_TLS;
	private static String       sourceHost = "localhost"; 
	private static String       sourceAddress = "name@mail.com"; 
	private static List<String> destinationAddressList = new ArrayList<String>();
	private static String       mailTitle = "Title";
	private static String       mailBody = "Body"; 
	

	private final static Logger       logger      = LoggerFactory.getLogger(KibanaEmailer.class);
	
	
	//================================
	// Public Methods
	//================================
	public static void main(String[] args) throws Exception {

		logger.info("Kibana reports generated at : " + new Date(System.currentTimeMillis()).toString()  ); 
		
		handleArguments(args);
		
		// file based config
		loadConfig();
		
		// generate screen captures
		generatePdfReports();	
		
		// destination e-mails present
		if( ! destinationAddressList.isEmpty() ){
			sendFileEmail(smtpSecurity);
		} 
	}

	//================================
	// Private Methods
	//================================
	/*
	 * utility method to handle program arguments
	 */
	private static void handleArguments( String[] args ) {
		
		String menuString = "Usage: KibanaEmailer " +
				"[" + CONFIG_FILE_CONFIG_ARGUMENT + " <configfile> [Default: ../config/kibana-emailer.yml]] - Specify config file \n";

		if ( args.length > 0 && args[0].contains("--help")) {
			System.err.println (menuString);
			System.exit(0);
		} else {

			int i = 0;
			String arg;

			if(args.length > 1) {
				while (i < args.length && args[i].startsWith("--")) {
					arg = args[i++];

					if (arg.equals(CONFIG_FILE_CONFIG_ARGUMENT)) {
						if (i < args.length) {
							kibanaConfigFile = args[i++];
						} else {
							System.err.println(CONFIG_FILE_CONFIG_ARGUMENT + " requires config file path/");
							System.exit(0);
						}
					} else {
						System.err.println(menuString);
						System.exit(0);
					} 
				}   
			}
		}
	}
	
	/*
	 * Utility method to load and parse emailer's configuration data
	 */
	@SuppressWarnings("unchecked")
	private static void loadConfig() {
		try {
			
			FileReader fr;
			fr = new FileReader(kibanaConfigFile);
			
			logger.info("Loading configuration from config file: " + kibanaConfigFile);
			
			YamlReader reader = new YamlReader(fr);
			
			Object object = reader.read();
			
			if(object == null) {
				logger.info( "Can't load config data from: " + kibanaConfigFile + " exiting...");
				System.out.println("Can't load config data from: " + kibanaConfigFile + " exiting...");
				System.exit(0);
			}
			
			Map<?,?> map = (Map<?,?>)object;
			
			if(map == null) {
				logger.info( "Can't load config data from: " + kibanaConfigFile + " exiting...");
				System.out.println("Can't load config data from: " + kibanaConfigFile + " exiting...");
				System.exit(0);
			}
			
		    // chromedriver
			Object driverPathObj = map.get(CHROME_DRIVER_CONFIG);
			if(driverPathObj != null && driverPathObj instanceof String ) {
				chromeDriverPath = (String)driverPathObj;
			} else {
				logger.info("Can't find: " + CHROME_DRIVER_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.out.println("Can't find: " + CHROME_DRIVER_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.exit(0);
			}
			
			// chromebrowser
			Object browserPathObj = map.get(CHROME_BROWSER_CONFIG);
			if(browserPathObj != null && browserPathObj instanceof String ) {
				chromeBrowserPath = (String)browserPathObj;
			} else {
				logger.info("Can't find: " + CHROME_BROWSER_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.out.println("Can't find: " + CHROME_BROWSER_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.exit(0);
			}
			
			Object captureDelayObj = map.get(SCREEN_CAPTURE_DELAY);
			if(captureDelayObj != null && captureDelayObj instanceof String ) {
				screenCaptureDelay = Integer.valueOf((String)captureDelayObj);
			} else {
				logger.info("Can't find: " + SCREEN_CAPTURE_DELAY + " in " + kibanaConfigFile + " exiting...");
				System.out.println("Can't find: " + SCREEN_CAPTURE_DELAY + " in " + kibanaConfigFile + " exiting...");
				System.exit(0);
			}
			
			// Kibana Urls
			Object urls = map.get(KIBANA_URLS_CONFIG);
			
			if(urls != null && urls instanceof ArrayList<?>) {
				kibanaUrls =  (ArrayList<Map<String, String>>)urls;
			} else {
				logger.info("Can't find: " + KIBANA_URLS_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.out.println("Can't find: " + KIBANA_URLS_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.exit(0);
			}
				
			// destination path to save screen captures
			Object destinationObj = map.get(DESTINATION_PATH_CONFIG);
			if( destinationObj != null && destinationObj instanceof String ) {
				destinationPath = (String)destinationObj;
			} else {
				logger.info("Can't find: " + DESTINATION_PATH_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.out.println("Can't find: " + DESTINATION_PATH_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.exit(0);
			}
			
			// == smtp config ==
			
			// smtp.host
			Object smtpHostObj = map.get(SMTP_HOST_CONFIG);
			if(smtpHostObj != null && smtpHostObj instanceof String) {
				smtpHost = (String)smtpHostObj;
			} else {
				logger.info("Can't find: " + SMTP_HOST_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.out.println("Can't find: " + SMTP_HOST_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.exit(0);
			}
			
			// smtp.port
			Object smtpPortObj = map.get(SMTP_PORT_CONFIG);
			if(smtpPortObj != null && smtpPortObj instanceof String) {
				smtpPort = (String)smtpHostObj;
			} else {
				logger.info("Can't find: " + SMTP_PORT_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.out.println("Can't find: " + SMTP_PORT_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.exit(0);
			}
			
			// smtp.username
			Object smtpUsernameObj = map.get(SMTP_USERNAME_CONFIG);
			if(smtpUsernameObj != null && smtpUsernameObj instanceof String) {
				smtpUsername = (String)smtpUsernameObj;
			} else {
				logger.info("Can't find: " + SMTP_USERNAME_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.out.println("Can't find: " + SMTP_USERNAME_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.exit(0);
			}
			
			// smtp.password
			Object smtpPasswordObj = map.get(SMTP_PASSWORD_CONFIG);
			if(smtpPasswordObj != null && smtpPasswordObj instanceof String) {
				smtpPassword = (String)smtpPasswordObj;
			} else {
				logger.info("Can't find: " + SMTP_PASSWORD_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.out.println("Can't find: " + SMTP_PASSWORD_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.exit(0);
			}
			
			// smtp.security "tls|ssl"
			Object smtpSecurityObj = map.get(SMTP_SECURITY_CONFIG);
			if(smtpSecurityObj != null && smtpSecurityObj instanceof String) {
				smtpSecurity = (String)smtpSecurityObj;
			} else {
				logger.info("Can't find: " + SMTP_SECURITY_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.out.println("Can't find: " + SMTP_SECURITY_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.exit(0);
			}

			// source.host
			Object sourceHostObj = map.get(SOURCE_HOST_CONFIG);
			if(sourceHostObj != null && sourceHostObj instanceof String) {
				sourceHost = (String)sourceHostObj;
			} else {
				logger.info("Can't find: " + SOURCE_HOST_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.out.println("Can't find: " + SOURCE_HOST_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.exit(0);
			}	

			// e-mail addresses configuration
			// source.mail
			Object sourceAddressObj = map.get(SOURCE_ADDRESS_CONFIG);
			if(sourceAddressObj != null && sourceAddressObj instanceof String) {
				sourceAddress = (String)sourceAddressObj;
			} else {
				logger.info("Can't find: " + SOURCE_ADDRESS_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.out.println("Can't find: " + SOURCE_ADDRESS_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.exit(0);
			}	
			
			// destination.addresses
			Object destinations = map.get(DESTINATION_ADDRESS_CONFIG);
			
			if(destinations != null && destinations instanceof ArrayList<?>) {
				destinationAddressList =  (ArrayList<String>)destinations;
			} else {
				logger.info("Can't find: " + DESTINATION_ADDRESS_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.out.println("Can't find: " + DESTINATION_ADDRESS_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.exit(0);
			}
			
			// e-mail content
			// mail.title
			Object mailTitleObj = map.get(MAIL_TITLE_CONFIG);
			if(mailTitleObj != null && mailTitleObj instanceof String) {
				mailTitle = (String)mailTitleObj;
			} else {
				logger.info("Can't find: " + MAIL_TITLE_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.out.println("Can't find: " + MAIL_TITLE_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.exit(0);
			}
			
			// mail.body
			Object mailBodyObj = map.get(MAIL_BODY_CONFIG);
			if(mailBodyObj != null && mailBodyObj instanceof String) {
				mailBody = (String)mailBodyObj;
			} else {
				logger.info("Can't find: " + MAIL_BODY_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.out.println("Can't find: " + MAIL_BODY_CONFIG + " in " + kibanaConfigFile + " exiting...");
				System.exit(0);
			}
			
			
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (YamlException e) {
			throw new RuntimeException(e);
		}
		
		logger.info("Loaded config details: " + 
						SMTP_HOST_CONFIG + ": `" + smtpHost + "`, " +
						SMTP_PORT_CONFIG + ": `" + smtpPort.toString() + "`, " +
						SMTP_USERNAME_CONFIG + ": `" + smtpUsername + "`, " +
						SOURCE_HOST_CONFIG  + ": `" + sourceHost + "`, " +
						SOURCE_ADDRESS_CONFIG + ": `" + sourceAddress + "`, " +
						DESTINATION_ADDRESS_CONFIG + ": `" + destinationAddressList.toString() + "`, " +
						MAIL_TITLE_CONFIG + ": `" + mailTitle + "`, " +
						MAIL_BODY_CONFIG + ": `" + mailBody
				    );
		logger.info("Loaded config details: " + KIBANA_URLS_CONFIG + ": " + kibanaUrls.toString());
	}
	
	/*
	 * Utility method that uses ChromeDriver to programmatically control
	 * the Chrome Browser to take dashboard screen captures 
	 */
	private static void generatePdfReports() {
		
		try {
			 System.setProperty("webdriver.chrome.driver", chromeDriverPath);		 
			 
			 Map<String, Object> chromeOptions = new HashMap<String, Object>();	
			 
			 // Specify alternate browser location
			 if(chromeBrowserPath != null && !chromeBrowserPath.isEmpty()) {
				 chromeOptions.put("binary", chromeBrowserPath);
			 }
			 
			 DesiredCapabilities capabilities = DesiredCapabilities.chrome();		 
			 
			 capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
 
			 int index = 1;
			 Date timestamp = new Date(System.currentTimeMillis());
			 
			 for( Map<String, String> entry : kibanaUrls) { 
				 
				 String dashboardName = entry.get(NAME_KEY);
				 String dashboardUrl = entry.get(URL_KEY);
				 
				 if(dashboardUrl == null) {
					 continue;
				 }

				 String filename;
				 
				 if((dashboardName != null)) {
					 String invalidCharRemoved = dashboardName.replaceAll("[\\/:\"*?<>|]+", "_").replaceAll(" ", "_");
					 filename = invalidCharRemoved + ".png";
				 } else {
					 filename = "dashboard-" + index++  + ".png";
				 }
				 
				 WebDriver driver = new ChromeDriver(capabilities);
				 driver.manage().window().maximize();
				 driver.get(dashboardUrl);

				 // let kibana load for x seconds before taking the snapshot
				 Integer delay = (screenCaptureDelay != null) ? (screenCaptureDelay * 1000) : (20000); 
				 Thread.sleep(delay);
				 // take screenshot
				 File scrnshot= ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
				 
				 // generate absolute path filename
				 String absoluteFileName = destinationPath + File.separator + 
						 DATA_DATE_FORMAT.format(timestamp) + File.separator + filename;
				 Map<String, String> fileMap = new HashMap<String, String>();
				 // file name portion
				 fileMap.put(FILE_NAME_KEY, filename);
				 fileMap.put(ABSOLUE_FILE_NAME_KEY, absoluteFileName);

				 kibanaScreenCaptures.add(fileMap);
				 File destFile = new File( absoluteFileName);

				 logger.info("Copying " + scrnshot + " in " +  destFile.getAbsolutePath());

				 FileUtils.copyFile(scrnshot, destFile);
				 driver.close();
			 }
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
	}
	
	/*
	 * Utility method that grabs png files and send them
	 * to a list of email addresses
	 */
	private static void sendFileEmail(String security)
	{

	      final String username = smtpUsername;
	      final String password = smtpPassword;
	      
	      // Get system properties
	      Properties properties = System.getProperties();

	      // Setup mail server
	      properties.setProperty("mail.smtp.host", smtpHost);

	      if( security.equals(SMTP_SECURITY_TLS) ) {
	    	  properties.put("mail.smtp.auth", "true");
	    	  properties.put("mail.smtp.starttls.enable", "true");
	    	  properties.put("mail.smtp.host", smtpHost);
	    	  properties.put("mail.smtp.port", smtpPort);
	      } else if( security.equals(SMTP_SECURITY_SSL) ) {
		      properties.put("mail.smtp.socketFactory.port", smtpPort);
		      properties.put("mail.smtp.socketFactory.class",
					              "javax.net.ssl.SSLSocketFactory");
		      properties.put("mail.smtp.auth", "true");      
		      properties.put("mail.smtp.port", smtpPort);
	      }

			Session session = Session.getInstance(properties,
			  new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			  });

	      try{
	         // Create a default MimeMessage object.
	         MimeMessage message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(sourceAddress));

	         // Set To: header field of the header.
	         for( String destinationAddress : destinationAddressList ) {
	        	 message.addRecipient( Message.RecipientType.TO,
	                                   new InternetAddress(destinationAddress));
	         }

	         // Set Subject: header field
	         message.setSubject(mailTitle);

	         // Create the message part 
	         BodyPart messageBodyPart = new MimeBodyPart();

	         StringBuffer bodyBuffer = new StringBuffer(mailBody);
	         if(!kibanaUrls.isEmpty()) {
	        	 bodyBuffer.append("\n\n"); 
	         }
	         
	         // Add urls info to e-mail
	         for( Map<String, String> kibanaUrl : kibanaUrls) {
	        	 // Add urls to e-mail
	        	 String urlName = kibanaUrl.get(NAME_KEY);
	        	 String reportUrl = kibanaUrl.get(URL_KEY);
	        	 if(urlName != null && reportUrl != null) {
	        		 bodyBuffer.append("- ").append(urlName).append(": ").append(reportUrl).append("\n\n\n");
	        	 }
	         }
	         
	         // Fill the message
	         messageBodyPart.setText(bodyBuffer.toString());
	         
	         // Create a multipart message
	         Multipart multipart = new MimeMultipart();

	         // Set text message part
	         multipart.addBodyPart(messageBodyPart);

	         // Part two is attachments
	         for(Map<String,String> kibanaScreenCapture : kibanaScreenCaptures) {
	        	 messageBodyPart = new MimeBodyPart();
	        	 String absoluteFilename = kibanaScreenCapture.get(ABSOLUE_FILE_NAME_KEY);
	        	 String filename = kibanaScreenCapture.get(FILE_NAME_KEY);
	        	 DataSource source = new FileDataSource(absoluteFilename);
	        	 messageBodyPart.setDataHandler(new DataHandler(source));
	        	 messageBodyPart.setFileName(filename);
	         
	        	 multipart.addBodyPart(messageBodyPart);
	         }

	         // Send the complete message parts
	         message.setContent(multipart );

	         // Send message
	         Transport.send(message);
	         logger.info("Sent mail message successfully");
	      }catch (MessagingException mex) {
	         throw new RuntimeException(mex);
	      }
	   }
	
}