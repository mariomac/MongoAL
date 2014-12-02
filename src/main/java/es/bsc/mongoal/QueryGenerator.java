/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.mongoal;

/**
 *
 * @author mmacias
 */
public class QueryGenerator extends MongoALBaseListener {
    @Override
    public void exitQuery(MongoALParser.QueryContext ctx) {
        System.out.println("The collection to parse is: " + ctx.collId.getText());                
    }

    @Override
    public void exitStage(MongoALParser.StageContext ctx) {
        System.out.println("Stage: " + ctx.getText());
    }


}
