package org.zamia.plugin.tool.vhdl;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.zamia.SourceLocation;
import org.zamia.instgraph.IGObject.OIDir;
import org.zamia.plugin.tool.vhdl.manager.EntityManager;
import org.zamia.plugin.tool.vhdl.manager.ToolManager;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.AssociationElement;
import org.zamia.vhdl.ast.AssociationList;
import org.zamia.vhdl.ast.ComponentDeclaration;
import org.zamia.vhdl.ast.ComponentInstantiation;
import org.zamia.vhdl.ast.InterfaceDeclaration;
import org.zamia.vhdl.ast.InterfaceList;
import org.zamia.vhdl.ast.Name;
import org.zamia.vhdl.ast.NameExtensionRange;
import org.zamia.vhdl.ast.TypeDefinitionSubType;
import org.zamia.vhdl.ast.VHDLNode;

public class HdlComponentInstantiation {
	
	private ComponentInstantiation componentInstantiation;
	
	private HdlArchitecture archi;

	public HdlComponentInstantiation(ComponentInstantiation componentInstantiation, HdlArchitecture hdlArchitecture) {
		this.componentInstantiation = componentInstantiation;
		archi = hdlArchitecture;
	}
	
	public ComponentInstantiation getComponentInstantiation() {
		return componentInstantiation;
	}

	public String getName() {
		return componentInstantiation != null ?(componentInstantiation.getName() != null ? componentInstantiation.getName().getId() : "null") : "component null";
	}

	private ArrayList<HdlEntity> listHdlEntity = new ArrayList<HdlEntity>();

	
	private ArrayList<InterfaceDeclaration> listComponentDeclaration = new ArrayList<InterfaceDeclaration>();
	
	
	public void setComponentDeclaration (ArrayList<InterfaceDeclaration> _listComponentDeclaration) {
		listComponentDeclaration = _listComponentDeclaration;
	}

	public String getPortConnexionName(Integer numOutput, String signalName) {
		int numChildren = componentInstantiation.getNumChildren();
		VHDLNode child;
		for (int i = 0; i < numChildren; i++) {
			child = componentInstantiation.getChild(i);
			if (child instanceof AssociationList) {
				int numAssociation = child.getNumChildren();
				if (numAssociation != 0) {
					AssociationElement associationElem = (AssociationElement) child.getChild(0);
					if (associationElem.getFormalPart() != null) {
						// case per label
						for (int j = 0; j < numAssociation; j++) {
							associationElem = (AssociationElement) child.getChild(j);
							if (associationElem.getFormalPart().toString().equalsIgnoreCase(signalName)) {
								return associationElem.getActualPart().toString();
							}
						}
					} else {
						// case per number
						if (numOutput < numAssociation) {
							associationElem = (AssociationElement) child.getChild(numOutput);
							return associationElem.getActualPart().toString();
						}
					}
				}
			}
		}
		return "";
		
	}

	public HdlEntity getEntity() {
		Map<String, HdlFile> listHdlFile;
			try {
				listHdlFile = EntityManager.getEntity();
			} catch (EntityException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				return null;
			}
			for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
				HdlFile hdlFile = entry.getValue();
				if (hdlFile.getListHdlEntity() == null) { continue;}
				for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
					if (hdlEntityItem.getEntity().getId().equalsIgnoreCase(getName())) {
						return hdlEntityItem;
					}
				}
			}

			return null;
			

	}	
	
	public void addUseInEntity(HdlEntity hdlEntity) {
		listHdlEntity.add(hdlEntity);
	}
	
	public ArrayList<HdlEntity> getUseInEntity() {
		return listHdlEntity;

	}

	public InterfaceDeclaration getInuputPort(String signalName) {
		int numChildren = componentInstantiation.getNumChildren();
		VHDLNode child;
		for (int i = 0; i < numChildren; i++) {
			child = componentInstantiation.getChild(i);
			if (child instanceof AssociationList) {
				int numAssociation = child.getNumChildren();
				if (numAssociation != 0) {
					AssociationElement associationElem = (AssociationElement) child.getChild(0);
					if (associationElem.getFormalPart() != null) {
						// case per label
						for (int j = 0; j < numAssociation; j++) {
							associationElem = (AssociationElement) child.getChild(j);
							if (associationElem.getActualPart() != null ) { //ActualPart NULL => OPEN
								if (ToolManager.getVectorName(associationElem.getActualPart().toString()).equalsIgnoreCase(signalName)) {

									InterfaceDeclaration interfaceDeclaration = getInterfaceDeclaration(associationElem.getFormalPart().toString());

									if (interfaceDeclaration != null) {
										if (interfaceDeclaration.getDir() == OIDir.IN) {
											return interfaceDeclaration;
										}
									}
								}
							}
							
						}
					} else {
						// case per number
						for (int j = 0; j < numAssociation; j++) {
							associationElem = (AssociationElement) child.getChild(j);
							if (ToolManager.getVectorName(associationElem.getActualPart().toString()).equalsIgnoreCase(signalName)) {
								InterfaceDeclaration interfaceDeclaration = getInterfaceDeclaration(j);
								if (interfaceDeclaration != null && interfaceDeclaration.getDir() == OIDir.IN) {
									return interfaceDeclaration;
								}
							}
						}
					}
				}
			}
		}
		return null;
		
	}

	private InterfaceDeclaration getInterfaceDeclaration(String signalName) {
		for (InterfaceDeclaration interfaceDeclaration : listComponentDeclaration) {
			if (interfaceDeclaration.getId().equalsIgnoreCase(signalName)) {
				return interfaceDeclaration;
			}
		}
		return null;
	}

	InterfaceDeclaration getInterfaceDeclaration(Integer numPort) {
		Integer nbPort = -1;
		for (InterfaceDeclaration interfaceDeclaration : listComponentDeclaration) {
			TypeDefinitionSubType typeDef = (TypeDefinitionSubType) interfaceDeclaration.getChild(0);
			Name name = (Name) typeDef.getChild(0);
			int numExtension = name.getNumChildren();
			if (numExtension == 0) {
				nbPort ++;
			} else {
				NameExtensionRange extension = (NameExtensionRange) name.getChild(0);
				HdlEntity componentEntity = getEntity();
				InterfaceList genericInstantiation = null;
				if (componentEntity != null) {
					genericInstantiation = componentEntity.setGeneric(getComponentInstantiation());
				}
				AssociationList generic = searchGeneric(componentInstantiation);
				
				int getnbInput = ToolManager.getnbInput(extension.getChild(0), generic, genericInstantiation);
				nbPort += getnbInput +1;
			}
		}
		if (numPort < listComponentDeclaration.size()) {
			return listComponentDeclaration.get(numPort);
		}
		return null;
	}


	private AssociationList searchGeneric(
			ComponentInstantiation componentInstantiation) {
		System.out.println("searchGeneric componentInstantiation  "+componentInstantiation.getLocation());
		int numChildren = componentInstantiation.getNumChildren();
		AssociationList generic = null;
		AssociationList mapComponent = null;
		for (int i = 0; i < numChildren; i++) {
			VHDLNode child = componentInstantiation.getChild(i);
			if (child instanceof AssociationList) {
				if (mapComponent == null) {
					System.out.println("mapComponent");
					mapComponent = (AssociationList) child;
				} else {
					System.out.println("generic");
					generic = mapComponent;
					mapComponent = (AssociationList) child;
				}
			}
		}

		return generic;
	}

	public void setComponentDeclaration(Architecture architecture) {
		int numChildren = architecture.getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			VHDLNode child = architecture.getChild(i);
			if (child instanceof ComponentDeclaration) {
				if (((ComponentDeclaration)child).getId().equalsIgnoreCase(getName())) {
					int numInterfaceList = child.getNumChildren();
					for (int j = 0; j < numInterfaceList; j++) {
						VHDLNode interfacelist = child.getChild(j);
						if (interfacelist instanceof InterfaceList) {
							int numInterface = interfacelist.getNumChildren();
							for (int k = 0; k < numInterface; k++) {
								InterfaceDeclaration interfaceElem = (InterfaceDeclaration) interfacelist.getChild(k);
								
								listComponentDeclaration.add(interfaceElem);
							}
						}
					}
				}
			}
		}
		
	}

	public String getArchiName() {
		return archi.getArchitecture().getId();
	}

	public HdlArchitecture getHdlArchi() {
		return archi;
	}

	public SourceLocation getLocation() {
		return componentInstantiation.getLocation();
	}

}
