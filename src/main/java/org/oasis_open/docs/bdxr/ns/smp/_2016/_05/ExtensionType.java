//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.10.14 at 01:10:00 AM EET 
//


package org.oasis_open.docs.bdxr.ns.smp._2016._05;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3c.dom.Element;


/**
 * 
 * 				A single extension for private use.
 * 			
 * 
 * <p>Java class for ExtensionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExtensionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ExtensionID" type="{http://www.w3.org/2001/XMLSchema}token" minOccurs="0"/>
 *         &lt;element name="ExtensionName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ExtensionAgencyID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ExtensionAgencyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ExtensionAgencyURI" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="ExtensionVersionID" type="{http://www.w3.org/2001/XMLSchema}normalizedString" minOccurs="0"/>
 *         &lt;element name="ExtensionURI" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="ExtensionReasonCode" type="{http://www.w3.org/2001/XMLSchema}token" minOccurs="0"/>
 *         &lt;element name="ExtensionReason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;any processContents='lax' namespace='##other'/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtensionType", propOrder = {
    "extensionID",
    "extensionName",
    "extensionAgencyID",
    "extensionAgencyName",
    "extensionAgencyURI",
    "extensionVersionID",
    "extensionURI",
    "extensionReasonCode",
    "extensionReason",
    "any"
})
public class ExtensionType {

    @XmlElement(name = "ExtensionID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String extensionID;
    @XmlElement(name = "ExtensionName")
    protected String extensionName;
    @XmlElement(name = "ExtensionAgencyID")
    protected String extensionAgencyID;
    @XmlElement(name = "ExtensionAgencyName")
    protected String extensionAgencyName;
    @XmlElement(name = "ExtensionAgencyURI")
    @XmlSchemaType(name = "anyURI")
    protected String extensionAgencyURI;
    @XmlElement(name = "ExtensionVersionID")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String extensionVersionID;
    @XmlElement(name = "ExtensionURI")
    @XmlSchemaType(name = "anyURI")
    protected String extensionURI;
    @XmlElement(name = "ExtensionReasonCode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String extensionReasonCode;
    @XmlElement(name = "ExtensionReason")
    protected String extensionReason;
    @XmlAnyElement(lax = true)
    protected Object any;

    /**
     * Gets the value of the extensionID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtensionID() {
        return extensionID;
    }

    /**
     * Sets the value of the extensionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtensionID(String value) {
        this.extensionID = value;
    }

    /**
     * Gets the value of the extensionName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtensionName() {
        return extensionName;
    }

    /**
     * Sets the value of the extensionName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtensionName(String value) {
        this.extensionName = value;
    }

    /**
     * Gets the value of the extensionAgencyID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtensionAgencyID() {
        return extensionAgencyID;
    }

    /**
     * Sets the value of the extensionAgencyID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtensionAgencyID(String value) {
        this.extensionAgencyID = value;
    }

    /**
     * Gets the value of the extensionAgencyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtensionAgencyName() {
        return extensionAgencyName;
    }

    /**
     * Sets the value of the extensionAgencyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtensionAgencyName(String value) {
        this.extensionAgencyName = value;
    }

    /**
     * Gets the value of the extensionAgencyURI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtensionAgencyURI() {
        return extensionAgencyURI;
    }

    /**
     * Sets the value of the extensionAgencyURI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtensionAgencyURI(String value) {
        this.extensionAgencyURI = value;
    }

    /**
     * Gets the value of the extensionVersionID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtensionVersionID() {
        return extensionVersionID;
    }

    /**
     * Sets the value of the extensionVersionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtensionVersionID(String value) {
        this.extensionVersionID = value;
    }

    /**
     * Gets the value of the extensionURI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtensionURI() {
        return extensionURI;
    }

    /**
     * Sets the value of the extensionURI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtensionURI(String value) {
        this.extensionURI = value;
    }

    /**
     * Gets the value of the extensionReasonCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtensionReasonCode() {
        return extensionReasonCode;
    }

    /**
     * Sets the value of the extensionReasonCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtensionReasonCode(String value) {
        this.extensionReasonCode = value;
    }

    /**
     * Gets the value of the extensionReason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtensionReason() {
        return extensionReason;
    }

    /**
     * Sets the value of the extensionReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtensionReason(String value) {
        this.extensionReason = value;
    }

    /**
     * Gets the value of the any property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     {@link Element }
     *     
     */
    public Object getAny() {
        return any;
    }

    /**
     * Sets the value of the any property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     {@link Element }
     *     
     */
    public void setAny(Object value) {
        this.any = value;
    }

}
