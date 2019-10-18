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
import eu.toop.commander.ToopCommanderMain;
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
   * An enum that represents the working modes of the simulator
   */
  private enum SimMode{
    /**
     * In this mode the simulator does not contain commander and
     * this does not expose a DC or a DP endpoint, it solely simulates
     * the toop infrastructure
     */
    SOLE,

    /**
     * In this mode the simualtor works with an embedded toop-commander that
     * provides a CLI and a /to-dc endpoint where interaction of the simulator and
     * the DC takes place within the application. In this mode no DP (i.e. no /to-dp)
     * endpoint is provided, thus one should also provide -dpURL with an external URL when
     * of a <code>/to-dp</code> when this mode is set
     */
    DC,

    /**
     * In this mode the simualtor works with an embedded toop-commander that
     * provides a /to-d- endpoint where interaction of the simulator and
     * the DP takes place within the application. In this mode no DC (i.e. no /to-dc)
     * endpoint and no CLI is provided, thus one should also provide -dcURL with an external URL when
     * this mode is set
     */
    DP
  }

  /**
   * The Logger instance
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ToopSimulatorMain.class);

  /**
   * Arguments:
   * <ol>
   *   <li><b>No args: </b>All arguments are optional. If no argument is provided, the simulator works in SOLE mode (i.e. no dc and do dp, only toop connector)</li>
   *   <li><b>[-mode SOLE/DC/DP]: </b>The Working Mode. Default: SOLE</li>
   *   <li><b>[-dcPort PORT]: </b>Toop commander DC Port (DC simulated, dcURL ignored) - default: 25800</li>
   *   <li><b>[-dcURL "URL"]: </b>Only used when -dcPort not provided, ie. don't simulate DC and expect an external URL (another DC)</li>
   *   <li><b>[-dpPort PORT]: </b>Toop commander DP Port (DP simulated, dpURL ignored) - default: 25802</li>
   *   <li><b>[-dpURL "URL"]: </b>Only used when -dpPort not provided, ie. don't simulate DP and expect an external URL (another DP)</li>
   *   <li><b>[-simPort PORT]: </b>Optional port for the simulator (i.e. the connector port for both DC and DP) default: 25801</li>
   * </ol>
   *  <br/>
   */
  public static void main(String[] args) throws Exception {

    prepareSimulator();

    int dcPort = 25800;
    int dpPort = 25802;
    int simPort = 25801;
    String dcURL = "http://localhost:" + dcPort + "/to-dc";
    String dpURL = "http://localhost:" + dpPort + "/to-dp";
    SimMode mode = SimMode.SOLE;

    boolean dcPortSet = false;
    boolean dpPortSet = false;

    if (args.length > 0) {
      CliCommand command = CliCommand.parse(Arrays.asList(args), false);

      if (command.hasOption("simPort")) {
        simPort = Integer.parseInt(command.getArguments("simPort").get(0));
      }

      if (command.hasOption("dcPort")) {
        dcPort = Integer.parseInt(command.getArguments("dcPort").get(0));
        dcPortSet = true;
      }

      if (command.hasOption("dpPort")) {
        dpPort = Integer.parseInt(command.getArguments("dpPort").get(0));
        dpPortSet = true;
      }

      if (command.hasOption("dcURL")) {
        dcURL = command.getArguments("dcURL").get(0);
      }

      if (command.hasOption("dpURL")) {
        dpURL = command.getArguments("dpURL").get(0);
      }

      if (command.hasOption("mode")) {
        mode = SimMode.valueOf(command.getArguments("mode").get(0));
      }
    }

    if (dcPortSet)
      dcURL = "http://localhost:" + dcPort + "/to-dc";

    if (dpPortSet)
      dpURL = "http://localhost:" + dpPort + "/to-dp";


    TCConfig.setMPToopInterfaceDCOverrideUrl(dcURL);
    TCConfig.setMPToopInterfaceDPOverrideUrl(dpURL);

    final Object serverLock = new Object();

    Thread simulatorThread = runJetty(serverLock, simPort);

    synchronized (serverLock) {
      //wait for the server to come up
      serverLock.wait();
    }

    if (mode != SimMode.SOLE) {
      //enter the commander mode
      if (mode == SimMode.DP)
        System.setProperty("CLI_ENABLED", "false");


      System.setProperty("DC_PORT", dcPort + "");
      System.setProperty("DC_ENABLED", mode == SimMode.DC ? "true" : "false");
      System.setProperty("DP_PORT", dpPort + "");
      System.setProperty("DP_ENABLED", mode == SimMode.DP ? "true" : "false");
      System.setProperty("FROM_DC_HOST", "localhost");
      System.setProperty("FROM_DC_PORT", simPort + "");
      System.setProperty("FROM_DP_HOST", "localhost");
      System.setProperty("FROM_DP_PORT", simPort + "");

      ConfigImpl.reloadSystemPropertiesConfig();

      //Run toop commander
      ToopCommanderMain.main(args);
    }


    //wait for the simulator thread to exit
    simulatorThread.join();
  }

  private static Thread runJetty(final Object serverLock, final int simPort) {

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
