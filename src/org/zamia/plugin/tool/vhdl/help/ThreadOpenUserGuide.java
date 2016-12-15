package org.zamia.plugin.tool.vhdl.help;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.zamia.ZamiaLogger;
import org.zamia.plugin.ZamiaPlugin;

public class ThreadOpenUserGuide extends Thread {

	public final static ZamiaLogger logger = ZamiaLogger.getInstance();
	
	@Override
	public void run() {
		
		try {
			ZamiaPlugin.threadCreateUserGuide.join();
			File pdf = new File("ZamiaCad_Rule_Checker_User_Guide.pdf");
		    Desktop.getDesktop().open(pdf);
		} catch (IOException | InterruptedException e) {
			logger.error("some exception message", e);
			e.printStackTrace();
		}
		
		
	}
	
	
}
