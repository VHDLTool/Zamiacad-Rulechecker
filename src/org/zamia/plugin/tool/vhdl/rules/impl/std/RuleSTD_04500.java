package org.zamia.plugin.tool.vhdl.rules.impl.std;

import org.w3c.dom.Element;
import org.zamia.SourceLocation;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.ClockSource;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.ListClockSource;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.plugin.tool.vhdl.manager.ClockSignalSourceManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.plugin.tool.vhdl.rules.impl.RuleManager;
import org.zamia.util.Pair;

/*
 * Clock Reassignment.
 * Do not reassign a clock in a concurrent statement.
 * No parameters.
 */
public class RuleSTD_04500 extends Rule {

	public RuleSTD_04500() {
		super(RuleE.STD_04500);
	}
	
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {

		initializeRule(parameterSource, ruleId);
		
		//// Make the clock source list.
		
		ListClockSource listClockSource;
		try {
			listClockSource = ClockSignalSourceManager.getClockSourceSignal();
		} catch (EntityException e) {
			LogNeedBuild();
			return new Pair<Integer, RuleResult> (RuleManager.NO_BUILD, null);
		}

		//// Write report
		
		Pair<Integer, RuleResult> result = null;
		
		ReportFile reportFile = new ReportFile(this);
		if (reportFile.initialize()) {
			for (ClockSource clockSource : listClockSource.getListClockSource()) {
				if (clockSource.checkAffectation()) {
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
					
//					reportFile.addElement(ReportFile.TAG_SONAR_ERROR, "Clock signal " + CLOCK_NAME_SRC + " is reassigned to " + clockId, info);
//					reportFile.addElement(ReportFile.TAG_SONAR_MSG, "Remove this assignment and replace " + clockId + " with \r\n" + 
//							+ CLOCK_NAME_SRC, info);
				}
			}
			
			result = reportFile.save();
		}
		
		return result;
	}
}
