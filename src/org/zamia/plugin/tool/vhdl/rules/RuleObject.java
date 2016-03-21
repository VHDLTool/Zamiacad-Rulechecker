package org.zamia.plugin.tool.vhdl.rules;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.tools.ToolService;

public class RuleObject extends AbstractTableModel  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2942071461964182374L;

	private final String[] entetes;

	private List<RuleStruct> rules = new ArrayList<RuleStruct>();
	 
	 private RuleService ruleService = RuleService.getInstance();
	 
	 private ToolService toolService = ToolService.getInstance();
	 
	 public static final int COL_ID = 0;
	 
	 public static final int COL_NAME = 1;
	 
	 public static final int COL_ENABLE = 2;
	 
	 public static final int COL_TYPE = 3;
	 
	 public static final int COL_PARAM = 4;
	 
	 public static final int COL_SELECTED = 5;
	 
	 public static final int COL_STATUS = 6;
	 
	 public static final int COL_LOG_FILE = 7;
	 
	 
    public RuleObject(ZamiaProject zPrj, String typeSelect) {
    	super();
    	if (typeSelect.equalsIgnoreCase("Tool")) {
    		rules = toolService.findAllTools(zPrj);
    	} else {
    		rules = ruleService.findAllRules(zPrj);
    	}
    	
        entetes = new String[] {  "<html>Requirement ID</html>", "<html>Requirement Name</html>", "<html>Implemented /Not Implemented</html>" ,
        			"Type" , "Parameter",  "", "status", "Log file"}; // the last column "enable" isn't in header to hide this
    }
 
    @Override
    public String getColumnName(int columnIndex) {
        return entetes[columnIndex];
    }

    @Override
    public int getRowCount() {
        return rules.size();
    }

    @Override
    public int getColumnCount() {
        return entetes.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        final RuleStruct rule = rules.get(rowIndex);

        switch (columnIndex) {
        case 0:
            return rule.getId();
        case 1:
            return rule.getName();
        case 2:
        	return rule.getImplemented();
        case 3:
            return rule.getType();
        case 4:
            return rule.getParameter();
        case 5:
            return rule.isSelect();
        case 6:
            return rule.getStatus();
        case 7:
            return rule.getLogFile();
        case 8:
            return rule.isEnable();

        default:
            throw new IllegalArgumentException("Le numero de colonne indique n'est pas valide.");

        }

    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
        case 0:
        case 1:
        case 2:
        case 3:
        case 4:
        case 6:
        case 7:
            return String.class;

        case 5:
        case 8:
            return Boolean.class;

         default:
            return Object.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        
        if (columnIndex == COL_SELECTED && ((String)getValueAt(rowIndex, COL_ENABLE)).equalsIgnoreCase("Implemented")) return true;
        
        if (columnIndex == COL_LOG_FILE) return true;
        
        return false;
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    	
    	switch (columnIndex) {
		case 0:
			rules.get(rowIndex).setId((String) aValue);
			break;
		case 1:
			rules.get(rowIndex).setName((String) aValue);
			break;
		case 2:
			rules.get(rowIndex).setImplemented((String) aValue);
			break;
		case 3:
			rules.get(rowIndex).setType((String) aValue);
			break;
		case 4:
			rules.get(rowIndex).setParameter((String) aValue);
			break;
		case 5:
			rules.get(rowIndex).setSelect((boolean) aValue);
			break;
		case 6:
			rules.get(rowIndex).setStatus((String) aValue);
			break;
		case 7:
			rules.get(rowIndex).setLogFile((String) aValue);
			break;
		case 8:
			rules.get(rowIndex).setEnable((boolean) aValue);
			break;


		default:
			break;
		}

        fireTableCellUpdated(rowIndex, columnIndex);// notify listeners
    }


	public List<RuleStruct> getRuleStruct() {
		return rules;
	}

}