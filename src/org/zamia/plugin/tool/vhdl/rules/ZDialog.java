package org.zamia.plugin.tool.vhdl.rules;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.w3c.dom.Element;
import org.zamia.ZamiaLogger;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.manager.ReportManager.ParameterSource;
import org.zamia.plugin.tool.vhdl.manager.ReportManager;
import org.zamia.plugin.tool.vhdl.manager.SynthesisReport;
import org.zamia.plugin.tool.vhdl.manager.ToolManager;
import org.zamia.plugin.tool.vhdl.manager.ZDialogManager;
import org.zamia.util.Pair;

public class ZDialog extends ZDialogManager  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6014763094098916313L;

	public final static ZamiaLogger logger = ZamiaLogger.getInstance();

	private List<RuleStruct> list = new ArrayList<RuleStruct>();

	private JTable table;

	private ZamiaProject zPrj;

	private static ZDialog instance;
	
	final String RULESET = "Ruleset";
	
	private JTextField rulesetTotal;

	final String HELP = "Help";
	
	private JTextField helpTotal;

	private JTextField helpNotExecuted;

	private JTextField helpPassed;

	final String ALGO = "Algo";
	
	private JTextField algoTotal;

	private JTextField algoNotExecuted;

	private JTextField algoPassed;

	private JTextField algoFailed;

	JButton callFileButon;

	private CallFile callFile;

	JButton callRepButon;

	private CallRep callRep;

	JPanel call;

	public static ZDialog getInstance(ZamiaProject zPrj) {
		ToolManager.init(zPrj);
		if (instance != null && instance.isVisible()) {
			return instance;
		}
		instance = new ZDialog(zPrj);
		return instance;
	}
	
	private ZDialog(ZamiaProject zPrj) {
		super(zPrj);
		this.zPrj = zPrj;
		this.initComponent();
		this.setSize(1000, 500);
		this.setLocationRelativeTo(null);
		this.setResizable(true);
		this.setTitle("Rules Selector");

	}

	public List<RuleStruct> showZDialog(){
		this.setVisible(true);      
		return this.list;      
	}

	private void initComponent(){
		final JPanel gui = new JPanel(new BorderLayout(5,5));

		JPanel InfoComponents = new JPanel(
                new BorderLayout( 3,3));
        
      //JToolBar tb = new JToolBar();
        JPanel plafComponents = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 3,3));
        
        plafComponents.setLayout(new GridLayout(3,1));

        JPanel infoRuleset = addInfoPanel(RULESET);
		plafComponents.add(infoRuleset, BorderLayout.NORTH);
		
	     //JToolBar tb = new JToolBar();
        JPanel infoHelp = addInfoPanel(HELP);
		plafComponents.add(infoHelp, BorderLayout.CENTER);
		
        JPanel infoAlgo = addInfoPanel(ALGO);
		plafComponents.add(infoAlgo, BorderLayout.SOUTH);
		
		InfoComponents.add(plafComponents, BorderLayout.NORTH);

		JPanel filePanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 4,4));
		
		

		call = new JPanel();
		callFile = new CallFile();
		callFileButon = new JButton(callFile);
		call.add(callFileButon);
		callRep = new CallRep();
		callRepButon = new JButton(callRep);
		call.add(callRepButon);
		
		filePanel.add(call);

		InfoComponents.add(new JLabel(""));
		InfoComponents.add(filePanel, BorderLayout.SOUTH);

        gui.add(InfoComponents,BorderLayout.NORTH);
        
		table = new JTable(new RuleObject(zPrj, "rule")) {
			/**
			 * 
			 */
			private static final long serialVersionUID = -6967709882190378807L;

			//Implement table cell tool tips.           
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try {
                	if (colIndex == RuleObject.COL_LOG_FILE)
                		tip = getValueAt(rowIndex, RuleObject.COL_LOG_FILE).toString();
                } catch (RuntimeException e1) {
                    //catch null pointer exception if mouse is over an empty line
                }

                return tip;
            }
		};
	
		table.getColumnModel().getColumn(RuleObject.COL_ID).setPreferredWidth(50);
		table.getColumnModel().getColumn(RuleObject.COL_NAME).setPreferredWidth(180);
		table.getColumnModel().getColumn(RuleObject.COL_ENABLE).setPreferredWidth(70);
		table.getColumnModel().getColumn(RuleObject.COL_TYPE).setPreferredWidth(50);
		table.getColumnModel().getColumn(RuleObject.COL_PARAM).setPreferredWidth(60);
		table.getColumnModel().getColumn(RuleObject.COL_PARAM_SOURCE).setPreferredWidth(60);
		table.getColumnModel().getColumn(RuleObject.COL_SELECTED).setPreferredWidth(70);
		table.getColumnModel().getColumn(RuleObject.COL_STATUS).setPreferredWidth(90);
		
		TableColumn paramSourceColumn = table.getColumnModel().getColumn(RuleObject.COL_PARAM_SOURCE);
		JComboBox<String> comboParamSource = new JComboBox<String>();
		for (ParameterSource p : ParameterSource.values())
		{
			comboParamSource.addItem(p.toString());
		}
		paramSourceColumn.setCellEditor(new DefaultCellEditor(comboParamSource));
		
		TableColumn col = table.getColumnModel().getColumn(RuleObject.COL_LOG_FILE);  
		ButtonCellRenderer renderer = new ButtonCellRenderer();  
		ButtonCellEditor editor = new ButtonCellEditor();  
		col.setCellEditor(editor);
		col.setCellRenderer(renderer);
		table.setDefaultRenderer(Object.class, new JTableRender());
		
		table.setAutoResizeMode(1);
		table.getTableHeader().setPreferredSize(new Dimension(0, 80));
		
		final TableRowSorter<TableModel> rowSorter
        = new TableRowSorter<>(table.getModel());
		table.setRowSorter(rowSorter);
		rowSorter.setSortable(RuleObject.COL_SELECTED, false);
		
		TableColumn tc = table.getColumnModel().getColumn(RuleObject.COL_SELECTED);
        tc.setHeaderRenderer(new SelectAllHeader(table, RuleObject.COL_SELECTED));

        final JTextField jtfFilter = new JTextField();
        jtfFilter.setPreferredSize(new Dimension(200, 24));
        jtfFilter.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = jtfFilter.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter(text));
                }
            }
        }); 
		   
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Find"),
                BorderLayout.WEST);
        panel.add(jtfFilter, BorderLayout.CENTER);

        JScrollPane tableScroll = new JScrollPane(table);
        Dimension tablePreferred = tableScroll.getPreferredSize();
        tableScroll.setPreferredSize(
            new Dimension(tablePreferred.width, tablePreferred.height/3) );
        
        gui.add( tableScroll, BorderLayout.CENTER );
        
        JPanel dynamicLabels = new JPanel(new BorderLayout(4,4));
        
        final JPanel boutons = new JPanel();
        boutons.add(panel, BorderLayout.WEST);
        boutons.add(new JButton(new LaunchRule()));
        boutons.add(new JButton(new Cancel()));

        dynamicLabels.add(boutons);
        gui.add(dynamicLabels, BorderLayout.SOUTH);
        
		setContentPane(gui);
		updateValues("");
	}  

	private JPanel addInfoPanel(String title) {
		switch (title) {
		case RULESET:
			return addInfoPanelTotal();
		case ALGO:
			return addInfoPanelAlgo();
		case HELP:
			return addInfoPanelHelp();

		default:
			return null;
		}
	}

	private JPanel addInfoPanelTotal() {
       final JPanel info = new JPanel();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder(RULESET);
        info.setBorder(titleBorder);
        
		final JPanel nbTotalPanel = new JPanel();
		nbTotalPanel.add(new JLabel("nb total"));
		
		rulesetTotal = new JTextField();
		rulesetTotal.setEditable(false);
		rulesetTotal.setPreferredSize(new Dimension(40, 24));
		nbTotalPanel.add(rulesetTotal);
		
		info.add(nbTotalPanel, BorderLayout.CENTER);
		
		return info;
	}

	private JPanel addInfoPanelAlgo() {
	       final JPanel info = new JPanel();
	        TitledBorder titleBorder;
	        titleBorder = BorderFactory.createTitledBorder(ALGO);
	        info.setBorder(titleBorder);
	        
			final JPanel nbTotalPanel = new JPanel();
			nbTotalPanel.add(new JLabel("nb total"));
			algoTotal = new JTextField();
			algoTotal.setEditable(false);
			algoTotal.setPreferredSize(new Dimension(40, 24));
			nbTotalPanel.add(algoTotal);
			info.add(nbTotalPanel, BorderLayout.CENTER);
			
			final JPanel nbNotExecutedPanel = new JPanel();
			nbNotExecutedPanel.add(new JLabel("nb not executed"));
			algoNotExecuted = new JTextField();
			algoNotExecuted.setEditable(false);
			algoNotExecuted.setPreferredSize(new Dimension(40, 24));
			nbNotExecutedPanel.add(algoNotExecuted);
			info.add(nbNotExecutedPanel, BorderLayout.CENTER);
			
			final JPanel nbPassedPanel = new JPanel();
			nbPassedPanel.add(new JLabel("nb passed"));

			algoPassed = new JTextField();
			algoPassed.setEditable(false);
			algoPassed.setPreferredSize(new Dimension(40, 24));
			nbPassedPanel.add(algoPassed);
			info.add(nbPassedPanel, BorderLayout.CENTER);
			
			final JPanel nbFailedPanel = new JPanel();
			nbFailedPanel.add(new JLabel("nb failed"));
			algoFailed = new JTextField();
			algoFailed.setEditable(false);
			algoFailed.setPreferredSize(new Dimension(40, 24));
			nbFailedPanel.add(algoFailed);
			info.add(nbFailedPanel, BorderLayout.CENTER);

			return info;
	}

	private JPanel addInfoPanelHelp() {
	       final JPanel info = new JPanel();
	        TitledBorder titleBorder;
	        titleBorder = BorderFactory.createTitledBorder(HELP);
	        info.setBorder(titleBorder);
	        
			final JPanel nbTotalPanel = new JPanel();
			nbTotalPanel.add(new JLabel("nb total"));
			
			helpTotal = new JTextField();
			helpTotal.setEditable(false);
			helpTotal.setPreferredSize(new Dimension(40, 24));
			nbTotalPanel.add(helpTotal);
			info.add(nbTotalPanel, BorderLayout.CENTER);
			
			final JPanel nbNotExecutedPanel = new JPanel();
			nbNotExecutedPanel.add(new JLabel("nb not executed"));
			helpNotExecuted = new JTextField();
			helpNotExecuted.setEditable(false);
			helpNotExecuted.setPreferredSize(new Dimension(40, 24));
			nbNotExecutedPanel.add(helpNotExecuted);
			info.add(nbNotExecutedPanel, BorderLayout.CENTER);
			
			final JPanel nbPassedPanel = new JPanel();
			nbPassedPanel.add(new JLabel("nb reported"));
			helpPassed = new JTextField();
			helpPassed.setEditable(false);
			helpPassed.setPreferredSize(new Dimension(40, 24));
			nbPassedPanel.add(helpPassed);
			info.add(nbPassedPanel, BorderLayout.CENTER);
			
			return info;
	}

	private class LaunchRule extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4539942266722187780L;

		private LaunchRule() {
			super("Launch");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			updateConfigSelectedRules();
			
			// This block is intended to reset the last column
			//
			// Related bug: 
			// If we click the last column and then click launch, the clicked cell will not be updated on UI level
			//
			table.getColumnModel().getColumn(RuleObject.COL_LOG_FILE).setCellEditor(null);
			revalidate();
			repaint();
			((AbstractTableModel)table.getModel()).fireTableDataChanged();
			table.getColumnModel().getColumn(RuleObject.COL_LOG_FILE).setCellEditor(new ButtonCellEditor());
			
			deleteDirectory("rule");
			
			List<String> listSelectedRule = new ArrayList<String>();
			for (int i = 0; i < table.getRowCount(); i++) {
				if ((boolean) table.getValueAt(i, RuleObject.COL_SELECTED)) {
					listSelectedRule.add((String) table.getValueAt(i, RuleObject.COL_ID));
				}
			}
			
			SynthesisReport synthesisReport = new SynthesisReport(SynthesisReport.Purpose.ForRule);
			
			List<RuleStruct> listRule = ((RuleObject) table.getModel()).getRuleStruct();
			for (int r = 0; r < listRule.size(); r++) {
				RuleStruct ruleItem = listRule.get(r);
				String ruleId = ruleItem.getId();
				String ruleType = ruleItem.getType();

				StatusE status = StatusE.NOT_EXECUTED;
				String text = "";
				String reportFileName = "";
				
				if (!listSelectedRule.contains(ruleId)) {
					status = StatusE.NOT_EXECUTED;
					text = status.toString();
					synthesisReport.addRuleReport(ruleId, status);
				} else {
					logger.info("#### Execute rule " + ruleId);
					Pair<Integer, RuleResult> returnObject = LaunchRules.Launch(ruleItem, zPrj);
					
					if (returnObject == null) {
						status = StatusE.FAILED;
						text = status.toString() + "(error)";
						synthesisReport.addRuleReport(ruleId, status);
						logger.error("#### execution failed");
					} else {
						int result = returnObject.getFirst();
						RuleResult ruleResult = returnObject.getSecond();
						
						if (result == ReportManager.WRONG_PARAM) {
							status = StatusE.NOT_EXECUTED;
							text = status.toString() + "(wrong parameter)";
							logger.warn(String.format("#### wrong parameter for rule %s.", ruleId));
							synthesisReport.addRuleReport(ruleId, status);
						} else if (result == ReportManager.NO_BUILD) {
							status = StatusE.NOT_EXECUTED;
							text = status.toString();
							logger.warn("#### project must be rebuild.");
							synthesisReport.addToolReport(ruleId, status, null);
							break;
						} else if (result == 0) {
							boolean isAlgo = ruleType.equalsIgnoreCase(RuleTypeE.ALGO.toString());
							status = isAlgo ? StatusE.PASSED : StatusE.REPORTED;
							text = status.toString();
							logger.info(String.format("#### no violations for rule %s.", ruleId));
							synthesisReport.addRuleReport(ruleId, status);
						} else {
							boolean isAlgo = ruleType.equalsIgnoreCase(RuleTypeE.ALGO.toString());
							status = isAlgo ? StatusE.FAILED : StatusE.REPORTED;
							text = status.toString();
							if (isAlgo) {
								text += String.format("(%d)", result);
							}
							if(ruleResult != null)
								reportFileName = ruleResult.getReportFileName();

							logger.info(String.format("####: %d violations for rule %s.", result, ruleId));
							synthesisReport.addRuleReport(ruleId, status, (reportFileName.replace(zPrj.fBasePath.toString() + "/", "./")).replace("./..", "..").replace(".../", ".."), result, ruleResult);
						}
					}
				}
				
				table.getModel().setValueAt(text, r, RuleObject.COL_STATUS);
				table.getModel().setValueAt(reportFileName, r, RuleObject.COL_LOG_FILE);
			}
			
			updateValues(synthesisReport.saveToFile());
		}
	}
	


	private class Cancel extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2703621250770463916L;

		private Cancel() {
			super("Cancel");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
//			updateConfigSelectedRules();
			setVisible(false);
		}

	}
	
	private class CallFile extends AbstractAction {

		String fileName ="";
		/**
		 * 
		 */
		private static final long serialVersionUID = -2703621250770463916L;

		private CallFile() {
			super("CallFile");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (fileName.length() !=0) {
				try {
					Desktop.getDesktop().open(new File(fileName));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
		public void updateFileName(String fileName) {
			this.fileName = fileName;
		}
	}
	
	private class CallRep extends AbstractAction {

		String fileName ="";
		/**
		 * 
		 */
		private static final long serialVersionUID = -2703621250770463916L;

		private CallRep() {
			super("CallRep");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (fileName.length() !=0) {
				try {
					Desktop.getDesktop().open(new File(fileName));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
		public void updateFileName(String fileName) {
			this.fileName = fileName;
		}
	}
	private void updateValues(String fileName) {
		Integer nbTotalValue = 0;
		
		Integer nbHelpValue = 0;
		Integer nbHelpNotExecutedValue = 0;
		Integer nbHelpPassedValue = 0;

		Integer nbAlgoValue = 0;
		Integer nbAlgoNotExecutedValue = 0;
		Integer nbAlgoPassedValue = 0;
		Integer nbAlgoFailedValue = 0;
		

		for (int i = 0; i < table.getRowCount(); i++) {
			nbTotalValue++;
			String status = ((String) table.getValueAt(i, RuleObject.COL_STATUS));
			String type = ((String) table.getValueAt(i, RuleObject.COL_TYPE));
			if (type.equalsIgnoreCase(RuleTypeE.ALGO.toString())) {
				nbAlgoValue++;
				if (status.contains(StatusE.FAILED.toString())) {
					nbAlgoFailedValue++;
				} else if (status.contains(StatusE.NOT_EXECUTED.toString())) {
					nbAlgoNotExecutedValue++;
				} else if (status.contains(StatusE.PASSED.toString())) {
					nbAlgoPassedValue++;
				}

			} else if (type.equalsIgnoreCase(RuleTypeE.HELP.toString())) {
				nbHelpValue++;
				if (status.contains(StatusE.NOT_EXECUTED.toString())) {
					nbHelpNotExecutedValue++;
				} else if (status.contains(StatusE.REPORTED.toString())) {
					nbHelpPassedValue++;
				}

			}
		}

		rulesetTotal.setText(nbTotalValue.toString());

		helpTotal.setText(nbHelpValue.toString());

		helpNotExecuted.setText(nbHelpNotExecutedValue.toString());

		helpPassed.setText(nbHelpPassedValue.toString());

		algoTotal.setText(nbAlgoValue.toString());

		algoNotExecuted.setText(nbAlgoNotExecutedValue.toString());

		algoPassed.setText(nbAlgoPassedValue.toString());

		algoFailed.setText(nbAlgoFailedValue.toString());


		if (fileName.length() == 0) {
			call.setVisible(false);
		} else {
			// TODO BGT?
			Integer index = fileName.lastIndexOf("/") > fileName.lastIndexOf("\\") ? fileName.lastIndexOf("/") : fileName.lastIndexOf("\\");
			String name = (index == -1) ? fileName : fileName.substring(index+1);
			callFileButon.setText(name);
			callFileButon.setToolTipText(fileName);
			callFile.updateFileName(fileName);
			String rep = fileName.replace(name, "");
			callRepButon.setText("go to reporting directory");
			callRepButon.setToolTipText(rep);
			callRep.updateFileName(rep);
			call.setVisible(true);
		}
	}

	public void updateConfigSelectedRules() {

		String pathFileName = ToolManager.getPathFileName("./rule_checker/rc_config_selected_rules.xml");
		File file = new File(pathFileName);
		if (file.exists()) {
			file.delete();
		}

		Element racine = ToolManager.initReportXml("config_selected_rules", RuleTypeE.NA);

		for (int i = 0; i < table.getRowCount(); i++) {
			if ((boolean) table.getValueAt(i, RuleObject.COL_SELECTED)) {
				String ruleId = (String) table.getValueAt(i, RuleObject.COL_ID);
				String paramSource = (String) table.getValueAt(i, RuleObject.COL_PARAM_SOURCE);
				
				ToolManager.addRuleSelectedXml(racine, ruleId, paramSource);
			}
		}
		
		ToolManager.finishReportXml(pathFileName);
	}
}
