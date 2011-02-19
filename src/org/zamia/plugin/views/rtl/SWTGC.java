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

	public SWTGC(GC aGC) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void start(int aWidth, int aHeight) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFont(VGFont aFont) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getFontHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int textWidth(String aText) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setLineWidth(int aWidth) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setForeground(VGColor aColor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBackground(VGColor aColor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawLine(int aX1, int aY1, int aX2, int aY2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawOval(int aX, int aY, int aXR, int aYR) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fillOval(int aX, int aY, int aXR, int aYR) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawText(String aLabel, int aX, int aY, boolean aTransparent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawRectangle(int aX, int aY, int aW, int aH) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fillRectangle(int aX, int aY, int aW, int aH) {
		// TODO Auto-generated method stub

	}

}
