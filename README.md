# toop-simulator
[![Build Status](https://api.travis-ci.org/TOOP4EU/toop-simulator.svg?branch=master)](https://travis-ci.org/TOOP4EU/toop-simulator)

**Latest Release:** [0.10.6](https://repo1.maven.org/maven2/eu/toop/toop-simulator/0.10.6/)



* [Introduction](#Introduction)
* [Getting started](#Getting-started)
* [Simulation Modes](#Simulation-Modes)
* [Configuration](#Configuration)
  * [Basic configuration](#basic-configuration)
  * [Advanced Configuration](#Advanced-Configuration)
     * [Configuring discovery](#configuring-discovery)
     * [Semantic mapping](#semantic-mapping)


## Introduction
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


## Simulation Modes
Toop simulator supports three working modes; namely `DC`, `SOLE` and `DP` (_default_).
A [toop-commander](https://github.com/TOOP4EU/toop-commander) which is activated only
in `DC` and `DP` modes is also bundled inside.

### DC Mode

* **As JVM  ARG:** -DSIM_MODE=DC<br/>
* **As ENV variable:** export SIM_MODE=DC<br/>
* **via toop-simulator.conf:** toop-simulator.mode="DC"


In `DC` mode, toop-simulator launches a toop-commander which provides a command line
interface and a `DC` endpoint connected directly to the simulator. In `DC` mode, 
you may provide a URL for an external DP via the `DP_URL` to communicate with. 
The `DP_URL` variable can be omitted, in which case the default DP url 
(`http://localhost:8082/to-dp`) will be used.

The architecture view for the `DC` mode is given below.

<br/>
<br/>
<br/>

![DC MODE OVERVIEW](./docs/DC-mode-overview.svg?sanitize=true "DC Mode Overview")


<br/>
<br/>
<br/>

To launch the simulator in `DC` mode, run the following command
```
# using JVM ARGS
java -DSIM_MODE=DC -DDP_URL="http://some.dp/to-dp" -jar toop-simulator-0.10.6-bundle.jar

# or alternatively set env variables
export SIM_MODE=DC
export DP_URL="http://some.dp/to-dp"
java -jar toop-simulator-0.10.6-bundle.jar

```

The simulator will launch a `/to-dc` endpoint as well as the toop-connector endpoints (`/from-dc`, `/from-dp`...) and 
a command line interface that you can send requests from (please see [toop-commander](https://github.com/TOOP4EU/toop-commander)).


### SOLE Mode

* **As JVM  ARG:** -DSIM_MODE=SOLE<br/>
* **As ENV variable:** export SIM_MODE=SOLE<br/>
* **via toop-simulator.conf:** toop-simulator.mode="SOLE"

In `SOLE` mode, toop-simulator launches a toop-connector without a `DC` or `DP` simulation and no CLI. 

If the `DC_URL` and `DP_URL` variable are omitted, then the default values (`http://localhost:8080/to-dc` 
and `http://localhost:8082/to-dp`) will be used.

<br/>
<br/>
<br/>

![SOLE MODE OVERVIEW](./docs/SOLE-mode-overview.svg?sanitize=true "SOLE Mode Overview")

<br/>
<br/>
<br/>

Assuming that you have some external DC and DP urls (e.g. `http://memberstate.a:8080/to-dc` and `http://memberstate.b:8080/to-dp`),
to launch the simulator in `SOLE` mode, run the following command
```
# using JVM ARGS
java -DSIM_MODE=SOLE -DDC_URL="http://memberstate.a:8080/to-dc" \
      -DDP_URL="http://memberstate.b:8080/to-dp" \
      -jar toop-simulator-0.10.6-bundle.jar

# or alternatively set env variables
export SIM_MODE=SOLE
export DC_URL="http://memberstate.a:8080/to-dc"
export DP_URL="http://memberstate.b:8080/to-dp"
java -jar toop-simulator-0.10.6-bundle.jar

```

The simulator will launch toop-connector endpoints (`/from-dc`, `/from-dp`...) and wait for requests and responses 
from the configured DC and DP endpoints.

### DP Mode


* By avoiding the SIM_MODE argument (DP mode is default)
* **As JVM  ARG:** -DSIM_MODE=DP<br/>
* **As ENV variable:** export SIM_MODE=DP<br/>
* **via toop-simulator.conf:** toop-simulator.mode="DP"


In `DP` mode, toop-simulator launches a toop-connector and a toop-commander with a DP that is directly connected
to the simulator and NO CLI (because the responses from toop-commander's DP interface are automatic).
The architecture view for the `DP` mode is given below.


<br/>
<br/>
<br/>

![DP MODE OVERVIEW](./docs/DP-mode-overview.svg?sanitize=true "DP Mode Overview")

<br/>
<br/>
<br/>


To run the simulator in `DP` mode, run the following command
```

# by omitting the SIM_MODE variable (DP is default)
# and DC_URL (which will be defaulted to http://localhost:8080/to-dc
java -jar toop-simulator-0.10.6-bundle.jar


# using JVM ARGS
java -DSIM_MODE=DP -DDP_URL="http://some.dp/to-dp" -jar toop-simulator-0.10.6-bundle.jar

# or alternatively set env variables
export SIM_MODE=DP
export DC_URL="http://some.dc/to-dc"
java -jar toop-simulator-0.10.6-bundle.jar

```

The `DC_URL` variable can be omitted, in which case the default DC url (`http://localhost:8080/to-dc`) will be used.


## Configuration
### Basic configuration
When the simulator is started, it creates a file named `toop-simulator.conf` in the current directory
if it doesn't exist. This is a HOCON file that allows configuring the simulation modes, http ports and endpoints.

**Sample `toop-simulator.conf`**
```hocon
toop-simulator {
  #the simulator mode, one of SOLE DC, DP
  mode = "DP"
  mode = ${?SIM_MODE}

  #Used only in DC Mode
  dcPort = 8080
  dcPort = ${?DC_PORT}
 
  #Used only in DP mode 
  dpPort = 8082
  dpPort = ${?DP_PORT}

  #used only if mode != DC
  dcURL = "http://localhost:8080/to-dc"
  dcURL = ${?DC_URL}

  #used only if mode != DP
  dpURL = "http://localhost:8082/to-dp"
  dpURL = ${?DP_URL}

  #The simulator will run the connector on this port
  connectorPort = 8081
  connectorPort = ${?CONNECTOR_PORT}
}
```

You don't have to directly edit this file (unless you want to persist your settings). For every configuration
item there is an ENV or JVM_ARG alternative. For example you can run the toop-connector on a different port by 
explicitly changing the `toop-simulator.connectorPort`, or setting `CONNECTOR_PORT` variable via ENV/JVM_ARG:
```shell script
java -DCONNECTOR_PORT=8091 toop-simulator-0.10.6-bundle.jar
```

### Advanced Configuration


The simulator mimics the TOOP Directory, SMP, SMS and Message Exchange Modules. The Directory and SMP simulators provide 
discovery service by using the file [`discovery-data.xml`](#discovery-dataxml), 
SMS does a static mapping with respect to the definitions in the file [`sms.conf`](#smsconf).
It simulates the end-to-end data flow of the Toop infrastructure. 
These files are created in the current directory with default values if they don't exist.


#### Configuring discovery

#### Semantic mapping

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

