
package eu.toop.schema.smp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for processType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="processType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="processidentifier" type="{}processidentifierType"/>
 *         &lt;element name="endpoint" type="{}endpointType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "processType", propOrder = {
    "processidentifier",
    "endpoint"
})
public class ProcessType {

    @XmlElement(required = true)
    protected ProcessidentifierType processidentifier;
    @XmlElement(required = true)
    protected EndpointType endpoint;

    /**
     * Gets the value of the processidentifier property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessidentifierType }
     *     
     */
    public ProcessidentifierType getProcessidentifier() {
        return processidentifier;
    }

    /**
     * Sets the value of the processidentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessidentifierType }
     *     
     */
    public void setProcessidentifier(ProcessidentifierType value) {
        this.processidentifier = value;
    }

    /**
     * Gets the value of the endpoint property.
     * 
     * @return
     *     possible object is
     *     {@link EndpointType }
     *     
     */
    public EndpointType getEndpoint() {
        return endpoint;
    }

    /**
     * Sets the value of the endpoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link EndpointType }
     *     
     */
    public void setEndpoint(EndpointType value) {
        this.endpoint = value;
    }

}
