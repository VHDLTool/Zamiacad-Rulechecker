package org.zamia.plugin.tool.vhdl.help;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.zamia.ZamiaLogger;
import org.zamia.plugin.ZamiaPlugin;

public class ThreadOpenFeature extends Thread {

	public final static ZamiaLogger logger = ZamiaLogger.getInstance();
	
	@Override
	public void run() {
		
		try {
			ZamiaPlugin.threadCreateFeature.join();
			File pdf = new File("ZamiaCad_Rule_Checker_Features_Description.pdf");
		    Desktop.getDesktop().open(pdf);
		} catch (IOException | InterruptedException e) {
			logger.error("some exception message", e);
			e.printStackTrace();
		}
		
		
	}
	
	
}
