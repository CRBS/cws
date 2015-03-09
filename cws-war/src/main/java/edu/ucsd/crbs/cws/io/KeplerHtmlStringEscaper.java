/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2014 The Regents of the University of California All Rights Reserved
 * 
 * Permission to copy, modify and distribute any part of this CRBS Workflow 
 * Service for educational, research and non-profit purposes, without fee, and
 * without a written agreement is hereby granted, provided that the above 
 * copyright notice, this paragraph and the following three paragraphs appear
 * in all copies.
 * 
 * Those desiring to incorporate this CRBS Workflow Service into commercial 
 * products or use for commercial purposes should contact the Technology
 * Transfer Office, University of California, San Diego, 9500 Gilman Drive, 
 * Mail Code 0910, La Jolla, CA 92093-0910, Ph: (858) 534-5815, 
 * FAX: (858) 534-7345, E-MAIL:invent@ucsd.edu.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR 
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING 
 * LOST PROFITS, ARISING OUT OF THE USE OF THIS CRBS Workflow Service, EVEN IF 
 * THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 * 
 * THE CRBS Workflow Service PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE
 * UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, 
 * UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES
 * NO REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR 
 * EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF 
 * THE CRBS Workflow Service WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER
 * RIGHTS. 
 */

package edu.ucsd.crbs.cws.io;

import java.util.LinkedHashMap;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class KeplerHtmlStringEscaper implements StringEscaper {

    
    /**
     * Contains a mapping of ASCII characters to HTML escape codes.  This
     * is needed to replace certain characters with special characters when
     * passing arguments to Kepler script
     */
    private final LinkedHashMap<String, String> m_EscapeMap;
    
    
    public KeplerHtmlStringEscaper(){
        //using a linked hash map cause order matters in the escape
        //since & needs to be replaced first and then is ignored cause
        //it is used in the codes
        m_EscapeMap = new LinkedHashMap<>();
        m_EscapeMap.put("&", "&#38;");
        m_EscapeMap.put(" ", "&#32;");
        m_EscapeMap.put("[!]", "&#33;");
        m_EscapeMap.put("\"", "&#34;");
        m_EscapeMap.put("%", "&#37;");

        m_EscapeMap.put("'", "&#39;");
        m_EscapeMap.put("[(]", "&#40;");
        m_EscapeMap.put("[)]", "&#41;");
        m_EscapeMap.put("[*]", "&#42;");
        //m_EscapeMap.put("/","&#47;");

        m_EscapeMap.put("<", "&#60;");
        m_EscapeMap.put("=", "&#61;");
        m_EscapeMap.put(">", "&#62;");
    }
    
     /**
     * Replaces special characters with html codes otherwise the parameters dont
     * get passed properly
     * @param val source String
     */
    @Override
    public String escapeString(String val) {
        if (val == null) {
            return null;
        }

        String tmpVal = val;

        for (String k : m_EscapeMap.keySet()) {
            tmpVal = tmpVal.replaceAll(k, m_EscapeMap.get(k));
        }
        return tmpVal;
    }

}
