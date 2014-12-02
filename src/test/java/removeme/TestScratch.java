/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package removeme;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;
import es.bsc.mongoal.MongoALLexer;
import es.bsc.mongoal.MongoALParser;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;

/**
 *
 * @author mmacias
 */
public class TestScratch {    
    @Test
    public void testScratch() throws Exception {
        MongoClient client;
        DB db;
        DBCollection events;
        
        String host = "localhost";
        int port = 27017;
        String dbName = "applicationmonitor";
        
        client = new MongoClient(host,port);
        db = client.getDB(dbName);
        
        String queryString = "FROM events";
        
        // parse the query
        MongoALLexer lexer = new MongoALLexer(new org.antlr.v4.runtime.ANTLRInputStream(queryString));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		MongoALParser parser = new MongoALParser(tokens);
        
        Object nameAndQuery[] = parser.query().collectionAndQuery;
        
        String collName = (String) nameAndQuery[0];
        List<DBObject> pipe = (List<DBObject>) nameAndQuery[1];
        
        
        events = db.getCollection("events");
         
        AggregationOutput out;
        
        Iterable<DBObject> iterable;
        
        //pipe.add((DBObject) JSON.parse("$match : { data.metric: { $gt: 9 } }}"));
        
        if(pipe.size() > 0) {
            iterable = events.aggregate(pipe).results();
        } else {
            iterable = events.find();
        }
        
        for(DBObject res : iterable) {
            System.out.println(res.toString());
        }
        
        client.close();
        
        ArrayNode an = new ArrayNode(JsonNodeFactory.instance);
        System.out.println(an.toString());
    }
    
}
