/*
 * Copyright (c) 2018. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.fhnw.digibp.service;

import ch.fhnw.digibp.util.NameGenerator;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import javax.inject.Named;
import java.util.logging.Logger;

/**
 *
 * @author andreas.martin
 */
@Named
public class DemoDataService implements JavaDelegate {

    private final static Logger LOGGER = Logger.getLogger(DemoDataService.class.getName());

    private void setVariableIfEmpty(DelegateExecution execution, String variable, String value)
    {
        Object object = execution.getVariable(variable);
        if(object==null || ((String) object).isEmpty()){
            execution.setVariable(variable, value);
        }
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        LOGGER.info("DemoDataService.generate() called!!!");

        setVariableIfEmpty(execution, "mscbis_personHasFamilyName", NameGenerator.getName());
        setVariableIfEmpty(execution, "mscbis_personHasFirstName", NameGenerator.getName());
        setVariableIfEmpty(execution, "mscbis_personHasNationality", NameGenerator.getName());
        setVariableIfEmpty(execution, "mscbis_personHasBusinessRole", "Applicant");
        setVariableIfEmpty(execution, "mscbis_applicationToProgramme", "FHNW MSc BIS Programme");
        setVariableIfEmpty(execution, "mscbis_personCandidateGrade", Integer.toString(1 + (int) (Math.random() * ((6 - 1) + 1))));

    }
}
