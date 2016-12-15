package org.zamia.plugin.tool.vhdl.tools;

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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.w3c.dom.Element;
import org.zamia.ZamiaLogger;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.manager.ToolManager;
import org.zamia.plugin.tool.vhdl.manager.ZDialogManager;
import org.zamia.plugin.tool.vhdl.rules.ButtonCellEditor;
import org.zamia.plugin.tool.vhdl.rules.ButtonCellRenderer;
import org.zamia.plugin.tool.vhdl.rules.JTableRender;
import org.zamia.plugin.tool.vhdl.rules.RuleObject;
import org.zamia.plugin.tool.vhdl.rules.RuleStruct;
import org.zamia.plugin.tool.vhdl.rules.RuleTypeE;
import org.zamia.plugin.tool.vhdl.rules.SelectAllHeader;
import org.zamia.plugin.tool.vhdl.rules.StatusE;
import org.zamia.plugin.tool.vhdl.rules.impl.RuleManager;
import org.zamia.util.Pair;

public class ZDialogTool extends ZDialogManager  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6014763094098916313L;

	public final static ZamiaLogger logger = ZamiaLogger.getInstance();

	private List<RuleStruct> list = new ArrayList<RuleStruct>();

	private JTable table;

	private ZamiaProject zPrj;

	private static ZDialogTool instance;
	
	final String RULESET = "Ruleset";
	
	private JTextField rulesetTotal;

	final String IDE = "Tool";
	
	private JTextField ideTotal;

	private JTextField ideNotExecuted;

	private JTextField idePassed;

	JButton callFileButon;

	private CallFile callFile;
	
	JButton callRepButon;

	private CallRep callRep;

	JPanel call;
	
	public static ZDialogTool getInstance(ZamiaProject zPrj) {
		ToolManager.init(zPrj);
		if (instance != null && instance.isVisible()) {
			return instance;
		}
		instance = new ZDialogTool(zPrj);
		return instance;
	}
	
	private ZDialogTool(ZamiaProject zPrj) {
		super(zPrj);
		this.zPrj = zPrj;
		this.initComponent();
		this.setSize(1000, 500);
		this.setLocationRelativeTo(null);
		this.setResizable(true);
		this.setTitle("Tools Selector");
		// TODO BGT
		ImageIcon img = new ImageIcon("C:\\Users\\cniesner\\workspace\\workspace_32b_travail\\zamia-eclipse-plugin\\share\\images\\Eclipse_icon.png");

		this.setIconImage(img.getImage());

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
        
        plafComponents.setLayout(new GridLayout(2,1));

        JPanel infoRuleset = addInfoPanel(RULESET);
		plafComponents.add(infoRuleset, BorderLayout.NORTH);
		
        JPanel infoHelp = addInfoPanel(IDE);
		plafComponents.add(infoHelp, BorderLayout.CENTER);
		
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
        
		table = new JTable(new RuleObject(zPrj, "Tool")) {
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
	
		table.getColumnModel().getColumn(RuleObject.COL_ID).setPreferredWidth(100);
		table.getColumnModel().getColumn(RuleObject.COL_NAME).setPreferredWidth(180);
		table.getColumnModel().getColumn(RuleObject.COL_ENABLE).setPreferredWidth(70);
		table.getColumnModel().getColumn(RuleObject.COL_TYPE).setPreferredWidth(50);
		table.getColumnModel().getColumn(RuleObject.COL_PARAM).setPreferredWidth(60);
		table.getColumnModel().getColumn(RuleObject.COL_SELECTED).setPreferredWidth(70);
		table.getColumnModel().getColumn(RuleObject.COL_STATUS).setPreferredWidth(90);

		
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
			
			updateConfigSelectedTools();
			
			deleteDirectory("tool");
			
			boolean ok = true;
			
			List<String> listSelectedRule = new ArrayList<String>();
			for (int i = 0; i < table.getRowCount(); i++) {
				if ((boolean) table.getValueAt(i, RuleObject.COL_SELECTED)) {
					listSelectedRule.add((String) table.getValueAt(i, RuleObject.COL_ID));
				}
			}
			Element racine = ToolManager.initReportXml("tool_reporting", RuleTypeE.NA);
			List<RuleStruct> listRule = ((RuleObject) table.getModel()).getRuleStruct();
			for (int r = 0; r < listRule.size(); r++) {
				RuleStruct ruleItem = listRule.get(r);
				table.getModel().setValueAt("", r, RuleObject.COL_LOG_FILE);
				if (listSelectedRule.contains(ruleItem.getId())) {
					
					Pair<Integer, String> result = LaunchTools.Launch(ruleItem, zPrj);
					Integer nbFailed = result.getFirst();
					if (nbFailed == RuleManager.WRONG_PARAM) {
						// wrong param
						return;
					}
					if (nbFailed == RuleManager.NO_BUILD) {
						// wrong param
						return;
					}
					
					if (nbFailed == -1) {
						ok = false;
					}
					StatusE statusE = nbFailed  == 0 ? (ruleItem.getType().equalsIgnoreCase(RuleTypeE.ALGO.toString()) ? StatusE.PASSED : StatusE.REPORTED) :  StatusE.FAILED;
					table.getModel().setValueAt(statusE == StatusE.FAILED? (StatusE.FAILED.toString()+ " ("+ nbFailed +")") : statusE.toString(), r, RuleObject.COL_STATUS);
					String fileName = result.getSecond();
					
					if (statusE == StatusE.REPORTED || statusE == StatusE.FAILED) {
						table.getModel().setValueAt(fileName, r, RuleObject.COL_LOG_FILE);
					} 

					ToolManager.addReportStatusXml(racine, ruleItem.getId(), statusE, nbFailed, fileName);
				} else if (ruleItem.isEnable()) {
					table.getModel().setValueAt(StatusE.NOT_EXECUTED.toString(), r, RuleObject.COL_STATUS);
					ToolManager.addReportStatusXml(racine, ruleItem.getId(), StatusE.NOT_EXECUTED);
				} else {
					ToolManager.addReportStatusXml(racine, ruleItem.getId(), StatusE.NOT_IMPLEPMENTED);
				}
			}
			
			updateValues(ToolManager.finishReportXml(RuleTypeE.TOOL));
			if (ok) {
				logger.info("Rule Checker: tools selector has been executed with success.");
			} else {
				logger.info("Rule Checker: tools selector has been executed with error.");
			}
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
			updateConfigSelectedTools();
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
		
		Integer nbIdeValue = 0;
		Integer nbIdeNotExecutedValue = 0;
		Integer nbIdePassedValue = 0;
		
		for (int i = 0; i < table.getRowCount(); i++) {
			nbTotalValue++;
			String status = ((String) table.getValueAt(i, RuleObject.COL_STATUS));
				nbIdeValue++;
				if (status.contains(StatusE.NOT_EXECUTED.toString())) {
					nbIdeNotExecutedValue++;
				} else if (status.contains(StatusE.REPORTED.toString())) {
					nbIdePassedValue++;
				}

		}

		rulesetTotal.setText(nbTotalValue.toString());

		ideTotal.setText(nbIdeValue.toString());

		ideNotExecuted.setText(nbIdeNotExecutedValue.toString());

		idePassed.setText(nbIdePassedValue.toString());


		if (fileName.length() == 0) {
			call.setVisible(false);
		} else {
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

	public void updateConfigSelectedTools() {
		
		List<String> listSelectedTool = new ArrayList<String>();
		for (int i = 0; i < table.getRowCount(); i++) {
			if ((boolean) table.getValueAt(i, RuleObject.COL_SELECTED)) {
				listSelectedTool.add((String) table.getValueAt(i, RuleObject.COL_ID));
			}
		}

		String pathFileName = ToolManager.getPathFileName("/rule_checker/rc_config_selected_tools.xml");
		File file = new File(pathFileName);
		if (file.exists()) {
			file.delete();
		}

		Element racine = ToolManager.initReportXml("config_selected_rules", RuleTypeE.NA);
		for (String selectedTool : listSelectedTool) {
			ToolManager.addToolSelectedXml(racine, selectedTool);
		}
		ToolManager.finishReportXml(pathFileName);
	}

	private JPanel addInfoPanel(String title) {
		switch (title) {
		case RULESET:
			return addInfoPanelTotal();
		case IDE:
			return addInfoPanelIde();
	
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
	
	private JPanel addInfoPanelIde() {
	       final JPanel info = new JPanel();
	        TitledBorder titleBorder;
	        titleBorder = BorderFactory.createTitledBorder(IDE);
	        info.setBorder(titleBorder);
	        
			final JPanel nbTotalPanel = new JPanel();
			nbTotalPanel.add(new JLabel("nb total"));
			ideTotal = new JTextField();
			ideTotal.setEditable(false);
			ideTotal.setPreferredSize(new Dimension(40, 24));
			nbTotalPanel.add(ideTotal);
			info.add(nbTotalPanel, BorderLayout.CENTER);
			
			final JPanel nbNotExecutedPanel = new JPanel();
			nbNotExecutedPanel.add(new JLabel("nb not executed"));
			ideNotExecuted = new JTextField();
			ideNotExecuted.setEditable(false);
			ideNotExecuted.setPreferredSize(new Dimension(40, 24));
			nbNotExecutedPanel.add(ideNotExecuted);
			info.add(nbNotExecutedPanel, BorderLayout.CENTER);
			
			final JPanel nbPassedPanel = new JPanel();
			nbPassedPanel.add(new JLabel("nb passed"));

			idePassed = new JTextField();
			idePassed.setEditable(false);
			idePassed.setPreferredSize(new Dimension(40, 24));
			nbPassedPanel.add(idePassed);
			info.add(nbPassedPanel, BorderLayout.CENTER);
			
			return info;
	}
	
}