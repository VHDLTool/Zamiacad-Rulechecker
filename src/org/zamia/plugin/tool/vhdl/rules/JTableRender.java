package org.zamia.plugin.tool.vhdl.rules;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


public class JTableRender extends DefaultTableCellRenderer {
	   /**
	 * 
	 */
	private static final long serialVersionUID = -9015984054412686123L;

	@Override
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        /**
	         * Fixer la couleur de fond de la première colonne en jaune
	         */
	        /**
	         * Colorier les cellules en orange si le montant est négatif
	         */
	            if (((String)table.getValueAt(row, RuleObject.COL_ENABLE)).equalsIgnoreCase("Not Implemented")) {
	                Color clr = Color.LIGHT_GRAY;
	                component.setBackground(clr);
	            } else {
	                Color clr = Color.WHITE;
	                component.setBackground(clr);
	            }
	        return component;
	    }
}
