/*
 * Copyright 2004-2009,2011 by the authors indicated in the @author tags.
 * All rights reserved.
 *
 * See the LICENSE file for details.
 *
 */

package org.zamia.plugin.views.rtl;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.ViewPart;
import org.zamia.ExceptionLogger;
import org.zamia.SourceLocation;
import org.zamia.ZamiaLogger;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.ZamiaProjectMap;
import org.zamia.rtl.RTLModule;
import org.zamia.rtl.RTLNode;
import org.zamia.rtl.RTLPort;
import org.zamia.rtl.RTLSignal;
import org.zamia.rtl.RTLVisualGraphContentProvider;
import org.zamia.rtl.RTLVisualGraphLabelProvider;
import org.zamia.rtl.RTLVisualGraphSelectionProvider;
import org.zamia.util.Position;
import org.zamia.util.SimpleRegexp;
import org.zamia.vg.VGBox;
import org.zamia.vg.VGGC;
import org.zamia.vg.VGLayout;

/**
 * 
 * @author Guenter Bartsch
 * 
 */

public class RTLView extends ViewPart implements ZoomObserver, PaintListener {

	public final static ZamiaLogger logger = ZamiaLogger.getInstance();

	public final static ExceptionLogger el = ExceptionLogger.getInstance();

	public static String VIEW_ID = "org.zamia.plugin.views.rtl.RTLView";

	public static final double TOP_MARGIN = 50.0;

	public static final double LEFT_MARGIN = 50.0;

	public static final double RIGHT_MARGIN = 50.0;

	public static final double BOTTOM_MARGIN = 50.0;

	public static final int SMALL_FONT_SIZE = 20;

	public static final int NORMAL_FONT_SIZE = 60;

	public static final int BIG_FONT_SIZE = 80;

	public static final String FONT_NAME = "Sans";

	private RTLModule fRTLM;

	RTLVisualGraphContentProvider fContentProvider = null;

	RTLVisualGraphLabelProvider fLabelProvider = null;

	private ColorScheme fColorScheme;

	private VGLayout<RTLNode, RTLPort, RTLSignal> fLayout;

	//	private HashMap<RTLSignal, Position> annotationPositions;

	private Display display;

	private Canvas fCanvas;

	private Text fLocationText, fSearchText;

	private Label fSelectionLabel;

	// offscreen image
	public final static int OFFSCREEN_WIDTH = 4096;

	public final static int OFFSCREEN_HEIGHT = 4096;

	private Image fOffscreenImage;

	private boolean fOffscreenValid;

	private Point fOffscreenOffset;

	private Point fOffscreenSize;

	private GC fOffscreenGC;

	private Position fTotalSize; // size of unzoomed circuit

	private Point fVisibleSize; // size of canvas

	private Point fVisibleOffset; // given by scrollbars

	private ZoomWidget fZoomWidget;

	private double fZoomFactor;

	private Point fZommedSize; // = total * zoom

	private Menu fPopupMenu;

	private ScrollBar fHScrollBar;

	private ScrollBar fVScrollBar;

	// fonts
	private Font fSmallFont, fNormalFont, fBigFont;

	private Composite fControl;

	private Sash fSash;

	private Composite fMainBox;

	private RTLTree fTree;

	private RTLVisualGraphSelectionProvider fSelectionProvider;

	private VGGC fGC;

	private IProject fPrj;

	private class MouseHandler implements MouseListener, MouseMoveListener {

		// private int grabEvents;
		private boolean fDragMode = false;

		private int fXStart, fYStart;

		public MouseHandler() {
			super();
			// grabEvents = 0;
		}

		public void mouseDown(MouseEvent aMouseEvent) {
			// if (dragMode) {
			// if (grabEvents++ == 0) {
			fXStart = aMouseEvent.x;
			fYStart = aMouseEvent.y;
			// }
			// } else {
			// if (grabEvents != 0) {
			// grabEvents = 0;
			// }

			fDragMode = handleMouseDown(aMouseEvent.x, aMouseEvent.y, aMouseEvent.button);
			// }
		}

		public void mouseDoubleClick(MouseEvent aMouseEvent) {
			handleMouseDoubleClick(aMouseEvent.x, aMouseEvent.y);
		}

		public void mouseUp(MouseEvent aMouseEvent) {
			// grabEvents = 0;
			fDragMode = false;
		}

		public void mouseMove(MouseEvent aMouseEvent) {
			if (fDragMode) {

				int oldOffsetX = fVisibleOffset.x;
				int oldOffsetY = fVisibleOffset.y;
				int newOffsetX = oldOffsetX;
				int newOffsetY = oldOffsetY;

				if (fZommedSize.x > fVisibleSize.x) {
					newOffsetX = Math.min(oldOffsetX + (fXStart - aMouseEvent.x), fZommedSize.x - fVisibleSize.x);
					if (newOffsetX < 0)
						newOffsetX = 0;
				}
				if (fZommedSize.y > fVisibleSize.y) {
					newOffsetY = Math.min(oldOffsetY + (fYStart - aMouseEvent.y), fZommedSize.y - fVisibleSize.y);
					if (newOffsetY < 0)
						newOffsetY = 0;
				}
				if (oldOffsetX != newOffsetX || oldOffsetY != newOffsetY) {
					fHScrollBar.setSelection(newOffsetX);
					fVScrollBar.setSelection(newOffsetY);
					fCanvas.update();
					fVisibleOffset.x = newOffsetX;
					fVisibleOffset.y = newOffsetY;
					fCanvas.redraw();
				}
				fXStart = aMouseEvent.x;
				fYStart = aMouseEvent.y;

			}
			// else if (grabEvents != 0) {
			// grabEvents = 0;
			// }
		}

	}

	public RTLView() {

	}

	public void createPartControl(Composite aParent) {

		display = aParent.getDisplay();

		// control = new RTLView(parent, new ColorSchemeZamia(display), true,
		// true, this);

		fColorScheme = new ColorSchemeZamia(display);

		fLayout = null;

		fSelectionProvider = new RTLVisualGraphSelectionProvider();

		display = getDisplay();

		fControl = new Composite(aParent, SWT.NONE);

		GridLayout gl = new GridLayout();
		fControl.setLayout(gl);
		gl.numColumns = 1;
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;

		// a coolbar for navigation and zoom

		CoolBar coolbar = new CoolBar(fControl, SWT.NONE);
		GridData gd = new GridData();
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = false;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		coolbar.setLayoutData(gd);

		// navigation coolitem

		Composite comp = new Composite(coolbar, SWT.NONE);

		gl = new GridLayout();
		comp.setLayout(gl);
		gl.numColumns = 3;
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;

		fLocationText = new Text(comp, SWT.BORDER);
		fLocationText.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				switch (e.character) {
				case SWT.CR:
					//navigate(location.getText());
					break;
				case '1':
					fZoomWidget.setFactor(1.0);
					break;
				}
			}
		});
		gd = new GridData();
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = false;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		fLocationText.setLayoutData(gd);

		ToolBar tb = new ToolBar(comp, SWT.FLAT);

		ToolItem ti = new ToolItem(tb, SWT.NONE);
		Image icon = ZamiaPlugin.getImage("/share/images/gohome.gif");
		ti.setImage(icon);
		ti.setToolTipText("Go to toplevel RTLModule");
		ti.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				RTLModule newRTLG = fRTLM;
				if (newRTLG == null)
					return;
				while (newRTLG.getParent() != null)
					newRTLG = newRTLG.getParent();
				setRTLModule(newRTLG);
			}
		});

		ti = new ToolItem(tb, SWT.NONE);
		icon = ZamiaPlugin.getImage("/share/images/up.gif");
		ti.setImage(icon);
		ti.setToolTipText("Go to parent RTLModule");
		ti.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				RTLModule newNL = (RTLModule) fRTLM.getParent();
				if (newNL != null)
					setRTLModule(newNL);
			}
		});

		// finish navigation coolitem

		CoolItem citem = new CoolItem(coolbar, SWT.NONE);
		citem.setControl(comp);
		calcSize(citem);
		Point pt = citem.getSize();
		citem.setSize(pt.x + 400, pt.y);

		/*
		 * zoom coolitem
		 */

		comp = new Composite(coolbar, SWT.NONE);

		gl = new GridLayout();
		gl.numColumns = 3;
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		comp.setLayout(gl);

		tb = new ToolBar(comp, SWT.FLAT);

		fZoomWidget = new ZoomWidget(comp, 0.5, 100.0, 1.0);
		fZoomWidget.addZoomObserver(this);

		citem = new CoolItem(coolbar, SWT.NONE);
		citem.setControl(comp);
		calcSize(citem);

		/***********************************************************************
		 * 
		 * navigator / canvas sash
		 * 
		 ***********************************************************************/

		fMainBox = new Composite(fControl, SWT.NONE);
		gd = new GridData();
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		fMainBox.setLayoutData(gd);

		fCanvas = new Canvas(fMainBox, SWT.V_SCROLL | SWT.H_SCROLL);

		fSash = new Sash(fMainBox, SWT.VERTICAL);
		fTree = new RTLTree(fMainBox);
		fTree.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {

				ITreeSelection selection = (ITreeSelection) fTree.getSelection();

				Object first = selection.getFirstElement();
				if (first instanceof RTLNode) {

					selectAndReveal((RTLNode) first);

				}
			}
		});

		final FormLayout form = new FormLayout();
		fMainBox.setLayout(form);

		FormData canvasData = new FormData();
		canvasData.left = new FormAttachment(0, 0);
		canvasData.right = new FormAttachment(fSash, 0);
		canvasData.top = new FormAttachment(0, 0);
		canvasData.bottom = new FormAttachment(100, 0);
		fCanvas.setLayoutData(canvasData);

		final int limit = 20, percent = 85;
		final FormData sashData = new FormData();
		sashData.left = new FormAttachment(percent, 0);
		sashData.top = new FormAttachment(0, 0);
		sashData.bottom = new FormAttachment(100, 0);
		fSash.setLayoutData(sashData);
		fSash.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				Rectangle sashRect = fSash.getBounds();
				Rectangle shellRect = fMainBox.getClientArea();
				int right = shellRect.width - sashRect.width - limit;
				e.x = Math.max(Math.min(e.x, right), limit);
				if (e.x != sashRect.x) {
					sashData.left = new FormAttachment(0, e.x);
					fMainBox.layout();
				}
			}
		});

		FormData treeData = new FormData();
		treeData.left = new FormAttachment(fSash, 0);
		treeData.right = new FormAttachment(100, 0);
		treeData.top = new FormAttachment(0, 0);
		treeData.bottom = new FormAttachment(100, 0);
		fTree.getControl().setLayoutData(treeData);

		/***********************************************************************
		 * canvas
		 ***********************************************************************/

		fOffscreenSize = new Point(OFFSCREEN_WIDTH, OFFSCREEN_HEIGHT);
		fOffscreenImage = new Image(display, fOffscreenSize.x, fOffscreenSize.y);
		fOffscreenGC = new GC(fOffscreenImage);

		fGC = new SWTGC(fOffscreenGC);

		fOffscreenOffset = new Point(0, 0);
		fOffscreenValid = false;

		MouseHandler mouseHandler = new MouseHandler();
		fCanvas.addMouseListener(mouseHandler);
		fCanvas.addMouseMoveListener(mouseHandler);

		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				if (event.type == SWT.MouseWheel) {

					Rectangle ca = fCanvas.getClientArea();
					double ax = (double) event.x / (double) ca.width;
					double ay = (double) event.y / (double) ca.height;

					double of = fZoomWidget.getFactor();

					if (event.count > 0) {
						fZoomWidget.setFactor(of * 1.25, false);
					} else {
						fZoomWidget.setFactor(of / 1.25, false);
					}
					doZoom(fZoomWidget.getFactor(), ax, ay);
					event.doit = false;
				}
			}
		};

		fCanvas.addListener(SWT.MouseWheel, listener);

		initScrollBars();

		/***********************************************************************
		 * search/status bar
		 ***********************************************************************/

		Composite statusBox = new Composite(fControl, SWT.NONE);
		gl = new GridLayout();
		statusBox.setLayout(gl);
		gl.numColumns = 8;
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;

		gd = new GridData();
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = false;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		statusBox.setLayoutData(gd);

		/*
		 * io/builtins limits toolbar
		 */

		tb = new ToolBar(statusBox, SWT.FLAT);

		ti = new ToolItem(tb, SWT.NONE);
		ti.setText("D");
		ti.setToolTipText("Dynamic Mode");
		ti.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				if (fContentProvider != null) {
					fContentProvider.setDynamicMode(!fContentProvider.isDynamicMode());

					placeAndRoute();
					updateZoom(fZoomWidget.getFactor());
				}
			}
		});

		ti = new ToolItem(tb, SWT.NONE);
		icon = ZamiaPlugin.getImage("/share/images/eraser.gif");
		ti.setImage(icon);
		ti.setToolTipText("Clear");
		ti.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				if (fContentProvider != null) {
					fContentProvider.setDynamicMode(true);
					fContentProvider.clearVisibility();

					placeAndRoute();
					updateZoom(fZoomWidget.getFactor());
				}
			}
		});

		/*
		 * search toolbar
		 */

		fSearchText = new Text(statusBox, SWT.BORDER);
		fSearchText.setToolTipText("Simple regexp signal/module search, e.g. '*foo*'");
		fSearchText.setText("          ");
		fSearchText.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				switch (e.character) {
				case SWT.CR:
					doSearch(fSearchText.getText());
					break;
				}
			}
		});
		gd = new GridData();
		gd.verticalAlignment = GridData.CENTER;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		fSearchText.setLayoutData(gd);

		tb = new ToolBar(statusBox, SWT.FLAT);

		ti = new ToolItem(tb, SWT.NONE);
		icon = ZamiaPlugin.getImage("/share/images/find.gif");
		ti.setImage(icon);
		ti.setToolTipText("Search");
		ti.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				doSearch(fSearchText.getText());
			}
		});

		fSelectionLabel = new Label(statusBox, SWT.NONE);
		fSelectionLabel.setText("");
		gd = new GridData();
		gd.verticalAlignment = GridData.CENTER;
		gd.grabExcessVerticalSpace = false;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		fSelectionLabel.setLayoutData(gd);

		fRTLM = null;

		reset();

		resizeFonts();

		fCanvas.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent event) {
				updateZoom(fZoomWidget.getFactor());
			}
		});

		/* Set up the paint canvas scroll bars */
		ScrollBar horizontal = fCanvas.getHorizontalBar();
		horizontal.setVisible(true);
		horizontal.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				scrollHorizontally((ScrollBar) event.widget);
			}
		});
		ScrollBar vertical = fCanvas.getVerticalBar();
		vertical.setVisible(true);
		vertical.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				scrollVertically((ScrollBar) event.widget);
			}
		});
		handleResize();

		fCanvas.addPaintListener(this);

		fPopupMenu = new Menu(fCanvas.getShell(), SWT.POP_UP);
		MenuItem item = new MenuItem(fPopupMenu, SWT.PUSH);
		item.setText("Trace");
		item.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

				// FIXME: implement

				//				RTLSignal s = getSelectedRTLSignal();
				//				if (s != null) {
				//					ISimulator sim = findSimulator();
				//					if (sim != null) {
				//						try {
				//							sim.trace(s.getPath());
				//						} catch (ZamiaException e1) {
				//							el.logException(e1);
				//						}
				//					}
				//				}
			}
		});

		fCanvas.setMenu(fPopupMenu);

	}

	private void placeAndRoute() {

		if (fRTLM == null)
			return;

		reset();
		resizeFonts();

		fLayout = new VGLayout<RTLNode, RTLPort, RTLSignal>(fContentProvider, fLabelProvider, fGC);

		fTotalSize = fLayout.getTotalSize();

		handleResize();
	}

	private void reset() {
		fTotalSize = new Position(1, 1);
		fVisibleSize = new Point(1, 1);
		fZommedSize = new Point(1, 1);
		fVisibleOffset = new Point(0, 0);
		fOffscreenOffset = new Point(0, 0);
		fZoomFactor = 1.0;
	}

	private void paintOffscreen(GC aOffscreenGC) {
		Font oldfont = aOffscreenGC.getFont();
		int fontSize = (int) (8.0 * getZoomFactor());

		if (fontSize < 2)
			fontSize = 2;
		Font font = new Font(display, "Sans", fontSize, SWT.NONE);
		aOffscreenGC.setFont(font);

		aOffscreenGC.setBackground(fColorScheme.getBgColor());
		aOffscreenGC.setLineWidth((int) (2 * getZoomFactor()));
		aOffscreenGC.fillRectangle(0, 0, fOffscreenSize.x, fOffscreenSize.y);

		if (fLayout != null) {
			fLayout.paint(fSelectionProvider);
		}

		aOffscreenGC.setFont(oldfont);
		font.dispose();
	}

	public void updateOffscreen(GC aOffscreenGC) {

		// update offscreenOffset if necessary, repaint in that case

		Point offset = new Point(fOffscreenOffset.x, fOffscreenOffset.y);

		if (offset.x > fVisibleOffset.x)
			offset.x = fVisibleOffset.x - fOffscreenSize.x / 2;
		if (offset.y > fVisibleOffset.y)
			offset.y = fVisibleOffset.y - fOffscreenSize.x / 2;

		int w = fOffscreenSize.x;
		int h = fOffscreenSize.y;

		if ((fVisibleOffset.x + fVisibleSize.x) >= offset.x + w)
			offset.x = fVisibleOffset.x - fOffscreenSize.x / 2;
		if ((fVisibleOffset.y + fVisibleSize.y) >= offset.y + h)
			offset.y = fVisibleOffset.y - fOffscreenSize.x / 2;

		if (offset.x < 0)
			offset.x = 0;
		if (offset.y < 0)
			offset.y = 0;

		if ((offset.x != fOffscreenOffset.x) || (offset.y != fOffscreenOffset.y)) {
			// System.out.println ("offscreenOffset updated to "+offset);
			fOffscreenValid = false;
			fOffscreenOffset = offset;
		}

		if (fOffscreenValid)
			return;

		paintOffscreen(aOffscreenGC);

		fOffscreenValid = true;
	}

	private void initScrollBars() {
		fHScrollBar = fCanvas.getHorizontalBar();
		// horizontal.setEnabled(false);
		fHScrollBar.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				scrollHorizontally((ScrollBar) event.widget);
			}
		});
		fVScrollBar = fCanvas.getVerticalBar();
		// vertical.setEnabled(false);
		fVScrollBar.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				scrollVertically((ScrollBar) event.widget);
			}
		});
	}

	public void paintControl(PaintEvent aPaintEvent) {

		updateOffscreen(fOffscreenGC);

		int ox = fVisibleOffset.x - fOffscreenOffset.x;
		int oy = fVisibleOffset.y - fOffscreenOffset.y;
		Rectangle clientRect = fCanvas.getClientArea();
		aPaintEvent.gc.drawImage(fOffscreenImage, ox, oy, fVisibleSize.x, fVisibleSize.y, clientRect.x, clientRect.y, fVisibleSize.x, fVisibleSize.y);
	}

	/*
	 * helper functions for drawing
	 */

	private final static double CLIPPING_MAX = Integer.MAX_VALUE;

	private final static double CLIPPING_MIN = Integer.MIN_VALUE;

	public int tX(double aX) {
		double d = (aX + LEFT_MARGIN) * getZoomFactor() - fOffscreenOffset.x;
		// simple clipping
		if (d > CLIPPING_MAX)
			return (int) CLIPPING_MAX;
		if (d < CLIPPING_MIN)
			return (int) CLIPPING_MIN;
		return (int) d;

	}

	public int tY(double aY) {
		double d = (aY + TOP_MARGIN) * getZoomFactor() - fOffscreenOffset.y;
		// simple clipping
		if (d > CLIPPING_MAX)
			return (int) CLIPPING_MAX;
		if (d < CLIPPING_MIN)
			return (int) CLIPPING_MIN;
		return (int) d;
	}

	public int tW(double aW) {
		double d = aW * getZoomFactor();
		// simple clipping
		if (d > CLIPPING_MAX)
			return (int) CLIPPING_MAX;
		if (d < CLIPPING_MIN)
			return (int) CLIPPING_MIN;
		return (int) d;
	}

	public int tH(double aH) {
		double d = aH * getZoomFactor();
		// simple clipping
		if (d > CLIPPING_MAX)
			return (int) CLIPPING_MAX;
		if (d < CLIPPING_MIN)
			return (int) CLIPPING_MIN;
		return (int) d;
	}

	public void scrollHorizontally(ScrollBar aScrollBar) {
		if (fZommedSize.x > fVisibleSize.x) {
			final int oldOffset = fVisibleOffset.x;
			final int newOffset = Math.min(aScrollBar.getSelection(), fZommedSize.x - fVisibleSize.x);
			if (oldOffset != newOffset) {
				fCanvas.update();
				fVisibleOffset.x = newOffset;
				fCanvas.redraw();
				// canvas.scroll(
				// Math.max(oldOffset - newOffset, 0),
				// 0,
				// Math.max(newOffset - oldOffset, 0),
				// 0,
				// visibleSize.x,
				// visibleSize.y,
				// false);
			}
		}
	}

	public void scrollVertically(ScrollBar aScrollBar) {
		if (fZommedSize.y > fVisibleSize.y) {
			final int oldOffset = fVisibleOffset.y;
			final int newOffset = Math.min(aScrollBar.getSelection(), fZommedSize.y - fVisibleSize.y);
			if (oldOffset != newOffset) {
				fCanvas.update();
				fVisibleOffset.y = newOffset;
				fCanvas.redraw();
				// canvas.scroll(
				// 0,
				// Math.max(oldOffset - newOffset, 0),
				// 0,
				// Math.max(newOffset - oldOffset, 0),
				// visibleSize.x,
				// visibleSize.y,
				// false);
			}
		}
	}

	private void handleResize() {
		fControl.update();

		Rectangle visibleRect = fCanvas.getClientArea();
		fVisibleSize.x = visibleRect.width;
		fVisibleSize.y = visibleRect.height;

		fZommedSize.x = tW(fTotalSize.getX() + LEFT_MARGIN + RIGHT_MARGIN);
		fZommedSize.y = tH(fTotalSize.getY() + TOP_MARGIN + BOTTOM_MARGIN);

		// System.out.println ("handleResize():
		// visibleSize="+visibleSize.x+"x"+visibleSize.y+",
		// zoomedSize="+zoomedSize.x+"x"+zoomedSize.y);

		ScrollBar horizontal = fCanvas.getHorizontalBar();
		if (horizontal != null) {
			fVisibleOffset.x = Math.min(horizontal.getSelection(), fZommedSize.x - fVisibleSize.x);
			if (fZommedSize.x <= fVisibleSize.x) {
				horizontal.setEnabled(false);
				horizontal.setSelection(0);
				fVisibleOffset.x = 0;
				horizontal.setValues(fVisibleOffset.x, 0, fVisibleSize.x, fVisibleSize.x, 8, fVisibleSize.x);
			} else {
				horizontal.setEnabled(true);
				horizontal.setValues(fVisibleOffset.x, 0, fZommedSize.x, fVisibleSize.x, 8, fVisibleSize.x);
			}
		}

		ScrollBar vertical = fCanvas.getVerticalBar();
		if (vertical != null) {
			fVisibleOffset.y = Math.min(vertical.getSelection(), fZommedSize.y - fVisibleSize.y);
			if (fZommedSize.y <= fVisibleSize.y) {
				vertical.setEnabled(false);
				vertical.setSelection(0);
				fVisibleOffset.y = 0;
				vertical.setValues(fVisibleOffset.y, 0, fVisibleSize.y, fVisibleSize.y, 8, fVisibleSize.y);
			} else {
				vertical.setEnabled(true);
				vertical.setValues(fVisibleOffset.y, 0, fZommedSize.y, fVisibleSize.y, 8, fVisibleSize.y);
			}
		}
	}

	public void setRTLModule(RTLModule aRTLM) {

		fRTLM = aRTLM;

		fOffscreenValid = false;
		if (fRTLM != null) {

			fContentProvider = new RTLVisualGraphContentProvider(fRTLM);
			fLabelProvider = new RTLVisualGraphLabelProvider(fRTLM);

			placeAndRoute();
			updateZoom(fZoomWidget.getFactor());

			fLocationText.setText("/");

			fPrj = ZamiaProjectMap.getProject(fRTLM.getZPrj());

		} else {
			fLocationText.setText("");
		}
		fZoomWidget.setFactor(1.0); // will call handleResize / canvas.redraw

		fTree.setInput(aRTLM);

	}

	public RTLModule getRTLModule() {
		return fRTLM;
	}

	private void doZoom(double aFactor, double aX, double aY) {

		Rectangle visibleRect = fCanvas.getClientArea();
		fVisibleSize.x = visibleRect.width;
		fVisibleSize.y = visibleRect.height;

		// 100% zoom should mean display whole circuit

		double fx = (double) fVisibleSize.x / ((double) fTotalSize.getX() + LEFT_MARGIN + RIGHT_MARGIN);
		double fy = (double) fVisibleSize.y / ((double) fTotalSize.getY() + TOP_MARGIN + BOTTOM_MARGIN);

		double of = fZoomFactor;

		if (fx > fy)
			fZoomFactor = aFactor * fy;
		else
			fZoomFactor = aFactor * fx;

		double df = fZoomFactor / of;

		fZommedSize.x = tX(fTotalSize.getX() - 1);
		fZommedSize.y = tY(fTotalSize.getY() - 1);

		ScrollBar horizontal = fCanvas.getHorizontalBar();
		if (horizontal != null) {

			double off = horizontal.getSelection();

			double margin = fVisibleSize.x * aX;

			double mx = (off + margin) * df - margin;

			int ox = (int) mx;

			fVisibleOffset.x = ox;
			horizontal.setValues(fVisibleOffset.x, 0, fZommedSize.x, fVisibleSize.x, 8, fVisibleSize.x);

		}
		ScrollBar vertical = fCanvas.getVerticalBar();
		if (vertical != null) {

			double off = vertical.getSelection();

			double margin = fVisibleSize.y * aY;

			double my = (off + margin) * df - margin;

			int oy = (int) my;

			fVisibleOffset.y = oy;
			vertical.setValues(fVisibleOffset.y, 0, fZommedSize.y, fVisibleSize.y, 8, fVisibleSize.y);
		}

		fOffscreenValid = false;

		resizeFonts();

		handleResize();
		fCanvas.redraw();

	}

	private void resizeFonts() {
		if (fSmallFont != null) {
			fSmallFont.dispose();
			fSmallFont = null;
		}
		if (fNormalFont != null) {
			fNormalFont.dispose();
			fNormalFont = null;
		}
		if (fBigFont != null) {
			fBigFont.dispose();
			fBigFont = null;
		}

		fSmallFont = new Font(display, FONT_NAME, tF(SMALL_FONT_SIZE), SWT.NONE);
		fNormalFont = new Font(display, FONT_NAME, tF(NORMAL_FONT_SIZE), SWT.NONE);
		fBigFont = new Font(display, FONT_NAME, tF(BIG_FONT_SIZE), SWT.BOLD);
	}

	public int tF(double aSize) {
		int d = tW(aSize);
		if (d < 2)
			d = 2;
		return d;
	}

	public void updateZoom(double aFactor) {
		fControl.update();

		doZoom(aFactor, 0.5, 0.5);
	}

	public double getZoomFactor() {
		return fZoomFactor;
	}

	public void clearHighlight() {
		fSelectionProvider.clear();
	}

	private void addHighlight(RTLNode aNode) {

		fSelectionProvider.setNodeSelection(aNode, true);

		fOffscreenValid = false;
		fCanvas.redraw();
	}

	public void selectAndReveal(RTLNode aNode) {

		if (fLayout == null)
			return;

		clearHighlight();

		addHighlight(aNode);

		// find position and size, zoom in

		VGBox<RTLNode, RTLPort, RTLSignal> box = fLayout.getNodeBox(aNode);
		if (box == null) {

			logger.error("RTLView: Node to reveal is not placed: %s", box);
			return;
		}

		int w = box.getWidth();
		if (w < 40)
			w = 40;
		int h = box.getHeight();
		if (h < 40)
			h = 40;

		double f1 = (fTotalSize.getX() / w) / 2.0;
		double f2 = (fTotalSize.getY() / h) / 2.0;

		double f = f1 < f2 ? f1 : f2;

		fZoomWidget.setFactor(f, false);
		doZoom(f, 0.0, 0.0);

		int offx = tX(box.getXPos()) - fVisibleSize.x / 2;
		int offy = tY(box.getYPos()) - fVisibleSize.y / 2;
		offx = offx < 0 ? 0 : offx;
		offy = offy < 0 ? 0 : offy;

		fHScrollBar.setSelection(offx);
		fVScrollBar.setSelection(offy);

		doZoom(f, 0.0, 0.0);
	}

	void addHighlight(RTLSignal aSignal) {
		fSelectionProvider.setSignalSelection(aSignal, true);
		fOffscreenValid = false;
		fCanvas.redraw();
	}

	public void doSearch(String aRegexp) {
		if (fRTLM == null)
			return;

		clearHighlight();

		String regexps[] = aRegexp.split(" ");

		for (int j = 0; j < regexps.length; j++) {

			String regexp = SimpleRegexp.convert(regexps[j]);

			if (regexp == null || regexp.length() < 1) {
				continue;
			}

			boolean inverse = false;
			char firstC = regexp.charAt(0);
			if (firstC == '-') {
				regexp = regexp.substring(1);
				inverse = true;
			} else if (firstC == '+') {
				regexp = regexp.substring(1);
			}

			int n = fRTLM.getNumNodes();
			for (int i = 0; i < n; i++) {
				RTLNode sub = fRTLM.getNode(i);
				if (sub.getInstanceName().matches(regexp)) {
					if (!inverse) {

						fContentProvider.setNodeVisible(sub, true);
						fSelectionProvider.setNodeSelection(sub, true);

					} else {

						fContentProvider.setNodeVisible(sub, false);
						fSelectionProvider.setNodeSelection(sub, false);

					}
				}
			}

			n = fRTLM.getNumSignals();
			for (int i = 0; i < n; i++) {
				RTLSignal s = fRTLM.getSignal(i);
				if (s.getId().matches(regexp)) {
					fSelectionProvider.setSignalSelection(s, !inverse);
				}
			}
		}

		placeAndRoute();
		updateZoom(fZoomWidget.getFactor());

		fOffscreenValid = false;
		fCanvas.redraw();
	}

	public boolean handleMouseDown(int aMX, int aMY, int aButton) {
		if (fRTLM == null) {
			return false;
		}

		// project coordinates, find out what has been hit

		int mx = (int) ((aMX + fVisibleOffset.x - fOffscreenOffset.x) / getZoomFactor() - LEFT_MARGIN);
		int my = (int) ((aMY + fVisibleOffset.y - fOffscreenOffset.y) / getZoomFactor() - TOP_MARGIN);

		clearHighlight();

		RTLPort p = fLayout.checkHitExpandablePort(mx, my, (int) (1.0 / getZoomFactor()));
		if (p != null) {

			RTLSignal s = p.getSignal();

			fContentProvider.setPortExpanded(p, true);

			int n = s.getNumConns();
			for (int i = 0; i < n; i++) {
				RTLPort conn = s.getConn(i);

				fContentProvider.setNodeVisible(conn.getNode(), true);
			}

			placeAndRoute();
			updateZoom(fZoomWidget.getFactor());

			return aButton == 1;
		}

		RTLSignal s = fLayout.checkHitSignal(mx, my, (int) (1.0 / getZoomFactor()));

		if (s != null) {
			fSelectionLabel.setText(s.getId());
			fSelectionProvider.clear();
			addHighlight(s);
			return aButton == 1;
		}

		RTLNode n = fLayout.checkHitNode(mx, my, (int) (1.0 / getZoomFactor()));

		if (n != null) {
			fSelectionLabel.setText(n.getInstanceName());
			fSelectionProvider.clear();
			addHighlight(n);
			return aButton == 1;
		}

		return aButton == 1;
	}

	public void handleMouseDoubleClick(int aX, int aY) {
		// project coordinates, find out what has been hit
		int mx = aX + fVisibleOffset.x - fOffscreenOffset.x;
		int my = aY + fVisibleOffset.y - fOffscreenOffset.y;

		RTLNode n = fLayout.checkHitNode(mx, my, (int) (1.0 / getZoomFactor()));

		if (n != null) {

			SourceLocation location = n.computeSourceLocation();
			if (location != null) {
				showSource(fPrj, location);
			}
		}

	}

	ColorScheme getColorScheme() {
		return fColorScheme;
	}

	Font getSmallFont() {
		return fSmallFont;
	}

	Font getNormalFont() {
		return fNormalFont;
	}

	Font getBigFont() {
		return fBigFont;
	}

	private void showSource(IProject aPrj, SourceLocation aLocation) {
		IWorkbenchPage page = getViewSite().getPage();
		ZamiaPlugin.showSource(page, aPrj, aLocation, 0);
	}

	private void calcSize(CoolItem aItem) {
		Control control = aItem.getControl();
		Point pt = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		pt = aItem.computeSize(pt.x, pt.y);
		aItem.setSize(pt);
	}

	public Display getDisplay() {
		return display;
	}

	@Override
	public void setFocus() {
	}
}
