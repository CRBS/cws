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
package edu.ucsd.crbs.cws.workflow.validate;

import edu.ucsd.crbs.cws.workflow.Parameter;
import edu.ucsd.crbs.cws.workflow.WorkflowParameter;
import java.util.regex.Pattern;

/**
 * Validates parameters that have a validation type set
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class ParameterValidatorImpl implements ParameterValidator {

    /**
     * Validates <b>param</b> that have
     * {@link WorkflowParameter#getValidationType()} set
     *
     * @param param Parameter to examine
     * @return null if parameter is valid otherwise will contain a string with
     * the error
     */
    @Override
    public String validate(Parameter param) {
        if (param == null) {
            return "Cannot perform validation, Parameter cannot be null";
        }

        //just skip any parameters that lack a workflow parameter
        if (param.getWorkflowParameter() == null) {
            return null;
        }

        if (param.getWorkflowParameter().getIsRequired() == true
                && param.getValue() == null) {
            return "Parameter value is null, but is set to required";
        }

        // no validation type was specified, no validation needs to be performed
        if (param.getWorkflowParameter().getValidationType() == null) {
            return null;
        }

        if (param.getValue() == null) {
            return "Value cannot be null";
        }

        if (param.getWorkflowParameter().getValidationType().equalsIgnoreCase(WorkflowParameter.ValidationType.NUMBER)) {
            return validateNumberParameter(param);
        } else if (param.getWorkflowParameter().getValidationType().equalsIgnoreCase(WorkflowParameter.ValidationType.DIGITS)) {
            return validateDigitsParameter(param);
        } else if (param.getWorkflowParameter().getValidationType().equalsIgnoreCase(WorkflowParameter.ValidationType.STRING)) {
            return validateStringParameter(param);
        }

        return "Unknown validation type";
    }

    private String validateNumberParameter(Parameter param) {
        double val;
        try {
            val = Double.parseDouble(param.getValue());
        } catch (NumberFormatException nfe) {
            return "Not a valid number: " + nfe.getMessage();
        }

        return isValueInRange(val, param);
    }

    private String validateDigitsParameter(Parameter param) {
        long val;
        try {
            val = Long.parseLong(param.getValue());

        } catch (NumberFormatException nfe) {
            return "Not a valid digit: " + nfe.getMessage();
        }

        return isValueInRange(val, param);
    }

    private String validateStringParameter(Parameter param) {
        if (param.getWorkflowParameter().getMaxLength() > 0) {
            if (param.getValue().length() > param.getWorkflowParameter().getMaxLength()) {
                return "Parameter value length: " + param.getValue().length()
                        + "exceeds max length of "
                        + param.getWorkflowParameter().getMaxLength();
            }
        }

        if (param.getWorkflowParameter().getValidationRegex() == null) {
            return null;
        }

        if (Pattern.matches(param.getWorkflowParameter().getValidationRegex(),
                param.getValue()) == false) {
            return "String does not match regex specified for this parameter";
        }
        return null;
    }

    private String isValueInRange(double value, Parameter param) {
        if (param.getWorkflowParameter().getMinValue() == param.getWorkflowParameter().getMaxValue()
                && param.getWorkflowParameter().getMaxValue() == 0) {
            return null;
        }
        if (value < param.getWorkflowParameter().getMinValue()) {
            return "Value is less then minimum value of " + param.getWorkflowParameter().getMinValue();
        }
        if (value > param.getWorkflowParameter().getMaxValue()) {
            return "Value is greater then maximum value of " + param.getWorkflowParameter().getMaxValue();
        }
        return null;
    }

}
