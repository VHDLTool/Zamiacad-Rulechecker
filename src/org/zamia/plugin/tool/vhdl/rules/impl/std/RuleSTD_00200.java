
package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zamia.SourceLocation;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.ClockSignal;
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
import org.w3c.dom.Element;

/*
 * Name of clock signal.
 * The clock signal name includes some tag defined by rule parameters.
 * Expect two parameters: position and tag. 
 */
public class RuleSTD_00200 extends Rule {

	public RuleSTD_00200() {
		super(RuleE.STD_00200);
	}
	
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		
		initializeRule(parameterSource, ruleId);
		
		//// Initialize parameters PositionE and PartNames from rule configuration.
		
		List<IHandbookParam> parameterList = getParameterList(zPrj);
		if (parameterList == null) {
			return new Pair<Integer, RuleResult> (WRONG_PARAM, null);
		}
		
		//// Makes the clock signal list. 
		
		List<ClockSignal> clockSignals = getAllClockSignals();
		if (clockSignals == null) {
			return new Pair<Integer, RuleResult> (NO_BUILD, null);
		}

		//// Write report
		
		Pair<Integer, RuleResult> result = null;
		
		ReportFile reportFile = new ReportFile(this);
		if (reportFile.initialize()) {
			for (ClockSignal clockSignal : clockSignals) {
				boolean isValid = false;
				for (IHandbookParam param : parameterList)
				{
					isValid |= param.isValid(clockSignal.toString());
				}

				if (!isValid) {				
					SourceLocation location = clockSignal.getLocation(); 
					Entity entity = clockSignal.getEntity();
					Architecture architecture = clockSignal.getArchitecture();
					Element info = reportFile.addViolation(location, entity, architecture);
					reportFile.addElement(ReportFile.TAG_CLOCK, clockSignal.toString(), info);
					String processLabel = clockSignal.getProcess().getLabel();
					if (processLabel == null || processLabel.length() == 0) {
						processLabel = "no label";
					}
					reportFile.addElement(ReportFile.TAG_PROCESS, processLabel, info);
					
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
					
					String paramString = null;
					for (Map.Entry<StringParam.Position, String> entry: params.entrySet())
					{
						paramString = paramString != null? paramString + " or to ": "";
						paramString = paramString + entry.getKey().toString() + " " + entry.getValue();
					}
					
					if (paramString != null)
					{
						reportFile.addSonarTags(info, SonarQubeRule.SONAR_ERROR_STD_00200, new Object[] {clockSignal.toString()}, SonarQubeRule.SONAR_MSG_STD_00200, new Object[] {clockSignal.toString(), paramString.toLowerCase()});
					}
				}
			}

			result = reportFile.save();
		}

		return result;
	}
}
