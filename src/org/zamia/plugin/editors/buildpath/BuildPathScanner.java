/*
 * Copyright 2007-2009,2011 by the authors indicated in the @author tags.
 * All rights reserved.
 *
 * See the LICENSE file for details.
 *
*/

package org.zamia.plugin.editors.buildpath;

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
import org.eclipse.swt.graphics.RGB;
import org.zamia.BuildPath;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.editors.ColorManager;
import org.zamia.plugin.editors.VHDLScanner;
import org.zamia.plugin.preferences.PreferenceConstants;


/**
 * 
 * @author Guenter Bartsch
 * 
 */
public class BuildPathScanner extends RuleBasedScanner {

	static class VHDLWordDetector implements IWordDetector {

		public boolean isVHDLIdentifierPart(char ch) {
			if (Character.isLetter(ch)) {
				return true;
			} else {
				if (Character.toString(ch).equals("_")) {
					return true;
				}
			}
			return false;
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

	public BuildPathScanner() {

		//RGB colorBackground = PreferenceConverter.getColor(store, PreferenceConstants.P_BACKGROUND);

		IToken keyword = VHDLScanner.getKeywordToken();
		IToken string = VHDLScanner.getStringToken();
		IToken comment = VHDLScanner.getCommentToken();
		IToken other = VHDLScanner.getDefaultToken();

		setDefaultReturnToken(other);
		List<IRule> rules = new ArrayList<IRule>();

		// Add rule for single line comments.
		rules.add(new EndOfLineRule("#", comment));

		// Add rule for strings and character constants.
		rules.add(new SingleLineRule("\"", "\"", string, '\\'));
		// FIXME between ' and ' should only one character to be scanned as string
		//rules.add(new SingleLineRule("\'", "\'", string, '\\')); 
		// Add word rule for keywords.
		// FIXME keyword following an underscore should be taken as normal text.
		WordRule wordRule = new WordRule(new VHDLWordDetector(), other, true);
		for (String kw: BuildPath.keyWords.keySet()) {
			wordRule.addWord(kw, keyword);
		}

		rules.add(wordRule);

		IRule[] result = new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}
}
