package org.zamia.plugin.tool.vhdl;

import org.zamia.SourceLocation;
import org.zamia.instgraph.IGObject.OIDir;
import org.zamia.vhdl.ast.InterfaceDeclaration;
import org.zamia.vhdl.ast.VHDLNode;

public class InputOutput extends RegisterInput{

	private InterfaceDeclaration interfaceDeclaration;
	
	private String tag;
	
	public InputOutput(InterfaceDeclaration _interfaceDeclaration, String _tag) {
		interfaceDeclaration = _interfaceDeclaration;
		tag = _tag;
	}
	
	@Override
	public VHDLNode getVhdlNode() {
		return interfaceDeclaration;
	}

	public String getNameS() {
		return interfaceDeclaration.getId();

	}

	public String getDirection() {
		return interfaceDeclaration.getDir().toString();
	}
	
	public OIDir getDir() {
		return interfaceDeclaration.getDir();
	}
	
	public String getTag() {
		return tag;
	}

	public SourceLocation getLocation() {
		return interfaceDeclaration.getLocation();
	}
	
	@Override
	public String toString() {
		return interfaceDeclaration.getId();
	}
}
