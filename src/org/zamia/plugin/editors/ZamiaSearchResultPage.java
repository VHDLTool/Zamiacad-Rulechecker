/* 
 * Copyright 2008 by the authors indicated in the @author tags. 
 * All rights reserved. 
 * 
 * See the LICENSE file for details.
 * 
 * Created by Guenter Bartsch on Jun 22, 2008
 */
package org.zamia.plugin.editors;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.viewsupport.ColoringLabelProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.zamia.ExceptionLogger;
import org.zamia.SourceLocation;
import org.zamia.ZamiaLogger;
import org.zamia.ZamiaProject;
import org.zamia.analysis.ReferenceSearchResult;
import org.zamia.analysis.ReferenceSite;
import org.zamia.analysis.ig.IGAssignmentsSearch.RootResult;
import org.zamia.analysis.ig.IGAssignmentsSearch.SearchAssignment;
import org.zamia.instgraph.IGObject;
import org.zamia.instgraph.IGObject.OIDir;
import org.zamia.instgraph.interpreter.logger.IGHitCountLogger;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.ZamiaProjectMap;

/**
 * 
 * @author Guenter Bartsch
 * 
 */
public class ZamiaSearchResultPage extends AbstractTextSearchViewPage {

	public final static ZamiaLogger logger = ZamiaLogger.getInstance();

	class ZamiaSearchTreeContentProvider implements ITreeContentProvider {

		private TreeViewer fTreeViewer;

		private ZamiaSearchResult fSearchResult;

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
			fSearchResult = (ZamiaSearchResult) newInput;
		}

		public void refresh() {
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

			if (parentElement instanceof RootResult && !showAssignments.isChecked()) {
				
				RootResult root = (RootResult) parentElement;
				int n = root.getNumChildren(); 
				
				// Simple Algorithm hides all but one assignment of the duplicates
				Collection res = new ArrayList(n);
				l1: for (ReferenceSearchResult r: root.fChildren) {
					for (Object a2: res)
						if (((SearchAssignment) a2).keyResult == ((SearchAssignment) r).keyResult)
							continue l1;
					res.add(r);
				}
					
//				// Complex algorithm introduces intermediate parent node for duplicates
				// Resulting tree may need more complex handling
//				HashSetArray<SearchAssignment> ch = new HashSetArray<SearchAssignment>(root.fChildren);
//				Collection<ReferenceSite> res = new ArrayList<ReferenceSite>(n);
//				while (!ch.isEmpty()) {
//					SearchAssignment a = ch.get(ch.size()-1);
//					RootResult next = a.keyResult;
//					if (next != null) {
//						ReferenceSite intermediate = new ReferenceSite(null, next.getLocation(), 1, RefType.Declaration, next.getPath(), loadObj(next.getDBID()));
//						for (SearchAssignment a2: ch) {
//							if (a2.keyResult == next) {
//								intermediate.add(a2);
//								ch.remove(a2);
//							}
//						}
//						assert intermediate.fChildren.size() > 0;
//						res.add(intermediate.fChildren.size() == 1 ? a : intermediate);
//					} else {
//						res.add(a);
//						ch.remove(a);
//					}
//					
//				}
					
				return res.toArray(new Object[res.size()]);
				
			}
			
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
			fContentProvider.refresh();
	}

	protected void configureTreeViewer(TreeViewer viewer) {
		viewer.setComparator(createViewerComparator());
        viewer.setLabelProvider(new ColoringLabelProvider(createLabelProvider()));
		fContentProvider = new ZamiaSearchTreeContentProvider();
		viewer.setContentProvider(fContentProvider);
		viewer.addSelectionChangedListener(backAction);
		
		// one click just shows in editor
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				showInEditor(getFirstSelected(event));
			}
			
		});
		
	}

    BackAction backAction = new BackAction();
    ForwardAction fwdAction = new ForwardAction();
    
	protected void fillToolbar(final IToolBarManager tbm) {
		tbm.appendToGroup(IContextMenuConstants.GROUP_SHOW, new Action("Highlight Assignments", IAction.AS_CHECK_BOX) {
			{
				setToolTipText("Highlights lines in Editor");
			}
			public void run() {
				ZamiaSearchResult root = (ZamiaSearchResult) getViewer().getInput();

				IGHitCountLogger linesLogger = new IGHitCountLogger("Lines logger");
				if (isChecked()) {
					logger.info("root = "  + root + " " + root.getElements());
					for (Object o : root.getElements())
						if (o instanceof SearchAssignment) {
							SearchAssignment a = (SearchAssignment) o;
							logger.info(" " + a);
							linesLogger.logHit(a.getLocation(), 0);
						}
				} else
					linesLogger = null;

				DebugReportVisualizer.getInstance(getZamiaProject()).setStaticalLines(linesLogger);
			}

		});
		tbm.appendToGroup(IContextMenuConstants.GROUP_GENERATE, new Action("Export", IAction.AS_PUSH_BUTTON) {
			{
				setToolTipText("Saves the graph as DOT file");
			}
			public void run() {
				(new Runnable() {
					
					String exportName(RootResult ref) {
						IGObject o = loadObj(ref.getDBID());
						return o == null ? null : o.getId() + ref.getPath().getPath(); 
					}
					public void run() {
						FileDialog fd = new FileDialog(getSite().getShell(), SWT.SAVE);
						fd.setFilterExtensions(new String[] {".dot"});
						String fname = fd.open();
						Collection<RootResult> searches = new ArrayList<RootResult>();
						for (Object e: fContentProvider.fSearchResult.getElements())
							if (e instanceof RootResult)
								searches.add((RootResult) e);
						
						try {
							Writer out = new FileWriter(fname);
							try {
								out.write("graph dependencies_from_assignments {\r");
								for (RootResult r : searches) {
									for (Object sa : fContentProvider.getChildren(r)) {
										SearchAssignment a = (SearchAssignment) sa;
										out.write("\t" + exportName(r)  
												//+ " -> " + exportName(a) 
												+ (a.keyResult != null ? " -> " + exportName(a.keyResult) : ""));
										out.write("\r");
									}
								}
								out.write("}");
							} finally {
								out.close();
							}
						} catch (IOException e) {
							ExceptionLogger.getInstance().logException(e);
						}
					}
				}).run();
			}
		});
		tbm.appendToGroup(IContextMenuConstants.GROUP_SHOW, backAction);
		tbm.appendToGroup(IContextMenuConstants.GROUP_SHOW, fwdAction);
		super.fillToolbar(tbm);
		tbm.appendToGroup(IContextMenuConstants.GROUP_VIEWER_SETUP, showAssignments);
	}
	
	Action showAssignments = new Action("Show Assignments", IAction.AS_CHECK_BOX) {
		
		{setToolTipText("Uncollapses duplicate assignments, the cases where a signal depends on another through multiple different assignments.");}
		
		public void run() {
			fContentProvider.refresh();
		}
	};
	
	protected void configureTableViewer(TableViewer viewer) {
		logger.error("ZamiaSearchResultPage: TableView not supported.");
		//		viewer.setComparator(createViewerComparator());
		//		viewer.setLabelProvider(createLabelProvider());
		//		fContentProvider = new ContentProvider();
		//		viewer.setContentProvider(fContentProvider);
	}

	private ReferencesSearchQuery getQuery() {
		return (ReferencesSearchQuery) fContentProvider.fSearchResult.fQuery;
	}
	private ZamiaProject getZamiaProject() {
		return getQuery().fZPrj;
	}
	
	void showInEditor(Object element) {
		

		logger.debug("Element: " + element);
		
		if (element instanceof ReferenceSearchResult) {

			ReferenceSearchResult rss = (ReferenceSearchResult) element;

			IProject prj = ZamiaProjectMap.getProject(getZamiaProject());
			
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
	public void showMatch(Match match, int offset, int length, boolean activate) throws PartInitException {
		Object element = match.getElement();
		showInEditor(element);
		if (element instanceof SearchAssignment) {
//			SearchAssignment ref = (SearchAssignment) element;
//			Match[] def = this.getDisplayedMatches(ref.keyResult);
//			if (def.length != 0) { // in case of reading a constant, fObj and keyResult can be null
//				StructuredSelection sel = new StructuredSelection(def[0].getElement());
//				getViewer().setSelection(sel);
//			}			
			fwdAction.run();
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

			if (element instanceof SearchAssignment) {
				ImageDescriptor descr = JavaPluginImages.createImageDescriptor(JavaPlugin.getDefault().getBundle(), JavaPluginImages.ICONS_PATH.append("e" + "lcl16").append("ch_callers.gif"), true);
				return JavaPlugin.getImageDescriptorRegistry().get(
						//JavaPluginImages.DESC_MISC_DEFAULT
						descr
						);
			}
			else if (element instanceof ReferenceSite) {

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

		Styler typeStyler = new ColorStyler(128, 180, 128);
		Styler pathStyler = new ColorStyler(128, 128, 255);
		class ColorStyler extends Styler {

			private Color foreground;
			ColorStyler(int red, int green, int blue) {
				foreground = new Color(Display.getDefault(), red, green, blue);
			}
			public void applyStyles(TextStyle textStyle) {
				textStyle.foreground = foreground;
			}	
			
		};
		
		@Override
		public StyledString getStyledText(Object element) {
			if (element instanceof ReferenceSite) {
				ReferenceSite rs = (ReferenceSite) element;
				//String icon = rs instanceof SearchAssignment ? "    => " : ""; 
				if (rs.getDBID() == 0)
					return new StyledString("a constant");
				IGObject igObj = loadObj(rs.getDBID());
				String type = igObj.getType().toString();

				String nextSearch = "";
				if (rs instanceof SearchAssignment) {
					RootResult searchNum = ((SearchAssignment)rs).keyResult;
					nextSearch = " => [" + (searchNum == null ? "search failed" : searchNum.num_prefix + "") + "]"; 
				}

				// []
				StyledString[] resultParts = new StyledString[] {
						new StyledString((rs instanceof RootResult) ? ((RootResult) rs).num_prefix + ". " : "", StyledString.QUALIFIER_STYLER),
						new StyledString(igObj.getId() + nextSearch),
						new StyledString(" : ", StyledString.QUALIFIER_STYLER), 
						new StyledString(type.toLowerCase(), typeStyler),
						new StyledString(getQuery().fSearchDownward || getQuery().fSearchUpward ? " - " + rs.getPath().getPath() : "", pathStyler) 									
				};
				
				StyledString result = new StyledString();
				for (StyledString p: resultParts) {
					result.append(p);
				}
				return result;
			}
			return new StyledString(element.toString());
		}

	}
	
	private IGObject loadObj(long dbid) {
		return (IGObject) getZamiaProject().getZDB().load(dbid);
	}
	
	protected IStyledLabelProvider createLabelProvider() {
		return new SearchLabelProvider();
	}

    protected ViewerComparator createViewerComparator() {
		return new ViewerComparator() {
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1 instanceof RootResult && e2 instanceof RootResult)
					return ((RootResult) e1).num_prefix - ((RootResult) e2).num_prefix;
				return super.compare(viewer, e1, e2);
			}
		};

	}
    
    Object getFirstSelected(SelectionChangedEvent event) {
    	IStructuredSelection sel = (IStructuredSelection) event.getSelection();
    	return sel.getFirstElement();
    }
	
    class BackAction extends Action implements ISelectionChangedListener { 
    	List hist = new ArrayList(); 
    	
    	private Object current() {
    		return hist.get(hist.size()-1);
    	}


    	public void selectionChanged(SelectionChangedEvent event) {
    		
    		Object selObj = getFirstSelected(event);

    		if (selObj == null ) {
    			hist.clear();
    		} else {
    			
    			if (hist.size() == 0 || selObj != current()) {
   					hist.add(selObj);
    			}
    		}
    		
    		setEnabled(hist.size() > 1);
    		
    		boolean fwdEnabled = selObj != null && selObj instanceof SearchAssignment 
    				&& ((SearchAssignment) selObj).getDBID() != 0
    				//&& fwdAction.selection.keyResult != null
    				; 
    		fwdAction.setEnabled(fwdEnabled);

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
    
    class ForwardAction extends Action {
	    public ForwardAction() {
	        ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
            setText(WorkbenchMessages.NavigationHistoryAction_forward_text); 
            setToolTipText(//WorkbenchMessages.NavigationHistoryAction_forward_toolTip
            		"Jump from assignment to the assignment-induced search");
            setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));
            setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD_DISABLED));
	        setEnabled(false);
	    }
	    
	    public void run() {
	    	if (isEnabled())
	    		getViewer().setSelection(new StructuredSelection(((SearchAssignment) backAction.current()).keyResult));
	    }

    }

    
}
