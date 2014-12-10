/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.mongoal;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.List;

/**
 * Interface to the MongoAL parser. It translates MongoAL strings and sends requests to the MongoDB database.
 */
public class QueryGenerator  {

    DB database;
    QueryVisitor queryVisitor;

    /**
     * Instantiates a new Query Generator.
     * @param database Connection to the MongoDB database to send the queries to
     */
    public QueryGenerator(DB database) {
        this.database = database;
        queryVisitor = new QueryVisitor();
    }

    /**
     * Sends a query to the MongoDB database
     * @param queryString MongoAL query
     * @return An iterable collection of MongoDB DBobjects with the results of the query
     */
    public Iterable<DBObject> query(String queryString) {
        MongoALLexer lexer = new MongoALLexer(new org.antlr.v4.runtime.ANTLRInputStream(queryString));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MongoALParser parser = new MongoALParser(tokens);
        Object[] ret = (Object[]) queryVisitor.visitQuery(parser.query());
        System.out.println(JSON.serialize(ret[1]));
        DBCollection events = database.getCollection((String)ret[0]);
        if(ret[1] == null) {
            return events.find();
        } else {
            return events.aggregate((List<DBObject>) ret[1]).results();
        }
    }

}
