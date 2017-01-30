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

        ln -s /my/gitrepos/ansible-elasticsearch /my/playbooks/elasticsearch
    
5. cd to playbook

	    cd /my/playbooks/elasticsearch
	
6. create hosts file indicating where master and data nodes are running

	    [elasticsearch_master_nodes]

	    [elasticsearch_master_data_nodes]
	    node01
	    node02
	    node03

	    [elasticsearch_data_nodes]
7. Create install-elasticsearch.yml or 
   copy /my/gitrepos/ansible-elasticsearch/install-elasticsearch.yml.sample into /my/playbooks/elasticsearch
   and modify parameters that might require modifications 

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
            cluster.name: "custom-cluster",
            discovery.zen.ping.unicast.hosts: "node01, node02, node03",
            network.host: "_eth0_, , _local_",
            http.port: 9200,
            transport.tcp.port: 9300,
            node.data: true,
            node.master: true,
            bootstrap.memory_lock: true,
            http.port: "{{es_api_port}}",
            transport.tcp.port:  "{{es_transport_port}}",
            }
        }
        vars:

        ansible_user: labadmin
        es_instance_name: "es1"
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

        ln -s /my/gitrepos/ansible-role-kibana /my/playbooks/kibana
    
5. cd to playbook

	    cd /my/playbooks/kibana
	
6. create hosts file indicating where master and data nodes are running

	    [kibana]
	    node01
	    node02
	    node03

	    
7.  copy /my/gitrepos/ansible-role-kibana/install-kibana.yml.sample into /my/playbooks/kibana
    should look like this
    

      ```      
     - hosts: kibana
     roles:
       - kibana

      ```   
        
8. Adjust few parameters in /my/playbooks/kibana/defaults/main.yml if required.     

      ```
      ---
      major_kibana_version: "5.x"

      kibana_server_port: 5601

      # specify if playbook should open firewall port
      kibana_listen_external: true

      # interface where kibana will respond to external request
      kibana_interface: "eno16777984"

      kibana_elasticsearch_url: "http://localhost:9200"

      kibana_elasticsearch_requestTimeout: 30000

      
      kibana_logging_dest: /var/log/kibana/kibana.log
      ```
      
8. run kibana playbook on ansible01

	    ansible-playbook -i host install-kibana.yml --ask-become
    
9. Point a browser to *node01:9601* or *node02:9601* or *node03:9601*

Kibana interface should be coming.

    



