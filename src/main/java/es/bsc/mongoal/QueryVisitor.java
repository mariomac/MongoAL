package es.bsc.mongoal;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

class QueryVisitor extends MongoALBaseVisitor<Object> {

	@Override
	public Object visitQuery(MongoALParser.QueryContext ctx) {
		Object[] things = new Object[2];
		things[0] = ctx.collId.getText();
		if(ctx.stage() != null && ctx.stage().size() > 0) {
			List<DBObject> querieslist = new ArrayList<DBObject>();
			for (MongoALParser.StageContext stage : ctx.stage()) {
				querieslist.add((DBObject)visit(stage));
			}
			things[1] = querieslist;
		}
		return things;
	}

	@Override
	public Object visitMatchStage(@NotNull MongoALParser.MatchStageContext ctx) {
		return new BasicDBObject("$match",
				visitLogicalExpression(ctx.logicalExpression()));
	}

	@Override
	public Object visitLogicalExpression(@NotNull MongoALParser.LogicalExpressionContext ctx) {
		List<MongoALParser.OrExpressionContext> orExprs = ctx.orExpression();
		if(orExprs.size() == 1) {
			return visit(ctx.orExpression(0));
		} else {
			BasicDBList dbl = new BasicDBList();
			for(MongoALParser.OrExpressionContext c : orExprs) {
				dbl.add(visit(c));
			}
			return new BasicDBObject("$and",dbl);
		}
	}

	@Override
	public Object visitOrExpression(@NotNull MongoALParser.OrExpressionContext ctx) {
		List<MongoALParser.AtomLogicalExpressionContext> atoms = ctx.atomLogicalExpression();
		if(atoms.size() == 1) {
			return visit(ctx.atomLogicalExpression(0));
		} else {
			BasicDBList dbl = new BasicDBList();
			for(MongoALParser.AtomLogicalExpressionContext a : atoms) {
				dbl.add(visit(a));
			}
			return new BasicDBObject("$or",dbl);
		}
	}

	@Override
	public Object visitAtomLogicalExpression(@NotNull MongoALParser.AtomLogicalExpressionContext ctx) {
		// TODO: support NOT operations
		if(ctx.comparisonExpression() != null) {
			DBObject comparisonExpression = (DBObject) visit(ctx.comparisonExpression());
			return comparisonExpression;
		} else if(ctx.LPAR() != null && ctx.RPAR() != null) {
			return visit(ctx.logicalExpression());
		} else throw new QueryException("On: " + ctx.getText());
	}

	@Override
	public Object visitComparisonExpression(@NotNull MongoALParser.ComparisonExpressionContext ctx) {
		DBObject dbo = new BasicDBObject();
		if(ctx.OPEQ() != null) {
			dbo.put(
					ctx.leftComparison().getText(),
					visit(ctx.rightComparison())
			);
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
			dbo.put(
					ctx.leftComparison().getText(),
					new BasicDBObject(op,visit(ctx.rightComparison()))
			);
		}
		return dbo;
	}

	@Override
	public Object visitRightComparison(@NotNull MongoALParser.RightComparisonContext ctx) {
		if(ctx.STRING() != null) {
			String content = ctx.STRING().getText();
			int length = content.length();
			// removes initial and final 'simple' or "double" commas
			return content.substring(1,length-1);
		} else if(ctx.FLOAT() != null) {
			return Float.valueOf(ctx.getText());
		} else if(ctx.INTEGER() != null) {
			return Integer.valueOf(ctx.getText());
		} else {
			throw new QueryException("Right operator: no string, no int, no float? In: " + ctx.getText());
		}
	}

	@Override
	public Object visitGroupStage(@NotNull MongoALParser.GroupStageContext ctx) {
		DBObject query = new BasicDBObject();
		if(ctx.NOTHING() != null) {
			query.put("_id",null);
		} else {
			query.put("_id", visit(ctx.addSubExpr()));
		}
		ListIterator<TerminalNode> simpleIds = ctx.SIMPLEID().listIterator();
		ListIterator<MongoALParser.AccumExprContext> accumExprs = ctx.accumExpr().listIterator();
		while(simpleIds.hasNext() && accumExprs.hasNext()) {
			query.put(
					simpleIds.next().getText(),
					visit(accumExprs.next())
			);
		}
		return new BasicDBObject("$group",query);
	}

	@Override
	public Object visitCompoundId(@NotNull MongoALParser.CompoundIdContext ctx) {
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
	public Object visitAccumExpr(@NotNull MongoALParser.AccumExprContext ctx) {
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
		return new BasicDBObject(opName,visit(ctx.addSubExpr()));
	}

	@Override
	public Object visitArrayIndex(@NotNull MongoALParser.ArrayIndexContext ctx) {
		return ctx.INTEGER().getText();
	}

	@Override
	public Object visitAddSubExpr(@NotNull MongoALParser.AddSubExprContext ctx) {
		BasicDBList multDivExprs = null;
		String operator = null;
		if(ctx.ADD() != null) {
			operator = "$add";
			multDivExprs = new BasicDBList();
		} else if(ctx.SUB() != null) {
			operator = "$subtract";
			multDivExprs = new BasicDBList();
		}
		if(multDivExprs == null) {
			return visit(ctx.multDivExpr(0));
		} else {
			multDivExprs.add(visit(ctx.multDivExpr(0)));
			multDivExprs.add(visit(ctx.multDivExpr(1)));
			return new BasicDBObject(operator,multDivExprs);
		}
	}

	@Override
	public Object visitMultDivExpr(@NotNull MongoALParser.MultDivExprContext ctx) {
		BasicDBList atomExprs = null;
		String operator = null;
		if(ctx.MULT() != null) {
			operator = "$multiply";
			atomExprs = new BasicDBList();
		} else if(ctx.DIV() != null) {
			operator = "$divide";
			atomExprs = new BasicDBList();
		} else if(ctx.MOD() != null) {
			operator = "$mod";
			atomExprs = new BasicDBList();
		}
		if(atomExprs == null) {
			return visit(ctx.atomExpr(0));
		} else {
			atomExprs.add(visit(ctx.atomExpr(0)));
			atomExprs.add(visit(ctx.atomExpr(1)));
			return new BasicDBObject(operator,atomExprs);
		}
	}

	@Override
	public Object visitAtomExpr(@NotNull MongoALParser.AtomExprContext ctx) {
		if(ctx.FLOAT() != null) {
			return Float.valueOf(ctx.FLOAT().getText());
		} else if(ctx.INTEGER() != null) {
			return Integer.valueOf(ctx.INTEGER().getText());
		} else if(ctx.LPAR() != null && ctx.RPAR() != null) {
			return visit(ctx.addSubExpr());
		} else if(ctx.compoundId() != null) {
			return "$"+visit(ctx.compoundId());
		} else if(ctx.negative() != null) {
			return visit(ctx.negative());
		} else throw new QueryException("WTF: "  + ctx.getText());
	}

	@Override
	public Object visitNegative(@NotNull MongoALParser.NegativeContext ctx) {
		BasicDBList dbl = new BasicDBList();
		dbl.add(0);
		dbl.add(visit(ctx.addSubExpr()));
		return new BasicDBObject("$subtract",dbl);

	}

	@Override
	public Object visitSortStage(@NotNull MongoALParser.SortStageContext ctx) {
		BasicDBObject sortFields = new BasicDBObject();
		for(MongoALParser.SortCriteriaContext sortCriteria : ctx.sortCriteria()) {
			int order = 1; // by default, ascending ORDER
			if(sortCriteria.DESCENDING() != null) {
				order = -1;
			}
			sortFields.put(visit(sortCriteria.compoundId()).toString(),order);
		}
		return new BasicDBObject("$sort", sortFields);
	}

}
