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

import eu.toop.commander.ToopCommanderMain;
import eu.toop.connector.api.TCConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The program entry point
 *
 * @author muhammet yildiz
 */
public class ToopSimulatorMainDC {
  /**
   * The Logger instance
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ToopSimulatorMainDC.class);

  public static void main(final String[] args) throws Exception {

    //we are in dc mode.
    // - set the commander from-dc and simulator to-dc interfaces to constant values
    // - let the user set the to-dp URL on the simulator, and from-dp interfaces.
    TCConfig.setMPToopInterfaceDCOverrideUrl("http://localhost:25800/to-dc");
    TCConfig.setMPToopInterfaceDPOverrideUrl(args[0]);
    Thread simulatorThread = new Thread(() -> {
      try {
        ToopSimulatorMain.main(new String[]{"25801"});
      } catch (Exception ex){
        throw new IllegalStateException(ex.getMessage(), ex);
      }
    });

    //start the simulator
    simulatorThread.start();

    //enter the commander mode
    ToopCommanderMain.main(args);

    simulatorThread.join();
  }

}
