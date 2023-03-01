/*
 * This function is an example of an implementation of a REST endpoint.
 * For more documentation, check 
 * https://github.com/meveo-org/meveo/tree/develop/meveo-admin-ejbs/src/main/java/org/meveo/service/technicalservice/endpoint
 */

/* replace here by you module package name */
package org.meveo.example;

import java.util.Map;
import java.util.HashMap;
import java.time.Instant;
import org.meveo.service.script.Script;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.persistence.CrossStorageApi;
import org.meveo.model.storage.Repository;
import org.meveo.service.storage.RepositoryService;
import org.meveo.api.rest.technicalservice.EndpointScript;
import javax.servlet.http.Part;
import javax.servlet.ServletInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* Here we inherit request and response variable by extending EndpointScript */
public class ExempleEndpointScript extends Script {

    private static final Logger LOG = LoggerFactory.getLogger(PersistEntityScript.class);

    /* Service used to persist an entity */
    private final CrossStorageApi crossStorageApi = getCDIBean(CrossStorageApi.class);

    /* Service used to retrieve the default repository (holding the data stores configuration) */
    private final RepositoryService repositoryService = getCDIBean(RepositoryService.class);

    /* Default repository */
    private final Repository defaultRepo = repositoryService.findDefaultRepository();
     
    /* variable used to store the result of the script */
    private String result;

    private Map<String, Object> automaticSerializedResult = new HashMap<>();

    /* variable used to get the input of the endpoint */
    private String inputValue;
 
    /* By using a getter the GUI for creating a rest endpoint
    *  will detect 'result' as being an output of the function
    *  it can then be selected as the response of the endpoint
    */
    public String getResult() {
        return this.result;
    }

    /* We can use POJO as outputs and enable automatic serialization on endpoint's definition */
    public Map<String, Object> automaticSerializedResult() {
        return this.automaticSerializedResult;
    }
    
    /* By using a setter the GUI for creating a rest endpoint
    *  will detect 'inputValue' as being an input of the function
    *  it can then be selected as query, body, or path param of the endpoint
    */
    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
    }

    @Override
    public void execute(Map<String, Object> parameters) throws BusinessException {
        //by defaut the login level of meveo is info
        LOG.info("input:{}", inputValue);
        
       // Affect value to the output value 
       this.result = "Input value is : " + inputValue;
       this.automaticSerializedResult.put("result", this.result);

       // We can access every information on the incoming request by accessing endpointRequest
       // See https://github.com/meveo-org/meveo/blob/develop/meveo-api/src/main/java/org/meveo/api/rest/technicalservice/impl/EndpointRequest.java#L44
       String remainingPath = this.endpointRequest.getRemainingPath(); // Retrieve the remaining path after endpoint acess path
       String contentTypeHeader = this.endpointRequest.getHeader("Content-Type"); // Get any header
       String acceptedLanguages = this.endpointRequest.getHeaders("Accepted-Language"); // Headers can also be multi-valued
       String method = this.endpointRequest.getMethod(); // POST, GET, DELETE, etc ...
       String queryString = this.endpointRequest.getQueryString(); // Part of url after the ?
       Part part = this.endpointRequest.getPart("inputFile"); // Get any part of form-data input
       ServletInputStream is = this.endpointRequest.getInputStream(); // Retrieve input as stream

       // We can manipulate finely the response by accessing endpointResponse
       // See https://github.com/meveo-org/meveo/blob/develop/meveo-api/src/main/java/org/meveo/api/rest/technicalservice/impl/EndpointResponse.java#L35
       this.endpointResponse.setHeader("My-Header", "Header-Value"); // Set any response header
       this.endpointResponse.setOutput(this.result.getBytes()); // Set the output as byte array
       this.endpointResponse.setStatus(404); // Set response status
       this.endpointResponse.setContentType("application/xml"); // Override defined content-type for the endpoint
       this.endpointResponse.setError("There was an error"); // Custom error message
       this.endpointResponse.sendRedirect("https://redirected-url.com/789745967"); // Send redirection to client
    }

}
