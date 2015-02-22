/*
 * Copyright 2007-2009 by the authors indicated in the @author tags.
 * All rights reserved.
 *
 * See the LICENSE file for details.
 *
 * 
 */
package org.zamia.plugin.editors.buildpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.jface.text.source.DefaultAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.texteditor.ITextEditor;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.editors.ColorManager;
import org.zamia.plugin.editors.NonRuleBasedDamagerRepairer;
import org.zamia.plugin.preferences.PreferenceConstants;


/**
 * 
 * @author Guenter Bartsch
 * BuildPathSourceViewerConfiguration -> BasicSourceViewerConfiguration by Valentin Tihhomirov
 */

public class BasicViewerConfiguration extends SourceViewerConfiguration {

	public abstract static class BasicIdentifierScanner extends RuleBasedScanner {
		
		public abstract String[] getKeywords();
		
		public IWordDetector createWordDetector() {
			return new IWordDetector() {
				
				/*public boolean isVHDLIdentifierPart(char ch) {
					return Character.isLetterOrDigit(ch) || (ch == '_');
				}

				public boolean isVHDLIdentifierStart(char ch) {
					return Character.isLetterOrDigit(ch);
				}*/

				public boolean isWordPart(char character) {
					return Character.isLetterOrDigit(character) || character == '_';
				}
			
				public boolean isWordStart(char character) {
					return isWordPart(character);
				}
			};
			
		}
		
		public abstract boolean ignoreCase();
		
		public abstract void addStrComment(List<IRule> rules, Token string, Token comment);
		
		public static Token token(String constant) {
			IPreferenceStore store = ZamiaPlugin.getDefault().getPreferenceStore();
			RGB rgb = PreferenceConverter.getColor(store, constant);
			Color color = ColorManager.getInstance().getColor(rgb);
			return new Token(new TextAttribute(color));
		}
		
		public static Token getCommentToken() {return token(PreferenceConstants.P_COMMENT);}
		public static Token getStringToken() {return token(PreferenceConstants.P_STRING);}
		public static Token getKeywordToken() {return token(PreferenceConstants.P_KEYWORD);}
		public static Token getDefaultToken() {return token(PreferenceConstants.P_DEFAULT);}
		
		public BasicIdentifierScanner () {
			IToken kwToken = token(PreferenceConstants.P_KEYWORD);
			IToken others = token(PreferenceConstants.P_DEFAULT);
			setDefaultReturnToken(others);

			List<IRule> rules = new ArrayList<IRule>();
//			rules[1] = new WhitespaceRule(new XMLWhitespaceDetector());
			WordRule wr = new WordRule(createWordDetector(), others, ignoreCase());
			
			for (String keyword: getKeywords()) 
				wr.addWord(keyword, kwToken);

			rules.add(wr);
			addStrComment(rules, getCommentToken(), getStringToken());
			
			setRules(rules.toArray(new IRule[rules.size()]));
		}
		
	}

	final static String COMMENT_CONTENT_TYPE = "__comment_partition_content_type";

	//private final ISharedTextColors fColors;
	private ITextEditor fEditor;
	public final BasicIdentifierScanner fScanner;
	private String[] fDefaultPrefixes;
	
	public BasicViewerConfiguration(BasicIdentifierScanner aScanner, String[] aDefaultPrefixes, ITextEditor aEditor) {
		fEditor= aEditor;
		fScanner = aScanner;
		fDefaultPrefixes = aDefaultPrefixes;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer aSourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(fScanner);
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		
	    IPreferenceStore store = ZamiaPlugin.getDefault().getPreferenceStore();
		
		RGB colorComment = PreferenceConverter.getColor(store, PreferenceConstants.P_COMMENT);
		
		NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(new TextAttribute(ColorManager.getInstance().getColor(colorComment)));
		reconciler.setDamager(ndr, COMMENT_CONTENT_TYPE);
		reconciler.setRepairer(ndr, COMMENT_CONTENT_TYPE);

		return reconciler;
	}

	protected ITextEditor getEditor() {
		return fEditor;
	}

	@Override
	public String[] getDefaultPrefixes(ISourceViewer aSourceViewer, String aContentType) {
		return fDefaultPrefixes;
	}
	
	@Override
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		return new DefaultAnnotationHover();
	}

}
