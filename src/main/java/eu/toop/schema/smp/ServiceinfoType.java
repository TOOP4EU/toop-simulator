
package eu.toop.schema.smp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for serviceinfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="serviceinfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="doctypeidentifier" type="{}doctypeidentifierType"/>
 *         &lt;element name="process" type="{}processType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="servicegroupid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "serviceinfoType", propOrder = {
    "doctypeidentifier",
    "process"
})
public class ServiceinfoType {

    @XmlElement(required = true)
    protected DoctypeidentifierType doctypeidentifier;
    @XmlElement(required = true)
    protected ProcessType process;
    @XmlAttribute(name = "servicegroupid")
    protected String servicegroupid;

    /**
     * Gets the value of the doctypeidentifier property.
     * 
     * @return
     *     possible object is
     *     {@link DoctypeidentifierType }
     *     
     */
    public DoctypeidentifierType getDoctypeidentifier() {
        return doctypeidentifier;
    }

    /**
     * Sets the value of the doctypeidentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link DoctypeidentifierType }
     *     
     */
    public void setDoctypeidentifier(DoctypeidentifierType value) {
        this.doctypeidentifier = value;
    }

    /**
     * Gets the value of the process property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessType }
     *     
     */
    public ProcessType getProcess() {
        return process;
    }

    /**
     * Sets the value of the process property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessType }
     *     
     */
    public void setProcess(ProcessType value) {
        this.process = value;
    }

    /**
     * Gets the value of the servicegroupid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServicegroupid() {
        return servicegroupid;
    }

    /**
     * Sets the value of the servicegroupid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServicegroupid(String value) {
        this.servicegroupid = value;
    }

}
