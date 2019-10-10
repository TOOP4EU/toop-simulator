
package eu.toop.schema.directory;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for businesscardType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="businesscardType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="participant" type="{http://www.peppol.eu/schema/pd/businesscard-generic/201907/}participantType"/>
 *         &lt;element name="entity" type="{http://www.peppol.eu/schema/pd/businesscard-generic/201907/}entityType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="doctypeid" type="{http://www.peppol.eu/schema/pd/businesscard-generic/201907/}doctypeidType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "businesscardType", namespace = "http://www.peppol.eu/schema/pd/businesscard-generic/201907/", propOrder = {
    "participant",
    "entity",
    "doctypeid"
})
public class BusinesscardType {

    @XmlElement(namespace = "http://www.peppol.eu/schema/pd/businesscard-generic/201907/", required = true)
    protected ParticipantType participant;
    @XmlElement(namespace = "http://www.peppol.eu/schema/pd/businesscard-generic/201907/")
    protected List<EntityType> entity;
    @XmlElement(namespace = "http://www.peppol.eu/schema/pd/businesscard-generic/201907/")
    protected List<DoctypeidType> doctypeid;

    /**
     * Gets the value of the participant property.
     * 
     * @return
     *     possible object is
     *     {@link ParticipantType }
     *     
     */
    public ParticipantType getParticipant() {
        return participant;
    }

    /**
     * Sets the value of the participant property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParticipantType }
     *     
     */
    public void setParticipant(ParticipantType value) {
        this.participant = value;
    }

    /**
     * Gets the value of the entity property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the entity property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEntity().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EntityType }
     * 
     * 
     */
    public List<EntityType> getEntity() {
        if (entity == null) {
            entity = new ArrayList<EntityType>();
        }
        return this.entity;
    }

    /**
     * Gets the value of the doctypeid property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the doctypeid property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDoctypeid().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DoctypeidType }
     * 
     * 
     */
    public List<DoctypeidType> getDoctypeid() {
        if (doctypeid == null) {
            doctypeid = new ArrayList<DoctypeidType>();
        }
        return this.doctypeid;
    }

}
