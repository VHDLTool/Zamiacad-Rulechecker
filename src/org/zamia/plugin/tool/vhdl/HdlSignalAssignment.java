package org.zamia.plugin.tool.vhdl;

import org.zamia.vhdl.ast.SequentialSignalAssignment;

public class HdlSignalAssignment {
	
	private SequentialSignalAssignment sequentialSignalAssignment;

	public HdlSignalAssignment(SequentialSignalAssignment sequentialSignalAssignment) {
		this.sequentialSignalAssignment = sequentialSignalAssignment;
	}
	
	public SequentialSignalAssignment getComponentInstantiation() {
		return sequentialSignalAssignment;
	}

//	public String getName() {
//		return sequentialSignalAssignment.get;
//	}


}
