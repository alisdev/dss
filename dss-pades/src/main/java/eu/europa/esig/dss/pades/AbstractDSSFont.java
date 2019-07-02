package eu.europa.esig.dss.pades;

import java.awt.Font;

public abstract class AbstractDSSFont implements DSSFont {
	
	protected static final float DEFAULT_TEXT_SIZE = 8f; // alisdev 8f
	
	protected Font javaFont;
	protected float size = DEFAULT_TEXT_SIZE;

	@Override
	public Font getJavaFont() {
		return javaFont;
	}

	@Override
	public float getSize() {
		return size;
	}

}
