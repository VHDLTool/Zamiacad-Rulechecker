/*
 * Copyright 2007-2008 by the authors indicated in the @author tags.
 * All rights reserved.
 *
 * See the LICENSE file for details.
 *
*/

package org.zamia.plugin.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.preferences.PreferenceConstants;

/**
 * 
 * @author Guenter Bartsch
 * 
 */
public class VHDLScanner extends RuleBasedScanner {

	static class VHDLWordDetector implements IWordDetector {

		public boolean isVHDLIdentifierPart(char ch) {
			return Character.isLetterOrDigit(ch) || (ch == '_');
		}

		public boolean isVHDLIdentifierStart(char ch) {
			return Character.isLetterOrDigit(ch);
		}

		public boolean isWordPart(char character) {
			return this.isVHDLIdentifierPart(character);
		}

		public boolean isWordStart(char character) {
			return this.isVHDLIdentifierStart(character);
		}
	}

	public static String[] fgKeywords = { "abs", "access", "after", "alias", "and", "architecture", "assert", "attribute", "begin", "block", "body", "buffer", "bus", "case", "component",
			"configuration", "constant", "disconnect", "downto", "else", "elsif", "end", "entity", "exit", "file", "for", "function", "generate", "generic", "group", "guarded", "if", "impure", "in",
			"inertial", "inout", "is", "label", "library", "linkage", "literal", "loop", "map", "mod", "nand", "new", "next", "nor", "not", "null", "of", "on", "open", "or", "others", "out",
			"package", "port", "postponed", "procedural", "procedure", "process", "protected", "pure", "range", "record", "reference", "register", "reject", "rem", "report", "return", "rol", "ror",
			"select", "severity", "signal", "shared", "sla", "sll", "sra", "srl", "subtype", "then", "to", "transport", "type", "unaffected", "units", "until", "use", "variable", "wait", "when",
			"while", "with", "xnor", "xor" };

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
	
	public VHDLScanner() {

		//		RGB colorBackground = PreferenceConverter.getColor(store, PreferenceConstants.P_BACKGROUND);

		//IToken keyword = token(colorKeyword),colorManager.getColor(colorBackground),0));
		//IToken string = token(colorString),colorManager.getColor(colorBackground),0));
		//IToken other = token(colorDefault),colorManager.getColor(colorBackground),0));
		//IToken comment = token(colorComment),colorManager.getColor(colorBackground),0));

		IToken other = getDefaultToken();

		setDefaultReturnToken(other);
		List<IRule> rules = new ArrayList<IRule>();

		// Add rule for single line comments.
		rules.add(new EndOfLineRule("--", getCommentToken()));

		// Add rule for strings and character constants.
		rules.add(new SingleLineRule("\"", "\"", getStringToken(), '\\'));
		// FIXME between ' and ' should only one character to be scanned as string
		//rules.add(new SingleLineRule("\'", "\'", string, '\\')); 
		// Add word rule for keywords.
		// FIXME keyword following an underscore should be taken as normal text.
		WordRule wordRule = new WordRule(new VHDLWordDetector(), other, true);
		for (int i = 0; i < fgKeywords.length; i++) {
			wordRule.addWord(fgKeywords[i], getKeywordToken());
		}

		rules.add(wordRule);

		IRule[] result = new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}
}
