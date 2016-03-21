package org.zamia.plugin.tool.vhdl.rules;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;


public class ButtonCellEditor extends AbstractCellEditor implements  
TableCellEditor {private static final long serialVersionUID = -9030406143699523582L;  

private String editingValue;  

@Override  
public Component getTableCellEditorComponent(JTable table, Object value,  
		boolean isSelected, int row, int column) {  

	editingValue = value.toString();  

	// Customize it  
	String fileName = value.toString();
	Integer index = fileName.lastIndexOf("/") > fileName.lastIndexOf("\\") ? fileName.lastIndexOf("/") : fileName.lastIndexOf("\\");
	String name = (index == -1) ? fileName : fileName.substring(index+1);
	if (name.length() == 0) {
		if (index != -1) {
			name = fileName.substring(0,index);
			index = name.lastIndexOf("/") > name.lastIndexOf("\\") ? name.lastIndexOf("/") : name.lastIndexOf("\\");
			name = name.substring(index+1);
		} else {
			return null;
		}
	}

	JButton button = new JButton(name); 
	button.addActionListener(new TraitementBut2(fileName));
	return button;  
}  

@Override  
public Object getCellEditorValue() {  
	return editingValue;  
}  

@Override  
public void cancelCellEditing() {  
	super.cancelCellEditing();  
}  


public class TraitementBut2 implements   ActionListener
{
	String fileName;
	public TraitementBut2(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * obligatoire car test implémente l'interface ActionListener
	 */
	public void actionPerformed(ActionEvent e)
	{
		try {
			Desktop.getDesktop().open(new File(fileName));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e2) {
			System.out.println("Exception  "+e2.getClass().getSimpleName());
		}
	}

}
}

