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
import org.zamia.plugin.tool.vhdl.HdlSubProgram;
import org.zamia.plugin.tool.vhdl.HdlSubProgram.TYPE_SUBPROGRAM;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.plugin.tool.vhdl.manager.SubProgramManager;
import org.zamia.plugin.tool.vhdl.rules.IHandbookParam;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.plugin.tool.vhdl.rules.impl.SonarQubeRule;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.BlockDeclarativeItem;
import org.zamia.vhdl.ast.SequentialProcess;
import org.zamia.vhdl.ast.SubProgram;
import org.zamia.vhdl.ast.VariableDeclaration;

public class RuleGEN_01000 extends Rule {
	
	private static final String POSITION = "Prefix";
	private static final String VALUE = "v_";

	public RuleGEN_01000() {
		super(RuleE.GEN_01000);
	}

	@Override
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		initializeRule(parameterSource, ruleId);
	
		ReportFile reportFile = new ReportFile(this);
		List<IHandbookParam> parameterList = null;
		
		// Uncomment this line to enable parameters
		parameterList = getParameterList(zPrj);
		if (parameterList == null || parameterList.isEmpty()) {
			parameterList = getDefaultStringParamList(POSITION, VALUE);
		}
		
		Pair<Integer, RuleResult> result = null;

		if (reportFile.initialize()) {
			try {
				List<HdlSubProgram> hdlSubPrograms = SubProgramManager.getSubProgram();
				for (HdlSubProgram hdlSubProgram : hdlSubPrograms) {
					SubProgram subProgram = hdlSubProgram.getSubProgram();
					HdlEntity hdlEntity = hdlSubProgram.getExtraInfo().getHdlEntity();
					HdlArchitecture hdlArchitecture = hdlSubProgram.getExtraInfo().getHdlArchitecture();
					for (int i = 0; i < subProgram.getNumDeclarations(); i++) {
						BlockDeclarativeItem item = subProgram.getDeclaration(i);
						if (item instanceof VariableDeclaration) {
							boolean isValid = false;
							for (IHandbookParam param: parameterList) {
								isValid |= param.isValid(item.getId());
							}
							if (!isValid) {
								Element element;
								if (hdlEntity == null) {
									element = reportFile.addViolation(item.getLocation(), " ", " ");
								} else if (hdlArchitecture == null) {
									element = reportFile.addViolation(
										item.getLocation(),
										hdlEntity.getEntity().getId(),
										" "
										);
								} else {
									element = reportFile.addViolation(
											item.getLocation(),
											hdlEntity.getEntity(),
											hdlArchitecture.getArchitecture()
											);
								}
								reportFile.addElement(ReportFile.TAG_PROCESS, " ", element);
								reportFile.addElement(ReportFile.TAG_FUNCTION, hdlSubProgram.getType() == TYPE_SUBPROGRAM.FUNCTION ? subProgram.getId() : " ", element);
								reportFile.addElement(ReportFile.TAG_PROCEDURE, hdlSubProgram.getType() == TYPE_SUBPROGRAM.PROCEDURE ? subProgram.getId() : " ", element);
								reportFile.addElement(ReportFile.TAG_VARIABLE, item.getId(), element);
								reportFile.addSonarTags(element,
										SonarQubeRule.SONAR_ERROR_GEN_01000,
										new Object[] {item.getId()},
										SonarQubeRule.SONAR_MSG_GEN_01000,
										new Object[] {item.getId(), VALUE, POSITION.toLowerCase()});
							}
						}
					}
				}
			} catch (EntityException e) {
				logger.error(e.getMessage());
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
			for (Entry<Process, ProcessInfo> entry: processMap.entrySet()) {
				SequentialProcess process = entry.getKey().getSequentialProcess();
				int n;
				if ((n = process.getNumDeclarations()) > 0) {
					BlockDeclarativeItem variable;
					for (int i = 0; i < n; i++) {
						variable = process.getDeclaration(i);
						if (variable instanceof VariableDeclaration) {
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
								reportFile.addElement(ReportFile.TAG_PROCESS, process.getLabel() != null ? process.getLabel() : " ", element);
								reportFile.addElement(ReportFile.TAG_FUNCTION, " ", element);
								reportFile.addElement(ReportFile.TAG_PROCEDURE, " ", element);
								reportFile.addElement(ReportFile.TAG_VARIABLE, variable.getId(), element);
								reportFile.addSonarTags(element,
										SonarQubeRule.SONAR_ERROR_GEN_01000,
										new Object[] {variable.getId()},
										SonarQubeRule.SONAR_MSG_GEN_01000,
										new Object[] {variable.getId(), VALUE, POSITION.toLowerCase()});
							}
						}
					}
				}
			}
			result = reportFile.save();
		}
		return result;
	}

}
