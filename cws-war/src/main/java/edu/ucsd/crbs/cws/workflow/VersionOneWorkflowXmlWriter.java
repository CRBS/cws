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

package edu.ucsd.crbs.cws.workflow;

import java.io.Writer;

/**
 * Writes a given {@link Workflow} as 1.x Kepler xml to writer passed in to 
 * {@link #write(java.io.Writer, edu.ucsd.crbs.cws.workflow.Workflow) }
 * method.  <b>NOTE: XML is NOT loadable by Kepler, but is intended to be consumed
 * by CAMERA Portal</b>
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class VersionOneWorkflowXmlWriter implements WorkflowWriter {

    /**
     * Writes {@link Workflow} <b>w</b> to <b>writer</b> in 1.x Kepler moml 
     * xml format.  This output will <b>NOT</b> properly load in Kepler!!!
     * 
     * This method will write out the parameters in a format that can be consumed
     * by the CAMERA portal.  
     * 
     * @param writer
     * @param w
     * @throws Exception 
     */
    @Override
    public void write(Writer writer, Workflow w) throws Exception {
        if (writer == null){
            throw new IllegalArgumentException("Writer is null");
        }
        if (w == null){
            throw new IllegalArgumentException("Workflow is null");
        }
        writeHeader(writer,w);
        
        for (WorkflowParameter param : w.getParameters()){
            writeParameter(writer,param);
        }
        
        writeFooter(writer,w);
    }
    
    private void writeParameter(Writer writer,WorkflowParameter param) throws Exception {
        
        writer.write("\t<property name=\""+param.getName()+
                "\" class=\"ptolemy.data.expr.StringParameter\" value=\""+
                param.getValue()+"\">\n");
        
        writer.write("\t\t<display name=\""+param.getDisplayName()+"\"/>\n");

        if (param.getHelp() != null){
            writeParameterAttribute(writer,"tooltip",param.getHelp());
        }
        if (param.getIsAdvanced() == true){
            writeParameterAttribute(writer,"advanced","true");
        }
        
        if (param.getValidationType() != null){
            writeParameterAttribute(writer,"inputtype",param.getValidationType());
        }
        if (param.getValidationHelp() != null){
            writeParameterAttribute(writer,"inputtypehelp",param.getValidationHelp());
        }
        if (param.getValidationRegex() != null){
            writeParameterAttribute(writer,"stringregex",param.getValidationRegex());
        }
        if (param.getType() != null){
            if (param.getType().equals("dropdown")){
                writeParameterAttribute(writer,"displaytype",param.getType());
            }
            else if (param.getType().equals("hidden")){
                writeParameterAttribute(writer,"hidden","true");
            }
            else if (param.getType().equals("checkbox")){
                writeParameterAttribute(writer,"displaytype","checkbox");
            }
        }
        
        if (param.getNameValueDelimiter() != null){
            writeParameterAttribute(writer,"delimitervalue",param.getNameValueDelimiter());
        }
        if (param.getIsRequired() == true){
            writeParameterAttribute(writer,"required","true");
        }
        
        writer.write("\t</property>\n");
        
    }
    
    private void writeHeader(Writer writer,Workflow w) throws Exception{
        writer.write("<?xml version=\"1.0\" standalone=\"no\"?>\n");
        writer.write("<entity name=\""+w.getName()+
                "\" class=\"ptolemy.actor.TypedCompositeActor\">\n");
        writer.write("\t<property name=\"WORKFLOWID\" value=\""+w.getId()+
                "\"/>\n");
    }
    
    private void writeFooter(Writer writer,Workflow w) throws Exception{
        writer.write("</entity>\n");
    }
    
    private void writeParameterAttribute(Writer writer,final String name,
            final String value) throws Exception {
        writer.write("\t\t<property name=\""+name+
                "\" class=\"ptolemy.data.expr.StringParameter\" value=\""+
                value+"\"/>\n");
    }

}
