ECS-Dashboard
======================


##Target Deployment

![Target Deployment](https://github.com/carone1/ecs-dashboard/blob/master/doc/images/ECSAnalyticsDeployment.png)

###Assumptions:

1. 1 ECS Cluster with 4 ECS VMs
2. 3 ECS Analytics Hosts running Linux Centos 7
3. CPU 8 or more cores per host
4. 8G or more RAM per host
5. 10 GigE network between hosts
6. Enough storage to hold daily metadata. 2B objects scenario should require 550Gb per day per ElasticSearch node.



It is highly recommended to read ElasticSearch Node documentation to better understand the different roles an ElasticSearch Host can support.

[Elasticsearch Node Roles](https://www.elastic.co/guide/en/elasticsearch/reference/2.3/modules-node.html#data-node)

As indicated in the Elasticsearch documentation see link, we recommend to install three master-eligible nodes with `minimum_master_nodes` set to 2.  In the future, extra data nodes can be added as required. 
  
	"An advantage of splitting the master and data roles between dedicated 
	nodes is that you can have just three master-eligible nodes and set
	`minimum_master_nodes` to 2.  You never have to change this setting,
	no matter how many dedicated data nodes you add to the cluster."
	
#### Extra Assumptions

1. ECS collector program should be installed on all hosts.  It is expected that only one host is scheduled to collect from ECS hosts.
2. ECS cleaner program should be installed on all hosts. It is expected that only one host is scheduled to clean data from the ElasticSearch cluster
3. Issues were encountered trying to use Centos6 for Kibana Eamailer.   Worked sucesfully on Centos 7.



# Installations

## Manual Installations

[Manual Installation] (./manual-installation.md)

## Ansible Installations

[Ansible Installation ] (./ansible-installation.md)

