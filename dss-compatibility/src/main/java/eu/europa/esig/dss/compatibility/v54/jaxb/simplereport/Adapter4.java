//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.05.04 at 07:38:36 AM CEST 
//


package eu.europa.esig.dss.compatibility.v54.jaxb.simplereport;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import eu.europa.esig.dss.compatibility.v54.jaxb.parsers.SignatureQualificationParser;
import eu.europa.esig.dss.compatibility.v54.validation.SignatureQualification;

public class Adapter4
    extends XmlAdapter<String, SignatureQualification>
{


    public SignatureQualification unmarshal(String value) {
        return (SignatureQualificationParser.parse(value));
    }

    public String marshal(SignatureQualification value) {
        return (SignatureQualificationParser.print(value));
    }

}
