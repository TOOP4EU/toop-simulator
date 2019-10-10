
package eu.toop.schema.smp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for endpointType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="endpointType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="certificate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="svcdescription" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *       &lt;attribute name="transportprofile" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="endpointref" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="reqblsig" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="techcontacturl" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "endpointType", propOrder = {
    "certificate",
    "svcdescription"
})
public class EndpointType {

    @XmlElement(required = true)
    protected String certificate;
    @XmlElement(required = true)
    protected String svcdescription;
    @XmlAttribute(name = "transportprofile")
    protected String transportprofile;
    @XmlAttribute(name = "endpointref")
    protected String endpointref;
    @XmlAttribute(name = "reqblsig")
    protected String reqblsig;
    @XmlAttribute(name = "techcontacturl")
    protected String techcontacturl;

    /**
     * Gets the value of the certificate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCertificate() {
        return certificate;
    }

    /**
     * Sets the value of the certificate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCertificate(String value) {
        this.certificate = value;
    }

    /**
     * Gets the value of the svcdescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSvcdescription() {
        return svcdescription;
    }

    /**
     * Sets the value of the svcdescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSvcdescription(String value) {
        this.svcdescription = value;
    }

    /**
     * Gets the value of the transportprofile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransportprofile() {
        return transportprofile;
    }

    /**
     * Sets the value of the transportprofile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransportprofile(String value) {
        this.transportprofile = value;
    }

    /**
     * Gets the value of the endpointref property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndpointref() {
        return endpointref;
    }

    /**
     * Sets the value of the endpointref property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndpointref(String value) {
        this.endpointref = value;
    }

    /**
     * Gets the value of the reqblsig property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReqblsig() {
        return reqblsig;
    }

    /**
     * Sets the value of the reqblsig property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReqblsig(String value) {
        this.reqblsig = value;
    }

    /**
     * Gets the value of the techcontacturl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTechcontacturl() {
        return techcontacturl;
    }

    /**
     * Sets the value of the techcontacturl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTechcontacturl(String value) {
        this.techcontacturl = value;
    }

}
