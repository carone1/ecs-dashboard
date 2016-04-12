package com.ecs.object.s3;

import java.util.List;

import com.emc.object.Protocol;
import com.emc.object.s3.S3Config;
import com.emc.object.s3.bean.Bucket;
import com.emc.object.s3.bean.ListBucketsResult;
import com.emc.object.s3.jersey.S3JerseyClient;
import com.emc.rest.smart.ecs.Vdc;

/**
 * ECS S3 client to collect Metadata from various ECS systems 
 * Created by Eric Caron
 */
public class EcsS3Client {
	
	private static S3JerseyClient s3JerseyClient;
	private static String ecsHost = "";
	private static String queryAction = "";

	public static void main(String[] args) throws Exception {

		String menuString = "Usage: EcsS3Client [--ecs-host <host>] [--query-action <list-buckets>  "; 
		
		if ( args.length > 0 && args[0].contains("--help")) {
			System.err.println (menuString);
			System.err.println("Example queue name are: *");
			System.exit(0);
		} else {

			int i = 0;
			String arg;
	
			while (i < args.length && args[i].startsWith("--")) {
				arg = args[i++];

				if (arg.equals("--ecs-host")) {
					if (i < args.length) {
						ecsHost = args[i++];
					} else {
						System.err.println("--lors-exchange-key requires an exchange key value");
						System.exit(0);
					}
				} else if (arg.equals("--query-action")) {
					if (i < args.length) {
						queryAction = args[i++];
					} else {
						System.err.println("--lors-exchange-key requires an exchange key value");
						System.exit(0);
					}
				} 
				else {
					System.err.println(menuString);
					System.exit(0);
				} 
			}                
		}
		
		if(!ecsHost.isEmpty()) {
			Vdc plymouthLab = new Vdc(ecsHost).withName("PlymouthLab");		
			S3Config s3config = new S3Config(Protocol.HTTP, plymouthLab);
			
			// in all cases, you need to provide your credentials
			s3config.withIdentity("eric-caron-admin").withSecretKey("Nord99sud");
			//withSecretKey("n4tGqMYn67Jk3dkJmZ9+j6rEEJL0G6TJDYi/C5fr");
			
			s3JerseyClient = new S3JerseyClient(s3config);
		} else {
			System.err.println("Invalid ECS hostname: `" + ecsHost + "`" );
		}
		
		if(queryAction.contains("list-buckets")){
			listBuckets();
		} else {
			System.err.println("Unsupported query action: " + queryAction );
			System.err.println(menuString);
			System.exit(0);
		}
	}
	
	private static void listBuckets() {
			    						
		// List all buckets 
		ListBucketsResult bucketResult = s3JerseyClient.listBuckets();
		
		if( bucketResult != null ) {
			List<Bucket> bucketList = bucketResult.getBuckets();
			for( Bucket buck : bucketList ) {
				System.out.println("Found bucket: " + buck.getName());
			}
		}
	}
	
	

}

