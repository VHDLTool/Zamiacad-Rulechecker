package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.plugin.tool.vhdl.rules.impl.SonarQubeRule;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.BlockDeclarativeItem;
import org.zamia.vhdl.ast.SequentialProcess;
import org.zamia.vhdl.ast.VariableDeclaration;

public class RuleSTD_06300 extends Rule {

	public RuleSTD_06300() {
		super(RuleE.STD_06300);
	}

	@Override
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		initializeRule(parameterSource, ruleId);
		
		//// Makes the process list. 
		
		Dictionary<Process, ProcessInfo> processInfos = getAllProcesses();
		Map<Process, ProcessInfo> processMap;
		if (processInfos == null) {
			return new Pair<> (NO_BUILD,null);
		} else {
			List<Process> keys = Collections.list(processInfos.keys());
			processMap = keys.stream().collect(Collectors.toMap(Function.identity(), processInfos::get));
		}
		
		Pair<Integer, RuleResult> result = null;
		
		ReportFile reportFile = new ReportFile(this);
		if (reportFile.initialize()) {
			for (Entry<Process, ProcessInfo> entry: processMap.entrySet()) {
				SequentialProcess process = entry.getKey().getSequentialProcess();
				int n;
				if ((n = process.getNumDeclarations()) > 0) {
					BlockDeclarativeItem variable;
					for (int i = 0; i < n; i++) {
						if ((variable = process.getDeclaration(i)) instanceof VariableDeclaration) {
							Element element = reportFile.addViolation(
									((VariableDeclaration)variable).getLocation(),
									entry.getValue().getEntity(),
									entry.getValue().getArchitecture()
									);
							reportFile.addElement(ReportFile.TAG_PROCESS, process.getLabel(), element);
							reportFile.addElement(ReportFile.TAG_VARIABLE, variable.getId(), element);
							reportFile.addSonarTags(element, SonarQubeRule.SONAR_ERROR_STD_06300, new Object[] {process.getLabel(), variable.getId()},
									SonarQubeRule.SONAR_MSG_STD_06300, new Object[] {variable.getId(), process.getLabel()});
						}
					}
				}
			}
			result = reportFile.save();
		}
		return result;
	}

}
