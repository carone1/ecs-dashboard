ECS-Dashbaord
======================

# Installations



## ElasticSearch

The collected metadata from ECS is stored in Elasticsearch  

[Centos 7 - : ElasticSearch Centos 7 Installation Guide] (https://www.digitalocean.com/community/tutorials/how-to-set-up-a-production-elasticsearch-cluster-on-centos-7)

[Ubuntu 14 - : ElasticSearch Ubuntu 14 Installation Guide] (https://www.digitalocean.com/community/tutorials/how-to-set-up-a-production-elasticsearch-cluster-on-ubuntu-14-04)

Few minor caveat from those guides.  

Assuming the hosts running ElasticSearch nodes are on a private lab/network - the vpn portion could be skipped from the guide.  

Instead of using `production` for the cluster name, we recommend using `ecs-analytics` as it is closer to the purpose of the cluster.

1. Note: Repeat Elasticsearch steps for all hosts in ElasticSearch Cluster. 
Initially we recommend three master eligible ElasticSearch nodes.  When required extra Elasticsearch nodes could be added to the cluster for extra capacity.

2. Note: Ensure ElasticSearch (9200:Default API port) is not blocked by a firewall. Between ElasticSearch hosts port `9300` is used to exchange cluster info. Ensure those ports are not blocked.


## Chrome Sense Plugin For Elasticsearch

To invoke Elasticsearch API's - The Chrome Sense plugin is very useful.

Download and Install: [Sense Chrome Plugin] (https://chrome.google.com/webstore/detail/sense-beta/lhjgkmllcaadmopgmanpapmpjgmfcfig?hl=en)

## ECS Metadata Collector

### Build/Upload Collector

	1. Build ecs-metadata-collector project. 
	2. Build generates `ecs-metadata-collector-<version>.tar under build/distributions`
	3. scp ecs-metadata-collector-<version>.tar file 

### Install Collector

	1. as user-running-collector. Don't use root to run ECS Metadata Collector
	2. cp ecs-metadata-collector-<version>.tar <collector-install-path>
	3. cd  <collector-install-path> 
	4. tar -xvf ecs-metadata-collector-<version>.tar


### Configure Collector

	1. cd ecs-metadata-collector-<version>
	2. vi ./config/logback.xml
	3. update path so logs will go to a proper location
		`<property name="DEV_HOME" value="update-to-your-location/ecs-collector-logs" />`

## Install Java

Java should already be installed as part of the ElasticSearch Guide if not install it.

	sudo yum install java-<version>
	
### Configure Java

	export JAVA_HOME=/usr/java/<version>/jre 
	(Adjust path where the jdk/jre is installed) 
	Consider adding export to user's bashrc file running collector.

Setting JAVA_HOME is important because the metadata collector relies on `JAVA_HOME` variable to grab certificates.

## ECS Management Certificates

The ECS Metadata Collector requires SSL certificates in order to communicate with ECS VMs.  

### Download Management SSL Certificate From Existing ECS

	echo -n | openssl s_client -connect <ecs-node-ip>:4443 | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > /tmp/ecs-mgmt.crt

### Management Certificate
Assuming management certificate is called /tmp/ecs-mgmt.crt

	As root: keytool -importcert -keystore /usr/java/jre/lib/security/cacerts -storepass changeit -file /tmp/ecs-mgmt.crt -alias ecs-mgmt

### Update Hosts File

Depending on how certificates are setup some hosts might need to be configured in local host file. 

#### Fictive Example

	Update /etc/hosts:
	<ip-address-1> ecs-host-vm-1   
	<ip-address-2> ecs-host-vm-2

## Run ECS Metadata Collector

Modify shell script with proper arguments

	cd <your-own-location>/ecs-metadata-collector-<version>/bin
	vi run-ecs-collector  
	(Adjust parameters to fit your environment. See usage instructions)

### Cron Job For ECS Metadata Collector

The script created in previous step should run once a day during a low traffic period.


##Kibana

### Download - Kibana

Grab latest tar file:  https://www.elastic.co/downloads/kibana

###Install - Kibana

	As user-running-kibana. Don't use root to run Kibana
	1. cp kibana-<version>.tar.gz <kibana-install-path>
	2. cd  <kibana-install-path> 
	3. tar -zxvf kibana-<version>.tar.gz
	4. Configure - Kibana
	5. vi <kibana-install-path>/kibana-<version>/config/kibana.yml

	The Elasticsearch instance to use for all your queries.
	
	elasticsearch.url: "http://localhost:9200"   
	
	We want Kibana and ElasticSearch running on the same host. 
	Use an externally visible ElasticSearch's ip address

### Start - Kibana

Kibana doesn't yet support running as a service. Someone on Github implemented an init.d script

Grab script from https://github.com/cjcotton/init-kibana

	sudo cp kibana file from github-link /etc.init.d/kibana

To make this script work, you'll need to change the following;

	Location of Kibana bin file:

	KIBANA_BIN=/home/kibana_user/kibana/current/bin
	DAEMON_USER=kibana_user
	
Once that's finished, you'll install the script to /etc/init.d/kibana 
	and run

	sudo service kibana start
	
You should be able to verify the script is running as the set user with

	ps aux | grep kibana
	
Note: Ensure Kibana port is not blocked by a firewall.

### Import Dashboard

The kibana configuration changes should applied only after the ECS Metadata Collector had a successful collection.  Only at that time, Kibana config changes should be implemented. 

Using Chrome Sense plugin verify that ecs-bucket, ecs-s3-object, ecs-billing-bucket, ecs-billing-namespace indexes are present and have entries in the docs.count column.  (Rest command: get _cat/indices?v)

[ElasticSearch Index Presence](doc/images/elasticSearchIndexPresence.png)

	Access Kibanna: http://<kibana-ip>:<kibana-port>/app/kibana#

### Configure Index Patterns

[Kibana index Pattern] (doc/images/kibanaindexPattern.png)

Under Settings / Indexes

	Create index for ecs-bucket, ecs-s3-object, ecs-billing-bucket, 
	ecs-billing-namespace. Always use the collection_time field 
	as the time-field name. 

###Import Searches

Under Settings / Objects / Searches

	Click Import and select kibana-searches-<date>.json

### Import Visualizations

Under Settings / Objects / Visualization

	Click Import and select kibana-visualization-<date>.json

### Import Dashboards

Under Settings / Objects / Dashboards

	Click Import and select kibana-dashboards-<date>.json
	
	
## ECS ElasticSearch Cleaner

### Download Cleaner
Download ecs-elasticsearch-cleaner-<version>.tar file provided by EMC

### Install Cleaner

As user-running-cleaner. Don't use root to run the ECS ElasticSearch Cleaner

	cp ecs-elasticsearch-cleaner-<version>.tar <cleaner-install-path>
	cd  <cleaner-install-path> 
	tar -xvf ecs-elasticsearch-cleaner-<version>.tar

### Configure Cleaner

	cd ecs-elasticsearch-cleaner-<version>
	vi ./config/logback.xml

	Update path so logs will go to a proper location
	
   	<property name="DEV_HOME" value="update-to-your-location/ecs-es-cleaner-logs>

### Install Java 

Assuming the ECS Collector is running on Centos.  Java should already be installed as part of the ElasticSearch Guide

	sudo yum install java-<version>

### Configure JAVA_HOME 

	export JAVA_HOME=/usr/java/<version>/jre 
	(Adjust path where the jdk/jre is installed) 
	Consider adding export to user's bashrc file running collector.

Setting JAVA_HOME is important because the metadata collector relies on the JAVA_HOME variable to grab certificates.

### Start Cleaner

Modify shell script with proper arguments

	cd <your-own-location>/ecs-elasticsearch-cleaner-<version>/bin
	vi run-ecs-es-cleaner  
	(adjust parameters to fit your environment)

### Cron Job For Cleaner

The script created in the previous step should be run once a day during a low traffic periods.


## ECS Kibana Emailer

Kibana does not support sending email with dashboard details. EMC developed an alternative which relies on the ChromeDriver utility.   This is just a temporary solution until Kibana introduces proper email support.

The solution was tested on OSX, Ubuntu 16 and CentOS 7.  Since the solution relies on opening on Chromium or Chrome for taking snapshots, it is required to have a proper display configured.

### Download - Emailer

Upload kibana-emailer-<version>.tar

### Install - Emailer

	as user-running-emailer. Don't use root to run the Kibana emailer
	cp kibana-emailer-<version>.tar <emailer-install-path>
	cd  <emailer-install-path> 
	tar -xvf kibana-emailer-<version>.tar
	
### Configure Logs - Emailer

	cd kibana-emailer-<version>
	vi ./config/logback.xml
	update path so logs will go to a proper location
    <property name="DEV_HOME" value="update-to-your-location/kibana-emailer-logs

### Download - Chrome Driver

http://chromedriver.storage.googleapis.com/index.html

### Install - Chrome Driver

	as user-running-emailer. Don't use root to run the Kibana emailer
	cp chrome-driver_<version>.zip <chrome-driver-install-path>
	cd  <chrome-driver-install-path> 
	unzip chrome-driver_<version>.zip 
	
### Install - Google Browser Centos 7 

Reference: http://www.tecmint.com/install-google-chrome-on-redhat-centos-fedora-linux/

1. Enable Google YUM repository
	
Create a file called /etc/yum.repos.d/google-chrome.repo and add the following lines of code to it.

	[google-chrome|google-chrome]
	name=google-chrome
	baseurl=[http://dl.google.com/linux/chrome/rpm/stable/$basearch]
	enabled=1
	gpgcheck=1
	gpgkey=[https://dl-ssl.google.com/linux/linux_signing_key.pub]
	
2. Installing Chrome Web Browser

First, check whether the latest version available from the Google’s own repository using following yum command.  Verify repo has chrome

	yum info google-chrome-stable

Install it

	yum install google-chrome-stable

Start to verify installation

	/bin/google-chrome 

### Install - Remote Desktop Software

Configure a remote desktop program of your choice so the chrome browser can be opened and screen capture can be taken.

### Configure Options - Kibana Emailer

	cd  <emailer-install-path> /config
	vi kibana-emailer.yml

Update parameters to fit your environment.

	# Config file for kibana emailer
	# kibana.urls:
	# - name: "Top 100 Dashboard"
	#   url: "http://kibana_username:kibana_password@url_to_kibana_dashboard"
	# - name: "Other Dashboard"
	#   url: "http://kibana_username:kibana_password@url_to_other_kibana_dashboard"


	# chrome driver location
	#chrome.driver: "<installation-chromedriver>/chromedriver"
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


### Install Java - Kibana Emailer

See above.


### Install Email Certificate - Kibana Emailer

The Kibana Emailer requires TLS or SSL certificates in order to communicate with a SMTP server. 

#### Obtain e-mail certificate 

Obtain email server certificate from email server administrator.

#### Email Certificate Installation

	Assuming email certificate is called /tmp/email.crt

	As root: keytool -importcert -keystore /usr/java/jre/lib/security/cacerts -storepass changeit -file /tmp/email.crt -alias email

### Start Kibana Emailer

	cd  <emailer-install-path> /bin
	./kibana-emailer --config-file ../config/kibana-emailer.yml
	
If it works well you can create a bash script to encapsulate the 

	`./kibana-emailer --config-file ../config/kibana-emailer.yml`

### Cron Job For Kibana Emailer

The script created in previous step should run once a day during a low traffic period.

