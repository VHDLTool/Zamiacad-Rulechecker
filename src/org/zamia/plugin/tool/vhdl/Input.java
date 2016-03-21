package org.zamia.plugin.tool.vhdl;

import java.util.ArrayList;

import org.zamia.vhdl.ast.OperationName;
import org.zamia.vhdl.ast.VHDLNode;

public class Input extends RegisterInput  implements Cloneable {
	
	
	private OperationName opName;


	public Input(OperationName _opName, HdlEntity hdlEntity, 
			HdlArchitecture hdlArchitecture, String _tagS, int _tagNum) {
		opName = _opName;
		name = _opName.getName();
		location = _opName.getLocation();
		tagS = _tagS;
		tagNum = _tagNum;
		setType(hdlEntity, hdlArchitecture);
		if (type == RegisterTypeE.RECORD && (!toString().equalsIgnoreCase(getRecordName()))) {
			type = RegisterTypeE.RECORD_PART;
			subName = getRecordName();
		}
		listOperand = new ArrayList<String>();
	}
	

	@Override
	public VHDLNode getVhdlNode() {
		return opName;
	}

}
