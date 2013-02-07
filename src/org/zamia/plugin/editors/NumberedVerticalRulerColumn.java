package org.zamia.plugin.editors;

import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.swt.graphics.RGB;
import org.zamia.instgraph.interpreter.logger.IGHitCountLogger;

/**
 * @author Anton Chepurov
 */
public class NumberedVerticalRulerColumn extends LineNumberRulerColumn {

	private final IGHitCountLogger fLineHitLogger;

	private final int fNumberOfDigits;

	public NumberedVerticalRulerColumn(IGHitCountLogger aHitCountLogger, RGB aFontColor) {
		fLineHitLogger = aHitCountLogger;
		setForeground(ColorManager.getInstance().getColor(aFontColor));

		fNumberOfDigits = String.valueOf(fLineHitLogger.getMaxCount()).length();
	}

	@Override
	protected String createDisplayString(int line) {

		int adjustedLine = line + 1;

		if (!fLineHitLogger.hasHitsAt(adjustedLine))
			return "";

		return String.valueOf(fLineHitLogger.getHitsAt(adjustedLine));
	}

	@Override
	protected int computeNumberOfDigits() {
		return fNumberOfDigits;
	}
}
