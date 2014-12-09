import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;
import es.bsc.mongoal.QueryGenerator;
import org.junit.Ignore;
import org.junit.Test;

import java.net.UnknownHostException;

/**
 * Created by mmacias on 4/12/14.
 */
@Ignore

public class TestGroupBy {
	@Test
	public void testGroupBy() throws UnknownHostException {
		String host = "localhost";
		int port = 27017;
		String dbName = "applicationmonitor";

		MongoClient client = new MongoClient(host,port);
		DB db = client.getDB(dbName);

		QueryGenerator query = new QueryGenerator(db);
		Iterable<DBObject> ret = query.query("FROM events " +
				"GROUP BY NOTHING \n" +
				"avg(timestamp) AS avgtime\n"+
				"max(timestamp) AS maxtime\n"+
				"min(timestamp) as mintime\n"+
						"avg(jarpleich) as jarpleicher"
		);

		//"or (data.metric > 4 and data.metric < 4.5)");
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

