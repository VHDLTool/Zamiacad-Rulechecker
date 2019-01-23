package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.ClockSignal;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.plugin.tool.vhdl.ResetSignal;
import org.zamia.plugin.tool.vhdl.manager.EntityManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.plugin.tool.vhdl.rules.impl.SonarQubeRule;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.Entity;

public class RuleSTD_01700 extends Rule {
	private static final String clockString = "Clock";
	private static final String resetString = "Reset";

	public RuleSTD_01700() {
		super(RuleE.STD_01700);
	}

	@Override
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		initializeRule(parameterSource, ruleId);
		
		//// Makes the clock signal list. 
		
		List<ClockSignal> clockSignals = getAllClockSignals();
		if (clockSignals == null) {
			return new Pair<>(NO_BUILD, null);
		}
		Map<Entity, List<ClockSignal>> clockMap = new HashMap<>();
		for (ClockSignal clockSignal: clockSignals) {
			if (clockMap.containsKey(clockSignal.getEntity())) {
				clockMap.get(clockSignal.getEntity()).add(clockSignal);
			} else {
				List<ClockSignal> list = new ArrayList<>();
				list.add(clockSignal);
				clockMap.put(clockSignal.getEntity(), list);
			}
		}
		
		//// Makes the reset signal list. 
		
		List<ResetSignal> resetSignals = getAllResetSignals();
		if (resetSignals == null) {
			return new Pair<>(NO_BUILD, null);
		}
		Map<Entity, List<ResetSignal>> resetMap = new HashMap<>();
		for (ResetSignal resetSignal: resetSignals) {
			if (resetMap.containsKey(resetSignal.getEntity())) {
				resetMap.get(resetSignal.getEntity()).add(resetSignal);
			} else {
				List<ResetSignal> list = new ArrayList<>();
				list.add(resetSignal);
				resetMap.put(resetSignal.getEntity(), list);
			}
		}
		
		//// Write report
		
		Pair<Integer, RuleResult> result = null;
		boolean isClock;
		ReportFile reportFile = new ReportFile(this);
		if (reportFile.initialize()) {
			try {
				for (Entry<String, HdlFile> entry: EntityManager.getEntity().entrySet()) {
					for (HdlEntity hdlEntity: entry.getValue().getListHdlEntity()) {
						Entity entity = hdlEntity.getEntity();
						List<ClockSignal> clockList = clockMap.get(entity);
						List<ResetSignal> resetList = resetMap.get(entity);
						int specialPortCnt = 0;
						for (int i = 0; i < entity.getNumInterfaceDeclarations(); i++) {
							isClock = false;
							logger.info(">>>>>>> Entity: %s >>>>>>> Interface: %s", entity.getId(), entity.getPorts().get(i).getId());
							if (clockList != null) {
								for (ClockSignal clockSignal: clockList) {
									logger.info("<<<<<<<< Clock: %s", clockSignal.toString());
									if (clockSignal.toString().equalsIgnoreCase(entity.getPorts().get(i).getId())) {
										isClock = true;
										if (i != specialPortCnt) {
											Element info = reportFile.addViolation(entity.getPorts().get(i).getLocation(), entity, clockSignal.getArchitecture());
											reportFile.addElement(ReportFile.TAG_CLOCK, clockSignal.toString(), info);
											reportFile.addElement(ReportFile.TAG_RESET, " ", info);
											reportFile.addSonarTags(info, SonarQubeRule.SONAR_ERROR_STD_01700, new Object[] {clockString, clockSignal.toString(), entity.getId()},
													SonarQubeRule.SONAR_MSG_STD_01700, new Object[] {clockSignal.toString(), entity.getId()});
										}
										specialPortCnt++;
										break;
									}
								}
							}
							if (isClock) {
								continue;
							}
							if (resetList != null) {
								for (ResetSignal resetSignal: resetList) {
									logger.info("<<<<<<<< Reset: %s", resetSignal.toString());
									if (resetSignal.toString().equalsIgnoreCase(entity.getPorts().get(i).getId())) {
										if (i != specialPortCnt) {
											Element info = reportFile.addViolation(entity.getPorts().get(i).getLocation(), entity, resetSignal.getArchitecture());
											reportFile.addElement(ReportFile.TAG_CLOCK, " ", info);
											reportFile.addElement(ReportFile.TAG_RESET, resetSignal.toString(), info);
											reportFile.addSonarTags(info, SonarQubeRule.SONAR_ERROR_STD_01700, new Object[] {resetString, resetSignal.toString(), entity.getId()},
													SonarQubeRule.SONAR_MSG_STD_01700, new Object[] {resetSignal.toString(), entity.getId()});
										}
										specialPortCnt++;
										break;
									}
								}
							}
						}
					}
				}
			} catch (EntityException e) {
				LogNeedBuild();
				return new Pair<>(NO_BUILD, null);
			}
			result = reportFile.save();
		}
		
		return result;
	}

}
