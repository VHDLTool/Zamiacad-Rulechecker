/*
 * Copyright 2004-2009 by the authors indicated in the @author tags.
 * All rights reserved.
 *
 * See the LICENSE file for details.
 *
 */

package org.zamia.plugin.editors.buildpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.PaintManager;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.MatchingCharacterPainter;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.editors.text.TextEditor;
import org.zamia.BuildPath;
import org.zamia.ExceptionLogger;
import org.zamia.SourceLocation;
import org.zamia.ZamiaLogger;
import org.zamia.plugin.editors.ColorManager;
import org.zamia.plugin.editors.ErrorMarkEditor;
import org.zamia.plugin.editors.ZamiaPairMatcher;
import org.zamia.plugin.editors.buildpath.BasicViewerConfiguration.BasicIdentifierScanner;
import org.zamia.vhdl.ast.VHDLNode;


/**
 * 
 * @author Guenter Bartsch
 * 
 */
public class BuildPathEditor extends ErrorMarkEditor {

	public final static ZamiaLogger logger = ZamiaLogger.getInstance();

	public final static ExceptionLogger el = ExceptionLogger.getInstance();

	protected MatchingCharacterPainter fBracketPainter;

	private PaintManager fPaintManager;

	private static final RGB BRACKETS_COLOR = new RGB(160, 160, 160);

	private final static char[] BRACKETS = { '{', '}', '(', ')', '[', ']' };

	protected AbstractSelectionChangedListener fOutlineSelectionChangedListener = new OutlineSelectionChangedListener();

	public BuildPathEditor() {
		
		class Scanner extends BasicIdentifierScanner {

			public String[] getKeywords() {
				Set<String> keyWords = BuildPath.keyWords.keySet();
				return new ArrayList<>(keyWords)
						.toArray(new String[keyWords.size()]);
			}
			public boolean ignoreCase() { return true; }

			@Override
			public void addStrComment(List<IRule> rules, Token string, Token comment) {
				// Add rule for single line comments.
				rules.add(new EndOfLineRule("#", comment));

				// Add rule for strings and character constants.
				rules.add(new SingleLineRule("\"", "\"", string, '\\'));
				// FIXME between ' and ' should only one character to be scanned as string
				//rules.add(new SingleLineRule("\'", "\'", string, '\\')); 
				// Add word rule for keywords.
				// FIXME keyword following an underscore should be taken as normal text.
			}

		}
		
		setSourceViewerConfiguration(new BasicViewerConfiguration(new Scanner(), new String[] {"#", ""}, this));
	}

	protected ISourceViewer createSourceViewer(Composite aParent, IVerticalRuler aRuler, int aStyles) {
		ISourceViewer viewer = new ProjectionViewer(aParent, aRuler, getOverviewRuler(), isOverviewRulerVisible(), aStyles);
		// ensure decoration support has been created and configured.
		getSourceViewerDecorationSupport(viewer);
		return viewer;
	}

	public void createPartControl(Composite aParent) {
		super.createPartControl(aParent);

		fPaintManager = new PaintManager(getSourceViewer());

		ISourceViewer sourceViewer = getSourceViewer();

		fBracketPainter = new MatchingCharacterPainter(sourceViewer, new ZamiaPairMatcher(BRACKETS));
		fBracketPainter.setColor(ColorManager.getInstance().getColor(BRACKETS_COLOR));
		fPaintManager.addPainter(fBracketPainter);
	}
	
	class OutlineSelectionChangedListener extends AbstractSelectionChangedListener {

		public void selectionChanged(SelectionChangedEvent aEvent) {
			Object selectedObject;

			ISelection selection = aEvent.getSelection();
			selectedObject = ((IStructuredSelection) selection).getFirstElement();

			if (selectedObject instanceof VHDLNode) {
				VHDLNode io = (VHDLNode) selectedObject;

				SourceLocation location = io.getLocation();
				if (location != null) {

					try {
						int offset = getSourceViewer().getDocument().getLineOffset(location.fLine - 1) + location.fCol - 1;
						selectAndReveal(offset, 1);
					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}
	}

//	public void updateColors() {
//		// FIXME: implement
//	}

}
