package org.zamia.plugin.editors;

import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.swt.graphics.RGB;
import org.zamia.SourceRanges;

/**
 * @author Anton Chepurov
 */
public class NumberedVerticalRulerColumn extends LineNumberRulerColumn {

	private final SourceRanges fSourceRanges;

	private final int fNumberOfDigits;

	public NumberedVerticalRulerColumn(SourceRanges aSourceRanges, RGB aFontColor) {
		fSourceRanges = aSourceRanges;
		setForeground(ColorManager.getInstance().getColor(aFontColor));

		fNumberOfDigits = String.valueOf(fSourceRanges.getMaxCount()).length();
	}

	@Override
	protected String createDisplayString(int line) {

		if (!fSourceRanges.hasLine(line))
			return "";

		return String.valueOf(fSourceRanges.getCount(line));
	}

	@Override
	protected int computeNumberOfDigits() {
		return fNumberOfDigits;
	}
}
