/**
 * 
 */
package com.emc.ecs.metadata.utils;

/**
 * @author nlengc
 *
 */
public class Constants {

	// ES client constants
	public final static String CLIENT_SNIFFING_CONFIG = "client.transport.sniff";
	public final static String CLIENT_CLUSTER_NAME_CONFIG = "cluster.name";
	public final static String COLLECTION_TIME = "collection_time";
	public final static String ANALYZED_TAG = "_analyzed";
	public final static String NOT_ANALYZED_INDEX = "not_analyzed";
	public final static String ANALYZED_INDEX = "analyzed";
	
	// xpack constants
	public final static String XPACK_SECURITY_USER = "xpack.security.user";
	public final static String XPACK_SSL_KEY = "xpack.ssl.key";
	public final static String XPACK_SSL_CERTIFICATE = "xpack.ssl.certificate";
	public final static String XPACK_SSL_CERTIFICATE_AUTH = "xpack.ssl.certificate_authorities";
	public final static String XPACK_SECURITY_TRANPORT_ENABLED = "xpack.security.transport.ssl.enabled";
}
