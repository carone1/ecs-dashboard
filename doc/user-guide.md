ECS-Dashboard
======================


##Target Deployment

![Target Deployment](https://github.com/carone1/ecs-dashboard/blob/master/doc/images/ECSAnalyticsDeployment.png)

ECS HOSTs:  ECS Hosts providing storage. 

##ECS Analytics Host:

1. **Elasticsearch**: No sql database providing data index and analytics capabilities. Open source product supported by Elastic.
 
2. **Kibana**: Dashboard product specifically designed to work with ElasticSearch.  Open source product supported by Elastic.

3. **ECS Metadata Collector**: Java program to collect metadata information from an ECS cluster. Collected metadata is stored in ElasticSearch. Developed by EMC.

4. **ES Data Cleaner**: Java program to check documents' age in ElasticSearch and delete accordingly.  The age criteria is configurable by the administrator. Developed by EMC.

5. **Kibana Emailer**: Kibana does not support emailing dashboards.  A custom emailer was developed by EMC.  The ChromeDriver program is leveraged to programmatically control Chrome and/or Firefox web browser(s). 

### ECS Metadata Collector Options

The Metadata collector supports various configuration arguments.

| Argument | Description |
|----------|:-----------|
| `--ecs-hosts <host1,host2>` | ECS hosts used for management API communications |
| `--ecs-mgmt-access-key <admin-username>` | ECS Admin username to authenticate sessions over management API communications |
| `--ecs-mgmt-secret-key <admin-password>` | ECS Admin password to authenticate sessions over management API communications |
| `--ecs-mgmt-port <management-port>` | ECS Management Port for Management API communications |
| `--elastic-hosts <es-host1,es-host2>` | ElasticSearch Hosts Names. |
| `--elastic-cluster <es-cluster-name>` | ElasticSearch's cluster name. This value must match the value configured in Es' configuration file |
| `--collect-data [billing | bucket | object | object-version | all]` | Full metadata collection option. All option includes billing, bucket and object data. object-version has to be invoked separately if required. |
| `--collect-only-modified-objects <modified since number of days #>` | Partial metadata object collection. Option to collect objects that have been modified since = `current date/time - specified number of days`. This option is an alternative to \--collect-data which does full collection might be too lengthy. This option requires that the object have MD Keys search enabled on the bucket and LastModified time is indexed for query purposes. |


At initialization time, collector tries to connect to all ElasticSearch hosts and verifies presence of indexes used by the solution. When indexes are not already present collector creates them before starting a collection run. See below for a detail more details about the ElasticSearch indexes. After indexes have been created and confirmed to be present, the collector connects to one or multiple ECS hosts and starts collecting object metadata. Metadata is collected in batches of 1000 objects per batch through ECS object APIs. After reception of each batch, metadata is written to an ElasticSearch cluster also in batches. The collector also collects billing data through ECS management APIs. The collector is expected to run on a daily basis so data in ElasticSearch is kept up-to-date.


## ElasticSearch Cleaner

As data is frequently collected, ElasticSearch indexes can't grow forever as disk space is limited. The data collected is timestamped through a field called `collection_time`.  Based on that field the cleaner can decide to delete documents that were collected x number of days ago. The administrator can decide how many days' worth of data should be kept around using the \--collection-days-to-keep.

The cleaner supports various configuration arguments.

| Argument | Description |
|----------|:-----------|
| `--elastic-hosts <es-host1,es-host2>`  | ElasticSearch Hosts Names.  |
| `--elastic-cluster <es-cluster-name>` | ElasticSearch's cluster name. This value must match the value configured in ES' configuration file  |
| `--clean-data [billing | bucket | object | object-version | all]` | Full metadata deletion option. All options include billing, bucket, object and object-version data |
| `--collection-days-to-keep <number-of-days>` | Specify number of days data should be kept. Example if user specifies 4 days then data collected 5 days ago will be deleted by the cleaner. |


## Kibana Emailer

Kibana doesn't yet support sending emails containing dashboard captures. As an interim solution, EMC develop a Java program that leverages an Open Source program called ChromeDriver which allows to control Chrome or Firefox programatically. 

The Kibana Emailer support only one config argument

| Argument | Description |
|----------|:-----------|
| `--config-file <emailer-config-file>` | Specify where to pickup the emailer's yml configuration file |

### Kibana Emailer - Config File

There is an example like below available in the Kibana Emailer tar file under the config directory.


	# Config file for kibana emailer

	# kibana.urls:
	# - name: "Top 100 Dashboard"
	#   url: "http://kibana_username:kibana_password@url_to_kibana_dashboard"
	# - name: "Other-Dashbaord"
	#   url: "http://kibana_username:kibana_password@url-2_to_kibana_dashboard""

	# chrome driver location
	chrome.driver: "<chromedriver-path>/chromedriver"
	# chrome browser location
	#chrome.browser: "some location"
	# delay before taking screen capture in seconds
	#screen.capture.delay: 20

	# path where Kibana screen captures are saved
	#destination.path: ""

	# email section
	# smtp
	#smtp.host: "localhost"
	#smtp.port: 587
	#smtp.username: "user"
	#smtp.password: "password"
	# Specify tls or ssl. Default to tls
	#smtp.security: "tls"

	# source host
	#source.host: "localhost"

	# e-mail address
	#source.address: "from-email@gmail.com"


	#destination.addresses:
	# - "destination1@gmail.com"
	# - "destination2@gmail.com"


	# e-mail content
	#mail.title: "Kibana Reports"
	#mail.body: "Kibana Body"




