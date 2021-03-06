package modules;

import static org.jibble.pircbot.Colors.*;
import debugging.Log;

import panacea.MapFunction;

import main.Git;
import main.Message;
import main.ModuleLoadException;
import main.ModuleSaveException;
import main.ModuleUnloadException;
import main.NoiseModule;
import main.Git.SyncException;
import static panacea.Panacea.*;

/**
 * ModuleManager
 *
 * @author Michael Mrozek
 *         Created Jun 14, 2009.
 */
public class ModuleManager extends NoiseModule {
	private static final String MODULE_REGEX = "[a-zA-Z0-9_ -]+";
	
	private static final String COLOR_ERROR = RED + REVERSE;
	private static final String COLOR_HASH = BLUE;
	private static final String COLOR_AUTHOR = BLUE;
	private static final String COLOR_DESCRIPTION = RED;
	
	@Command("\\.load (" + MODULE_REGEX + ")")
	public void loadModules(Message message, String moduleNames) {
		for(String moduleName : moduleNames.split(" ")) {
			try {
				this.bot.loadModule(moduleName);
				try {
					this.bot.saveModules();
				} catch(ModuleSaveException e) {
					Log.e(e);
				}
				
				this.bot.sendNotice("Module " + Help.COLOR_MODULE + moduleName + NORMAL + " loaded");
			} catch(ModuleLoadException e) {
				this.bot.sendNotice(COLOR_ERROR + e.getMessage());
			}
		}
	}

	@Command("\\.unload (" + MODULE_REGEX + ")")
	public void unloadModules(Message message, String moduleNames) {
		for(String moduleName : moduleNames.split(" ")) {
			if(moduleName.equals("ModuleManager")) {
				this.bot.sendNotice(COLOR_ERROR + ".unload cannot unload the ModuleManager");
				continue;
			}
			
			try {
				this.bot.unloadModule(moduleName);
				try {
					this.bot.saveModules();
				} catch(ModuleSaveException e) {
					Log.e(e);
				}

				this.bot.sendNotice("Module " + Help.COLOR_MODULE + moduleName + NORMAL + " unloaded");
			} catch(ModuleUnloadException e) {
				this.bot.sendNotice(COLOR_ERROR + e.getMessage());
			}
		}
	}

	@Command("\\.reload (" + MODULE_REGEX + ")")
	public void reloadModules(Message message, String moduleNames) {
		this.unloadModules(message, moduleNames);
		this.loadModules(message, moduleNames);
	}
	
	@Command("\\.rev")
	public void rev(Message message) {
		final Git.Revision rev = this.bot.revision;
		this.bot.reply(message, "Currently on revision " + COLOR_HASH + rev.getHash() + NORMAL + " by " + COLOR_AUTHOR + rev.getAuthor() + NORMAL + " -- " + COLOR_DESCRIPTION + rev.getDescription() + NORMAL);
		this.bot.reply(message, Git.revisionLink(rev));
	}

	@Override public String getDescription() {return "Manages modules";}
	@Override public String getFriendlyName() {return "Module Manager";}
	@Override public String[] getExamples() {
		return new String[] {
				".load _module_ -- Loads the specified module",
				".unload _module_ -- Unloads the specified module",
				".reload _module_ -- Unloads and then loads the specified module",
				".sync _branch_ -- Fast-forward to the specified branch, and reload any modified modules",
				".co _branch_ -- Same as .sync",
				".rev -- Show the current revision"
		};
	}
}
