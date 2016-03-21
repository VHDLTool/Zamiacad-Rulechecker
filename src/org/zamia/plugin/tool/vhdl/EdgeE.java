package org.zamia.plugin.tool.vhdl;

public enum EdgeE {
		NAN(""),
		RISING("rising"),
		FALLING("falling"),
		BOTH("both");

		private String edge;

		private EdgeE(String _edge) {
			edge = _edge;
		}
		
		
		@Override
		public String toString() {
			return edge;
		}
		
}
