/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package removeme;

import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author mmacias
 */
@Ignore
public class TestScratch {    
    @Test
    public void testScratch() throws Exception {
       /* MongoClient client;
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
        parser.addParseListener(new QueryGenerator());
        parser.query();

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
                */
    }
    
}
