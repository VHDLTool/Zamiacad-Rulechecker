package org.zamia.plugin.tool.vhdl.rules;

public enum StatusE {
	// status of rule 
	NOT_IMPLEPMENTED("Not implemented"), // this means that the corresponding rule has not been implemented
	NOT_EXECUTED("Not executed"), // this means that the corresponding rule has not been checked
	PASSED("Passed"), // the rule has been checked with success on the VLSI project
	// This is not the primary objective of the VHDL TOOL.
	FAILED("Failed"),    // the rule has failed. The violations are reported in the violations report.
	REPORTED("Reported");
	
	private String status;
	
	StatusE(String status){
		this.status = status;
	}
	
	@Override
	public String toString() {
		return status;
	}
	
}
