package org.zamia.plugin.tool.vhdl;

import java.io.IOException;

import org.zamia.ZamiaException;
import org.zamia.ZamiaLogger;
import org.zamia.analysis.ast.ASTDeclarationSearch;
import org.zamia.plugin.tool.vhdl.manager.ToolManager;
import org.zamia.vhdl.ast.DeclarativeItem;
import org.zamia.vhdl.ast.DiscreteRange;
import org.zamia.vhdl.ast.InterfaceDeclaration;
import org.zamia.vhdl.ast.InterfaceList;
import org.zamia.vhdl.ast.Name;
import org.zamia.vhdl.ast.Range;
import org.zamia.vhdl.ast.SignalDeclaration;
import org.zamia.vhdl.ast.TypeDeclaration;
import org.zamia.vhdl.ast.TypeDefinition;
import org.zamia.vhdl.ast.TypeDefinitionConstrainedArray;
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
	
	public boolean isArray() {
		if (type == RegisterTypeE.ARRAY) {
			return true;
		}
		return false;
	}
	
	protected void setType(HdlEntity hdlEntity, HdlArchitecture hdlArchitecture) {
		
		// Search at architecture level
		int numChildren = hdlArchitecture.getArchitecture().getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			VHDLNode child = hdlArchitecture.getArchitecture().getChild(i);

			if (child instanceof SignalDeclaration) {
				SignalDeclaration signal = (SignalDeclaration) child;
				String signalId = signal.getId();
				if (signalId.equalsIgnoreCase(getVectorName())) {
					int status = setGenericType(signal.getType().toString());
					if (status == 1) {
						searchOtherType(hdlEntity, hdlArchitecture, signalId);
					}
					else if (status == 2) {
						getSignalVectorRange(signal, hdlEntity, hdlArchitecture);
					}
					return;
				}  else if (signalId.equalsIgnoreCase(getRecordName())) {
					if (searchOtherType(hdlEntity, hdlArchitecture, signalId)) {
						return;
					}
				}
//				return;
			}
		}
		numChildren = hdlEntity.getEntity().getNumChildren();

		// Search at entity level
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
							String searchedType = interfaceDec.getType().toString();
							/*if(interfaceDec.getType() instanceof TypeDefinitionSubType) {
								TypeDefinition td = findReferencedType((TypeDefinitionSubType) interfaceDec.getType());
								searchedType = td.toString();
								logger.debug(((InterfaceDeclaration) subChild).getId()+" is of type "+interfaceDec.getType()+" which is a subtype of: "+td);
							}*/
							int status = setTypeFromEntity(searchedType, interfaceDec.getId());
							logger.info("setTypeFromEntity: "+typeS+" -> "+type);
//							int status = setGenericType(searchedType);
//							logger.info("setGenericType: "+typeS+" -> "+type);
							if (status == 1) {
								// not yet implemented yet entity
//								searchOtherType(hdlEntity, hdlArchitecture, signalId);
							}
							if (status == 2) {
								getSignalVectorRange(interfaceDec, hdlEntity, hdlArchitecture);
							}
							return;
						}
					}
				}
			}
		}	
	}
	
		// signalType: signal.getType().toString(),
	// signalId: signal.getId(),
	private int setGenericType(String signalType) {
		//0: ok 
		//1: need to call SearchOtherTypes 
		//2: need to call getSignalVectorRange
		int res = 0; 
		if (signalType.equalsIgnoreCase("STD_LOGIC")) {

			type = RegisterTypeE.DISCRETE;
			typeS = signalType;
			range = 1;
		} else if (signalType.contains("STD_LOGIC_VECTOR")) {
			if(getVectorName().equalsIgnoreCase(toString())) {
				type = RegisterTypeE.VECTOR;
				typeS = signalType;
				range = getRangeVector();
				res = 2;
			} else {
				type = RegisterTypeE.VECTOR_PART;
				typeS = signalType;
				String argument = toString().replace(getVectorName(), "").replace("(", "").replace(")", "");
				try {
					determineVectorType(argument);
				} catch (NumberFormatException e) {
					res = 2;
				}
			}
		} else if (signalType.contains("STATE_ARRAY_TYPE")) {
			type = RegisterTypeE.STATE_ARRAY_TYPE;
			typeS = signalType;
			range = 1;
		} else {
			type = RegisterTypeE.UNKNOWN_TYPE;
			typeS = signalType;
			range = 1;
			res = 1;
		}
		return res;
	}
	
	private int setTypeFromEntity(String sType, String sId) {
		int res = 0;
		if (sType.equalsIgnoreCase("STD_LOGIC")) {
			type = RegisterTypeE.DISCRETE;
			typeS = sType;
			range = 1;
		} else if (sType.contains("STD_LOGIC_VECTOR")) {
			type = RegisterTypeE.VECTOR_PART;
			if(sId.equalsIgnoreCase(toString())) {
				typeS = sType;
				range = 1;
			} else {
				//getSignalVectorRange(interfaceDec, hdlEntity, hdlArchitecture);
				res = 2;
			}
		}
		return res;
	}
	
	private void determineVectorType(String vectorStr) {
//		int index = (vectorStr.indexOf("downto") != -1)? vectorStr.indexOf("downto"): vectorStr.indexOf("to");
		// case downto
		if (vectorStr.indexOf("downto") != -1) {
			int index = (vectorStr.indexOf("downto"));
			fAscending = false;
			fLeft = Integer.valueOf(vectorStr.substring(0, index).trim());
			fRight = Integer.valueOf(vectorStr.substring(index+6, vectorStr.length()).trim());
			range = getRangeVector();						
		}
		// case to
		else if (vectorStr.indexOf("to") != -1) {
			int index = vectorStr.indexOf("to");
			fAscending = true;
			fLeft = Integer.valueOf(vectorStr.substring(0, index).trim());
			fRight = Integer.valueOf(vectorStr.substring(index+2, vectorStr.length()).trim());
			range = getRangeVector();
		}
		// case discrete
		else {
			fAscending = true;
			fLeft = Integer.valueOf(vectorStr.trim());
			fRight = Integer.valueOf(vectorStr.trim());
			range = 1;
		}
	}
	
	private TypeDefinition findReferencedType(TypeDefinitionSubType subType) {
		try {

			DeclarativeItem declaration = ASTDeclarationSearch.search(subType.getName(), ToolManager.getZamiaProject());

			if (declaration != null && declaration instanceof TypeDeclaration) {
				TypeDefinition td = ((TypeDeclaration) declaration).getType();
				return td;
			}

		} catch (IOException e) {
			
		} catch (ZamiaException e) {
			
		}
		return null;
	}

	private boolean searchOtherType(HdlEntity hdlEntity, HdlArchitecture hdlArchitecture,
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
						} else if (child2 instanceof TypeDefinitionConstrainedArray) {
							TypeDefinitionConstrainedArray typeArray = (TypeDefinitionConstrainedArray) child2;
							TypeDefinition elementType = typeArray.getElementType();
							System.out.println("elementType "+elementType.toString());
							DiscreteRange discreteRange = (DiscreteRange) typeArray.getChild(1);
							setRange(discreteRange.getRange(), hdlEntity, hdlArchitecture);
							type = RegisterTypeE.ARRAY;
							typeS = child2.toString();
							range = getRangeVector();
							return true;
						} else if (child2 != null) {
							System.out.println("child2 "+child2.toString()+ "  loc "+child2.getLocation());
							System.out.println("child2 "+child2.getClass().getSimpleName());
						}
//						System.out.println("child "+child2.getClass().getSimpleName());
					}
				}
			}
		}

		return false;
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
	

	private void setRange(Range range, HdlEntity hdlEntity, HdlArchitecture hdlArchitecture) {
		fAscending = range.isAscending();
		fLeft = ToolManager.getOp(range.getLeft(), hdlEntity, hdlArchitecture);
		fRight = ToolManager.getOp(range.getRight(), hdlEntity, hdlArchitecture);
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
