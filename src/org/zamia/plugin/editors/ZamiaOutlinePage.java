/*
 * Copyright 2007,2009 by the authors indicated in the @author tags.
 * All rights reserved.
 *
 * See the LICENSE file for details.
 *
*/

package org.zamia.plugin.editors;

import java.util.function.Function;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.OpenAndLinkWithEditorHelper;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.zamia.ASTNode;
import org.zamia.ExceptionLogger;
import org.zamia.IDesignModule;
import org.zamia.SourceLocation;
import org.zamia.ZamiaLogger;
import org.zamia.ZamiaProject;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.ZamiaProjectMap;
import org.zamia.vhdl.ast.VHDLNode; // shouldn't it be ASTNode?


/**
 * Content outline page for the VHDL editor.
 * 
 * @author Guenter Bartsch
 */

public class ZamiaOutlinePage extends ContentOutlinePage {

	public final static ZamiaLogger logger = ZamiaLogger.getInstance();

	public final static ExceptionLogger el = ExceptionLogger.getInstance();

	private ZamiaEditor fEditor;

	private Object fInput;

	private ZamiaOutlineLabelProvider fLabelProvider;

	private boolean fIsDisposed;

	private OutlineSearchDialog fSearchDlg;

	private TreeViewer fTreeViewer;

	public ZamiaOutlinePage(ZamiaEditor aEditor) {
		fEditor = aEditor;
		fIsDisposed = true;
	}

	public class OutlineSearchAction extends org.eclipse.jface.action.Action {

		private final ZamiaOutlineContentProvider fContentProvider;

		public OutlineSearchAction(final ZamiaOutlineContentProvider aContentProvider) {
			super("org.zamia.actions.OutlineSearchAction", AS_PUSH_BUTTON);
			fContentProvider = aContentProvider;
			final ImageDescriptor desc = AbstractUIPlugin.imageDescriptorFromPlugin(ZamiaPlugin.PLUGIN_ID, "share/images/search.gif");
			setImageDescriptor(desc);
			setToolTipText("Search...");
		}

		@Override
		public void run() {

			logger.info("ZamiaOutlinePage: Search.");

			fSearchDlg.connect(fContentProvider);

			fSearchDlg.open();
			Object sel[] = fSearchDlg.getResult();
			if (sel == null)
				return;
			for (int i = 0; i < sel.length; i++) {
				if (sel[i] instanceof StructuredSelection) {
					StructuredSelection s = (StructuredSelection) sel[i];

					try {

						// FIXME: doesn't work (tree needs to be expanded)
						//fTreeViewer.setSelection(s, true);

						int n = s.size();
						if (n < 1) {
							continue;
						}

						Object o = s.toArray()[n - 1];
						if (o instanceof VHDLNode) {

							VHDLNode ast = (VHDLNode) o;

							ZamiaReconcilingStrategy strategy = fEditor.getReconcilingStrategy();

							ZamiaProject zprj = strategy.getZPrj();

							ZamiaPlugin.showSource(getSite().getPage(), ZamiaProjectMap.getProject(zprj), ast.getLocation(), 0);
						}

					} catch (Throwable e1) {
						el.logException(e1);
					}
				}
			}

			fSearchDlg.disconnect();
		}
	}

	public void createControl(Composite aParent) {
		super.createControl(aParent);

		fLabelProvider = new ZamiaOutlineLabelProvider(aParent);

		fSearchDlg = new OutlineSearchDialog(getSite().getShell());
		fSearchDlg.setTitle("Outline Search");

		fTreeViewer = getTreeViewer();

		ZamiaOutlineContentProvider contentProvider = new ZamiaOutlineContentProvider(fEditor);

		fTreeViewer.setContentProvider(contentProvider);
		fTreeViewer.setLabelProvider(fLabelProvider);
		fTreeViewer.addSelectionChangedListener(this);
		fTreeViewer.setAutoExpandLevel(2);

		// Adds button to viewer's toolbar
		final IToolBarManager mgr = getSite().getActionBars().getToolBarManager();
		mgr.add(new OutlineSearchAction(contentProvider));

		if (fInput != null) {
			fTreeViewer.setInput(fInput);
		}

		fIsDisposed = false;

		update();
		
		new OpenAndLinkWithEditorHelper(fTreeViewer) {
			
			{setLinkWithEditor(true);}
			
			@Override protected void activate(ISelection selection) {
				open(selection, true);
			}

			@Override protected void linkToEditor(ISelection selection) {
				//fEditor.doSelectionChanged(selection);
				fEditor.outlineSelectionChanged(selection);
			}

			@Override protected void open(ISelection selection, boolean activate) {
				linkToEditor(selection);
				if (activate) getSite().getPage().activate(fEditor);
			}

		};

	}

	public void setInput(Object aInput) {
		fInput = aInput;
		update();
	}

	public void update() {
		TreeViewer viewer = getTreeViewer();

		if (viewer != null) {
			Control control = viewer.getControl();

			if ((control != null) && !control.isDisposed()) {
				viewer.removeSelectionChangedListener(this);
				control.setRedraw(false);
				viewer.setInput(fInput);

				// viewer.expandAll();
				control.setRedraw(true);
				// selectNode(fEditor.getCursorLine(), true);
				viewer.addSelectionChangedListener(this);
			}
		}
	}

	public boolean isDisposed() {
		return fIsDisposed;
	}

	public void select(SourceLocation target) {
		ZamiaOutlineContentProvider cp = (ZamiaOutlineContentProvider) fTreeViewer.getContentProvider();
		
		
		IDesignModule[] modules = fEditor.getReconcilingStrategy().getRootElements();
		
		if (modules.length == 0) return;
		Object sm = null; for (int i = 0 ; i != modules.length; i++) 
			if (modules[i].getLocation().compareTo(target) <= 0) sm = modules[i];
		
		class Helper {void selectLastChild(Object parent) {
			Object lastChild = null; Object[] elements = cp.getChildren(parent)  ;
			if (elements != null) for (int i = 0 ; i != elements.length; i++)
				if (elements[i] instanceof ASTNode) {
					ASTNode astEl = (ASTNode) elements[i];
					if (astEl.getLocation().compareTo(target) <= 0) lastChild = astEl;
				};
			
			if (lastChild == null) fTreeViewer.setSelection(new StructuredSelection(parent), true);
			else selectLastChild(lastChild);

		}}
		new Helper().selectLastChild(sm);
	}
	
}
