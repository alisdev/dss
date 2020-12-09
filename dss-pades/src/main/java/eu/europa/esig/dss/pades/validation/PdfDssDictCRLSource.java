package eu.europa.esig.dss.pades.validation;

import eu.europa.esig.dss.crl.CRLBinary;
import eu.europa.esig.dss.enumerations.RevocationOrigin;
import eu.europa.esig.dss.pdf.PdfDssDict;
import eu.europa.esig.dss.pdf.PdfVRIDict;
import eu.europa.esig.dss.spi.x509.revocation.crl.OfflineCRLSource;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The CRL source extracted from a DSS dictionary
 */
public class PdfDssDictCRLSource extends OfflineCRLSource {

    /** The map of PDF object ids and corresponding CRL binaries */
    private transient Map<Long, CRLBinary> crlMap;

    /**
     * Default constructor
     *
     * @param dssDictionary {@link PdfDssDict}
     */
    public PdfDssDictCRLSource(PdfDssDict dssDictionary) {
        extractDSSCRLs(dssDictionary);
        extractVRICRLs(dssDictionary);
    }

    /**
     * Empty constructor
     */
    PdfDssDictCRLSource() {
    }

    /**
     * Returns a map of all CRL entries contained in DSS dictionary or into nested
     * VRI dictionaries
     *
     * @return a map of CRL binaries with their object ids
     */
    public Map<Long, CRLBinary> getCrlMap() {
        if (crlMap != null) {
            return crlMap;
        }
        return Collections.emptyMap();
    }

    private Map<Long, CRLBinary> getDssCrlMap(PdfDssDict dssDictionary) {
        if (dssDictionary != null) {
            crlMap = dssDictionary.getCRLs();
            return crlMap;
        }
        return Collections.emptyMap();
    }

    /**
     * Extract the CRLs from the DSS dictionary
     *
     * @param dssDictionary {@link PdfDssDict}
     */
    protected void extractDSSCRLs(PdfDssDict dssDictionary) {
        Map<Long, CRLBinary> dssCrlMap = getDssCrlMap(dssDictionary);
        for (CRLBinary crl : dssCrlMap.values()) {
            addBinary(crl, RevocationOrigin.DSS_DICTIONARY);
        }
    }

    /**
     * Extract the CRLs from all embedded VRI dictionaries
     *
     * @param dssDictionary {@link PdfDssDict}
     */
    protected void extractVRICRLs(PdfDssDict dssDictionary) {
        if (dssDictionary != null) {
            List<PdfVRIDict> vriDictList = dssDictionary.getVRIs();
            for (PdfVRIDict vriDict : vriDictList) {
                extractVRICRLs(vriDict);
            }
        }
    }

    /**
     * Extract the CRLs from the VRI dictionary
     *
     * @param vriDictionary {@link PdfDssDict}
     */
    protected void extractVRICRLs(PdfVRIDict vriDictionary) {
        if (vriDictionary != null) {
            for (Map.Entry<Long, CRLBinary> crlEntry : vriDictionary.getCRLs().entrySet()) {
                if (!crlMap.containsKey(crlEntry.getKey())) {
                    crlMap.put(crlEntry.getKey(), crlEntry.getValue());
                }
                addBinary(crlEntry.getValue(), RevocationOrigin.VRI_DICTIONARY);
            }
        }
    }

}
