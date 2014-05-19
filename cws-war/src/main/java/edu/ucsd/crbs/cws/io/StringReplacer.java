/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.cws.io;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public interface StringReplacer {
 
    /**
     * Given a line of text this method will supply the identical line or
     * a different line.
     * @param line
     * @return 
     */
    public String replace(final String line);
    
}
