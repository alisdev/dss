//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.01.30 at 08:31:52 AM CET 
//


package eu.europa.esig.dss.compatibility.v54.jaxb.detailedreport;

import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import eu.europa.esig.dss.compatibility.v54.jaxb.parsers.DateParser;

public class Adapter1
    extends XmlAdapter<String, Date>
{


    public Date unmarshal(String value) {
        return (DateParser.parse(value));
    }

    public String marshal(Date value) {
        return (DateParser.print(value));
    }

}
