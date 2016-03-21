package org.zamia.plugin.tool.vhdl;

import org.zamia.vhdl.ast.Name;
import org.zamia.vhdl.ast.NameExtensionAttribute;
import org.zamia.vhdl.ast.NameExtensionRange;
import org.zamia.vhdl.ast.OperationCompare;
import org.zamia.vhdl.ast.OperationLiteral;
import org.zamia.vhdl.ast.OperationLogic;
import org.zamia.vhdl.ast.OperationName;
import org.zamia.vhdl.ast.SequentialSignalAssignment;
import org.zamia.vhdl.ast.VHDLNode;

public class ClockSourceRead {

	private VHDLNode read;
	private String entityName;
	private String architectureName;


	public ClockSourceRead(VHDLNode _read, String _entityName, String _architectureName) {
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



	public boolean isWrongUsesClock() {
		if (read  instanceof SequentialSignalAssignment) {
			return true;
		} else if (read  instanceof OperationName) {
			int numChildren = read.getNumChildren();
			for (int i = 0; i < numChildren; i++) {
				VHDLNode child = read.getChild(i);
				if (child instanceof Name) {
					int numSubChildren = child.getNumChildren();
					for (int j = 0; j < numSubChildren; j++) {
						VHDLNode subChild = child.getChild(j);
						if (subChild instanceof NameExtensionRange) {
							String operation = child.toString().replace(subChild.toString(), "");
							if (operation.equalsIgnoreCase("FALLING_EDGE") ||
									operation.equalsIgnoreCase("RISING_EDGE")) {
								return false;
							}
								
						} else if (subChild instanceof NameExtensionAttribute) {
							if (subChild.toString().equalsIgnoreCase("'EVENT")) {
								return false;
							}
						}
					}
					
				}
			}
//			System.out.println("CHILD "+read+ " TYPE "+read.getClass().getSimpleName());

			return true;
		} else if (read  instanceof OperationLogic) {
			
			if (findClock(read)) {
				return false;
			}
//			System.out.println("OPERATION LOGIC "+read+ " TYPE "+read.getClass().getSimpleName()+ " LOCATION "+read.getLocation());
			return true;
		} else {
//			System.out.println("CLOCK READ "+read+ " TYPE "+read.getClass().getSimpleName()+ " LOCATION "+read.getLocation());
			return true;
		}

	}



	private boolean findClock(VHDLNode node) {
		int numChildren = node.getNumChildren();
		if (numChildren == 2) {
			VHDLNode child1 = node.getChild(0);
			VHDLNode child2 = node.getChild(1);
			if (child1 instanceof OperationCompare && child2 instanceof OperationName) {
				return findClockWithEvent(child1, child2);
			} else if (child2 instanceof OperationCompare && child1 instanceof OperationName) {
				return findClockWithEvent(child2, child1);
			}
			if (child1 instanceof OperationCompare && child2 instanceof OperationLogic) {
				return findClockWithStable(child1, child2);
			} else if (child2 instanceof OperationCompare && child1 instanceof OperationLogic) {
				return findClockWithStable(child2, child1);
			}
		}
		return false;
	}



	private boolean findClockWithStable(VHDLNode child1, VHDLNode child2) {
		String signalName = "";
		VHDLNode subChild11 = child1.getChild(0);
		VHDLNode subChild12 = child1.getChild(1);
		if (subChild11 instanceof OperationName && subChild12 instanceof OperationLiteral) {
			signalName = subChild11.toString();
		} else if (subChild12 instanceof OperationName && subChild11 instanceof OperationLiteral) {
			signalName = subChild12.toString();
		} else {
			return false;
		}
		if (child2.getChild(0).toString().replace(signalName, "").equalsIgnoreCase("'STABLE")) {
			return true;
		}
		return false;
	}



	private boolean findClockWithEvent(VHDLNode child1, VHDLNode child2) {
		String signalName = "";
		VHDLNode subChild11 = child1.getChild(0);
		VHDLNode subChild12 = child1.getChild(1);
		if (subChild11 instanceof OperationName && subChild12 instanceof OperationLiteral) {
			signalName = subChild11.toString();
		} else if (subChild12 instanceof OperationName && subChild11 instanceof OperationLiteral) {
			signalName = subChild12.toString();
		} else {
			return false;
		}
		if (child2.toString().replace(signalName, "").equalsIgnoreCase("'EVENT")) {
			return true;
		}
		return false;
	}


}
