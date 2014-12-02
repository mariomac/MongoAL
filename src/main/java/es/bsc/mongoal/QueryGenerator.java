/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.mongoal;

import com.mongodb.DBObject;

/**
 *
 * @author mmacias
 */
public class QueryGenerator extends MongoALBaseVisitor<String[]> {

    public static QueryGenerator instantiate(MongoALParser parser) {
        add visitor to parser
    }

    @Override
    public String[] visitQuery(MongoALParser.QueryContext ctx) {
        return super.visitQuery(ctx); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    private static class // create here a visitor that generates DBObjects

    
    
}
