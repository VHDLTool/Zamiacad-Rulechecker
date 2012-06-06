/*
 * Copyright 2007-2009 by the authors indicated in the @author tags.
 * All rights reserved.
 *
 * See the LICENSE file for details.
 *
 */

package org.zamia.plugin.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.zamia.ExceptionLogger;
import org.zamia.SourceLocation;
import org.zamia.ToplevelPath;
import org.zamia.ZamiaLogger;
import org.zamia.ZamiaProject;
import org.zamia.analysis.SourceLocation2IG;
import org.zamia.analysis.ig.IGReferencesSearch;
import org.zamia.analysis.ig.IGReferencesSearch.SearchJob;
import org.zamia.instgraph.IGItem;
import org.zamia.instgraph.IGObject;
import org.zamia.instgraph.IGObject.IGObjectCat;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.views.sim.SimulatorView;
import org.zamia.util.Pair;


/**
 * 
 * @author Guenter Bartsch
 * 
 */

public class ShowInSimAction extends OpenDeclarationAction {

	public static final ZamiaLogger logger = ZamiaLogger.getInstance();

	public static final ExceptionLogger el = ExceptionLogger.getInstance();

	static ZamiaEditor fEditor;

	static Shell fShell;

	static Display fDisplay;

	class ShowInSimJob extends Job {

		private final ToplevelPath fPath;

		public ShowInSimJob() {
			super("Show in Simulator View");
			fPath = getPath();
		}

		protected IStatus run(IProgressMonitor monitor) {
			try {

				if (fView.getSim() == null) {
					ZamiaPlugin.showError(fShell, "Should I initialize the simulator for you?", "Initialize simulator first", "Simulator not initialized");
					return Status.CANCEL_STATUS;
				}
				
				if (fPath == null) {
					ZamiaPlugin.showError(fShell, "No Path", "No path was given.", "Editor location field empty?");
				}

				ZamiaProject proj = getZamiaProject();
				
				SourceLocation location = getLocation();

				Pair<IGItem, ToplevelPath> nearest = SourceLocation2IG.findNearestItem(location, fPath, getZamiaProject());

				if (nearest != null) {
					
					IGItem item = nearest.getFirst();
					ToplevelPath path = nearest.getSecond();

					logger.info("ShowInSimJob: nearest item: %s, path: %s", item, path);

					if (item != null) {
						
						IGObject obj = IGReferencesSearch.asObject(item);

						if (!obj.getCat().equals(IGObjectCat.SIGNAL)) {
							ZamiaPlugin.showError(fShell, "Not a signal.", "Cursor location mapped to \n\n" + obj + "\n\nWhich is " +obj.getCat()+ " rather than a signal.", "Not a signal.");
							return Status.CANCEL_STATUS;
						}
						
						// find local declaration, see if sim has a waveform for that
						SearchJob job = IGReferencesSearch.findLocalDeclarationScope(proj.getIGM(), obj, path);
						
						ToplevelPath signalPath = job.getPath().append(job.getObject().getId());
						
						if (fView.hasSignal(signalPath.getPath()))
							
							showInSim(signalPath);
						
						else // try global decl otherwise
							ZamiaPlugin.showError(fShell, "", "There was a code to handle the case where tracing local declarations fails but I have commented it out during optimization. Tell me when this happens. ",
									"I think it is bacause you've incrementally recompiled something and forget to restart the simulator.");

					} else {
						ZamiaPlugin.showError(fShell, "No Intermediate Object", "Current source location does not correspond to any intermediate object.",
								"IG Item search didn't return an item (path only).");
					}
				} else {
					ZamiaPlugin.showError(fShell, "No Intermediate Object", "Current source location does not correspond to any intermediate object.",
							"IG Item search failed.");
				}
			} catch (Throwable e) {
				ZamiaPlugin.showError(fShell, "Zamia Exception Caught", e.getMessage(), "");
				el.logException(e);
			}

			return Status.OK_STATUS;
		}

	}

	private IWorkbenchPage fPage;

	private SimulatorView fView;

	private ToplevelPath fSignalPath;

	public void setActiveEditor(IAction aAction, IEditorPart aTargetEditor) {
		if (aTargetEditor == null) {
			return;
		}
		if (!(aTargetEditor instanceof ZamiaEditor)) {
			return;
		}
		fEditor = (ZamiaEditor) aTargetEditor;
		fShell = fEditor.getSite().getShell();
		fDisplay = fShell.getDisplay();
	}

	public void selectionChanged(IAction aAction, ISelection aSelection) {
		aAction.setEnabled(true);
	}

	public void run(IAction aAction) {

		try {

			ToplevelPath path = fEditor.getPath();

			if (path == null) {
				ZamiaPlugin.showError(fEditor.getSite().getShell(), "No Path Information", "No path information found.", "Editor has no design path information.");
				return;
			}

			//fPath = path.getPath();

			processSelection();

			IWorkbenchWindow window = ZamiaPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();

			fPage = window.getActivePage();

			fView = (SimulatorView) fPage.showView("org.zamia.plugin.views.sim.SimulatorView");

			if (fView != null) {

				ShowInSimJob job = new ShowInSimJob();
				job.setPriority(Job.SHORT);
				job.schedule();
			}

		} catch (BadLocationException e) {
			el.logException(e);
		} catch (PartInitException e) {
			el.logException(e);
		}

	}

	private void showInSim(ToplevelPath aPath) {

		logger.info("ShowInSimAction: About to show %s", aPath);

		fSignalPath = aPath;

		fDisplay.asyncExec(new Runnable() {
			public void run() {

				fView.trace(fSignalPath.getPath());
			}
		});
	}

}
