/*
 * Copyright 2006-2010 by the authors indicated in the @author tags.
 * All rights reserved.
 *
 * See the LICENSE file for details.
 *
 * Created by guenter on Apr 16, 2006
 */

package org.zamia.plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.internal.resources.ICoreConstants;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.zamia.ERManager;
import org.zamia.ExceptionLogger;
import org.zamia.SourceFile;
import org.zamia.ZamiaException;
import org.zamia.ZamiaLogger;
import org.zamia.ZamiaProject;
import org.zamia.ZamiaProjectBuilder;
import org.zamia.plugin.build.ZamiaBuilder;
import org.zamia.plugin.build.ZamiaErrorObserver;
import org.zamia.zdb.ZDBException;


/**
 * 
 * @author Guenter Bartsch
 * 
 */

public class ZamiaProjectMap {

	public final static ExceptionLogger el = ExceptionLogger.getInstance();

	protected final static ZamiaLogger logger = ZamiaLogger.getInstance();

	private static HashMap<IProject, ZamiaProject> fZPrjs = new HashMap<IProject, ZamiaProject>();

	private static HashMap<ZamiaProject, IProject> fIPrjs = new HashMap<ZamiaProject, IProject>();

	public static class EclipseProjectFileIterator extends ZamiaProject.FileIterator {
		
		private final IProject project;
		
		/** 
		 Normally, we would iterate all project directories and collect a list of files 
		 when requested. This is necessary to support virtual directories (file linking).
		 Yet, we must also clean up zamia build errors when corresponing files are removed. 
		 And, because of eclipse bug 367021, we must keep an extra list of project files
		 so that when link is deleted, we could locate its absolute path. 
		
  		 Maps local project path => absolute path  		 */
		public final Map<String, String> projectFiles = new HashMap<String, String>();
		
		EclipseProjectFileIterator(String baseDir, IProject aProject) throws IOException {
			super(baseDir);
			project = aProject;
			try {
				iterateFolder(project.members());
			} catch (CoreException e) {
				throw new IOException("Failed to iterate project files", e);
			}
		}
		
		public SourceFile toSF(File absoluteLocation) throws ZamiaException {
			for (String key : projectFiles.keySet()) {
				String value = projectFiles.get(key);
				if (new File(value).equals(absoluteLocation))
					return new SourceFile(absoluteLocation, key);
			}
			throw new ZamiaException("Failed to locate " + absoluteLocation + " in the project");
		}

		/** Returns all links that refer the same absolute path as the argument */
		public Set<IFile> getFiles(IFile local) {
			SourceFile sf = ZamiaPlugin.getSourceFile(local);
			Set<IFile> accumulator = new HashSet<IFile>();
			for (String key : projectFiles.keySet()) {
				String value = projectFiles.get(key);
				if (value.equals(sf.getAbsolutePath()))
					accumulator.add(project.getFile(key));
			}
			return accumulator;
		}
		public File[] getFiles() throws IOException, ZamiaException {
			File[] result = new File[projectFiles.size()];
			String[] s = projectFiles.values().toArray(new String[result.length]);
			for (int i = 0 ; i != result.length ; i++)
				result[i] = new File(s[i]);
			return result;
		}

		//in normal case we would use this for getFiles(). Now we have to use 
		// the iteration on startup to initialize the projectFiles
		public void iterateFolder(IResource[] folder) throws CoreException {
			
			for (IResource r : folder) {
				
				if (r instanceof IFolder)
					iterateFolder(((IFolder) r).members());
				else
					add(ZamiaPlugin.getSourceFile((IFile) r));
			}
				
		}

		private void add(SourceFile sf) {
			
			if (sf.getLocalPath().endsWith("BuildPath.txt") ||
					(
					ZamiaProjectBuilder.fileNameAcceptable(sf.getLocalPath())  && // line name will satisfy user, referenced file 
					ZamiaProjectBuilder.fileNameAcceptable(sf.getFileName())) // must have extension acceptable by compiler
					)
				projectFiles.put(sf.getLocalPath(), sf.getAbsolutePath());			
		}
		
		//we are notified when files are added/removed. Sf is produced from delta resource.
		public void listChanged(IResourceDelta aDelta, SourceFile sf) {
			switch (aDelta.getKind()) {
			case IResourceDelta.ADDED:
				add(sf); 
				break;
				
			case IResourceDelta.REMOVED: 
				// due to Eclipse bug 367021, IFile.getLocation() is garbage here and we cannot 
				// delete ZDB errors using it. We must entail the projectFiles map and remove 
				// errors when last link to resource is deleted.
				
				String abs = projectFiles.remove(sf.getLocalPath()); // local location must be ok

				if (abs == null) // this is not our file -- forget it
					return;
				
				//if no links to this file remains, clear file errors in Zamia
				if (!projectFiles.values().contains(abs)) {
					sf.setFile(new File(abs)); // fix the absolute location
					getZamiaProject().getERM().removeErrors(sf);
				}
				break;
			}
			
		}
		
		ZamiaProject getZamiaProject() {
			return ZamiaProjectMap.getZamiaProject(project);
		}
	}
	
	public static ZamiaProject getZamiaProject(final IProject aProject) {

		ZamiaProject zprj = fZPrjs.get(aProject);

		if (zprj == null) {
			try {

				String baseDir = aProject.getLocation().toOSString();
				
				String path = ZamiaBuilder.getPersistentBuildPath(aProject);
				SourceFile bpsf = null;				
				if (path != null) {
					IFile resource = (IFile) aProject.findMember(path);
					if (resource != null)
						bpsf = ZamiaPlugin.getSourceFile(resource);
				}
					
				if (bpsf == null) {
					String localPath = "BuildPath.txt";
					bpsf = new SourceFile(new File(baseDir + File.separator + localPath), localPath);
				}

				while (zprj == null) {

					try {
						zprj = new ZamiaProject(aProject.getName(), new EclipseProjectFileIterator(baseDir, aProject), bpsf, null);
						
					} catch (ZDBException e) {

						File lockfile = e.getLockFile();

						int answer = ZamiaPlugin.askQuestion(null, "Lockfile exists", "A lockfile for project\n\n" + aProject.getName() + "\n\nalready exists:\n\n"
								+ lockfile.getAbsolutePath() + "\n\nAnother instance of zamiaCAD is probably running.", SWT.ICON_ERROR | SWT.ABORT | SWT.RETRY | SWT.IGNORE);

						switch (answer) {
						case SWT.ABORT:
							logger.info("ZamiaProjectMap: Shutting down because lockfile was in the way.");
							try {
								PlatformUI.getWorkbench().close();
							} catch (Throwable t) {
								el.logException(t);
							}
							System.exit(1);

						case SWT.RETRY:
							break;

						case SWT.IGNORE:
							logger.info("ZamiaProjectMap: deleting lockfile '%s'.", lockfile.getAbsolutePath());
							lockfile.delete();
							break;
						}
					}
				}

				fZPrjs.put(aProject, zprj);
				fIPrjs.put(zprj, aProject);

				// hook up error observer

				ERManager erm = zprj.getERM();
				erm.addObserver(new ZamiaErrorObserver(aProject));

			} catch (ZamiaException e1) {
				el.logException(e1);
			} catch (IOException e) {
				el.logException(e);
			}
		}
		return zprj;
	}

	public static IProject getProject(ZamiaProject aZPrj) {
		return fIPrjs.get(aZPrj);
	}

	public static void remove(IProject aPrj) {
		ZamiaProject zprj = getZamiaProject(aPrj);
		fZPrjs.remove(aPrj);
		fIPrjs.remove(zprj);
	}

	public static void shutdown() {
		long startTime = System.currentTimeMillis();
		for (ZamiaProject zprj : fIPrjs.keySet()) {
			System.out.println("Shutting down project " + zprj + "...");
			zprj.shutdown();
			System.out.println("Shutting down project " + zprj + "...done.");
		}
		long stopTime = System.currentTimeMillis();
		double d = ((double) stopTime - startTime) / 1000.0;
		System.out.printf("Shutdown took %fs.\n", d);
	}

}
