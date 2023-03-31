/*
 * This function is an example of using the meveo credentials to retrieve and store credential details when calling
 * third party APIs
 * For more documentation check
 * https://github.com/meveo-org/meveo/tree/develop/meveo-admin-ejbs/src/main/java/org/meveo/service/admin/impl/credentials
 */

/* replace here by your module package name */
package org.meveo.example;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.MvCredential;
import org.meveo.service.admin.impl.credentials.CredentialHelperService;
import org.meveo.service.script.Script;

import org.apache.commons.lang3.StringUtils;

public class ExampleCredentialsScript extends Script {
    // this is the code of the credentials stored in meveo.
    private static final String CREDENTIAL_CODE = "TWILIO";
    // retrieve an instance of the CredentialHelperService using the built-in getCDIBean function of meveo Script class
    // most services in meveo can be retrieved this way too
    private final CredentialHelperService credentialHelperService = getCDIBean(CredentialHelperService.class);

    private String result;

    public String getResult() {
        return result;
    }

    @Override
    public void execute(Map<String, Object> parameters) throws BusinessException {
        super.execute(parameters);

        // Credentials can either be retrieved using their code as shown here or by their domain name:
        // e.g. MvCredential credential = credentialHelperService.getCredential(DOMAIN_NAME);
        MvCredential credential = credentialHelperService.getCredentialByCode(CREDENTIAL_CODE);

        // these properties are the properties available when using an OAuth type credential.
        // Other properties are available as described in the documentation.
        String DOMAIN = credential.getDomainName();
        String TWILIO_SID = credential.getUsername();
        String TWILIO_TOKEN = credential.getToken();

        // the extraParameters is a map that may contain other details that may be needed when invoking the API
        Map<String, String> credentialParameters = credential.getExtraParameters();

        // we can use the values directly
        String TWILIO_PHONE_NUMBER = credentialParameters.get("phoneNumber");

        // We can also define defaults by using apache common's StringUtils class
        String PROTOCOL = StringUtils.defaultIfBlank(credentialParameters.get("protocol"), "https://");
        String ACCOUNTS_PATH = StringUtils.defaultIfBlank(credentialParameters.get("accountsPath"),
                "/2010-04-01/Accounts/");
        String API_TYPE = StringUtils.defaultIfBlank(credentialParameters.get("apiType"), "/Messages.json");

        String url = PROTOCOL + DOMAIN + ACCOUNTS_PATH + TWILIO_SID + API_TYPE;

        String recipient = "+15551234567";
        Form map = new Form()
                .param("To", recipient)
                .param("From", TWILIO_PHONE_NUMBER)
                .param("Body", "Testing text message");

        String authString = TWILIO_SID + ":" + TWILIO_TOKEN;
        String AUTH_TOKEN = DatatypeConverter.printBase64Binary((authString).getBytes(StandardCharsets.UTF_8));

        String response = ClientBuilder
                .newClient()
                .target(url)
                .request(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "Basic " + AUTH_TOKEN)
                .post(Entity.form(map), Response.class)
                .readEntity(String.class);

        result = "{\"status\": \"success\", \"result\": " + response + "}";
    }
}
