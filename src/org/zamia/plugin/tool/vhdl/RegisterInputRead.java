package org.zamia.plugin.tool.vhdl;

import java.util.ArrayList;
import java.util.List;

import org.zamia.SourceLocation;
import org.zamia.vhdl.ast.ConditionalSignalAssignment;
import org.zamia.vhdl.ast.SequentialSignalAssignment;
import org.zamia.vhdl.ast.Target;
import org.zamia.vhdl.ast.VHDLNode;

public class RegisterInputRead  implements Cloneable {

	private VHDLNode read;
	private HdlEntity hdlEntity;
	private String architectureName;
	private boolean used;
	private String parentNane = null;
	
	private StopConditionE stopCondition;
	
	private List<RegisterInputRead> listReadRegisterInput = new ArrayList<RegisterInputRead>();

	public RegisterInputRead(VHDLNode _read, HdlEntity _hdlEntity,
			String _architectureName, String _parentNane, int cmptHierar) {
		read = _read;
		hdlEntity = _hdlEntity;
		architectureName = _architectureName;
		used = false;
		if (parentNane != null) {
			parentNane += ";";
			parentNane += _parentNane;
		} else {
			parentNane = _parentNane;
		}
		stopCondition = StopConditionE.NAN;
	}

	public RegisterInputRead(VHDLNode _read, HdlEntity _hdlEntity,
			String _architectureName, Target target, String parent, int cmptHierar) {
		read = _read;
		hdlEntity = _hdlEntity;
		architectureName = _architectureName;
		used = false;
		if (parent != null) {
			parentNane = parent;
		}
		if (target != null) {
			if (parentNane != null) {
				parentNane += ";";
				parentNane += target.getName().toString();
			} else {
				parentNane = target.getName().toString();
			}
		}
		stopCondition = StopConditionE.NAN;
	}

	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}

	public VHDLNode getRead() {
		return read;
	}

	public Target getTarget() {
		if (read instanceof SequentialSignalAssignment) {
			return ((SequentialSignalAssignment)read).getTarget();
		} else if (read instanceof ConditionalSignalAssignment) {
			return ((ConditionalSignalAssignment)read).getTarget();
		}
		return null;
	}
	
	public String getOperation() {
		if (getTarget() == null ) { return read.toString();}
		
		return read.toString().replaceFirst(getTarget().getName().toString(), "");
	}

	public HdlEntity getHdlEntity() {
		return hdlEntity;
	}

	public String getArchitectureName() {
		return architectureName;
	}
	
	public SourceLocation getLocation() {
		return read.getLocation();

	}
	
	@Override
	public String toString() {
		return read.toString();
	}

	public void setUsed() {
		used = true;
	}
	
	public boolean isUsed() {
		return used;
	}
	
	public int addReadRegisterInput(RegisterInputRead registerInputRead) {
		if (!listReadRegisterInput.contains(registerInputRead)) {
			this.listReadRegisterInput.add(registerInputRead);
			return this.listReadRegisterInput.size();
		}
		return 0;
	}
	
	public void clearListReadRegisterInput() {
		listReadRegisterInput = new ArrayList<RegisterInputRead>();
	}

	public List<RegisterInputRead> getListReadRegisterInput() {
		return listReadRegisterInput;
	}

	public boolean ListReadRegisterInputContains(
			SequentialSignalAssignment assign) {
		for (RegisterInputRead register : listReadRegisterInput) {
			if (register.getTarget() != null) {
				if (register.getTarget().getName().toString().equalsIgnoreCase(assign.getTarget().getName().toString())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RegisterInputRead)) {
			return false;
		}
		
		RegisterInputRead register = (RegisterInputRead)obj;
		
		if (!(getLocation().equals(register.getLocation()))) {
			return false;
		}
		
		if (!(toString().equalsIgnoreCase(register.toString()))) {
			return false;
		}
		
		return true;
	}

	public boolean isSameParent() {
		if (parentNane == null) { return false;}
		
		String[] split = parentNane.split(";");
		for (String string : split) {
			if (getTarget() != null && getTarget().getName().toString().equalsIgnoreCase(string)){
				return true;
			}
		}
		return false;
	}

	public String getParent() {
		return parentNane;
	}

	public String getType() {
		if (read instanceof SequentialSignalAssignment) {
			return "process";
		} else if (read instanceof ConditionalSignalAssignment) {
			return "assignment";
		}
		return "IO";
	}

	public StopConditionE getStopCondition() {
		return stopCondition;
	}

	public void setStopCondition(StopConditionE stopCondition) {
		this.stopCondition = stopCondition;
	}

}
