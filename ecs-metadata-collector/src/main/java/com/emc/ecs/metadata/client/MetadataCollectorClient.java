package com.emc.ecs.metadata.client;


import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emc.ecs.metadata.bo.BillingBO;
import com.emc.ecs.metadata.bo.ObjectBO;
import com.emc.ecs.metadata.dao.BillingDAO;
import com.emc.ecs.metadata.dao.ObjectDAO;
import com.emc.ecs.metadata.dao.elasticsearch.ElasticBillingDAO;
import com.emc.ecs.metadata.dao.elasticsearch.ElasticDAOConfig;
import com.emc.ecs.metadata.dao.elasticsearch.ElasticS3ObjectDAO;
import com.emc.ecs.metadata.dao.file.FileBillingDAO;
import com.emc.ecs.metadata.dao.file.FileObjectDAO;


/**
 * ECS S3 client to collect Metadata from various ECS systems 
 * Created by Eric Caron
 */
public class MetadataCollectorClient {
	
	
	private static final Integer DEFAULT_ECS_MGMT_PORT = 4443;
	private static final String  ECS_COLLECT_BILLING_DATA = "billing";
	private static final String  ECS_COLLECT_BUCKET_DATA = "bucket";
	private static final String  ECS_COLLECT_OBJECT_DATA = "object";
	private static final String  ECS_COLLECT_OBJECT_VERSION_DATA = "object-version";
	private static final String  ECS_COLLECT_ALL_DATA = "all";
	
	private static final String ECS_HOSTS_CONFIG_ARGUMENT         = "--ecs-hosts";
	private static final String ECS_ACCESS_KEY_CONFIG_ARGUMENT    = "--ecs-access-key";
	private static final String ECS_SECRET_KEY_CONFIG_ARGUMENT    = "--ecs-secret-key";
	private static final String ECS_OBJECT_HOSTS_CONFIG_ARGUMENT  = "--ecs-object-hosts";
	private static final String ECS_MGMT_PORT_CONFIG_ARGUMENT     = "--ecs-mgmt-port";
	private static final String ECS_COLLECT_DATA_CONFIG_ARGUMENT  = "--collect-data";
	private static final String ELASTIC_HOSTS_CONFIG_ARGUMENT     = "--elastic-hosts";
	private static final String ELASTIC_PORT_CONFIG_ARGUMENT      = "--elastic-port";
	private static final String ELASTIC_CLUSTER_CONFIG_ARGUMENT   = "--elastic-cluster";
	
	// secret argument to test various collection time
	// specific x number of days before current day
	private static final String ECS_COLLECTION_DAY_SHIFT_ARGUMENT = "--collection-day-shift"; 
		
	private static String  ecsHosts         = "";
	private static String  ecsMgmtAccessKey = "";
	private static String  ecsMgmtSecretKey = "";
	private static String  elasticHosts     = "";
	private static Integer elasticPort      = 9300;
	private static String  elasticCluster   = "ecs-analytics";
	private static String  ecsObjectHosts   = "";
	private static Integer ecsMgmtPort      = DEFAULT_ECS_MGMT_PORT;
	private static String  collectData      = ECS_COLLECT_ALL_DATA;
	private static Integer relativeDayShift = 0;
	
	final static Logger logger = LoggerFactory.getLogger(MetadataCollectorClient.class);
	
	public static void main(String[] args) throws Exception {

		String menuString = "Usage: MetadataCollector [" + ECS_HOSTS_CONFIG_ARGUMENT + " <host1,host2>] " + 
													"[" + ECS_ACCESS_KEY_CONFIG_ARGUMENT + " <admin-username>]" +
				                                    "[" + ECS_SECRET_KEY_CONFIG_ARGUMENT + "<admin-password>]" +
				                                    "[" + ECS_OBJECT_HOSTS_CONFIG_ARGUMENT + " <host1,host2>] " +
													"[" + ECS_MGMT_PORT_CONFIG_ARGUMENT + "<mgmt-port>]" +
													"[" + ELASTIC_HOSTS_CONFIG_ARGUMENT + " <host1,host2>] " +
													"[" + ELASTIC_PORT_CONFIG_ARGUMENT + "<elastic-port>]" +
													"[" + ELASTIC_CLUSTER_CONFIG_ARGUMENT + "<elastic-cluster>]" +
				                                    "[" + ECS_COLLECT_DATA_CONFIG_ARGUMENT + " <" + 
															ECS_COLLECT_BILLING_DATA + "|" + 
															ECS_COLLECT_BUCKET_DATA + "|" +
															ECS_COLLECT_OBJECT_DATA + "|" +
															ECS_COLLECT_OBJECT_VERSION_DATA + "|" +
															ECS_COLLECT_ALL_DATA +">] "; 

		
		if ( args.length > 0 && args[0].contains("--help")) {
			System.err.println (menuString);
			System.err.println("Example queue name are: *");
			System.exit(0);
		} else {

			int i = 0;
			String arg;
	
			while (i < args.length && args[i].startsWith("--")) {
				arg = args[i++];

				if (arg.contains(ECS_HOSTS_CONFIG_ARGUMENT)) {
					if (i < args.length) {
						ecsHosts = args[i++];
					} else {
						System.err.println(ECS_HOSTS_CONFIG_ARGUMENT + " requires hosts value(s)");
						System.exit(0);
					}
				} else if (arg.contains(ECS_ACCESS_KEY_CONFIG_ARGUMENT)) {
					if (i < args.length) {
						ecsMgmtAccessKey = args[i++];
					} else {
						System.err.println(ECS_ACCESS_KEY_CONFIG_ARGUMENT + " requires an access-key value");
						System.exit(0);
					}
				}  else if (arg.equals(ECS_SECRET_KEY_CONFIG_ARGUMENT)) {
					if (i < args.length) {
						ecsMgmtSecretKey = args[i++];
					} else {
						System.err.println(ECS_SECRET_KEY_CONFIG_ARGUMENT + " requires a secret-key value");
						System.exit(0);
					}
				} else if (arg.contains(ECS_OBJECT_HOSTS_CONFIG_ARGUMENT)) {
					if (i < args.length) {
						ecsObjectHosts = args[i++];
					} else {
						System.err.println(ECS_OBJECT_HOSTS_CONFIG_ARGUMENT + " requires hosts value(s)");
						System.exit(0);
					}
				} else if (arg.equals(ECS_MGMT_PORT_CONFIG_ARGUMENT)) {
					if (i < args.length) {
						ecsMgmtPort = Integer.valueOf(args[i++]);
					} else {
						System.err.println(ECS_MGMT_PORT_CONFIG_ARGUMENT + " requires a mgmt port value");
						System.exit(0);
					}
				} else if (arg.equals(ECS_COLLECT_DATA_CONFIG_ARGUMENT)) {
					if (i < args.length) {
						collectData = args[i++];
					} else {
						System.err.println(ECS_COLLECT_DATA_CONFIG_ARGUMENT + " requires a collect data value");
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
				} else if (arg.equals(ECS_COLLECTION_DAY_SHIFT_ARGUMENT)) {
					if (i < args.length) {
						relativeDayShift = Integer.valueOf(args[i++]);
					} else {
						System.err.println(ECS_COLLECTION_DAY_SHIFT_ARGUMENT + " requires a day shift value port value");
						System.exit(0);
					}
				} else {
					System.err.println(menuString);
					System.exit(0);
				} 
			}                
		}
		
		// Check hosts
		if(ecsHosts.isEmpty()) {	
			System.err.println("Missing ECS hostname use " + ECS_HOSTS_CONFIG_ARGUMENT + 
					           "<host1, host2> to specify a value" );
		}
		
		// access/user key
		if(ecsMgmtAccessKey.isEmpty()) {
			System.err.println("Missing managment access key use" + ECS_ACCESS_KEY_CONFIG_ARGUMENT +
								"<username> to specify a value" );
			return;
		}

		// access/user key
		if(ecsMgmtAccessKey.isEmpty()) {
			System.err.println("Missing access key use " + ECS_ACCESS_KEY_CONFIG_ARGUMENT +
								"<admin-password> to specify a value" );
			return;
		}
		
		// grab current to timestamp in order
		// to label collected data with time
		Date collectionTime = new Date(System.currentTimeMillis());
		
		// check if secret day shifting testing option was specified
		if( relativeDayShift != 0 ) {
			Long epochTime = collectionTime.getTime();
			Long daysShift = TimeUnit.DAYS.toMillis(relativeDayShift);
			collectionTime = new Date(epochTime - daysShift);
		}	
		
		if(collectData.equals(ECS_COLLECT_BILLING_DATA) ){
			// collect billing data
			collectBillingData(collectionTime);
		} else if (collectData.equals(ECS_COLLECT_BUCKET_DATA)) {
			// collect object bucket info
			collectObjectBucketData(collectionTime);
		} else if(collectData.equals(ECS_COLLECT_OBJECT_DATA)) {
			
			// object hosts
			if(ecsObjectHosts.isEmpty()) {
				System.err.println("Missing object hosts use " + ECS_OBJECT_HOSTS_CONFIG_ARGUMENT +
									"<host1,host2> to specify a value" );
			}
			
			// collect object data
			collectObjectData(collectionTime);
		} else if(collectData.equals(ECS_COLLECT_OBJECT_VERSION_DATA)) {
			
			// object hosts
			if(ecsObjectHosts.isEmpty()) {
				System.err.println("Missing object hosts use " + ECS_OBJECT_HOSTS_CONFIG_ARGUMENT +
									"<host1,host2> to specify a value" );
			}
			
			// collect object data
			collectObjectVersionData(collectionTime);
		} else if(collectData.equals(ECS_COLLECT_ALL_DATA)) {
			
			// object hosts
			if(ecsObjectHosts.isEmpty()) {
				System.err.println("Missing object hosts use " + ECS_OBJECT_HOSTS_CONFIG_ARGUMENT +
									"<host1,host2> to specify a value" );
			}
			
			// collect object bucket info
			collectObjectBucketData(collectionTime);
			
			// collect billing data 
			collectBillingData(collectionTime);

			// collect object data
			collectObjectData(collectionTime);
			
			// collect object version data
			collectObjectVersionData(collectionTime);
		} else {		
			System.err.println("Unsupported data collection action: " + collectData );
			System.err.println(menuString);
			System.exit(0);
		}
		
	}

	private static void collectBillingData(Date collectionTime) {
		
		BillingDAO billingDAO = null;
		
		if(!elasticHosts.isEmpty()) {
			
			// Instantiate file DAO
			ElasticDAOConfig daoConfig = new ElasticDAOConfig();
			daoConfig.setHosts(Arrays.asList(elasticHosts.split(",")));
			daoConfig.setPort(elasticPort);
			daoConfig.setClusterName(elasticCluster);
			billingDAO = new ElasticBillingDAO(daoConfig);
		} else {
			// Instantiate file DAO
			billingDAO = new FileBillingDAO(null);
		}
		
		// instantiate billing BO
		BillingBO billingBO = new BillingBO( ecsMgmtAccessKey, 
											 ecsMgmtSecretKey,
											 Arrays.asList(ecsHosts.split(",")),
											 ecsMgmtPort,
											 billingDAO );
		
		// Start collection
		billingBO.collectBillingData(collectionTime);
		
		billingBO.shutdown();
	}
	
	private static void collectObjectBucketData(Date collectionTime) {
		
		BillingDAO billingDAO = null;
		
		if(!elasticHosts.isEmpty()) {
			
			// Instantiate file DAO
			ElasticDAOConfig daoConfig = new ElasticDAOConfig();
			daoConfig.setHosts(Arrays.asList(elasticHosts.split(",")));
			daoConfig.setPort(elasticPort);
			daoConfig.setClusterName(elasticCluster);
			billingDAO = new ElasticBillingDAO(daoConfig);
		} else {
			// Instantiate file DAO
			billingDAO = new FileBillingDAO(null);
		}
		
		// instantiate billing BO
		BillingBO billingBO = new BillingBO( ecsMgmtAccessKey, 
											 ecsMgmtSecretKey,
											 Arrays.asList(ecsHosts.split(",")),
											 ecsMgmtPort,
											 billingDAO );
		
		
		
		// Start collection
		billingBO.collectObjectBukcetData(collectionTime);
		
		billingBO.shutdown();
	}
	
	private static void collectObjectData(Date collectionTime) {
		
		List<String> hosts = Arrays.asList(ecsHosts.split(","));
		List<String> objectHosts = Arrays.asList(ecsObjectHosts.split(","));
		
		// instantiate billing BO
		BillingBO billingBO = new BillingBO( ecsMgmtAccessKey, 
											 ecsMgmtSecretKey,
											 hosts,
											 ecsMgmtPort,
											 null );  // dao is not required in this case
		
		// Instantiate DAO
		ObjectDAO objectDAO = null;
		if(!elasticHosts.isEmpty()) {
			
			// Instantiate ElasticSearch DAO
			ElasticDAOConfig daoConfig = new ElasticDAOConfig();
			daoConfig.setHosts(Arrays.asList(elasticHosts.split(",")));
			daoConfig.setPort(elasticPort);
			daoConfig.setClusterName(elasticCluster);
			objectDAO = new ElasticS3ObjectDAO(daoConfig);
		} else {
			// Instantiate file DAO
			objectDAO = new FileObjectDAO();
		}
		
		
		ObjectBO objectBO = new ObjectBO(billingBO, objectHosts, objectDAO );
		
		// Start collection
		objectBO.collectObjectData(collectionTime);
		
		objectBO.shutdown();
	}
	
	
	private static void collectObjectVersionData(Date collectionTime) {
		
		List<String> hosts = Arrays.asList(ecsHosts.split(","));
		List<String> objectHosts = Arrays.asList(ecsObjectHosts.split(","));
		
		// instantiate billing BO
		BillingBO billingBO = new BillingBO( ecsMgmtAccessKey, 
											 ecsMgmtSecretKey,
											 hosts,
											 ecsMgmtPort,
											 null );  // dao is not required in this case
		
		// Instantiate DAO
		ObjectDAO objectDAO = null;
		if(!elasticHosts.isEmpty()) {
			
			// Instantiate ElasticSearch DAO
			ElasticDAOConfig daoConfig = new ElasticDAOConfig();
			daoConfig.setHosts(Arrays.asList(elasticHosts.split(",")));
			daoConfig.setPort(elasticPort);
			daoConfig.setClusterName(elasticCluster);
			objectDAO = new ElasticS3ObjectDAO(daoConfig);
		} else {
			// Instantiate file DAO
			objectDAO = new FileObjectDAO();
		}
		
		
		ObjectBO objectBO = new ObjectBO(billingBO, objectHosts, objectDAO );
		
		// Start collection
		objectBO.collectObjectVersionData(collectionTime);
		
		objectBO.shutdown();
	}
	
}