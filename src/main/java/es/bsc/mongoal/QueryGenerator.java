/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.mongoal;

import org.antlr.v4.runtime.CommonTokenStream;

import java.util.List;

/**
 * Interface to the MongoAL parser. It translates MongoAL strings and sends requests to the MongoDB database.
 */
public class QueryGenerator {


	String jsonQuery;
	String collectionName;

	public QueryGenerator(String mongoAlQueryString) {
		QueryVisitor queryVisitor = new QueryVisitor();
		MongoALLexer lexer = new MongoALLexer(new org.antlr.v4.runtime.ANTLRInputStream(mongoAlQueryString));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		MongoALParser parser = new MongoALParser(tokens);
		jsonQuery = queryVisitor.visitQuery(parser.query()).toString();
		collectionName = queryVisitor.getCollectionId();
	}

	public String getJsonQueryString() {
		return jsonQuery;
	}

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
