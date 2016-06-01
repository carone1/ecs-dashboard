package com.emc.ecs.elasticsearch.cleaner;


import java.util.Arrays;
import java.util.Date;
//import java.util.Queue;
//import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emc.ecs.metadata.dao.BillingDAO;
import com.emc.ecs.metadata.dao.BillingDAO.ManagementDataType;
import com.emc.ecs.metadata.dao.ObjectDAO;
import com.emc.ecs.metadata.dao.ObjectDAO.ObjectDataType;
import com.emc.ecs.metadata.dao.elasticsearch.ElasticBillingDAO;
import com.emc.ecs.metadata.dao.elasticsearch.ElasticDAOConfig;
import com.emc.ecs.metadata.dao.elasticsearch.ElasticS3ObjectDAO;


/**
 * ECS ElasticSearch utility to remove old documents in ECS related indexes 
 * Created by Eric Caron
 */
public class ElasticSearchCleaner {
	
	
	private static final String  ECS_CLEAN_BILLING_DATA = "billing";
	private static final String  ECS_CLEAN_BUCKET_DATA = "bucket";
	private static final String  ECS_CLEAN_OBJECT_DATA = "object";
	private static final String  ECS_CLEAN_OBJECT_VERSION_DATA = "object-version";
	private static final String  ECS_CLEAN_ALL_DATA = "all";
	

	private static final String ECS_CLEAN_DATA_CONFIG_ARGUMENT  = "--clean-data";
	private static final String ELASTIC_HOSTS_CONFIG_ARGUMENT     = "--elastic-hosts";
	private static final String ELASTIC_PORT_CONFIG_ARGUMENT      = "--elastic-port";
	private static final String ELASTIC_CLUSTER_CONFIG_ARGUMENT   = "--elastic-cluster";
	
	//
	private static final String ES_COLLECTION_DAYS_TO_KEEP_ARGUMENT = "--collection-days-to-keep"; 
		

	private static String  elasticHosts         = "";
	private static Integer elasticPort          = 9300;
	private static String  elasticCluster       = "ecs-analytics";
	private static String  cleanData            = ECS_CLEAN_ALL_DATA;
	private static Integer collectionDaysToKeep = 7;
	
	private final static Logger       logger             = LoggerFactory.getLogger(ElasticSearchCleaner.class);
	
	private static AtomicLong         objectCount        = new AtomicLong(0L);
	
	public static void main(String[] args) throws Exception {

		String menuString = "Usage: ElasticSearchCleaner " +
								"[" + ELASTIC_HOSTS_CONFIG_ARGUMENT + " <host1,host2>] - Specify a list of Elasticsearch hosts \n" +
								"[" + ELASTIC_PORT_CONFIG_ARGUMENT + "<elastic-port>] - Specify a non default ElasticSearch port {Default: 9300} \n" +
								"[" + ELASTIC_CLUSTER_CONFIG_ARGUMENT + "<elastic-cluster>] = Specify a specific ElasticSearch cluster name {Default: ecs-analytics}" +
				                "[" + ECS_CLEAN_DATA_CONFIG_ARGUMENT + " <" + 
										ECS_CLEAN_BILLING_DATA + "|" + 
										ECS_CLEAN_BUCKET_DATA + "| \n" +
										ECS_CLEAN_OBJECT_DATA + "|" +
										ECS_CLEAN_OBJECT_VERSION_DATA + "| \n" +
										ECS_CLEAN_ALL_DATA +">] - Specify which ElasticSearch index to clean \n" +
								"[" + ES_COLLECTION_DAYS_TO_KEEP_ARGUMENT + "<number0of-days-to-keep-in-es> - " + 
										"Specify how many days of data to keep in ElasticSearch {Default: 7 (days)}"; 

		
		if ( args.length > 0 && args[0].contains("--help")) {
			System.err.println (menuString);
			System.exit(0);
		} else {

			int i = 0;
			String arg;
	
			while (i < args.length && args[i].startsWith("--")) {
				arg = args[i++];

				if (arg.equals(ECS_CLEAN_DATA_CONFIG_ARGUMENT)) {
					if (i < args.length) {
						cleanData = args[i++];
					} else {
						System.err.println(ECS_CLEAN_DATA_CONFIG_ARGUMENT + " requires a collect data value");
						System.exit(0);
					}
				} else if (arg.contains(ELASTIC_HOSTS_CONFIG_ARGUMENT)) {
					if (i < args.length) {
						elasticHosts = args[i++];
					} else {
						System.err.println(ELASTIC_HOSTS_CONFIG_ARGUMENT + " requires hosts value(s)");
						System.exit(0);
					}
				} else if (arg.equals(ELASTIC_PORT_CONFIG_ARGUMENT)) {
					if (i < args.length) {
						elasticPort = Integer.valueOf(args[i++]);
					} else {
						System.err.println(ELASTIC_PORT_CONFIG_ARGUMENT + " requires a port value");
						System.exit(0);
					}
				} else if (arg.equals(ELASTIC_CLUSTER_CONFIG_ARGUMENT)) {
					if (i < args.length) {
						elasticCluster = args[i++];
					} else {
						System.err.println( ELASTIC_CLUSTER_CONFIG_ARGUMENT + " requires a cluster value");
						System.exit(0);
					}
				} else if (arg.equals(ES_COLLECTION_DAYS_TO_KEEP_ARGUMENT)) {
					if (i < args.length) {
						collectionDaysToKeep = Integer.valueOf(args[i++]);
					} else {
						System.err.println(ES_COLLECTION_DAYS_TO_KEEP_ARGUMENT + " requires a day shift value port value");
						System.exit(0);
					}
				} else {
					System.err.println(menuString);
					System.exit(0);
				} 
			}                
		}
		
		// object hosts
		if(elasticHosts.isEmpty()) {
			System.err.println("Missing elastic hosts use " + ELASTIC_HOSTS_CONFIG_ARGUMENT +
								"<host1,host2> to specify a value" );
		}
		
		
		// grab current to timestamp in order
		// to go back in time to prune old data out
		Date objectCollectionStart = new Date(System.currentTimeMillis());
		Long epochTime = objectCollectionStart.getTime();
		Long daysShift = TimeUnit.DAYS.toMillis(collectionDaysToKeep);
		Date thresholdDate = new Date(epochTime - daysShift);
		
		
		if(cleanData.equals(ECS_CLEAN_BILLING_DATA) ){
			
			// collect billing data
			cleanBillingData(thresholdDate);
			
		} else if (cleanData.equals(ECS_CLEAN_BUCKET_DATA)) {
			
			// collect object bucket info
			cleanObjectBucketData(thresholdDate);
			
		} else if(cleanData.equals(ECS_CLEAN_OBJECT_DATA)) {
			
			// collect object data
			cleanObjectData(thresholdDate);
			
		} else if(cleanData.equals(ECS_CLEAN_OBJECT_VERSION_DATA)) {
			
			// collect object data
			cleanObjectVersionData(thresholdDate);
			
		} else if(cleanData.equals(ECS_CLEAN_ALL_DATA)) {
			
			// collect object bucket info
			cleanObjectBucketData(thresholdDate);
			
			// collect billing data 
			cleanBillingData(thresholdDate);

			// collect object data
			cleanObjectData(thresholdDate);
			
			// collect object version data
			cleanObjectVersionData(thresholdDate);
		} else {		
			System.err.println("Unsupported data collection action: " + cleanData );
			System.err.println(menuString);
			System.exit(0);
		}
		
		
		Long objectCollectionFinish = System.currentTimeMillis();
		Double deltaTime = Double.valueOf((objectCollectionFinish - epochTime)) / 1000 ;
		logger.info("Deleted " + objectCount.get() + " objects");
		logger.info("Total deletion time: " + deltaTime + " seconds");
		
	}

	private static void cleanBillingData(Date thresholdDate) {
			
		// Instantiate file DAO
		ElasticDAOConfig daoConfig = new ElasticDAOConfig();
		daoConfig.setHosts(Arrays.asList(elasticHosts.split(",")));
		daoConfig.setPort(elasticPort);
		daoConfig.setClusterName(elasticCluster);
		BillingDAO billingDAO = new ElasticBillingDAO(daoConfig);	

		billingDAO.purgeOldData(ManagementDataType.billing_namespace, thresholdDate);
		billingDAO.purgeOldData(ManagementDataType.billing_bucket, thresholdDate);
	}
	
	private static void cleanObjectBucketData(Date thresholdDate) {
		
			
		// Instantiate file DAO
		ElasticDAOConfig daoConfig = new ElasticDAOConfig();
		daoConfig.setHosts(Arrays.asList(elasticHosts.split(",")));
		daoConfig.setPort(elasticPort);
		daoConfig.setClusterName(elasticCluster);
		BillingDAO billingDAO = new ElasticBillingDAO(daoConfig);
		
		billingDAO.purgeOldData(ManagementDataType.object_bucket, thresholdDate);

	}
	
	private static void cleanObjectData(Date thresholdDate) {
		
		// Instantiate ElasticSearch DAO
		ElasticDAOConfig daoConfig = new ElasticDAOConfig();
		daoConfig.setHosts(Arrays.asList(elasticHosts.split(",")));
		daoConfig.setPort(elasticPort);
		daoConfig.setClusterName(elasticCluster);
		ObjectDAO objectDAO = new ElasticS3ObjectDAO(daoConfig);
		
		objectDAO.purgeOldData(ObjectDataType.object, thresholdDate);

	}
	
	
	private static void cleanObjectVersionData(Date thresholdDate) {
			
		// Instantiate ElasticSearch DAO
		ElasticDAOConfig daoConfig = new ElasticDAOConfig();
		daoConfig.setHosts(Arrays.asList(elasticHosts.split(",")));
		daoConfig.setPort(elasticPort);
		daoConfig.setClusterName(elasticCluster);
		ObjectDAO objectDAO = new ElasticS3ObjectDAO(daoConfig);
		
		objectDAO.purgeOldData(ObjectDataType.object_versions, thresholdDate);
	}
	
}