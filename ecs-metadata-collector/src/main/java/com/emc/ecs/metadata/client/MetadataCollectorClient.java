

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


package com.emc.ecs.metadata.client;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emc.ecs.metadata.bo.BillingBO;
import com.emc.ecs.metadata.bo.NamespaceBO;
import com.emc.ecs.metadata.bo.ObjectBO;
import com.emc.ecs.metadata.bo.VdcBO;
import com.emc.ecs.metadata.dao.BillingDAO;
import com.emc.ecs.metadata.dao.EcsCollectionType;
import com.emc.ecs.metadata.dao.NamespaceDAO;
import com.emc.ecs.metadata.dao.ObjectDAO;
import com.emc.ecs.metadata.dao.VdcDAO;
import com.emc.ecs.metadata.dao.elasticsearch.ElasticBillingDAO;
import com.emc.ecs.metadata.dao.elasticsearch.ElasticDAOConfig;
import com.emc.ecs.metadata.dao.elasticsearch.ElasticNamespaceDAO;
import com.emc.ecs.metadata.dao.elasticsearch.ElasticS3ObjectDAO;
import com.emc.ecs.metadata.dao.elasticsearch.ElasticVdcDAO;
import com.emc.ecs.metadata.dao.file.FileBillingDAO;
import com.emc.ecs.metadata.dao.file.FileNamespaceDAO;
import com.emc.ecs.metadata.dao.file.FileObjectDAO;
import com.emc.ecs.metadata.dao.file.FileVdcDAO;


/**
 * ECS S3 client to collect Metadata from various ECS systems 
 * 
 */
public class MetadataCollectorClient {
	
	
	private static final Integer DEFAULT_ECS_MGMT_PORT = 4443;
	private static final Integer DEFAULT_ECS_ALTERNATIVE_MGMT_PORT = 9101;
	private static final String  ECS_COLLECT_BILLING_DATA = "billing";
	private static final String  ECS_COLLECT_OBJECT_DATA = "object";
	private static final String  ECS_COLLECT_OBJECT_VERSION_DATA = "object-version";
	private static final String  ECS_COLLECT_NAMESPACE_DETAIL = "namespace-detail";
	private static final String  ECS_COLLECT_NAMESPACE_QUOTA = "namespace-quota";
	private static final String  ECS_COLLECT_ALL_VDC = "vdc";
	private static final String  ECS_COLLECT_BUCKET_OWNER = "bucket-owner";
	private static final String  ECS_COLLECT_ALL_DATA = "all";
	
	private static final String ECS_HOSTS_CONFIG_ARGUMENT                    = "--ecs-hosts";
	private static final String ECS_MGMT_ACCESS_KEY_CONFIG_ARGUMENT          = "--ecs-mgmt-access-key";
	private static final String ECS_MGMT_SECRET_KEY_CONFIG_ARGUMENT          = "--ecs-mgmt-secret-key";
	private static final String ECS_MGMT_PORT_CONFIG_ARGUMENT                = "--ecs-mgmt-port";
	private static final String ECS_COLLECT_DATA_CONFIG_ARGUMENT             = "--collect-data";
	private static final String ECS_COLLECT_MODIFIED_OBJECT_CONFIG_ARGUMENT  = "--collect-only-modified-objects";
	private static final String ECS_INIT_INDEXES_ONLY_CONFIG_ARGUMENT        = "--init-indexes-only";
	
	private static final String ELASTIC_HOSTS_CONFIG_ARGUMENT                = "--elastic-hosts";
	private static final String ELASTIC_PORT_CONFIG_ARGUMENT                 = "--elastic-port";
	private static final String ELASTIC_CLUSTER_CONFIG_ARGUMENT              = "--elastic-cluster";
	
	
	private static final String ECS_OBJECT_LAST_MODIFIED_MD_KEY  = "LastModified";
	
	// secret argument to test various collection time
	// specific x number of days before current day
	private static final String ECS_COLLECTION_DAY_SHIFT_ARGUMENT = "--collection-day-shift"; 
		
	
	private static final String            DATA_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	private static final SimpleDateFormat  DATA_DATE_FORMAT = new  SimpleDateFormat(DATA_DATE_PATTERN);
	
	private static final String menuString = "Usage: MetadataCollector [" + ECS_HOSTS_CONFIG_ARGUMENT + " <host1,host2>] " + 
			"[" + ECS_MGMT_ACCESS_KEY_CONFIG_ARGUMENT + " <admin-username>]" +
			"[" + ECS_MGMT_SECRET_KEY_CONFIG_ARGUMENT + "<admin-password>]" +
			"[" + ECS_MGMT_PORT_CONFIG_ARGUMENT + "<mgmt-port {default: 4443}>]" +
			"[" + ELASTIC_HOSTS_CONFIG_ARGUMENT + " <host1,host2>] " +
			"[" + ELASTIC_PORT_CONFIG_ARGUMENT + "<elastic-port {default: 9300}>]" +
			"[" + ELASTIC_CLUSTER_CONFIG_ARGUMENT + "<elastic-cluster>]" +
			"[" + ECS_INIT_INDEXES_ONLY_CONFIG_ARGUMENT + "]" +
			"[" + ECS_COLLECT_MODIFIED_OBJECT_CONFIG_ARGUMENT + "<number of days>" + " | " +
			ECS_COLLECT_DATA_CONFIG_ARGUMENT + " <" + 
			ECS_COLLECT_BILLING_DATA + "|" +
			ECS_COLLECT_OBJECT_DATA + "|" +
			ECS_COLLECT_OBJECT_VERSION_DATA + "|" +
			ECS_COLLECT_NAMESPACE_DETAIL + "|" +
			ECS_COLLECT_NAMESPACE_QUOTA + "|" +
			ECS_COLLECT_ALL_VDC + "|" +
			ECS_COLLECT_BUCKET_OWNER        + "|" +
			ECS_COLLECT_ALL_DATA +">] "; 
	
	private static String  ecsHosts                          = "";
	private static String  ecsMgmtAccessKey                  = "";
	private static String  ecsMgmtSecretKey                  = "";
	private static String  elasticHosts                      = "";
	private static Integer elasticPort                       = 9300;
	private static String  elasticCluster                    = "ecs-analytics";
	private static Integer ecsMgmtPort                       = DEFAULT_ECS_MGMT_PORT;
	private static String  collectData                       = ECS_COLLECT_ALL_DATA;
	private static Integer relativeDayShift                  = 0;
	private static Integer objectModifiedSinceNoOfDays       = 0;
	private static boolean relativeObjectModifiedSinceOption = false;
	private static boolean initIndexesOnlyOption             = false;
	private static Integer ecsAlternativeMgmtPort			 = DEFAULT_ECS_ALTERNATIVE_MGMT_PORT;
	
	
	private final static Logger       logger             = LoggerFactory.getLogger(MetadataCollectorClient.class);
	
	// Thread pool 
	private static ThreadPoolExecutor threadPoolExecutor = 
			(ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	private static Queue<Future<?>>   futures            = new ConcurrentLinkedQueue<Future<?>>();
	private static AtomicLong         objectCount        = new AtomicLong(0L);
	
	public static void main(String[] args) throws Exception {

		// handle passed in arguments
		handleArguments(args);
		
		// grab current to timestamp in order
		// to label collected data with time
		Date collectionTime = new Date(System.currentTimeMillis());
		
		if(initIndexesOnlyOption) {
			initIndexesOnly(collectionTime);
			// no need to go further
			return;
		}
		
		
		if(relativeObjectModifiedSinceOption) {
			// collect object data
			collectObjectDataModifiedSinceDate(collectionTime, objectModifiedSinceNoOfDays);
		} else {
			// check if secret day shifting testing option was specified
			if( relativeDayShift != 0 ) {
				Long epochTime = collectionTime.getTime();
				Long daysShift = TimeUnit.DAYS.toMillis(relativeDayShift);
				collectionTime = new Date(epochTime - daysShift);
			}	
			
			if(collectData.equals(ECS_COLLECT_BILLING_DATA) ){
				// collect billing data
				collectBillingData(collectionTime);
			} 
			else if(collectData.equals(ECS_COLLECT_OBJECT_DATA) ) {

				// only collection all object if the modified since option has been specified
				if(!relativeObjectModifiedSinceOption){
					// collect object data
					collectObjectData(collectionTime);
				}
			} else if(collectData.equals(ECS_COLLECT_OBJECT_VERSION_DATA)) {

				// collect object data
				collectObjectVersionData(collectionTime);
			} else if(collectData.equals(ECS_COLLECT_ALL_DATA)) {

				// collect billing data 
				collectBillingData(collectionTime);

				// only collection all object if the modified since option has not been specified
				if(!relativeObjectModifiedSinceOption) {
					// collect object data
					collectObjectData(collectionTime);
				}
				collectNamespaceDetails(collectionTime);
				collectNamespaceQuota(collectionTime);
			} else if(collectData.equals(ECS_COLLECT_NAMESPACE_DETAIL)) {
				// collect namespace details
				collectNamespaceDetails(collectionTime);
			} else if(collectData.equals(ECS_COLLECT_NAMESPACE_QUOTA)) {
				// collect namespace quota
				collectNamespaceQuota(collectionTime);
			}  else if(collectData.equals(ECS_COLLECT_BUCKET_OWNER)) {
				// collect bucket owner
				collectBucketOwnership(collectionTime);
			}  else if(collectData.equals(ECS_COLLECT_ALL_VDC)) {
				// collect vdc list
				collectVdcList(collectionTime);
			} else {		
				System.err.println("Unsupported data collection action: " + collectData );
				System.err.println(menuString);
				System.exit(0);
			}
		}
		
		
		// wait for all threads to complete their work
		while ( !futures.isEmpty() ) {
		    try {
				Future<?> future = futures.poll();
				if(future != null){
					future.get();
				}
			} catch (InterruptedException e) {
				logger.error(e.getLocalizedMessage());
			} catch (ExecutionException e) {
				logger.error(e.getLocalizedMessage());
			}
		}
		
		Long objectCollectionFinish = System.currentTimeMillis();
		Double deltaTime = Double.valueOf((objectCollectionFinish - collectionTime.getTime())) / 1000 ;
		logger.info("Collected " + objectCount.get() + " objects");
		logger.info("Total collection time: " + deltaTime + " seconds");
		
		// take everything down once all threads have completed their work
		threadPoolExecutor.shutdown();
		
		// wait for all threads to terminate
		boolean termination = false; 
		do {
			try {
				termination = threadPoolExecutor.awaitTermination(2, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				logger.error(e.getLocalizedMessage());
				termination = true;
			}
		} while(!termination);
		
		
	}

	/**
	 * Handles passed in arguments
	 * @param args
	 */
	private static void handleArguments(String[] args) {
		
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
				} else if (arg.contains(ECS_MGMT_ACCESS_KEY_CONFIG_ARGUMENT)) {
					if (i < args.length) {
						ecsMgmtAccessKey = args[i++];
					} else {
						System.err.println(ECS_MGMT_ACCESS_KEY_CONFIG_ARGUMENT + " requires an access-key value");
						System.exit(0);
					}
				}  else if (arg.equals(ECS_MGMT_SECRET_KEY_CONFIG_ARGUMENT)) {
					if (i < args.length) {
						ecsMgmtSecretKey = args[i++];
					} else {
						System.err.println(ECS_MGMT_SECRET_KEY_CONFIG_ARGUMENT + " requires a secret-key value");
						System.exit(0);
					}
				} else if (arg.equals(ECS_MGMT_PORT_CONFIG_ARGUMENT)) {
					if (i < args.length) {
						ecsMgmtPort = Integer.valueOf(args[i++]);
					} else {
						System.err.println(ECS_MGMT_PORT_CONFIG_ARGUMENT + " requires a mgmt port value");
						System.exit(0);
					}
				} else if (arg.equals(ECS_COLLECT_MODIFIED_OBJECT_CONFIG_ARGUMENT)) {
					if (i < args.length) {
						relativeObjectModifiedSinceOption = true;
						objectModifiedSinceNoOfDays       = Integer.valueOf(args[i++]);
					} else {
						System.err.println(ECS_COLLECT_MODIFIED_OBJECT_CONFIG_ARGUMENT + " requires a specified number of days value");
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
				} else if (arg.equals( ECS_INIT_INDEXES_ONLY_CONFIG_ARGUMENT)) { 
					initIndexesOnlyOption = true;
				} else {
					System.err.println("Unreconized option: " + arg); 
					System.err.println(menuString);
					System.exit(0);
				} 
			}                
		}

		if(initIndexesOnlyOption) {
			// Check hosts
			if(elasticHosts.isEmpty()) {	
				System.err.println("Missing Elastic hostname use " + ELASTIC_HOSTS_CONFIG_ARGUMENT + 
								"<host1, host2> to specify a value" );
				return;
			}	
		} else {

			// Check hosts
			if(ecsHosts.isEmpty()) {	
				System.err.println("Missing ECS hostname use " + ECS_HOSTS_CONFIG_ARGUMENT + 
						"<host1, host2> to specify a value" );
				return;
			}

			// management access/user key
			if(ecsMgmtAccessKey.isEmpty()) {
				System.err.println("Missing managment access key use" + ECS_MGMT_ACCESS_KEY_CONFIG_ARGUMENT +
						"<admin-username> to specify a value" );
				return;
			}

			// management access/user key
			if(ecsMgmtSecretKey.isEmpty()) {
				System.err.println("Missing management secret key use " + ECS_MGMT_SECRET_KEY_CONFIG_ARGUMENT +
						"<admin-password> to specify a value" );
				return;
			}
		}
	}
	
	/**
	 * Collects Billing data
	 * 
	 * @param collectionTime
	 */
	private static void collectBillingData(Date collectionTime) {
		
		BillingDAO billingDAO = null;
		
		if(!elasticHosts.isEmpty()) {
			
			// Instantiate file DAO
			ElasticDAOConfig daoConfig = new ElasticDAOConfig();
			daoConfig.setHosts(Arrays.asList(elasticHosts.split(",")));
			daoConfig.setPort(elasticPort);
			daoConfig.setClusterName(elasticCluster);
			daoConfig.setCollectionTime(collectionTime);
			billingDAO = new ElasticBillingDAO(daoConfig);
			
			// init indexes
			billingDAO.initIndexes(collectionTime);
		} else {
			// Instantiate file DAO
			billingDAO = new FileBillingDAO(null);
		}
		
		// instantiate billing BO
		BillingBO billingBO = new BillingBO( ecsMgmtAccessKey, 
											 ecsMgmtSecretKey,
											 Arrays.asList(ecsHosts.split(",")),
											 ecsMgmtPort,
											 billingDAO,
											 objectCount );
		
		// Start collection
		billingBO.collectBillingData(collectionTime);
		
		billingBO.shutdown();
	}
	
	
	/**
	 * Collects object data
	 * 
	 * @param collectionTime
	 */
	private static void collectObjectData(Date collectionTime) {
		
		List<String> hosts = Arrays.asList(ecsHosts.split(","));
		
		// instantiate billing BO
		BillingBO billingBO = new BillingBO( ecsMgmtAccessKey, 
											 ecsMgmtSecretKey,
											 hosts,
											 ecsMgmtPort,
											 null,        // dao is not required in this case
											 objectCount ); 
		
		// Instantiate DAO
		ObjectDAO objectDAO = null;
		if(!elasticHosts.isEmpty()) {
			
			// Instantiate ElasticSearch DAO
			ElasticDAOConfig daoConfig = new ElasticDAOConfig();
			daoConfig.setHosts(Arrays.asList(elasticHosts.split(",")));
			daoConfig.setPort(elasticPort);
			daoConfig.setClusterName(elasticCluster);
			daoConfig.setCollectionTime(collectionTime);
			daoConfig.setCollectionType(EcsCollectionType.object);
			objectDAO = new ElasticS3ObjectDAO(daoConfig);
			
			// init indexes
		    objectDAO.initIndexes(collectionTime);
				
		} else {
			// Instantiate file DAO
			objectDAO = new FileObjectDAO();
		}	
		
		ObjectBO objectBO = new ObjectBO(billingBO, hosts, objectDAO, threadPoolExecutor, futures, objectCount );
		
		// Start collection
		objectBO.collectObjectData(collectionTime);
		
		objectBO.shutdown();
	}
	
	/**
	 * Collect only objects modified since a certain date
	 * 
	 * @param collectionTime
	 * @param numberOfDays
	 */
	private static void collectObjectDataModifiedSinceDate(Date collectionTime, Integer numberOfDays) {
		
		List<String> hosts = Arrays.asList(ecsHosts.split(","));
		
		
		// instantiate billing BO
		BillingBO billingBO = new BillingBO( ecsMgmtAccessKey, 
											 ecsMgmtSecretKey,
											 hosts,
											 ecsMgmtPort,
											 null,         // dao is not required in this case
											 objectCount );  
		
		// Instantiate DAO
		ObjectDAO objectDAO = null;
		if(!elasticHosts.isEmpty()) {
			
			// Instantiate ElasticSearch DAO
			ElasticDAOConfig daoConfig = new ElasticDAOConfig();
			daoConfig.setHosts(Arrays.asList(elasticHosts.split(",")));
			daoConfig.setPort(elasticPort);
			daoConfig.setClusterName(elasticCluster);
			daoConfig.setCollectionTime(collectionTime);
			daoConfig.setCollectionType(EcsCollectionType.object);
			objectDAO = new ElasticS3ObjectDAO(daoConfig);
			
			// init indexes
			objectDAO.initIndexes(collectionTime);
		} else {
			// Instantiate file DAO
			objectDAO = new FileObjectDAO();
		}
		
		
		ObjectBO objectBO = new ObjectBO(billingBO, hosts, objectDAO, threadPoolExecutor, futures, objectCount );
		
		// query criteria should look like ( LastModified >= 'since date' )
		
		Date sinceDate = new Date( (collectionTime.getTime() - (TimeUnit.MILLISECONDS.convert(numberOfDays, TimeUnit.DAYS)) ));
		
		String yesterdayDateTime = DATA_DATE_FORMAT.format( sinceDate );
		String queryCriteria = "( " + ECS_OBJECT_LAST_MODIFIED_MD_KEY + " >= '" + yesterdayDateTime + "' )";
		
		// Start collection
		objectBO.collectObjectData(collectionTime, queryCriteria);
		
		objectBO.shutdown();
	}
	
	/**
	 * Collects object version data
	 * 
	 * @param collectionTime
	 */
	private static void collectObjectVersionData(Date collectionTime) {
		
		List<String> hosts = Arrays.asList(ecsHosts.split(","));
		
		
		// instantiate billing BO
		BillingBO billingBO = new BillingBO( ecsMgmtAccessKey, 
											 ecsMgmtSecretKey,
											 hosts,
											 ecsMgmtPort,
											 null,         // dao is not required in this case
											 objectCount );
		
		// Instantiate DAO
		ObjectDAO objectDAO = null;
		if(!elasticHosts.isEmpty()) {
			
			// Instantiate ElasticSearch DAO
			ElasticDAOConfig daoConfig = new ElasticDAOConfig();
			daoConfig.setHosts(Arrays.asList(elasticHosts.split(",")));
			daoConfig.setPort(elasticPort);
			daoConfig.setClusterName(elasticCluster);
			daoConfig.setCollectionTime(collectionTime);
			daoConfig.setCollectionType(EcsCollectionType.object_version);
			objectDAO = new ElasticS3ObjectDAO(daoConfig);
			
			// init indexes
			objectDAO.initIndexes(collectionTime);
		} else {
			// Instantiate file DAO
			objectDAO = new FileObjectDAO();
		}
		
		
		ObjectBO objectBO = new ObjectBO(billingBO, hosts, objectDAO, threadPoolExecutor, futures, objectCount );
		
		//objectBO.
		
		// Start collection
		objectBO.collectObjectVersionData(collectionTime);
		
		objectBO.shutdown();
	}
	
	
	private static void initIndexesOnly(Date collectionTime) {
		
		// Instantiate Object DAO
		ObjectDAO objectDAO = null;
		BillingDAO billingDAO = null;
		
		if(!elasticHosts.isEmpty()) {
			
			// Instantiate ElasticSearch DAO
			ElasticDAOConfig daoConfig = new ElasticDAOConfig();
			daoConfig.setHosts(Arrays.asList(elasticHosts.split(",")));
			daoConfig.setPort(elasticPort);
			daoConfig.setClusterName(elasticCluster);
			daoConfig.setCollectionTime(collectionTime);
			daoConfig.setCollectionType(EcsCollectionType.object);
			objectDAO = new ElasticS3ObjectDAO(daoConfig);
			
			// init indexes
			daoConfig.setCollectionType(EcsCollectionType.object_version);
			objectDAO.initIndexes(collectionTime);
			daoConfig.setCollectionType(EcsCollectionType.object);
			objectDAO.initIndexes(collectionTime);
			
			billingDAO = new ElasticBillingDAO(daoConfig);
			// init indexes
			billingDAO.initIndexes(collectionTime);
		}
		
	}
	
	/**
	 * Collects Namespace details data
	 * 
	 * @param collectionTime
	 */
	private static void collectNamespaceDetails(Date collectionTime) {
		
		NamespaceDAO namespaceDAO = null;
		if(!elasticHosts.isEmpty()) {
			// Instantiate file DAO
			ElasticDAOConfig daoConfig = new ElasticDAOConfig();
			daoConfig.setHosts(Arrays.asList(elasticHosts.split(",")));
			daoConfig.setPort(elasticPort);
			daoConfig.setClusterName(elasticCluster);
			daoConfig.setCollectionTime(collectionTime);
			namespaceDAO = new ElasticNamespaceDAO(daoConfig);
			// init indexes
			namespaceDAO.initIndexes(collectionTime);
		} else {
			// Instantiate file DAO
			namespaceDAO = new FileNamespaceDAO(null);
		}
		
		// instantiate billing BO
		NamespaceBO namespaceBO = new NamespaceBO( ecsMgmtAccessKey, 
											 ecsMgmtSecretKey,
											 Arrays.asList(ecsHosts.split(",")),
											 ecsMgmtPort,
											 namespaceDAO,
											 objectCount );
		
		// Start collection
		namespaceBO.collectNamespaceDetails(collectionTime);
		namespaceBO.shutdown();
	}
	
	/**
	 * Collects Namespace quota data
	 * 
	 * @param collectionTime
	 */
	private static void collectNamespaceQuota(Date collectionTime) {
		NamespaceDAO namespaceDAO = null;
		if(!elasticHosts.isEmpty()) {
			// Instantiate file DAO
			ElasticDAOConfig daoConfig = new ElasticDAOConfig();
			daoConfig.setHosts(Arrays.asList(elasticHosts.split(",")));
			daoConfig.setPort(elasticPort);
			daoConfig.setClusterName(elasticCluster);
			daoConfig.setCollectionTime(collectionTime);
			namespaceDAO = new ElasticNamespaceDAO(daoConfig);
			// init indexes
			namespaceDAO.initIndexes(collectionTime);
		} else {
			// Instantiate file DAO
			namespaceDAO = new FileNamespaceDAO(null);
		}
		
		// instantiate billing BO
		NamespaceBO namespaceBO = new NamespaceBO( ecsMgmtAccessKey, 
											 ecsMgmtSecretKey,
											 Arrays.asList(ecsHosts.split(",")),
											 ecsMgmtPort,
											 namespaceDAO,
											 objectCount );
		
		// Start collection
		namespaceBO.collectNamespaceQuota(collectionTime);
		namespaceBO.shutdown();
	}
	
	private static void collectBucketOwnership(Date collectionTime) {
		VdcDAO vdcDAO = null;
		if(!elasticHosts.isEmpty()) {
			// Instantiate file DAO
			ElasticDAOConfig daoConfig = new ElasticDAOConfig();
			daoConfig.setHosts(Arrays.asList(elasticHosts.split(",")));
			daoConfig.setPort(elasticPort);
			daoConfig.setClusterName(elasticCluster);
			daoConfig.setCollectionTime(collectionTime);
			vdcDAO = new ElasticVdcDAO(daoConfig);
			// init indexes
			vdcDAO.initIndexes(collectionTime);
		} else {
			// Instantiate file DAO
			vdcDAO = new FileVdcDAO(null);
		}
		
		// instantiate billing BO
		VdcBO vdcBO = new VdcBO( ecsMgmtAccessKey, 
											 ecsMgmtSecretKey,
											 Arrays.asList(ecsHosts.split(",")),
											 ecsMgmtPort,
											 ecsAlternativeMgmtPort,
											 vdcDAO,
											 objectCount );
		
		// Start collection
		vdcBO.collectBucketOwner(collectionTime);
		vdcBO.shutdown();
	}
	
	/**
	 * 
	 * @param collectionTime
	 */
	private static void collectVdcList(Date collectionTime) {
		VdcDAO vdcDAO = null;
		if(!elasticHosts.isEmpty()) {
			// Instantiate file DAO
			ElasticDAOConfig daoConfig = new ElasticDAOConfig();
			daoConfig.setHosts(Arrays.asList(elasticHosts.split(",")));
			daoConfig.setPort(elasticPort);
			daoConfig.setClusterName(elasticCluster);
			daoConfig.setCollectionTime(collectionTime);
			vdcDAO = new ElasticVdcDAO(daoConfig);
			// init indexes
			vdcDAO.initIndexes(collectionTime);
		} else {
			// Instantiate file DAO
			vdcDAO = new FileVdcDAO(null);
		}
		
		// instantiate billing BO
		VdcBO vdcBO = new VdcBO( ecsMgmtAccessKey, 
											 ecsMgmtSecretKey,
											 Arrays.asList(ecsHosts.split(",")),
											 ecsMgmtPort,
											 vdcDAO,
											 objectCount );
		
		// Start collection
		vdcBO.collectVdcDetails(collectionTime);
		vdcBO.shutdown();	
	}
	
}