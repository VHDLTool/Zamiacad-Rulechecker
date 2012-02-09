package org.zamia.plugin.editors;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.views.sim.SimulatorView;

/**
 * @author Anton Chepurov
 */
public class ReferenceSearchAction extends StaticAnalysisAction {

	private final SimulatorView fSimulatorView;

	private Shell fShell;

	public ReferenceSearchAction(SimulatorView aSimulatorView) {
		fSimulatorView = aSimulatorView;
	}

	@Override
	public void run(IAction iAction) {

		try {

			IWorkbenchWindow window = ZamiaPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
			if (window == null) {
				logger.error("ShowReferencesAction: Internal error: window == null.");
				return;
			}

			fShell = window.getShell();

			IEditorPart activeEditor = window.getActivePage().getActiveEditor();
			setActiveEditor(null, activeEditor);

			processSelection();

			if (fLocation == null) {
				showError("Failed to determine source location.");
				return;
			}


			ReferencesSearchQuery query = new ReferencesSearchQuery(this, true, true, false, true, false, false);

			NewSearchUI.addQueryListener(new ReferencesSearchQueryListener(query, fSimulatorView));

			NewSearchUI.runQueryInBackground(query);


		} catch (BadLocationException e) {
			el.logException(e);
			showError("Catched exception:\n" + e + "\nSee log for details.");
		}
	}

	private void showError(String aMsg) {
		logger.error("ShowReferencesAction: Error: %s", aMsg);
		ZamiaPlugin.showError(fShell, "ShowReferencesAction: Error", aMsg, "");
	}

}
