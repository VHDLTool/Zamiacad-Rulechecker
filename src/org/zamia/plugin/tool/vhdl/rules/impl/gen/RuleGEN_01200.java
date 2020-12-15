package org.zamia.plugin.tool.vhdl.rules.impl.gen;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.zamia.SourceLocation;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.plugin.tool.vhdl.rules.IHandbookParam;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.StringParam;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.plugin.tool.vhdl.rules.impl.SonarQubeRule;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.Entity;

/*
 * Identification of process label.
 * Process label name starts by a prefix defined by rule parameter.
 * No parameters.
 */
public class RuleGEN_01200 extends Rule {

	public RuleGEN_01200() {
		super(RuleE.GEN_01200);
	}
	
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {

		initializeRule(parameterSource, ruleId);
		
		//// Initialize the parameter from rule configuration.

		List<IHandbookParam> parameterList = getParameterList(zPrj);
		if (parameterList == null) {
			return new Pair<Integer, RuleResult> (WRONG_PARAM, null);
		}
		
		//// Makes the process list. 
		
		Dictionary<Process, ProcessInfo> processInfos = getAllProcesses();
		if (processInfos == null) {
			return new Pair<Integer, RuleResult> (NO_BUILD, null);
		}
		
		//// Write report
		
		Pair<Integer, RuleResult> result = null;
		
		ReportFile reportFile = new ReportFile(this);
		if (reportFile.initialize()) {
			Enumeration<Process> processes = processInfos.keys();
			while (processes.hasMoreElements()) {
				Process process = processes.nextElement();
				ProcessInfo processInfo = processInfos.get(process);
				if (process.hasLabel()) {
					String processLabel = process.getLabel();
					
					boolean isValid = false;
					for (IHandbookParam param : parameterList) {
						isValid |= param.isValid(processLabel);
					}
					
					if (!isValid) {					
						SourceLocation location = process.getLocation(); 
						Entity entity = processInfo.getEntity();
						Architecture architecture = processInfo.getArchitecture();
						Element info = reportFile.addViolation(location, entity, architecture);
						reportFile.addElement(ReportFile.TAG_PROCESS, processLabel, info);

						/*String paramPosition = null;
						String paramValue = null;
						if (parameterList.size() == 1)
						{
							IHandbookParam param = parameterList.get(0);
							if (param instanceof StringParam)
							{
								StringParam stringParam = (StringParam) param;
								paramPosition = stringParam.getPosition().toString().toLowerCase();
								paramValue = stringParam.getValue().toLowerCase();
							}
						}*/
						//FIXME: this is copy pasted from RuleSTD__00200.java
						String paramString = null;
						HashMap<StringParam.Position, String> params = new HashMap<StringParam.Position, String>();
						for (IHandbookParam param : parameterList)
						{
							if (param instanceof StringParam)
							{
								StringParam stringParam = (StringParam) param;
								
								if (params.containsKey(stringParam.getPosition()))
								{
									String positionValues = (String) params.get(stringParam.getPosition());
									params.put(stringParam.getPosition(), positionValues + ", " + stringParam.getValue());
								}
								else
								{
									params.put(stringParam.getPosition(), stringParam.getValue());
								}
							}
						}
						for (Map.Entry<StringParam.Position, String> entry2: params.entrySet())
						{
							paramString = paramString != null? paramString + " or to ": "";
							paramString = paramString + entry2.getKey().toString() + " " + entry2.getValue();
						}
						//endFIXME
						
						if (paramString != null )
						{
							reportFile.addSonarTags(info, SonarQubeRule.SONAR_ERROR_GEN_01200, new Object[] {processLabel}, SonarQubeRule.SONAR_MSG_GEN_01200, new Object[] {processLabel, paramString.toLowerCase()});
						}
					}
				}
			}

			result = reportFile.save();
		}

		return result;	
	}
}
