package org.zamia.plugin.tool.vhdl.rules.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.zamia.ZamiaLogger;
import org.zamia.ZamiaProject;
import org.zamia.instgraph.IGObject.OIDir;
import org.zamia.plugin.tool.vhdl.ClockSignal;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.RegisterInput;
import org.zamia.plugin.tool.vhdl.ResetSignal;
import org.zamia.plugin.tool.vhdl.Violation;
import org.zamia.plugin.tool.vhdl.manager.ReportManager;
import org.zamia.plugin.tool.vhdl.manager.ToolManager;
import org.zamia.plugin.tool.vhdl.rules.RuleService;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.Entity;
import org.zamia.vhdl.ast.InterfaceDeclaration;
import org.zamia.vhdl.ast.InterfaceList;
import org.zamia.vhdl.ast.Range;
import org.zamia.vhdl.ast.RecordElementDeclaration;
import org.zamia.vhdl.ast.SignalDeclaration;
import org.zamia.vhdl.ast.TypeDefinition;
import org.zamia.vhdl.ast.TypeDefinitionRecord;
import org.zamia.vhdl.ast.VHDLNode;

public abstract class RuleManager extends ReportManager {

	protected final String NOT_USED = "not used";
	
	protected final String NOT_DEFINED = "not defined";

	protected final String NOT_INITIALIZED = "not initialized";

	protected List<Violation> listViolation;

	public RuleManager() {
		
	}
	

	protected static boolean nameValide(String name, List<String> partName, PositionE position) {

		for (String prefix : partName) {
			switch (position) {
			case PREFIX:
				if(name.startsWith(prefix.toUpperCase())) 
					return true;
				break;
			case SUFFIX:
				if(name.endsWith(prefix.toUpperCase())) 
					return true;
				break;

			case CONTAIN:
				if(name.contains(prefix.toUpperCase())) 
					return true;
				break;

			default:

				return false;
			}
		}

		return false;
	}

	protected boolean isSignal(String portName, HdlArchitecture hdlArchitectureItem) {
		Architecture architecture = hdlArchitectureItem.getArchitecture();
		int numChildren = architecture .getNumChildren();
		VHDLNode child;
		for (int i = 0; i < numChildren; i++) {
			child = architecture.getChild(i);
			if (child instanceof SignalDeclaration) {
				
				if(((SignalDeclaration)child).getId().equalsIgnoreCase(portName)) {
					return true;
				}
			}
		}
	return false;
}
	
	protected int searchInput(String clockSignalName,
			HdlEntity hdlEntityItem) {
		Entity entity = hdlEntityItem.getEntity();
		int numChildren = entity.getNumChildren();
		VHDLNode child;
		for (int i = 0; i < numChildren; i++) {
			child = entity.getChild(i);
			if (child instanceof InterfaceList) {
				InterfaceList interfacelist = (InterfaceList) child;
				InterfaceDeclaration interfaceDeclaration;
				for (int j = 0; j < interfacelist.getNumChildren(); j++) {
					interfaceDeclaration = (InterfaceDeclaration) interfacelist.getChild(j);
					if (interfaceDeclaration.getId().equalsIgnoreCase(clockSignalName) 
							&& interfaceDeclaration.getDir() == OIDir.IN) {
						return j;
					}
				}
			}
		}
		return -1;
		
	}

	protected void checkInitialization(ClockSignal clockSignalItem, HdlFile hdlFile, HdlEntity hdlEntityItem,
			HdlArchitecture hdlArchitectureItem, Process processItem) {
		// verify initialization
		for (RegisterInput registerAffectation : clockSignalItem.getListRegister()) {
			boolean find = false;
			for (ResetSignal resetSignalItem : clockSignalItem.getListResetSignal()) {
				for (RegisterInput registerInitialization : resetSignalItem.getListRegister()) {
					if (registerAffectation.equals(registerInitialization)) {
						find = true;
					} else if (registerAffectation.getVectorName().
							equalsIgnoreCase(registerInitialization.toString())) {
						find = true;
					} else if (registerAffectation.getRecordName().
							equalsIgnoreCase(registerInitialization.toString())) {
						find = true;
					}
					
				}
			}
			if (!find && registerAffectation.isVector()) {
				boolean findIndex = false;
				for (ResetSignal resetSignalItem : clockSignalItem.getListResetSignal()) {
					for (RegisterInput registerInitialization : resetSignalItem.getListRegister()) {
						if (registerInitialization.getVectorName().equalsIgnoreCase(registerAffectation.toString())) {
							findIndex = true;
						}
					}
				}

				if (findIndex) {
					find = checkPartialInitialzedVector(registerAffectation, clockSignalItem, 
							hdlFile, hdlEntityItem,
							hdlArchitectureItem, processItem);
				}
			}
			
			if (!find && registerAffectation.isRecord()) {
				find = checkPartialInitialzedRecord(registerAffectation, clockSignalItem, 
						hdlFile, hdlEntityItem,
						hdlArchitectureItem, processItem);
			}
			
			if (!find) {
				addViolation(NOT_INITIALIZED, registerAffectation.toString(), 
						registerAffectation.getLocation().fLine, 
						hdlFile.getLocalPath(), hdlEntityItem.getEntity(), 
						hdlArchitectureItem.getArchitecture(), 
						processItem, clockSignalItem);
			}
		}
		
		
	}


	private boolean checkPartialInitialzedVector(RegisterInput registerAffectation,
			ClockSignal clockSignalItem, HdlFile hdlFile, HdlEntity hdlEntityItem, 
			HdlArchitecture hdlArchitectureItem, Process processItem) {
		// find partial initialized
		boolean find = false;
		// find partial used
		int indexMax = 0;
		int indexMin = 0;
		if (registerAffectation.isAscending()) {
			indexMax = registerAffectation.getRight();
			indexMin = registerAffectation.getLeft();
		} else {
			indexMax = registerAffectation.getLeft();
			indexMin = registerAffectation.getRight();
		}
		find = checkInitializedInVector(registerAffectation.toString(), registerAffectation.getLocation().fLine,
				indexMin, indexMax, clockSignalItem, hdlFile, hdlEntityItem, hdlArchitectureItem, processItem);
		return find;
	}
	
	private boolean checkInitializedInVector(String vectorName, int location, int indexMin,
			int indexMax, ClockSignal clockSignalItem, HdlFile hdlFile, HdlEntity hdlEntityItem, 
			HdlArchitecture hdlArchitectureItem, Process processItem) {
		boolean find = false;
		for (int i = indexMin; i <= indexMax; i++) {
			String registerIndexName = vectorName+"("+String.valueOf(i)+")";
			boolean findIndex = false;
			for (ResetSignal resetSignalItem : clockSignalItem.getListResetSignal()) {
				for (RegisterInput registerInitialization : resetSignalItem.getListRegister()) {
					if (registerInitialization.toString().equalsIgnoreCase(registerIndexName)) {
						find = true;
						findIndex = true;
					}
					
				}
			}
			if (!findIndex) {
				addViolation(NOT_INITIALIZED, registerIndexName,
						location, hdlFile.getLocalPath(), hdlEntityItem.getEntity(), 
						hdlArchitectureItem.getArchitecture(), 
						processItem, clockSignalItem);
			}
		}
		return find;
	}
	
	private boolean checkPartialInitialzedRecord(RegisterInput registerAffectation, ClockSignal clockSignalItem,
			HdlFile hdlFile, HdlEntity hdlEntityItem,
			HdlArchitecture hdlArchitectureItem, Process processItem) {
		// find partial initialized
		boolean find = false;
		TypeDefinitionRecord record = registerAffectation.getRecord();
		int numChildren = record.getNumChildren();
		for (int i = 0; i < numChildren; i++) { //each record element
			RecordElementDeclaration recordElement = (RecordElementDeclaration) record.getChild(i);
			String registerElemRecordName = registerAffectation.toString()+"."+recordElement.getId();
			boolean findIndex = false;
			for (ResetSignal resetSignalItem : clockSignalItem.getListResetSignal()) {
				for (RegisterInput registerInitialization : resetSignalItem.getListRegister()) {
					if (registerInitialization.toString().equalsIgnoreCase(registerElemRecordName)) {
						find = true;
						findIndex = true;
					}
					
				}
			}
			if (!findIndex && recordElement.getTypeDefinition().toString().
					startsWith("STD_LOGIC_VECTOR")) {
				findIndex  = checkPartialInitialzedRecordInVector(registerElemRecordName,
						recordElement.getTypeDefinition(), registerAffectation, clockSignalItem, 
						hdlFile, hdlEntityItem, hdlArchitectureItem, processItem);
			}
			if (!findIndex) {
				addViolation(NOT_INITIALIZED, registerElemRecordName,
						registerAffectation.getLocation().fLine, 
						hdlFile.getLocalPath(), hdlEntityItem.getEntity(), 
						hdlArchitectureItem.getArchitecture(), 
						processItem, clockSignalItem);
			}
		}
		return find;
	}
	
	private boolean checkPartialInitialzedRecordInVector(String vectorName,
			TypeDefinition typeDefinition, RegisterInput registerAffectation, ClockSignal clockSignalItem,
			HdlFile hdlFile, HdlEntity hdlEntityItem,
			HdlArchitecture hdlArchitectureItem, Process processItem) {
		// find partial initialized
		boolean find = false;
		
		VHDLNode name = typeDefinition.getChild(0);
		VHDLNode nameExtRange = name.getChild(0);
		Range range = (Range) nameExtRange.getChild(0);

		int indexMax = 0;
		int indexMin = 0;
		if (range.isAscending()) {
			indexMax = ToolManager.getOp(range.getRight(), hdlEntityItem, hdlArchitectureItem);
			indexMin = ToolManager.getOp(range.getLeft(), hdlEntityItem, hdlArchitectureItem);
		} else {
			indexMax = ToolManager.getOp(range.getLeft(), hdlEntityItem, hdlArchitectureItem);
			indexMin = ToolManager.getOp(range.getRight(), hdlEntityItem, hdlArchitectureItem);
		}

		find = checkInitializedInVector(vectorName, registerAffectation.getLocation().fLine,
				indexMin, indexMax, clockSignalItem, hdlFile, hdlEntityItem, hdlArchitectureItem, processItem);

		return find;
		
	}

	protected void checkAffectation(ClockSignal clockSignalItem, HdlFile hdlFile, HdlEntity hdlEntityItem,
			HdlArchitecture hdlArchitectureItem, Process processItem) {
		// verify affectation
		for (ResetSignal resetSignalItem : clockSignalItem.getListResetSignal()) {
			for (RegisterInput registerInitialization : resetSignalItem.getListRegister()) {
				boolean find = false;
				for (RegisterInput registerAffectation : clockSignalItem.getListRegister()) {
					if (registerAffectation.equals(registerInitialization) ||
							registerInitialization.getVectorName().equalsIgnoreCase(registerAffectation.toString()) ||
							registerInitialization.getRecordName().equalsIgnoreCase(registerAffectation.toString())) {
						find = true;
					}
				}
				
				if (!find && registerInitialization.isVector()) {
					boolean findIndex = false;
					for (RegisterInput registerAffectationIndex : clockSignalItem.getListRegister()) {
						if (registerAffectationIndex.getVectorName().equalsIgnoreCase(registerInitialization.toString())) {
							findIndex = true;
						}
					}

					if (findIndex) {
						find = checkPartialUsedVector(registerInitialization,  
								clockSignalItem, hdlFile, hdlEntityItem,
								hdlArchitectureItem, processItem);
					}
				}
				
				if (!find && registerInitialization.isRecord()) {
					find = checkPartialUsedRecord(registerInitialization,  
							clockSignalItem, hdlFile, hdlEntityItem,
							hdlArchitectureItem, processItem);
				}
				
				if (!find) {
					addViolation(NOT_USED, registerInitialization.toString(),
							registerInitialization.getLocation().fLine,
							hdlFile.getLocalPath(), hdlEntityItem.getEntity(), 
							hdlArchitectureItem.getArchitecture(), 
							processItem, clockSignalItem);
				}

			}
		}
		
	}
	
	private boolean checkPartialUsedVector(RegisterInput registerInitialization, 
			ClockSignal clockSignalItem, HdlFile hdlFile, HdlEntity hdlEntityItem, 
			HdlArchitecture hdlArchitectureItem, Process processItem) {
		// find partial used
		boolean find = false;
		int indexMax = 0;
		int indexMin = 0;
		if (registerInitialization.isAscending()) {
			indexMax = registerInitialization.getRight();
			indexMin = registerInitialization.getLeft();
		} else {
			indexMax = registerInitialization.getLeft();
			indexMin = registerInitialization.getRight();
		}
		for (int i = indexMin; i <= indexMax; i++) {
			String registerIndexName = registerInitialization.toString()+"("+String.valueOf(i)+")";
			boolean findIndex = false;
			for (RegisterInput registerAffectationIndex : clockSignalItem.getListRegister()) {
				if (registerAffectationIndex.toString().equalsIgnoreCase(registerIndexName)) {
					findIndex = true;
					find = true;
				}
			}
			if (!findIndex) {
				addViolation(NOT_USED, registerIndexName,
						registerInitialization.getLocation().fLine,
						hdlFile.getLocalPath(), hdlEntityItem.getEntity(), 
						hdlArchitectureItem.getArchitecture(), 
						processItem, clockSignalItem);
			}
		}
		return find;
	}
	

	private boolean checkPartialUsedRecord(RegisterInput registerInitialization,
			ClockSignal clockSignalItem, HdlFile hdlFile,
			HdlEntity hdlEntityItem, HdlArchitecture hdlArchitectureItem,
			Process processItem) {
		// find partial used
		boolean find = false;
		TypeDefinitionRecord record = registerInitialization.getRecord();
		int numChildren = record.getNumChildren();
		for (int i = 0; i < numChildren; i++) { //each record element
			RecordElementDeclaration recordElement = (RecordElementDeclaration) record.getChild(i);
			String registerElementName = registerInitialization.toString()+"."+recordElement.getId();
			boolean findIndex = false;
			for (RegisterInput registerAffectationIndex : clockSignalItem.getListRegister()) {
				if (registerAffectationIndex.toString().equalsIgnoreCase(registerElementName)) {
					findIndex = true;
					find = true;
				}
			}
			if (!findIndex && recordElement.getTypeDefinition().toString().
					startsWith("STD_LOGIC_VECTOR")) {
				boolean findSubIndex = false;
				for (RegisterInput registerAffectation : clockSignalItem.getListRegister()) {
					if (registerAffectation.getVectorName().equalsIgnoreCase(registerElementName)) {
						findSubIndex = true;
					}
				}

				if (findSubIndex) {
					findIndex  = checkPartialUsedRecordInVector(registerElementName,
							recordElement.getTypeDefinition(), registerInitialization, clockSignalItem, 
							hdlFile, hdlEntityItem, hdlArchitectureItem, processItem);
				}
			}
			if (!findIndex) {
				addViolation(NOT_USED, registerElementName,
						registerInitialization.getLocation().fLine,
						hdlFile.getLocalPath(), hdlEntityItem.getEntity(), 
						hdlArchitectureItem.getArchitecture(), 
						processItem, clockSignalItem);
			}
		}
		return find;
	}
	

	private boolean checkPartialUsedRecordInVector(String vectorName, TypeDefinition typeDefinition,
			RegisterInput registerInitialization, ClockSignal clockSignalItem,
			HdlFile hdlFile, HdlEntity hdlEntityItem,
			HdlArchitecture hdlArchitectureItem, Process processItem) {
		
		boolean find = false;
		
		VHDLNode name = typeDefinition.getChild(0);
		VHDLNode nameExtRange = name.getChild(0);
		Range range = (Range) nameExtRange.getChild(0);

		int indexMax = 0;
		int indexMin = 0;
		if (range.isAscending()) {
			indexMax = ToolManager.getOp(range.getRight(), hdlEntityItem, hdlArchitectureItem);
			indexMin = ToolManager.getOp(range.getLeft(), hdlEntityItem, hdlArchitectureItem);
		} else {
			indexMax = ToolManager.getOp(range.getLeft(), hdlEntityItem, hdlArchitectureItem);
			indexMin = ToolManager.getOp(range.getRight(), hdlEntityItem, hdlArchitectureItem);
		}
		find = checkUsedInVector(vectorName, registerInitialization.getLocation().fLine,
				indexMin, indexMax, clockSignalItem, hdlFile, hdlEntityItem, hdlArchitectureItem, processItem);

		return find;
	}
	
	private boolean checkUsedInVector(String vectorName, int location, int indexMin,
			int indexMax, ClockSignal clockSignalItem, HdlFile hdlFile, HdlEntity hdlEntityItem, 
			HdlArchitecture hdlArchitectureItem, Process processItem) {
		boolean find = false;
		for (int i = indexMin; i <= indexMax; i++) {
			String registerIndexName = vectorName+"("+String.valueOf(i)+")";
			boolean findIndex = false;
			for (RegisterInput registerAffectation : clockSignalItem.getListRegister()) {
				if (registerAffectation.toString().equalsIgnoreCase(registerIndexName)) {
					find = true;
					findIndex = true;
				}
			}
			if (!findIndex) {
				addViolation(NOT_USED, registerIndexName,
						location, hdlFile.getLocalPath(), hdlEntityItem.getEntity(), 
						hdlArchitectureItem.getArchitecture(), 
						processItem, clockSignalItem);
			}
		}
		return find;
	}
	
	private void addViolation(String error, String name,
			int fLine, String localPath, Entity entity,
			Architecture architecture, Process processItem,
			ClockSignal clockSignalItem) {
		listViolation.add(new Violation(error, name, fLine, localPath, entity,
			architecture, processItem, clockSignalItem));
		
	}

}
