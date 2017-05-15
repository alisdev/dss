/**
 * DSS - Digital Signature Services Copyright (C) 2015 European Commission, provided under the CEF
 * programme
 *
 * This file is part of the "DSS - Digital Signature Services" project.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 */
package eu.europa.esig.dss;

import java.io.Serializable;

/**
 * This class allows to custom text generation in the PAdES visible signature
 *
 */
@SuppressWarnings("serial")
public class RemoteSignatureImageTextParameters implements Serializable {

    /**
     * Enum to define where to add the signer name on the image
     */
    public enum SignerPosition {
        TOP, BOTTOM, RIGHT, LEFT
    }

    /**
     * This variable allows to add signer name on the image (by default, LEFT)
     */
    private SignerPosition signerNamePosition = SignerPosition.LEFT;

    /**
     * This variable defines the text to sign
     */
    private String text;

    public SignerPosition getSignerNamePosition() {
        return signerNamePosition;
    }

    public void setSignerNamePosition( SignerPosition signerNamePosition ) {
        this.signerNamePosition = signerNamePosition;
    }

    public String getText() {
        return text;
    }

    public void setText( String text ) {
        this.text = text;
    }
}
