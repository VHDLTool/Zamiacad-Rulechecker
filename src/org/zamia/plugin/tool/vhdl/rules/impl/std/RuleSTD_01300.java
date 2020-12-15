package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.zamia.SourceLocation;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.plugin.tool.vhdl.manager.ArchitectureManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.plugin.tool.vhdl.rules.impl.SonarQubeRule;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.BlockDeclarativeItem;
import org.zamia.vhdl.ast.ComponentDeclaration;
import org.zamia.vhdl.ast.Entity;
import org.zamia.vhdl.ast.InterfaceDeclaration;
import org.zamia.vhdl.ast.InterfaceList;
import org.zamia.vhdl.ast.VHDLPackage;

public class RuleSTD_01300 extends Rule {
	
	private int line = 0;
	private ReportFile reportFile;
	private InterfaceDeclaration firstPort;

	public RuleSTD_01300() {
		super(RuleE.STD_01300);
	}

	@Override
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		initializeRule(parameterSource, ruleId);
		
		// get entities
		Map<String, HdlFile> fileMap = new HashMap<>();
		try { 
			fileMap = ArchitectureManager.getArchitecture();
		} catch (EntityException e) {
			LogNeedBuild();
			return new Pair<>(NO_BUILD, null);
		}
		
		// write a report
		Pair<Integer, RuleResult> result = null;
		reportFile = new ReportFile(this);
		if(reportFile.initialize()) {
			for (Entry<String, HdlFile> hdlFile: fileMap.entrySet()) {
				List<HdlEntity> hdlEntities = hdlFile.getValue().getListHdlEntity();
				for (HdlEntity hdlEntity: hdlEntities) {
					Entity entity = hdlEntity.getEntity();
					List<HdlArchitecture> architectureList = hdlEntity.getListHdlArchitecture();
					// port
					firstPort = null;
					for (int i = 0; i < entity.getNumInterfaceDeclarations(); i++) {
						checkViolation(entity.getPorts().get(i), entity.getId(), architectureList.isEmpty() ? " " : architectureList.get(0).getArchitecture().getId());
					}
					// generic
					firstPort = null;
					if (entity.getGenerics() != null) {
						for (int i = 0; i < entity.getGenerics().getNumInterfaces(); i++) {
							checkViolation(entity.getGenerics().get(i), entity.getId(), architectureList.isEmpty() ? " " : architectureList.get(0).getArchitecture().getId());
						}
					}
					// component in architecture
					for (HdlArchitecture hdlArchitecture: architectureList) {
						Architecture architecture = hdlArchitecture.getArchitecture();
                        for (int i = 0; i < architecture.getNumDeclarations(); i++) {
                        	BlockDeclarativeItem item = architecture.getDeclaration(i);
                        	if (item instanceof ComponentDeclaration) {
                    			checkInComponent((ComponentDeclaration) item, entity.getId(), architecture.getId());
                    		}
                        }
					}
				}
				// component in package
                List<VHDLPackage> packages = hdlFile.getValue().getListHdlPackage();
                for (VHDLPackage vhdlPackage : packages) {
                    for (int i = 0; i < vhdlPackage.getNumDeclarations(); i++) {
                    	BlockDeclarativeItem item = vhdlPackage.getDeclaration(i);
                    	if (item instanceof ComponentDeclaration) {
                			checkInComponent((ComponentDeclaration) item, " ", " ");
                		}
                    }
                }
			}
			result = reportFile.save();
		}
		return result;
	}
	
	private void checkInComponent(ComponentDeclaration componentDeclaration, String entityId, String architectureId) {
		InterfaceList list = componentDeclaration.getInterfaces();
		firstPort = null;
		// component port
		if (list != null) {
			for (int j = 0; j < list.getNumInterfaces(); j++) {
				checkViolation(list.get(j), entityId, architectureId);
			}
		}
		firstPort = null;
		list = componentDeclaration.getGenerics();
		// component generic
		if (list != null) {
			for (int j = 0; j < list.getNumInterfaces(); j++) {
				checkViolation(list.get(j), entityId, architectureId);
			}
		}
	}
	
	private void checkViolation(InterfaceDeclaration item, String entityId, String architectureId) {
		SourceLocation location = item.getLocation();
		if (line == location.fLine) {
			if (firstPort != null) {
				Element element = reportFile.addViolation(location, entityId, architectureId);
				reportFile.addElement(ReportFile.TAG_PORT, firstPort.getId(), element);
				reportFile.addSonarTags(element, SonarQubeRule.SONAR_ERROR_STD_01300, new Object[] {firstPort.getId()}, SonarQubeRule.SONAR_MSG_STD_01300, new Object[] {firstPort.getId()});
				firstPort = null;
			}
			Element info = reportFile.addViolation(location, entityId, architectureId);
			reportFile.addElement(ReportFile.TAG_PORT, item.getId(), info);
			reportFile.addSonarTags(info, SonarQubeRule.SONAR_ERROR_STD_01300, new Object[] {item.getId()}, SonarQubeRule.SONAR_MSG_STD_01300, new Object[] {item.getId()});
		} else {
			line = location.fLine;
			firstPort = item;
		}
	}

}
