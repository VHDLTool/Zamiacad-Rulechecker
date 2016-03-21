package org.zamia.plugin.tool.vhdl.rules;

public enum RuleTypeE {
	// type of rule 
	ALGO("Algo"), // this requirement targets a rule declared in the VLSI handbook and that can be checked automatically by the tool.
						// In that case, only the violated rules will be reported to the user
	HELP("Help"), // this requirement targets a rule declared in the VLSI handbook and that cannot be checked automatically. 
	// On that particular case, the VHDL TOOL pre-processes the information so that the user can make his own analysis. 
	// The pre-processing may consist of a list of all impacted VHDL elements by a rule but, 
	// no precise check is done at this stage because it may imply deep processing such as synthesis for example.
	// This is not the primary objective of the VHDL TOOL.
	IDE("Tool"), // this requirement focuses on IDE features that help VHDL TOOL users to configure the tool, 
	// generate log file and ease design/review process.
	TOOL("Tool"), 
	RULE("Rule"), 
	NA("NA");    // use for not implemented rule 
	
	private String type;
	
	RuleTypeE(String type){
		this.type = type;
	}
	
	@Override
	public String toString() {
		return type;
	}
	
}