package org.zamia.plugin.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.python.core.PyObject;
import org.zamia.ExceptionLogger;
import org.zamia.ZamiaProject;
import org.zamia.cli.jython.ZCJInterpreter;
import org.zamia.instgraph.interpreter.logger.Report;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.ZamiaProjectMap;
import org.zamia.plugin.editors.DebugReportVisualizer;

/**
 * @author Anton Chepurov
 */
public class ScriptRunner extends AbstractHandler {

	public final static ExceptionLogger el = ExceptionLogger.getInstance();

	@Override
	public Object execute(ExecutionEvent executionEvent) throws ExecutionException {

		ITreeSelection v = (ITreeSelection) HandlerUtil.getVariable(executionEvent, "selection");

		IProject project = (IProject) v.getFirstElement();

		ZamiaProject zprj = ZamiaProjectMap.getZamiaProject(project);

		doScript(zprj);

		return null;
	}

	private void doScript(ZamiaProject aZprj) {

		Shell shell = ZamiaPlugin.getShell();

		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setText("Select script file");
		dialog.setFilterPath(aZprj.fBasePath.toString());
		dialog.setFilterExtensions(new String[]{"*.py"});
		String selected = dialog.open();

		if (selected == null) {
			return;
		}

		ZamiaPlugin.showConsole();

		ZCJInterpreter interpreter = aZprj.getZCJ();
		if (interpreter == null) {
			aZprj.initJythonInterpreter();
			interpreter = aZprj.getZCJ();
			if (interpreter == null) {
				MessageBox msg = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
				msg.setText("Script execution failure");
				msg.setMessage("Could not start Jython interpreter.\n    See log file for details.");
				msg.open();
				return;
			}
		}

		ScriptJob job = new ScriptJob(interpreter, selected);
		job.setPriority(Job.LONG);
		job.schedule();
	}

	private class ScriptJob extends Job {

		private final ZCJInterpreter fInterpreter;
		private final String fScriptFile;

		public ScriptJob(ZCJInterpreter aInterpreter, String aScriptFile) {
			super("Script execution...");
			fInterpreter = aInterpreter;
			fScriptFile = aScriptFile;
			addJobChangeListener(REPORT_VISUALIZER);
		}

		@Override
		protected IStatus run(IProgressMonitor iProgressMonitor) {
			try {
				fInterpreter.evalFile(fScriptFile);
			} catch (Throwable e) {
				el.logException(e);
			}
			return Status.OK_STATUS;
		}
	}

	private static final JobChangeAdapter REPORT_VISUALIZER = new JobChangeAdapter() {
		@Override
		public void done(final IJobChangeEvent event) {

			Display.getDefault().asyncExec(new Runnable() {
				public void run() {

					ScriptJob script = (ScriptJob) event.getJob();

					ZamiaProject zprj = script.fInterpreter.getZprj();

					PyObject reportObject = script.fInterpreter.getObject("reportAssignments");
					if (reportObject != null) {

						Report report = (Report) reportObject.__tojava__(Report.class);

						DebugReportVisualizer.getInstance(zprj).setAssignments(report);
					}

					reportObject = script.fInterpreter.getObject("reportConditions");
					if (reportObject != null) {

						Report report = (Report) reportObject.__tojava__(Report.class);

						DebugReportVisualizer.getInstance(zprj).setConditions(report);
					}

//					reportObject = script.fInterpreter.getObject("reportBranches");
				}
			});
		}
	};
}
