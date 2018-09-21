Ansible Based Installation
==========================

In this example: ansible playbooks are installed on a host called ansible01.
The workload is handled by nodes called: node01, node02, node03

## Prerequisite
1. node01, node02 and node03 have sshd running
2. node01, node02 and node03 have python installed
3. ansible01 is able to ssh to node01, node02 and node03 without being prompted for a password
     1. Generate keys on ansible01
     	   ssh-keygen -t rsa
     2. Public keys transfers from ansible01 to node01, node02 and node03

          ```ssh-copy-id <username>@host```
     3. ssh to node01, node02 and node03 to ensure public keys are accepted
        on ansible01

          ssh <username@host
4. install ansible on ansible01 hosts

	      sudo yum install ansible
5. tweak ansible config on ansible01 as ElasticSearch playbook expects elevated privileges
   for installing packages by default.

          sudo vi /etc/ansible/ansible.cfg

          uncomment those lines

          [privilege_escalation]

          become=True

          become_method=sudo
6. Ensure user account to run ansible playbook has sudo privileges on node01, node02 and node03.



## Ansible ElasticSearch

The collected metadata from ECS is stored in ElasticSearch. An ansible playbook is used
to deploy Elasticsearch on our hosts.


1. Create /my/gitrepos directory

	    mkdir /my/gitrepos ; /my/gitrepos

2. Clone Elasticsearch playbook

	    git clone https://github.com/carone1/ansible-elasticsearch.git

3. Create /my/playbooks/roles

	    mkdir -p /my/playbooks/roles

4. Link ES playbook under roles

        ln -s /my/gitrepos/ansible-elasticsearch /my/playbooks/roles/elasticsearch

5. cd to playbookdir

	    cd /my/playbooks/

6. create hosts file indicating where master and data nodes are running

	    [elasticsearch_master_nodes]

	    [elasticsearch_master_data_nodes]
	    node01
	    node02
	    node03

	    [elasticsearch_data_nodes]

7. copy /my/gitrepos/ansible-elasticsearch/install-elasticsearch.yml.sample into
   /my/playbooks/ and modify parameters that might require modifications

      ```

      ---
      - hosts: master_data_nodes
      name: Elasticsearch with custom configuration
      roles:
        #expand to all available parameters
        - { role: elasticsearch,
            es_instance_name: "es1",
            es_data_dirs: "/datadisk/elasticsearch/data",
            es_log_dir: "/datadisk/elasticsearch/logs",
        es_config: {
            node.name: "node1",
            cluster.name: "ecs-analytics",
            discovery.zen.ping.unicast.hosts: "node01, node02, node03",
            network.host: "_eth0_, , _local_",
            node.data: true,
            node.master: true,
            bootstrap.memory_lock: true,
            http.port: "{{es_api_port}}",
            transport.tcp.port:  "{{es_transport_port}}",
            }
        }

      ```



Note: Most values can be modified to fit your preferences like es_instance_name,
      node.name, cluster.name. Change the value eth0 to match your hosts nic.

8. run elasticsearch playbook on ansible01

	    ansible-playbook -i host install-elasticsearch.yml --ask-become

9. Point a browser to *node01:9200* or *node02:9200* or *node03:9200*

Should be getting an output like this.

     ```
     {
       "name" : "192.168.0.10-es1",
       "cluster_name" : "analytics-cluster",
       "cluster_uuid" : "hP9JQqUpTaus_SdMlSMX7Q",
       "version" : {
         "number" : "5.1.2",
         "build_hash" : "c8c4c16",
         "build_date" : "2017-01-11T20:18:39.146Z",
         "build_snapshot" : false,
         "lucene_version" : "6.3.0"
     },
     "tagline" : "You Know, for Search"
     }
     ```

## X-Pack and SSL features

In order for X-Pack to enforce security and encrypt traffic to, from and within your Elasticsearch cluster, define following properties in elasticsearch configuration file via es_config environment variable.  

* ```xpack.ssl.key``` - full path to certificate key
* ```xpack.ssl.certificate``` - full path to node certificate
* ```xpack.ssl.certificate_authorities``` - full path to certificate authorities
* ```xpack.security.transport.ssl.enabled``` - enable ssl on transport layer
* ```xpack.security.http.ssl.enabled``` - enable ssl on http layer

An example is as follow:

      ```
      ---
      - hosts: master_data_nodes
      name: Elasticsearch with custom configuration
      roles:
        #expand to all available parameters
        - { role: elasticsearch,
            es_instance_name: "es1",
            es_data_dirs: "/datadisk/elasticsearch/data",
            es_log_dir: "/datadisk/elasticsearch/logs",
            es_config: {
              node.name: "node1",
              cluster.name: "ecs-analytics",
              discovery.zen.ping.unicast.hosts: "node01, node02, node03",
              network.host: "_eth0_, , _local_",
              node.data: true,
              node.master: true,
              bootstrap.memory_lock: true,
              http.port: "{{es_api_port}}",
              transport.tcp.port:  "{{es_transport_port}}",
              xpack.ssl.key: "{{conf_dir}}/{{node_key}}",
              xpack.ssl.certificate: "{{conf_dir}}/{{node_crt}}",
              xpack.ssl.certificate_authorities: ["{{conf_dir}}/{{node_ca}}"],
              xpack.security.transport.ssl.enabled: true,
              xpack.security.http.ssl.enabled: true
            }
          }
          vars:
            es_enable_xpack: true
            es_xpack_custom_url: "https://artifacts.elastic.co/downloads/packs/x-pack/x-pack-{{ es_major_version }}.zip"
            use_xpack_certificate: true
            es_api_basic_auth_username: elastic
            es_api_basic_auth_password: changeme
            es_xpack_features: ["alerting","monitoring","graph","security"]
            local_certificate_conf_dir: "/localdisk/ansible-playbook/node01"
            node_crt: "node01.crt"
            node_key: "node01.key"
            node_ca: "ca.crt"
      ```

* ```node_key``` - node certificate key
* ```node_crt``` - node certificate
* ```node_ca``` - node certificate authorities
* ```local_certificate_conf_dir``` - local folder containing all certificates and keys
* ```use_xpack_certificate``` - set to true if you want to install certificates via playbook

#### Important note on certificates
Certificates that you obtain must allow for both clientAuth and serverAuth if the extended key usage extension is present. The certificates need to be in PEM format. Although not required, it is highly recommended that the certificate contain the dns name(s) and/or ip address(es) of the node so that hostname verification may be used.

## Ansible Kibana

The collected metadata from ECS is stored in ElasticSearch and displayed using Kibana.
An ansible playbook is used to deploy Kibana on our hosts.


1. Create /my/gitrepos directory

	    mkdir /my/gitrepos ; /my/gitrepos

2. Clone Elasticsearch playbook

	    git clone https://github.com/carone1/ansible-role-kibana.git

3. Create /my/playbooks/roles

	    mkdir -p /my/playbooks/roles

4. Link ES playbook under roles

        ln -s /my/gitrepos/ansible-role-kibana /my/playbooks/roles/kibana

5. cd to playbook dir

	    cd /my/playbooks/

6. create hosts file indicating where kibana nodes are running. Alternatively, kibana role can be added
   bottom of the existing hosts file created during the elasticsearch installation steps.

	    [kibana]
	    node01
	    node02
	    node03


7.  copy /my/gitrepos/ansible-role-kibana/install-kibana.yml.sample into /my/playbooks/
    should look like this


      ```
      - hosts: kibana
      roles:
       - kibana

      ```

8. Adjust few parameters in /my/playbooks/roles/kibana/defaults/main.yml if required.

      ```
      ---
      major_kibana_version: "5.x"

      kibana_server_port: 5601

      # specify if playbook should open firewall port
      kibana_listen_external: true

      # interface where kibana will respond to external request
      kibana_interface: "eth0"

      kibana_elasticsearch_url: "http://localhost:9200"

      kibana_elasticsearch_requestTimeout: 30000


      kibana_logging_dest: /var/log/kibana/kibana.log
      ```

10. if you want to enable security features (x-pack), add  and/or adjust following parameters in /my/playbooks/roles/kibana/defaults/main.yml

      ```
      ---
      kibana_elasticsearch_url: "https://localhost:9200"

      # enable or disable x-pack plugin (security)
      kibana_use_xpack_authentication: false

      # kibana needs to authenticate with Elasticsearch
      kibana_elasticsearch_username: "elastic"

      kibana_elasticsearch_password: "changeme"

      # kibana home directory
      kibana_home: /usr/share/kibana
      ```

11. run kibana playbook on ansible01

	    ansible-playbook -i host install-kibana.yml --ask-become

12. Point a browser to *node01:5601* or *node02:5601* or *node03:5601*

Kibana interface should be coming.



## ECS Dashboard

On top of using Elasticsearch and Kibana there are extra java programs to:

	1. Collect metadata information from ECS systems. Referenced as the 'Collector'
	2. Purge older metadata stored in Elasticsearch. Referenced as the 'Cleaner'
	3. Screen capture Kibana Dashboards and send them by email. Referenced as the 'Emailer'

An ansible playbook was developed to install Collectors, Cleaners, Emailer on nodes where Elasticsearch and Kibana are also installed.


1. Create /my/gitrepos directory

	    mkdir /my/gitrepos ; /my/gitrepos

2. Clone Elasticsearch playbook

	    git clone https://github.com/carone1/ansible-ecs-dashboard.git

3. Create /my/playbooks/roles

	    mkdir -p /my/playbooks/roles

4. Link ECS Dashboard playbook under roles

        ln -s /my/gitrepos/ansible-ecs-dashboard /my/playbooks/roles/ecs_dashboard

5. cd to playbook root

	    cd /my/playbooks

6. create hosts file indicating where Collectors, Cleaners and Emailers will be installed

	    [ecs_dashboard]
	    node01
	    node02
	    node03


7.  copy /my/gitrepos/ansible-ecs-dashboard/install-ecs-dashboard.yml.sample into /my/playbooks/install-ecs-dashboard.yml
    It should look like this


      ```
      - hosts: ecs_dashboard
      roles:
       - ecs_dashboard

      ```

8. Adjust few parameters in /my/playbooks/roles/ecs-dashboard/defaults/main.yml where indicated below.

      ```
      ---

      # default file for ecs dashboard

      ecs_dashboard_bin_version: 1.5.2
      ecs_dashboard_url_version: 'v1.5.2'

      chrome_driver_url_version: '2.27'
      chrome_driver_bin_version: '2.27'

      # *** section to be modified ***
      ecs_hosts: 'specify-host'
      ecs_mgmt_access_key: 'specify-user-key'
      ecs_mgmt_secret_key: 'specify-user-secret'
      ecs_mgmt_port: 4443
      ecs_alt_mgmt_port: 9101

      # *** section to be modified as mutliple Elasticsearch hosts are proabably installed***
      # ip or hosts for elasticsearch cluster
      # multiple hosts ip must comma seperated
      elastic_hosts: 'localhost'

      # port to use to communicate with elasticsearch
      # cluster this is the transport port
      elastic_port: 9300

      # This value must match the value in the elasticsearch playbook
      # elasticsearch cluster name
      elastic_cluster_name: 'ecs-analytics'

      # specify how many days worth of data
      # cleaner will keep in elastcisearch
      collection_days_to_keep: 7


      # Emailer settings  (Optional: You can ignore emailer section if you are not using emailer)

      # *** section to be modified as the links are not valid ****
      kibana_urls:
       - name: "AnalysisPerNamespace"
         url: "http://kibana_username:kibana_password@url_to_kibana_dashboard"
      # - name: "Top 10 Dashboard"
      #   url: "http://kibana_username:kibana_password@url_to_kibana_dashboard_2"

      # *** section to modified if chrome is installed somewhere else ****
      # chrome browser location
      chrome_browser: "/bin/google-chrome"

      # delay before taking screen capture in seconds
      screen_capture_delay: 15

      # path where Kibana screen captures are saved
      screen_capture_destination_path: "{{ ecs_dashboard_install_dir }}/kibana-screen-captures"

      # section to be modified to match your smtp server config
      smtp_host: "localhost"
      smtp_port: 587
      smtp_username: "smtpusername"
      smtp_password: "smtppassword"

      # Specify tls or ssl. Default to tls
      smtp_security: "tls"

      # source host
      smtp_source_host: "localhost"

      # e-mail address
      smtp_source_address: "ecsdashboard@ecsanalytics.com"

      smtp_destination_addresses:
      - "destination@gmail.com"
      # - "destination2@gmail.com"

      # e-mail content
      # mail.title
      smtp_mail_title: "Kibana Reports"

      # mail.body
      smtp_mail_body: "Kibana Body"

      # certificate
      smtp_ssl_port: 443
      
      # x-pack authentication
      
      # enable or disbale x-pack security (if set to false, security must also be disable on elasticsearch cluster)
      xpack_security_enable: false
      # x-pack credentials
      xpack_user: "elastic"
      xpack_pwd: "changeme"
      # path certificates used by ssl
      xpack_ssl_key: "/etc/elasticsearch/es1/es1.key"
      xpack_ssl_certificate: "/etc/elasticsearch/es1/es1.crt"
      xpack_ca_certificate: "/etc/elasticsearch/es1/ca.crt"

      ```

8. run kibana playbook on ansible01

	    ansible-playbook -i host install-ecs-dashboard.yml --ask-become

	    If not installing emailer

	    ansible-playbook -i host install-ecs-dashboard.yml --ask-become --skip-tags emailer

9. Under /opt/ecs-dashboard on node01, node02, node03 there will be different java programs installed and pre-configured scripts installed to run collectors, cleaners, emailers.

      	drwxr-xr-x. 2 ecs-dashboard ecs-dashboard  chromedriver-2.9
      	drwxr-xr-x. 5 ecs-dashboard ecs-dashboard  ecs-elasticsearch-cleaner-<ecs_dashboard_version>
      	drwxr-xr-x. 6 ecs-dashboard ecs-dashboard  ecs-metadata-collector-<ecs_dashboard_version>
      	drwxr-xr-x. 5 ecs-dashboard ecs-dashboard  kibana-emailer-<ecs_dashboard_version>
      	drwxr-xr-x. 5 ecs-dashboard ecs-dashboard  logs
      	-rwxr-xr-x. 1 ecs-dashboard ecs-dashboard  run_ecs_collector_for_all_data.sh
      	-rwxr-xr-x. 1 ecs-dashboard ecs-dashboard  run_ecs_collector_for_billing_data.sh
      	-rwxr-xr-x. 1 ecs-dashboard ecs-dashboard  run_ecs_collector_for_index_init.sh
      	-rwxr-xr-x. 1 ecs-dashboard ecs-dashboard  run_ecs_collector_for_object_data.sh
      	-rwxr-xr-x. 1 ecs-dashboard ecs-dashboard  run_ecs_collector_for_object_version_data.sh
      	-rwxr-xr-x. 1 ecs-dashboard ecs-dashboard  run_ecs_collector_for_bucket_owner_data.sh
      	-rwxr-xr-x. 1 ecs-dashboard ecs-dashboard  run_ecs_collector_for_vdc_data.sh
      	-rwxr-xr-x. 1 ecs-dashboard ecs-dashboard  run_ecs_collector_for_namespace_detail_data.sh      	
      	-rwxr-xr-x. 1 ecs-dashboard ecs-dashboard  run_ecs_collector_for_namespace_quota_data.sh
      	-rwxr-xr-x. 1 ecs-dashboard ecs-dashboard  run_ecs_collector_for_object_data_namespace.sh      	
      	-rwxr-xr-x. 1 ecs-dashboard ecs-dashboard  run_ecs_collector_for_object_data_bucket.sh      	      	
      	-rwxr-xr-x. 1 ecs-dashboard ecs-dashboard  run_ecs_elasticsearch_cleaner_for_all_data.sh
      	-rwxr-xr-x. 1 ecs-dashboard ecs-dashboard  run_ecs_elasticsearch_cleaner_for_billing_data.sh
      	-rwxr-xr-x. 1 ecs-dashboard ecs-dashboard  run_ecs_elasticsearch_cleaner_for_object_data.sh
      	-rwxr-xr-x. 1 ecs-dashboard ecs-dashboard  run_ecs_elasticsearch_cleaner_for_object_version_data.sh
      	-rwxr-xr-x. 1 ecs-dashboard ecs-dashboard  run_kibana_emailer.sh


# Import Default Kibana ECS Dashboards

### Verify Indexes Presence


Using Kibana DevTools http://<kibana-ip>:5601/app/kibana#/dev_tools/console?_g=() verify that indexes are present.

	ecs-bucket-<yyyy-mm-dd-HH:MM:ss>,
	ecs-s3-object-<yyyy-mm-dd-HH:MM:ss>,
	ecs-object-version-<yyyy-mm-dd-HH:MM:ss>,
	ecs-billing-bucket-<yyyy-mm-dd-HH:MM:ss>,
	ecs-billing-namespace-<yyyy-mm-dd-HH:MM:ss>
	ecs-vdc-<yyyy-mm-dd-HH:MM:ss>,
	ecs-owner-bucket-<yyyy-mm-dd-HH:MM:ss>,
	ecs-namespace-detail-<yyyy-mm-dd-HH:MM:ss>,
	ecs-namespace-quota-<yyyy-mm-dd-HH:MM:ss>

	(Rest command: "get _cat/indices?v")

![ElasticSearch Index Presence](https://github.com/carone1/ecs-dashboard/blob/master/doc/images/elasticSearchIndexPresence.png)

	Access Kibanna: http://<kibana-ip>:<kibana-port>/app/kibana#

### Start Data Collection

Might be design intent but Kibana 5.2+ will complain when importing searches/visualizations/dashboards if referenced indexes don't have any record/document present in them.  The import will sill be succesful but Kibana will keep give errors when opening out dashboards.   As described here [Visualize field is a required parameter](https://discuss.elastic.co/t/visualize-field-is-a-required-parameter-how-to-solve/74619) The workaround is to initiate a data collection wait for a few minutes before proceding to Index Pattern Creation

	On Node01 or Node02 or Node03
	cd /opt/ecs-dashboard
	First shell
	./run_ecs_collector_for_object_version_data.sh
	Second Shell
	./run_ecs_collector_for_object_data.sh


Wait 4-5 minutes so all indexes will have at least one document in them.

There are special scripts which take some parameters to be able to execute data collection. Scripts run_ecs_collector_for_object_data_namespace.sh and run_ecs_collector_for_object_data_bucket.sh use namespace name and bucket name. For the first one it will collect all data for the specified namespace, as for the second it will collect all data for specified namespace and all buckets which name starts with specified bucket name.

	On Node01 or Node02 or Node03
	cd /opt/ecs-dashboard
	./run_ecs_collector_for_object_data_namespace.sh <namespace_name>
	or execute
	./run_ecs_collector_for_object_data_bucket.sh <namespace_name> <bucket_name>

Where <namespace_name> and <bucekt_name> are to be replaced with namespace and bucket names.

### WARNING on Data Collection and Kibana Data Aggregation

Data collection must be initiated on a daily basis. Moreover there a special scripts that can only be executed ONCE A DAY to allow Kibana to properly aggregate data.

	./run_ecs_collector_for_object_data_namespace.sh <namespace_name>
	./run_ecs_collector_for_object_data_bucket.sh <namespace_name> <bucket_name>
	./run_ecs_collector_for_object_data_bucket.sh (Full object bucket import)
	
If you run one of those scripts on a day you MUST NOT execute others on the same day (or re-run it) as it will results in data being imported to Kibana more than once.

For example, if you run script ./run_ecs_collector_for_object_data.sh you MUST NOT run script ./run_ecs_collector_for_object_data_bucket.sh <namespace_name> <bucket_name> and ./run_ecs_collector_for_object_data_namespace.sh on the same day.

The first script will have already imported data and the later will just import (AGAIN) data from the specified namespace and/or bucket.


### Configure Index Patterns

![Kibana Index Pattern] (https://github.com/carone1/ecs-dashboard/blob/master/doc/images/kibanaIndexPattern.png)

Under Settings / Index Paterns

Create index patern for

	ecs-bucket*,
	ecs-s3-object*,
	ecs-object-version*,
	ecs-billing-bucket*,
	ecs-billing-namespace*.
	ecs-vdc*,
	ecs-owner-bucket*,
	ecs-namespace-detail*,
	ecs-namespace-quota*

Note: Always use the collection_time field as the time-field name. It is very very important to end index patern names with -* so the pattern will be able to see all indexes terminating with -yyyy-mm-dd patterns.

###Import Searches

Under Settings / Objects / Searches


	Download file locally from => wget https://github.com/carone1/ecs-dashboard/releases/download/v<ecs_dashboard_version>/kibana-searches-<ecs_dashboard_version>.json
	Click Import and select kibana-searches-<ecs_dashboard_version>.json.

### Import Visualizations

Under Settings / Objects / Visualization

	Download file locally from => wget https://github.com/carone1/ecs-dashboard/releases/download/v<ecs_dashboard_version>/kibana-visualization-<ecs_dashboard_version>.json
	Click Import and select kibana-visualization-<ecs_dashboard_version>.json.

### Import Dashboards

Under Settings / Objects / Dashboards

	Download file locally from => wget https://github.com/carone1/ecs-dashboard/releases/download/v<ecs_dashboard_version>/kibana-dashboards-<ecs_dashboard_version>.json
	Click Import and select kibana-dashboards-<ecs_dashboard_version>.json.

### Check ECS Dashbords

	ECS Dashboards release are available at https://github.com/carone1/ecs-dashboard/releases


Voila! You should have a functional dashboard at this point.
