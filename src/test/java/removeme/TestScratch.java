/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package removeme;

import com.mongodb.*;
import com.mongodb.util.JSON;

import es.bsc.mongoal.QueryGenerator;
import es.bsc.mongoal.test.ErrorReport;
import junit.framework.TestCase;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.net.UnknownHostException;
import java.util.UUID;
import java.util.logging.Logger;

/**
 *
 * @author mmacias
 */

public class TestScratch extends TestCase {
    public static final String HOST = "localhost";
    public static final int PORT = 27017;
    private String dbName;
    private static final String COLLECTION_NAME = "docs";

    MongoClient client;
    QueryGenerator query;
    DB db;
    DBCollection collection;

    @Override
    public void setUp() throws Exception {

        dbName = "mongoaltest-" + UUID.randomUUID();

        client = new MongoClient(HOST,PORT);
        db = client.getDB(dbName);


        // Insert data

        collection = db.getCollection(COLLECTION_NAME);

        collection.insert((DBObject)JSON.parse("{'val1':2,'obj':{'prop':2,'vec':[4,5,6]}}"));
        collection.insert((DBObject)JSON.parse("{'val1':3,'obj':{'prop':1,'vec':[1,2,3]}}"));
        collection.insert((DBObject)JSON.parse("{'val1':1,'obj':{'prop':3,'vec':[7,8,9]}}"));
        collection.insert((DBObject)JSON.parse("{'val1':20,'obj':{'prop':6,'vec':[16,17,18]}}"));
        collection.insert((DBObject)JSON.parse("{'val1':10,'obj':{'prop':5,'vec':[13,14,15]}}"));
        collection.insert((DBObject)JSON.parse("{'val1':30,'obj':{'prop':7,'vec':[19,20,21]}}"));
        collection.insert((DBObject)JSON.parse("{'val1':0,'obj':{'prop':4,'vec':[10,11,12]}}"));
        collection.insert((DBObject)JSON.parse("{'val1':0,'obj':{'prop':3,'vec':[10,11,12]}}"));
        collection.insert((DBObject)JSON.parse("{'val1':0,'obj':{'prop':7,'vec':[10,11,12]}}"));
        collection.insert((DBObject)JSON.parse("{'val1':0,'obj':{'prop':5,'vec':[10,11,12]}}"));


        query = new QueryGenerator(db);
    }

    @Override
    public void tearDown() throws Exception {
        Logger.getLogger(getClass().getName()).info("Dropping database and closing client...");
        client.dropDatabase(dbName);
        client.close();
    }

    public void igntestGroup() throws Exception {

        // TODO sacadas de aqui
        // max(obj.vec) as maxVector deberia devolver un entero. Devuelve un array vacío
        // si ponemos 'AS ...' dos veces con el mismo identificador, debería dar error
        // que de un error avisando si el SIMPLEID que representa el acumulador no es correcto
        Iterable<DBObject> ret = query.query("FROM docs " +
                        "GROUP BY NOTHING \n" +
                        "avg(val1) AS avg\n"+
                        "max(val1) AS max\n"+
                        "min(val1) as min\n"+
                        "avg(obj.prop) as propAccess\n" +
                        "max(obj.vec) as maxVector" // esto debería dar un valor
        );

        //"or (data.metric > 4 and data.metric < 4.5)");
        for(DBObject dbo : ret) {
            System.out.println(JSON.serialize(dbo));
        }
    }


    public void igntestMatch() throws Exception {
        // TODO
        // Si hacemos OR obj.vec[1] = 20 no devuelve lo que tiene que devoler, pero puesto así sí funciona
        Iterable<DBObject> ret = query.query("FROM docs MATCH val1 = 0 OR obj.vec = 20");
        //"or (data.metric > 4 and data.metric < 4.5)");
        for(DBObject dbo : ret) {
            System.out.println(JSON.serialize(dbo));
        }
    }

    public void ign_testOrderGrammar() throws Exception {
        System.out.println("Test Order Grammar");
        String[] queries = {
                "FROM docs SORT BY val1",
                "FROM docs SORT BY val1 ASCENDING",
                "FROM docs SORT BY val1 DESCENDING",
                "FROM docs SORT BY obj.prop",
                "FROM docs SORT BY obj.vec, janders",
                "FROM docs SORT BY obj.vec[1] DESCENDING",
                "FROM docs SORT BY obj.vec, janders ASCENDING, thing DESCENDING",
        };

        for(String query : queries) {
            assertEquals(ErrorReport.getReport(query).count(), 0);
        }
    }

    public void _testOrderBy() throws Exception {
        // TODO: no funcionan
        // FROM docs SORT BY obj.vec[1] DESCENDING
        String[] queries = {
                "FROM docs SORT BY val1",
                "FROM docs SORT BY val1 ASCENDING",
                "FROM docs SORT BY val1 DESCENDING",
                "FROM docs SORT BY obj.prop",
                "FROM docs SORT BY obj.vec, janders",
                "FROM docs SORT BY obj.vec[1] DESCENDING",
                "FROM docs SORT BY obj.vec, janders ASCENDING, thing DESCENDING",
                "FROM docs SORT BY obj.prop, val1 DESCENDING",
                "FROM docs SORT BY val1 ASCENDING, obj.prop DESCENDING",
        };

        for(String str : queries) {
            System.out.println("***** Executing: " + str);
            for(DBObject dbo : query.query(str)) {
                System.out.println(JSON.serialize(dbo));
            }
        }
    }

    public void testTest() throws  Exception {
        //TODO: GROUP BY val1 AS OrderValue should fail
        String queryString = "FROM docs\n" +
                "MATCH obj.prop > 3 \n" +
                "GROUP BY val1-obj.prop \n" +
                "sum(val1) AS Field1 \n" +
                "sum(obj.prop) AS Field2 \n" +
                "sum(obj.prop + val1) AS FieldsSum \n" +
                "SORT BY _id ASCENDING";
        System.out.println(queryString);
        for(DBObject dbo : query.query(queryString)) {
            System.out.println(JSON.serialize(dbo));
        }

    }
}
