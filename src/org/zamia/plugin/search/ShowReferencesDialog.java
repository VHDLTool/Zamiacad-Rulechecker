/* 
 * Copyright 2008,2009 by the authors indicated in the @author tags. 
 * All rights reserved. 
 * 
 * See the LICENSE file for details.
 * 
 * Created by Guenter Bartsch on Sep 7, 2008
 */
package org.zamia.plugin.search;

import org.eclipse.jface.dialogs.ControlEnableState;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.zamia.ToplevelPath;


/**
 * 
 * @author Guenter Bartsch
 * 
 */

public class ShowReferencesDialog extends Dialog implements SelectionListener {
	
	private Button fSearchButton;
	private Text fSearchJobText, fPathText;
	private String fPath, fSearchJobTextStr = "";
	private Text fDepthText;
	public int fDepth;

	// The controls are occasionally disposed. So, despite the dialog is not destroyed, we still need duplicate
	// values in usual java fields because controls are disposed. 
	
	//Instead of binding controls with values through the enumerated index, we could use a map: Button -> value
	enum Option {
		UsePath, WritesOnly, ReadsOnly, AssignThrough, SupportAlias,
		ScopeLocal, ScopeDown, ScopeGlobal;
		static boolean[] newValues() {
			boolean result[] = new boolean[values().length];
			for (Option o: new Option[] {UsePath, ScopeLocal, WritesOnly})
				result[o.ordinal()] = true;
			return result;
		}
	}
	
	Button[] rwReserved = new Button[2]; // these radio buttons replace checkboxes when assignment through is on
	Composite assignmentPanel, pathPanel;
	
	boolean values[];
	Button[] buttons = new Button[Option.values().length];
	
	private Button getButton(Option option) {
		return buttons[option.ordinal()];
	}
	
	public boolean getValue(Option option) {
		return values[option.ordinal()];
	}
	
	public boolean isSearchUp() {
		return getValue(Option.ScopeGlobal);
	}

	public boolean isSearchDown() {
		return isSearchUp() || getValue(Option.ScopeDown);
	}

	public boolean isFollowAssignments() {
		return getValue(Option.AssignThrough) && getValue(Option.UsePath);
	}

	protected ShowReferencesDialog(Shell parentShell, String aJobText, ToplevelPath aPath, boolean[] values, int aDepth) {
		super(parentShell);
		fPath = (aPath == null) ? "" : aPath.toString();
		fSearchJobTextStr = aJobText;
		fDepth = aDepth;
		this.values = (values == null) ? Option.newValues() : values;
	}


	private Button newButton(Composite parent, String label, Option key, int kind, boolean grabExcessVertical) {
		Button result = new Button(parent, kind | SWT.LEFT);
		result.setText(label);
		setGridData(result, SWT.LEFT, false, SWT.CENTER, grabExcessVertical);
		result.addSelectionListener(this);
		int ord = key.ordinal();
		buttons[ord] = result;
		result.setSelection(values[ord]);
		return result;
	}
	private Button newRadio(Composite parent, String label, Option key) {
		return newButton(parent, label, key, SWT.RADIO, true);
	}

	private Button newCheckBox(Composite parent, String label, final Option key) {
		return newButton(parent, label, key, SWT.CHECK, true);
	}	

	private Composite newComposite(Composite parent, int style, int columns) {
		Composite panel = new Composite(parent, style);
		GridLayout layout = new GridLayout();
		layout.numColumns = columns;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		panel.setLayout(layout);
		return panel;
	}
	
	@Override
	protected Control createContents(Composite parent) {

		Shell shell = getShell();
		shell.setText("Search References");

		Composite panel = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = false;
		panel.setLayout(layout);
		panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		fSearchJobText = new Text(panel, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.LEFT | SWT.BORDER | SWT.V_SCROLL);
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, true, true);
		gd.widthHint = 400;
		gd.heightHint = 100;
		fSearchJobText.setLayoutData(gd);

		Composite mainPanel = createMainPanel(panel);
		setGridData(mainPanel, SWT.FILL, true, SWT.TOP, false);

		Composite buttonBar = createMyButtonBar(panel);
		setGridData(buttonBar, SWT.FILL, true, SWT.BOTTOM, false);

		applyDialogFont(panel);
		
		// initialize control values
		{
			
			fSearchJobText.setText(fSearchJobTextStr);
			
			for (int i = 0 ; i != values.length ; i++) {
				Button b = buttons[i];
				b.setSelection(values[i]);
			}
			
			getButton(Option.UsePath).setEnabled(fPath.length() != 0);
			getButton(Option.AssignThrough).setEnabled(fPath.length() != 0);
			
			fPathText.setText(fPath);
		}

		parent.pack();
		fDepthText.setText(fDepth + "");
		widgetSelected(null);
		
		return panel;
		
	}

	private Composite createMainPanel(Composite parent) {

		Composite panel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = true;
		panel.setLayout(layout);

		createScopeGroup(panel);

		Composite optionsGroup = createOptionsGroup(panel);
		setGridData(optionsGroup, SWT.FILL, true, SWT.FILL, true);
		
		createAssignmentsGroup(parent);
		return panel;
	}
	
	private void createAssignmentsGroup(Composite parent) {

		assignmentPanel = newComposite(parent, SWT.NONE, 2);
			newCheckBox(assignmentPanel, "Follow assignments", Option.AssignThrough);
			Composite depthPanel = newComposite(assignmentPanel, SWT.BORDER, 2);
				Label label = new Label(depthPanel, SWT.NONE);
				label.setText("Depth:");
				fDepthText = new Text(depthPanel, SWT.NONE);
				
				// disable search when depth is not integer 
				fDepthText.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent e) {
						widgetSelected(null);
					}
				});
				fDepthText.setEnabled(false);
				fDepthText.setToolTipText("Limit amount of assignments to follow. Empty or negative value results in unlimited search");
		
	}

	Group createScopeGroup(Composite parent) {
		Group scope = new Group(parent, SWT.SHADOW_ETCHED_IN);
		scope.setText("Scope");
		GridLayout scopeLayout = new GridLayout();
		scopeLayout.numColumns = 3;
		scope.setLayout(scopeLayout);
		
		newRadio(scope, "local", Option.ScopeLocal);
		newRadio(scope, "local+down", Option.ScopeDown);
		newRadio(scope, "global", Option.ScopeGlobal);
		
		setGridData(scope, SWT.FILL, true, SWT.FILL, false);
		return scope;

	}
	
	private Composite createMyButtonBar(Composite parent) {

		Composite panel = newComposite(parent, SWT.NULL, 2);

		Button cancelButton = createButton(panel, IDialogConstants.CANCEL_ID, "Cancel", false);
		setGridData(cancelButton, SWT.RIGHT, true, SWT.BOTTOM, false);

		fSearchButton = createButton(panel, IDialogConstants.OK_ID, "Search", true);
		setGridData(fSearchButton, SWT.RIGHT, false, SWT.BOTTOM, false);

		return panel;
	}
	
	private Composite createOptionsGroup(Composite parent) {

		Composite panel = newComposite(parent, SWT.NONE, 2);

		Button alias = newCheckBox(newComposite(parent, SWT.NONE, 2), "Trace aliased signals", Option.SupportAlias);
		alias.setEnabled(false);
		
		// show checkboxes and hide radio buttons
		
		pathPanel = newComposite(panel, SWT.NONE, 2);
		pathPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		boolean initialButtKind = getValue(Option.AssignThrough);
		setVisible(rwReserved[0] = newButton(pathPanel, "Drivers", Option.WritesOnly, initialButtKind ? SWT.CHECK : SWT.RADIO, true), false);
		setVisible(rwReserved[1] = newButton(pathPanel, "Readers", Option.ReadsOnly, initialButtKind ? SWT.CHECK : SWT.RADIO, true), false);
		newButton(pathPanel, "Drivers", Option.WritesOnly, initialButtKind ? SWT.RADIO : SWT.CHECK, true);
		newButton(pathPanel, "Readers", Option.ReadsOnly, initialButtKind ? SWT.RADIO : SWT.CHECK, true);

		newCheckBox(pathPanel, "Use Path", Option.UsePath);
		fPathText = new Text(pathPanel, SWT.BORDER | SWT.READ_ONLY);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, true);
		fPathText.setLayoutData(gd);

		return panel;
	}

	private void setGridData(Control component, int horizontalAlignment, boolean grabExcessHorizontalSpace, int verticalAlignment, boolean grabExcessVerticalSpace) {
		GridData gd = new GridData();
		component.setLayoutData(gd);
		gd.horizontalAlignment = horizontalAlignment;
		gd.grabExcessHorizontalSpace = grabExcessHorizontalSpace;
		gd.verticalAlignment = verticalAlignment;
		gd.grabExcessVerticalSpace = grabExcessVerticalSpace;
	}

	private void setVisible(Button b, boolean visible) {
		GridData ld = ((GridData)b.getLayoutData());
		ld.exclude = !visible;
		b.setLayoutData(ld);
		b.setVisible(visible);
	}
	
	private void swapButtons(Option visible, int reserved) {
		Button tmp = buttons[visible.ordinal()]; 
		setVisible(buttons[visible.ordinal()] = rwReserved[reserved], true); 
		setVisible(rwReserved[reserved] = tmp, false);
		buttons[visible.ordinal()].setSelection(tmp.getSelection());
	}
	
	/**
	 * Updates values on a button click and enables/disables buttons.
	 * The flow of events:<br>
	 * 	 UsePath -> enables IG search options (over hierarchy)<br>
	 *   Follow Assignments -> enables depth<br>
	 *   					-> controls R/W checkbox shape (cannot do both when following assignments) <br>
	 *   Depth -> disables Search button when contains bad value  <br>
	 * */
	public void widgetSelected(SelectionEvent e) {

		boolean before = isFollowAssignments();
				
		for (int i = 0 ; i != values.length; i++) 
			values[i] = buttons[i].getSelection();

		// UsePath -> IG hierarchy search enabled
		boolean igSearchEnabled = getButton(Option.UsePath).getSelection(); 
		setChildrenEnabled(assignmentPanel, igSearchEnabled);
		
		// FollowAssignments -> Depth Text and scope box shape
		fDepthText.setEnabled(isFollowAssignments());
		
		//boolean ba = getButton(Option.AssignThrough).getSelection();
		if (before != isFollowAssignments()) { // buttons must be radio when assigns are followed
			if (isFollowAssignments()) { // we must not allow both radio buttons to have the same values
				Button b1 = getButton(Option.ReadsOnly);
				Button b2 = getButton(Option.WritesOnly);
				if (b1.getSelection() == b2.getSelection()) 
					b2.setSelection(!b1.getSelection());
			}
			swapButtons(Option.WritesOnly, 0);
			swapButtons(Option.ReadsOnly, 1);
			pathPanel.layout();
		}

		// Depth text -> Search button
		fSearchButton.setEnabled(true);
		if (fDepthText.isEnabled())
			try {
				String text = fDepthText.getText().trim();
				fDepth = text.length() == 0 ? -1 : Integer.parseInt(fDepthText.getText());
			} catch (NumberFormatException ex) {
				fSearchButton.setEnabled(false);
			}
		
	}
	
	/**Does Eclipse's homework, who does not gray out children when parent's Composite is disabled.*/
	void setChildrenEnabled(Control c, boolean enabled) {
		c.setEnabled(enabled);
		if (c instanceof Composite)
			for (Control ch : ((Composite)c).getChildren()) {
				setChildrenEnabled(ch, enabled);
		}
	}
	
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		
	}
	
}
