package eu.toop.simulator.mock;

import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.peppolid.IDocumentTypeIdentifier;
import com.helger.peppolid.IParticipantIdentifier;
import eu.toop.connector.api.TCIdentifierFactory;
import eu.toop.connector.api.r2d2.IR2D2ErrorHandler;
import eu.toop.connector.api.r2d2.IR2D2ParticipantIDProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLBasedParticipantIDProvider implements IR2D2ParticipantIDProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(XMLBasedParticipantIDProvider.class);
  /**
   * Used in case no matches are found
   */
  private static final CommonsHashSet<IParticipantIdentifier> EMPTY_SET = new CommonsHashSet<>();

  /**
   * The database :)
   * The first key is the country code,
   * the second key is the doc type id as URI
   */
  private final Map<String, Map<String, ICommonsSet<IParticipantIdentifier>>> directoryMap;

  /**
   * Read the file or resource `discovery.conf` and build the directory entries
   */
  public XMLBasedParticipantIDProvider() {
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(eu.toop.directory.schema.ObjectFactory.class);

      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

      eu.toop.directory.schema.RootType rootType = ((JAXBElement<eu.toop.directory.schema.RootType>) jaxbUnmarshaller.unmarshal(this.getClass().getResourceAsStream("/directory-business-cards.xml"))).getValue();

      // ok now that we have parsed the XML document and we will be serving queries with country code and doc type,
      // then no need to hold the root type and long loop at each query.
      // instead, we can create a mapping as [countrycode, doctype] ---> IParticipantIdentifier at once.
      // so that the queries (getAllParticipantIDs) perform faster.

      directoryMap = new HashMap<>();

      List<String> _tmpDocTypes = new ArrayList<>(10);
      //loop through all business cards
      rootType.getBusinesscard().forEach(businesscardType -> {
        _tmpDocTypes.clear();

        //populate all the doctypes
        businesscardType.getDoctypeid().forEach(doctypeidType -> {
          _tmpDocTypes.add(doctypeidType.getScheme() + "::" + doctypeidType.getValue());
        });


        businesscardType.getEntity().forEach(entityType -> {
          //obtain the submap for countrycode
          String countrycode = entityType.getCountrycode();
          Map<String, ICommonsSet<IParticipantIdentifier>> innerMap;
          if(directoryMap.containsKey(countrycode)){
            innerMap = directoryMap.get(countrycode);
          } else {
            innerMap = new HashMap<>();
            directoryMap.put(countrycode, innerMap);
          }

          //now for each doc type above, create a sub-sub-hash set (if not there already), and add the participant identifier
          _tmpDocTypes.forEach(uri -> {

            ICommonsSet<IParticipantIdentifier> identifierSet;
            if (innerMap.containsKey(uri)){
              identifierSet = innerMap.get(uri);
            } else {
              identifierSet = new CommonsHashSet<>();
              innerMap.put(uri, identifierSet);
            }

            //now add a new participant identifier to this set.
            identifierSet.add(TCIdentifierFactory.INSTANCE.createParticipantIdentifier(businesscardType.getParticipant().getScheme(),
                businesscardType.getParticipant().getValue()));
          });
        });
      });

    } catch (Exception ex) {
      LOGGER.error(ex.getMessage(), ex);
      throw new IllegalStateException("Couldn't parse resource /directory-business-cards.xml. Check logs for details ");
    }
  }

  @Nonnull
  @Override
  public ICommonsSet<IParticipantIdentifier> getAllParticipantIDs(@Nonnull String sLogPrefix, @Nonnull String sCountryCode,
                                                                  @Nonnull IDocumentTypeIdentifier aDocumentTypeID,
                                                                  @Nonnull IR2D2ErrorHandler aErrorHandler) {
    String documentTypeIDURIEncoded = aDocumentTypeID.getURIEncoded();

    LOGGER.info(sLogPrefix + "Query directory for [countryCode: " + sCountryCode +
        ", doctype: " + documentTypeIDURIEncoded + "]");

    if (directoryMap.containsKey(sCountryCode)){
      Map<String, ICommonsSet<IParticipantIdentifier>> innerMap = directoryMap.get(sCountryCode);

      if (innerMap.containsKey(documentTypeIDURIEncoded)){
        return innerMap.get(documentTypeIDURIEncoded);
      } else {
        LOGGER.warn(sLogPrefix + "The country " + sCountryCode + " does not support " + documentTypeIDURIEncoded);
      }
    } else {
      LOGGER.warn(sLogPrefix + "Country not found in the directory");
    }

    return EMPTY_SET;
  }
}
