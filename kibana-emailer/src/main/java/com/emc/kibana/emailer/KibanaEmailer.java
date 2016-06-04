package com.emc.kibana.emailer;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;










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
		
	private static String chromeDriverPath = "";
	private static String kibanaConfigFile = "kibana-emailer.yml";

	private static List<String> kibanaUrls = new ArrayList<String>();
	
	private final static Logger       logger      = LoggerFactory.getLogger(KibanaEmailer.class);
	
	
	public static void main(String[] args) throws Exception {

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
		
		loadConfig();
		
		generatePdfReports();	
		
		
		//logger.info("Total deletion time: " + deltaTime + " seconds");
		
	}

	
	private static void loadConfig() {
		try {
			
			FileReader fr = new FileReader(ClassLoader.getSystemResource(kibanaConfigFile).getFile());
			
			YamlReader reader = new YamlReader(fr);
			
			Object object = reader.read();
			
			Map<?,?> map = (Map<?,?>)object;
			
			chromeDriverPath = (String)map.get("chromedriver.path");
			
			Object urls = map.get("kibana.urls");
			
			if(urls instanceof ArrayList<?>) {
				ArrayList<?> urlList =  (ArrayList<?>)urls;
				
				Iterator<Map<String, String>> itr = (Iterator<Map<String, String>>) urlList.iterator();
				while( itr.hasNext() ) {
					Map<String, String> urlMap = itr.next();
					kibanaUrls.add( urlMap.get("url"));
				}
			}
			
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (YamlException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	private static void generatePdfReports() {
		
		try {
			 System.setProperty("webdriver.chrome.driver", chromeDriverPath);		 
			 
			 Map<String, Object> chromeOptions = new HashMap<String, Object>();
			 //chromeOptions.put("binary", chromeDriverPath);
			 
			 DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			 capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
 
			 for( String url : kibanaUrls) { 
				 WebDriver driver = new ChromeDriver(capabilities);
				 driver.manage().window().maximize();
				 driver.get(url);

				 Thread.sleep(20000);
				 File scrnshot= ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
				 File destFile = new File("googlessnapshot.png");

				 FileUtils.copyFile(scrnshot, destFile);
				 driver.close();
			 }	

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
	}
	
	
	
	
	
	
	
	
}