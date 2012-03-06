package org.zamia.plugin.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.zamia.ZamiaException;
import org.zamia.ZamiaLogger;
import org.zamia.cli.jython.ZCJInterpreter;
import org.zamia.plugin.ZamiaPlugin;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Anton Chepurov
 */
public class CompileWithGHDLAction implements IEditorActionDelegate {

	public static final ZamiaLogger logger = ZamiaLogger.getInstance();

	private static List<String> MARKED_EDITORS = new LinkedList<String>();

	private ZamiaEditor fEditor;

	@Override
	public void setActiveEditor(IAction iAction, IEditorPart iEditorPart) {
		if (iEditorPart == null) {
			return;
		}
		if (!(iEditorPart instanceof ZamiaEditor)) {
			return;
		}
		fEditor = (ZamiaEditor) iEditorPart;
	}

	@Override
	public void run(IAction iAction) {

		clean();

		IWorkbenchWindow window = ZamiaPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			logger.error("CompileWithGHDLAction: Internal error: window == null.");
			return;
		}

		IEditorPart activeEditor = window.getActivePage().getActiveEditor();
		setActiveEditor(null, activeEditor);

		ZCJInterpreter interpreter = getInterpreter();

		String fullPath = ((IFile) fEditor.getEditorInput().getAdapter(IFile.class)).getLocation().toOSString();
		File file = fEditor.getSourceFile().getFile();
		interpreter.setObject("sourceFile", file);
		interpreter.setObject("sourceFileFullPath", fullPath);

		try {
			interpreter.evalFile("builtin:/python/ghdl.py");
			MARKED_EDITORS.add(fullPath);
		} catch (ZamiaException e) {
			logger.error("CompileWithGHDLAction: Error during script execution: %s", e);
		}
	}

	private ZCJInterpreter getInterpreter() {
		return fEditor.getZPrj().getZCJ();
	}

	private void clean() {

		ZCJInterpreter interpreter = getInterpreter();
		for (String markedEditor : MARKED_EDITORS) {
			interpreter.eval("marker_clean(\"" + markedEditor + "\")");
		}
		MARKED_EDITORS = new LinkedList<String>();
	}

	@Override
	public void selectionChanged(IAction iAction, ISelection iSelection) {
		iAction.setEnabled(true);
	}
}
