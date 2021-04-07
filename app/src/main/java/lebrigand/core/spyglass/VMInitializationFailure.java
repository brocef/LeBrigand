package lebrigand.core.spyglass;

public class VMInitializationFailure extends Exception {
	/**
	 * Eclipse dun this for us
	 */
	private static final long serialVersionUID = 6632526939667887155L;

	protected VMInitializationFailure(String message) {
		super(message);
	}
	
	protected VMInitializationFailure() {
		super();
	}
}
