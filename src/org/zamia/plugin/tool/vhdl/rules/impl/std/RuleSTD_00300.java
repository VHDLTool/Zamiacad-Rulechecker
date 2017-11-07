
package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.zamia.SourceLocation;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.plugin.tool.vhdl.ResetSignal;
import org.zamia.plugin.tool.vhdl.rules.IHandbookParam;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.StringParam;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.Entity;

/*
 * Name of reset signal.
 * The reset signal name includes some tag defined in the rule parameters.
 * Expect two parameters: position and tag.
 */
public class RuleSTD_00300 extends Rule {

	public RuleSTD_00300() {
		super(RuleE.STD_00300);
	}
	
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		
		initializeRule(parameterSource, ruleId);
		
		//// Initialize parameters PositionE and PartNames from rule configuration.
		
		List<IHandbookParam> parameterList = getParameterList(zPrj);
		if (parameterList == null) {
			return new Pair<Integer, RuleResult> (WRONG_PARAM, null);
		}
		
		//// Makes the reset signal list. 
		
		List<ResetSignal> resetSignals = getAllResetSignals();
		if (resetSignals == null) {
			return new Pair<Integer, RuleResult> (NO_BUILD, null);
		}

		//// Write report
		
		Pair<Integer, RuleResult> result = null;
		
		ReportFile reportFile = new ReportFile(this);
		if (reportFile.initialize()) {
			for (ResetSignal resetSignal : resetSignals) {
				boolean isValid = false;
				for (IHandbookParam param : parameterList)
				{
					isValid |= param.isValid(resetSignal.toString());
				}

				if (!isValid) {				
					SourceLocation location = resetSignal.getLocation(); 
					Entity entity = resetSignal.getEntity();
					Architecture architecture = resetSignal.getArchitecture();
					Element info = reportFile.addViolation(location, entity, architecture);
					reportFile.addElement(ReportFile.TAG_RESET, resetSignal.toString(), info);
					String processLabel = resetSignal.getProcess().getLabel();
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
						reportFile.addElement(ReportFile.TAG_SONAR_ERROR, "Reset signal " + resetSignal.toString() + " is miswritten", info);
						reportFile.addElement(ReportFile.TAG_SONAR_MSG, "Change signal name " + resetSignal.toString() + " to " + paramString.toLowerCase(), info);
					}
				}
			}

			result = reportFile.save();
		}
		
		return result;
	}
}

