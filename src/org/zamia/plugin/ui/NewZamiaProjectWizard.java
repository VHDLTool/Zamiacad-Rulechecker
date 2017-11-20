/*
 * Copyright 2005-2009 by the authors indicated in the @author tags.
 * All rights reserved.
 *
 * See the LICENSE file for details.
 *
*/

package org.zamia.plugin.ui;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.zamia.ExceptionLogger;
import org.zamia.FSCache;
import org.zamia.ZamiaLogger;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.build.ZamiaBuilder;
import org.zamia.plugin.build.ZamiaNature;
import org.zamia.util.Native;

/**
 * 
 * @author Guenter Bartsch
 *
 */
public class NewZamiaProjectWizard extends BasicNewResourceWizard implements IExecutableExtension {

	protected final static ZamiaLogger logger = ZamiaLogger.getInstance();

	protected final static ExceptionLogger el = ExceptionLogger.getInstance();

	private WizardNewProjectCreationPage namePage;

	private IProject project;

	private IConfigurationElement configurationElement;

	public NewZamiaProjectWizard() {
		super();
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setNeedsProgressMonitor(true);
	}

	static class TopLevelWizardPage extends WizardPage {
		
		public Text fProjectText;
		public Button fuseDefaultsButton; 
		
		TopLevelWizardPage() {
			super("do you see this title?", "Toplevel Entity", null);
			setDescription("You may leave this value unspecified or use BuildPath.txt to specify it later");
		}
		
		public QualifiedName BP_CONTENT_DISABLED_QN = new QualifiedName(ZamiaPlugin.PLUGIN_ID, "default bp content disabled");
		
		public void createControl(Composite parent) {
			Composite container = new Composite(parent, SWT.NULL);
			GridLayout layout = new GridLayout();
			container.setLayout(layout);
			setControl(container);
	
			Label label = new Label(container, SWT.NULL); label.setText("&Toplevel Entity:");
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			(fProjectText = new Text(container, SWT.BORDER | SWT.SINGLE)).setLayoutData(gd);
			fuseDefaultsButton = new Button(container, SWT.CHECK | SWT.RIGHT);
			fuseDefaultsButton.setText("Add sample entries to BuildPath.txt");
			try {
				fuseDefaultsButton.setSelection(ResourcesPlugin.getWorkspace().getRoot().getPersistentProperty(
						BP_CONTENT_DISABLED_QN) == null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	static class HandbookWizardPage extends WizardPage {
		
		public FileChooser fileChooser;
		public FileChooser directoryChooser;
		public Text listHandbook;
		
		HandbookWizardPage() {
			super("do you see this title?", "Select handbook rulesets", null);
			setDescription("You may leave this value unspecified or use rc_config.xml to specify it later");
		}
		
		public static QualifiedName BP_CONTENT_DISABLED_QN = new QualifiedName(ZamiaPlugin.PLUGIN_ID, "default bp content disabled");
		
		public void createControl(Composite parent) {
			Composite container = new Composite(parent, SWT.NULL);
			GridLayout layout = new GridLayout();
			container.setLayout(layout);
			setControl(container);
	
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			
			Label ldirectory = new Label(container, SWT.WRAP);
			ldirectory.setText("Select handbook folder");
			// setup bold font
			Font boldFont = JFaceResources.getFontRegistry().getBold(
					JFaceResources.DEFAULT_FONT);
			ldirectory.setFont((org.eclipse.swt.graphics.Font) boldFont);
	 
			directoryChooser = new FileChooser(container, false);
			
			gd.heightHint = 25;
			directoryChooser.setLayoutData(gd);

			Label l = new Label(container, SWT.WRAP);
			l.setText("Handbook file:");
			// setup bold font
			l.setFont((org.eclipse.swt.graphics.Font) boldFont);
			fileChooser = new FileChooser(container, true);
			directoryChooser.setFileChooser(fileChooser);
			gd.heightHint = 25;
			fileChooser.setLayoutData(gd);
			
			final Button addButton = new Button(container, SWT.PUSH | SWT.RIGHT);
			addButton.setText("Add new handbook");
			addButton.setEnabled(false);
			fileChooser.setAddButton(addButton);
			listHandbook = new Text(container, SWT.SINGLE | SWT.BORDER);
			listHandbook.setText("");
			listHandbook.setSize(200, 200);
			listHandbook.setEditable(false);
			GridData gridData = new GridData(GridData.FILL_BOTH);
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalAlignment = GridData.FILL;
			listHandbook.setLayoutData(gridData);

			addButton.addSelectionListener(new SelectionListener() {
 

			public void widgetDefaultSelected(SelectionEvent e) {
			}
 
			public void widgetSelected(SelectionEvent e) {
				if (!listHandbook.getText().contains(fileChooser.getText())) {
					listHandbook.setText(listHandbook.getText()+ (listHandbook.getText().length()!=0 ? ";":"") +fileChooser.getText());
				}
				fileChooser.clearText();
				addButton.setEnabled(false);
			}
		});
		}		
	}
	
//	static class LogDirectoryWizardPage extends WizardPage {
//		
//		public FileChooser fileChooser;
//		
//		LogDirectoryWizardPage() {
//			super("do you see this title?", "Configure log directory", null);
//			setDescription("You may leave this value unspecified or use rc_config.xml to specify it later");
//		}
//		
//		public static QualifiedName BP_CONTENT_DISABLED_QN = new QualifiedName(ZamiaPlugin.PLUGIN_ID, "default bp content disabled");
//		
//		public void createControl(Composite parent) {
//			Composite container = new Composite(parent, SWT.NULL);
//			GridLayout layout = new GridLayout();
//			container.setLayout(layout);
//			setControl(container);
//	
//			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
//			
//			Label l = new Label(container, SWT.WRAP);
//			l.setText("root Log:");
//			// setup bold font
//			Font boldFont = JFaceResources.getFontRegistry().getBold(
//					JFaceResources.DEFAULT_FONT);
//			l.setFont((org.eclipse.swt.graphics.Font) boldFont);
//	 
//			fileChooser = new FileChooser(container, false);
//			
//			gd.heightHint = 25;
//			fileChooser.setLayoutData(gd);
//		}		
//	}
	
	TopLevelWizardPage topLevelPage;

	HandbookWizardPage handbookPage;

//	LogDirectoryWizardPage logDirectoryPage;

	String[] topEntity = new String[1];
	public void addPages() {
		super.addPages();
		//namePage = new NewZamiaProjectWizardPage(); // Gunter, why do we need a special wizard?
		namePage = new WizardNewProjectCreationPage("New Zamia Project Page Name");//
		namePage.setTitle("Create New zamiaCAD Project"); //
		namePage.setDescription("Select the project name, saving directory and its toplevel entity");
		addPage(namePage);
		addPage(topLevelPage = new TopLevelWizardPage());
		addPage(handbookPage = new HandbookWizardPage());
//		addPage(logDirectoryPage = new LogDirectoryWizardPage());
	}

	public boolean performFinish() {

		//valtih: accessing page methods from another thread throws "invalid Display thread" error.  
		// Might be, it is the reason why gunter decided to create his one replacement for the page? 
		// I do it simpler: copy name and location in constants. 
		// This way we avoid code duplication and enable custom project location.
		try {
			final String name = namePage.getProjectName();
			final IPath location = namePage.getLocationPath();
			final String topLevel = topLevelPage.fProjectText.getText();
			final String handbook = handbookPage.fileChooser.getText();
			final String handbookList = handbookPage.listHandbook.getText();
			final String rootHandbook = handbookPage.directoryChooser.getText();
//			final String logDirectory = logDirectoryPage.fileChooser.getText();
			final boolean bpGreenStuff = topLevelPage.fuseDefaultsButton.getSelection();
			
			WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
				protected void execute(IProgressMonitor monitor) {
					String rootHandbookStr = rootHandbook;
					
					monitor.beginTask("Project is being created ...", 50);

					// create project
					try {
						IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
						monitor.subTask("Directory is being created");
						project = root.getProject(name);
						IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(project.getName());
						if(!Platform.getLocation().equals(location))
							description.setLocation(location);
						description.setNatureIds(new String[] { ZamiaNature.NATURE_ID });
						ICommand command = description.newCommand();
						command.setBuilderName(ZamiaBuilder.BUILDER_ID);
						description.setBuildSpec(new ICommand[] { command });
						project.create(description, monitor);
						monitor.worked(10);
						project.open(monitor);
						addZamiaNature(project, new SubProgressMonitor(monitor, 10));

						//Compute relative rootHandbook filepath if possible (need same drive on windows)
						try {
							Path rootHandbookPath = Paths.get(rootHandbook);
							Path rootProjectPath = Paths.get(project.getLocationURI());
							rootHandbookStr = (rootProjectPath.relativize(rootHandbookPath)).toString();
							rootHandbookStr = rootHandbookStr.replace("\\", "/");
						} catch (Exception e) {
							//Here if drive diff on windows platform -> keep absolute path
						}						
						
						try {
							IFile file = project.getFile("BuildPath.txt");
							if (file.exists()) {
								// rewriting file, especially linked one can be dangerous
								//file.setContents(getInitialBuildPathContents(), true, false, null);
								logger.warn("Failed to create build path because file " + file.getLocation() + " already exists");
							} else {
								file.create(new ByteArrayInputStream(new byte[0]), false, null);
								
								if (bpGreenStuff) {
									file.appendContents(getInitialBuildPathContents(), IResource.NONE, null);
								}
								ResourcesPlugin.getWorkspace().getRoot().setPersistentProperty(
										topLevelPage.BP_CONTENT_DISABLED_QN, bpGreenStuff ? null : "disabled");
								
								if (topLevel.length() != 0)
									file.appendContents(new ByteArrayInputStream(("toplevel " + topLevel.
											toUpperCase()).getBytes()), IResource.NONE, null);
							}
						} catch (Throwable t) {
							el.logException(t);
						}
						
						//create directory rule_checker
						try {
							IFolder folder = project.getFolder("rule_checker");
							if (!folder.exists()) {
								// create directory
								folder.create(false, false, null);
							}
						} catch (Throwable t) {
							el.logException(t);
						}
						
						// create rc_config.txt
						try {
							IFile file = project.getFile("rule_checker"+"/"+"rc_config.txt");
							if (file.exists()) {
								// rewriting file, especially linked one can be dangerous
								//file.setContents(getInitialBuildPathContents(), true, false, null);
								logger.warn("Failed to create build path because file " + file.getLocation() + " already exists");
							} else {
								file.create(new ByteArrayInputStream(new byte[0]), false, null);
								
								file.appendContents(getInitialConfigFileContents(), IResource.NONE, null);
									
							}
							
						} catch (Throwable t) {
							el.logException(t);
						}

						// create rc_config.xml
						try {
							IFile file = project.getFile("rule_checker"+"/"+"rc_config.xml");
							if (file.exists()) {
								// rewriting file, especially linked one can be dangerous
								//file.setContents(getInitialBuildPathContents(), true, false, null);
								logger.warn("Failed to create build path because file " + file.getLocation() + " already exists");
							} else {
								file.create(new ByteArrayInputStream(new byte[0]), false, null);
								if ((handbook.length()+handbookList.length()) != 0 || rootHandbook.length() != 0) {
									file.appendContents(getInitialRCConfigFileHeaderContents(), IResource.NONE, null);
									file.appendContents(new ByteArrayInputStream(("\n \t<!-- \n \t<root_directory>").getBytes()), IResource.NONE, null);	
									file.appendContents(new ByteArrayInputStream(("\n \t \t <alias>LOG_ROOT</alias>").getBytes()), IResource.NONE, null);
									file.appendContents(new ByteArrayInputStream(("\n \t \t <path>C:/dev/FPGA/project/log</path>").getBytes()), IResource.NONE, null);// TODO BGT chemin
									file.appendContents(new ByteArrayInputStream(("\n \t</root_directory> \n \t-->\n").getBytes()), IResource.NONE, null);	
									if (rootHandbook.length() != 0) {
										file.appendContents(new ByteArrayInputStream(("\n \t<root_directory>").getBytes()), IResource.NONE, null);	
										file.appendContents(new ByteArrayInputStream(("\n \t \t <alias>HANDBOOK_ROOT</alias>").getBytes()), IResource.NONE, null);
										file.appendContents(new ByteArrayInputStream(("\n \t \t <path>"+rootHandbookStr+"</path>").getBytes()), IResource.NONE, null);
										file.appendContents(new ByteArrayInputStream(("\n \t</root_directory>\n").getBytes()), IResource.NONE, null);	
									} else {
										file.appendContents(new ByteArrayInputStream(("\n \t<!-- \n \t<root_directory>").getBytes()), IResource.NONE, null);	
										file.appendContents(new ByteArrayInputStream(("\n \t \t <alias>HANDBOOK_ROOT</alias>").getBytes()), IResource.NONE, null);
										file.appendContents(new ByteArrayInputStream(("\n \t \t <path>C:/handbook</path>").getBytes()), IResource.NONE, null);
										file.appendContents(new ByteArrayInputStream(("\n \t</root_directory> \n \t-->\n").getBytes()), IResource.NONE, null);	
									}
									if ((handbook.length()+handbookList.length()) != 0) {
										String handbookResult = handbookList.contains(handbook) ? handbookList : (handbook +";" + handbookList);
										List<String> hanbookList = Arrays.asList(handbookResult.split(";"));
										file.appendContents(new ByteArrayInputStream(("\n \t<handBook>\n").getBytes()), IResource.NONE, null);	
										for (String handbookItem : hanbookList) {
											if (handbookItem.length() != 0) {
												if (rootHandbook.length() != 0) {
													if (handbookItem.startsWith(rootHandbook)) {
														file.appendContents(new ByteArrayInputStream((" \t\t<handBook_fileName>$HANDBOOK_ROOT"+handbookItem.substring(rootHandbook.length())+"</handBook_fileName>\n").getBytes()), IResource.NONE, null);
													} else {
														file.appendContents(new ByteArrayInputStream((" \t\t<handBook_fileName>"+handbookItem+"</handBook_fileName>\n").getBytes()), IResource.NONE, null);
													}
												} else {
													file.appendContents(new ByteArrayInputStream((" \t\t<handBook_fileName>"+handbookItem+"</handBook_fileName>\n").getBytes()), IResource.NONE, null);
												}
											}
										}
										file.appendContents(new ByteArrayInputStream((" \t</handBook>\n\n").getBytes()), IResource.NONE, null);	
									} else {
										file.appendContents(new ByteArrayInputStream(("\n \t<!-- \n \t<handBook>\n \t\t<handBook_fileName>").getBytes()), IResource.NONE, null);											
										file.appendContents(new ByteArrayInputStream(("C:/Projets/Handbook/handbook_CNE.xml").getBytes()), IResource.NONE, null);
										file.appendContents(new ByteArrayInputStream(("</handBook_fileName>\n \t</handBook> \n \t-->\n\n").getBytes()), IResource.NONE, null);	
									}
									file.appendContents(getInitialRCConfigFileFooterContents(), IResource.NONE, null);
									
									
								} else {
									file.appendContents(getInitialRCConfigFileContents(), IResource.NONE, null);
								}
									
							}
							
						} catch (Throwable t) {
							el.logException(t);
						}

						// create rc_handbook_parameters.xml
						try {
								IFile file = project.getFile("rule_checker"+"/"+"rc_handbook_parameters.xml");
								if (file.exists()) {
									// rewriting file, especially linked one can be dangerous
									//file.setContents(getInitialBuildPathContents(), true, false, null);
									logger.warn("Failed to create build path because file " + file.getLocation() + " already exists");
								} else {
									file.create(new ByteArrayInputStream(new byte[0]), false, null);
									
									file.appendContents(getInitialRCHandbookParametersFileContents(), IResource.NONE, null);
								}
							
						} catch (Throwable t) {
							el.logException(t);
						}

					} catch (CoreException x) {

					} finally {
						monitor.done();
					}
				}
			};
			getContainer().run(true, false, op);

			BasicNewProjectResourceWizard.updatePerspective(configurationElement);
			//selectAndReveal(project);
		} catch (InvocationTargetException x) {
			return false;
		} catch (InterruptedException x) {
			return false;
		}

		return true;
	}

	public boolean performCancel() {
		return true;
	}

	public static void addZamiaNature(IProject project, IProgressMonitor monitor) throws CoreException {
		if (!project.hasNature(ZamiaNature.NATURE_ID)) {
			IProjectDescription description = project.getDescription();
			String[] prevNatures = description.getNatureIds();
			String[] newNatures = new String[prevNatures.length + 1];
			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
			newNatures[prevNatures.length] = ZamiaNature.NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, monitor);
		}
	}

	private InputStream getInitialBuildPathContents() {
		return FSCache.getInstance().getClass().getResourceAsStream("./templates/BuildPath.txt");
	}

	private InputStream getInitialConfigFileContents() {
		return this.getClass().getResourceAsStream("/templates/rc_config.txt");
	}

	private InputStream getInitialRCConfigFileContents() {
		return this.getClass().getResourceAsStream("/templates/rc_config.xml");
	}

	private InputStream getInitialRCConfigFileHeaderContents() {
		return this.getClass().getResourceAsStream("/templates/rc_config_header.txt");
	}

	private InputStream getInitialRCConfigFileFooterContents() {
		return this.getClass().getResourceAsStream("/templates/rc_config_footer.txt");
	}

	private InputStream getInitialRCHandbookParametersFileContents() {
		return this.getClass().getResourceAsStream("/templates/rc_handbook_parameters.xml");
	}

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		configurationElement = config;
	}

}
