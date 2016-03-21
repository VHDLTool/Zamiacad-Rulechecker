package org.zamia.plugin.tool.vhdl.rules.impl;

public enum TypeParam {
	// type of param for rule
	// if new type is used in verifier param file, add this in enum
	
		STRING("String", String.class),
		INTEGER("Integer", Integer.class),
		POSITION("PositionE", PositionE.class),
		NONE("None", Object.class);
		
		private String className;
		private Class<?> type;
		
		//Constructeur
		TypeParam(String className, Class<?> type){
		    this.className = className;
		    this.type = type;
		  }
		
		
		
		public static boolean exist(String className) {
			
			for(TypeParam typeParam : TypeParam.values()) { 
		        if (typeParam.getClassName().equalsIgnoreCase(className)) {
		        	return true;
		        }
		     }
			return false;
		}



		private String getClassName() {
			return className;
		}



		public static Class<?> getClass(String className) {
			for(TypeParam typeParam : TypeParam.values()) { 
		        if (typeParam.getClassName().equalsIgnoreCase(className)) {
		        	return typeParam.type;
		        }
		     }
			return NONE.type;
		}
	}

