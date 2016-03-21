package org.zamia.plugin.tool.vhdl;

public enum LevelE {
		NAN(""),
		HIGH("high"),
		LOW("low"),
		BOTH("both");
		
		
		private String level;

		private LevelE(String _level) {
			level = _level;
		}
		
		
		@Override
		public String toString() {
			return level;
		}

}
