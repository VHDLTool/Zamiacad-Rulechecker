package org.zamia.plugin.editors;

import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.swt.graphics.RGB;
import org.zamia.SourceRanges;

/**
 * @author Anton Chepurov
 */
public class NumberedVerticalRulerColumn extends LineNumberRulerColumn {

	private final SourceRanges fSourceRanges;

	public NumberedVerticalRulerColumn(SourceRanges aSourceRanges, RGB aFontColor) {
		fSourceRanges = aSourceRanges;
		setForeground(ColorManager.getInstance().getColor(aFontColor));
	}

	@Override
	protected String createDisplayString(int line) {

		if (!fSourceRanges.hasLine(line))
			return "";

		return String.valueOf(fSourceRanges.getCount(line));
	}
}
