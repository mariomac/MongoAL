import com.fasterxml.jackson.databind.JsonNode;
import es.bsc.mongoal.test.ErrorReport;
import org.antlr.v4.runtime.*;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static es.bsc.mongoal.test.ErrorReport.*;

public class TestGroupByGrammar {

	@Test
	public void testTest() {

		ErrorReport report = getReport("FROM someCollection " +
				"GROUP BY cosa.arr[45].val AS bad name " +
				"GROUP BY simpleId As goodName " +
				"GROUP BY SOMETHING " +
				"GROUP BY avg(arr[33]*2-var) aS correctExpression");

		assertTrue(report.isOffending("name"));
		assertFalse(report.isOffending("SOMETHING"));
		assertThat(report.count(), is(1));

		report = getReport("FROM someCollection " +
				"GROUP BY cosa.arr[45].val AS goodName " +
				"GROUP BY simpleId As goodName " +
				"GROUP BY SOMETHING " +
				"GROUP BY avg(arr[33]*2-var) aS correctExpression");

		assertEquals(report.count(),0);

		report = getReport("FROM someCollection " +
				"GROUP BY cosa.arr[45].val AS goodName " +
				"GROUPBY simpleId As goodName " +
				"GROUP BY SOMETHING " +
				"GROUP BY avg(arr[33]*2-var) aS correctExpression");

		assertTrue(report.isOffending("GROUPBY"));
        
        report = getReport("FROM somecollection");
        assertEquals(report.count(),0);
                
        report = getReport("FROM");
        assertTrue(report.isOffending("<EOF>"));
        assertEquals(report.count(),1);
        
        
	}
}