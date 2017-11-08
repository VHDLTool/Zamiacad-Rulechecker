package org.zamia.plugin.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.zamia.BuildPath;
import org.zamia.BuildPathEntry;
import org.zamia.SourceFile;
import org.zamia.SourceLocation;
import org.zamia.Utils;
import org.zamia.ZamiaLogger;
import org.zamia.ZamiaProject;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.ZamiaProjectMap;

/** Every line in the PRJ file has a from 
 *		vhdl lib_name "foo.vhdl"
 *
 * A lot of files are mapped into the same lib. Importing, we
 * first, create a <lib_folder> and link all library files there.
 * It is an error if such folder already exists.  
 * Secondly, the folder is mapped awhole to the lib by adding 
 * 'local lib "<lib_folder>"'
 * entry in the BuildPath.txt.
 */

//extend ZDBImportWizard because we need similar project-file input
public class XilinxPrjImportWizard extends ZDBImportWizard {

	public String getWindowTitle() {
		return "Xilinx PRJ import";
	}
	
	protected ZDBImportWizardPage1 createZDBImportWizardPage1() {
		return new ZDBImportWizardPage1(fSelection) {
			public String getTitle() {return "Import PRJ File";}
			public String getDescription() {return "This wizard imports a PRJ file."; }
			protected String getFileOpenDialogTitle() {return "Import PRJ...";}
			protected String getFileOpenDialogFilter() {return "*.prj";}		};
	}
	
	//TODO: call this function when prj is dropped into the project
	public static void importXilinxPrj(final IProject iprj, final String fileName) {

		final ZamiaProject zprj = ZamiaProjectMap.getZamiaProject(iprj);
		
		Job job = new Job("Importing PRJ from " + fileName + " to " + zprj) {
			
			ZamiaLogger logger = ZamiaPlugin.logger;
			
			@SuppressWarnings("deprecation")
			protected IStatus run(final IProgressMonitor monitor) {

				
				logger.info("parsing PRJ = " + fileName);
				
				Set<String> libs = new HashSet<>();
				
				// Step 1: Parse PRJ, create lib folders and link vhdl files into them 
				try (BufferedReader b = new BufferedReader(new FileReader(fileName))) {
					String line;
					while((line = b.readLine()) != null) {
						line = line.trim();
						if (line.length() == 0)
							continue;
						
						String[] segments = line.split(" ");
						if (segments.length != 3) {
							logger.warn("Line (%s) has wront format, must be VHDL LIB_NAME \"vhdl_file\"", line);
							continue;
						}
						
						String srcPath = segments[2].replace("\"", "");
						String lib = segments[1].toUpperCase();
						
						File srcFile = new File(srcPath);
						String name = srcFile.getName();
						
						if (lib.toLowerCase().equals("work")) {
							logger.info("ignoring %s in work", name);
							continue;
						}
						
						IFolder folder = iprj.getFolder(lib);
						boolean contained = !libs.add(lib); 
						if (!contained) {
							if (folder.exists()) { // it is bad if folder already exists and is compiled into a wrong library
								new BPEntryHandler(lib) {
									void mapConflict(BuildPathEntry bpe, String lib) {
										logger.warn("Folder (" + lib + ") exists already and is mapped to " + bpe.fLibId + " instead of " + lib + "!");
									}
								};
							} else
								folder.create(false, true, monitor);
						}
						
						if (!srcFile.exists()) {
							logger.warn("Going to link non existent file %s", srcPath);
						}
						
						IFile ifile = folder.getFile(name);
						URI iUri = ifile.getLocationURI();
						URI srcUri = srcFile.toURI();
						if (ifile.exists()) {
							
							if (!iUri.equals(srcUri))
								logger.warn(ifile + " file is already linked to " + iUri + " instead of " + srcUri);
//							else 
//								logger.warn(ifile + " already links to " + srcUri + " = " + iUri);
						} else
							ifile.createLink(srcUri, IResource.NONE, monitor);
						
					}
					
				} catch (IOException | CoreException e) {
					ZamiaPlugin.el.logException(e);
				}

				// Step 2: append lib_folder => library mappings in BP

				final StringBuilder sb = new StringBuilder();
				
				for (String lib: libs) {
					new BPEntryHandler(lib) {
						void map(String lib) {
							sb.append("local " + lib  + " \"" + lib + "\"\n");
						}
						void freeToMap(String lib) {
							logger.info("appending " + lib + " to BP");
							map(lib);
						}
						void mapConflict(BuildPathEntry bpe, String lib) {
							logger.warn(bpe.fPrefix + " is alredy mapped to " + bpe.fLibId + " instead of our " + lib);				
							sb.append("#Commented because of conflict with (" + bpe.fPrefix + " => " + bpe.fLibId + ")\n");
							sb.append("#"); map(lib);
							
						}
						
						void mappedAlreadyProperly(BuildPathEntry bpe) {
							//logger.info(bpe.fPrefix + " is already mapped to " + bpe.fLibId);
						}
						
					};
				}
				
				
				final String bpPath = zprj.getBuildPath().getSourceFile().getLocalPath();
				final IFile bpIFile = iprj.getFile(bpPath);

				if (sb.length() != 0) // if there is anything to map
					Display.getDefault().asyncExec(new Runnable() {
                     public void run() {
                    	 
     					sb.insert(0, "\n#  PRJ import: " + fileName + "\n");
    					sb.append("#- - - - - - import end - - - - - - \n");
    					
						IWorkbenchPage page = ZamiaPlugin.getWorkbenchWindow().getActivePage();
                    	 try {
                    		 ITextEditor editor = (ITextEditor) IDE.openEditor(page, bpIFile);
                    		 IEditorInput input = editor.getEditorInput();
                    		 IDocument doc = editor.getDocumentProvider().getDocument(input);
                    		 sb.insert(0, doc.get());
                    		 
                    		 String bpText = sb.toString();
                    		 doc.set(bpText);
                    		 logger.info("PRJ import finished: Library folders created, linked files to them and mapped to be compiled into libraries in the BuildPath");
                    		 
                    		 //saving does not help to force update (parse) of buildpath file
                    		 editor.getDocumentProvider().saveDocument(monitor, input, doc, false);
						} catch (CoreException e) {
							ZamiaPlugin.el.logException(e);
						}                    	 
                     }
                  });
               
				
				return Status.OK_STATUS;
			}

			class BPEntryHandler {
				
				void freeToMap(String lib) {}
				void mapConflict(BuildPathEntry bpe, String lib) {}
				void mappedAlreadyProperly(BuildPathEntry bpe) {}
				
				BPEntryHandler (String lib) {
					String fakeName = lib + "/";
					BuildPathEntry bpe = zprj.getBuildPath().findEntry(new SourceFile(new File(fakeName), fakeName));
					if (bpe.fPrefix != null) {
						if (bpe.fLibId.equals(lib))
							mappedAlreadyProperly(bpe);
						else 
							mapConflict(bpe, lib);
							
					} else freeToMap(lib);
					
					
				}
			}
		};

		job.setPriority(Job.LONG);
		job.schedule();

	}
	
	@Override
	public boolean performFinish() {

		ZamiaPlugin.showConsole();

		IProject prj = fMainPage.getProject();
		String fileName = fMainPage.getFileName();
		importXilinxPrj(prj, fileName);
		
		return true;
	}

}
