package org.zamia.plugin.tool.vhdl;

import java.util.ArrayList;
import java.util.List;

import org.zamia.SourceLocation;
import org.zamia.vhdl.ast.ConditionalSignalAssignment;
import org.zamia.vhdl.ast.ConditionalWaveform;
import org.zamia.vhdl.ast.OperationCompare;
import org.zamia.vhdl.ast.OperationConcat;
import org.zamia.vhdl.ast.OperationLogic;
import org.zamia.vhdl.ast.OperationName;
import org.zamia.vhdl.ast.SequentialSignalAssignment;
import org.zamia.vhdl.ast.Target;
import org.zamia.vhdl.ast.VHDLNode;
import org.zamia.vhdl.ast.Waveform;

public class RegisterInputSource  implements Cloneable {

	private VHDLNode read;
	private HdlEntity hdlEntity;
	private String architectureName;
	private boolean used;
	private String parentNane = null;

	private List<RegisterInputSource> listSourceRegisterInput = new ArrayList<RegisterInputSource>();
	private List<String> listOperand;
	
	private StopConditionE stopCondition;

	public RegisterInputSource(VHDLNode _read, HdlEntity _hdlEntity,
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

	public RegisterInputSource(VHDLNode _read, HdlEntity _hdlEntity,
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
	
	public void addSourceRegisterInput(RegisterInputSource registerInputRead) {
		if (!listSourceRegisterInput.contains(registerInputRead)) {
			this.listSourceRegisterInput.add(registerInputRead);
		}
	}
	
	public void clearListSourceRegisterInput() {
		listSourceRegisterInput = new ArrayList<RegisterInputSource>();
	}

	public List<RegisterInputSource> getListSourceRegisterInput() {
		return listSourceRegisterInput;
	}

	public boolean ListReadRegisterInputContains(
			SequentialSignalAssignment assign) {
		for (RegisterInputSource register : listSourceRegisterInput) {
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
		if (!(obj instanceof RegisterInputSource)) {
			return false;
		}
		
		RegisterInputSource register = (RegisterInputSource)obj;
		
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
		} else if (read instanceof OperationName) {
			return "process";
		} else if (read.getParent().getParent() instanceof SequentialSignalAssignment) {
			return "process";
		} else if (read.getParent().getParent() instanceof ConditionalSignalAssignment) {
			return "assignment";
		}
//		System.out.println("getType "+read.getParent().getParent().getClass().getSimpleName());
		return "IO";
	}

	public List<String> getListOperand() {
		listOperand = new ArrayList<String>();
		
		if (read instanceof ConditionalSignalAssignment) {
			int numChildren = read.getNumChildren();
			for (int i = 0; i < numChildren; i++) {
				VHDLNode child = read.getChild(i);
				if (child instanceof ConditionalWaveform) {
					int numChildren2 = child.getNumChildren();
					for (int j = 0; j < numChildren2; j++) {
						VHDLNode child2 = child.getChild(j);
						if (child2 instanceof OperationLogic) {
							searchInOp(child2);
						} else if (child2 instanceof Waveform) {
							VHDLNode child3 = child2.getChild(0);
							searchInOp(child3);
						} else if (child2 != null) {
							System.out.println("child2 "+child2.toString()+ " type "+child2.getClass().getSimpleName());
						}
					}
				}
			}
			
		}
		return listOperand;
	}


	
	private void searchInOp(VHDLNode child) {
		if (child == null) { return; }
		int numChildren2 = child.getNumChildren();
		for (int j = 0; j < numChildren2; j++) {
			VHDLNode child2 = child.getChild(j);
			if (child2 instanceof OperationCompare) {
				searchInOp(child2);
			} else if (child2 instanceof OperationLogic) {
				searchInOp(child2);
			}else  if (child2 instanceof OperationName) {
				if (!child2.toString().equalsIgnoreCase(toString())) {
					listOperand.add(child2.toString());
				}
			} else if (child2 instanceof OperationConcat) {
				searchInOp(child2);
			}

		}
	}

	public VHDLNode getVhdlNode() {
		return read;
		
	}

	public StopConditionE getStopCondition() {
		return stopCondition;
	}

	public void setStopCondition(StopConditionE stopCondition) {
		this.stopCondition = stopCondition;
	}


}
