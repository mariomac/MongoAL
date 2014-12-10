package es.bsc.mongoal;

/**
 * Created by mmacias on 3/12/14.
 */
public class QueryException extends RuntimeException {
	public QueryException() {
		super();
	}

	public QueryException(String message) {
		super(message);
	}

	public QueryException(String message, Throwable cause) {
		super(message, cause);
	}

	public QueryException(Throwable cause) {
		super(cause);
	}

}
