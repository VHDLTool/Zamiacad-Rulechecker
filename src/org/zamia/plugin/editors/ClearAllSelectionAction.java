package org.zamia.plugin.editors;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

/**
 * @author Anton Chepurov
 */
public class ClearAllSelectionAction implements IEditorActionDelegate {

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
		DebugReportVisualizer.getInstance(fEditor.getZPrj()).clearAllSelection();
	}

	@Override
	public void selectionChanged(IAction iAction, ISelection iSelection) {
		iAction.setEnabled(true);
	}
}
