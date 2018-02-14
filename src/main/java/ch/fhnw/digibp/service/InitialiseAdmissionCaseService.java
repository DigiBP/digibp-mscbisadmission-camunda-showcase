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

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

/**
 *
 * @author andreas.martin
 */
@Named
public class InitialiseAdmissionCaseService implements JavaDelegate {

    private final static Logger LOGGER = Logger.getLogger(InitialiseAdmissionCaseService.class.getName());
    

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        LOGGER.info("initialiseAdmissionCase called!!!");

        execution.setVariable("mscbis_applicationHasDate", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()));

    }
}
