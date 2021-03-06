package modules;

import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

import main.Message;
import main.NoiseBot;
import main.NoiseModule;

import static panacea.Panacea.*;
import static modules.Slap.slapUser;
import static org.jibble.pircbot.Colors.*;


/**
 * Spook
 *
 * @author Michael Mrozek
 *         Created Jun 16, 2009.
 */
public class Spook extends NoiseModule {
	private static final String COLOR_ERROR = RED;
	
	private static File SPOOK_FILE = new File("spook.lines");
	
	private Vector<String> lines;
	
	@Override public void init(NoiseBot bot) {
		super.init(bot);
		try {
			this.lines = new Vector<String>();
			final Scanner s = new Scanner(SPOOK_FILE);
			while(s.hasNextLine()) {
				lines.add(substring(s.nextLine(), 0, -1));
			}
		} catch(FileNotFoundException e) {
			this.bot.sendNotice("No spook lines file found");
		}
	}

	@Command("\\.spook ([0-9]+)")
	public void spook(Message message, int num) {
		num = range(num, 1, 20);
		if(num <= this.lines.size()) {
			final Set<String> choices = new LinkedHashSet<String>();
			while(choices.size() < num) {
				// Yuck..
				choices.add(getRandom(this.lines.toArray(new String[0])));
			}
			this.bot.sendMessage(implode(choices.toArray(new String[0]), " "));
		} else {
			this.bot.sendMessage("There are only " + this.lines.size() + " entries in the spook lines file");
		}
	}
	
	@Command("\\.addspook (.*)")
	public void addspook(Message message, String spook) {
		spook = spook.trim();
		if (!spook.matches("^[a-zA-Z0-9][a-zA-Z0-9 _.-]+")) {
			this.bot.sendAction(slapUser(message.getSender()));
		} else if (this.lines.contains(spook)) {
			this.bot.reply(message, COLOR_ERROR + "Message already exists");
		} else {
			try {
				FileWriter writer = new FileWriter(SPOOK_FILE, true);
				writer.append(spook + '\u0000' + '\n');
				writer.close();
				this.lines.add(spook);
				this.bot.reply(message, "Added");
			} catch (Exception e) {
				this.bot.reply(message, COLOR_ERROR + "Error adding to spook file");
			}
		}
	}

	@Command("\\.spook")
	public void spookDefault(Message message) {this.spook(message, 10);}
	
	@Override public String getFriendlyName() {return "Spook";}
	@Override public String getDescription() {return "Displays a random line from the Emacs spook file";}
	@Override public String[] getExamples() {
		return new String[] {
				".spook",
				".spook 15"
		};
	}
	@Override public File[] getDependentFiles() {return new File[] {SPOOK_FILE};}
}
