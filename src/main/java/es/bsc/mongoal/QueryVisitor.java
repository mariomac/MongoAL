package es.bsc.mongoal;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.lang.reflect.Array;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

class QueryVisitor extends MongoALBaseVisitor<CharSequence> {

	private String collectionId;

	public String getCollectionId() {
		return collectionId;
	}

	@Override
	public CharSequence visitQuery(MongoALParser.QueryContext ctx) {
		StringBuilder queryString = new StringBuilder("[");
		collectionId = ctx.collId.getText();
		if(ctx.stage() != null && ctx.stage().size() > 0) {
			boolean addComma = false;
			for (MongoALParser.StageContext stage : ctx.stage()) {
				if(addComma) {
					queryString.append(',');
				}
				queryString.append(visit(stage));
				addComma = true;
			}
		}
		queryString.append(']');
		return queryString;
	}

	@Override
	public CharSequence visitMatchStage(@NotNull MongoALParser.MatchStageContext ctx) {
		return new StringBuilder("{\"$match\":{").append(visitLogicalExpression(ctx.logicalExpression())).append("}}");
	}

	@Override
	public CharSequence visitLogicalExpression(@NotNull MongoALParser.LogicalExpressionContext ctx) {
		List<MongoALParser.OrExpressionContext> orExprs = ctx.orExpression();
		if(orExprs.size() == 1) {
			return visit(ctx.orExpression(0));
		} else {
			StringBuilder sb = new StringBuilder("\"$and\":[");
			boolean addComma = false;
			for(MongoALParser.OrExpressionContext c : orExprs) {
				if(addComma) {
					sb.append(',');
				}
				sb.append('{').append(visit(c)).append('}');;
				addComma = true;
			}
			return sb.append(']');
		}
	}

	@Override
	public CharSequence visitOrExpression(@NotNull MongoALParser.OrExpressionContext ctx) {
		List<MongoALParser.AtomLogicalExpressionContext> atoms = ctx.atomLogicalExpression();
		if(atoms.size() == 1) {
			return visit(ctx.atomLogicalExpression(0));
		} else {
			StringBuilder sb = new StringBuilder("\"$or\" : [");
			boolean addComma = false;
			for(MongoALParser.AtomLogicalExpressionContext a : atoms) {
				if(addComma) {
					sb.append(',');
				}
				sb.append('{').append(visit(a)).append('}');
				addComma = true;
			}
			return sb.append(']');
		}
	}

	@Override
	public CharSequence visitAtomLogicalExpression(@NotNull MongoALParser.AtomLogicalExpressionContext ctx) {
		// TODO: support NOT operations
		if(ctx.comparisonExpression() != null) {
			return visit(ctx.comparisonExpression());
		} else if(ctx.LPAR() != null && ctx.RPAR() != null) {
			return visit(ctx.logicalExpression());
		} else throw new QueryException("On: " + ctx.getText());
	}

	@Override
	public CharSequence visitComparisonExpression(@NotNull MongoALParser.ComparisonExpressionContext ctx) {
		StringBuilder sb = new StringBuilder();
		if(ctx.OPEQ() != null) {
			appendUnescapedString(ctx.leftComparison().getText(), sb);
			sb.append(':').append(visit(ctx.rightComparison()));
		} else {
			String op = null;
			if(ctx.OPGT() != null) {
				op = "$gt";
			} else if(ctx.OPGTE() != null) {
				op = "$gte";
			} else if(ctx.OPLT() != null) {
				op = "$lt";
			} else if(ctx.OPLTE() != null) {
				op = "$lte";
			} else if(ctx.OPNEQ() != null) {
				op = "$ne";
			} else if(ctx.OPGTE() != null) {
				op = "$gte";
			} else if(ctx.OPGTE() != null) {
				op = "$gte";
			}
			appendUnescapedString(ctx.leftComparison().getText(),sb);
			sb.append(":{\"").append(op).append("\":").append(visit(ctx.rightComparison())).append('}');
		}
		return sb;
	}

	@Override
	public CharSequence visitRightComparison(@NotNull MongoALParser.RightComparisonContext ctx) {
		if(ctx.STRING() != null) {
			String content = ctx.STRING().getText();
			int length = content.length();
			StringBuilder sb = new StringBuilder();
			// removes initial and final 'simple' or "double" commas
			appendUnescapedString(content.substring(1, length - 1),sb);
			return sb;
		} else if(ctx.FLOAT() != null || ctx.INTEGER() != null) {
			return ctx.getText();
		} else {
			throw new QueryException("Right operator: no string, no int, no float? In: " + ctx.getText());
		}
	}

	@Override
	public CharSequence visitGroupStage(@NotNull MongoALParser.GroupStageContext ctx) {
		StringBuilder sb = new StringBuilder("{\"$group\":{");
		if(ctx.NOTHING() != null) {
			sb.append("\"_id\":null");
		} else {
			sb.append("\"_id\":").append(visit(ctx.addSubExpr()));
		}
		ListIterator<TerminalNode> simpleIds = ctx.SIMPLEID().listIterator();
		ListIterator<MongoALParser.AccumExprContext> accumExprs = ctx.accumExpr().listIterator();
		while(simpleIds.hasNext() && accumExprs.hasNext()) {
			sb.append(",\"").append(simpleIds.next().getText()).append("\":")
					.append(visit(accumExprs.next()));
		}
		return sb.append("}}");
	}

	@Override
	public CharSequence visitCompoundId(@NotNull MongoALParser.CompoundIdContext ctx) {
		StringBuilder sb = new StringBuilder(ctx.SIMPLEID().getText());
		if(ctx.arrayIndex() != null) {
			sb.append(".").append(visit(ctx.arrayIndex()));
		}
		if(ctx.compoundId() != null) {
			sb.append(".").append(visit(ctx.compoundId()));
		}
		return sb;
	}

	@Override
	public CharSequence visitAccumExpr(@NotNull MongoALParser.AccumExprContext ctx) {
		String opName = null;
		String id = ctx.SIMPLEID().getText().trim();
		if(id.equalsIgnoreCase("addtoset")) {
			opName = "$addToSet";
		} else if(id.equalsIgnoreCase("avg")) {
			opName = "$avg";
		} else if(id.equalsIgnoreCase("first")) {
			opName = "$first";
		} else if(id.equalsIgnoreCase("last")) {
			opName = "$last";
		} else if(id.equalsIgnoreCase("max")) {
			opName = "$max";
		} else if(id.equalsIgnoreCase("min")) {
			opName = "$min";
		} else if(id.equalsIgnoreCase("push")) {
			opName = "$push";
		} else if(id.equalsIgnoreCase("sum")) {
			opName = "$sum";
		} else throw new QueryException("visitAccumExpr WTF: " + ctx.getText());
		return new StringBuilder("{\"").append(opName).append("\":").append(visit(ctx.addSubExpr())).append('}');
	}

	@Override
	public CharSequence visitArrayIndex(@NotNull MongoALParser.ArrayIndexContext ctx) {
		return ctx.INTEGER().getText();
	}

	@Override
	public CharSequence visitAddSubExpr(@NotNull MongoALParser.AddSubExprContext ctx) {
		StringBuilder sb = null;
		if(ctx.ADD() != null) {
			sb = new StringBuilder("{\"$add\":[");
		} else if(ctx.SUB() != null) {
			sb = new StringBuilder("{\"$subtract\":[");
		} else {
			return visit(ctx.multDivExpr(0));
		}
		sb.append(visit(ctx.multDivExpr(0)));
		sb.append(',').append(visit(ctx.multDivExpr(1)));
		return sb.append("]}");

	}

	@Override
	public CharSequence visitMultDivExpr(@NotNull MongoALParser.MultDivExprContext ctx) {
		StringBuilder sb = null;
		String operator = null;
		if(ctx.MULT() != null) {
			sb = new StringBuilder("{\"$multiply\":[");
		} else if(ctx.DIV() != null) {
			sb = new StringBuilder("{\"$divide\":[");
		} else if(ctx.MOD() != null) {
			sb = new StringBuilder("{\"$mod\":[");
		} else {
			return visit(ctx.atomExpr(0));
		}
		sb.append(visit(ctx.atomExpr(0)));
		sb.append(',').append(visit(ctx.atomExpr(1)));
		return sb.append("]}");

	}

	@Override
	public CharSequence visitAtomExpr(@NotNull MongoALParser.AtomExprContext ctx) {
		if(ctx.FLOAT() != null) {
			return (ctx.FLOAT().getText());
		} else if(ctx.INTEGER() != null) {
			return (ctx.INTEGER().getText());
		} else if(ctx.LPAR() != null && ctx.RPAR() != null) {
			return visit(ctx.addSubExpr());
		} else if(ctx.compoundId() != null) {
			return new StringBuilder("\"$").append(visit(ctx.compoundId())).append('"');
		} else if(ctx.negative() != null) {
			return visit(ctx.negative());
		} else throw new QueryException("WTF: "  + ctx.getText());
	}

	@Override
	public CharSequence visitNegative(@NotNull MongoALParser.NegativeContext ctx) {
		return new StringBuilder("{\"$subtract\":[0,")
				.append(visit(ctx.addSubExpr()))
				.append("]}");
	}

	@Override
	public CharSequence visitSortStage(@NotNull MongoALParser.SortStageContext ctx) {
		StringBuilder sb = new StringBuilder("{");
		boolean addComma = false;
		for(MongoALParser.SortCriteriaContext sortCriteria : ctx.sortCriteria()) {
			int order = 1; // by default, ascending ORDER
			if(sortCriteria.DESCENDING() != null) {
				order = -1;
			}
			if(addComma) {
				sb.append(',');
			}
			addComma = true;
			sb.append('"').append(visit(sortCriteria.compoundId())).append("\":").append(order);
		}
		return sb.append('}');
	}

	private static void appendUnescapedString(String value, StringBuilder sb) {
		sb.append('"');
		// Unescape string
		StringTokenizer st = new StringTokenizer((String)value,"\t\b\n\f\r\'\"\\",true);
		while(st.hasMoreTokens()) {
			String t = st.nextToken();
			switch (t) {
				case "\t":
					sb.append("\\t");
					break;
				case "\b":
					sb.append("\\b");
					break;
				case "\n":
					sb.append("\\n");
					break;
				case "\f":
					sb.append("\\f");
					break;
				case "\r":
					sb.append("\\r");
					break;
				case "\'":
					sb.append("\\'");
					break;
				case "\"":
					sb.append("\\\"");
					break;
				case "\\":
					sb.append("\\\\");
					break;
				default:
					sb.append(t);
			}
		}
		sb.append('"');
	}

}
