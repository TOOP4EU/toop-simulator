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
import eu.toop.connector.api.TCConfig;
import eu.toop.connector.app.mp.MPConfig;
import eu.toop.simulator.mock.DiscoveryProvider;
import eu.toop.simulator.mock.MultiNsSMMConceptProvider;
import org.eclipse.jetty.server.Server;

import javax.annotation.Nonnull;

/**
 * The program entry point
 *
 * @author muhammet yildiz
 */
public class ToopSimulatorMain {

  /**
   * program entry point
   */
  public static void main(String[] args) throws Exception {

    ToopSimulatorResources.transferResourcesToFileSystem();
    prepareSimulator();


    final SimulationMode simulationMode = SimulatorConfig.mode;


    //Start the simulator in a new thread, and get its thread so that we can wait on it.
    Thread simulatorThread = startSimulator(simulationMode);

    //now prepare and run commander if we are not in SOLE mode
    if (simulationMode != SimulationMode.SOLE) {

      //if we are not dc, then no CLI
      if (simulationMode != SimulationMode.DC)
        System.setProperty("CLI_ENABLED", "false");

      //only one mode (DC or DP) at a time should be available on the commander
      System.setProperty("DC_ENABLED", simulationMode == SimulationMode.DC ? "true" : "false");
      System.setProperty("DP_ENABLED", simulationMode == SimulationMode.DP ? "true" : "false");

      //set up the /to-dc and /to-dp ports on the commander.
      System.setProperty("DC_PORT", SimulatorConfig.dcPort + "");
      System.setProperty("DP_PORT", SimulatorConfig.dpPort + "");

      //we need to make sure that commander knows our /from-dc and /from-dp endpoints,
      //both of the endpoints (/from-dc and /from-dp) on the commander will point to the
      //simulator. We don't have to worry about that because, the commander is here to communicate
      //with our simulator. And only once side at a time (DC or DP) will be enabled,
      //the other side, even if configured, will be ignored (disabled)
      System.setProperty("FROM_DC_HOST", "localhost");
      System.setProperty("FROM_DC_PORT", SimulatorConfig.connectorPort + "");
      System.setProperty("FROM_DP_HOST", "localhost");
      System.setProperty("FROM_DP_PORT", SimulatorConfig.connectorPort + "");

      ConfigImpl.reloadSystemPropertiesConfig();

      //Tip, top. Run the toop commander
      ToopCommanderMain.startCommander();
    }


    //wait for the simulator thread to exit
    simulatorThread.join();
  }

  private static Thread startSimulator(SimulationMode simulationMode) throws InterruptedException {
    String dcURL;
    String dpURL;

    if (simulationMode == SimulationMode.DC) {
      //we are simulating dc, so ignore the config URL and create one on localhost
      dcURL = "http://localhost:" + SimulatorConfig.dcPort + "/to-dc";
    } else {
      //we are not simulating dc, it means we need an actual dc endpoint. get it from config
      dcURL = SimulatorConfig.dcURL;
    }

    if (simulationMode == SimulationMode.DP) {
      //we are simulating dp, so ignore the config URL and create one on localhost
      dpURL = "http://localhost:" + SimulatorConfig.dpPort + "/to-dp";
    } else {
      //we are not simulating dp, it means we need an actual dp endpoint. get it from config
      dpURL = SimulatorConfig.dpURL;
    }

    TCConfig.setMPToopInterfaceDCOverrideUrl(dcURL);
    TCConfig.setMPToopInterfaceDPOverrideUrl(dpURL);

    final Object serverLock = new Object();

    //start jetty
    Thread simulatorThread = runJetty(serverLock, SimulatorConfig.connectorPort);

    synchronized (serverLock) {
      //wait for the server to come up
      serverLock.wait();
    }

    return simulatorThread;
  }

  /**
   * Start simulator server
   * @param serverLock used to notify all the threads waiting on this lock to wake up
   *                   after server start.
   * @param httpPort the port to publish the jetty on
   * @return
   */
  private static Thread runJetty(final Object serverLock, final int httpPort) {

    Thread simulatorThread = new Thread(() -> {
      try {
        final JettyStarter js = new JettyStarter(ToopSimulatorMain.class) {
          @Override
          protected void onServerStarted(@Nonnull Server aServer) {
            synchronized (serverLock) {
              serverLock.notify();
            }
          }
        }.setPort(httpPort)
            .setStopPort(httpPort + 100)
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
