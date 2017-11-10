package org.zamia.plugin.ui;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
 
public class FileChooser extends Composite {
 
	Text mText;
	Button mButton;
	String title = null;
	boolean xmlSelect;
	Button addButton;
	FileChooser fileChooser;
	private String path = "";
 
	public FileChooser(Composite parent, boolean xmlSelect) {
		super(parent, SWT.NULL);
		this.xmlSelect = xmlSelect;
		createContent();
	}

	public void createContent() {
		GridLayout layout = new GridLayout(2, false);
		setLayout(layout);
 
		mText = new Text(this, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		mText.setLayoutData(gd);
 
 
		mButton = new Button(this, SWT.NONE);
		mButton.setText("...");
		mButton.addSelectionListener(new SelectionListener() {
 

			

			public void widgetDefaultSelected(SelectionEvent e) {
			}
 
			public void widgetSelected(SelectionEvent e) {
				if (xmlSelect) {
					FileDialog fdlg = new FileDialog(mButton.getShell(),  SWT.OPEN  );
					fdlg.setFilterExtensions(new String [] {"*.xml"});
					fdlg.setFilterPath(path);
					fdlg.setText("Open");
					String path = fdlg.open();
					if (path == null) return;
					mText.setText(path.replace("\\", "/"));
					addButton.setEnabled(true);
				} else {
					DirectoryDialog dlg = new DirectoryDialog(mButton.getShell(),  SWT.OPEN  );
					dlg.setText("Open");
					String path = dlg.open();
					if (path == null) return;
					mText.setText(path.replace("\\", "/"));
					if (fileChooser!= null) {
						fileChooser.setFilterPath(path);
					}
				}
			}
		});
	}
 
	protected void setFilterPath(String path) {
		this.path  = path;
		
	}

	public String getText() {
		return mText.getText();
	}
 
	public void clearText() {
		mText.setText("");
	}
 
	public Text getTextControl() {
		return mText;		
	}
 
	public File getFile() {
		String text = mText.getText();
		if (text.length() == 0) return null;
		return new File(text);
	}
 
	public String getTitle() {
		return title;
	}
 
	public void setTitle(String title) {
		this.title = title;
	}

	public void setAddButton(Button addButton) {
		this.addButton = addButton;
		
	}

	public void setFileChooser(FileChooser fileChooser) {
		this.fileChooser = fileChooser;
		
	}
}