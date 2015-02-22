/*
 * Copyright 2004-2008 by the authors indicated in the @author tags.
 * All rights reserved.
 *
 * See the LICENSE file for details.
 *
*/


package org.zamia.plugin.editors;

import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.zamia.plugin.editors.buildpath.BasicViewerConfiguration.BasicIdentifierScanner;

/**
 * 
 * @author Guenter Bartsch
 *
 */
public class VHDLEditor extends ZamiaEditor {

	public static class Scanner extends BasicIdentifierScanner {

		static public String[] keywords = new String[] { "ABS", "ACCESS", "AFTER", "ALIAS", "AND", "ARCHITECTURE", "ASSERT", "ATTRIBUTE", "BEGIN", "BLOCK", "BODY", "BUFFER", "BUS", "CASE", "COMPONENT",
			"CONFIGURATION", "CONSTANT", "DISCONNECT", "DOWNTO", "ELSE", "ELSIF", "END", "ENTITY", "EXIT", "FILE", "FOR", "FUNCTION", "GENERATE", "GENERIC", "GROUP", "GUARDED", "IF", "IMPURE", "IN",
			"INERTIAL", "INOUT", "IS", "LABEL", "LIBRARY", "LINKAGE", "LITERAL", "LOOP", "MAP", "MOD", "NAND", "NEW", "NEXT", "NOR", "NOT", "NULL", "OF", "ON", "OPEN", "OR", "OTHERS", "OUT",
			"PACKAGE", "PORT", "POSTPONED", "PROCEDURAL", "PROCEDURE", "PROCESS", "PROTECTED", "PURE", "RANGE", "RECORD", "REFERENCE", "REGISTER", "REJECT", "REM", "REPORT", "RETURN", "ROL", "ROR",
			"SELECT", "SEVERITY", "SIGNAL", "SHARED", "SLA", "SLL", "SRA", "SRL", "SUBTYPE", "THEN", "TO", "TRANSPORT", "TYPE", "UNAFFECTED", "UNITS", "UNTIL", "USE", "VARIABLE", "WAIT", "WHEN",
			"WHILE", "WITH", "XNOR", "XOR" };
		
		public String[] getKeywords() {
			return keywords;
		}
		
		public void addStrComment(List<IRule> rules, Token string, Token comment) {

			//		RGB colorBackground = PreferenceConverter.getColor(store, PreferenceConstants.P_BACKGROUND);

			//IToken keyword = token(colorKeyword),colorManager.getColor(colorBackground),0));
			//IToken string = token(colorString),colorManager.getColor(colorBackground),0));
			//IToken other = token(colorDefault),colorManager.getColor(colorBackground),0));
			//IToken comment = token(colorComment),colorManager.getColor(colorBackground),0));

			// Add rule for single line comments.
			rules.add(new EndOfLineRule("--", getCommentToken()));

			// Add rule for strings and character constants.
			rules.add(new SingleLineRule("\"", "\"", getStringToken(), '\\'));
			// FIXME between ' and ' should only one character to be scanned as string
			//rules.add(new SingleLineRule("\'", "\'", string, '\\')); 
			// Add word rule for keywords.
			// FIXME keyword following an underscore should be taken as normal text.
		}

		@Override
		public boolean ignoreCase() {
			return true;
		}
	}

	public VHDLEditor() {
		super(new Scanner(), new String[] {"--", ""});
	}

}
