package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.RegisterInput;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.plugin.tool.vhdl.Sensitivity;
import org.zamia.plugin.tool.vhdl.manager.InputCombinationalProcessManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.plugin.tool.vhdl.rules.impl.RuleManager;
import org.zamia.plugin.tool.vhdl.rules.impl.SensitivityRuleViolation;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.Entity;

/*
 * Sensitivity list for combinational processes.
 * Combinational processes have a sensitivity list including all inputs signals which are read.
 * No Parameters.
 */
public class RuleSTD_05300 extends Rule {

	private HdlFile _hdlFile;
	private Entity _entity;
	private Architecture _architecture;
	private List<SensitivityRuleViolation> _violations;
	
	public RuleSTD_05300() {
		super(RuleE.STD_05300);
	}

	@Override
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		
		initializeRule(parameterSource, ruleId);
		
		//// Make register list

		Map<String, HdlFile> hdlFiles;
		try {
			hdlFiles = InputCombinationalProcessManager.getInputCombinationalProcess();
		} catch (EntityException e) {
			logger.error("Current project needs a build.");
			return new Pair<Integer, RuleResult> (RuleManager.NO_BUILD, null);
		}

		//// Check rule
		
		_violations = new ArrayList<SensitivityRuleViolation>();
		
		for(Entry<String, HdlFile> entry : hdlFiles.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() == null) 
				continue;
			
			_hdlFile = hdlFile;
			for (HdlEntity hdlEntityItem : _hdlFile.getListHdlEntity()) {
				_entity = hdlEntityItem.getEntity();
				if (hdlEntityItem.getListHdlArchitecture() == null) 
					continue;
				
				for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
					_architecture = hdlArchitectureItem.getArchitecture();
					if (hdlArchitectureItem.getListProcess() == null) 
						continue;
					
					for (Process process : hdlArchitectureItem.getListProcess()) {
						if (process.isSynchronous()) 
							continue;

						checkSensitivityList(process);
						checkSensitivityUsed(process);
					}
				}
			}
		}

		//// Write report
		
		Pair<Integer, RuleResult> result = null;
		
		ReportFile reportFile = new ReportFile(this);
		if (reportFile.initialize()) {
			for (SensitivityRuleViolation violation : _violations) {
				violation.generate(reportFile);
			}

			result = reportFile.save();
		}
		
		return result;
	}

	private void checkSensitivityUsed(Process process) {
		for (Sensitivity sensitivity : process.getListSensitivity()) {
			boolean find = false;
			for (RegisterInput input : process.getListInput()) {
				if (checkSensitivityUsedInSignal(sensitivity, input)) {
					find = true;
				}
			}
			
			if (!find && (sensitivity.isVector() || sensitivity.isPartOfVector())) {
				// check partial used
				for (RegisterInput input : process.getListInput()) {
					if (input.getVectorName().equalsIgnoreCase(sensitivity.getVectorName())) {
						find = true;
					}
				}

				if (find) {
					for (int i = sensitivity.getIndexMin() ; i <= sensitivity.getIndexMax(); i++) {
						boolean findIndex = false;
						String vectorName = sensitivity.toString()+"("+i+")";
						for (RegisterInput input : process.getListInput()) {
							if (vectorName.toString().equalsIgnoreCase(input.toString())) {
								findIndex = true;
							}
						}
						if (!findIndex) {
							String fileName = getFileName(_hdlFile);
							int line = sensitivity.getLocation().fLine; 
							String sensitivityName = vectorName;
							_violations.add(
									new SensitivityRuleViolation(fileName, line, _entity, _architecture, process, sensitivityName, false));
						}
					}
				}
				
			}
			if (!find) {
				String fileName = getFileName(_hdlFile);
				int line = sensitivity.getLocation().fLine; 
				String sensitivityName = sensitivity.toString();
				_violations.add(
						new SensitivityRuleViolation(fileName, line, _entity, _architecture, process, sensitivityName, false));
			}
		}
		
	}

	private boolean checkSensitivityUsedInSignal(Sensitivity sensitivity, RegisterInput input) {
		boolean find = false;
		if (sensitivity.toString().equalsIgnoreCase(input.toString())) {
			find = true;
		}
		return find;
	}

	private void checkSensitivityList(Process process) {
		for (RegisterInput input : process.getListInput()) {
			checkSensitivityRegister(input, process.getListSensitivity(), process);
		}
	}

	private void checkSensitivityRegister(RegisterInput input, ArrayList<Sensitivity> listSensitivity,
		Process process) {
		boolean find = false;
		for (Sensitivity sensitivity : listSensitivity) {
			if (input.toString().equalsIgnoreCase(sensitivity.toString())) {
				find = true;
			}
		}
		// case not defined : partial used  (vector)
		if (!find && (input.isVector() || input.isPartOfVector())) {
			for (Sensitivity sensitivity : listSensitivity) {
				if (input.getVectorName().equalsIgnoreCase(sensitivity.toString())) {
					find = true;
				}
			}
		}
		// case not defined : partial defined  (vector)
		if (!find && (input.isVector()||input.isPartOfVector())) {
			// check partial defined
			for (Sensitivity sensitivity : listSensitivity) {
				if (input.getVectorName().equalsIgnoreCase(sensitivity.getVectorName())) {
					find = true;
				}
			}
			
			if (find) {
				int indexMin = 0;
				int indexMax = 0;
				if (input.isAscending()) {
					indexMin = input.getLeft();
					indexMax = input.getRight();
				} else {
					indexMax = input.getLeft();
					indexMin = input.getRight();
				}
				for (int i = indexMin ; i <= indexMax; i++) {
					boolean findIndex = false;
					String vectorName = input.toString()+"("+i+")";
					for (Sensitivity sensitivity : listSensitivity) {
						if (vectorName.toString().equalsIgnoreCase(sensitivity.toString())) {
							findIndex = true;
						}
					}
					if (!findIndex) {
						String fileName = getFileName(_hdlFile);
						int line = input.getLocation().fLine; 
						String sensitivityName = vectorName;
						_violations.add(
								new SensitivityRuleViolation(fileName, line, _entity, _architecture, process, sensitivityName, true));
					}
				}
			}
		}

		if (!find) {
			String fileName = getFileName(_hdlFile);
			int line = input.getLocation().fLine; 
			String sensitivityName = input.toString();
			_violations.add(
					new SensitivityRuleViolation(fileName, line, _entity, _architecture, process, sensitivityName, true));
		}
	}
	
	/*
	 * Remove / or \ at the beginning of the filename
	 */
	private String getFileName(HdlFile hdlFile) {
		String localPath = hdlFile.getLocalPath();
		String fileName = localPath;
		
		if (fileName.length() > 0) {
			char firstChar = fileName.charAt(0);
			if (firstChar == '/' || firstChar == '\\') {
				fileName = fileName.substring(1);
			}
			
		}
		
		return fileName;
	}
}
