package org.zamia.plugin.editors;

import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.zamia.plugin.editors.buildpath.BasicViewerConfiguration.BasicIdentifierScanner;

public class VerilogEditor extends ZamiaEditor {
	
	static class Scanner extends BasicIdentifierScanner {

		public boolean ignoreCase() { return false; }

		public void addStrComment(List<IRule> rules, Token string, Token comment) {

	        // Add rule for single line comments.
	        rules.add(new EndOfLineRule("//", comment));

			// Add rule for strings and character constants.
			rules.add(new SingleLineRule("\"", "\"", string, '\\')); 
			// FIXME between ' and ' should only one character to be scanned as string
			rules.add(new SingleLineRule("\'", "\'", string, '\\')); 
		}

		public String[] getKeywords() {
			return new String[] {"always","and","assign","attribute","begin","buf","bufif0","bufif1","case","casex","casez","cmos","deassign","default","defparam","disable","edge","else","end","endattribute","endcase","endfunction", "endmodule","endprimitive","endspecify","endtable","endtask","event","for","force","forever","fork","function","highz0","highz1","if","ifnone","initial","inout","input","integer","join","medium","module","large","macromodule","nand","negedge","nmos","nor","not","notif0","notif1","or","output","parameter","pmos","posedge","primitive","pull0","pull1","pulldown","pullup","rcmos","real","realtime","reg","release","repeat","rnmos","rpmos","rtran","rtranif0","rtranif1","scalared","signed","small","specify","specparam","strength","strong0","strong1","supply0","supply1","table","task","time","tran","tranif0","tranif1","tri","tri0","tri1","triand","trior","trireg","unsigned","vectored","wait","wand","weak0","weak1","while","wire","wor","xnor","xor"};
		}
	}

	public VerilogEditor() {
		super(new Scanner(), new String[] {"//", ""});
	}
}
