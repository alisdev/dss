//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.05.04 at 07:38:36 AM CEST 
//


package eu.europa.esig.dss.compatibility.v54.jaxb.simplereport;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import eu.europa.esig.dss.compatibility.v54.jaxb.parsers.IndicationParser;
import eu.europa.esig.dss.compatibility.v54.validation.policy.rules.Indication;

public class Adapter2
    extends XmlAdapter<String, Indication>
{


    public Indication unmarshal(String value) {
        return (IndicationParser.parse(value));
    }

    public String marshal(Indication value) {
        return (IndicationParser.print(value));
    }

}
