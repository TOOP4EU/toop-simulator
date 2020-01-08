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
package eu.toop.simulator.mock;


import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.IsSPIImplementation;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.error.level.EErrorLevel;
import eu.toop.commons.dataexchange.v140.TDETOOPRequestType;
import eu.toop.commons.dataexchange.v140.TDETOOPResponseType;
import eu.toop.commons.exchange.AsicReadEntry;
import eu.toop.commons.exchange.ToopMessageBuilder140;
import eu.toop.commons.exchange.ToopRequestWithAttachments140;
import eu.toop.commons.exchange.ToopResponseWithAttachments140;
import eu.toop.connector.api.as4.*;
import eu.toop.kafkaclient.ToopKafkaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Serializable;

/**
 * TOOP {@link IMessageExchangeSPI} implementation using ph-as4.
 *
 * @author yerlibilgin
 */
@IsSPIImplementation
public class MockDCDPMessageExchange implements IMessageExchangeSPI {
  public static final String ID = "mem-mockdcdp";
  private static final Logger LOGGER = LoggerFactory.getLogger(MockDCDPMessageExchange.class);

  private IMEIncomingHandler m_aIncomingHandler;

  public MockDCDPMessageExchange() {
  }

  @Nonnull
  @Nonempty
  public String getID() {
    return ID;
  }

  public void registerIncomingHandler(@Nonnull final ServletContext aServletContext,
                                      @Nonnull final IMEIncomingHandler aIncomingHandler) throws MEException {
    ValueEnforcer.notNull(aServletContext, "ServletContext");
    ValueEnforcer.notNull(aIncomingHandler, "IncomingHandler");
    if (m_aIncomingHandler != null)
      throw new IllegalStateException("Another incoming handler was already registered!");
    m_aIncomingHandler = aIncomingHandler;
  }

  public void sendDCOutgoing(@Nonnull final IMERoutingInformation aRoutingInfo, @Nonnull final MEMessage aMessage) {
    LOGGER.info("SEND DC Outgoing");

    //TCConfig.getSMMMappingNamespaceURIForDP()
    try {
      processRequestResponse(aMessage);
    } catch (Exception ex) {
      throw new MEException(ex);
    }

  }

  public void sendDPOutgoing(@Nonnull final IMERoutingInformation aRoutingInfo,
                             @Nonnull final MEMessage aMessage) throws MEException {
    LOGGER.info("SEND DP Outgoing");
    try {
      processRequestResponse(aMessage);
    } catch (Exception ex) {
      throw new MEException(ex);
    }
  }



  public void processRequestResponse(@Nonnull MEMessage aMessage) throws IOException {
    // Always use response, because it is the super set of request and
    // response
    final MEPayload aPayload = aMessage.head();
    if (aPayload != null) {
      // Extract from ASiC
      final ICommonsList<AsicReadEntry> aAttachments = new CommonsArrayList<>();
      final Serializable aMsg = ToopMessageBuilder140.parseRequestOrResponse(aPayload.getData().getInputStream(), aAttachments::add);

      // Response before Request because it is derived from Request!
      if (aMsg instanceof TDETOOPResponseType) {
        // This is the way from DP back to DC; we're in DC incoming mode
        final ToopResponseWithAttachments140 aResponse = new ToopResponseWithAttachments140((TDETOOPResponseType) aMsg,
            aAttachments);
        m_aIncomingHandler.handleIncomingResponse(aResponse);
      } else if (aMsg instanceof TDETOOPRequestType) {
        // This is the way from DC to DP; we're in DP incoming mode
        final ToopRequestWithAttachments140 aRequest = new ToopRequestWithAttachments140((TDETOOPRequestType) aMsg,
            aAttachments);
        m_aIncomingHandler.handleIncomingRequest(aRequest);
      } else
        ToopKafkaClient.send(EErrorLevel.ERROR, () -> "Unsupported Message: " + aMsg);
    } else
      ToopKafkaClient.send(EErrorLevel.WARN, () -> "MEMessage contains no payload: " + aMessage);
  }


  public void shutdown(@Nonnull final ServletContext aServletContext) {
  }


  @Override
  public String toString() {
    return "MockDCDPMessageExchange: Connector to DC-DP direct message submitter";
  }
}
