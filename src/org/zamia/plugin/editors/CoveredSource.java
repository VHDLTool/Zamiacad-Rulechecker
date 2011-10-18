package org.zamia.plugin.editors;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.zamia.SourceFile;
import org.zamia.SourceLocation;

/**
 * @author Anton Chepurov
 */
public class CoveredSource {

	private HashMap<SourceFile, Set<Integer>> fLines = new HashMap<SourceFile, Set<Integer>>();

	public CoveredSource(Collection<SourceLocation> aCovered) {

		for (SourceLocation loc : aCovered) {
			if (loc == null) {
				continue;
			}

			Set<Integer> lines = fLines.get(loc.fSF);
			if (lines == null) {
				lines = new TreeSet<Integer>();
				fLines.put(loc.fSF, lines);
			}

			lines.add(loc.fLine);
		}
	}

	public void highlight(StyledText aTextWidget, Color aColor, SourceFile aSF, boolean aHighlight) {

		if (!fLines.containsKey(aSF)) {
			return;
		}

		Set<Integer> lines = fLines.get(aSF);

		if (!aHighlight) {
			for (StyleRange range : aTextWidget.getStyleRanges()) {
				range.background = null;
				aTextWidget.setStyleRange(range);
			}
			return;
		}

		int nLines = aTextWidget.getLineCount();
		for (int i = 0; i < nLines; i++) {
			if (!lines.contains(i + 1)) {
				continue;
			}

			int off = aTextWidget.getOffsetAtLine(i);
			int length = aTextWidget.getLine(i).length();

			for (StyleRange range : aTextWidget.getStyleRanges(off, length)) {
				range.background = aColor;
				aTextWidget.setStyleRange(range);
			}
		}

	}
}
