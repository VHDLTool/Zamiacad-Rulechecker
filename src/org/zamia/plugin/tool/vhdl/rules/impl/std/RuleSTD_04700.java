package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.ClockSignal;
import org.zamia.plugin.tool.vhdl.ClockSource;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.ListClockSource;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.plugin.tool.vhdl.manager.ClockSignalManager;
import org.zamia.plugin.tool.vhdl.manager.ClockSignalSourceManager;
import org.zamia.plugin.tool.vhdl.rules.IHandbookParam;
import org.zamia.plugin.tool.vhdl.rules.IntParam;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.plugin.tool.vhdl.rules.impl.RuleManager;
import org.zamia.plugin.tool.vhdl.rules.impl.SonarQubeRule;
import org.zamia.util.Pair;

/*
 * Number of clock domains per modules.
 * Each module in the design handle only one clock.
 * One Parameter: nbClockDomain (integer)
 */
public class RuleSTD_04700 extends Rule {

	private class Violation {
		private String _fileName;
		private String _entityId;
		private String _architectureId;
		private ClockSource _clockSource;
		
		public Violation(String fileName, String entityId, String architectureId, ClockSource clockSource) {
			_fileName = fileName;
			_entityId = entityId;
			_architectureId = architectureId;
			_clockSource = clockSource;
		}
		
		public void generate(ReportFile reportFile, List<IHandbookParam> parameterList, int nbFailure) {
			int line = _clockSource.getSignalDeclaration().getLocation().fLine;
			Element info = reportFile.addViolation(_fileName, line, _entityId, _architectureId);
			
			reportFile.addElement(ReportFile.TAG_SOURCE_TAG, _clockSource.getTag(), info); 
			reportFile.addElement(ReportFile.TAG_CLOCK, _clockSource.toString(), info); 
			reportFile.addElement(ReportFile.TAG_SIGNAL_TYPE, _clockSource.getType(), info); 

			String paramRelation = null;
			String paramValue = null;
			if (parameterList.size() == 1)
			{
				IHandbookParam param = parameterList.get(0);
				if (param instanceof IntParam)
				{
					IntParam intParam = (IntParam) param;
					paramRelation = intParam.getRelation().toString();
					paramValue = intParam.getValue().toString();
				}
			}
			
			if (paramRelation != null && paramValue != null)
			{
				reportFile.addSonarTags(info, SonarQubeRule.SONAR_ERROR_STD_04700, null, SonarQubeRule.SONAR_MSG_STD_04700, new Object[] {nbFailure, paramRelation, paramValue, _entityId});
			}
			
		}
	}

	public RuleSTD_04700() {
		super(RuleE.STD_04700);
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
		Map<String, HdlFile> listHdlFile;
		try {
			listClockSource = ClockSignalSourceManager.getClockSourceSignal();
			listHdlFile = ClockSignalManager.getClockSignal();
		} catch (EntityException e) {
			LogNeedBuild();
			return new Pair<Integer, RuleResult> (RuleManager.NO_BUILD, null);
		}

		//// Check rule
		
		ArrayList<Violation> violations = new ArrayList<Violation>();
		
		for (Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() != null) {
				for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
					if (hdlFile.getListHdlEntity() != null) {
						for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
							ListClockSource listClockSourceEntity = createListClockSource(listClockSource, hdlEntityItem, hdlArchitectureItem);

							boolean isValid = false;
							for (IHandbookParam param : parameterList)
							{
								isValid |= param.isValid(listClockSourceEntity.getListClockSource().size());
							}

							if (!isValid) 
							{
								String entityId = hdlEntityItem.getEntity().getId();
								String architectureId = hdlArchitectureItem.getArchitecture().getId();
								for (ClockSource clockSource : listClockSourceEntity.getListClockSource()) {
									violations.add(new Violation(hdlFile.getLocalPath(), entityId, architectureId, clockSource));
								}
							}
						}
					}
				}
			}
		}

		//// Write report
		
		Pair<Integer, RuleResult> result = null;
		
		ReportFile reportFile = new ReportFile(this);
		if (reportFile.initialize()) {
			for (Violation violation : violations) {
				violation.generate(reportFile, parameterList, violations.size());
			}
			
			result = reportFile.save();
		}
		
		return result;
	}
	
	private ListClockSource createListClockSource(ListClockSource listClockSource, HdlEntity hdlEntityItem, HdlArchitecture hdlArchitectureItem) {
		ListClockSource listClockSourceEntity = new ListClockSource();
		
		for (ClockSource clockSource : listClockSource.getListClockSource()) {
			if (hdlArchitectureItem.getListProcess() != null) {
				for (Process processItem : hdlArchitectureItem.getListProcess()) {
					if (processItem.isSynchronous()) {
						for (ClockSignal clockSignal : processItem.getListClockSignal()) {
							if (clockSource.equals(clockSignal.getClockSource())) {
								listClockSourceEntity.add(clockSource, clockSignal);
							}
						}
					}
				}
			}
		}
		
		return listClockSourceEntity;
	}
}
