ECS-Dashbaord
======================
This project provides Custom Kibana Dashboards for EMC's Elastic Cloud Storage (ECS).

# Description

This project is divided in three sub systems:

1. ECS Metadata Collector
2. ElasticSerach Metadata Cleaner
3. Kibana Emailer

The ECS Metadata Collector is responsible to collect metadata of various component stored in EMC Elastic Cloud Storage.  The collector polls remote ECS instances via REST API, and store results into an ElasticSearch cluster.

The ElasticSearch Cleaner is responsible to purge data that was collected > x number days. 
 
The Kibana emailer is responsible to generate email containing Kibana dashboards.  

##Target Deployment

###Assumptions:

1. 1 ECS Cluster with 4 ECS VMs
2. 3 ECS Analytics Hosts running Linux Centos 7
3. CPU 8 or more cores per host
4. 8G or more RAM per host
5. 10 GigE network between hosts
6. Enough storage to hold daily metadata. 2B objects scenario should require 550Gb per day per ElasticSearch node.

![Target Deployment][doc/images/ECSAnalyticsDeployment.png]


It is highly recommended to read ElasticSearch Node documentation to better understand the different roles an ElasticSearch Host can support.

[Elasticsearch Node Roles](https://www.elastic.co/guide/en/elasticsearch/reference/2.3/modules-node.html#data-node)

As indicated in the Elasticsearch documentation see link, we recommend to install three master-eligible nodes with `minimum_master_nodes` set to 2.  In the future, extra data nodes can be added as required. 
  
	"An advantage of splitting the master and data roles between dedicated nodes is 
	that you can have just three master-eligible nodes and set minimum_master_nodes to 2.   
	You never have to change this setting, no matter how many dedicated data nodes you add 
	to the cluster."
	
#### Extra Assumptions

1. ECS collector program should be installed on all hosts.  It is expected that only one host is scheduled to collect from ECS hosts.
2. ECS cleaner program should be installed on all hosts. It is expected that only one host is scheduled to clean data from the ElasticSearch cluster
3. Kibana emailer works better on Ubuntu.  Issues were encountered tyring to use Centos6.   To be verified on Centos 7.

# Installation

[Installatio Guide] (installation.md)



## Usage Instructions

[User Guide] (userguide.md)


## Future

Improvements to the code structure and efficiency may come in the future.

## Contribution

Create a fork of the project into your own reposity. Make all your necessary changes and create a pull request with a description on what was added or removed and details explaining the changes in lines of code. If approved, project owners will merge it.

Licensing
---------

ECS-Dashboard is freely distributed under the <a href="http://emccode.github.io/sampledocs/LICENSE">MIT License</a>. See LICENSE for details.

Support
-------
Please file bugs and issues on the Github issues page for this project. This is to help keep track and document everything related to this repo. For general discussions and further support you can join the [EMC {code} Community slack channel](http://community.emccode.com/). Lastly, for questions asked on [Stackoverflow.com](https://stackoverflow.com) please tag them with **EMC**. The code and documentation are released with no warranties or SLAs and are intended to be supported through a community driven process.
