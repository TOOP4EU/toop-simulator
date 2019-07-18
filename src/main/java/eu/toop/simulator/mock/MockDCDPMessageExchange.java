/**
 * Copyright (C) 2019 toop.eu
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
import eu.toop.connector.api.as4.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;

/**
 * TOOP {@link IMessageExchangeSPI} implementation using ph-as4.
 *
 * @author muhammet yildiz
 */
@IsSPIImplementation
public class MockDCDPMessageExchange implements IMessageExchangeSPI
{
  public static final String ID = "mem-mockdcdp";
  private static final Logger LOGGER = LoggerFactory.getLogger (MockDCDPMessageExchange.class);

  private IMEIncomingHandler m_aIncomingHandler;

  public MockDCDPMessageExchange()
  {}

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return ID;
  }

  public void registerIncomingHandler (@Nonnull final ServletContext aServletContext,
                                       @Nonnull final IMEIncomingHandler aIncomingHandler) throws MEException
  {
    ValueEnforcer.notNull (aServletContext, "ServletContext");
    ValueEnforcer.notNull (aIncomingHandler, "IncomingHandler");
    if (m_aIncomingHandler != null)
      throw new IllegalStateException ("Another incoming handler was already registered!");
    m_aIncomingHandler = aIncomingHandler;

    //final MEMDelegate aDelegate = MEMDelegate.getInstance ();
//
    //aDelegate.registerNotificationHandler (aRelayResult -> {
    //  // more to come
    //  ToopKafkaClient.send (EErrorLevel.INFO,
    //      () -> "Notification[" +
    //          aRelayResult.getErrorCode () +
    //          "]: " +
    //          aRelayResult.getDescription ());
    //});
//
    //aDelegate.registerSubmissionResultHandler (aRelayResult -> {
    //  // more to come
    //  ToopKafkaClient.send (EErrorLevel.INFO,
    //      () -> "SubmissionResult[" +
    //          aRelayResult.getErrorCode () +
    //          "]: " +
    //          aRelayResult.getDescription ());
    //});
//
    //// Register the AS4 handler needed
    //aDelegate.registerMessageHandler (aMEMessage -> {
    //  // Always use response, because it is the super set of request and
    //  // response
    //  final MEPayload aPayload = aMEMessage.head ();
    //  if (aPayload != null)
    //  {
    //    // Extract from ASiC
    //    final ICommonsList<AsicReadEntry> aAttachments = new CommonsArrayList<>();
    //    final Serializable aMsg = ToopMessageBuilder140.parseRequestOrResponse (aPayload.getData ().getInputStream (), aAttachments::add);
//
    //    // Response before Request because it is derived from Request!
    //    if (aMsg instanceof TDETOOPResponseType)
    //    {
    //      // This is the way from DP back to DC; we're in DC incoming mode
    //      final ToopResponseWithAttachments140 aResponse = new ToopResponseWithAttachments140 ((TDETOOPResponseType) aMsg,
    //          aAttachments);
    //      m_aIncomingHandler.handleIncomingResponse (aResponse);
    //    }
    //    else
    //    if (aMsg instanceof TDETOOPRequestType)
    //    {
    //      // This is the way from DC to DP; we're in DP incoming mode
    //      final ToopRequestWithAttachments140 aRequest = new ToopRequestWithAttachments140 ((TDETOOPRequestType) aMsg,
    //          aAttachments);
    //      m_aIncomingHandler.handleIncomingRequest (aRequest);
    //    }
    //    else
    //      ToopKafkaClient.send (EErrorLevel.ERROR, () -> "Unsuspported Message: " + aMsg);
    //  }
    //  else
    //    ToopKafkaClient.send (EErrorLevel.WARN, () -> "MEMessage contains no payload: " + aMEMessage);
    //});
  }

  public void sendDCOutgoing (@Nonnull final IMERoutingInformation aRoutingInfo, @Nonnull final MEMessage aMessage)
  {
    //final GatewayRoutingMetadata aGRM = new GatewayRoutingMetadata (aRoutingInfo.getSenderID ().getURIEncoded (),
    //    aRoutingInfo.getDocumentTypeID ().getURIEncoded (),
    //    aRoutingInfo.getProcessID ().getURIEncoded (),
    //    aRoutingInfo.getEndpointURL (),
    //    aRoutingInfo.getCertificate (),
    //    EActingSide.DC);
    //MEMDelegate.getInstance ().sendMessage (aGRM, aMessage);


    LOGGER.info("SEND DC Outgoing");
  }

  public void sendDPOutgoing (@Nonnull final IMERoutingInformation aRoutingInfo,
                              @Nonnull final MEMessage aMessage) throws MEException
  {
    //final GatewayRoutingMetadata aGRM = new GatewayRoutingMetadata (aRoutingInfo.getSenderID ().getURIEncoded (),
    //    aRoutingInfo.getDocumentTypeID ().getURIEncoded (),
    //    aRoutingInfo.getProcessID ().getURIEncoded (),
    //    aRoutingInfo.getEndpointURL (),
    //    aRoutingInfo.getCertificate (),
    //    EActingSide.DP);
    //MEMDelegate.getInstance ().sendMessage (aGRM, aMessage);


    LOGGER.info("SEND DP Outgoing");
  }

  public void shutdown (@Nonnull final ServletContext aServletContext)
  {}


  @Override
  public String toString() {
    return "MockDCDPMessageExchange: Connector to DC-DP direct message submitter";
  }
}
