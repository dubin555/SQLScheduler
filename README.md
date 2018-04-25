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

### ToDo
- [ ] Akka to Akka Remote/Cluster
- [ ] support more job type
- [ ] More Unit test