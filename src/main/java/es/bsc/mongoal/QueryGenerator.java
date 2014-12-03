/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.mongoal;

import com.mongodb.*;
import com.mongodb.util.JSON;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author mmacias
 */
public class QueryGenerator  {

    DB database;
    QueryVisitor queryVisitor;

    public QueryGenerator(DB database) {
        this.database = database;
        queryVisitor = new QueryVisitor();
    }

    public Iterable<DBObject> query(String queryString) {
        MongoALLexer lexer = new MongoALLexer(new org.antlr.v4.runtime.ANTLRInputStream(queryString));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MongoALParser parser = new MongoALParser(tokens);
        Object[] ret = (Object[]) queryVisitor.visitQuery(parser.query());

        DBCollection events = database.getCollection((String)ret[0]);
        if(ret[1] == null) {
            return events.find();
        } else {
            return events.aggregate((List<DBObject>) ret[1]).results();
        }
    }

    private class QueryVisitor extends MongoALBaseVisitor<Object> {

        @Override
        public Object visitQuery(MongoALParser.QueryContext ctx) {
            Object[] things = new Object[2];
            things[0] = ctx.collId.getText();
            if(ctx.stage() != null && ctx.stage().size() > 0) {
                List<DBObject> querieslist = new ArrayList<DBObject>();
                for (MongoALParser.StageContext stage : ctx.stage()) {
                    querieslist.add((DBObject)visit(stage));
                }
                System.out.println(JSON.serialize(querieslist));
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
            // de momento no soporta empalmar ni con ORs ni ANDs, solo la primera expression
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
            } else throw new UnknownError("On: " + ctx.getText());
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
                // removes initial and final 'simple' of "double" commas
                return content.substring(1,length-1);
            } else if(ctx.FLOAT() != null) {
                return Float.valueOf(ctx.getText());
            } else if(ctx.INTEGER() != null) {
                return Integer.valueOf(ctx.getText());
            } else {
                throw new UnknownError("Right operator: no string, no int, no float? In: " + ctx.getText());
            }
        }

        @Override
        public Object visitAddSubExpr(@NotNull MongoALParser.AddSubExprContext ctx) {
            return super.visitAddSubExpr(ctx);
        }

        @Override
        public Object visitMultDivExpr(@NotNull MongoALParser.MultDivExprContext ctx) {
            return super.visitMultDivExpr(ctx);
        }

        @Override
        public Object visitAtomExpr(@NotNull MongoALParser.AtomExprContext ctx) {
            return super.visitAtomExpr(ctx);
        }

        @Override
        public Object visitNegative(@NotNull MongoALParser.NegativeContext ctx) {
            return super.visitNegative(ctx);
        }
    }// create here a visitor that generates DBObjects

    
    
}
