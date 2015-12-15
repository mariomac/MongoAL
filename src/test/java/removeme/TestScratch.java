/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package removeme;

import com.mongodb.util.JSON;

import es.bsc.mongoal.MongoQuery;
import junit.framework.TestCase;

/**
 *
 * @author mmacias
 */

public class TestScratch extends TestCase {

    public void testGroup() throws Exception {

        // TODO sacadas de aqui
        // max(obj.vec) as maxVector deberia devolver un entero. Devuelve un array vacío
        // si ponemos 'AS ...' dos veces con el mismo identificador, debería dar error
        // que de un error avisando si el SIMPLEID que representa el acumulador no es correcto
		JSON.parse(MongoQuery.translateQuery("FROM docs " +
				"GROUP BY NOTHING \n" +
				"avg(val1) AS avg\n"+
				"max(val1) AS max\n"+
				"min(val1) as min\n"+
				"avg(obj.prop) as propAccess\n" +
				"max(obj.vec) as maxVector").getJsonQueryString());
    }


    public void testMatch() throws Exception {
        // TODO
        // Si hacemos OR obj.vec[1] = 20 no devuelve lo que tiene que devoler, pero puesto así sí funciona
		System.out.println("FROM docs MATCH val1 < 1418551830051 OR obj.vec = 20");
		JSON.parse(MongoQuery.translateQuery("FROM docs MATCH val1 < 1418551830051 OR obj.vec = 20").getJsonQueryString());
    }

    public void testOrderGrammar() throws Exception {
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
		for(String str : queries) {
			System.out.println(MongoQuery.translateQuery(str));
			JSON.parse(
					MongoQuery.translateQuery(str).getJsonQueryString());
		}
    }

    public void testOrderBy() throws Exception {
        // TODO: no funcionan
        // FROM docs SORT BY obj.vec[1] DESCENDING
        String[] queries = {
				"FROM events MATCH appId = 'SinusApp'  AND timestamp > 1431589934576 AND timestamp <= 1431589944576 GROUP BY NOTHING  avg(data.metric) as metric",
				"FROM docs MATCH ajarr > 3 AND ajarr < 4",
				"FROM DOCS MATCH ajurr > 3 OR ajurr <4",
				"FROM DOCS MATCH (quepasanen <= 3 OR quepasatio > 3) AND tralari = \"hola\"",
                "FROM docs SORT BY val1",
                "FROM docs SORT BY val1 ASCENDING",
                "FROM docs SORT BY val1 DESCENDING",
                "FROM docs SORT BY obj.prop",
                "FROM docs SORT BY obj.vec, janders",
                "FROM docs SORT BY obj.vec[1] DESCENDING",
                "FROM docs SORT BY obj.vec, janders ASCENDING, thing DESCENDING",
                "FROM docs SORT BY obj.prop, val1 DESCENDING",
                "FROM docs SORT BY val1 ASCENDING, obj.prop DESCENDING",
				"FROM docs MATCH jarl = \"QuePasaPerracus\""
        };

        for(String str : queries) {
			JSON.parse(MongoQuery.translateQuery(str).getJsonQueryString());
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
		System.out.println(MongoQuery.translateQuery(queryString));
		JSON.parse(MongoQuery.translateQuery(queryString).getJsonQueryString());


	}
}
