/*
 * Copyright 2005-2008,2011 by the authors indicated in the @author tags.
 * All rights reserved.
 *
 * See the LICENSE file for details.
 *
 * Created by guenter on Feb 4, 2006
 */

package org.zamia.plugin.views.rtl;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.preferences.PreferenceConstants;

/**
 * Eclipse preferences based color scheme
 * 
 * @author Guenter Bartsch
 *
 */
public class ColorScheme {

	private Color fColorBackground;

	private Color fColorSignal;

	private Color fColorModule;

	private Color fColorModuleLabel;

	private Color fColorHilight;

	public ColorScheme(Display aDisplay) {

		IPreferenceStore store = ZamiaPlugin.getDefault().getPreferenceStore();

		RGB rgb = PreferenceConverter.getColor(store, PreferenceConstants.P_BACKGROUND);
		fColorBackground = new Color(aDisplay, rgb);
		rgb = PreferenceConverter.getColor(store, PreferenceConstants.P_SIGNAL);
		fColorSignal = new Color(aDisplay, rgb);
		rgb = PreferenceConverter.getColor(store, PreferenceConstants.P_MODULE);
		fColorModule = new Color(aDisplay, rgb);
		rgb = PreferenceConverter.getColor(store, PreferenceConstants.P_MODULE_LABEL);
		fColorModuleLabel = new Color(aDisplay, rgb);
		rgb = PreferenceConverter.getColor(store, PreferenceConstants.P_HILIGHT);
		fColorHilight = new Color(aDisplay, rgb);
	}

	public Color getBackgroundColor() {
		return fColorBackground;
	}

	public Color getSignalColor() {
		return fColorSignal;
	}

	public Color getModuleColor() {
		return fColorModule;
	}

	public Color getModuleLabelColor() {
		return fColorModuleLabel;
	}

	public Color getHilightColor() {
		return fColorHilight;
	}
}
