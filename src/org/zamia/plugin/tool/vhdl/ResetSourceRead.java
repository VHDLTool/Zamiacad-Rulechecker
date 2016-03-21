package org.zamia.plugin.tool.vhdl;

import org.zamia.vhdl.ast.VHDLNode;

public class ResetSourceRead {

	private VHDLNode read;
	private String entityName;
	private String architectureName;

	
	public ResetSourceRead(VHDLNode _read, String _entityName, String _architectureName) {
		read = _read;
		entityName = _entityName;
		architectureName = _architectureName;
	}



	public VHDLNode getRead() {
		return read;
	}


	public String getEntityName() {
		return entityName;
	}

	public String getArchitectureName() {
		return architectureName;
	}
	

}
