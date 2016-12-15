package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.List;

import org.w3c.dom.Element;
import org.zamia.SourceLocation;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.ClockSource;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.ListClockSource;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.plugin.tool.vhdl.manager.ClockSignalSourceManager;
import org.zamia.plugin.tool.vhdl.rules.IHandbookParam;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.plugin.tool.vhdl.rules.impl.RuleManager;
import org.zamia.util.Pair;

/*
 * Clock domain number in the design.
 * One clock domain is used in the design.
 * One Parameter: nbClockDomain (integer)
 */
public class RuleSTD_04600 extends Rule {

	public RuleSTD_04600() {
		super(RuleE.STD_04600);
	}
	
	@Override
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {

		initializeRule(parameterSource, ruleId);
		
		//// Initialize parameter NbClockDomain from rule configuration.
		
		List<IHandbookParam> parameterList = getParameterList(zPrj);
		if (parameterList == null) {
			return new Pair<Integer, RuleResult> (WRONG_PARAM, null);
		}
		
		//// Make the clock source list.

		ListClockSource listClockSource;
		try {
			listClockSource = ClockSignalSourceManager.getClockSourceSignal();
		} catch (EntityException e) {
			LogNeedBuild();
			return new Pair<Integer, RuleResult> (RuleManager.NO_BUILD, null);
		}

		//// Check rule
		
		boolean isValid = false;
		int clockSourceNumber = listClockSource.getListClockSource().size();
		for (IHandbookParam param : parameterList)
		{
			isValid |= param.isValid(clockSourceNumber);
		}
		
		//// Write report
		
		Pair<Integer, RuleResult> result = null;
		
		ReportFile reportFile = new ReportFile(this);
		if (reportFile.initialize()) {
			if (!isValid) {
				for (ClockSource clockSource : listClockSource.getListClockSource()) {
					String entityId = clockSource.getEntity();
					String architectureId = clockSource.getArchitecture();
					SourceLocation location = clockSource.getSignalDeclaration().getLocation();
					Element info = reportFile.addViolation(location, entityId, architectureId);
					
					String sourceTag = clockSource.getTag();
					reportFile.addElement(ReportFile.TAG_SOURCE_TAG, sourceTag, info); 
					String clockId = clockSource.toString();
					reportFile.addElement(ReportFile.TAG_CLOCK, clockId, info); 
					String signalType = clockSource.getType();
					reportFile.addElement(ReportFile.TAG_SIGNAL_TYPE, signalType, info); 
				}
			}
			
			result = reportFile.save();
		}
		
		return result;
	}
}
