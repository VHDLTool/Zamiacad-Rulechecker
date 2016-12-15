package org.zamia.plugin.tool.vhdl.rules;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.manager.ReportManager.ParameterSource;
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
	 
	 public static final int COL_PARAM_SOURCE = 5;

	 public static final int COL_SELECTED = 6;
	 
	 public static final int COL_STATUS = 7;
	 
	 public static final int COL_LOG_FILE = 8;
	 
	 private String typeSelect;
	 
    public RuleObject(ZamiaProject zPrj, String typeSelect) {
    	super();
    	this.typeSelect = typeSelect;
    			
    	if (typeSelect.equalsIgnoreCase("Tool")) {
    		rules = toolService.findAllTools(zPrj);
    	} else {
    		rules = ruleService.findAllRules(zPrj);
    	}
    	
        entetes = new String[] {  "<html>Requirement ID</html>", "<html>Requirement Name</html>", "<html>Implemented /Not Implemented</html>" ,
        			"Type" , "Parameters",  "Parameters source", "", "status", "Log file"}; // the last column "enable" isn't in header to hide this
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
            return rule.getParameter().equalsIgnoreCase("Yes")? rule.getParameterSource().toString(): "";
        case 6:
            return rule.isSelect();
        case 7:
            return rule.getStatus();
        case 8:
            return rule.getLogFile();
        case 9:
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
        case 5:
        case 7:
        case 8:
            return String.class;

        case 6:
        case 9:
            return Boolean.class;
            
         default:
            return Object.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        
        if (columnIndex == COL_SELECTED && ((String)getValueAt(rowIndex, COL_ENABLE)).equalsIgnoreCase("Implemented")) return true;
        
        if (columnIndex == COL_LOG_FILE) return true;
        
        if (columnIndex == COL_PARAM_SOURCE && ((String)getValueAt(rowIndex, COL_PARAM)).equalsIgnoreCase("Yes") && !typeSelect.equalsIgnoreCase("Tool")) return true;

        return false;
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    	
    	final RuleStruct rule = rules.get(rowIndex);
    	
    	switch (columnIndex) {
		case 0:
			rule.setId((String) aValue);
			break;
		case 1:
			rule.setName((String) aValue);
			break;
		case 2:
			rule.setImplemented((String) aValue);
			break;
		case 3:
			rule.setType((String) aValue);
			break;
		case 4:
			rule.setParameter((String) aValue);
			break;
		case 5:
			if (rule.getParameter().equalsIgnoreCase("Yes"))
			{
				rule.setParameterSource(ParameterSource.valueOf((String )aValue));
			}
			break;
		case 6:
			rule.setSelect((boolean) aValue);
			break;
		case 7:
			rule.setStatus((String) aValue);
			break;
		case 8:
			rule.setLogFile((String) aValue);
			break;
		case 9:
			rule.setEnable((boolean) aValue);
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