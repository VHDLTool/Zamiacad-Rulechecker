package org.zamia.plugin.tool.vhdl;


public enum NodeInfo {
	
		// type of param for rule
		// if new type is used in verifier param file, add this in enum
		
			NAME("Name"),
			TYPE("Type"),
			LOCATION("Loc"),
			DIRECTION("Type"),
			CLOCK_SOURCE("ClockSource"),
			NB_LINE("NbLine"),
			EDGE("Edge"),
			LEVEL("Level"),
			IS_SYNCHRONOUS("IsSynchronous"),
			HAS_ASYNCHRONOUS_RESET("HasAsynchronousReset"),
			BEFORE("Before"),
			AFTER("After"), 
			TAG("Tag"), 
			RANGE("Range"),
			ID("ID");
			
			private String type;
			
			//Constructeur
			NodeInfo(String type){
			    this.type = type;
			  }
			
		@Override
		public String toString() {
			return type;
		}


		}
