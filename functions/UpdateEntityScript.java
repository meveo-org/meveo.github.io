/*
 * This function is an example of entity update
 * for more documentation check 
 * https://github.com/meveo-org/meveo/tree/develop/meveo-admin-ejbs/src/main/java/org/meveo/api/persistence#ii1-persisting-an-entity
 */


/* replace here by you module package name */
package org.meveo.example;

import java.util.Map;
import java.time.Instant;
import org.meveo.service.script.Script;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.persistence.CrossStorageApi;
import org.meveo.model.storage.Repository;
import org.meveo.service.storage.RepositoryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* Replace here by your Custom entity class  */
import org.meveo.model.customEntities.MyCustomEntity;


public class UpdateEntityScript extends Script {

    private static final Logger LOG = LoggerFactory.getLogger(PersistEntityScript.class);
    
    /* Service used to persist an entity */
    private final CrossStorageApi crossStorageApi = getCDIBean(CrossStorageApi.class);

    /* Service used to retrieve the default repository (holding the data stores configuration) */
    private final RepositoryService repositoryService = getCDIBean(RepositoryService.class);

    /* Default repository */
    private final Repository defaultRepo = repositoryService.findDefaultRepository();
  
    /* variable used to retrieve the id of the entity to update */
    private String entityUuid;

    /* By using a setter the GUI for creating a rest endpoint
    *  will detect 'enityUuid' as being an input of the function
    *  it can then be selected as a path parameter of the endpoint
    */
    private void setEntityUuid(String entityUuid){
        this.entityUuid = entityUuid;
    }

    /* variable used to store the result of the script */
    private String result;
 
    /* By using a getter the GUI for creating a rest endpoint
    *  will detect 'result' as being an output of the function
    *  it can then be selected as the response of the endpoint
    */
    public String getResult() {
        return this.result;
    }

    @Override
    public void execute(Map<String, Object> parameters) throws BusinessException {
        //by defaut the login level of meveo is info
        LOG.info("input:{}",parameters);

        /* 
         * Let's assume the 'uuid' of the entity to update is sent in parameters,
         * This is done for instance by calling a POST rest endpoint 
         * with a json body:
         * {
         *   "name":"Wildfly",
         *   "description":"A java opensource application server"
         * }
         */
        
        String name = (String)parameters.get("name");
        String description = (String)parameters.get("description");
        
        try {
            // lookup the entity by its uuid
            MyCustomEntity existingEntity = crossStorageApi.find(defaultRepo, entityUuid, MyCustomEntity.class);
            
            newEntity.setName(name);
            newEntity.setDescription(description);
            newEntity.setLastUpdate(Instant.now());
          	
            String uuid = crossStorageApi.createOrUpdate(defaultRepo, newEntity);

            LOG.info("Entity  with Id {} updated ",uuid);
            result = "{\"status\": \"success\", \"result\": \"" + uuid + "\"}";
        } catch (Exception ex) {
            String errorMessage = ex.getMessage();
            result = "{\"status\": \"failed\", \"result\": \"" + errorMessage + "\"}";
            LOG.error(errorMessage, ex);
        }
    }

}
