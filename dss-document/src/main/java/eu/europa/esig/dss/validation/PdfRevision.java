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
package eu.europa.esig.dss.validation;

import java.util.List;

/**
 * The usage of this interface permit the user to choose the underlying PDF library use to created PDF signatures.
 */
// TODO : move to dss-pades module, remove PAdES-specific methods and remove from AdvancedSignature
public interface PdfRevision {
	
	/**
	 * Returns a PDF Signature Dictionary info container
	 * 
	 * @return {@code PdfSignatureDictionary}
	 */
	PdfSignatureDictionary getPdfSigDictInfo();
	
	/**
	 * Returns a list of signature field names that refer the current object
	 * 
	 * @return list of {@link String} field names
	 */
	List<String> getFieldNames();

}
