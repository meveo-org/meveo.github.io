/*
 * This function is an example of Module script that make sure some role exists in DB before the module is installed
 */

/* replace here by your module package name */
package org.meveo.example;

import java.util.Map;

import org.meveo.model.security.Role;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.admin.exception.BusinessException;
import org.meveo.service.script.module.ModuleScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddAppUserRole extends ModuleScript {
    private static final Logger LOG = LoggerFactory.getLogger(AddAppUserRole.class);
    private final RoleService roleService = getCDIBean(RoleService.class);

    @Override
    public void preInstallModule(Map<String, Object> methodContext) throws BusinessException {
        Role role = null;
        try {
            role = roleService.findByName("APP_USER");
        } catch (Exception e) {
            // do nothing, we will create if it does not exist.
        }

        if (role == null) {
            role = new Role();
            role.setName("APP_USER");
            role.setDescription("Role for Liquichain App secured endpoints ");

            try {
                roleService.create(role);
            } catch (Exception e) {
                LOG.error("Failed to add APP_USER role.", e);
            }
        } else {
            LOG.debug("APP_USER role already exists.");
        }
    }
}
