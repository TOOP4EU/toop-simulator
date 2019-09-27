/**
 * Copyright (C) 2018-2019 toop.eu
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.toop.simulator;

import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.simple.participant.SimpleParticipantIdentifier;
import com.helger.photon.jetty.JettyRunner;
import com.helger.photon.jetty.JettyStarter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.impl.ConfigImpl;
import eu.toop.commander.Command;
import eu.toop.commander.ToopCommanderMain;
import eu.toop.connector.api.TCConfig;
import eu.toop.connector.api.r2d2.IR2D2Endpoint;
import eu.toop.connector.api.r2d2.R2D2Endpoint;
import eu.toop.connector.api.r2d2.R2D2EndpointProviderConstant;
import eu.toop.connector.api.r2d2.R2D2ParticipantIDProviderConstant;
import eu.toop.connector.app.mp.MPConfig;
import eu.toop.simulator.mock.MultiNsSMMConceptProvider;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
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

  static IParticipantIdentifier dummyParticipantIdentifier;

  static IR2D2Endpoint dummyR2D2Endpoint;

  /**
   * Arguments:
   * No args: If no arguments are provided, the sim works in a sole mode (i.e. no dc and do dp, only toop connector) <br/>
   * [-mode 0/1/2]: The Working Mode. If 0: Sole, 1: DC, 2: DP
   * [-dcPort PORT]: Toop commander DC Port (DC simulated, dcURL ignored) - default: 25800 <br/>
   * [-dcURL "URL"]: Only used when -dcPort not provided, ie. don't simulate DC and expect an external URL (another DC) <br/>
   * [-dpPort PORT]: Toop commander DC Port (DC simulated, dcURL ignored) - default: 25802 <br/>
   * [-dpURL "URL"]: Only used when -dcPort not provided, ie. don't simulate DC and expect an external URL (another DC) <br/>
   * [-simPort PORT]: Optional port for the simulator (i.e. the connector port for both DC and DP) default: 25801 <br/>
   */
  public static void main(String[] args) throws Exception {

    prepareSimulator();

    int dcPort = 25800;
    int dpPort = 25802;
    int simPort = 25801;
    String dcURL = "http://localhost:" + dcPort + "/to-dc";
    String dpURL = "http://localhost:" + dpPort + "/to-dp";
    int mode = 0; //sole

    final int SOLE = 0;
    final int DC = 1;
    final int DP = 2;

    boolean dcURLUserSet = false;
    boolean dpURLUserSet = false;

    if (args.length > 0) {
      Command command = Command.parse(Arrays.asList(args), false);

      if (command.hasOption("simPort")) {
        simPort = Integer.parseInt(command.getArguments("simPort").get(0));
      }

      if (command.hasOption("dcPort")) {
        dcPort = Integer.parseInt(command.getArguments("dcPort").get(0));
      }

      if (command.hasOption("dpPort")) {
        dpPort = Integer.parseInt(command.getArguments("dpPort").get(0));
      }

      if (command.hasOption("dcURL")) {
        dcURL = command.getArguments("dcURL").get(0);
        dcURLUserSet = true;
      }

      if (command.hasOption("dpURL")) {
        dpURL = command.getArguments("dpURL").get(0);
        dpURLUserSet = true;
      }

      if (command.hasOption("mode")) {
        mode = Integer.parseInt(command.getArguments("mode").get(0));
      }
    }


    if (!dcURLUserSet)
      dcURL = "http://localhost:" + dcPort + "/to-dc";

    if (!dpURLUserSet)
      dpURL = "http://localhost:" + dpPort + "/to-dp";


    TCConfig.setMPToopInterfaceDCOverrideUrl(dcURL);
    TCConfig.setMPToopInterfaceDPOverrideUrl(dpURL);

    final Object serverLock = new Object();

    Thread simulatorThread = runJetty(serverLock, simPort);

    //enter the commander mode
    if (mode == DP)
      System.setProperty("CLI_ENABLED", "false");


    System.setProperty("DC_PORT", dcPort + "");
    System.setProperty("DC_ENABLED", mode == DC ? "true" : "false");
    System.setProperty("DP_PORT", dpPort + "");
    System.setProperty("DP_ENABLED", mode == DP ? "true" : "false");
    System.setProperty("FROM_DC_HOST", "localhost");
    System.setProperty("FROM_DC_PORT", simPort + "");
    System.setProperty("FROM_DP_HOST", "localhost");
    System.setProperty("FROM_DP_PORT", simPort + "");


    ConfigImpl.reloadSystemPropertiesConfig();

    synchronized (serverLock) {
      //wait for the server to come up
      serverLock.wait();
    }

    ToopCommanderMain.main(args);

    simulatorThread.join();

    return;
  }

  public static Thread runJetty(final Object serverLock, final int simPort) {

    Thread simulatorThread = new Thread(() -> {
      try {
        final JettyStarter js = new JettyStarter(ToopSimulatorMain.class) {
          @Override
          protected void onServerStarted(@Nonnull Server aServer) {
            synchronized (serverLock) {
              serverLock.notify();
            }
          }
        }.setPort(simPort)
            .setStopPort(simPort + 100)
            .setSessionCookieName("TOOP_TC_SESSION")
            .setContainerIncludeJarPattern(JettyStarter.CONTAINER_INCLUDE_JAR_PATTERN_ALL);
        js.run();

      } catch (Exception ex) {
        throw new IllegalStateException(ex.getMessage(), ex);
      }
    });

    //start the simulator
    simulatorThread.start();
    return simulatorThread;
  }

  private static void prepareSimulator() throws Exception {
    initializeDummyValues();

    prepareDirectorySimulator();

    prepareSMPSimulator();

    prepareSMSSimulator();
  }

  private static void initializeDummyValues() throws Exception {

    dummyParticipantIdentifier = new SimpleParticipantIdentifier("dummyscheme", "dummyvalue") {
      @Override
      public String toString() {
        return "Dummy participant identifier: " + this.getScheme() + ":" + this.getValue();
      }
    };

    InputStream crtStream = ToopSimulatorMain.class.getResourceAsStream("/sample.x509");
    X509Certificate x509 = (X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(crtStream);
    dummyR2D2Endpoint = new R2D2Endpoint(dummyParticipantIdentifier, "http", "http://dummyhttpurl", x509) {
      @Override
      public String toString() {
        return "Dummy R2D2Endpoint: " + this.getEndpointURL();
      }
    };
  }

  private static void prepareSMSSimulator() {
    LOGGER.debug("Preparing SMS Simulator");

    Config config;

    //first check if we have a file nameds sms.conf and if exists, load it first

    File file = new File("sms.conf");
    if (file.exists()) {
      LOGGER.info("Loading semantic mappings from the file \"sms.conf\"");
      config = ConfigFactory.parseFile(file).resolve();
    } else {
      LOGGER.info("Loading semantic mappings from the resource \"sms.conf\"");
      config = ConfigFactory.load("sms").resolve();
    }

    List<Map<String, Object>> mappings = (List<Map<String, Object>>) config.getAnyRef("Mappings");

    //iterate over every mapping, create an inverse of it as well, and add both to the MultiNSSmm...
    //this can be simplified by helper objects

    Map<String, Map<String, Map<String, String>>> crazyMap = new HashMap<>();

    for (Map<String, Object> mapping : mappings) {

      Map<String, String> conceptMap = new HashMap<>();
      Map<String, String> inverseConceptMap = new HashMap<>();

      String sourceNS = (String) mapping.get("sourceNS");
      String targetNS = (String) mapping.get("targetNS");
      Map<String, String> concepts = (Map<String, String>) mapping.get("concepts");

      for (String key : concepts.keySet()) {
        String value = concepts.get(key);
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

  private static void populateMapping(Map<String, Map<String, Map<String, String>>> conversionMap,
                                      Map<String, String> conceptMap, String sourceNS, String targetNS) {
    Map<String, Map<String, String>> nsMap;
    if (conversionMap.containsKey(sourceNS)) {
      nsMap = conversionMap.get(sourceNS);
    } else {
      nsMap = new HashMap<>();
      conversionMap.put(sourceNS, nsMap);
    }

    if (nsMap.containsKey(targetNS)) {
      throw new IllegalStateException(sourceNS + "->" + targetNS + " mapping already defined");
    }

    nsMap.put(targetNS, conceptMap);
  }

  private static void prepareSMPSimulator() throws CertificateException {
    R2D2EndpointProviderConstant mockEndpointProvider = new R2D2EndpointProviderConstant(dummyR2D2Endpoint);
    MPConfig.setEndpointProvider(mockEndpointProvider);
  }

  private static void prepareDirectorySimulator() {
    R2D2ParticipantIDProviderConstant constantIDProvider = new R2D2ParticipantIDProviderConstant(dummyParticipantIdentifier);
    MPConfig.setParticipantIDProvider(constantIDProvider);
  }
}
