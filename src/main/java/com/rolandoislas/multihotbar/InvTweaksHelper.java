package com.rolandoislas.multihotbar;

import cpw.mods.fml.common.Loader;
import invtweaks.InvTweaks;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * Created by Rolando on 2/22/2017.
 */
public class InvTweaksHelper {
	private static int disableTicks = 0;
	private static String initialRefillValue = "";

	public static void addDelay() {
		if (!invTweaksLoaded())
			return;
		disableTicks = 80; // ~4 seconds
	}

	private static boolean invTweaksLoaded() {
		return Loader.isModLoaded("inventorytweaks");
	}

	public static void tick() {
		if (!invTweaksLoaded())
			return;
		if (disableTicks > 0) {
			if (initialRefillValue.isEmpty())
				setAutoRefill(false);
			disableTicks--;
		}
		else if (!initialRefillValue.isEmpty()) {
			setAutoRefill(initialRefillValue.equals("true"));
			initialRefillValue = "";
		}
	}

	private static void setAutoRefill(boolean autorefill) {
		if (!invTweaksLoaded())
			return;
		File invConfig = new File(Config.config.getConfigFile().getParent(), "InvTweaks.cfg");
		try {
			FileInputStream in = new FileInputStream(invConfig);
			String stringConfig = IOUtils.toString(in);
			in.close();
			if (stringConfig.isEmpty())
				return;
			if (initialRefillValue.isEmpty())
				initialRefillValue = String.valueOf(stringConfig.contains("enableAutoRefill=true"));
			stringConfig = stringConfig.replace("enableAutoRefill=true", "enableAutoRefill=" + autorefill);
			stringConfig = stringConfig.replace("enableAutoRefill=false", "enableAutoRefill=" + autorefill);
			FileOutputStream out = new FileOutputStream(invConfig);
			IOUtils.write(stringConfig, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			InvTweaks.getConfigManager().getConfig().refreshProperties();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void reset() {
		disableTicks = 0;
		if (!initialRefillValue.isEmpty()) {
			setAutoRefill(initialRefillValue.equals("true"));
			initialRefillValue = "";
		}
	}
}
