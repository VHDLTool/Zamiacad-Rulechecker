/* 
 * Copyright 2008 by the authors indicated in the @author tags. 
 * All rights reserved. 
 * 
 * See the LICENSE file for details.
 * 
 * Created by Guenter Bartsch on Jun 22, 2008
 */
package org.zamia.plugin.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.internal.ui.viewsupport.ColoringLabelProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.search.ui.IContextMenuConstants;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.IWorkbenchHelpContextIds;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.zamia.SourceLocation;
import org.zamia.SourceRanges;
import org.zamia.Utils;
import org.zamia.ZamiaLogger;
import org.zamia.analysis.ReferenceSearchResult;
import org.zamia.analysis.ReferenceSite;
import org.zamia.analysis.ig.IGAssignmentsSearch.SearchAssignment;
import org.zamia.instgraph.IGObject;
import org.zamia.instgraph.IGObject.OIDir;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.ZamiaProjectMap;
import org.zamia.plugin.views.sim.SimulatorView;


/**
 * 
 * @author Guenter Bartsch
 * 
 */
public class ZamiaSearchResultPage extends AbstractTextSearchViewPage {

	public final static ZamiaLogger logger = ZamiaLogger.getInstance();

	static class ZamiaSearchTreeContentProvider implements ITreeContentProvider {

		private TreeViewer fTreeViewer;

		private AbstractTextSearchResult fSearchResult;

		@SuppressWarnings("unchecked")
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof AbstractTextSearchResult) {

				AbstractTextSearchResult atsr = (AbstractTextSearchResult) inputElement;

				Object[] elements = atsr.getElements();

				ArrayList res = new ArrayList();

				int n = elements.length;
				for (int i = 0; i < n; i++) {

					Object element = elements[i];

					if (element instanceof ReferenceSearchResult) {

						ReferenceSearchResult rss = (ReferenceSearchResult) element;
						if (rss.getParent() != null && rss.getParent().getParent() == null) {
							res.add(element);
						}
					}
				}
				return res.toArray();

			}
			return new Object[0];
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			fTreeViewer = (TreeViewer) viewer;
			fSearchResult = (AbstractTextSearchResult) newInput;
		}

		public void clear() {
			fTreeViewer.refresh();
		}

		public void elementsChanged(Object[] updatedElements) {

			fTreeViewer.refresh();
			fTreeViewer.expandAll();

			//			for (int i = 0; i < updatedElements.length; i++) {
			//				if (fSearchResult.getMatchCount(updatedElements[i]) > 0) {
			//					if (fTreeViewer.testFindItem(updatedElements[i]) != null)
			//						fTreeViewer.refresh(updatedElements[i]);
			//					else {
			//						fTableViewer.add(updatedElements[i]);
			//					}
			//				} else {
			//					fTreeViewer.remove(updatedElements[i]);
			//				}
			//			}
		}

		public Object[] getChildren(Object parentElement) {

			if (parentElement instanceof ReferenceSearchResult) {
				ReferenceSearchResult rss = (ReferenceSearchResult) parentElement;

				int n = rss.getNumChildren();
				Object res[] = new Object[n];
				for (int i = 0; i < n; i++) {
					res[i] = rss.getChild(i);
				}

				return res;

			}

			return null;
		}

		public Object getParent(Object element) {

			if (element instanceof ReferenceSite) {
				ReferenceSite rs = (ReferenceSite) element;
				return rs.getParent();
			} else if (element instanceof ReferenceSearchResult) {
				ReferenceSearchResult rss = (ReferenceSearchResult) element;
				return rss.getParent();
			}

			return null;
		}

		public boolean hasChildren(Object element) {
			if (element instanceof ReferenceSearchResult) {
				return ((ReferenceSearchResult) element).getNumChildren() > 0;
			}
			return false;
		}

	}

	private ZamiaSearchTreeContentProvider fContentProvider;

	public ZamiaSearchResultPage() {
		//		super(AbstractTextSearchViewPage.FLAG_LAYOUT_FLAT);
		super(AbstractTextSearchViewPage.FLAG_LAYOUT_TREE);
	}

	protected void elementsChanged(Object[] objects) {
		if (fContentProvider != null && fContentProvider.fSearchResult != null) {
			fContentProvider.elementsChanged(objects);
		}
	}

	protected void clear() {
		if (fContentProvider != null)
			fContentProvider.clear();
	}

	protected void configureTreeViewer(TreeViewer viewer) {
		viewer.setComparator(createViewerComparator());
        viewer.setLabelProvider(new ColoringLabelProvider(createLabelProvider()));
		fContentProvider = new ZamiaSearchTreeContentProvider();
		viewer.setContentProvider(fContentProvider);
		viewer.addSelectionChangedListener(backAction);
	}

    BackAction backAction = new BackAction();
    
	protected void fillToolbar(final IToolBarManager tbm) {
		tbm.appendToGroup(IContextMenuConstants.GROUP_SHOW, new Action("Highlight assignments", IAction.AS_CHECK_BOX) {
			
			public void run() {
				ZamiaSearchResult root = (ZamiaSearchResult) getViewer().getInput();
				
				SourceRanges sources = SourceRanges.createRanges();
				if (isChecked()) {
					logger.info("root = "  + root + " " + root.getElements());
					for (Object o : root.getElements())
						if (o instanceof SearchAssignment) {
							SearchAssignment a = (SearchAssignment) o;
							logger.info(" " + a);
							sources.add(a.getLocation(), 0);
						}
				} else 
					sources = null;
				
				ZamiaEditor.setStaticSources(sources);
				SimulatorView.highlightOpenEditors();
			}
			
		});
		tbm.appendToGroup(IContextMenuConstants.GROUP_SHOW, backAction);
		super.fillToolbar(tbm);
	}
	
	protected void configureTableViewer(TableViewer viewer) {
		logger.error("ZamiaSearchResultPage: TableView not supported.");
		//		viewer.setComparator(createViewerComparator());
		//		viewer.setLabelProvider(createLabelProvider());
		//		fContentProvider = new ContentProvider();
		//		viewer.setContentProvider(fContentProvider);
	}

	public void showMatch(Match match, int offset, int length, boolean activate) throws PartInitException {

		Object element = match.getElement();

		logger.debug("Element: " + element);
		if (element instanceof SearchAssignment) {
			SearchAssignment ref = (SearchAssignment) element;
			Match[] def = this.getDisplayedMatches(ref.keyResult);
			if (def.length != 0) { // in case of reading a constant, fObj and keyResult can be null
				StructuredSelection sel = new StructuredSelection(def[0].getElement());
				getViewer().setSelection(sel);
			}
		} 
		
		if (element instanceof ReferenceSearchResult) {

			ReferenceSearchResult rss = (ReferenceSearchResult) element;

			IProject prj = ZamiaProjectMap.getProject(rss.getZamiaProject());
			
			SourceLocation location = rss.getLocation();
			if (location != null) {
				IEditorPart editor = ZamiaPlugin.showSource(getSite().getPage(), prj, location, rss.getLength());
				if (editor instanceof ZamiaEditor) {
					ZamiaEditor ze = (ZamiaEditor) editor;
					ze.setPath(rss.getPath());
				}
			}
		}
	}

	class SearchLabelProvider extends LabelProvider implements IStyledLabelProvider {

		private final Image searchIcon;

		private final Image declIcon;

		private final Image readIcon;

		private final Image writeIcon;

		private final Image rwIcon;

		private final Image instantiationIcon;

		private final Image fInIcon;
		private final Image fOutIcon;
		private final Image fInoutIcon;

		public SearchLabelProvider() {
			super();

			searchIcon = ZamiaPlugin.getImage("/share/images/search.gif");
			declIcon = ZamiaPlugin.getImage("/share/images/decl.gif");
			readIcon = ZamiaPlugin.getImage("/share/images/read.gif");
			writeIcon = ZamiaPlugin.getImage("/share/images/write.gif");
			rwIcon = ZamiaPlugin.getImage("/share/images/rw.gif");
			instantiationIcon = ZamiaPlugin.getImage("/share/images/decl.gif");
			fInIcon = ZamiaPlugin.getImage("/share/images/in.gif");
			fOutIcon = ZamiaPlugin.getImage("/share/images/out.gif");
			fInoutIcon = ZamiaPlugin.getImage("/share/images/inout.gif");
		}

		public Image getImage(Object element) {

			if (element instanceof ReferenceSite) {

				ReferenceSite rs = (ReferenceSite) element;

				switch (rs.getRefType()) {
				case Declaration:
					return declIcon;
				case Read:
					return readIcon;
				case Write:
					return writeIcon;
				case ReadWrite:
					return rwIcon;
				case Instantiation:
					return instantiationIcon;
				}
			} else if (element instanceof ReferenceSearchResult) {

				ReferenceSearchResult rsr = (ReferenceSearchResult) element;

				OIDir dir = rsr.getDirection();
				switch (dir) {
				case IN:
					return fInIcon;
				case OUT:
					return fOutIcon;
				case INOUT:
				case BUFFER:
				case LINKAGE:
					return fInoutIcon;
				}

				return searchIcon;
			}

			return null;
		}

		public String getText(Object object) {
			throw new RuntimeException("Use getStyledText() instead of getText()");
//			if (object instanceof IFile) {
//				IFile ifile = (IFile) object;
//
//				return ifile.getProjectRelativePath().toOSString();
//
//			}
//			return object.toString();
		}

		@Override
		public StyledString getStyledText(Object element) {
			//System.err.println("generating style text for " + element + " (" + element.getClass().getName() + ")");
			if (element instanceof ReferenceSite) {
				ReferenceSite rs = (ReferenceSite) element;
				if (rs.getDBID() == 0)
					return  new StyledString("assign a constant");
				IGObject igObj = (IGObject) rs.getZamiaProject().getZDB().load(rs.getDBID()); 
				return new StyledString(rs.getRefType() + ": " + igObj.getId()).append(new StyledString(" : " + igObj.getType().toString() + " - " + rs.getPath(), StyledString.QUALIFIER_STYLER));
			}
			return new StyledString(element.toString());
		}
	}
	
	protected IStyledLabelProvider createLabelProvider() {
		return new SearchLabelProvider();
	}

    protected ViewerComparator createViewerComparator() {
		return new ViewerComparator();
	}
    
    class BackAction extends Action implements ISelectionChangedListener { 
    	List hist = new ArrayList(); 
    	
    	private Object current() {
    		return hist.get(hist.size()-1);
    	}
    	
    	public void selectionChanged(SelectionChangedEvent event) {
    		
    		IStructuredSelection sel = (IStructuredSelection) event.getSelection();
    		Object selObj = sel.getFirstElement();
    		
    		if (selObj == null ) {
    			hist.clear();
    		} else {
    			
    			if (hist.size() == 0 || selObj != current()) {
   					hist.add(selObj);
    			}
    		}
    		
    		setEnabled(hist.size() > 1);
    	}
    	
	    public void run() {
    		hist.remove(hist.size()-1);
	    	getViewer().setSelection(new StructuredSelection(current()));
	    	setEnabled(hist.size() > 1);
	    }
    	
	    public BackAction() {
	        ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
            setText(WorkbenchMessages.NavigationHistoryAction_backward_text); 
            setToolTipText(WorkbenchMessages.NavigationHistoryAction_backward_toolTip);
            setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_BACK));
            setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_BACK_DISABLED));
	        setEnabled(false);
	    }
	    
    }
    
}
