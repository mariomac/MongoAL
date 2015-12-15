/*
 * This code is distributed under a Beer-Ware license: Mario Macías wrote it. Considering this, you can do whatever
 * you want with it: modifying, resdistributing, selling... But you always must credit me as an author of the code.
 * In addition, if you meet me someday and think this code has been useful for you, you are obligued to buy me a
 * beer as a payment for my contribution. If it's possible, a good beer.
 */

package es.bsc.mongoal;

/**
 * Exceptions that may be thrown from MongoAL during the parsing process.
 * @author Mario Macías
 */
public class MongoQueryException extends RuntimeException {

	/**
	 * @see {@link Exception#Exception()}
	 */
	public MongoQueryException() {
		super();
	}

	/**
	 * @see {@link Exception#Exception(String)}
	 */
	public MongoQueryException(String message) {
		super(message);
	}

	/**
	 * @see {@link Exception#Exception(String,Throwable)}
	 */
	public MongoQueryException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @see {@link Exception#Exception(Throwable)}
	 */
	public MongoQueryException(Throwable cause) {
		super(cause);
	}

}
