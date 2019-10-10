
package eu.toop.schema.smp;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the eu.toop.smp.schema package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _SmpData_QNAME = new QName("", "smp-data");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: eu.toop.smp.schema
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SmpDataType }
     * 
     */
    public SmpDataType createSmpDataType() {
        return new SmpDataType();
    }

    /**
     * Create an instance of {@link BusinesscardType }
     * 
     */
    public BusinesscardType createBusinesscardType() {
        return new BusinesscardType();
    }

    /**
     * Create an instance of {@link EndpointType }
     * 
     */
    public EndpointType createEndpointType() {
        return new EndpointType();
    }

    /**
     * Create an instance of {@link ServiceinfoType }
     * 
     */
    public ServiceinfoType createServiceinfoType() {
        return new ServiceinfoType();
    }

    /**
     * Create an instance of {@link EntityType }
     * 
     */
    public EntityType createEntityType() {
        return new EntityType();
    }

    /**
     * Create an instance of {@link ServicegroupType }
     * 
     */
    public ServicegroupType createServicegroupType() {
        return new ServicegroupType();
    }

    /**
     * Create an instance of {@link ParticipantType }
     * 
     */
    public ParticipantType createParticipantType() {
        return new ParticipantType();
    }

    /**
     * Create an instance of {@link ContactType }
     * 
     */
    public ContactType createContactType() {
        return new ContactType();
    }

    /**
     * Create an instance of {@link IdentifierType }
     * 
     */
    public IdentifierType createIdentifierType() {
        return new IdentifierType();
    }

    /**
     * Create an instance of {@link DoctypeidentifierType }
     * 
     */
    public DoctypeidentifierType createDoctypeidentifierType() {
        return new DoctypeidentifierType();
    }

    /**
     * Create an instance of {@link NameType }
     * 
     */
    public NameType createNameType() {
        return new NameType();
    }

    /**
     * Create an instance of {@link ProcessidentifierType }
     * 
     */
    public ProcessidentifierType createProcessidentifierType() {
        return new ProcessidentifierType();
    }

    /**
     * Create an instance of {@link ProcessType }
     * 
     */
    public ProcessType createProcessType() {
        return new ProcessType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SmpDataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "smp-data")
    public JAXBElement<SmpDataType> createSmpData(SmpDataType value) {
        return new JAXBElement<SmpDataType>(_SmpData_QNAME, SmpDataType.class, null, value);
    }

}
