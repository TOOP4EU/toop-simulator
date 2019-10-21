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
if it doesn't exist. This is a [HOCON](https://github.com/lightbend/config/blob/master/HOCON.md) file that allows configuring the simulation modes, http ports and endpoints.

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

Windows users, please see 
[**Setting Environment variables on Windows**](https://www.shellhacks.com/windows-set-environment-variable-cmd-powershell/).

### Advanced Configuration


The simulator mimics the TOOP Directory, SMP, SMS and Message Exchange Modules. 
The Directory and SMP simulators provide discovery service by consuming the file
`discovery-data.xml`, SMS does a static mapping with respect to the definitions 
in the file `sms.conf`. These files are created in the current directory with default
values if they don't exist.


#### Configuring discovery

You may want to add discovery information for a country and participant id. In this case, you need to
modify the `discovery-data.xml` file. The sample structure of this file is as follows:

````xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>


<sim:ServiceMetadataList xmlns:bdxr="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05"
                         xmlns:sim="http://eu/toop/simulator/schema/discovery">


  <sim:CountryAwareServiceMetadata countryCode="GQ">
    <sim:ServiceMetadata>
      <bdxr:ServiceInformation>
        <bdxr:ParticipantIdentifier scheme="iso6523-actorid-upis">9999:elonia-dev</bdxr:ParticipantIdentifier>
        <bdxr:DocumentIdentifier scheme="toop-doctypeid-qns">
          urn:eu:toop:ns:dataexchange-1p40::Request##urn:eu.toop.request.registeredorganization::1.40
        </bdxr:DocumentIdentifier>
        <bdxr:ProcessList>
          <bdxr:Process>
            <bdxr:ProcessIdentifier scheme="toop-procid-agreement">urn:eu.toop.process.datarequestresponse</bdxr:ProcessIdentifier>
            <bdxr:ServiceEndpointList>
              <bdxr:Endpoint transportProfile="bdxr-transport-ebms3-as4-v1p0">
                <bdxr:EndpointURI>http://gw-elonia.acc.exchange.toop.eu/holodeckb2b/as4</bdxr:EndpointURI>
                <bdxr:RequireBusinessLevelSignature>false</bdxr:RequireBusinessLevelSignature>
                <bdxr:Certificate>
                  MIIEvTCCAqWgAwIBAgICEBEwDQYJKoZIhvcNAQELBQAwVzELMAkGA1UEBhMCRVUxDTALBgNVBAoMBFRPT1AxDTALBgNVBAsMBENDVEYxKjAoBgNVBAMMIVRPT1AgUElMT1RTIFRFU1QgQUNDRVNTIFBPSU5UUyBDQTAeFw0xOTA0MjQxODMyMzBaFw0yMTA0MjMxODMyMzBaMEwxCzAJBgNVBAYTAkVVMQ0wCwYDVQQKDARUT09QMQ0wCwYDVQQLDARDQ1RGMR8wHQYDVQQDDBZnYXRld2F5LmVsb25pYS50b29wLmV1MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzBbWuqRILiMKRxJkHxYiZFYw1pRGplx4HtlTY1+RxjHT/mTQDv1cQ4PZ8RbPSLUl3dHzeddMBURX8ssGIz+UvCaA8bChCBvReFVtEWU0CZTb+eWqyYzgByTXdu5fEFsDiw55SHixo/c0zvqLMU9Qcatroa9wy4IUV+YCSiRudjSdNqKh3uoAixKSgPf7XRA6GLXQBxb/QJZQ5gIMRAN5Ql3YwsAai0ZQtrTHJ07sDq6061uYL/JU4WOuXRDomZsIDH0lUakChdHf0UdPPWT6bDauq8T4d499oo33NqRyPneuijvEwZrV/cEgpWSFWKETRFxqJdCZMFpuONJ4V3aZWwIDAQABo4GdMIGaMAkGA1UdEwQCMAAwEQYJYIZIAYb4QgEBBAQDAgQwMCoGCWCGSAGG+EIBDQQdFhtUT09QIFBpbG90IFRlc3QgQ2VydGlmaWNhdGUwDgYDVR0PAQH/BAQDAgXgMB0GA1UdDgQWBBRp1HcAq76F4SH3JzmS8Nzenk1sYzAfBgNVHSMEGDAWgBQVzruhPMDANGRod8/RFMMvwVGnwTANBgkqhkiG9w0BAQsFAAOCAgEARQPqia+/iULw07a9P59WWxmGqX+8OWJdN4cQNODNW3xL6z7RQkBGvQNPFnyTLAy9wjr8/ZmLxYoD754UTXQOXqHl+6pPGZarVpyCpP3CIAxkDUkehrYznmdYHgtcNHvX9L2Nx4s10HWidR+RLdzbH74Re2XoB7EFs0SKdPg5KAhEU5yiKUTrg/82ltyJuagnBjrS7nJguXlGmLPXc4n9WE38MS2tnDs/1hSpiUkM60G0Ut773hFHeT4ld3O3gU1/g6sra3tsy+cpBIe41mdNBiCVlwA7TwD1Tto9sq7DUXjsSzUoRylydEED1ulRN54aCKflDV9HMZpn71Ylv2z7frqb30NKHYMCCKqbNLcJNzvgWA0kU6EuCqPlGJgrkJrgPKXLYiWXh6Z62d0s/f6k+uJGOMPsh7yl+gWSsRHnw1zw9TNlz5KGTvcalkhOdDI+8isjzGMW6e6Skh+XsJxOlgNRNVXxE3Ar0448gOuKbw3Skh28Ddr2amd6uWEg9hWWUVb2GtQBT+2OxFi8Lk96GwGCgVDG2S7VPfKHiGPapDw6K3DVnrsRnSuzf8FnYT8tMNoLVGmvspkf4RO3eAx5HF0mx3i78whACBcP/4HmAtAStK1hh1fXe0niGuJP+6A4leAUItfSWGM6q3o2lIfSEBz1sW2Kb8zULWMmsskUITk=
                </bdxr:Certificate>
                <bdxr:ServiceDescription>Test AP</bdxr:ServiceDescription>
                <bdxr:TechnicalContactUrl>philip@helger.com</bdxr:TechnicalContactUrl>
              </bdxr:Endpoint>
            </bdxr:ServiceEndpointList>
          </bdxr:Process>
        </bdxr:ProcessList>
      </bdxr:ServiceInformation>
    </sim:ServiceMetadata>

    <sim:ServiceMetadata>...
    </sim:ServiceMetadata>

  </sim:CountryAwareServiceMetadata>

  <sim:CountryAwareServiceMetadata countryCode="PF">
   ...
  </sim:CountryAwareServiceMetadata>

  ...

</sim:ServiceMetadataList>
````

The file contains a root `ServiceMetadataList` that can contain multiple `CountryAwareServiceMetadata` elements.
A `CountryAwareServiceMetadata` element has an attribute called `countryCode` that takes 
a two letter country code. 

A `CountryAwareServiceMetadata` element may contain multiple `ServiceMetadata` elements. 
The [`ServiceMetadata`](http://docs.oasis-open.org/bdxr/bdx-smp/v1.0/os/bdx-smp-v1.0-os.html#_Toc490131026) type
has been inherited from the [XSD Schema](http://docs.oasis-open.org/bdxr/bdx-smp/v1.0/os/schemas/bdx-smp-201605.xsd)
 of the [OASIS Service Metadata Publishing (SMP) Version 1.0](http://docs.oasis-open.org/bdxr/bdx-smp/v1.0/bdx-smp-v1.0.html).

**NOTE**: For ease of use, the simulator supports reading the certificates from external files by providing their file
system paths in an extension called `:CertFileName` as given in the below example.

```xml
<bdxr:Endpoint transportProfile="bdxr-transport-ebms3-as4-v1p0">
    <bdxr:EndpointURI>https://www.as4gateway.com/msh</bdxr:EndpointURI>
    <!-- we are using cert file, so this binary value is dummy. See the extension-->
    <bdxr:Certificate>MIIC9jCC</bdxr:Certificate>
    <bdxr:ServiceDescription>RO 1 Endpoint</bdxr:ServiceDescription>
    <bdxr:TechnicalContactUrl>Jerry</bdxr:TechnicalContactUrl>
    <!-- In order not to violate the SMP schema we have to provide
         our certficate file name in an extension -->
    <bdxr:Extension>
      <!-- Put the certificate path in this element (DER or PEM) file -->
      <sim:CertFileName>path/to/my/certificate.der</sim:CertFileName>
    </bdxr:Extension>
</bdxr:Endpoint>
```
You **have to** provide a dummy base64 string in the `bdxr:Certificate` element for successfull schema parsing, which will be ignored
by the simulator. The certificate file may be a 
[DER](https://en.wikipedia.org/wiki/X.690#DER_encoding) or a 
[PEM](https://en.wikipedia.org/wiki/Privacy-Enhanced_Mail) file.

#### Semantic mapping

The simulator reads the `sms.conf` file that contains all the information about the semantic mapping of
the concepts from and to the toop namespace (`http://toop.eu/registered-organization`). 
The default contents are the predefined mappings for `elonia` and `freedonia`. 

`sms.conf` is a [HOCON](https://github.com/lightbend/config/blob/master/HOCON.md) config file that contains a `list` of `mapping groups` 
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

**Note**: The value of `toop.smm.namespaceuri` in the `toop-connector.properties` is important to be set to the namespace of
the DP that the simulator will send the asic requests to. The simulator includes a toop-connector.properties file within 
the jar file. Please see 
[TOOP Connector Deployment and Configuration Guide](http://wiki.ds.unipi.gr/display/TOOP/TOOP+Connector+Deployment+and+Configuration+Guide)
for more information on configuring `toop-connector.properties` 

