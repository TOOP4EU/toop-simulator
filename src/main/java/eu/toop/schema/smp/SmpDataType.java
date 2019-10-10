
package eu.toop.schema.smp;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for smp-dataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="smp-dataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="servicegroup" type="{}servicegroupType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="businesscard" type="{}businesscardType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "smp-dataType", propOrder = {
    "servicegroup",
    "businesscard"
})
public class SmpDataType {

    protected List<ServicegroupType> servicegroup;
    protected List<BusinesscardType> businesscard;
    @XmlAttribute(name = "version")
    protected String version;

    /**
     * Gets the value of the servicegroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the servicegroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getServicegroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ServicegroupType }
     * 
     * 
     */
    public List<ServicegroupType> getServicegroup() {
        if (servicegroup == null) {
            servicegroup = new ArrayList<ServicegroupType>();
        }
        return this.servicegroup;
    }

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

}
