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
package eu.europa.esig.dss.asic.xades.signature.asics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import eu.europa.esig.dss.asic.common.ASiCParameters;
import eu.europa.esig.dss.asic.xades.signature.GetDataToSignASiCWithXAdESHelper;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.utils.Utils;

public class DataToSignASiCSWithXAdESFromFiles extends AbstractGetDataToSignASiCSWithXAdES implements GetDataToSignASiCWithXAdESHelper {

	private final List<DSSDocument> filesToBeSigned;
	private final Date signingDate;
	private final ASiCParameters asicParameters;

	private List<DSSDocument> signedDocuments;

	public DataToSignASiCSWithXAdESFromFiles(List<DSSDocument> filesToBeSigned, Date signingDate, ASiCParameters asicParameters) {
		this.filesToBeSigned = filesToBeSigned;
		this.signingDate = signingDate;
		this.asicParameters = asicParameters;
	}

	@Override
	public String getSignatureFilename() {
		return getSignatureFileName(asicParameters);
	}

	@Override
	public String getTimestampFilename() {
		throw new UnsupportedOperationException("Timestamp file cannot be added with ASiC-S + XAdES");
	}

	@Override
	public List<DSSDocument> getToBeSigned() {
		return getSignedDocuments();
	}

	@Override
	public DSSDocument getExistingSignature() {
		return null;
	}

	@Override
	public List<DSSDocument> getSignedDocuments() {
		if (signedDocuments == null) {
			if (Utils.collectionSize(filesToBeSigned) > 1) {
				signedDocuments = Arrays.asList(createPackageZip(filesToBeSigned, signingDate));
			} else {
				signedDocuments = new ArrayList<>(filesToBeSigned);
			}
		}
		return signedDocuments;
	}

	@Override
	public List<DSSDocument> getManifestFiles() {
		// No manifest file in ASiC-S
		return Collections.emptyList();
	}

	@Override
	public List<DSSDocument> getSignatures() {
		// new container
		return new ArrayList<>();
	}
	
	@Override
	public DSSDocument getRootDocument() {
		// No root container when using files
		return null;
	}

}
