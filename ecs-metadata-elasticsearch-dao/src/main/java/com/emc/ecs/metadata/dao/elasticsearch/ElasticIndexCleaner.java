package com.emc.ecs.metadata.dao.elasticsearch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticIndexCleaner {

	private static Logger LOGGER = LoggerFactory.getLogger(ElasticIndexCleaner.class);
	
    public static void truncateOldIndexes(TransportClient elasticClient, 
    		                               Date            thresholdDate, 
    		                               String          indexName,
    		                               String          indexType) {
    	
    	List<String> indexesToDelete = new ArrayList<String>(); 
    	  	
    	ImmutableOpenMap<String, IndexMetaData> indices = elasticClient.admin().cluster()
                                   .prepareState().get().getState().getMetaData().getIndices();

    	Iterator<String> itr = indices.keysIt();
    	
    	while( itr.hasNext() ) {
    		String idxName = itr.next();
    		
    		// only process index that are matching the main index name
    		if(idxName.startsWith(indexName)) {
    			// stripping out index name portion
    			String dateString = idxName.replaceAll(indexName + "-", "");
    			// assumption: string is following this format YYYY-MM-DD
    			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    			try {
    				//create date out of the constructed date
    				Date indexDate = sdf.parse(dateString);

    				// check if index date is older 
    				// than threshold date
    				if(indexDate.before(thresholdDate) || indexDate.equals(thresholdDate)) {
    					// Looks like this index is older than our
    					// threshold date 
    					// index is marked for deletion deletion
    					indexesToDelete.add(idxName);
    				}
    			} catch (ParseException e) {
    				LOGGER.error("Issue encountered when parsing index name: " + idxName + " error:" + e.getMessage());
    			}
    		}
    	}
    	
    	// Delete identified indexes 
    	IndicesAdminClient indicesAdminClient = elasticClient.admin().indices();
    	for(String indexToDelete : indexesToDelete) {
    		DeleteIndexRequest deleteRequest = new DeleteIndexRequest(indexToDelete);
    		indicesAdminClient.delete(deleteRequest);
        }
    }
	
}
