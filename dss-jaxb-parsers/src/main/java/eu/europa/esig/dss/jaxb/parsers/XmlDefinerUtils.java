package eu.europa.esig.dss.jaxb.parsers;

import javax.xml.XMLConstants;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public final class XmlDefinerUtils {
	// ALIS Coufal - added try catch for support evn with loaded xcercesImpl
	private static final Logger LOG = LoggerFactory.getLogger(DateParser.class);

	public static SchemaFactory getSecureSchemaFactory() throws SAXException {
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			sf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		} catch (Exception e) {
			LOG.trace("", e);
		}
		try {
			sf.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		} catch (Exception e) {
			LOG.trace("", e);
		}
		try {
			sf.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
		} catch (Exception e) {
			LOG.trace("", e);
		}
		return sf;
	}

	public static TransformerFactory getSecureTransformerFactory() throws TransformerConfigurationException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		try {
			transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		} catch (Exception e) {
			LOG.trace("", e);
		}
		try {
			transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		} catch (Exception e) {
			LOG.trace("", e);
		}
		try {
			transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
		} catch (Exception e) {
			LOG.trace("", e);
		}
		return transformerFactory;
	}
}
