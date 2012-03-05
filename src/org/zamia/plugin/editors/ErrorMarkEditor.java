package org.zamia.plugin.editors;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.ui.ProblemsLabelDecorator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.ResourceUtil;

public class ErrorMarkEditor extends TextEditor {

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
    
}
