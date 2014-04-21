/**
 * Go Forge Poller
 * Copyright (C) 2014 drrb
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Go Forge Poller. If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.drrb.goforgepoller;

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import com.thoughtworks.go.plugin.api.response.validation.ValidationError;
import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;

import static io.github.drrb.goforgepoller.util.DisplayedProperty.property;

public class ForgePollerPluginConfig implements PackageMaterialConfiguration {
    private static final Logger LOGGER = Logger.getLoggerFor(ForgePollerPluginConfig.class);
    public static final String FORGE_URL = "FORGE_URL";

    public static final String MODULE_NAME = "MODULE_NAME";
    public static final String LOWER_VERSION_BOUND_INCLUSIVE = "LOWER_VERSION_BOUND_INCLUSIVE";
    public static final String UPPER_VERSION_BOUND_EXCLUSIVE = "UPPER_VERSION_BOUND_EXCLUSIVE";

    public RepositoryConfiguration getRepositoryConfiguration() {
        RepositoryConfiguration repoConfig = new RepositoryConfiguration();
        repoConfig.add(property(FORGE_URL).withDisplayName("Forge URL").build());
        return repoConfig;
    }

    public PackageConfiguration getPackageConfiguration() {
        PackageConfiguration packageConfig = new PackageConfiguration();
        packageConfig.add(property(MODULE_NAME)
                .withDisplayName("Module Name")
                .withDisplayOrder(0)
                .build());
        packageConfig.add(property(LOWER_VERSION_BOUND_INCLUSIVE)
                .withRequired(false)
                .withDisplayName("Version to poll >=")
                .withDisplayOrder(1)
                .withPartOfIdentity(true)
                .build());
        packageConfig.add(property(UPPER_VERSION_BOUND_EXCLUSIVE)
                .withRequired(false)
                .withDisplayName("Version to poll <")
                .withDisplayOrder(2)
                .withPartOfIdentity(true)
                .build());
        return packageConfig;
    }

    @Override
    public ValidationResult isRepositoryConfigurationValid(RepositoryConfiguration repositoryConfiguration) {
        ValidationResult validationResult = new ValidationResult();
        String forgeUrl = repositoryConfiguration.get(FORGE_URL).getValue();
        LOGGER.info(String.format("Validating configuration for forge (URL = %s)", forgeUrl));
        if (forgeUrl == null || forgeUrl.trim().isEmpty()) {
            validationResult.addError(new ValidationError("Forge URL is required"));
        }
        return validationResult;
    }

    @Override
    public ValidationResult isPackageConfigurationValid(PackageConfiguration packageConfiguration, RepositoryConfiguration repositoryConfiguration) {
        ValidationResult validationResult = new ValidationResult();
        String moduleName = packageConfiguration.get(MODULE_NAME).getValue();
        LOGGER.info(String.format("Validating configuration for module (name = %s)", moduleName));
        if (moduleName == null || moduleName.trim().isEmpty()) {
            validationResult.addError(new ValidationError("Module name is required"));
        } else if (! moduleName.matches("\\A[a-z][a-z0-9_]*/[a-z][a-z0-9_]*\\Z")) {
            validationResult.addError(new ValidationError("Module name should be in format \"author/module\""));
        }
        return validationResult;
    }
}
