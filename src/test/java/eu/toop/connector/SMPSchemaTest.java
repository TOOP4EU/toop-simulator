package eu.toop.connector;

import com.helger.commons.collection.impl.ICommonsList;
import com.helger.peppolid.simple.doctype.SimpleDocumentTypeIdentifier;
import com.helger.peppolid.simple.participant.SimpleParticipantIdentifier;
import com.helger.peppolid.simple.process.SimpleProcessIdentifier;
import eu.toop.connector.api.r2d2.IR2D2Endpoint;
import eu.toop.simulator.mock.XMLBasedEPProvider;
import eu.toop.simulator.util.JAXBUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class SMPSchemaTest {

  private static XMLBasedEPProvider xmlBasedEPProvider;

  @BeforeClass
  public static void init() {
    xmlBasedEPProvider = new XMLBasedEPProvider();
  }

  @Test
  public void parseBusinessCards() {
    eu.toop.schema.smp.SmpDataType smpDataType = JAXBUtil.parseFileOrResource("smp-data.xml",
        eu.toop.schema.smp.ObjectFactory.class);
    System.out.println(smpDataType.getServicegroup().size());
  }

  @Test
  public void queryEndpoint1() {

    ICommonsList<IR2D2Endpoint> endpoints = xmlBasedEPProvider.getEndpoints("LOG",
        new SimpleParticipantIdentifier("iso6523-actorid-upis", "1000:test"),
        new SimpleDocumentTypeIdentifier("toop-doctypeid-qns", "urn:eu:toop:ns:dataexchange-1p10::Request##urn:eu.toop.request.registeredorganization::1.10"),
        new SimpleProcessIdentifier("toop-procid-agreement", "urn:eu.toop.process.datarequestresponse"),
        "bdxr-transport-ebms3-as4-v1p0",
        null
    );

    System.out.println(endpoints.size());

    Assert.assertEquals(1, endpoints.size());
  }

  @Test
  public void queryEndpoint2() {

    ICommonsList<IR2D2Endpoint> endpoints = xmlBasedEPProvider.getEndpoints("LOG",
        new SimpleParticipantIdentifier("iso6523-actorid-upis", "1000:test"),
        new SimpleDocumentTypeIdentifier("toop-doctypeid-qns", "urn:eu:toop:ns:dataexchange-1p10::Response##urn:eu.toop.response.registeredorganization::1.10"),
        new SimpleProcessIdentifier("toop-procid-agreement", "urn:eu.toop.process.datarequestresponse"),
        "bdxr-transport-ebms3-as4-v1p0",
        null
    );

    System.out.println(endpoints.size());
    Assert.assertEquals(1, endpoints.size());

  }
}
