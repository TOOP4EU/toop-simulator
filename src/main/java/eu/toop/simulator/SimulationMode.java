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
package eu.toop.simulator;

/**
 * An enum that represents the working modes of the simulator
 * @author yerlibilgin
 */
public enum SimulationMode {
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