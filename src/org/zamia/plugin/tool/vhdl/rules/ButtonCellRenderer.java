package org.zamia.plugin.tool.vhdl.rules;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ButtonCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = -3987688353662913146L;  
	   
    @Override  
    public Component getTableCellRendererComponent(JTable table, Object value,  
        boolean isSelected, boolean hasFocus, int row, int column) {  
   
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

        // Customize it  
    	String fileName = value.toString();
    	Integer index = fileName.lastIndexOf("/") > fileName.lastIndexOf("\\") ? fileName.lastIndexOf("/") : fileName.lastIndexOf("\\");
    	String name = (index == -1) ? fileName : fileName.substring(index+1);
    	if (name.length() == 0 && index != -1) {
    			name = fileName.substring(0,index);
    			index = name.lastIndexOf("/") > name.lastIndexOf("\\") ? name.lastIndexOf("/") : name.lastIndexOf("\\");
    			name = name.substring(index+1);
    	}

    	if (name.length() == 0) {
            if (((String)table.getValueAt(row, RuleObject.COL_ENABLE)).equalsIgnoreCase("Not Implemented")) {
                Color clr = Color.LIGHT_GRAY;
                component.setBackground(clr);
            } else {
                Color clr = Color.WHITE;
                component.setBackground(clr);
            }

    	}
        JButton button = new JButton(name); 
        if (((String)table.getValueAt(row, RuleObject.COL_ENABLE)).equalsIgnoreCase("Not Implemented")) {
            Color clr = Color.LIGHT_GRAY;
            button.setBackground(clr);
        } else {
            Color clr = Color.WHITE;
            button.setBackground(clr);
        }

        return button;  
    }  
    
}
