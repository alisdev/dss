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
package eu.europa.esig.dss.model;

import java.util.Objects;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;

/**
 * This class represents the parameters provided when generating specific timestamps in a signature, such as an
 * AllDataObjectsTimestamp or an
 * IndividualDataObjectsTimestamp.
 */
@SuppressWarnings("serial")
public abstract class TimestampParameters implements SerializableTimestampParameters {

	/**
	 * The digest algorithm to provide to the timestamping authority
	 */
	protected DigestAlgorithm digestAlgorithm = DigestAlgorithm.SHA256;
	
	protected TimestampParameters() {
	}

	protected TimestampParameters(DigestAlgorithm digestAlgorithm) {
		this.digestAlgorithm = digestAlgorithm;
	}
	
    /**
     * Uses for transfering signatures timestaps
     */
    private byte[] encodedBcTimeStampToken; // alisdev

	@Override
	public DigestAlgorithm getDigestAlgorithm() {
		return digestAlgorithm;
	}

	public void setDigestAlgorithm(final DigestAlgorithm digestAlgorithm) {
		Objects.requireNonNull(digestAlgorithm, "DigestAlgorithm cannot be null!");
		this.digestAlgorithm = digestAlgorithm;
	}

    public byte[] getEncodedTimeStampToken() { // alisdev
        return encodedBcTimeStampToken;
    }

    public void setEncodedTimeStampToken( byte[] encodedTimeStampToken ) { // alisdev
        this.encodedBcTimeStampToken = encodedTimeStampToken;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((digestAlgorithm == null) ? 0 : digestAlgorithm.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TimestampParameters other = (TimestampParameters) obj;
		if (digestAlgorithm != other.digestAlgorithm) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "TimestampParameters {digestAlgorithm=" + digestAlgorithm.getName() + "}";
	}
}
