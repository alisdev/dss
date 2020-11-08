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
package eu.europa.esig.dss.ws.signature.dto.parameters;

import java.io.Serializable;

/**
 * Parameters for a visible signature creation
 *
 */
@SuppressWarnings("serial")
public class RemoteSignatureImageParameters implements Serializable {

    public static final int DEFAULT_PAGE = 1;

     /**
     * This variable contains the image to use (company logo,...)
     */
     private byte[] image;

    /**
     * This variable defines the page where the image will appear (1st page by default)
     */
    private int page = DEFAULT_PAGE;

    /**
     * This variable defines the position of the image in the PDF page (X axis)
     */
    private float xAxis;

    /**
     * This variable defines the position of the image in the PDF page (Y axis)
     */
    private float yAxis;
    
    /**
     * This variable defines height of the image
     */
    private float height;
    
    /**
     * This variable defines width of the image
     */
    private float width;

	/**
	 * This variable defines the DPI of the image
	 */
	private Integer dpi;
	
    public Integer getDpi() {
		return dpi;
	}

	public void setDpi(Integer dpi) {
		this.dpi = dpi;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}
	
	
	public float getWidht() {
		return width;
	}

	public void setWidht(float widht) {
		this.width = widht;
	}

	/**
     * This variable is use to defines the text to generate on the image
     */
    private RemoteSignatureImageTextParameters textParameters;

     public byte[] getImage() {
     return image;
     }
    
     public void setImage( byte[] image ) {
     this.image = image;
     }

    private String signatureReason;

    private String signerLocation;

    public float getxAxis() {
        return xAxis;
    }

    public void setxAxis( float xAxis ) {
        this.xAxis = xAxis;
    }

    public float getyAxis() {
        return yAxis;
    }

    public void setyAxis( float yAxis ) {
        this.yAxis = yAxis;
    }

    public int getPage() {
        return page;
    }

    public void setPage( int page ) {
        this.page = page;
    }

    public RemoteSignatureImageTextParameters getTextParameters() {
        return textParameters;
    }

    public void setTextParameters( RemoteSignatureImageTextParameters textParameters ) {
        this.textParameters = textParameters;
    }

    public String getSignatureReason() {
        return signatureReason;
    }

    public void setSignatureReason( String signatureReason ) {
        this.signatureReason = signatureReason;
    }

    public String getSignerLocation() {
        return signerLocation;
    }

    public void setSignerLocation( String signerLocation ) {
        this.signerLocation = signerLocation;
    }

}
