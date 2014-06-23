package edu.ucsd.crbs.cws.workflow;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@RunWith(JUnit4.class)
public class TestParameter {

    @Test
    public void testGettersAndSetters(){
        Parameter p = new Parameter();
        assertTrue(p.getName() == null);
        assertTrue(p.getValue() == null);
        
        p.setName("bob");
        p.setValue("val");
        assertTrue(p.getName().equals("bob"));
        assertTrue(p.getValue().equals("val"));
    }

    @Test
    public void testAsString(){
        Parameter p = new Parameter();
        assertTrue(p.asString().equals("name=null,value=null"));
        
        p.setName("bob");
        assertTrue(p.asString().equals("name=bob,value=null"));
        
        p.setValue("val");
        assertTrue(p.asString().equals("name=bob,value=val"));
        
        p.setName(null);
        assertTrue(p.asString().equals("name=null,value=val"));
    }
    
}
