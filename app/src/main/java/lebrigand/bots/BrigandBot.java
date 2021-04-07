package lebrigand.bots;

import lebrigand.core.ui.Messenger;

public abstract class BrigandBot extends Thread implements Messenger {
	private boolean kill;
	private Messenger msger;
	
	public BrigandBot(Messenger msger) {
		this.setName(getBotName());
		this.setDaemon(true);
		this.kill = false;
		this.msger = msger;
	}
	
	public abstract String getBotName();
	
	public abstract void run();
	
	public void killBot() {
		this.kill = true;
//		this.interrupt();
	}
	
	public boolean stillAlive() {
		return !this.kill;
	}
	
	public void updateBotStatus(String status) {
		msger.updateBotStatus(status);
	}
	
	public void log(String msg) {
		msger.log(msg);
	}
	
	public void log(String format, Object...args) {
		msger.log(format, args);
	}
	
	public void log(Object o) {
		msger.log(o);
	}
}
