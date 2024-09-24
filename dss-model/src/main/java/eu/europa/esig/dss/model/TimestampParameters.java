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
 * License along with this library; if not, write to th e Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.model;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;

import java.util.Arrays;
import java.util.Objects;

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
	protected DigestAlgorithm digestAlgorithm = DigestAlgorithm.SHA512;

	/**
	 * Uses for transfering signatures timestaps (ALISDEV)
	 */
	private byte[] encodedBcTimeStampToken; // alisdev prenos cas. razitka z KEO4

	/**
	 * Empty constructor
	 */
	protected TimestampParameters() {
		// empty
	}

	/**
	 * The default constructor
	 *
	 * @param digestAlgorithm {@link DigestAlgorithm} to use for data digest computation
	 */
	protected TimestampParameters(DigestAlgorithm digestAlgorithm) {
		this.digestAlgorithm = digestAlgorithm;
	}

	@Override
	public DigestAlgorithm getDigestAlgorithm() {
		return digestAlgorithm;
	}

	/**
	 * Sets DigestAlgorithm to use for timestamped data's digest computation
	 *
	 * @param digestAlgorithm {@link DigestAlgorithm}
	 */
	public void setDigestAlgorithm(final DigestAlgorithm digestAlgorithm) {
		Objects.requireNonNull(digestAlgorithm, "DigestAlgorithm cannot be null!");
		this.digestAlgorithm = digestAlgorithm;
	}

	/**
	 * Returns the encoded timestamp token (ALISDEV)
	 */
	public byte[] getEncodedTimeStampToken() {
		return encodedBcTimeStampToken; // alisdev prenos cas. razitka z KEO4
	}

	/**
	 * Sets the encoded timestamp token (ALISDEV)
	 * @param encodedTimeStampToken use to transfer custom timestamps
	 */
	public void setEncodedTimeStampToken( byte[] encodedTimeStampToken ) {
		this.encodedBcTimeStampToken = encodedTimeStampToken;  // alisdev prenos cas. razitka z KEO4
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((digestAlgorithm == null) ? 0 : digestAlgorithm.hashCode());
		result = (prime * result) + Arrays.hashCode(encodedBcTimeStampToken); // alisdev
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
		if (!Arrays.equals(encodedBcTimeStampToken, other.encodedBcTimeStampToken)) { // alisdev
			return false; // alisdev
		}
		return true;
	}

	@Override
	public String toString() {
		return "TimestampParameters {digestAlgorithm=" + digestAlgorithm.getName() + ", encodedBcTimeStampToken=" + Arrays.toString(encodedBcTimeStampToken) + "}";
	}

}
