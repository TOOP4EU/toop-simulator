
package eu.toop.schema.directory;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for entityType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="entityType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.peppol.eu/schema/pd/businesscard-generic/201907/}nameType"/>
 *         &lt;element name="geoinfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.peppol.eu/schema/pd/businesscard-generic/201907/}idType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="website" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contact" type="{http://www.peppol.eu/schema/pd/businesscard-generic/201907/}contactType" minOccurs="0"/>
 *         &lt;element name="additionalinfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="countrycode" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "entityType", namespace = "http://www.peppol.eu/schema/pd/businesscard-generic/201907/", propOrder = {
    "name",
    "geoinfo",
    "id",
    "website",
    "contact",
    "additionalinfo"
})
public class EntityType {

    @XmlElement(namespace = "http://www.peppol.eu/schema/pd/businesscard-generic/201907/", required = true)
    protected NameType name;
    @XmlElement(namespace = "http://www.peppol.eu/schema/pd/businesscard-generic/201907/")
    protected String geoinfo;
    @XmlElement(namespace = "http://www.peppol.eu/schema/pd/businesscard-generic/201907/")
    protected List<IdType> id;
    @XmlElement(namespace = "http://www.peppol.eu/schema/pd/businesscard-generic/201907/")
    protected String website;
    @XmlElement(namespace = "http://www.peppol.eu/schema/pd/businesscard-generic/201907/")
    protected ContactType contact;
    @XmlElement(namespace = "http://www.peppol.eu/schema/pd/businesscard-generic/201907/")
    protected String additionalinfo;
    @XmlAttribute(name = "countrycode")
    protected String countrycode;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link NameType }
     *     
     */
    public NameType getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link NameType }
     *     
     */
    public void setName(NameType value) {
        this.name = value;
    }

    /**
     * Gets the value of the geoinfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGeoinfo() {
        return geoinfo;
    }

    /**
     * Sets the value of the geoinfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGeoinfo(String value) {
        this.geoinfo = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the id property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IdType }
     * 
     * 
     */
    public List<IdType> getId() {
        if (id == null) {
            id = new ArrayList<IdType>();
        }
        return this.id;
    }

    /**
     * Gets the value of the website property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWebsite() {
        return website;
    }

    /**
     * Sets the value of the website property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWebsite(String value) {
        this.website = value;
    }

    /**
     * Gets the value of the contact property.
     * 
     * @return
     *     possible object is
     *     {@link ContactType }
     *     
     */
    public ContactType getContact() {
        return contact;
    }

    /**
     * Sets the value of the contact property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContactType }
     *     
     */
    public void setContact(ContactType value) {
        this.contact = value;
    }

    /**
     * Gets the value of the additionalinfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalinfo() {
        return additionalinfo;
    }

    /**
     * Sets the value of the additionalinfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalinfo(String value) {
        this.additionalinfo = value;
    }

    /**
     * Gets the value of the countrycode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountrycode() {
        return countrycode;
    }

    /**
     * Sets the value of the countrycode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountrycode(String value) {
        this.countrycode = value;
    }

}
