
package eu.toop.directory.schema;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for rootType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="rootType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="businesscard" type="{http://www.peppol.eu/schema/pd/businesscard-generic/201907/}businesscardType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="creationdt" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rootType", namespace = "http://www.peppol.eu/schema/pd/businesscard-generic/201907/", propOrder = {
    "businesscard"
})
public class RootType {

    @XmlElement(namespace = "http://www.peppol.eu/schema/pd/businesscard-generic/201907/")
    protected List<BusinesscardType> businesscard;
    @XmlAttribute(name = "version")
    protected String version;
    @XmlAttribute(name = "creationdt")
    protected String creationdt;

    /**
     * Gets the value of the businesscard property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the businesscard property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBusinesscard().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BusinesscardType }
     * 
     * 
     */
    public List<BusinesscardType> getBusinesscard() {
        if (businesscard == null) {
            businesscard = new ArrayList<BusinesscardType>();
        }
        return this.businesscard;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the creationdt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreationdt() {
        return creationdt;
    }

    /**
     * Sets the value of the creationdt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreationdt(String value) {
        this.creationdt = value;
    }

}
