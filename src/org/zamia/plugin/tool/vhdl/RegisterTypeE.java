package org.zamia.plugin.tool.vhdl;

public enum RegisterTypeE {
	NAN("NAN"),
	DISCRETE("discrete"),
	VECTOR("vector"),
	VECTOR_PART("vector_part"),
	RECORD("record"), 
	ENUMERATION("enumeration"), 
	RECORD_PART("record_part"),
	STATE_ARRAY_TYPE("state_array_type"),
	UNKNOWN_TYPE("unknown_type");
	
	
	private String type;

	private RegisterTypeE(String _type) {
		type = _type;
	}
	
	
	@Override
	public String toString() {
		return type;
	}

}
