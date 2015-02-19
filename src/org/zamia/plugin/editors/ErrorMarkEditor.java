package org.zamia.plugin.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.ui.ProblemsLabelDecorator;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.ResourceUtil;
import org.zamia.plugin.ZamiaPlugin;

public class ErrorMarkEditor extends TextEditor {

	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		setEditorContextMenuId("#BlockCommentTextEditorContext"); //$NON-NLS-1$
	}
	
	@Override
	protected void initializeKeyBindingScopes() {
		setKeyBindingScopes(new String[] { "org.zamia.plugin.TextEditorScope" });
	}

	IResource getResource() {
		return ResourceUtil.getResource(getEditorInput());
	}
	
	{
		ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener() {

			public void resourceChanged(IResourceChangeEvent event) {
				IResource file = getResource();
				IResourceDelta delta= event.getDelta();
				if (delta != null && file != null) {
					IResourceDelta child = delta.findMember(file.getFullPath());
					if (child != null && (child.getFlags() & IResourceDelta.MARKERS) != 0) {
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								firePropertyChange(IWorkbenchPart.PROP_TITLE);
							}
						});
					}
				}
			}
			
		}, IResourceChangeEvent.POST_CHANGE);
	}
    public Image getTitleImage() {
    	final Image image = super.getTitleImage();
    	IResource file = getResource();
    	return (file != null) ? new ProblemsLabelDecorator().decorateImage(image, file) : image;  
    }
    
	public ISourceViewer getMySourceViewer() {
		return getSourceViewer();
	}

	public SourceViewerConfiguration getSourceViewerCfg() {
		return getSourceViewerConfiguration();
	}

	// occurrences highilghted under cursor marker stuff
	class SelectionChangedListener extends AbstractSelectionChangedListener {
		
		String SELECTED_WORD_OCCURRENCES = "org.eclipse.jdt.ui.occurrences";
		
		final IWordDetector wd;
		SelectionChangedListener(IWordDetector wd) {
			this.wd = wd;
		}
		
		private void annotate(Collection<Integer> occurrences, int len, String text) {

			// Prevent overlap of annotations
			//removeAnnotaionByOffset(zamiaEditor, offsetOfSelectedWord);
			IAnnotationModel annotationModel = ErrorMarkEditor.this.getDocumentProvider().getAnnotationModel(ErrorMarkEditor.this.getEditorInput());
			for (Iterator<Annotation> annotationIterator = 
					annotationModel.getAnnotationIterator(); annotationIterator.hasNext();) {
				Annotation toBeRemoved = annotationIterator.next();
				boolean isOurAnnotation = toBeRemoved.getType().equals(SELECTED_WORD_OCCURRENCES);
				if (isOurAnnotation) {
					annotationModel.removeAnnotation(toBeRemoved);
				} 
			}

			for (Integer offset : occurrences) {
				//log("added %s as (%s,%s)", m.code, off, len);
				annotationModel.addAnnotation(new Annotation(SELECTED_WORD_OCCURRENCES
						, false, text), new Position(offset, len)); 
				//getPreviousAnnotationsRegister().put(offset, annotation);

			};
		}
		
		void log(String format, Object... args) { ZamiaPlugin.logger.info(format, args); }
		
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			TextSelection ts = (TextSelection) event.getSelection();
			try {
				//log("text selection(%s, %s) = '%s', proveider = %s, source=%s", ts.getOffset(), ts.getLength(), ts.getText(), event.getSelectionProvider(), event.getSource());
				
				String docText = ErrorMarkEditor.this.getSourceViewer().getDocument().get();
				
				// find identifier under cursor
				int start = ts.getOffset(); for (; start > 0  && wd.isWordStart(docText.charAt(start-1)); start--);
				Function<Integer, Integer> findWordEnd = (end) -> { for (; end < docText.length() && wd.isWordPart(docText.charAt(end)); end++); return end;};
				int end = findWordEnd.apply(ts.getOffset());
				//log("start = %s, end = %s", start, end);
				BiFunction<Integer, Integer, String> substring = (begin, stop) -> {
					String result = docText.substring(begin, stop);
					//return wd.ignoreCase() ? result.toUpperCase() : result;
					return result;
				};
				String selectedId = substring.apply(start, end); 
				
				int len = end-start; 
						
				// find occurrences
				List<Integer> occurrences = new ArrayList<>();
				for (int occurs = 0 ; occurs < docText.length(); ) {
					end = findWordEnd.apply(occurs);
					//log("scanning at %s", occurs);
					if (occurs != end) {
						//log("word %s-%s", occurs, end);
						if (substring.apply(occurs, end).equals(selectedId)) 
							occurrences.add(occurs);
						occurs = end;
					} else occurs += 1;
				}
				
				if (len > 0) annotate(occurrences, len, "Occurrence of '"+selectedId+"'");

			} catch (Exception ex) {
			}
		}

	}

	
}
