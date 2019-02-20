package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.plugin.tool.vhdl.manager.ArchitectureManager;
import org.zamia.plugin.tool.vhdl.manager.PackageBodyManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.Block;
import org.zamia.vhdl.ast.BlockDeclarativeItem;
import org.zamia.vhdl.ast.ComponentDeclaration;
import org.zamia.vhdl.ast.ConcurrentStatement;
import org.zamia.vhdl.ast.GenerateStatement;
import org.zamia.vhdl.ast.InterfaceDeclaration;
import org.zamia.vhdl.ast.InterfaceList;
import org.zamia.vhdl.ast.PackageBody;
import org.zamia.vhdl.ast.SequentialProcess;
import org.zamia.vhdl.ast.SharedVariableDeclaration;
import org.zamia.vhdl.ast.SignalDeclaration;
import org.zamia.vhdl.ast.SubProgram;
import org.zamia.vhdl.ast.TypeDefinition;
import org.zamia.vhdl.ast.VHDLNode;
import org.zamia.vhdl.ast.VHDLPackage;
import org.zamia.vhdl.ast.VariableDeclaration;

public class RuleSTD_05900 extends Rule {
	
	private static final String INTEGER = "INTEGER";
	private static final String [] REPORT_TAGS = new String[]{ReportFile.TAG_PORT, ReportFile.TAG_SIGNAL, ReportFile.TAG_VARIABLE};
	
	private final Map<String, Integer> map;
	
	private String entityID = null;
	private String architectureID = null;
	private String [] values = new String[] {"", "", ""};
	private ReportFile reportFile = new ReportFile(this);

	public RuleSTD_05900() {
		super(RuleE.STD_05900);
		map = new HashMap<>();
		for (int i = 0; i < REPORT_TAGS.length; i++) {
			map.put(REPORT_TAGS[i], i);
		}
	}

	@Override
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		initializeRule(parameterSource, ruleId);
		
		Pair<Integer, RuleResult> result = null;
		if (reportFile.initialize()) {
			try {
				Map<String, HdlFile> hdlFiles = ArchitectureManager.getArchitecture();
				for (Entry<String, HdlFile> hdlFile : hdlFiles.entrySet()) {
					List<HdlEntity> hdlEntities = hdlFile.getValue().getListHdlEntity();
					for (HdlEntity hdlEntity : hdlEntities) {
						entityID = hdlEntity.getEntity().getId();
						// TODO do something in entity?
						List<HdlArchitecture> hdlArchitectures = hdlEntity.getListHdlArchitecture();
						for (HdlArchitecture hdlArchitecture : hdlArchitectures) {
							// do operations in each architecture
							Architecture architecture = hdlArchitecture.getArchitecture();
							architectureID = architecture.getId();
							// declarative items
							for (int i = 0; i < architecture.getNumDeclarations(); i++) {
								expandDeclarativeItem(architecture.getDeclaration(i));
							}
							// concurrent statements
							for (int i = 0; i < architecture.getNumConcurrentStatements(); i++) {
								ConcurrentStatement cStatement = architecture.getConcurrentStatement(i);
								expandConcurrentStatement(cStatement);
							}
						} 
					}
					entityID = architectureID = null;
					// do operations in each package
					List<VHDLPackage> packages = hdlFile.getValue().getListHdlPackage();
					for (VHDLPackage vhdlPackage : packages) {
						for (int i = 0; i < vhdlPackage.getNumDeclarations(); i++) {
							expandDeclarativeItem(vhdlPackage.getDeclaration(i));
						}
					}
				}
				// do operations in each package body
				hdlFiles = PackageBodyManager.getPackageBody();
				for (Entry<String, HdlFile> hdlFile : hdlFiles.entrySet()) {
					List<PackageBody> packageBodies = hdlFile.getValue().getListPackageBody();
					for (PackageBody packageBody : packageBodies) {
						for (int i = 0; i < packageBody.getNumDeclarations(); i++) {
							expandDeclarativeItem(packageBody.getDeclaration(i));
						}
					}
				}
			} catch (EntityException e) {
				LogNeedBuild();
				return new Pair<>(NO_BUILD, null);
			}
			result = reportFile.save();
		}
		return result;
	}
	
	private void setValues(int i, String value) {
		for (int j = 0; j < values.length; j++) {
			if (j == i) {
				values[j] = value;
			} else {
				values[j] = "";
			}
		}
	}
	
	private void checkViolation(VHDLNode node) {
		TypeDefinition type = null;
		String sonarTag = null;
		if (node instanceof SignalDeclaration) {
			SignalDeclaration item = (SignalDeclaration)node;
			type = item.getType();
			sonarTag = "signal";
			setValues(map.get(ReportFile.TAG_SIGNAL), item.getId());
		} else if (node instanceof VariableDeclaration) {
			VariableDeclaration item = (VariableDeclaration)node;
			type = (TypeDefinition) item.getChild(0);
			sonarTag = "variable";
			setValues(map.get(ReportFile.TAG_VARIABLE), item.getId());
		} else if (node instanceof SharedVariableDeclaration) {
			SharedVariableDeclaration item = (SharedVariableDeclaration)node;
			type = item.getType();
			sonarTag = "variable";
			setValues(map.get(ReportFile.TAG_VARIABLE), item.getId());
		} else if (node instanceof InterfaceDeclaration) {
			InterfaceDeclaration item = (InterfaceDeclaration)node;
			type = item.getType();
			sonarTag = "port";
			setValues(map.get(ReportFile.TAG_PORT), item.getId());
		}

		if (type!= null && type.toString().contains(INTEGER) && type.getNumChildren() == 1) {
			Element element = reportFile.addViolation(node.getLocation(), entityID, architectureID);
			for (int i = 0; i < REPORT_TAGS.length; i++) {
				reportFile.addElement(REPORT_TAGS[i], values[i], element);
			}
			// TODO add sonar msg
		}
	}
	
	private void expandDeclarativeItem(BlockDeclarativeItem item) {
		if (item instanceof ComponentDeclaration) {
			ComponentDeclaration componentDeclaration = (ComponentDeclaration)item;
			InterfaceList list = componentDeclaration.getInterfaces();
			if (list != null) {
				for (int i = 0; i < list.getNumInterfaces(); i++) {
					checkViolation(list.get(i));
				}
			}
		} else if (item instanceof SubProgram) {
			SubProgram subProgram = (SubProgram) item;
			for (int i = 0; i < subProgram.getNumDeclarations(); i++) {
				expandDeclarativeItem(subProgram.getDeclaration(i));
			}
		} else if (item instanceof VariableDeclaration || item instanceof SignalDeclaration || item instanceof SharedVariableDeclaration) {
			checkViolation(item);
		}
	}
	
	private void expandConcurrentStatement(ConcurrentStatement concurrentStatement) {
		if (concurrentStatement instanceof SequentialProcess) {
			SequentialProcess process = (SequentialProcess) concurrentStatement;
			for (int i = 0; i < process.getNumDeclarations(); i++) {
				checkViolation(process.getDeclaration(i));
			}
		} else if (concurrentStatement instanceof Block) {
			logger.info("[Concurrent] Block statement at %d", concurrentStatement.getLocation().fLine);
			// TODO
		} else if (concurrentStatement instanceof GenerateStatement) {
			GenerateStatement generateStatement = (GenerateStatement) concurrentStatement;
			for (int i = 0; i < generateStatement.getNumChildren() - 2; i++) {
				VHDLNode node = generateStatement.getChild(i);
				if (node instanceof BlockDeclarativeItem) {
					expandDeclarativeItem((BlockDeclarativeItem) node);
				} else if (node instanceof ConcurrentStatement) {
					expandConcurrentStatement((ConcurrentStatement) node);
				}
			}
		}
	}

}
