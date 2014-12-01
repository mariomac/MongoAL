/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package removeme;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.Test;

/**
 *
 * @author mmacias
 */
public class TestScratch {
    @Test
    public void testScratch() {
        ArrayNode an = new ArrayNode(JsonNodeFactory.instance);
        System.out.println(an.toString());
    }
    
}
