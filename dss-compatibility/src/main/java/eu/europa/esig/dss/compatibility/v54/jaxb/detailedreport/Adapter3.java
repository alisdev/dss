//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.01.30 at 08:31:52 AM CET 
//


package eu.europa.esig.dss.compatibility.v54.jaxb.detailedreport;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import eu.europa.esig.dss.compatibility.v54.jaxb.parsers.SubIndicationParser;
import eu.europa.esig.dss.compatibility.v54.validation.policy.rules.SubIndication;

public class Adapter3
    extends XmlAdapter<String, SubIndication>
{


    public SubIndication unmarshal(String value) {
        return (SubIndicationParser.parse(value));
    }

    public String marshal(SubIndication value) {
        return (SubIndicationParser.print(value));
    }

}