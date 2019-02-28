package org.zamia.plugin.tool.vhdl;

import org.zamia.vhdl.ast.PackageBody;
import org.zamia.vhdl.ast.SequentialProcess;
import org.zamia.vhdl.ast.SubProgram;

public class HdlSubProgram {
	public enum TYPE_SUBPROGRAM {FUNCTION, PROCEDURE}
	
	private SubProgram subProgram;
	private TYPE_SUBPROGRAM type;
	private ExtraInfo extraInfo = new ExtraInfo();
	
	public class ExtraInfo {
		private HdlEntity hdlEntity = null;
		private HdlArchitecture hdlArchitecture = null;
		private Package _package = null;
		private PackageBody packageBody = null;
		private SequentialProcess process = null;
		
		public HdlEntity getHdlEntity() {
			return hdlEntity;
		}
		
		public HdlArchitecture getHdlArchitecture() {
			return hdlArchitecture;
		}
	}
	
	public HdlSubProgram(SubProgram subProgram, HdlEntity hdlEntity, HdlArchitecture hdlArchitecture) {
		this.subProgram = subProgram;
		this.extraInfo.hdlEntity = hdlEntity;
		this.extraInfo.hdlArchitecture = hdlArchitecture;
		type = (subProgram.getChild(0) == null ? TYPE_SUBPROGRAM.PROCEDURE : TYPE_SUBPROGRAM.FUNCTION);
	}
	
	public HdlSubProgram(SubProgram subProgram, HdlEntity hdlEntity, HdlArchitecture hdlArchitecture, SequentialProcess process) {
		this(subProgram, hdlEntity, hdlArchitecture);
		this.extraInfo.process = process;
	}
	
	public HdlSubProgram(SubProgram subProgram, Package _package, PackageBody packageBody) {
		this.extraInfo._package = _package;
		this.subProgram = subProgram;
		this.extraInfo.packageBody = packageBody;
		type = (subProgram.getChild(0) == null ? TYPE_SUBPROGRAM.PROCEDURE : TYPE_SUBPROGRAM.FUNCTION);
	}
	
	public SubProgram getSubProgram() {
		return subProgram;
	}
	
	public TYPE_SUBPROGRAM getType() {
		return type;
	}
	
	public ExtraInfo getExtraInfo() {
		return extraInfo;
	}
	
}
