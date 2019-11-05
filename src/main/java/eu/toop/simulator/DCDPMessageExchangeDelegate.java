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

import com.helger.commons.annotation.Nonempty;
import eu.toop.connector.api.as4.*;
import eu.toop.connector.mem.def.spi.DefaultMessageExchangeSPI;
import eu.toop.simulator.mock.MockDCDPMessageExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;

/**
 * <p>
 * This class delegates to an underlying IMessageExchangeSPI implementation
 * that is configured with respect to the <code>toop-simulator.mockGateway</code> configuration.<br>
 * If the <code>toop-simulator.mockGateway==true</code> then the gateway communication simulated
 * and an instance of {@link eu.toop.simulator.mock.MockDCDPMessageExchange} is used,
 * <br>otherwise (<code>toop-simulator.mockGateway==true</code>) then the message exchange communication
 * is not simulated and an instance of {@link eu.toop.connector.mem.def.spi.DefaultMessageExchangeSPI} is used.
 * </p>
 * <p>In the latter case, the communication with a gateway is configured with the key
 * <code>toop.mem.as4.endpoint</code> and all
 * <code>the toop.mem.as4.*</code> configurations (see toop-connector.properties) become significant.</p>
 * <p>Another important point for the latter case is that; the discovery data will have to be realistic as well.
 * You should not use fake certificates, and the CName property of the x509 certificate has to be
 * compatible with the gateway party id. Please see more on toop wiki</p>
 *
 * @author yerlibilgin
 */
public class DCDPMessageExchangeDelegate implements IMessageExchangeSPI {

  private static final Logger LOGGER = LoggerFactory.getLogger(DCDPMessageExchangeDelegate.class);
  private final IMessageExchangeSPI underlyingSPI;

  /**
   * Create a new DCDPMessageExchangeDelegate. Check the configuration and decide to
   * mock or use the default SPI for the IMessageExchangeSPI.
   */
  public DCDPMessageExchangeDelegate(){
    if (SimulatorConfig.mockGateway){
      LOGGER.info("Using " + MockDCDPMessageExchange.class.getName());
      underlyingSPI = new MockDCDPMessageExchange();
    } else {
      LOGGER.info("Using " + DefaultMessageExchangeSPI.class.getName());
      underlyingSPI = new DefaultMessageExchangeSPI();
    }
  }

  /**
   * Since the id mem-default is already taken, we cannot provide it for the second time.
   * So we provide the simulator value {@value eu.toop.simulator.mock.MockDCDPMessageExchange#ID}
   * @return the id of this message exchange SPI implementation
   */
  @Override
  @Nonempty
  @Nonnull
  public String getID() {
    return MockDCDPMessageExchange.ID;
  }

  @Override
  public void registerIncomingHandler(@Nonnull ServletContext servletContext, @Nonnull IMEIncomingHandler imeIncomingHandler) throws MEException {
    underlyingSPI.registerIncomingHandler(servletContext, imeIncomingHandler);
  }

  @Override
  public void sendDCOutgoing(@Nonnull IMERoutingInformation imeRoutingInformation, @Nonnull MEMessage meMessage) throws MEException {
    underlyingSPI.sendDCOutgoing(imeRoutingInformation, meMessage);
  }

  @Override
  public void sendDPOutgoing(@Nonnull IMERoutingInformation imeRoutingInformation, @Nonnull MEMessage meMessage) throws MEException {
    underlyingSPI.sendDPOutgoing(imeRoutingInformation, meMessage);
  }

  @Override
  public void shutdown(@Nonnull ServletContext servletContext) {
    underlyingSPI.shutdown(servletContext);
  }
}
