package org.zamia.plugin.tool.vhdl.rules.impl.gen;

import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.plugin.tool.vhdl.manager.ArchitectureManager;
import org.zamia.plugin.tool.vhdl.manager.PackageBodyManager;
import org.zamia.plugin.tool.vhdl.rules.IHandbookParam;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.BlockDeclarativeItem;
import org.zamia.vhdl.ast.Entity;
import org.zamia.vhdl.ast.PackageBody;
import org.zamia.vhdl.ast.SequentialProcess;
import org.zamia.vhdl.ast.SubProgram;
import org.zamia.vhdl.ast.VHDLPackage;
import org.zamia.vhdl.ast.VariableDeclaration;

public class RuleGEN_01000 extends Rule {
	
	private static final String POSITION = "Prefix";
	private static final String VALUE = "v_";
	
	private ReportFile reportFile = new ReportFile(this);
	private List<IHandbookParam> parameterList = null;

	protected RuleGEN_01000() {
		super(RuleE.GEN_01000);
	}

	@Override
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		initializeRule(parameterSource, ruleId);
		
		parameterList = getParameterList(zPrj);
		if (parameterList == null || parameterList.isEmpty()) {
			parameterList = getDefaultStringParamList(POSITION, VALUE);
		}
		
		Pair<Integer, RuleResult> result = null;
		
		// search variable in function
		try {
			PackageBodyManager.getPackageBody();
			PackageBodyManager.resetInfo();
			Map<String, HdlFile> hdlFiles = ArchitectureManager.getArchitecture();
			for (Entry<String, HdlFile> entry: hdlFiles.entrySet()) {
				HdlFile hdlFile = entry.getValue();
				for (HdlEntity hdlEntity: hdlFile.getListHdlEntity()) {
					// search function in entity
					List<BlockDeclarativeItem> items = hdlEntity.getEntity().fDeclarations;
					for (BlockDeclarativeItem item: items) {
						if (item instanceof SubProgram) {
							searchVariableInSubProgram((SubProgram)item, hdlEntity.getEntity(), null);
						}
					}
					// search function in architecture
					for (HdlArchitecture hdlArchitecture: hdlEntity.getListHdlArchitecture()) {
						List<BlockDeclarativeItem> itemList = hdlArchitecture.getArchitecture().fDeclarations;
						for (BlockDeclarativeItem item: itemList) {
							if (item instanceof SubProgram) {
								searchVariableInSubProgram((SubProgram)item, hdlEntity.getEntity(), hdlArchitecture.getArchitecture());
							}
						}
					}
				}
				for (VHDLPackage vhdlPackage: hdlFile.getListHdlPackage()) {
					List<BlockDeclarativeItem> items = vhdlPackage.fDeclarations;
					for (BlockDeclarativeItem item: items) {
						if (item instanceof SubProgram) {
							searchVariableInSubProgram((SubProgram)item, null, null);
						}
					}
				}
				for (PackageBody packageBody: hdlFile.getListPackageBody()) {
					List<BlockDeclarativeItem> items = packageBody.fDeclarations;
					for (BlockDeclarativeItem item: items) {
						if (item instanceof SubProgram) {
							searchVariableInSubProgram((SubProgram)item, null, null);
						}
					}
				}
			}
		} catch (EntityException e) {
			e.printStackTrace();
			return new Pair<> (NO_BUILD, null);
		}
		
		// search variable in process
		Dictionary<Process, ProcessInfo> processInfos = getAllProcesses();
		Map<Process, ProcessInfo> processMap;
		if (processInfos == null) {
			return new Pair<> (NO_BUILD, null);
		} else {
			List<Process> keys = Collections.list(processInfos.keys());
			processMap = keys.stream().collect(Collectors.toMap(Function.identity(), processInfos::get));
		}
		if (reportFile.initialize()) {
			for (Entry<Process, ProcessInfo> entry: processMap.entrySet()) {
				SequentialProcess process = entry.getKey().getSequentialProcess();
				int n;
				if ((n = process.getNumDeclarations()) > 0) {
					BlockDeclarativeItem variable;
					for (int i = 0; i < n; i++) {
						if ((variable = process.getDeclaration(i)) instanceof VariableDeclaration) {
							boolean isValid = false;
							for (IHandbookParam param: parameterList) {
								isValid |= param.isValid(variable.getId());
							}
							if (!isValid) {
								Element element = reportFile.addViolation(
										variable.getLocation(),
										entry.getValue().getEntity(),
										entry.getValue().getArchitecture()
										);
								reportFile.addElement(ReportFile.TAG_VARIABLE, variable.getId(), element);
							}
						}
					}
				}
			}
		}
		return result;
	}
	
	private void searchVariableInSubProgram(SubProgram subProgram, Entity entity, Architecture architecture) {
		for (int i = 0; i < subProgram.getNumDeclarations(); i++) {
			BlockDeclarativeItem item = subProgram.getDeclaration(i);
			if (item instanceof SubProgram) {
				searchVariableInSubProgram((SubProgram) item, entity, architecture);
			} else if (item instanceof VariableDeclaration) {
				boolean isValid = false;
				for (IHandbookParam param: parameterList) {
					isValid |= param.isValid(item.getId());
				}
				if (!isValid) {
					Element element;
					if (entity == null) {
						element = reportFile.addViolation(item.getLocation());
					} else if (architecture == null) {
						element = reportFile.addViolation(
							item.getLocation(),
							entity.getId(),
							""
							);
					} else {
						element = reportFile.addViolation(
								item.getLocation(),
								entity,
								architecture
								);
					}
					reportFile.addElement(ReportFile.TAG_VARIABLE, item.getId(), element);
				}
			}
		}
	}

}
