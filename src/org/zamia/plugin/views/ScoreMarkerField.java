package org.zamia.plugin.views;

import org.eclipse.ui.views.markers.MarkerField;
import org.eclipse.ui.views.markers.MarkerItem;
import org.zamia.plugin.editors.DebugReportVisualizer;

public class ScoreMarkerField extends MarkerField {

	public ScoreMarkerField() {
		super();
	}
	
	@Override
	public String getValue(MarkerItem item) {
		return item.getAttributeValue(DebugReportVisualizer.SUSP_SCORE_ID, "");
	}
}
