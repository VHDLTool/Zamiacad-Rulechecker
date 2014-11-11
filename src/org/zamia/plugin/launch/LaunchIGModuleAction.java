/*
 * Copyright 2007, 2009 by the authors indicated in the @author tags.
 * All rights reserved.
 *
 * See the LICENSE file for details.
 *
*/

package org.zamia.plugin.launch;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.zamia.ExceptionLogger;
import org.zamia.ZamiaLogger;
import org.zamia.ZamiaProject;
import org.zamia.plugin.ZamiaProjectMap;
import org.zamia.plugin.views.navigator.IGModuleWrapper;
import org.zamia.vhdl.ast.DMUID;


/**
 * Used to launch an IGNode directly, automatically generates/updates a
 * corresponding launch configuration
 * 
 * @author Guenter Bartsch
 * 
 */

public class LaunchIGModuleAction extends org.eclipse.ui.actions.ActionDelegate {

	public final static ZamiaLogger logger = ZamiaLogger.getInstance();
	public final static ExceptionLogger el = ExceptionLogger.getInstance();
	
	protected IGModuleWrapper fWrapper;

	@Override
	public void selectionChanged(IAction aAction, ISelection aSelection) {

		if (aSelection instanceof IStructuredSelection) {
			Object first = ((IStructuredSelection) aSelection).getFirstElement();

			if (first != null && first instanceof IGModuleWrapper) {
				fWrapper = (IGModuleWrapper) first;
			}
		}
		super.selectionChanged(aAction, aSelection);
	}

	public void run(IAction aAction) {

		if (fWrapper == null)
			return;

		try {
			logger.debug("LaunchIGModuleAction: Launching: " + fWrapper);

			ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();

			ILaunchConfigurationType type = null;

			type = manager.getLaunchConfigurationType("org.zamia.plugin.SimLaunchConfigurationDelegate");

			//System.out.println("Type: " + type);

			ILaunchConfiguration config = null;
			// if the configuration already exists, delete it

			DMUID duuid = fWrapper.getDMUID();
			
			String id = duuid.toString();
			
			List traces = null;
			ILaunchConfiguration[] configurations = manager.getLaunchConfigurations();
			for (int i = 0; i < configurations.length; i++) {
				if (configurations[i].getName().equals(id)) {
					traces = SimRunnerConfig.getTraces(configurations[i]);
					configurations[i].delete();
				}
			}
			// else create a new one
				ILaunchConfigurationWorkingCopy configWC = type.newInstance(null, id);

				ZamiaProject zprj = fWrapper.getZPrj();
				
				IProject prj = ZamiaProjectMap.getProject(zprj);

				configWC.setAttribute(SimRunnerConfig.ATTR_TOPLEVEL, duuid.toString());
				configWC.setAttribute(SimRunnerConfig.ATTR_PROJECT, prj.getName());
				if (traces != null)
					configWC.setAttribute(SimRunnerConfig.ATTR_TRACES, traces);
				config = configWC.doSave();
				
			ILaunch launch = config.launch(ILaunchManager.RUN_MODE, null);
			manager.addLaunch(launch);
			//config.delete();
		} catch (CoreException e) {
			el.logException(e);
		}
	}

}
