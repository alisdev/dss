/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * 
 * This file is part of the "DSS - Digital Signature Services" project.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.cades.validation.evidencerecord;

import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.FoundCertificatesProxy;
import eu.europa.esig.dss.diagnostic.TimestampWrapper;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.SignatureCertificateSource;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CAdESLevelLTWithAsn1EvidenceRecordValidationTest extends AbstractCAdESWithEvidenceRecordTestValidation {

    @Override
    protected DSSDocument getSignedDocument() {
        return new FileDocument("src/test/resources/validation/evidence-record/Signature-C-LT-d233a2d9-a257-40dc-bcdb-bf4516b6d1da.p7m");
    }

    @Override
    protected List<DSSDocument> getDetachedEvidenceRecords() {
        return Collections.singletonList(new FileDocument("src/test/resources/validation/evidence-record/evidence-record-d233a2d9-a257-40dc-bcdb-bf4516b6d1da.ers"));
    }

    @Override
    protected CertificateSource getTrustedCertificateSource() {
        CommonTrustedCertificateSource trustedCertificateSource = new CommonTrustedCertificateSource();
        trustedCertificateSource.addCertificate(DSSUtils.loadCertificateFromBase64EncodedString("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"));
        trustedCertificateSource.addCertificate(DSSUtils.loadCertificateFromBase64EncodedString("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 "));
        trustedCertificateSource.addCertificate(DSSUtils.loadCertificateFromBase64EncodedString("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"));
        trustedCertificateSource.addCertificate(DSSUtils.loadCertificateFromBase64EncodedString("MIIDVzCCAj+gAwIBAgIBATANBgkqhkiG9w0BAQ0FADBNMRAwDgYDVQQDDAdyb290LWNhMRkwFwYDVQQKDBBOb3dpbmEgU29sdXRpb25zMREwDwYDVQQLDAhQS0ktVEVTVDELMAkGA1UEBhMCTFUwHhcNMjIwMTEzMTYwMzM1WhcNMjQwMTEzMTYwMzM1WjBNMRAwDgYDVQQDDAdyb290LWNhMRkwFwYDVQQKDBBOb3dpbmEgU29sdXRpb25zMREwDwYDVQQLDAhQS0ktVEVTVDELMAkGA1UEBhMCTFUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCRHOEXneXmMs+kosfF6axk1fopOaqpG0CJV9oDY07hPH0lTUKX0WpeHvflF/X0crUWW9xybA0NOKHpmRp68v55R4nRLB5fHUu/bOddi/L/i6RZYrySE/47LfXAUEsvUbewSUdzJU+jKKQOTSmenSZQDC3a7U72WOcCmTtuNh1c1tu76ffWMx3CNoDDSJkucOI6vqmjAf0g2yObRXN/4umk8wOg81eiLV6T1pzCWNkuja07BqIi0tQcf8P9ZcbqnoIrsXZcaRZx4DfUVqQDa6WQY8iWqn28rChRF3XG4XRsW5SdeSU+HOhbQmfc1Zn6Xp94rMg/dc7ozMo/51n1OdrfAgMBAAGjQjBAMA4GA1UdDwEB/wQEAwIBBjAdBgNVHQ4EFgQUek0zqwFuoxiLJwjVOXDg6RFTKT4wDwYDVR0TAQH/BAUwAwEB/zANBgkqhkiG9w0BAQ0FAAOCAQEAhV8vxZzlLmW2FnO660dtQwlVbrpZSIrJY4q8XfYOeJ4lraJ1xV5XtS61lTL+PvBBlTRB8lBuNAtHPnq+qxG06fKfIaGkCcOH62WV/LA9qYnUpWgCWO5c4DUKlyaf9JrQksNUYd23HwJnJTRD7tSe2REpOrB2fUH1b6xvVsCZ8xsCt3SAnkGuu8l2oYtBBgfr/vZ2+k8vdhkQIhIyf7/YkYBLXikVItjZ064Q0oypXfsOd5xyCnYDkBKnMnj6QgPsayWZ/MAAxH+upmiQkmViMTm2GbLtSLzsAe/cU9Ym+9+Ci5pnB+heZ+LoZ6svBKaYWvHbl6yLvpV31XnuK/QPWQ=="));
        return trustedCertificateSource;
    }

    @Override
    protected int getNumberOfExpectedEvidenceScopes() {
        return 1;
    }

    @Override
    protected void verifyCertificateSourceData(SignatureCertificateSource certificateSource, FoundCertificatesProxy foundCertificates) {
        // skip
    }

    @Override
    protected void checkTimestamp(DiagnosticData diagnosticData, TimestampWrapper timestampWrapper) {
        assertNotNull(timestampWrapper.getProductionTime());
        assertTrue(timestampWrapper.isMessageImprintDataFound());
        assertTrue(timestampWrapper.isMessageImprintDataIntact());
        assertTrue(timestampWrapper.isSignatureIntact());
        assertTrue(timestampWrapper.isSignatureValid());
    }

    @Override
    protected boolean allArchiveDataObjectsProvidedToValidation() {
        return false;
    }

}
