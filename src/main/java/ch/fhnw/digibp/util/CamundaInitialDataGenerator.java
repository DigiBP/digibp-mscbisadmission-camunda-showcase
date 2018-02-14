/*
 * Copyright (c) 2018. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.fhnw.digibp.util;

import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.authorization.*;
import org.camunda.bpm.engine.filter.Filter;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.persistence.entity.AuthorizationEntity;
import org.camunda.bpm.engine.task.TaskQuery;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.camunda.bpm.engine.authorization.Authorization.ANY;
import static org.camunda.bpm.engine.authorization.Authorization.AUTH_TYPE_GRANT;
import static org.camunda.bpm.engine.authorization.Permissions.*;
import static org.camunda.bpm.engine.authorization.Resources.APPLICATION;
import static org.camunda.bpm.engine.authorization.Resources.FILTER;

/**
 * @author andreas.martin
 */
@Component
public class CamundaInitialDataGenerator {

    @Inject
    private ProcessEngine processEngine;

    private final static Logger LOGGER = Logger.getLogger(CamundaInitialDataGenerator.class.getName());

    @PostConstruct
    public void createUsers() {
        final IdentityService identityService = processEngine.getIdentityService();

        if (identityService.isReadOnly()) {
            LOGGER.info("Identity service provider is Read Only, not creating any demo users.");
            return;
        }

        if (identityService.createUserQuery().userId("knut").singleResult() != null) {
            LOGGER.info("Not creating any demo users.");
            return;
        }

        if (identityService.createUserQuery().userId("demo").singleResult() == null) {
            LOGGER.info("Generating demo user for MSc BIS");
            User user = identityService.newUser("demo");
            user.setFirstName("Demo");
            user.setLastName("Demo");
            user.setPassword("demo");
            user.setEmail("demo@camunda.org");
            identityService.saveUser(user);
        }

        LOGGER.info("Generating demo data for MSc BIS");

        User user1 = identityService.newUser("knut");
        user1.setFirstName("Knut");
        user1.setLastName("Dean");
        user1.setPassword("knut");
        user1.setEmail("andreas.martin@fhnw.ch");

        identityService.saveUser(user1);

        User user2 = identityService.newUser("holger");
        user2.setFirstName("Holger");
        user2.setLastName("VDean");
        user2.setPassword("holger");
        user2.setEmail("andreas.martin@fhnw.ch");

        identityService.saveUser(user2);

        User user3 = identityService.newUser("markus");
        user3.setFirstName("Markus");
        user3.setLastName("EHead");
        user3.setPassword("markus");
        user3.setEmail("andreas.martin@fhnw.ch");

        identityService.saveUser(user3);

        User user4 = identityService.newUser("neyyer");
        user4.setFirstName("Neyyer");
        user4.setLastName("Admin");
        user4.setPassword("neyyer");
        user4.setEmail("andreas.martin@fhnw.ch");

        identityService.saveUser(user4);

        Group deanGroup = identityService.newGroup("dean");
        deanGroup.setName("Dean");
        deanGroup.setType("WORKFLOW");
        identityService.saveGroup(deanGroup);

        Group interviewteamGroup = identityService.newGroup("interviewteam");
        interviewteamGroup.setName("Interview Team");
        interviewteamGroup.setType("WORKFLOW");
        identityService.saveGroup(interviewteamGroup);

        Group admissioncommission = identityService.newGroup("admissioncommission");
        admissioncommission.setName("Admission Commission");
        admissioncommission.setType("WORKFLOW");
        identityService.saveGroup(admissioncommission);

        Group studyassistant = identityService.newGroup("studyassistant");
        studyassistant.setName("Study Assistant");
        studyassistant.setType("WORKFLOW");
        identityService.saveGroup(studyassistant);

        final AuthorizationService authorizationService = processEngine.getAuthorizationService();

        // create group
        if (identityService.createGroupQuery().groupId(Groups.CAMUNDA_ADMIN).singleResult() == null) {
            Group camundaAdminGroup = identityService.newGroup(Groups.CAMUNDA_ADMIN);
            camundaAdminGroup.setName("camunda BPM Administrators");
            camundaAdminGroup.setType(Groups.GROUP_TYPE_SYSTEM);
            identityService.saveGroup(camundaAdminGroup);


            // create ADMIN authorizations on all built-in resources
            for (Resource resource : Resources.values()) {
                if (authorizationService.createAuthorizationQuery().groupIdIn(Groups.CAMUNDA_ADMIN).resourceType(resource).resourceId(ANY).count() == 0) {
                    AuthorizationEntity userAdminAuth = new AuthorizationEntity(AUTH_TYPE_GRANT);
                    userAdminAuth.setGroupId(Groups.CAMUNDA_ADMIN);
                    userAdminAuth.setResource(resource);
                    userAdminAuth.setResourceId(ANY);
                    userAdminAuth.addPermission(ALL);
                    authorizationService.saveAuthorization(userAdminAuth);
                }
            }
        }

        identityService.createMembership("knut", "dean");
        identityService.createMembership("holger", "dean");
        identityService.createMembership("knut", "interviewteam");
        identityService.createMembership("holger", "interviewteam");
        identityService.createMembership("knut", "admissioncommission");
        identityService.createMembership("holger", "admissioncommission");
        identityService.createMembership("markus", "admissioncommission");
        identityService.createMembership("neyyer", "studyassistant");

        identityService.createMembership("demo", "dean");
        identityService.createMembership("demo", "interviewteam");
        identityService.createMembership("demo", "admissioncommission");
        identityService.createMembership("demo", "studyassistant");
        if (identityService.createUserQuery().userId("demo").memberOfGroup("camunda-admin").singleResult() == null) {
            identityService.createMembership("demo", "camunda-admin");
        }

        Authorization deanTasklistAuth = authorizationService.createNewAuthorization(AUTH_TYPE_GRANT);
        deanTasklistAuth.setGroupId("dean");
        deanTasklistAuth.addPermission(ACCESS);
        deanTasklistAuth.setResourceId("tasklist");
        deanTasklistAuth.setResource(APPLICATION);
        authorizationService.saveAuthorization(deanTasklistAuth);

        Authorization deanReadProcessDefinition = authorizationService.createNewAuthorization(AUTH_TYPE_GRANT);
        deanReadProcessDefinition.setGroupId("dean");
        deanReadProcessDefinition.addPermission(Permissions.READ);
        deanReadProcessDefinition.addPermission(Permissions.READ_HISTORY);
        deanReadProcessDefinition.setResource(Resources.PROCESS_DEFINITION);
        deanReadProcessDefinition.setResourceId("*");
        authorizationService.saveAuthorization(deanReadProcessDefinition);

        Authorization interviewteamTasklistAuth = authorizationService.createNewAuthorization(AUTH_TYPE_GRANT);
        interviewteamTasklistAuth.setGroupId("interviewteam");
        interviewteamTasklistAuth.addPermission(ACCESS);
        interviewteamTasklistAuth.setResourceId("tasklist");
        interviewteamTasklistAuth.setResource(APPLICATION);
        authorizationService.saveAuthorization(interviewteamTasklistAuth);

        Authorization interviewteamReadProcessDefinition = authorizationService.createNewAuthorization(AUTH_TYPE_GRANT);
        interviewteamReadProcessDefinition.setGroupId("interviewteam");
        interviewteamReadProcessDefinition.addPermission(Permissions.READ);
        interviewteamReadProcessDefinition.addPermission(Permissions.READ_HISTORY);
        interviewteamReadProcessDefinition.setResource(Resources.PROCESS_DEFINITION);
        interviewteamReadProcessDefinition.setResourceId("*");
        authorizationService.saveAuthorization(interviewteamReadProcessDefinition);

        Authorization admissioncommissionTasklistAuth = authorizationService.createNewAuthorization(AUTH_TYPE_GRANT);
        admissioncommissionTasklistAuth.setGroupId("admissioncommission");
        admissioncommissionTasklistAuth.addPermission(ACCESS);
        admissioncommissionTasklistAuth.setResourceId("tasklist");
        admissioncommissionTasklistAuth.setResource(APPLICATION);
        authorizationService.saveAuthorization(admissioncommissionTasklistAuth);

        Authorization admissioncommissionReadProcessDefinition = authorizationService.createNewAuthorization(AUTH_TYPE_GRANT);
        admissioncommissionReadProcessDefinition.setGroupId("admissioncommission");
        admissioncommissionReadProcessDefinition.addPermission(Permissions.READ);
        admissioncommissionReadProcessDefinition.addPermission(Permissions.READ_HISTORY);
        admissioncommissionReadProcessDefinition.setResource(Resources.PROCESS_DEFINITION);
        admissioncommissionReadProcessDefinition.setResourceId("*");
        authorizationService.saveAuthorization(admissioncommissionReadProcessDefinition);

        Authorization studyassistantTasklistAuth = authorizationService.createNewAuthorization(AUTH_TYPE_GRANT);
        studyassistantTasklistAuth.setGroupId("studyassistant");
        studyassistantTasklistAuth.addPermission(ACCESS);
        studyassistantTasklistAuth.setResourceId("tasklist");
        studyassistantTasklistAuth.setResource(APPLICATION);
        authorizationService.saveAuthorization(studyassistantTasklistAuth);

        Authorization studyassistantReadProcessDefinition = authorizationService.createNewAuthorization(AUTH_TYPE_GRANT);
        studyassistantReadProcessDefinition.setGroupId("studyassistant");
        studyassistantReadProcessDefinition.addPermission(Permissions.READ);
        studyassistantReadProcessDefinition.addPermission(Permissions.READ_HISTORY);
        studyassistantReadProcessDefinition.addPermission(Permissions.CREATE_INSTANCE);
        studyassistantReadProcessDefinition.setResource(Resources.PROCESS_DEFINITION);
        studyassistantReadProcessDefinition.setResourceId("*");
        authorizationService.saveAuthorization(studyassistantReadProcessDefinition);

        Authorization studyassistantCreateProcessDefinition = authorizationService.createNewAuthorization(AUTH_TYPE_GRANT);
        studyassistantCreateProcessDefinition.setGroupId("studyassistant");
        studyassistantCreateProcessDefinition.addPermission(Permissions.CREATE);
        studyassistantCreateProcessDefinition.setResource(Resources.PROCESS_INSTANCE);
        studyassistantCreateProcessDefinition.setResourceId("*");
        authorizationService.saveAuthorization(studyassistantCreateProcessDefinition);

        // create default filters
        FilterService filterService = processEngine.getFilterService();

        if (filterService.getFilter("My Tasks") == null) {
            LOGGER.info("Generating default filters for MSc BIS");
            Map<String, Object> filterProperties = new HashMap<String, Object>();
            filterProperties.put("description", "Tasks assigned to me");
            filterProperties.put("priority", -10);
            filterProperties.put("refresh", true);
            TaskService taskService = processEngine.getTaskService();
            TaskQuery query = taskService.createTaskQuery().taskAssigneeExpression("${currentUser()}");
            Filter myTasksFilter = filterService.newTaskFilter().setName("My Tasks").setProperties(filterProperties).setOwner("demo").setQuery(query);
            filterService.saveFilter(myTasksFilter);

            filterProperties.clear();
            filterProperties.put("description", "Tasks assigned to my Groups");
            filterProperties.put("priority", -5);
            filterProperties.put("refresh", true);
            query = taskService.createTaskQuery().taskCandidateGroupInExpression("${currentUserGroups()}").taskUnassigned();
            Filter groupTasksFilter = filterService.newTaskFilter().setName("My Group Tasks").setProperties(filterProperties).setOwner("demo").setQuery(query);
            filterService.saveFilter(groupTasksFilter);

            // global read authorizations for these filters
            Authorization globalMyTaskFilterRead = authorizationService.createNewAuthorization(Authorization.AUTH_TYPE_GLOBAL);
            globalMyTaskFilterRead.setResource(FILTER);
            globalMyTaskFilterRead.setResourceId(myTasksFilter.getId());
            globalMyTaskFilterRead.addPermission(READ);
            authorizationService.saveAuthorization(globalMyTaskFilterRead);

            Authorization globalGroupFilterRead = authorizationService.createNewAuthorization(Authorization.AUTH_TYPE_GLOBAL);
            globalGroupFilterRead.setResource(FILTER);
            globalGroupFilterRead.setResourceId(groupTasksFilter.getId());
            globalGroupFilterRead.addPermission(READ);
            authorizationService.saveAuthorization(globalGroupFilterRead);
        }
    }

}
