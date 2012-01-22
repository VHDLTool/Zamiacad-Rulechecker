/* 
 * Copyright 2008 by the authors indicated in the @author tags. 
 * All rights reserved. 
 * 
 * See the LICENSE file for details.
 * 
 * Created by Guenter Bartsch on Aug 18, 2008
 */
package org.zamia.plugin.editors;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.IInformationProviderExtension;
import org.eclipse.jface.text.information.IInformationProviderExtension2;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ide.ResourceUtil;
import org.zamia.ASTNode;
import org.zamia.ExceptionLogger;
import org.zamia.SourceFile;
import org.zamia.SourceLocation;
import org.zamia.ZamiaException;
import org.zamia.ZamiaLogger;
import org.zamia.ZamiaProject;
import org.zamia.analysis.SourceLocation2AST;
import org.zamia.analysis.ast.ASTDeclarationSearch;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.ZamiaProjectMap;
import org.zamia.plugin.editors.OpenDeclarationAction.LocatedDeclaration;
import org.zamia.vhdl.ast.VHDLNode;

/**
 * 
 * @author Guenter Bartsch
 * 
 */

public class VHDLInformationProvider implements IInformationProvider, IInformationProviderExtension, IInformationProviderExtension2 {

	public final static ZamiaLogger logger = ZamiaLogger.getInstance();

	public final static ExceptionLogger el = ExceptionLogger.getInstance();

	private IEditorPart fEditor;

	public VHDLInformationProvider(IEditorPart aEditor) {
		fEditor = aEditor;
	}

	public String getInformation(ITextViewer textViewer, IRegion subject) {
		Object o = getInformation2(textViewer, subject); 
		return o == null ? null : o.toString();
	}

	public IRegion getSubject(ITextViewer aTextViewer, int aOffset) {

		IDocument doc = aTextViewer.getDocument();
		try {
			return senseIdentifierRange(doc, aOffset);
		} catch (BadLocationException e) {
			// probably hit EOF
		}
		return new Region(aOffset, 0);

	}
	
	public static Region senseIdentifierRange(IDocument doc, int start) throws BadLocationException {
		int end = start;
		while (start >= 0) {
			char c = doc.getChar(start);
			if (!Character.isJavaIdentifierPart(c) && c != '.')
				break;
			start--;
		}
		while (true) {
			char c = doc.getChar(end);
			if (!Character.isJavaIdentifierPart(c) && c != '.')
				break;
			end++;
		}
		return new Region(start+1, end - start-1);
	}
	public Object getInformation2(ITextViewer aTextViewer, IRegion subject) {
		return getInformationStaticMethod(subject.getOffset());
	}
	
	public static Object getInformationStaticMethod(int offset) {
		try {
  			OpenDeclarationAction oda = new OpenDeclarationAction();
			oda.processSelection(offset);
			LocatedDeclaration ld = oda.findDeclaration();
			if (ld != null)
				return ld.fItem;
  		} catch (BadLocationException e) {
  			el.logException(e);
  		}
		return null;
	}
	
	public IInformationControlCreator getInformationPresenterControlCreator() {

		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				//				return new DefaultInformationControl(parent, "Result of declaration search.");
				return new DefaultInformationControl(parent);
			}
		};
	}
}
