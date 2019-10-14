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

import com.helger.photon.jetty.JettyStarter;
import com.typesafe.config.impl.ConfigImpl;
import eu.toop.commons.util.CliCommand;
import eu.toop.connector.api.TCConfig;
import eu.toop.connector.app.mp.MPConfig;
import eu.toop.simulator.mock.DiscoveryProvider;
import eu.toop.simulator.mock.MultiNsSMMConceptProvider;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

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

  /**
   * Arguments:
   * No args: If no arguments are provided, the sim works in a sole mode (i.e. no dc and do dp, only toop connector) <br/>
   * [-mode 0/1/2]: The Working Mode. If 0: Sole, 1: DC, 2: DP. Default: SOLE
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
      CliCommand command = CliCommand.parse(Arrays.asList(args), false);

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

    //check if Toop commander is on classpath (if we are not in SOLE mode)
    Class<?> toopCommanderMainClass = null;
    if (mode != SOLE) {
      try {
        toopCommanderMainClass = Class.forName("eu.toop.commander.ToopCommanderMain", true,
            new URLClassLoader(
                new URL[]{new File("./toop-commander-0.10.6-SNAPSHOT.jar").toURI().toURL()}
            ));
      } catch (Exception ex) {
        LOGGER.error("Toop Commander doesn't exist on classpath. ");
        return;
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

    synchronized (serverLock) {
      //wait for the server to come up
      serverLock.wait();
    }

    if (mode != SOLE) {
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

      //RUN TOOP Commander via reflection.
      final Method main = toopCommanderMainClass.getMethod("main", String[].class);
      System.out.println(main);
      main.invoke(null, (Object) args);

      simulatorThread.join();
    }
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

  /**
   * Prepare three simulators (Directory, SMP and SMS) here
   *
   * @throws Exception
   */
  private static void prepareSimulator() {
    MPConfig.setParticipantIDProvider(DiscoveryProvider.getInstance());
    MPConfig.setEndpointProvider(DiscoveryProvider.getInstance());
    MPConfig.setSMMConceptProvider(new MultiNsSMMConceptProvider());
  }
}
