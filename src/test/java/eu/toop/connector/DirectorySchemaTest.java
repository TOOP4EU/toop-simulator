package eu.toop.connector;

import eu.toop.simulator.mock.XMLBasedParticipantIDProvider;
import eu.toop.simulator.util.JAXBUtil;
import org.junit.Test;

public class DirectorySchemaTest {

  @Test
  public void parseBusinessCards() {
    eu.toop.schema.directory.RootType rootType = JAXBUtil.parseFileOrResource("directory-business-cards.xml",
        eu.toop.schema.directory.ObjectFactory.class);
    System.out.println(rootType.getCreationdt());

  }

  @Test
  public void testDirectorySimulator(){
    XMLBasedParticipantIDProvider xmlBasedParticipantIDProvider = new XMLBasedParticipantIDProvider();

    System.out.println(xmlBasedParticipantIDProvider);
  }
}
