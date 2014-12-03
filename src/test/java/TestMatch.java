import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;
import es.bsc.mongoal.QueryGenerator;
import org.junit.Test;

import java.net.UnknownHostException;

/**
 * Created by mmacias on 3/12/14.
 */
public class TestMatch {
	@Test
	public void testMatch() throws UnknownHostException {
		String host = "localhost";
		int port = 27017;
		String dbName = "applicationmonitor";

		MongoClient client = new MongoClient(host,port);
		DB db = client.getDB(dbName);

		QueryGenerator query = new QueryGenerator(db);
		Iterable<DBObject> ret = query.query("FROM events MATCH nodeId = 'TheSinusNode'");
		for(DBObject dbo : ret) {
			System.out.println(JSON.serialize(dbo));
		}

		client.close();
//
//		ret = query.query("FROM events MATCH data.metric = 0.0");
//		System.out.println("ret[0] = " + ret[0]);
//		if(ret[1] != null) {
//			System.out.println("Query: " + JSON.serialize(ret[1]));
//		}
	}
}
