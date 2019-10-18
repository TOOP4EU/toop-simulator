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

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import eu.toop.commander.util.CommanderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The utility class for reading the toop-simulator.conf file/classpath resource.
 * <p>
 * If a file with name "toop-simulator.conf" exists in the current directory, then it
 * is read, otherwise, the classpath is checked for /toop-sumlator.conf and if it exists
 * its read.
 * <p>
 * If none of the above paths are valid, then an Exception is thrown.
 *
 * @author yerlibilgin
 */
public class SimulatorConfig {
  /**
   * Logger instance
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(SimulatorConfig.class);

  /**
   * Simulation mode.
   */
  public static final SimulationMode mode;

  /**
   * The port that the /to-dc endpoint will be published on. Ignored if mode != DC
   */
  public static final int dcPort;

  /**
   * The port that the /to-dp endpoint will be published on. Ignored if  mode != DP
   */
  public static final int dpPort;

  /**
   * the http endpoint for an actual /to-dc (iff it is not simulated)
   */
  public static final String dcURL;

  /**
   * the http endpoint for an actual /to-dp (iff it is not simulated)
   */
  public static final String dpURL;

  /**
   * The port that the connector HTTP server will be published on
   */
  public static final int connectorPort;

  static {
    //check if the file toop-simulator.conf exists, and load it,
    //otherwise go for classpath resource
    String pathName = "toop-simulator.conf";

    Config conf = CommanderUtil.resolveConfiguration(pathName)
        .withFallback(ConfigFactory.systemProperties())
        .resolve();

    try {
      mode = SimulationMode.valueOf(conf.getString("toop-simulator.mode"));
    } catch (IllegalArgumentException ex) {
      LOGGER.error(ex.getMessage(), ex);
      throw ex;
    }
    dcPort = conf.getInt("toop-simulator.dcPort");
    dpPort = conf.getInt("toop-simulator.dpPort");

    dcURL = conf.getString("toop-simulator.dcURL");
    dpURL = conf.getString("toop-simulator.dpURL");

    connectorPort = conf.getInt("toop-simulator.connectorPort");
  }
}
