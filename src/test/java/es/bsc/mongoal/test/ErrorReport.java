package es.bsc.mongoal.test;

import es.bsc.mongoal.MongoALLexer;
import es.bsc.mongoal.MongoALParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import java.util.BitSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by mmacias on 1/12/14.
 */
public class ErrorReport implements ANTLRErrorListener {
	// key: line:charposition
	private Map<String, CommonToken> offendingSymbols = new TreeMap<String,CommonToken>();

	public boolean isOffending(String offendingSymbol) {
		return offendingSymbols.get(offendingSymbol) != null;
	}

//	public String get(String ) {
//		CommonToken ct = offendingSymbols.get(line + ":" + charPos);
//		return ct==null?null:ct.getText();
//	}

	public int count() {
		return offendingSymbols.size();
	}

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
		System.err.println(line+":"+charPositionInLine+"\t " +msg);
		if(offendingSymbol != null) {
			if(offendingSymbols.get(((CommonToken)offendingSymbol).getText()) == null) {
				offendingSymbols.put(((CommonToken)offendingSymbol).getText(), (CommonToken) offendingSymbol);
			} else throw new RuntimeException("Two offending symbols have the same name in the same expressions. Please redo your tests");
		} else throw new RuntimeException("esto no me lo esperaba. No hay offending symbol en posicion "+line+":"+charPositionInLine + ". Mensaje: " + msg);
	}

	@Override
	public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
		throw new UnsupportedOperationException("reportAmbiguity**\nrecognizer: " + recognizer + "\ndfa: " + dfa +"\nstartIndex: "+ startIndex
				+"\nstopIndex: " + stopIndex+"\nexact: " + exact +"\nambigAlts: " + ambigAlts +"\nconfigs: " + configs);
	}

	@Override
	public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {
	}

	@Override
	public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {

	}
    
	public static ErrorReport getReport(String queryString) {
		MongoALLexer lexer = new MongoALLexer(new org.antlr.v4.runtime.ANTLRInputStream(queryString));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		MongoALParser parser = new MongoALParser(tokens);
		System.out.println("Executing: " + queryString);
		parser.removeErrorListeners();
		ErrorReport report = new ErrorReport();
		parser.addErrorListener(report);
		parser.query();
		return report;
	}
}
