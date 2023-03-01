/*
 * This function is an example of entity creation and peristence
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


/* Here we use a generic script by extending Script */
public class PersistEntityScript extends Script {

    private static final Logger LOG = LoggerFactory.getLogger(PersistEntityScript.class);
    
    /* Service used to persist an entity */
    private final CrossStorageApi crossStorageApi = getCDIBean(CrossStorageApi.class);

    /* Service used to retrieve the default repository (holding the data stores configuration) */
    private final RepositoryService repositoryService = getCDIBean(RepositoryService.class);

    /* Default repository */
    private final Repository defaultRepo = repositoryService.findDefaultRepository();
  
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
         * Let assume some 'name' and 'desription' are sent to the function,
         * This is done for instance by calling a POST rest endpoint 
         * with a json body:
         * {
         *   "name":"Jboss",
         *   "description":"An opensource application server"
         * }
         */
        
         /*
         we can implement some validation and return an error if it fails
         by throwing a BusinessException the Rest endpoint would return an error
         here we want it to succeed (HTTP code 200) but write the error in a message
        */
        if(!parameters.containsKey("name")){
            result = "{\"status\": \"failed\", \"result\": \"No name provided\"}";
            return;
        }

        String name = (String)parameters.get("name");
        String description = (String)parameters.get("description");
        
        try {
            MyCustomEntity existingEntity = crossStorageApi.find(defaultRepo, MyCustomEntity.class)
            .by("name", name)
            .getResult();
            if(existingEntity!=null){
                throw new BusinessException("An entity with the same name already exists.");
            }

        	MyCustomEntity newEntity = new MyCustomEntity();
            newEntity.setName(name);
            newEntity.setDescription(description);
            newEntity.setCreationDate(Instant.now());
          	
            String uuid = crossStorageApi.createOrUpdate(defaultRepo, newEntity);

            LOG.info("Entity created with Id: {}",uuid);
            result = "{\"status\": \"success\", \"result\": \"" + uuid + "\"}";
        } catch (Exception ex) {
            String errorMessage = ex.getMessage();
            result = "{\"status\": \"failed\", \"result\": \"" + errorMessage + "\"}";
            LOG.error(errorMessage, ex);
        }
    }

}
