package org.zamia.plugin.tool.vhdl.rules.impl;

public class SonarQubeRule {
	
	/** Sonar error messages */
	public static final String SONAR_ERROR_GEN_01000 = "The variable %s is miswritten";
	public static final String SONAR_ERROR_GEN_01200 = "Label %s is miswritten";
	public static final String SONAR_ERROR_GEN_01600 = "The package %s is miswritten";
	public static final String SONAR_ERROR_GEN_02100 = "The architecture %s is miswritten";
	public static final String SONAR_ERROR_GEN_02300 = "Clock signal change its name from %s to %s";
	public static final String SONAR_ERROR_GEN_02400 = "Reset signal change its name from %s to %s";
	public static final String SONAR_ERROR_GEN_04900 = "Reset signal %s is misused";
	
	public static final String SONAR_ERROR_STD_00200 = "Clock %s signal miswritten";
	public static final String SONAR_ERROR_STD_00300 = "Reset signal %s is miswritten";
	public static final String SONAR_ERROR_STD_00400 = "Label is missing";
	public static final String SONAR_ERROR_STD_01800_IEEE = "IEEE library identified";
	public static final String SONAR_ERROR_STD_01800_OTHER = "Other library identified";
	public static final String SONAR_ERROR_STD_03600_LEVEL_1 = "Reset signal %s is active %s on contrary of other reset signal inside %s";
	public static final String SONAR_ERROR_STD_03600_LEVEL_2 = "Reset signal %s is active %s on contrary of other reset signal inside the design";
	public static final String SONAR_ERROR_STD_03700 = "Reset generation identified";
	public static final String SONAR_ERROR_STD_03800 = "Synchronous %s signal not asynchronously reset";
	public static final String SONAR_ERROR_STD_04500 = "Clock signal %s is reassigned to %s";
	public static final String SONAR_ERROR_STD_04600 = "Too many clock domains in the design";
	public static final String SONAR_ERROR_STD_04700 = "Too many clock domains in the entity %s";
	public static final String SONAR_ERROR_STD_04800_LEVEL_1 = "Clock signal %s use %s edge on contrary of other clock signal inside %s";
	public static final String SONAR_ERROR_STD_04800_LEVEL_2 = "Clock signal %s use rising edge on contrary of other clock signal inside the design";
	public static final String SONAR_ERROR_STD_05000_MISSING = "Signal %s is not in the sensitivity list of the synchronous process";
	public static final String SONAR_ERROR_STD_05000_MORE = "Signal %s should not be in the sensitivity list of the process";
	public static final String SONAR_ERROR_STD_05300_MISSING = "Signal %s is not in the sensitivity list of the process";
	public static final String SONAR_ERROR_STD_05300_MORE = "Signal %s should not be in the sensitivity list of the process";
	public static final String SONAR_ERROR_STD_01000 = "The file %s contains more than 1 entity declaration";
	public static final String SONAR_ERROR_STD_01100 = "The file %s contains more than 1 architecture declaration";
	public static final String SONAR_ERROR_STD_00900 = "The file %s does not contain the name of the entity %s";
	public static final String SONAR_ERROR_STD_06300 = "Process %s contains a variable which name is %s";
	public static final String SONAR_ERROR_STD_01700 = "%s %s is not declared at the beginning of the entity %s";
	public static final String SONAR_ERROR_STD_06800 = "%s signal has got an initial value at definition.";
	public static final String SONAR_ERROR_STD_01300 = "Port %s is declared on the same line as other ports";

	/** Sonar remediation messages */
	public static final String SONAR_MSG_GEN_01000 = "Change the variable %s to include %s as %s";
	public static final String SONAR_MSG_GEN_01200 = "Change label name %s to include %s as %s";
	public static final String SONAR_MSG_GEN_01600 = "Change the package %s to include %s as %s";
	public static final String SONAR_MSG_GEN_02100 = "Replace the architecture %s by behavioral, rtl or simulation";
	public static final String SONAR_MSG_GEN_02300 = "Change %s to %s";
	public static final String SONAR_MSG_GEN_02400 = "Change signal name %s to %s";
	public static final String SONAR_MSG_GEN_04900 = "Use %s for clock inputs only and not as common signal";
	
	public static final String SONAR_MSG_STD_00200 = "Change signal name %s to %s";
	public static final String SONAR_MSG_STD_00300 = "Change signal name %s to %s";
	public static final String SONAR_MSG_STD_00400 = "Add label to the process";
	public static final String SONAR_MSG_STD_01800_IEEE = "Nothing to be done";
	public static final String SONAR_MSG_STD_01800_OTHER = "If %s is a technology dependent library, use it only in a single VHDL file";
	public static final String SONAR_MSG_STD_03600_LEVEL_1 = "Choose a unique reset polarity for every reset signal in entity %s";
	public static final String SONAR_MSG_STD_03600_LEVEL_2 = "Choose a unique reset polarity for every reset signal in the design";
	public static final String SONAR_MSG_STD_03700 = "Control reset generation mechanism";
	public static final String SONAR_MSG_STD_03800 = "Initialize %s signal with a reset";
	public static final String SONAR_MSG_STD_04500 = "Remove this assignment and replace %s with %s";
	public static final String SONAR_MSG_STD_04600 = "Lower clock domain from %d to %s %s";
	public static final String SONAR_MSG_STD_04700 = "Lower clock domain from %d to %s %s inside %s";
	public static final String SONAR_MSG_STD_04800_LEVEL_1 = "Check that clock domain change mechanism is an authorized one";
	public static final String SONAR_MSG_STD_04800_LEVEL_2 = "Check that only one file in the design implements clock change from rising edge to falling edge";
	public static final String SONAR_MSG_STD_05000_MISSING = "Add %s in the sensitivity list of %s";
	public static final String SONAR_MSG_STD_05000_MORE = "Use only clock and reset signals in the sensitivity of a synchronous process";
	public static final String SONAR_MSG_STD_05300_MISSING = "Add %s in the sensitivity list of %s";
	public static final String SONAR_MSG_STD_05300_MORE = "Remove %s from the sensitivity list of %s";
	public static final String SONAR_MSG_STD_01000 = "Reduce the number of entity declarations to 1 in the file %s";
	public static final String SONAR_MSG_STD_01100 = "Reduce the number of architecture declarations to 1 in the file %s";
	public static final String SONAR_MSG_STD_00900 = "Change the %s to contain the name of the entity %s";
	public static final String SONAR_MSG_STD_06300 = "Change variable %s by signal for process %s";
	public static final String SONAR_MSG_STD_01700 = "Move %s declaration at the beginning of the entity %s";
	public static final String SONAR_MSG_STD_06800 = "Remove initialization of %s signal in the definition.";
	public static final String SONAR_MSG_STD_01300 = "Dedicate one line for the declaration of %s";
}
