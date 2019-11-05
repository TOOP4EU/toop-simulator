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
package eu.toop.simulator.mock;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;
import com.helger.peppolid.IDocumentTypeIdentifier;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.IProcessIdentifier;
import eu.toop.connector.api.TCIdentifierFactory;
import eu.toop.connector.api.r2d2.*;
import eu.toop.simulator.ToopSimulatorResources;
import eu.toop.simulator.schema.discovery.ObjectFactory;
import eu.toop.simulator.schema.discovery.ServiceMatadataListType;
import eu.toop.simulator.util.JAXBUtil;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class plays the role of both a directory and an SMP server. It reads its contents
 * from the file or classpath resource 'disovery-data.xml' and creates a map in the memory
 * to provide query results.
 *
 * @author yerlibilgin
 */
public class DiscoveryProvider implements IR2D2ParticipantIDProvider, IR2D2EndpointProvider {

  /**
   * The Logger instance
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveryProvider.class);

  private static final DiscoveryProvider instance = new DiscoveryProvider();

  public static DiscoveryProvider getInstance() {
    return instance;
  }


  private final ServiceMatadataListType serviceMatadataListType;


  /**
   * The directory database
   * The key is the query params <code>getAllParticipantIDs</code> call
   */
  private Map<DIRQuery, ICommonsSet<IParticipantIdentifier>> directoryMap;

  /**
   * The SMP database
   * The key is the query params <code>getEndpoints</code> call
   */
  private Map<SMPQuery, ICommonsList<IR2D2Endpoint>> smpMap;


  /*
   * Used in case no matches are found
   */
  private static final CommonsHashSet<IParticipantIdentifier> EMPTY_PID_SET = new CommonsHashSet<>();
  private static final CommonsArrayList<IR2D2Endpoint> EMPTY_R2D2LIST = new CommonsArrayList<>();


  private CertificateFactory certificateFactory;

  {
    try {
      certificateFactory = CertificateFactory.getInstance("X509");
    } catch (CertificateException e) {
    }
  }

  private DiscoveryProvider() {
    //parse the file or resource discovery-data.xml
    serviceMatadataListType = JAXBUtil.parseURL(ToopSimulatorResources.getDiscoveryDataResURL(), ObjectFactory.class);

    //build a database for beter performance


    //step 1. build a directory map ( CountryCode, Doctype -- > List<participant id> )
    buildDirectoryMap();

    //step 2. build an SMP map (participant,doctype,transportprofile,procid) --> r2d2ep)
    buildSMPMap();
  }

  @Nonnull
  @Override
  public ICommonsSet<IParticipantIdentifier> getAllParticipantIDs(@Nonnull String sLogPrefix, @Nonnull String sCountryCode, @Nonnull IDocumentTypeIdentifier aDocumentTypeID, @Nonnull IR2D2ErrorHandler aErrorHandler) {

    LOGGER.info(sLogPrefix + "Query directory for [countryCode: " + sCountryCode +
        ", doctype: " + aDocumentTypeID.getURIEncoded() + "]");

    DIRQuery dirQuery = new DIRQuery(sCountryCode, aDocumentTypeID);

    if (directoryMap.containsKey(dirQuery)) {
      return directoryMap.get(dirQuery);
    }

    return EMPTY_PID_SET;
  }


  @Nonnull
  @Override
  public ICommonsList<IR2D2Endpoint> getEndpoints(@Nonnull String sLogPrefix, @Nonnull IParticipantIdentifier aRecipientID, @Nonnull IDocumentTypeIdentifier aDocumentTypeID, @Nonnull IProcessIdentifier aProcessID, @Nonnull String sTransportProfileID, @Nonnull IR2D2ErrorHandler aErrorHandler) {
    LOGGER.info(sLogPrefix + "received getEndpoints query for \n" +
        "\tparticipant: " + aRecipientID.getURIEncoded() +
        "\tdoctype: " + aDocumentTypeID.getURIEncoded() +
        "\tprocess: " + aProcessID.getURIEncoded() +
        "\ttransport profile: " + sTransportProfileID);

    ICommonsList<IR2D2Endpoint> result;

    SMPQuery smpQuery = new SMPQuery(aRecipientID, aDocumentTypeID, aProcessID, sTransportProfileID);

    if (smpMap.containsKey(smpQuery)) {
      return smpMap.get(smpQuery);
    }

    return EMPTY_R2D2LIST;
  }


  private void buildDirectoryMap() {

    LOGGER.info("Building directory map");
    // ok now that we have parsed the XML document and we will be serving queries with country code and doc type,
    // then no need to hold the root type and long loop at each query.
    // instead, we can create a mapping as [countrycode, doctype] ---> IParticipantIdentifier at once.
    // so that the queries (getAllParticipantIDs) perform faster.

    directoryMap = new HashMap<>();

    serviceMatadataListType.getCountryAwareServiceMetadata().forEach(countryAwareServiceMetadataType -> {

      String countrycode = countryAwareServiceMetadataType.getCountryCode();

      countryAwareServiceMetadataType.getServiceMetadata().forEach(serviceMetadataType -> {
        DocumentIdentifierType documentIdentifier = serviceMetadataType.getServiceInformation().getDocumentIdentifier();
        IDocumentTypeIdentifier docID = TCIdentifierFactory.INSTANCE_TC.createDocumentTypeIdentifier(documentIdentifier.getScheme(), documentIdentifier.getValue());


        DIRQuery dirQuery = new DIRQuery(countrycode, docID);

        ICommonsSet<IParticipantIdentifier> identifierSet;
        if (directoryMap.containsKey(dirQuery)) {
          identifierSet = directoryMap.get(dirQuery);
        } else {
          identifierSet = new CommonsHashSet<>();
          directoryMap.put(dirQuery, identifierSet);
        }

        //now add a new participant identifier to this set.
        //TODO: vulnerable, do null check
        identifierSet.add(TCIdentifierFactory.INSTANCE.createParticipantIdentifier(serviceMetadataType.getServiceInformation().getParticipantIdentifier().getScheme(),
            serviceMetadataType.getServiceInformation().getParticipantIdentifier().getValue()));
      });
    });
  }


  /**
   * Build a map of SMPQuery --> List&lt;IR2D2Participant&gt;
   */
  private void buildSMPMap() {
    LOGGER.info("Building SMP Map");

    smpMap = new HashMap<>();

    serviceMatadataListType.getCountryAwareServiceMetadata().forEach(country -> {

      country.getServiceMetadata().stream().map(ServiceMetadataType::getServiceInformation).forEach(serviceInformation -> {

        ParticipantIdentifierType participantIdentifier = serviceInformation.getParticipantIdentifier();
        IParticipantIdentifier participantID = TCIdentifierFactory.INSTANCE_TC.createParticipantIdentifier(participantIdentifier.getScheme(),
            participantIdentifier.getValue());

        DocumentIdentifierType documentIdentifier = serviceInformation.getDocumentIdentifier();
        IDocumentTypeIdentifier documentTypeID = TCIdentifierFactory.INSTANCE_TC.createDocumentTypeIdentifier(documentIdentifier.getScheme(), documentIdentifier.getValue());

        serviceInformation.getProcessList().getProcess().forEach(processType -> {
          ProcessIdentifierType processIdentifier = processType.getProcessIdentifier();
          IProcessIdentifier procID = TCIdentifierFactory.INSTANCE_TC.createProcessIdentifier(processIdentifier.getScheme(), processIdentifier.getValue());
          processType.getServiceEndpointList().getEndpoint().forEach(endpointType -> {
            String transportProfile = endpointType.getTransportProfile();

            try {
              final X509Certificate x509Certificate[] = new X509Certificate[1];
              //check if we have an extension to set the certificate from file
              endpointType.getExtension().forEach(extensionType -> {
                try {
                  if (extensionType.getAny() != null) {
                    Node any = (Node) extensionType.getAny();

                    if (any.getLocalName().equals("CertFileName")) {
                      //this is a special case for simulator. One can want to put his retificate as
                      //a file name, so that we can parse it from the file.

                      InputStream stream;
                      String path = any.getTextContent();
                      File file = new File(path);
                      if (file.exists()) {
                        stream = new FileInputStream(file);
                      } else {
                        //try possibly from resource root dir
                        file = new File(ToopSimulatorResources.SIMULATOR_CONFIG_DIR + path);
                        if (file.exists()) {
                          stream = new FileInputStream(file);
                        } else {
                          //file doesn't exist, try resource
                          stream = DiscoveryProvider.this.getClass().getResourceAsStream("/" + path);
                          if (stream == null) {
                            throw new IllegalStateException("A file or a classpath resource with name " + path + " was not found");
                          }
                        }
                      }
                      LOGGER.debug("FULL CERT PATH PARSE: " + file.getAbsolutePath());
                      x509Certificate[0] = (X509Certificate) certificateFactory.generateCertificate(stream);
                    }

                  }
                } catch (Exception ex) {
                  throw new IllegalStateException(ex.getMessage(), ex);
                }
              });

              //do we have a certificate ?
              if (x509Certificate[0] == null) {
                //no we don't, so load it form the default smp cert element
                x509Certificate[0] = (X509Certificate) certificateFactory.generateCertificate(new NonBlockingByteArrayInputStream(endpointType.getCertificate()));
              }

              ICommonsList<IR2D2Endpoint> list;

              SMPQuery smpQuery = new SMPQuery(participantID, documentTypeID, procID, transportProfile);
              if (smpMap.containsKey(smpQuery)) {
                list = smpMap.get(smpQuery);
              } else {
                list = new CommonsArrayList<>();
                smpMap.put(smpQuery, list);
              }

              list.add(new R2D2Endpoint(participantID, endpointType.getTransportProfile(), endpointType.getEndpointURI(),
                  x509Certificate[0]));

            } catch (Exception ex) {
              LOGGER.error(ex.getMessage(), ex);
            }
          });
        });
      });

    });
  }

  /**
   * Reflectively creates a URI as <code>scheme::value</code>
   * by calling the metods "getScheme" and "getValue"
   * over the given object via reflection.
   *
   * @param object the object which exposes methods "getScheme" "getValue"
   * @return the created URI
   * @implNote This method uses reflection and is for internal use only, please don't use it for other purposes, or use it with care.
   */
  private static String createIdentifierURI(Object object) {
    Class<?> clazz = object.getClass();
    try {
      Method getScheme = clazz.getMethod("getScheme");
      Method getValue = clazz.getMethod("getValue");

      return getScheme.invoke(object) + "::" + getValue.invoke(object);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      throw new IllegalStateException("Couldn't build an Identifier from the passed object of type " + clazz);
    }
  }

  /**
   * A placeholder for a simple smp query, to play the KEY role in the SMP map.
   */
  private class SMPQuery {
    private IParticipantIdentifier aRecipientID;
    private IDocumentTypeIdentifier aDocumentTypeID;
    private IProcessIdentifier aProcessID;
    private String sTransportProfileID;

    public SMPQuery(IParticipantIdentifier aRecipientID, IDocumentTypeIdentifier aDocumentTypeID, IProcessIdentifier aProcessID, String sTransportProfileID) {
      this.aRecipientID = aRecipientID;
      this.aDocumentTypeID = aDocumentTypeID;
      this.aProcessID = aProcessID;
      this.sTransportProfileID = sTransportProfileID;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      SMPQuery smpQuery = (SMPQuery) o;
      return aRecipientID.equals(smpQuery.aRecipientID) &&
          aDocumentTypeID.equals(smpQuery.aDocumentTypeID) &&
          aProcessID.equals(smpQuery.aProcessID) &&
          sTransportProfileID.equals(smpQuery.sTransportProfileID);
    }

    @Override
    public int hashCode() {
      return Objects.hash(aRecipientID, aDocumentTypeID, aProcessID, sTransportProfileID);
    }
  }

  /**
   * A placeholder for a simple dir query, to play the KEY role in the directory map.
   */
  private class DIRQuery {
    private String sCountryCode;
    private IDocumentTypeIdentifier aDocumentTypeID;

    public DIRQuery(String sCountryCode, IDocumentTypeIdentifier aDocumentTypeID) {
      this.sCountryCode = sCountryCode;
      this.aDocumentTypeID = aDocumentTypeID;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      DIRQuery dirQuery = (DIRQuery) o;
      return sCountryCode.equals(dirQuery.sCountryCode) &&
          aDocumentTypeID.equals(dirQuery.aDocumentTypeID);
    }

    @Override
    public int hashCode() {
      return Objects.hash(sCountryCode, aDocumentTypeID);
    }
  }

}
