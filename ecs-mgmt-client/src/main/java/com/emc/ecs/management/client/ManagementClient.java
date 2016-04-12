package com.emc.ecs.management.client;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.core.Response.StatusType;

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.emc.rest.smart.LoadBalancer;
import com.emc.rest.smart.SmartClientFactory;
import com.emc.rest.smart.SmartConfig;
import com.emc.rest.smart.ecs.EcsHostListProvider;
import com.emc.rest.smart.ecs.PingResponse;
import com.emc.rest.smart.ecs.Vdc;
import com.sun.jersey.api.client.Client;



public class ManagementClient {

	
	public static void main(String[] args) throws Exception {
				
		
		//System.setProperty("javax.net.ssl.HostnameVerifier", "/usr/java/jdk1.7.0_75/jre/lib/security/cacerts");
		//System.setProperty("javax.net.ssl.keyStorePassword", "changeit");
		
		Client mgmtClient = createMgmtClient("eric-caron-admin", "Nord99sud", 4443, "localhost");
		
		// header is workaround for STORAGE-1833
        PingResponse response = mgmtClient.resource(new URI("https://localhost:4443/object/namespaces"))
        		.header("x-sds-auth-token", "BAAcQ0xhcGxJdnpIQmVpTUM1WXF5L1A4T3Z3amU4PQMAjAQASHVybjpzdG9yYWdlb3M6VmlydHVhbERhdGFDZW50ZXJEYXRhOjQwN2I2YjZjLWJkYTQtNGJhNC04OWY3LTIyMGFjM2Q5YzA0NAIADTE0NjAzMTUwMDUxMDgDAC51cm46VG9rZW46YjFiNTVkM2QtZTY5Yy00M2E0LWIwYzAtZWZhYWRjMGI0ZjdkAgAC0A8=")
                .get(PingResponse.class);
	}	

	
	static Client createMgmtClient(String userName, String secretKey, int port, String... ipAddresses ) {
		
	    SmartConfig smartConfig = new SmartConfig(ipAddresses);
	    LoadBalancer loadBalancer = smartConfig.getLoadBalancer();

	    // creates a standard (non-load-balancing) jersey client
	    Client pollClient = SmartClientFactory.createStandardClient(smartConfig);

	    // create a host list provider based on the S3 ?endpoint call (will use the standard client we just made)
	    EcsHostListProvider hostListProvider = new EcsHostListProvider(pollClient, loadBalancer,
	            userName, secretKey);

	    hostListProvider.setProtocol("https");
	    hostListProvider.setPort(port);
	    hostListProvider.withVdcs(new Vdc(ipAddresses));

	    smartConfig.setHostListProvider(hostListProvider);

	    return SmartClientFactory.createSmartClient(smartConfig);
	}
	
}
