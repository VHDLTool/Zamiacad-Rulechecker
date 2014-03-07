package org.zamia.plugin.editors;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.zamia.SourceFile;
import org.zamia.ZamiaLogger;
import org.zamia.ZamiaProject;
import org.zamia.instgraph.interpreter.logger.IGHitCountLogger;
import org.zamia.instgraph.interpreter.logger.Report;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.ZamiaProjectMap;
import org.zamia.util.Pair;

import static org.zamia.plugin.editors.DebugReportVisualizer.MarkerType.BUG;
import static org.zamia.plugin.editors.DebugReportVisualizer.MarkerType.GREEN;
import static org.zamia.plugin.editors.DebugReportVisualizer.MarkerType.ORANGE;
import static org.zamia.plugin.editors.DebugReportVisualizer.MarkerType.POINT;
import static org.zamia.plugin.editors.DebugReportVisualizer.MarkerType.RED;
import static org.zamia.plugin.editors.DebugReportVisualizer.MarkerType.SIMULATED_LINE;
import static org.zamia.plugin.editors.DebugReportVisualizer.MarkerType.STATIC_LINE;
import static org.zamia.plugin.editors.DebugReportVisualizer.MarkerType.YELLOW;

/**
 * @author Anton Chepurov
 */
public class DebugReportVisualizer {

	private static final Pattern STARTS_WITH_WORD_CHAR_PATTERN = Pattern.compile("\\w.*");

	private static final ZamiaLogger logger = ZamiaLogger.getInstance();

	private static HashMap<ZamiaProject, DebugReportVisualizer> fVisualizers;

	private ZamiaEditor fEditor;

	private Report fAssignments, fConditions;

	private IGHitCountLogger fSimulatedLines, fStaticalLines;

	public static DebugReportVisualizer getInstance(ZamiaProject aZprj) {
		if (fVisualizers == null) {
			fVisualizers = new HashMap<ZamiaProject, DebugReportVisualizer>();
		}
		if (!fVisualizers.containsKey(aZprj)) {
			fVisualizers.put(aZprj, new DebugReportVisualizer());
		}
		return fVisualizers.get(aZprj);
	}

	public void setAssignments(Report aAssignments) {
		fAssignments = aAssignments;
		createPointMarkersForSuspects();
		visualizeDebugReport();
	}

	public void setConditions(Report aConditions) {
		fConditions = aConditions;
	}

	public void setSimulatedLines(IGHitCountLogger aSimulatedLines) {
		fSimulatedLines = aSimulatedLines;
		highlightDeprecatedLines();
	}

	public void setStaticalLines(IGHitCountLogger aStaticalLines) {
		fStaticalLines = aStaticalLines;
		highlightDeprecatedLines();
	}

	public void clearAllSelection() {
		fAssignments = null;
		fConditions = null;
		fSimulatedLines = null;
		fStaticalLines = null;
		processOpenEditors(MARKERS_CLEARER);
	}

	public void highlightDeprecatedLines() {
		processOpenEditors(HIGHLIGHTER);
	}

	private void visualizeDebugReport() {
		processOpenEditors(DEBUG_REPORT_VISUALIZER);
	}

	public void highlightDeprecatedLines(ZamiaEditor aZamiaEditor) {

		//todo: refactor this chain of two-fold methods
		fEditor = aZamiaEditor;

		if (!isActivePageLoaded())
			return;

		boolean doCoverage = fEditor.isShowingCoverage();
		boolean doStaticAnalysis = fStaticalLines != null;

		highlightText(doCoverage, doStaticAnalysis);

		addHitCountColumn(doCoverage);
	}

	private void highlightText(boolean aDoCoverage, boolean aDoStaticAnalysis) {

		IResource resource = fEditor.getResource();
		if (resource == null)
			return;

		clearDeprecatedMarkers();

		SourceFile file = fEditor.getSourceFile();

		if (!(fSimulatedLines != null && fSimulatedLines.hasLoggerForFile(file)
				|| fStaticalLines != null && fStaticalLines.hasLoggerForFile(file))) {
			return;
		}

		IGHitCountLogger coverageRanges = fSimulatedLines != null ? ((IGHitCountLogger) fSimulatedLines.getLogger(file)) : null;
		IGHitCountLogger staticalRanges = fStaticalLines != null ? ((IGHitCountLogger) fStaticalLines.getLogger(file)) : null;

		StyledText textWidget = fEditor.getTextWidget();
		int nLines = textWidget.getLineCount();
		for (int i = 0; i < nLines; i++) {
			int adjustedLine = i + 1;
			boolean dynamic = aDoCoverage && coverageRanges != null && coverageRanges.hasHitsAt(adjustedLine);
			boolean statical = aDoStaticAnalysis && staticalRanges != null && staticalRanges.hasHitsAt(adjustedLine);

			String markerType;
			String message;
			if (dynamic) {
				if (statical) {
					markerType = BUG.id;
					message = "A bug is probably located here";
				} else {
					markerType = SIMULATED_LINE.id;
					message = "This line was executed " + coverageRanges.getHitsAt(adjustedLine) + " times during current simulation run";
				}
			} else {
				if (statical) {
					markerType = STATIC_LINE.id;
					message = "Through-signal reference search result";
				} else {
					continue;
				}
			}

			int off = textWidget.getOffsetAtLine(i);
			int length = textWidget.getLine(i).length();

			createInfoMarker(markerType, message, i, off, length, resource);
		}
	}

	private void clearDeprecatedMarkers() {
		try {
			deleteMarkers(SIMULATED_LINE.id);
			deleteMarkers(STATIC_LINE.id);
			deleteMarkers(BUG.id);
		} catch (CoreException e) {
			logger.debug("DebugReportVisualizer: failed to delete debug markers", e);
		}
	}

	private void addHitCountColumn(boolean aDoCoverage) {

		SourceFile file = fEditor.getSourceFile();

		NumberedVerticalRulerColumn hitCountColumn = (aDoCoverage && fSimulatedLines != null && fSimulatedLines.hasLoggerForFile(file))
				? new NumberedVerticalRulerColumn(((IGHitCountLogger) fSimulatedLines.getLogger(file)), new RGB(33, 222, 75)) /* malachite */
				: null;

		fEditor.addHitCountColumn(hitCountColumn);
	}

	private void createPointMarkersForSuspects() {

		try {
			Report suspects = fAssignments.getSuspects();

			for (SourceFile file : suspects.getFiles()) {

				IFile iFile = ZamiaPlugin.getIFile(file, ZamiaProjectMap.getProject(ZamiaPlugin.findCurrentProject()));

				iFile.deleteMarkers(POINT.id, false, IFile.DEPTH_INFINITE);

				TextFileDocumentProvider provider = new TextFileDocumentProvider();
				provider.connect(iFile);
				IDocument document = provider.getDocument(iFile);

				for (Report.FileReport.ItemLine line : suspects.getLines(file)) {

					int lineNr = line.getLine() - 1;
					int pointOff = document.getLineOffset(lineNr) + line.getCol() - 1;

					// put it into Markers view + "debugmarkerpoint" as marker extension id (not "org.zamia.plugin.debugmarkerpoint"!!!)
					String msg = ""; String score = "";
					
					if (line.isSuspect()) {
						msg = line.getMarkerMessage();
						score = String.format("%.3f", line.getV1());
					}

					IMarker marker = createMarker(POINT.id, IMarker.SEVERITY_WARNING, msg, lineNr, pointOff, 1, iFile);
					marker.setAttribute(SUSP_SCORE_ID, score);
					
				}
			}

			ZamiaPlugin.showMarkers();

		} catch (CoreException e) {
			logger.debug("DebugReportVisualizer: failed to obtain document provider for file: %s", e.getMessage());
		} catch (BadLocationException e) {
			logger.debug("DebugReportVisualizer: failed to obtain line offset in file: %s", e.getMessage());
		}
	}

	void highlightAssignments(ZamiaEditor aEditor) {
		fEditor = aEditor;
		clearDebugMarkers();
		highlightReport(fAssignments, null);
	}

	void highlightConditions(ZamiaEditor aEditor, Pair<Integer, Integer> aSelection) {
		fEditor = aEditor;
		clearDebugMarkers();
		highlightReport(fAssignments, aSelection);
		highlightReport(fConditions, aSelection);
	}

	private void highlightReport(Report aReport, Pair<Integer, Integer> aSelection) {

		if (!isActivePageLoaded())
			return;

		if (aReport == null) {
			return;
		}

		SourceFile file = fEditor.getSourceFile();
		if (!aReport.hasFile(file)) {
			return;
		}

		ExistingMarkers existingMarkers = new ExistingMarkers();

		createMarkersFrom(aReport.getRedSuspects(file), RED.id, aSelection, existingMarkers);

		createMarkersFrom(aReport.getOrangeSuspects(file), ORANGE.id, aSelection, existingMarkers);

		createMarkersFrom(aReport.getYellowSuspects(file), YELLOW.id, aSelection, existingMarkers);

		if (!(aReport == fAssignments && aSelection != null)) {
			createMarkersFrom(aReport.getGreen(file), GREEN.id, aSelection, existingMarkers);
		}
	}

	private void clearDebugMarkers() {
		try {
			deleteMarkers(RED.id);
			deleteMarkers(ORANGE.id);
			deleteMarkers(YELLOW.id);
			deleteMarkers(GREEN.id);
		} catch (CoreException e) {
			logger.debug("DebugReportVisualizer: failed to delete debug markers", e);
		}
	}

	private void deleteMarkers(String aMarkerType) throws CoreException {
		IResource resource = fEditor.getResource();
		if (resource != null)
			resource.deleteMarkers(aMarkerType, false, IResource.DEPTH_INFINITE);
	}

	private void createMarkersFrom(Report aReport, String aMarkerType, Pair<Integer, Integer> aSelection, ExistingMarkers aExistingMarkers) {

		Collection<Report.FileReport.ItemLine> itemLines = aReport.getLines(fEditor.getSourceFile());

		if (itemLines.isEmpty()) {
			return;
		}

		IResource resource = fEditor.getResource();

		StyledText textWidget = fEditor.getTextWidget();

		boolean asPoint = aSelection != null;

		for (Report.FileReport.ItemLine line : itemLines) {

			int lineNr = line.getLine() - 1;

			if (aSelection != null && isOutsideSelection(lineNr, aSelection)) {
				continue;
			}

			int off = textWidget.getOffsetAtLine(lineNr);
			int length = textWidget.getLine(lineNr).length();

			if (asPoint) {

				int colNr = line.getCol() - 1;

				off = off + colNr;
				length = computeIdentifierLength(textWidget, lineNr, colNr);

				length = extendIdentifierLengthToArrow(textWidget, lineNr, colNr, length);

				if (aExistingMarkers.containsPoint(lineNr, colNr)) {
					continue;
				}
				aExistingMarkers.addPoint(lineNr, colNr);

			} else {

				if (aExistingMarkers.containsLine(lineNr)) {
					continue;
				}
				aExistingMarkers.addLine(lineNr);
			}

			createInfoMarker(aMarkerType, null, lineNr, off, length, resource);
		}
	}

	private void createInfoMarker(String aMarkerType, String aMessage, int aLineNr, int aOff, int aLength, IResource aResource) {
		createMarker(aMarkerType, IMarker.SEVERITY_INFO, aMessage, aLineNr, aOff, aLength, aResource);
	}

	private IMarker createMarker(String aMarkerType, int aSeverity, String aMessage, int aLineNr, int aOff, int aLength, IResource aResource) {
		try {
			IMarker marker = aResource.createMarker(aMarkerType);
			marker.setAttribute(IMarker.SEVERITY, aSeverity);
			marker.setAttribute(IMarker.TRANSIENT, true);
			marker.setAttribute(IMarker.LINE_NUMBER, aLineNr);
			marker.setAttribute(IMarker.CHAR_START, aOff);
			marker.setAttribute(IMarker.CHAR_END, aOff + aLength);
			if (aMessage != null) {
				marker.setAttribute(IMarker.MESSAGE, aMessage);
			}
			return marker;
		} catch (CoreException e) {
			logger.debug("DebugReportVisualizer: failed to add marker", e);
		}
		return null;
	}

	private boolean isOutsideSelection(int aLineNr, Pair<Integer, Integer> aSelection) {
		return aLineNr < aSelection.getFirst() || aLineNr > aSelection.getSecond();
	}

	private int computeIdentifierLength(StyledText textWidget, int aLine, int aCol) {

		String line = textWidget.getLine(aLine);

		line = line.substring(aCol);

		boolean startsWithWordChar = STARTS_WITH_WORD_CHAR_PATTERN.matcher(line).matches();

		if (startsWithWordChar) {

			return line.split("\\W", 2)[0].length();

		} else if (line.startsWith("\"")) {

			return line.substring(0, line.indexOf("\"", 1) + 1).length();

		} else if (line.startsWith("\'")) {

			return line.substring(0, line.indexOf("\'", 1) + 1).length();

		} else {

			return line.split("[\\s\\(\\)'\"\\w]", 2)[0].length();
		}
	}

	private int extendIdentifierLengthToArrow(StyledText textWidget, int aLine, int aCol, int aLength) {

		String line = textWidget.getLine(aLine);

		line = line.substring(aCol);

		String wLine = line.substring(0, aLength);

		if (!wLine.equalsIgnoreCase("when")) {
			return aLength;
		}

		int ret = 0;
		int n = textWidget.getLineCount();

		while (true) {

			if (line.contains("=>")) {
				ret += line.indexOf("=>") + 2;
				break;
			}
			ret += line.length() + 1; // + line.separator

			aLine++;
			if (aLine == n) {
				return aLength;
			}
			line = textWidget.getLine(aLine);
		}
		return ret;
	}

	public final static String SUSP_SCORE_ID = "suspiciousness score";
	static enum MarkerType {
		SIMULATED_LINE("org.zamia.plugin.coveragemarker"),
		STATIC_LINE("org.zamia.plugin.staticanalysismarker"),
		BUG("org.zamia.plugin.bugsuspectmarker"),
		GREEN("org.zamia.plugin.debugmarkergreen"),
		YELLOW("org.zamia.plugin.debugmarkeryellow"),
		ORANGE("org.zamia.plugin.debugmarkerorange"),
		RED("org.zamia.plugin.debugmarkerred"),
		POINT("org.zamia.plugin.debugmarkerpoint");

		private final String id;

		MarkerType(String id) {
			this.id = id;
		}
	}

	abstract static class EditorProcessor {
		abstract void processEditor(ZamiaEditor aZamiaEditor);
	}

	private static boolean isActivePageLoaded() {
		return ZamiaPlugin.getPage() != null;
	}

	private void processOpenEditors(EditorProcessor aProcessor) {

		IWorkbenchPage page = ZamiaPlugin.getPage();

		for (IEditorReference ref : page.getEditorReferences()) {
			IEditorPart openEditor = ref.getEditor(false);

			if (openEditor instanceof ZamiaEditor) {
				ZamiaEditor zamiaEditor = (ZamiaEditor) openEditor;

				aProcessor.processEditor(zamiaEditor);
			}
		}
	}

	private static final EditorProcessor HIGHLIGHTER = new EditorProcessor() {
		@Override
		void processEditor(ZamiaEditor aZamiaEditor) {

			DebugReportVisualizer visualizer = getInstance(aZamiaEditor.getZPrj());

			visualizer.highlightDeprecatedLines(aZamiaEditor);
		}
	};

	private static final EditorProcessor DEBUG_REPORT_VISUALIZER = new EditorProcessor() {
		@Override
		void processEditor(ZamiaEditor aZamiaEditor) {

			DebugReportVisualizer visualizer = getInstance(aZamiaEditor.getZPrj());

			visualizer.highlightAssignments(aZamiaEditor);
		}
	};

	private static final EditorProcessor MARKERS_CLEARER = new EditorProcessor() {
		@Override
		void processEditor(ZamiaEditor aZamiaEditor) {

			DebugReportVisualizer visualizer = getInstance(aZamiaEditor.getZPrj());

			visualizer.fEditor = aZamiaEditor;

			visualizer.clearDebugMarkers();

			visualizer.clearDeprecatedMarkers();
		}
	};

	private static class ExistingMarkers {

		private HashSet<Integer> fLines = new HashSet<Integer>();

		private HashMap<Integer, HashSet<Integer>> fPoints = new HashMap<Integer, HashSet<Integer>>();

		public void addLine(int aLine) {
			fLines.add(aLine);
		}

		public boolean containsLine(int aLine) {
			return fLines.contains(aLine);
		}

		public void addPoint(int aLine, int aCol) {
			if (!fPoints.containsKey(aLine)) {
				fPoints.put(aLine, new HashSet<Integer>());
			}
			HashSet<Integer> cols = fPoints.get(aLine);
			cols.add(aCol);
		}

		public boolean containsPoint(int aLine, int aCol) {
			return fPoints.containsKey(aLine) && fPoints.get(aLine).contains(aCol);
		}
	}
}
