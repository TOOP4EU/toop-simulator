package eu.toop.simulator.mock;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;
import com.helger.peppolid.IDocumentTypeIdentifier;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.security.certificate.CertificateHelper;
import eu.toop.connector.api.r2d2.IR2D2Endpoint;
import eu.toop.connector.api.r2d2.IR2D2EndpointProvider;
import eu.toop.connector.api.r2d2.IR2D2ErrorHandler;
import eu.toop.connector.api.r2d2.R2D2Endpoint;
import eu.toop.schema.smp.EndpointType;
import eu.toop.schema.smp.SmpDataType;
import eu.toop.simulator.util.JAXBUtil;
import org.apache.logging.log4j.core.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * This class represends an end point provider that feeds from the smp.xml data exported from the toop smp server. <br/>
 * The XML format is not standard and that is a TODO for the future releases
 */
public class XMLBasedEPProvider implements IR2D2EndpointProvider {

  private CertificateFactory certificateFactory;
  {
    try {
      certificateFactory = CertificateFactory.getInstance("X509");
    } catch (CertificateException e) {
    }
  }

  /**
   * The Logger instance
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(XMLBasedEPProvider.class);
  private final SmpDataType smpData;

  public XMLBasedEPProvider() {
    LOGGER.debug("Parsing smp-data.xml");
    this.smpData = JAXBUtil.parseFileOrResource("smp-data.xml",
        eu.toop.schema.smp.ObjectFactory.class);
  }

  @Nonnull
  @Override
  public ICommonsList<IR2D2Endpoint> getEndpoints(@Nonnull String sLogPrefix,
                                                  @Nonnull IParticipantIdentifier aRecipientID,
                                                  @Nonnull IDocumentTypeIdentifier aDocumentTypeID,
                                                  @Nonnull IProcessIdentifier aProcessID,
                                                  @Nonnull String sTransportProfileID,
                                                  @Nonnull IR2D2ErrorHandler aErrorHandler) {
    LOGGER.info(sLogPrefix + "received query for \n" +
        "\tparticipant: " + aRecipientID.getURIEncoded() +
        "\tdoctype: " + aDocumentTypeID.getURIEncoded() +
        "\tprocess: " + aProcessID.getURIEncoded() +
        "\ttransport profile: " + sTransportProfileID);


    ICommonsList<IR2D2Endpoint> endpoints = new CommonsArrayList<>();

    smpData.getServicegroup().forEach(servicegroupType -> {
      String participantURI = servicegroupType.getParticipant().getScheme() + "::" + servicegroupType.getParticipant().getValue();

      if (participantURI.equals(aRecipientID.getURIEncoded())) {
        servicegroupType.getServiceinfo().forEach(serviceinfoType -> {
          String docTypeURI = serviceinfoType.getDoctypeidentifier().getScheme() + "::" + serviceinfoType.getDoctypeidentifier().getValue();
          String processURI = serviceinfoType.getProcess().getProcessidentifier().getScheme() + "::" + serviceinfoType.getProcess().getProcessidentifier().getValue();
          EndpointType serviceEndpoint = serviceinfoType.getProcess().getEndpoint();
          if (docTypeURI.equals(aDocumentTypeID.getURIEncoded()) &&
              processURI.equals(aProcessID.getURIEncoded()) &&
              serviceEndpoint.getTransportprofile().equals(sTransportProfileID)) {

            try {
              String certString = serviceEndpoint.getCertificate();
              X509Certificate x509Certificate;
              if (certString.startsWith("file:")) {
                //this is a special case for simulator. One can want to put his retificate as
                //a file name, so that we can parse it from the file.

                String path = new URL(certString).getPath();
                File file = new File(path);
                LOGGER.debug("FULL CERT PATH PARSE: " + file.getAbsolutePath());
                x509Certificate = (X509Certificate) certificateFactory.generateCertificate(new FileInputStream(file));
              } else {
                x509Certificate = (X509Certificate) certificateFactory.generateCertificate(new NonBlockingByteArrayInputStream(certString.getBytes(StandardCharsets.UTF_8)));
              }

              URL url = new URL(serviceEndpoint.getEndpointref());
              endpoints.add(new R2D2Endpoint(aRecipientID,
                  url.getProtocol(),
                  url.toString(),
                  x509Certificate
              ));
            } catch (Exception ex) {
              LOGGER.error(ex.getMessage(), ex);
            }
          }
        });
      }
    });

    LOGGER.debug("SMP found " + endpoints.size() + " matches ");
    return endpoints;
  }
}
