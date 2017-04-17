ECS-Dashboard
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

Supported Kibana Reports

1. Report 1 - Top 100 User Report
2. Report 2 - Detailed Object Report
3. Report 3 - Summary Report: Total GB by namespace, bucket and Node protocol usage  
4. Report 4 - Multi Version File / Object Report
5. Report 5 - Filename / ObjectName Report (Search report) 

[Dashboard Screenshots] (doc/dashboard-screenshots.md)

# Compilation

[Compilation Guide] (doc/compilation.md)

# Installation

[Installation Guide] (doc/installation.md)



## Usage Instructions

[User Guide] (doc/user-guide.md)


## Future

Improvements to the code structure and efficiency may come in the future.

## Contribution

Create a fork of the project into your own repository. Make all your necessary changes and create a pull request with a description on what was added or removed and details explaining the changes in lines of code. If approved, project owners will merge it.

Licensing
---------

ECS-Dashboard is freely distributed under the <a href="http://emccode.github.io/sampledocs/LICENSE">MIT License</a>. See LICENSE for details.

Support
-------
Please file bugs and issues on the Github issues page for this project. This is to help keep track and document everything related to this repo. For general discussions and further support you can join the [EMC {code} Community slack channel](http://community.emccode.com/). Lastly, for questions asked on [Stackoverflow.com](https://stackoverflow.com) please tag them with **EMC**. The code and documentation are released with no warranties or SLAs and are intended to be supported through a community driven process.
