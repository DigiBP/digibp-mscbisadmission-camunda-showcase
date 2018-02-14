/*
 * Copyright (c) 2018. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package ch.fhnw.digibp.message;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import javax.inject.Named;

/**
 * Created by andreas.martin on 05.04.2017.
 */
@Named
public class AdditionalDocumentsArrivedMessage implements JavaDelegate {

    public void execute(DelegateExecution delegateExecution) throws Exception {
        delegateExecution.getProcessEngineServices().getRuntimeService()
                .createMessageCorrelation("Message_AdditionalDocumentsArrived")
                .processInstanceId(delegateExecution.getVariable("mscbis_processInstanceId").toString())
                .correlateWithResult();
    }
}