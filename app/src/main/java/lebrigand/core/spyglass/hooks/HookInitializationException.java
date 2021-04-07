package lebrigand.core.spyglass.hooks;

public class HookInitializationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5148915497652687355L;

	public HookInitializationException() {
	}

	public HookInitializationException(String arg0) {
		super(arg0);
	}

	public HookInitializationException(Throwable arg0) {
		super(arg0);
	}

	public HookInitializationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public HookInitializationException(String arg0, Throwable arg1,
			boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
