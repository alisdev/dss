module jpms_dss_simple_certificate_report {
	requires jpms_dss_jaxb_parsers;
	requires java.xml.bind;

	exports eu.europa.esig.dss.simplecertificatereport;
	exports eu.europa.esig.dss.simplecertificatereport.jaxb;

//	uses javax.xml;
//	uses javax.xml.transform.stream;
//	uses javax.xml.validation;
}