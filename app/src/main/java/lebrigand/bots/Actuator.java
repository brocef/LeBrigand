package lebrigand.bots;

public interface Actuator {
	public void mouseMove(int x, int y);
	
	public void mousePress(int buttons);
	
	public void mouseRelease(int buttons);
	
	public void mouseClick(int buttons);
	
	public void keyPress(int keycode, char c);
	
	public void keyPress(int keycode);
	
	public void keyRelease(int keycode, char c);
	
	public void keyRelease(int keycode);
	
	public void keyType(int keycode, char c);
	
	public void keyType(int keycode);
	
	public void sleep(int millis);
}
