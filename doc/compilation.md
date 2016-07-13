ECS-Dashboard
======================

# Compilation

The instructiuons assumed the compilation is done on Linux.

## ECS Metadata Collector

	cd ecs-metadata-collector
	./gradlew distTar	
	
After the compilation completes there should be a tar file generated
	
	 `ecs-metadata-collector/build/distributions`
	                  / `ecs-metadata-collector-<version>.tar` 
	
## ECS ElasticSearch Cleaner

	cd ecs-elasticsearch-cleaner
	./gradlew distTar
	
After the compilation completes there should be a tar file generated 
	
	 `ecs-elasticsearch-cleaner/build/distributions`
	                  / `ecs-metadata-collector-<version>.tar` 



## ECS Kibana Emailer

	cd kibana-emailer
	./gradlew distTar	
	
After the compilation completes there should be a tar file generated
	
	 `kibana-emailer/build/distributions`
	                  / `kibana-emailer-<version>.tar` 
	

	


	
	




