package org.zamia.plugin.tool.vhdl.tools;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.zamia.plugin.tool.vhdl.manager.ReportManager.ParameterSource;
import org.zamia.plugin.tool.vhdl.rules.RuleObject;


public class JToolTableRender extends DefaultTableCellRenderer {
	   /**
	 * 
	 */
	private static final long serialVersionUID = -9015984054412686123L;

	@Override
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

	        if (((String)table.getValueAt(row, RuleObject.COL_ENABLE)).equalsIgnoreCase("Not Implemented") || (!((String)table.getValueAt(row, RuleObject.COL_PARAM_SOURCE)).equalsIgnoreCase("") && column == RuleObject.COL_PARAM_SOURCE)) {
	                Color clr = Color.LIGHT_GRAY;
	                component.setBackground(clr);
	            } else {
	                Color clr = Color.WHITE;
	                component.setBackground(clr);
	            }
	        return component;
	    }
}
