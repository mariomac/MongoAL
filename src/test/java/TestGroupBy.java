import es.bsc.mongoal.test.ErrorReport;
import org.antlr.v4.runtime.*;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;


public class TestGroupBy {
	private static ErrorReport getReport(String queryString) {
		System.out.println("Executing: " + queryString);
		MongoALLexer lexer = new MongoALLexer(new org.antlr.v4.runtime.ANTLRInputStream(queryString));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		MongoALParser parser = new MongoALParser(tokens);
		parser.removeErrorListeners();
		ErrorReport report = new ErrorReport();
		parser.addErrorListener(report);
		parser.query();
		return report;
	}

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


	}
}