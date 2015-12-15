import com.mongodb.util.JSON;
import es.bsc.mongoal.MongoQuery;
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
		String q =("FROM events " +
				"GROUP BY NOTHING \n" +
				"avg(timestamp) AS avgtime\n"+
				"max(timestamp) AS maxtime\n"+
				"min(timestamp) as mintime\n"+
						"avg(jarpleich) as jarpleicher"
		);

		System.out.println("q = " + q);
		JSON.parse(MongoQuery.translateQuery(q).toString());
//
//		ret = query.query("FROM events MATCH data.metric = 0.0");
//		System.out.println("ret[0] = " + ret[0]);
//		if(ret[1] != null) {
//			System.out.println("Query: " + JSON.serialize(ret[1]));
//		}
	}
}

