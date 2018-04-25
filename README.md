[![Build Status](https://travis-ci.org/dubin555/SQLScheduler.svg?branch=master)](https://travis-ci.org/dubin555/SQLScheduler)
![](https://img.shields.io/badge/language-java-orange.svg)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](https://github.com/dubin555/SQLSchdduler/master/LICENSE)
# SQLScheduler
Scheduler for running command on timer.
![Arch for SQLScheduler](https://github.com/dubin555/SQLScheduler/blob/master/png/SQLSchedulerArch.png)
## Install
### Requirement
* Apache Phoenix
* MySQL
* Java 8

### Compile
```bash
mvn clean package
```

### Change the config file
* MySQL setting
* Concurrent setting

### Main Class
* web.WebApi

### API
#### Create a job
Post request to start a job to /job
Currently, only `Shell` or `Apache Phoenix SQL` are supported.
* Define the variable `${variable_name}`, you can use it later.
* Chain the jobs.
e.g. 
1. We define a task which first running at 15.p.m., run every 5 minutes,
2. Get the last date.
3. select the count from some table where date = yesterday.
4. upsert the result into some other table.
```json
{
	"starttime":900,
	"freq":5,
	"category":"shell_test",
	"tasks":[
	{
		"jobType":"shell",
		"command":"date -v-1d +%F",
		"outputs":["yesterday"]
	},
	{
		"jobType":"phoenix",
		"jdbc":"jdbc:phoenix:zk1:2181",
		"sql":"select count(1) as countN from TABLE_TEST where date = '${yesterday}';",
		"outputs":["countN"]
	},
	{
		"jobType":"phoenix",
		"jdbc":"jdbc:phoenix:zk1:2181",
		"sql":"upsert into RESULT_TEST values('${yesterday}_${countN}','${countN}','${yesterday}')",
		"outputs":[]
	}
	]
}
```
It will be scheduled to run as you want.

For more usage, you may get a lot to do.
like:
* Monitor some database.
* Refresh some setting.
* Put some data into Apache Kafka, if Kafka Actor is implemented.
#### Supported URI
| URI        |  Method           |  detail |
| ------------- |:-------------:| -----:|
| /jobs      |Get | Return all the accepted jobs |
| /job      | Post      |   Post a json into it, return a jobId. |
| job/jobId | Get      |    Get the job with jobId |
| job/jobId | Delete      |    Delete the job with jobId |
| job/category | Get      |    Get the jobs with the category |
| job/jobId | Delete      |    Delete the jobs with the category |

### ToDo
- [ ] Akka to Akka Remote/Cluster for HA
- [ ] support more job type
- [ ] More Unit test