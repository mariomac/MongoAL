/*
 * This code is distributed under a Beer-Ware license: Mario Macías wrote it. Considering this, you can do whatever
 * you want with it: modifying, resdistributing, selling... But you always must credit me as an author of the code.
 * In addition, if you meet me someday and think this code has been useful for you, you are obligued to buy me a
 * beer as a payment for my contribution. If it's possible, a good beer.
 */
package es.bsc.mongoal;

import org.antlr.v4.runtime.CommonTokenStream;

import java.util.List;

/**
 * This class contains information of a MongoDB query, formed from a MongoAL query language string.
 *
 * @autor Mario Macías (http://github.com/mariomac)
 */
public class MongoQuery {


	private String jsonQuery;
	private String collectionName;

	private MongoQuery(String jsonQuery, String collectionName) {
		this.jsonQuery = jsonQuery;
		this.collectionName = collectionName;
	}

	/**
	 * Creates a {@link MongoQuery} object from a MongoAL query string.
	 * @param mongoAlQueryString MongoAL query string
	 * @return MongoDB query object
	 */
	public static MongoQuery translateQuery(String mongoAlQueryString) {
		QueryVisitor queryVisitor = new QueryVisitor();
		MongoALLexer lexer = new MongoALLexer(new org.antlr.v4.runtime.ANTLRInputStream(mongoAlQueryString));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		MongoALParser parser = new MongoALParser(tokens);
		String jsonQuery = queryVisitor.visitQuery(parser.query()).toString();
		String collectionName = queryVisitor.getCollectionId();

		return new MongoQuery(jsonQuery, collectionName);
	}

	/**
	 * Returns the parsed JSON, MongoDB query string, translated from the MongoAL query string.
	 * @return The parsed JSON, MongoDB query string, translated from the MongoAL query string.
	 */
	public String getJsonQueryString() {
		return jsonQuery;
	}

	/**
	 * Returns the collection name where the query string must be applied, as parsed from the MongoAL query string.
	 * @return The collection name where the query string must be applied, as parsed from the MongoAL query string.
	 */
	public String getCollectionName() {
		return collectionName;
	}

	@Override
	public String toString() {
		return "QueryGenerator{" +
				"jsonQuery='" + jsonQuery + '\'' +
				", collectionName='" + collectionName + '\'' +
				'}';
	}
}
