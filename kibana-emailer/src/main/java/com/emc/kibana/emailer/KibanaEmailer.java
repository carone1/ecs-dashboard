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
 * Utility to grab web sites' content and email them.
 * Created by Eric Caron
 */
public class KibanaEmailer {

	private static final String CONFIG_FILE_CONFIG_ARGUMENT  = "--config-file";
	
	private static final String KIBANA_URLS_CONFIG = "kibana.urls";
	
	private static final String CHROME_DRIVER_CONFIG = "chromedriver";
	
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
	private static final String DESTINATION_ADDRESS_CONFIG = "destination.address";
	
	private static final String MAIL_TITLE_CONFIG = "mail.title";
	
	private static final String MAIL_BODY_CONFIG = "mail.body";
	
	private static final String ABSOLUE_FILE_NAME_KEY = "absolute-file-name";
	private static final String FILE_NAME_KEY = "file-name";
	
	private static final String NAME_KEY = "name";
	private static final String URL_KEY = "url";
	
	
	private static final String            DATA_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
	private static final SimpleDateFormat  DATA_DATE_FORMAT = new  SimpleDateFormat(DATA_DATE_PATTERN);
		
	private static String chromeDriverPath = "";
	private static String kibanaConfigFile = "kibana-emailer.yml";
	private static String destinationPath = ".";

	private static ArrayList<Map<String, String>> kibanaUrls = new ArrayList<Map<String, String>>();
	
	private static List<Map<String, String>> kibanaScreenCaptures = new ArrayList<Map<String, String>>();
	
	private static String smtpHost = "localhost";
	private static String smtpPort = "587"; 
	private static String smtpUsername = "username";
	private static String smtpPassword = "password"; 
	private static String smtpSecurity = SMTP_SECURITY_TLS;
	private static String sourceHost = "localhost"; 
	private static String sourceAddress = "name@mail.com"; 
	private static String destinationAddress = "name@mail.com"; 
	private static String mailTitle = "Title";
	private static String mailBody = "Body"; 
	
	
	private final static Logger       logger      = LoggerFactory.getLogger(KibanaEmailer.class);
	
	
	public static void main(String[] args) throws Exception {

		logger.info("Kibana reports generated at : " + new Date(System.currentTimeMillis()).toString()  ); 
		
		handleArguments(args);
		
		// file based config
		loadConfig();
		
		// generate screen captures
		generatePdfReports();	
		
		if(smtpSecurity.equals(SMTP_SECURITY_TLS)){
			sendFileEmailViaTLS();
		} else if (smtpSecurity.equals(SMTP_SECURITY_SSL)) {
			sendFileEmailViaSSL();
		}
	}

	
	private static void handleArguments( String[] args ) {
		
		String menuString = "Usage: KibanaEmailer " +
				"[" + CONFIG_FILE_CONFIG_ARGUMENT + " <configfile> [Default: ./config/kibana-emailer.yml]] - Specify config file \n";

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
	
	@SuppressWarnings("unchecked")
	private static void loadConfig() {
		try {
			
			FileReader fr = new FileReader(ClassLoader.getSystemResource(kibanaConfigFile).getFile());
			
			logger.info("Loading configuration from config file: " + kibanaConfigFile);
			
			YamlReader reader = new YamlReader(fr);
			
			Object object = reader.read();
			
			Map<?,?> map = (Map<?,?>)object;
			
		    // chromedriver
			chromeDriverPath = (String)map.get(CHROME_DRIVER_CONFIG);
			
			Object urls = map.get(KIBANA_URLS_CONFIG);
			
			if(urls instanceof ArrayList<?>) {
				kibanaUrls =  (ArrayList<Map<String, String>>)urls;
			}
				
			destinationPath = (String)map.get(DESTINATION_PATH_CONFIG);
			
			
			// smtp config 
			// smtp.host
			smtpHost = (String)map.get(SMTP_HOST_CONFIG);
			// smtp.port: 587
			smtpPort = (String)map.get(SMTP_PORT_CONFIG);
			// smtp.username: "user"
			smtpUsername = (String)map.get(SMTP_USERNAME_CONFIG);
			// smtp.password: "password"
			smtpPassword = (String)map.get(SMTP_PASSWORD_CONFIG);
			
			// smtp.security: "tls|ssl"
			smtpSecurity = (String)map.get(SMTP_SECURITY_CONFIG);

			// source.host
			sourceHost = (String)map.get(SOURCE_HOST_CONFIG);
				

			// e-mail addresses config
			// source.mail
			sourceAddress = (String)map.get(SOURCE_ADDRESS_CONFIG);
			// destination.mail
			destinationAddress = (String)map.get(DESTINATION_ADDRESS_CONFIG);
			// e-mail content
			// mail.title
			mailTitle = (String)map.get(MAIL_TITLE_CONFIG);
			// mail.body
			mailBody = (String)map.get(MAIL_BODY_CONFIG);
			
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
						DESTINATION_ADDRESS_CONFIG + ": `" + destinationAddress + "`, " +
						MAIL_TITLE_CONFIG + ": `" + mailTitle + "`, " +
						MAIL_BODY_CONFIG + ": `" + mailBody
				    );
		logger.info("Loaded config details: " + 
				KIBANA_URLS_CONFIG + ": " + kibanaUrls.toString()
		    );
	}
	
	
	private static void generatePdfReports() {
		
		try {
			 System.setProperty("webdriver.chrome.driver", chromeDriverPath);		 
			 
			 Map<String, Object> chromeOptions = new HashMap<String, Object>();		 
			 DesiredCapabilities capabilities = DesiredCapabilities.chrome();		 
			 
			 capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
 
			 int index = 1;
			 Date timestamp = new Date(System.currentTimeMillis());
			 
			 for( Map<String, String> entry : kibanaUrls) { 
				 
				 String dashboardName = entry.get(NAME_KEY);
				 String dashboardUrl = entry.get(URL_KEY);
				 
				 if(dashboardUrl != null) {
					
					 WebDriver driver = new ChromeDriver(capabilities);
					 driver.manage().window().maximize();
					 driver.get(dashboardUrl);

					 // let kibana load for 20 seconds before taking the snapshot
					 Thread.sleep(20000);
					 // take screenshot
					 File scrnshot= ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
					 // generate filename
					 String filename = (dashboardName != null) ? dashboardName + ".png" : "dashboard-" + index++  + ".png";
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
			 }	

		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
	}
	
	
	private static void sendFileEmailViaTLS()
	{

	      final String username = smtpUsername;
	      final String password = smtpPassword;
	      
	      // Get system properties
	      Properties properties = System.getProperties();

	      // Setup mail server
	      properties.setProperty("mail.smtp.host", smtpHost);

	      properties.put("mail.smtp.auth", "true");
	      properties.put("mail.smtp.starttls.enable", "true");
	      properties.put("mail.smtp.host", smtpHost);
	      properties.put("mail.smtp.port", smtpPort);

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
	         message.addRecipient(Message.RecipientType.TO,
	                                  new InternetAddress(destinationAddress));

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
	        		 bodyBuffer.append("- ").append(urlName).append(": ").append(reportUrl).append("\n");
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
	         mex.printStackTrace();
	      }
	   }
	
	
	
	private static void sendFileEmailViaSSL()
	{

	      final String username = smtpUsername;
	      final String password = smtpPassword;
	      
	      // Get system properties
	      Properties properties = System.getProperties();

	      // Setup mail server
	      properties.setProperty("mail.smtp.host", smtpHost);

	      properties.put("mail.smtp.host", smtpHost);
	      properties.put("mail.smtp.socketFactory.port", smtpPort);
	      properties.put("mail.smtp.socketFactory.class",
				              "javax.net.ssl.SSLSocketFactory");
	      properties.put("mail.smtp.auth", "true");
	      
	      
	      
	      properties.put("mail.smtp.port", smtpPort);
	      
	      
			Session session = Session.getDefaultInstance(properties,
			  new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			  });

	      
	      // Get the default Session object.
	      //Session session = Session.getDefaultInstance(properties);

	      try{
	         // Create a default MimeMessage object.
	         MimeMessage message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(sourceAddress));

	         // Set To: header field of the header.
	         message.addRecipient(Message.RecipientType.TO,
	                                  new InternetAddress(destinationAddress));

	         // Set Subject: header field
	         message.setSubject(mailTitle);

	         // Create the message part 
	         BodyPart messageBodyPart = new MimeBodyPart();

	         // Fill the message
	         messageBodyPart.setText(mailBody);
	         
	         // Create a multipar message
	         Multipart multipart = new MimeMultipart();

	         // Set text message part
	         multipart.addBodyPart(messageBodyPart);

	         // Part two is attachment
	         messageBodyPart = new MimeBodyPart();
	         String filename = "googlesnapshot.png";
	         DataSource source = new FileDataSource(filename);
	         messageBodyPart.setDataHandler(new DataHandler(source));
	         messageBodyPart.setFileName(filename);
	         multipart.addBodyPart(messageBodyPart);

	         // Send the complete message parts
	         message.setContent(multipart );

	         // Send message
	         Transport.send(message);
	         System.out.println("Sent message successfully....");
	      }catch (MessagingException mex) {
	         mex.printStackTrace();
	      }
	   }
}