/*
 * This function calls a script by its code
 * For more documentation check
 * https://github.com/meveo-org/meveo/blob/develop/meveo-admin-ejbs/src/main/java/org/meveo/service/script/README.md#calling-a-script-from-another-script
 */


/* replace here by your module package name */
package org.meveo.example;

import java.util.HashMap;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecuteScriptByCode extends Script {

    private static final Logger LOG = LoggerFactory.getLogger(ExecuteScriptByCode.class);

    /* Service used to exeute a script knowing its code */
    private final ScriptInstanceService scriptInstanceService = getCDIBean(ScriptInstanceService.class);

    @Override
    public void execute(Map<String, Object> parameters) throws BusinessException {
        try {
            // Create a context containing input and output parameters of the script to call
            // we put in this map the input parameters 
            Map<String, Object> context = new HashMap<>();
            context.put("inputParam1", "value1");

            // we call the script escute method whose code is known
            // note that this method do not call the init or finilaze method of the script
            scriptInstanceService.execute("MyScriptCode", context);

            // during the execution the script can change the value of some input parameters
            // or add output parameters to the context
            LOG.info("Script execution result:{}", context.get("outputParam1"));

        } catch (BusinessException ex) {
            LOG.error("cannot execute Script", ex);
        }
    }

}
