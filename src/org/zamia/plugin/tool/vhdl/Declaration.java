package org.zamia.plugin.tool.vhdl;

import org.zamia.ZamiaLogger;
import org.zamia.plugin.tool.vhdl.manager.ToolManager;
import org.zamia.vhdl.ast.InterfaceDeclaration;
import org.zamia.vhdl.ast.InterfaceList;
import org.zamia.vhdl.ast.Name;
import org.zamia.vhdl.ast.OperationName;
import org.zamia.vhdl.ast.Range;
import org.zamia.vhdl.ast.SignalDeclaration;
import org.zamia.vhdl.ast.TypeDeclaration;
import org.zamia.vhdl.ast.TypeDefinitionEnum;
import org.zamia.vhdl.ast.TypeDefinitionRecord;
import org.zamia.vhdl.ast.TypeDefinitionSubType;
import org.zamia.vhdl.ast.VHDLNode;

public abstract class Declaration {

	public final static ZamiaLogger logger = ZamiaLogger.getInstance();
	
	protected RegisterTypeE type = RegisterTypeE.NAN;

	protected String typeS = "";

	protected int fLeft = 0;
	
	protected int fRight = 0;
	
	protected boolean fAscending = true;
	
	protected Name name;

	protected TypeDefinitionRecord record;

	protected int range;
	
	public Name getName() {
		return name;
	}
	
	public int getRangeNb() {
		return range;
	}
	
	public abstract String toString();

	public abstract VHDLNode getVhdlNode();

	public RegisterTypeE getType() {
		return type;
	}
	
	public String getTypeS() {
		return typeS;
	}
	
	public int getLeft() {
		return fLeft;
	}
	
	public int getRight() {
		return fRight;
	}
	
	public boolean isAscending() {
		return fAscending;
	}
	
	public String getRange() {
		return fLeft + (fAscending ? " to " : " downto " ) +fRight;
	}
	
	public int getIndex() {
		if (isVector()) {
			if (toString().equalsIgnoreCase(getVectorName())) { return -1;}
			
			int indexOf = toString().indexOf("(");
			if (indexOf == -1) { return -1;}
			
			String substring = toString().substring(0, indexOf);
			
			indexOf = substring.indexOf("(");
			if (indexOf == -1) { return -1;}

			try {
				
				return Integer.valueOf(toString().substring(0,indexOf));
			} catch (NumberFormatException e) {
				return -1;
			}
			
		} else {
			return -1;
		}

	}

	public int getIndexMin() {
		int indexMin = 0;
		if (isAscending()) {
			indexMin = getLeft();
		} else {
			indexMin = getRight();
		}
		return indexMin;
	}

	public int getIndexMax() {
		int indexMax = 0;
		if (isAscending()) {
			indexMax = getRight();
		} else {
			indexMax = getLeft();
		}
		return indexMax;
	}

	public boolean isVector() {
		if (type == RegisterTypeE.VECTOR) {
			return true;
		}
		return false;
	}

	public boolean isDiscrete() {
		if (type == RegisterTypeE.DISCRETE) {
			return true;
		}
		return false;
	}

	public boolean isPartOfVector() {
		if (type == RegisterTypeE.VECTOR_PART) {
			return true;
		}
		return false;
	}
	
	protected void setType(HdlEntity hdlEntity, HdlArchitecture hdlArchitecture) {
		int numChildren = hdlArchitecture.getArchitecture().getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			VHDLNode child = hdlArchitecture.getArchitecture().getChild(i);

			if (child instanceof SignalDeclaration) {
				SignalDeclaration signal = (SignalDeclaration) child;
				if (signal.getId().equalsIgnoreCase("key_reg0") && toString().equalsIgnoreCase("key_reg0"))
					System.out.println("signal SignalDeclaration  "+signal.getId()+"  type "+signal.getType()+ " toString  "+ toString());
				if (signal.getId().equalsIgnoreCase(getVectorName())) {
					if (signal.getId().equalsIgnoreCase("key_reg0") && toString().equalsIgnoreCase("key_reg0"))
						System.out.println("signal getVectorName  "+signal.getId()+"  type "+signal.getType()+ " toString  "+ toString());

					if (signal.getType().toString().equalsIgnoreCase("STD_LOGIC")) {
						if (signal.getId().equalsIgnoreCase("key_reg0") && toString().equalsIgnoreCase("key_reg0"))
							System.out.println("signal getVectorName STD_LOGIC  "+signal.getId()+"  type "+signal.getType()+ " toString  "+ toString());

						type = RegisterTypeE.DISCRETE;
						typeS = signal.getType().toString();
						range = 1;
						return;
					} else if (signal.getType().toString().contains("STD_LOGIC_VECTOR")) {
						if (signal.getId().equalsIgnoreCase("key_reg0") && toString().equalsIgnoreCase("key_reg0"))
							System.out.println("signal getVectorName STD_LOGIC_VECTOR  "+signal.getId()+"  type "+signal.getType()+ " toString  "+ toString());

						if(getVectorName().equalsIgnoreCase(toString())) {
							type = RegisterTypeE.VECTOR;
							typeS = signal.getType().toString();
							getSignalVectorRange(signal, hdlEntity, hdlArchitecture);
							range = getRangeVector();
							return;
						} else {
							String argument = toString().replace(getVectorName(), "").replace("(", "").replace(")", "");
							int index = argument.indexOf("downto");
							if (index != -1) {
								// case downto
								try {
									fAscending = false;
									fLeft = Integer.valueOf(argument.substring(0, index).trim());
									fRight = Integer.valueOf(argument.substring(index+6, argument.length()).trim());
								} catch (NumberFormatException e) {
									getSignalVectorRange(signal, hdlEntity, hdlArchitecture);
								}
								type = RegisterTypeE.VECTOR_PART;
								typeS = signal.getType().toString();
								range = getRangeVector();
								return;
							} 
							
							index = argument.indexOf("to");
							if (index != -1) {
								// case to
								try {
									fAscending = true;
									fLeft = Integer.valueOf(argument.substring(0, index).trim());
									fRight = Integer.valueOf(argument.substring(index+2, argument.length()).trim());
								} catch (NumberFormatException e) {
									getSignalVectorRange(signal, hdlEntity, hdlArchitecture);
								}

								type = RegisterTypeE.VECTOR_PART;
								typeS = signal.getType().toString();
								range = getRangeVector();
								return;
							} 
							
							// case discrete
							try {
								fAscending = true;
								fLeft = Integer.valueOf(argument.trim());
								fRight = Integer.valueOf(argument.trim());
							} catch (NumberFormatException e) {
								getSignalVectorRange(signal, hdlEntity, hdlArchitecture);
							}
							type = RegisterTypeE.VECTOR_PART;
							typeS = signal.getType().toString();
							range = 1;
							return;
						}
					} else if (signal.getType().toString().contains("STATE_ARRAY_TYPE")) {
						type = RegisterTypeE.STATE_ARRAY_TYPE;
						typeS = signal.getType().toString();
						range = 1;
						return;
					} else {
						if (searchOtherType(hdlArchitecture, signal.getType().toString())) {
							return;
						}
						type = RegisterTypeE.UNKNOWN_TYPE;
						typeS = signal.getType().toString();
						range = 1;
						return;
					}
				} else if (signal.getId().equalsIgnoreCase(getRecordName())) {
					if (signal.getId().equalsIgnoreCase("key_reg0") && toString().equalsIgnoreCase("key_reg0"))
						System.out.println("signal getRecordName  "+signal.getId()+"  type "+signal.getType()+ " toString  "+ toString());

					if (searchOtherType(hdlArchitecture, signal.getType().toString())) {
						if (signal.getId().equalsIgnoreCase("key_reg0") && toString().equalsIgnoreCase("key_reg0"))
							System.out.println("signal searchOtherType  "+signal.getId()+"  type "+signal.getType()+ " toString  "+ toString());

						return;
					}
//				} else if (signal.getType().toString().equalsIgnoreCase("STATE_ARRAY_TYPE") && toString().equalsIgnoreCase(signal.getId())) {
//					if (signal.getId().equalsIgnoreCase("key_reg0") && toString().equalsIgnoreCase("key_reg0"))
//						System.out.println("signal STATE_ARRAY_TYPE  "+signal.getId()+"  type "+signal.getType()+ " toString  "+ toString());
//					type = RegisterTypeE.STATE_ARRAY_TYPE;
//					typeS = signal.getType().toString();
//					range = 1;
//					return;
//				} else {
//					type = RegisterTypeE.STATE_ARRAY_TYPE;
//					typeS = signal.getType().toString();
//					range = 1;
//					return;
				}
			}
		}
		numChildren = hdlEntity.getEntity().getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			VHDLNode child = hdlEntity.getEntity().getChild(i);
			if (child instanceof InterfaceList) {
				InterfaceList interfaceList = (InterfaceList) child;
				int numSubChildren = interfaceList.getNumChildren();
				for (int j = 0; j < numSubChildren; j++) {
					VHDLNode subChild = child.getChild(j);
					if (subChild instanceof InterfaceDeclaration) {
						InterfaceDeclaration interfaceDec = (InterfaceDeclaration) subChild;
						if (interfaceDec.getId().equalsIgnoreCase(getVectorName())) {
							if (interfaceDec.getType().toString().equalsIgnoreCase("STD_LOGIC")) {
								type = RegisterTypeE.DISCRETE;
								typeS = interfaceDec.getType().toString();
								range = 1;
								return;
							} else if (interfaceDec.getType().toString().contains("STD_LOGIC_VECTOR")) {
								if(interfaceDec.getId().equalsIgnoreCase(toString())) {
									type = RegisterTypeE.VECTOR_PART;
									typeS = interfaceDec.getType().toString();
									range = 1;
									return;
								}
								type = RegisterTypeE.VECTOR_PART;

								getSignalVectorRange(interfaceDec, hdlEntity, hdlArchitecture);
								return;
							} else {
//								logger.debug("+++++++++ TYPE interfaceDec  "+interfaceDec.getType().toString());
							}
						}
					}
				}
			}
		}
		
	}
	
	public String getRecordName() {
		int indexOf = toString().indexOf(".");
		if (indexOf == -1) { return toString();}
		
		return toString().substring(0, indexOf);
	}

	private int getRangeVector() {
		int indexMax = 0;
		int indexMin = 0;
		if (isAscending()) {
			indexMax = getRight();
			indexMin = getLeft();
		} else {
			indexMax = getLeft();
			indexMin = getRight();
		}
		return (indexMax - indexMin +1);
	}

	private boolean searchOtherType(HdlArchitecture hdlArchitecture,
			String otherType) {
		int numChildren = hdlArchitecture.getArchitecture().getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			VHDLNode child = hdlArchitecture.getArchitecture().getChild(i);
//			if (child != null) {
//				System.out.println("child "+child.getClass().getSimpleName());
//			}
			if (child instanceof TypeDeclaration) {
				TypeDeclaration typeDec = (TypeDeclaration) child;
				if (typeDec.getId().equalsIgnoreCase(otherType)) {
					int numChildren2 = child.getNumChildren();
					for (int j = 0; j < numChildren2; j++) {
						VHDLNode child2 = child.getChild(j);
						if (child2 instanceof TypeDefinitionRecord) {
							type = RegisterTypeE.RECORD;
							typeS = otherType;
							record = (TypeDefinitionRecord) child2;
							range = child2.getNumChildren();
							return true;
						} else if (child2 instanceof TypeDefinitionEnum) {
							type = RegisterTypeE.ENUMERATION;
							typeS = otherType;
							range = 1;
							return true;
						} else if (child2 != null) {
							System.out.println("child2 "+child2.getClass().getSimpleName());
						}
//						System.out.println("child "+child2.getClass().getSimpleName());
					}
				}
			}
		}

		return false;
	}

	private void getSignalVectorRange(SignalDeclaration signal, HdlEntity hdlEntity, HdlArchitecture hdlArchitecture) {
		int numChildren = signal.getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			VHDLNode child = signal.getChild(i);
			if (child instanceof TypeDefinitionSubType) {
				searchRangeInTypeDefinitionSubType(child, hdlEntity, hdlArchitecture);
			}
		}
	}

	private void getSignalVectorRange(InterfaceDeclaration signal, HdlEntity hdlEntity, HdlArchitecture hdlArchitecture) {
		int numChildren = signal.getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			VHDLNode child = signal.getChild(i);
			if (child instanceof TypeDefinitionSubType) {
				searchRangeInTypeDefinitionSubType(child, hdlEntity, hdlArchitecture);
			}
		}
	}

	private void searchRangeInTypeDefinitionSubType(VHDLNode child, HdlEntity hdlEntity, HdlArchitecture hdlArchitecture) {
		VHDLNode name = child.getChild(0);
		VHDLNode nameExtRange = name.getChild(0);
		VHDLNode childNameExtRange = nameExtRange.getChild(0);
		if (childNameExtRange instanceof Range) {
			Range range = (Range) childNameExtRange;
			fLeft = ToolManager.getOp(range.getLeft(), hdlEntity, hdlArchitecture);
			fRight = ToolManager.getOp(range.getRight(), hdlEntity, hdlArchitecture);
			fAscending = range.isAscending();
		}
	}

	public String getVectorName() {
		return ToolManager.getVectorName(toString());
	}
	
}
