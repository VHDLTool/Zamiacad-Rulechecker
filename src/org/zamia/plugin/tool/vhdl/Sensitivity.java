package org.zamia.plugin.tool.vhdl;

import org.zamia.SourceLocation;
import org.zamia.vhdl.ast.Name;
import org.zamia.vhdl.ast.VHDLNode;

public class Sensitivity extends Declaration {

	public Sensitivity(Name name, HdlEntity hdlEntity, HdlArchitecture hdlArchitecture) {
		this.name = name;
		setType(hdlEntity, hdlArchitecture);
	}

	public SourceLocation getLocation() {
		return name.getLocation();
	}

	@Override
	public String toString() {
		return getName().toString();
	}

	@Override
	public VHDLNode getVhdlNode() {
		return name;
	}


}
