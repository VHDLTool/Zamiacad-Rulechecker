package org.zamia.plugin.editors;

import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Composite;
import org.zamia.plugin.editors.buildpath.BasicViewerConfiguration;

public class PythonEditor extends ErrorMarkEditor {
	
	public static class PythonConfiguration extends BasicViewerConfiguration {

		static class Scanner extends BasicIdentifierScanner {

			@Override
			public void addStrComment(List<IRule> rules, Token string, Token comment) {
				rules.add(new SingleLineRule("'", "'", string));
				rules.add(new SingleLineRule("\"", "\"", string)); // curiously, this already covers the multiline strings
				rules.add(new MultiLineRule("\"\"\"", "\"\"\"", string));
				rules.add(new MultiLineRule("'''", "'''", string));
				rules.add(new EndOfLineRule("#", comment));
			}
			
			public boolean ignoreCase() {return false;}

			public String[] getKeywords() {
				return new String[] {"and","del","from","not","while","as","elif","global","or","with","assert","else","if","pass","yield","break","except","import","print","class","exec","in","raise","continue","finally","is","return","def","for","lambda","try"};
			} 

			
		}
		
		public PythonConfiguration(PythonEditor editor) {
			super(new Scanner(), new String[] {"#", ""}, editor);
		}
		
		public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
			return new String[] {IDocument.DEFAULT_CONTENT_TYPE,};
		}

		public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
			PresentationReconciler reconciler = new PresentationReconciler();

			DefaultDamagerRepairer dr = new DefaultDamagerRepairer(fScanner);
			reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
			reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
			
			return reconciler;
		}
		
	}
	
	public PythonEditor() {
		setSourceViewerConfiguration(new PythonConfiguration(this));
	}

	
}


