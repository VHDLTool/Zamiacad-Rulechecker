package org.zamia.plugin.tool.vhdl;


public enum NodeType {
	
		// type of param for rule
		// if new type is used in verifier param file, add this in enum
		
			FILE("file"),
			ENTITY("entity"),
			ARCHITECTURE("architecture"),
			IO("IO"),
			PROCESS("process"),
			CLOCK_SIGNAL("clockSignal"),
			CLOCK_SOURCE("clockSource"),
			RESET_SIGNAL("resetSignal"),
			REGISTER("register"),
			SOURCE("source"),
			REGISTER_SOURCE("registerSource"),
			INPUT("input"),
			RESET_SOURCE("resetSource"),
			LIBRARY("library"),
			COMPONENT("component"),
			MAP("map"),
			INSTANCE("instance"),
			EDGE("edge"),
			LEVEL("level"), 
			SENSITIVITY("sensitivity"), 
			ALL("signal"),
			DUMP("dump"), 
			SINK("sink"), 
			STAGE("stage"),
			STOP_CONDITION("stopCondition");
			
			private String type;
			
			//Constructeur
			NodeType(String type){
			    this.type = type;
			  }
			
		@Override
		public String toString() {
			return type;
		}


		}
