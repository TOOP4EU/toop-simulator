# toop-simulator

The TOOP Infrastructure Simulator provides a platform that mimics the workflow
of the entire TOOP Infrastructure in a local and possibly online environment.
Its purpose is to help DC and DP instances test their systems mostly during
development, prior to joining a production environment.
The simulator contains offline working mocks of the TOOP Directory,
SMP, SMS and Message Exchange Module. The Dir and SMP mocks provide dummy answers to the queries.
While the SMS does a static mapping with respect to the definitions in the file/resource `sms.conf`.
It simulates the end-to-end data flow of the Toop infrastructure

## Workflow

To compile the entire project, run "mvn verify".

To run the application, 
* go to `./target` directory : ```cd ./target```
* copy the `toop-simulator.jks` to the current directory: `cp ../toop-simulator.jks .`
* run the app: `java -jar toop-simulator-{version}.jar`
* Browse http://localhost:8090 to verify that it is working.

Note: You can provide a different http port as below:
```
 java -jar toop-simulator-{version}.jar 9789
```

## Configuration

The simulator is essentially a toop-connector; therefore it contains a `toop-connector.properties` file. 
 
### toop-connector.properties

The properties file is completely the same as the it is supposed to be in `toop-connector`. 
It is located in the root directory of the jar file.
However, the important key-value pairs are listed below:

```properties
# SMM namespace URI to map to
toop.smm.namespaceuri = http://toop.elo/elonia-business-register
# Where is the DP located (for step 2/4)
toop.mp.dp.url = http://localhost:8091/to-dp

# Where is the DC located (for step 4/4)
toop.mp.dc.url = http://localhost:8091/to-dc

# Keystore for signing the ASiC content
toop.keystore.type         = JKS
toop.keystore.path         = toop-simulator.jks
toop.keystore.password     = toop4eu
toop.keystore.key.alias    = toop-simulator
toop.keystore.key.password = toop4eu
```

The other properties don't matter. Since there is no external interface connection, 
please ignore all the TOOP Dir, SMP, SMS, MEM, BDXL related properties.

### sms.conf

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