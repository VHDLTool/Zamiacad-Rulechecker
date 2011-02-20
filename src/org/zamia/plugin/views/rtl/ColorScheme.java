/*
 * Copyright 2005-2008,2011 by the authors indicated in the @author tags.
 * All rights reserved.
 *
 * See the LICENSE file for details.
 *
 * Created by guenter on Feb 4, 2006
 */

package org.zamia.plugin.views.rtl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * @author Guenter Bartsch
 *
 */
public abstract class ColorScheme {

	protected Color black;
	protected Color blue;
	protected Color white;
	protected Color green;
	protected Color yellow;
	protected Color red;
	protected Color gray;

	public ColorScheme(Display aDisplay) {
		black = aDisplay.getSystemColor(SWT.COLOR_BLACK);
		blue = aDisplay.getSystemColor(SWT.COLOR_BLUE);
		white = aDisplay.getSystemColor(SWT.COLOR_WHITE);
		green = aDisplay.getSystemColor(SWT.COLOR_DARK_GREEN);
		yellow = aDisplay.getSystemColor(SWT.COLOR_YELLOW);
		red = aDisplay.getSystemColor(SWT.COLOR_RED);
		gray = aDisplay.getSystemColor(SWT.COLOR_GRAY);
	}

	public abstract Color getBackgroundColor();
	public abstract Color getSignalColor();
	public abstract Color getModuleColor();
	public abstract Color getModuleLabelColor();
	public abstract Color getHilightColor();
}
