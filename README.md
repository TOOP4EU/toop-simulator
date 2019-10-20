# toop-simulator
[![Build Status](https://api.travis-ci.org/TOOP4EU/toop-simulator.svg?branch=master)](https://travis-ci.org/TOOP4EU/toop-simulator)

**Latest Release:** [0.10.6](https://repo1.maven.org/maven2/eu/toop/toop-simulator/0.10.6/)



* [Overview](#Overview)
* [Getting started](#Getting started)
* [Toop Simulator Architecture](#Toop Simulator Architecture)
* [Simulation Modes](#Simulation Modes)
* [Configuration](#Configuration)
* [Basic configuration](#Basic configuration)
  * [toop-simulator.conf](#toop-simulator.conf)
* [Advanced Configuration](#Advanced Configuration)
  * [toop-connector.properties](#toop-connector.properties)
  * [discovery-data.xml](#discovery-data.xml)
  * [sms.conf](#sms.conf)

## Overview
The TOOP Infrastructure Simulator provides a platform that mimics the workflow
of the entire TOOP Infrastructure in a local or online environment. Its purpose is to assist DC and DP instances 
for testing their systems during development, prior to joining a production environment.

## Getting started
* Download the jar bundle from https://repo1.maven.org/maven2/eu/toop/toop-simulator/0.10.6/toop-simulator-0.10.6-bundle.jar
* run it as `java -jar toop-simulator-0.10.6-bundle.jar`. This will start the simulator 
in [DP mode](#Simulation Modes) (i.e. it will simulate the toop-connector as well as a DP).
[More on simulator modes](#Simulation Modes)
* The simulator will start http servers on ports 8081 and 8082 for the connector and DP respectively. 
It will assume the existence of and use a `/to-dc` endpoint (i.e. DC) on `http://localhost:8080/to-dc`.
* Now you can,
  * start up your own DC on localhost:8080
  * send requests to the simulator on port `http://localhost:8081/from-dc`
  * and receive responses or errors on port `http://localhost:8080/to-dc`


## Toop Simulator Architecture

![Alt text](./docs/overview.svg?sanitize=true)
<img src="./docs/overview.svg?sanitize=true" />

The simulator mimics the TOOP Directory, SMP, SMS and Message Exchange Modules. The Directory and SMP simulators provide 
discovery service by using the file [`discovery-data.xml`](#discovery\-data.xml), 
SMS does a static mapping with respect to the definitions in the file [`sms.conf`](#sms).
It simulates the end-to-end data flow of the Toop infrastructure. 
These files are created in the current directory with default values if they don't exist.



## Simulation Modes

## Configuration
### Basic configuration
#### toop-simulator.conf

### Advanced Configuration

#### discovery\-data\.xml

#### sms.conf

This is the resource that contains all the information about the semantic mapping of
the concepts from and to the toop namespace (`http://toop.eu/registered-organization`). 
It currently contains the predefined mappings for `elonia` and `freedonia`. 

**Note**: If the file `sms.conf` exists in the working directory of the simulator, it will be loaded;
otherwise the simulator will try to load the resource `sms.conf` that is in the root directory
of the jar file. 

The resource is a typesafe config file that contains a `list` of `mapping groups` 
each containing source and destination namespaces (i.e. `sourceNS` and `targetNS`) 
and a `mapping of concepts`. A fragment from `sms.conf` is given below:

```hocon
Mappings=[
  {
    sourceNS="http://toop.elo/elonia-business-register"
    targetNS="http://toop.eu/registered-organization"
    concepts {
      "EloniaActivityDescription"="activityDescription"
      "EloniaBirthDate"="birthDate"
      "EloniaCapitalType"="capitalType"
      "EloniaCompanyCode"="CompanyCode"
      "EloniaCompanyName"="companyName"
       #... and the rest
    }
  },
  {
     sourceNS="http://toop.fre/freedonia-business-register"
     targetNS="http://toop.eu/registered-organization"
     concepts {
       "FreedoniaActivityDescription"="activityDescription"
       "FreedoniaBirthDate"="birthDate"
       "FreedoniaCapitalType"="capitalType"
       "FreedoniaCompanyCode"="CompanyCode"
        #... and the rest
     }
  }
]

```

You can add as many mappings as possible. You don't need to add the  _inverse_ of the 
mappings as the simulator does this for you (with the current assumption that mappings are one-to-one).

**Note**: The value of `toop.smm.namespaceuri` in the `toop-connector.properties` is very important to be set to the namespace of
the DP that the simulator will send the asic requests to.

