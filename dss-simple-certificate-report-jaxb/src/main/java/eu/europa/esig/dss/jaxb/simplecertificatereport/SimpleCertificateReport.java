//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.05.04 at 07:37:28 AM CEST 
//


package eu.europa.esig.dss.jaxb.simplecertificatereport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Chain" type="{http://dss.esig.europa.eu/validation/simple-certificate-report}Chain" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="ValidationTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "chain"
})
@XmlRootElement(name = "SimpleCertificateReport")
public class SimpleCertificateReport implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElementWrapper(name = "Chain")
    @XmlElement(name = "ChainItem", namespace = "http://dss.esig.europa.eu/validation/simple-certificate-report")
    protected List<XmlChainItem> chain = new ArrayList<XmlChainItem>();
    @XmlAttribute(name = "ValidationTime")
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date validationTime;

    /**
     * Gets the value of the validationTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getValidationTime() {
        return validationTime;
    }

    /**
     * Sets the value of the validationTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValidationTime(Date value) {
        this.validationTime = value;
    }

    public List<XmlChainItem> getChain() {
        return chain;
    }

    public void setChain(List<XmlChainItem> chain) {
        this.chain = chain;
    }

}
