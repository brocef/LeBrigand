package lebrigand.core.ui;

public interface Messenger {
	public void updateBotStatus(String status);
	public void log(String msg);
	public void log(String format, Object...args);
	public void log(Object o);
}
