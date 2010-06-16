package uima.wikipedia.parser;

public class MWParseException extends Exception {
	private static final long	serialVersionUID	= 1L;

	public MWParseException() {
		super();
	}

	public MWParseException(String message) {
		super(message);
	}

	@Override
	public String getMessage() {
		return super.getMessage();
	}
}
