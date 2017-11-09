
package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;

import org.w3c.dom.Element;
import org.zamia.SourceLocation;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.plugin.tool.vhdl.rules.impl.SonarQubeRule;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.Entity;

/*
 * Label for Process
 * Processes are identified by a label.
 * No parameters.
 */
public class RuleSTD_00400 extends Rule {

	public RuleSTD_00400() {
		super(RuleE.STD_00400);
	}

	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {

		initializeRule(parameterSource, ruleId);
		
		//// Makes the process list. 
		
		Dictionary<Process, ProcessInfo> processInfos = getAllProcesses();
		if (processInfos == null) {
			return new Pair<Integer, RuleResult> (NO_BUILD,null);
		}
		
		//// Write report
		
		Pair<Integer, RuleResult> result = null;
		
		ReportFile reportFile = new ReportFile(this);
		if (reportFile.initialize()) {
			Enumeration<Process> processes = processInfos.keys();
			while (processes.hasMoreElements()) {
				Process process = processes.nextElement();
				ProcessInfo processInfo = processInfos.get(process);
				if (!process.hasLabel()) {
					SourceLocation location = process.getLocation(); 
					Entity entity = processInfo.getEntity();
					Architecture architecture = processInfo.getArchitecture();
					Element info = reportFile.addViolation(location, entity, architecture);
					
					reportFile.addSonarTags(info, SonarQubeRule.SONAR_ERROR_STD_00400, null, SonarQubeRule.SONAR_MSG_STD_00400, null);
				}
			}
			
			result = reportFile.save();
		}

		return result;	
	}
}

