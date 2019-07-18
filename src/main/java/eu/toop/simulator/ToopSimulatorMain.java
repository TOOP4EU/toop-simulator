/**
 * Copyright (C) 2018-2019 toop.eu
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
package eu.toop.simulator;

import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.simple.participant.SimpleParticipantIdentifier;
import com.helger.photon.jetty.JettyStarter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import eu.toop.connector.api.r2d2.IR2D2Endpoint;
import eu.toop.connector.api.r2d2.R2D2Endpoint;
import eu.toop.connector.api.r2d2.R2D2EndpointProviderConstant;
import eu.toop.connector.api.r2d2.R2D2ParticipantIDProviderConstant;
import eu.toop.connector.api.smm.SMMConceptProviderMapBased;
import eu.toop.connector.app.mp.MPConfig;
import eu.toop.simulator.mock.MultiNsSMMConceptProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * The program entry point
 *
 * @author muhammet yildiz
 */
public class ToopSimulatorMain {
  /**
   * The Logger instance
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ToopSimulatorMain.class);
  public static void main(String[] args) throws Exception {
    prepareDirectorySimulator();

    prepareSMPSimulator();

    prepareSMSSimulator();

    final JettyStarter js = new JettyStarter (ToopSimulatorMain.class).setPort (8090)
          .setStopPort (9090)
          .setSessionCookieName ("TOOP_TC_SESSION")
          .setContainerIncludeJarPattern (JettyStarter.CONTAINER_INCLUDE_JAR_PATTERN_ALL);
      js.run ();
  }

  private static void prepareSMSSimulator() {
    LOGGER.debug("Preparing SMS Simulator");

    Map<String, String> map = new HashMap<>();

    Config config = ConfigFactory.load("smm").resolve();


    List<Map<String, Object>> mappings = (List<Map<String, Object>>) config.getAnyRef("Mappings");

    //iterate over every mapping, create an inverse of it as well, and add both to the MultiNSSmm...
    //this can be simplified by helper objects

    Map<String, Map<String, Map<String, String>>> crazyMap = new HashMap<>();

    for(Map<String, Object> mapping : mappings){

      Map<String, String> conceptMap = new HashMap<>();
      Map<String, String> inverseConceptMap = new HashMap<>();

      String sourceNS = (String) mapping.get("sourceNS");
      String targetNS = (String) mapping.get("targetNS");
      System.out.println(sourceNS);
      System.out.println(targetNS);
      Map<String, String> concepts = (Map<String, String>) mapping.get("concepts");

      for(String key : concepts.keySet()){
        String value = concepts.get(key);
        System.out.println("\t" + key + " " + value);
        conceptMap.put(key, value);
        inverseConceptMap.put(value, key);
      }

      //forward mapping
      populateMapping(crazyMap, conceptMap, sourceNS, targetNS);

      //backwards mappind
      populateMapping(crazyMap, inverseConceptMap, targetNS, sourceNS);
    }

    MPConfig.setSMMConceptProvider(new MultiNsSMMConceptProvider(crazyMap));
  }

  private static void populateMapping(Map<String, Map<String, Map<String, String>>> crazyMap, Map<String, String> conceptMap, String sourceNS, String targetNS) {
    Map<String, Map<String, String>> nsMap;
    if(crazyMap.containsKey(sourceNS)){
      nsMap = crazyMap.get(sourceNS);
    } else {
      nsMap = new HashMap<>();
      crazyMap.put(sourceNS, nsMap);
    }

    if(nsMap.containsKey(targetNS)){
      throw new IllegalStateException(sourceNS + "->" + targetNS + " mapping already defined");
    }

    nsMap.put(targetNS, conceptMap);
  }

  static IParticipantIdentifier aPi = new SimpleParticipantIdentifier("dummyscheme", "dummyvalue");
  private static void prepareSMPSimulator() throws CertificateException {

    IR2D2Endpoint aEndpoint = new R2D2Endpoint(aPi, "http", "http://lololo",

        (X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(

            ToopSimulatorMain.class.getResourceAsStream("/out.der")
        ));
    R2D2EndpointProviderConstant mockEndpointProvider = new R2D2EndpointProviderConstant(aEndpoint);


    MPConfig.setEndpointProvider(mockEndpointProvider);
  }

  public static void prepareDirectorySimulator() {

    R2D2ParticipantIDProviderConstant constantIDProvider = new R2D2ParticipantIDProviderConstant(aPi);
    MPConfig.setParticipantIDProvider(constantIDProvider);
  }
}
