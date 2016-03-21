package org.zamia.plugin.tool.vhdl;

public enum NumberReportE {
		NAN(""),
		FIRST("_1"),
		SECOND("_2"),
		THIRD("_3");
		
		private String suffix;
	
		NumberReportE(String _suffix) {
			suffix = _suffix;
		}
		
		
		@Override
		public String toString() {
			return suffix;
		}

}
