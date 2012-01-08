package org.zamia.plugin.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.ZamiaProjectMap;
import org.zamia.plugin.build.ZamiaBuilder;

public class BuildPathDecorator extends LabelProvider implements ILabelDecorator
{
	// Method to decorate Image 
	public Image decorateImage(Image image, Object object)
	{
	  return null;
	}
	// Method to decorate Text
	public String decorateText(String label, Object object)
	{
		if (object instanceof IFile) {
			IFile file = (IFile) object;
			//String peristentBp = ZamiaBuilder.getPersistentBuildPath(file.getProject());
			String persistentBp = ZamiaProjectMap.getZamiaProject(file.getProject()).getBuildPath().getSourceFile().getLocalPath();
			if (ZamiaPlugin.computeLocalPath(file).equals(persistentBp)) {
				return "*active* "+label + "";
			}
		}
		return null;

	}
}
