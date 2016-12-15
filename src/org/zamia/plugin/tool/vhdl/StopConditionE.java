package org.zamia.plugin.tool.vhdl;

public enum StopConditionE {
	NAN(""),
	MAX_STAGE("maxStage"),
	IO_PAD("IO Pad"),
	IO("IO Black Box"),
	STATE_MACHINE("State Machine"),
	CONSTANT_ASSIGNMENT("Constant Assignment");

	private String stopConditionE;

	private StopConditionE(String _stopConditionE) {
		stopConditionE = _stopConditionE;
	}
	
	
	@Override
	public String toString() {
		return stopConditionE;
	}
	
}
