/*
 * Copyright 2005-2009 by the authors indicated in the @author tags.
 * All rights reserved.
 *
 * See the LICENSE file for details.
 *
*/

package org.zamia.plugin.ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.wizards.newresource.ResourceMessages;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.zamia.ExceptionLogger;
import org.zamia.FSCache;
import org.zamia.ZamiaLogger;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.build.ZamiaBuilder;
import org.zamia.plugin.build.ZamiaNature;


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
		
		public static QualifiedName BP_CONTENT_DISABLED_QN = new QualifiedName(ZamiaPlugin.PLUGIN_ID, "default bp content disabled");
		
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
	
	TopLevelWizardPage topLevelPage;

	String[] topEntity = new String[1];
	public void addPages() {
		super.addPages();
		//namePage = new NewZamiaProjectWizardPage(); // Gunter, why do we need a special wizard?
		namePage = new WizardNewProjectCreationPage("New Zamia Project Page Name");//
		namePage.setTitle("Create New zamiaCAD Project"); //
		namePage.setDescription("Select the project name, saving directory and its toplevel entity");
		addPage(namePage);
		addPage(topLevelPage = new TopLevelWizardPage());
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
			final boolean bpGreenStuff = topLevelPage.fuseDefaultsButton.getSelection();
			
			WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
				protected void execute(IProgressMonitor monitor) {

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

						try {
							IFile file = project.getFile("BuildPath.txt");
							if (file.exists()) {
								// rewriting file, especially linked one can be dangerous
								//file.setContents(getInitialBuildPathContents(), true, false, null);
								logger.warn("Failed to create build path because file " + file.getLocation() + " already exists");
							} else {
								file.create(new ByteArrayInputStream(new byte[0]), false, null);
								
								if (bpGreenStuff)
									file.appendContents(getInitialBuildPathContents(), IResource.NONE, null);
									
								ResourcesPlugin.getWorkspace().getRoot().setPersistentProperty(
										topLevelPage.BP_CONTENT_DISABLED_QN, bpGreenStuff ? null : "disabled");
								
								if (topLevel.length() != 0)
									file.appendContents(new ByteArrayInputStream(("toplevel " + topLevel.
											toUpperCase()).getBytes()), IResource.NONE, null);
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
		return FSCache.getInstance().getClass().getResourceAsStream("/templates/BuildPath.txt");
	}

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		configurationElement = config;
	}
}