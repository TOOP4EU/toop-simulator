/**
 * Copyright (C) 2018-2020 toop.eu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.toop.connector;

import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.peppolid.IParticipantIdentifier;
import eu.toop.connector.api.TCIdentifierFactory;
import eu.toop.connector.api.r2d2.IR2D2Endpoint;
import eu.toop.simulator.ToopSimulatorResources;
import eu.toop.simulator.mock.DiscoveryProvider;
import eu.toop.simulator.schema.discovery.ObjectFactory;
import eu.toop.simulator.schema.discovery.ServiceMatadataListType;
import eu.toop.simulator.util.JAXBUtil;
import org.junit.Assert;
import org.junit.Test;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceMetadataType;
import org.w3c.dom.Node;

public class DiscoveryTest {
  @Test
  public void parseXml() {
    ServiceMatadataListType type = JAXBUtil.parseURL(ToopSimulatorResources.getDiscoveryDataResURL(), ObjectFactory.class);
    type.getCountryAwareServiceMetadata().forEach(countryAwareServiceMetadataType -> {
      System.out.println(countryAwareServiceMetadataType.getCountryCode());

      countryAwareServiceMetadataType.getServiceMetadata().stream().map(ServiceMetadataType::getServiceInformation).forEach(serviceInformationType -> {
        serviceInformationType.getProcessList().getProcess().forEach(processType -> {
          processType.getServiceEndpointList().getEndpoint().forEach(endpointType -> {
            System.out.println(endpointType.getEndpointURI());

            if (endpointType.getExtension().size() > 0) {
              endpointType.getExtension().forEach(extensionType -> {
                System.out.println(extensionType.getAny());
                if (extensionType.getAny() != null) {
                  Node any = (Node) extensionType.getAny();


                  System.out.println(any.getNodeName());
                  System.out.println(any.getTextContent());

                }
              });
            }
          });
        });
      });
    });
  }

  @Test
  public void queryEndpoint1() {

    ICommonsList<IR2D2Endpoint> endpoints = DiscoveryProvider.getInstance().getEndpoints("LOG",
        TCIdentifierFactory.INSTANCE_TC.createParticipantIdentifier("iso6523-actorid-upis", "9915:test3"),
        TCIdentifierFactory.INSTANCE_TC.createDocumentTypeIdentifier("toop-docid", "maritime:ShipCertificate:request"),
        TCIdentifierFactory.INSTANCE_TC.createProcessIdentifier("toop-procid", "toop-request"),
        "bdxr-transport-ebms3-as4-v1p0",
        null
    );

    System.out.println(endpoints.size());

    Assert.assertEquals(1, endpoints.size());
  }

  @Test
  public void queryDir() {

    ICommonsSet<IParticipantIdentifier> allParticipantIDs = DiscoveryProvider.getInstance().getAllParticipantIDs("LOG",
        "AX",
        TCIdentifierFactory.INSTANCE_TC.createDocumentTypeIdentifier("toop-docid", "maritime:ShipCertificate:request"),
        null
    );

    System.out.println(allParticipantIDs.size());
    Assert.assertEquals(1, allParticipantIDs.size());

  }
}
