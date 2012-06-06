package org.zamia.plugin.editors;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.zamia.util.Pair;

/**
 * @author Anton Chepurov
 */
public class ShowSelectedConditionsAction implements IEditorActionDelegate {

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

		ITextSelection selection = (ITextSelection) fEditor.getSelectionProvider().getSelection();

		if (selection.isEmpty()) {
			return;
		}

		int start = selection.getStartLine();

		int end = selection.getEndLine();

		DebugReportVisualizer.getInstance(fEditor.getZPrj()).highlightConditions(fEditor, new Pair<Integer, Integer>(start, end));
	}

	@Override
	public void selectionChanged(IAction iAction, ISelection iSelection) {
		iAction.setEnabled(true);
	}
}
