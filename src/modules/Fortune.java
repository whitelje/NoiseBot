package modules;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

import debugging.Log;
import main.Message;
import main.NoiseBot;
import main.NoiseModule;

import static org.jibble.pircbot.Colors.*;

import static panacea.Panacea.*;

/**
 * Fortune
 *
 * @author Michael Mrozek
 *         Created Jun 16, 2009.
 */
public class Fortune extends NoiseModule {
	private static File FORTUNE_FILE = new File("fortunes");
	private static final String COLOR_ERROR = RED;
	
	private String[] fortunes;
	
	@Override public void init(NoiseBot bot) {
		super.init(bot);
		try {
			final Vector<String> fortunesVec = new Vector<String>();
			final Scanner s = new Scanner(FORTUNE_FILE);
			while(s.hasNextLine()) {
				fortunesVec.add(s.nextLine());
			}
			this.fortunes = fortunesVec.toArray(new String[0]);
		} catch(FileNotFoundException e) {
			this.bot.sendNotice("No fortune file found");
		}
	}

	@Command("\\.fortune")
	public void fortune(Message message) {
		this.bot.sendMessage(getRandom(this.fortunes));
	}

	@Command("\\.fortune (.*)")
	public void fortune(Message message, String keyword) {
		try {
			this.bot.sendMessage(getRandomMatch(this.fortunes, ".*" + keyword + ".*"));
		} catch (Exception e) {
			this.bot.reply(message, COLOR_ERROR + "No matches");
			e.printStackTrace();
		}
	}
	
	@Override public String getFriendlyName() {return "Fortune";}
	@Override public String getDescription() {return "Displays a random fortune from the Plan 9 fortune file";}
	@Override public String[] getExamples() {
		return new String[] {
				".fortune"
		};
	}
	@Override public File[] getDependentFiles() {return new File[] {FORTUNE_FILE};}
}
