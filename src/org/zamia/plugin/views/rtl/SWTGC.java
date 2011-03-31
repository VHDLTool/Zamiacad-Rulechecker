/* 
 * Copyright 2011 by the authors indicated in the @author tags. 
 * All rights reserved. 
 * 
 * See the LICENSE file for details.
 * 
 * Created by Guenter Bartsch on Feb 18, 2011
 */
package org.zamia.plugin.views.rtl;

import org.eclipse.swt.graphics.GC;
import org.zamia.vg.VGGC;

/**
 * 
 * @author Guenter Bartsch
 *
 */

public class SWTGC implements VGGC {

	private RTLView fRTLView;

	private GC fGC;

	private VGFont fFont;

	private int fLineWidth = 1;

	public SWTGC(GC aGC, RTLView aRTLView) {
		fGC = aGC;
		fRTLView = aRTLView;
	}

	@Override
	public void start(int aWidth, int aHeight) {
	}

	@Override
	public void finish() {
	}

	@Override
	public void setFont(VGFont aFont) {
		fFont = aFont;
		switch (fFont) {
		case LARGE:
			fGC.setFont(fRTLView.getLargeFont());
			break;
		case NORMAL:
			fGC.setFont(fRTLView.getNormalFont());
			break;
		case SMALL:
			fGC.setFont(fRTLView.getSmallFont());
			break;
		}
	}

	@Override
	public int getFontHeight() {
		switch (fFont) {
		case LARGE:
			return RTLView.LARGE_FONT_SIZE;
		case NORMAL:
			return RTLView.NORMAL_FONT_SIZE;
		case SMALL:
			return RTLView.SMALL_FONT_SIZE;
		}
		return RTLView.NORMAL_FONT_SIZE;
	}

	@Override
	public int textWidth(String aText) {
		return (int) (fGC.textExtent(aText).x / fRTLView.getZoomFactor());
	}

	@Override
	public void setLineWidth(int aWidth) {
		fLineWidth = aWidth;
		fGC.setLineWidth(fLineWidth);
	}

	@Override
	public void setForeground(VGColor aColor) {
		switch (aColor) {
		case BACKGROUND:
			fGC.setForeground(fRTLView.getColorScheme().getBackgroundColor());
			break;
		case HIGHLIGHT:
			fGC.setForeground(fRTLView.getColorScheme().getHilightColor());
			break;
		case MODULE:
			fGC.setForeground(fRTLView.getColorScheme().getModuleColor());
			break;
		case MODULE_LABEL:
			fGC.setForeground(fRTLView.getColorScheme().getModuleLabelColor());
			break;
		case SIGNAL:
			fGC.setForeground(fRTLView.getColorScheme().getSignalColor());
			break;
		}
	}

	@Override
	public void setBackground(VGColor aColor) {
		switch (aColor) {
		case BACKGROUND:
			fGC.setBackground(fRTLView.getColorScheme().getBackgroundColor());
			break;
		case HIGHLIGHT:
			fGC.setBackground(fRTLView.getColorScheme().getHilightColor());
			break;
		case MODULE:
			fGC.setBackground(fRTLView.getColorScheme().getModuleColor());
			break;
		case MODULE_LABEL:
			fGC.setBackground(fRTLView.getColorScheme().getModuleLabelColor());
			break;
		case SIGNAL:
			fGC.setBackground(fRTLView.getColorScheme().getSignalColor());
			break;
		}
	}

	@Override
	public void drawLine(int aX1, int aY1, int aX2, int aY2) {
		fGC.drawLine(fRTLView.tX(aX1), fRTLView.tY(aY1), fRTLView.tX(aX2), fRTLView.tY(aY2));
	}

	@Override
	public void drawOval(int aX, int aY, int aXR, int aYR) {
		fGC.drawOval(fRTLView.tX(aX - aXR), fRTLView.tY(aY - aYR), fRTLView.tW(2 * aXR), fRTLView.tH(2 * aYR));
	}

	@Override
	public void fillOval(int aX, int aY, int aXR, int aYR) {
		fGC.fillOval(fRTLView.tX(aX - aXR), fRTLView.tY(aY - aYR), fRTLView.tW(2 * aXR), fRTLView.tH(2 * aYR));
	}

	@Override
	public void drawText(String aLabel, int aX, int aY, boolean aTransparent) {

		int h = fGC.getFontMetrics().getAscent();

		//		int h = (int) (fGC.textExtent(aLabel).x / fRTLView.getZoomFactor());

		fGC.drawText(aLabel, fRTLView.tX(aX), fRTLView.tY(aY) - h, aTransparent);
	}

	@Override
	public void drawRectangle(int aX, int aY, int aW, int aH) {
		fGC.drawRectangle(fRTLView.tX(aX), fRTLView.tY(aY), fRTLView.tW(aW), fRTLView.tH(aH));
	}

	@Override
	public void fillRectangle(int aX, int aY, int aW, int aH) {
		fGC.fillRectangle(fRTLView.tX(aX), fRTLView.tY(aY), fRTLView.tW(aW), fRTLView.tH(aH));
		fGC.drawRectangle(fRTLView.tX(aX), fRTLView.tY(aY), fRTLView.tW(aW), fRTLView.tH(aH));
	}

}
