package eu.toop.connector;

import eu.toop.directory.schema.ObjectFactory;
import eu.toop.directory.schema.RootType;
import eu.toop.simulator.mock.XMLBasedParticipantIDProvider;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class DirectorySchemaTest {

  @Test
  public void parseBusinessCards() throws JAXBException {
    JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);

    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

    RootType rootType = ((JAXBElement<RootType>) jaxbUnmarshaller.unmarshal(this.getClass().getResourceAsStream("/directory-business-cards.xml"))).getValue();

    System.out.println(rootType.getCreationdt());

  }

  @Test
  public void testDirectorySimulator(){
    XMLBasedParticipantIDProvider xmlBasedParticipantIDProvider = new XMLBasedParticipantIDProvider();

    System.out.println(xmlBasedParticipantIDProvider);
  }
}
