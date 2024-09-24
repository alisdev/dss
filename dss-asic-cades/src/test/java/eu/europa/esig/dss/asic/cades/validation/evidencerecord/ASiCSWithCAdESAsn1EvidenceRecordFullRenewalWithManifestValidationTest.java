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
package eu.europa.esig.dss.asic.cades.validation.evidencerecord;

import eu.europa.esig.dss.asic.common.validation.AbstractASiCWithAsn1EvidenceRecordTestValidation;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.enumerations.DigestMatcherType;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.model.ReferenceValidation;
import eu.europa.esig.dss.spi.x509.evidencerecord.EvidenceRecord;
import eu.europa.esig.dss.spi.x509.tsp.TimestampToken;
import eu.europa.esig.dss.spi.x509.tsp.TimestampedReference;
import eu.europa.esig.dss.utils.Utils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ASiCSWithCAdESAsn1EvidenceRecordFullRenewalWithManifestValidationTest extends AbstractASiCWithAsn1EvidenceRecordTestValidation {

    @Override
    protected DSSDocument getSignedDocument() {
        return new FileDocument("src/test/resources/validation/evidencerecord/er-asn1-full-renewal-with-manifest.asics");
    }

    @Override
    protected void checkDetachedEvidenceRecords(List<EvidenceRecord> detachedEvidenceRecords) {
        assertTrue(Utils.isCollectionNotEmpty(detachedEvidenceRecords));

        EvidenceRecord evidenceRecord = detachedEvidenceRecords.get(0);
        List<ReferenceValidation> referenceValidationList = evidenceRecord.getReferenceValidation();
        assertEquals(2, referenceValidationList.size());

        boolean foundArchiveObject = false;
        boolean notFoundArchiveObject = false;
        for (ReferenceValidation referenceValidation : referenceValidationList) {
            if (DigestMatcherType.EVIDENCE_RECORD_ARCHIVE_OBJECT == referenceValidation.getType()) {
                assertNotNull(referenceValidation.getDocumentName());
                assertTrue(referenceValidation.isFound());
                assertTrue(referenceValidation.isIntact());
                foundArchiveObject = true;
            } else if (DigestMatcherType.EVIDENCE_RECORD_ORPHAN_REFERENCE == referenceValidation.getType()) {
                assertNull(referenceValidation.getDocumentName());
                assertFalse(referenceValidation.isFound());
                assertFalse(referenceValidation.isIntact());
                notFoundArchiveObject = true;
            }
        }
        assertTrue(foundArchiveObject);
        assertTrue(notFoundArchiveObject);

        List<TimestampedReference> timestampedReferences = evidenceRecord.getTimestampedReferences();
        assertTrue(Utils.isCollectionNotEmpty(timestampedReferences));

        List<TimestampToken> timestamps = evidenceRecord.getTimestamps();
        assertEquals(3, Utils.collectionSize(timestamps));

        TimestampToken originalTst = timestamps.get(0);
        assertTrue(originalTst.isProcessed());
        assertTrue(originalTst.isMessageImprintDataFound());
        assertTrue(originalTst.isMessageImprintDataIntact());
        assertEquals(0, Utils.collectionSize(originalTst.getReferenceValidations()));

        TimestampToken tstRenewal = timestamps.get(1);
        assertTrue(tstRenewal.isProcessed());
        assertTrue(tstRenewal.isMessageImprintDataFound());
        assertTrue(tstRenewal.isMessageImprintDataIntact());

        boolean arcTstRefFound = false;
        boolean orphanRefFound = false;
        assertEquals(2, Utils.collectionSize(tstRenewal.getReferenceValidations()));
        for (ReferenceValidation referenceValidation : tstRenewal.getReferenceValidations()) {
            if (DigestMatcherType.EVIDENCE_RECORD_ARCHIVE_TIME_STAMP == referenceValidation.getType()) {
                assertNull(referenceValidation.getDocumentName());
                assertTrue(referenceValidation.isFound());
                assertTrue(referenceValidation.isIntact());
                arcTstRefFound = true;
            } else if (DigestMatcherType.EVIDENCE_RECORD_ORPHAN_REFERENCE == referenceValidation.getType()) {
                assertNull(referenceValidation.getDocumentName());
                assertFalse(referenceValidation.isFound());
                assertFalse(referenceValidation.isIntact());
                orphanRefFound = true;
            }
        }
        assertTrue(arcTstRefFound);
        assertTrue(orphanRefFound);

        TimestampToken tstChainRenewal = timestamps.get(2);
        assertTrue(tstChainRenewal.isProcessed());
        assertTrue(tstChainRenewal.isMessageImprintDataFound());
        assertTrue(tstChainRenewal.isMessageImprintDataIntact());

        assertEquals(1, Utils.collectionSize(tstChainRenewal.getReferenceValidations()));
        ReferenceValidation referenceValidation = tstChainRenewal.getReferenceValidations().get(0);
        assertEquals(DigestMatcherType.EVIDENCE_RECORD_ARCHIVE_OBJECT, referenceValidation.getType());
        assertNotNull(referenceValidation.getDocumentName());
        assertTrue(referenceValidation.isFound());
        assertTrue(referenceValidation.isIntact());
    }

    @Override
    protected void checkContainerInfo(DiagnosticData diagnosticData) {
        assertNotNull(diagnosticData.getContainerInfo());
        assertNotNull(diagnosticData.getContainerType());
        assertNull(diagnosticData.getMimetypeFileContent());
        assertTrue(Utils.isCollectionNotEmpty(diagnosticData.getContainerInfo().getContentFiles()));
        assertTrue(Utils.isCollectionNotEmpty(diagnosticData.getContainerInfo().getManifestFiles()));
    }

    @Override
    protected boolean allArchiveDataObjectsProvidedToValidation() {
        return false;
    }

    @Override
    protected boolean tstCoversOnlyCurrentHashTreeData() {
        return false;
    }

}
