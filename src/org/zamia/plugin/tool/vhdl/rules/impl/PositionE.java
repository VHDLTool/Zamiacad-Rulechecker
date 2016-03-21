package org.zamia.plugin.tool.vhdl.rules.impl;

public enum PositionE {
	// type of param for rule
	// if new type is used in verifier param file, add this in enum
	
		PREFIX("prefix"),
		SUFFIX("suffix"),
		CONTAIN("contain");
		
		private String type;
		
		//Constructeur
		PositionE(String type){
		    this.type = type;
		  }
		
		
		public static boolean exist(String className) {
			for(PositionE typeParam : PositionE.values()) { 
		        if (typeParam.getType().equalsIgnoreCase(className)) {
		        	return true;
		        }
		     }
			return false;
		}
		

		private String getType() {
			return type;
		}

	}

