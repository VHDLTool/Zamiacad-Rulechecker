package org.zamia.plugin.editors;

public class VerilogEditor extends ZamiaEditor {
	
	public VerilogEditor() {
		super(new VerilogScanner(), new String[] {"//", ""});
	}
}
