/**
 * Go Puppet Forge Poller
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
 * along with Go Puppet Forge Poller. If not, see <http://www.gnu.org/licenses />.
 */
package io.github.drrb.goforgepoller;

import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import com.thoughtworks.go.plugin.api.response.validation.ValidationError;
import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;
import io.github.drrb.goforgepoller.forge.Version;
import io.github.drrb.goforgepoller.util.EntryPoint;
import io.github.drrb.goforgepoller.util.Log;
import io.github.drrb.goforgepoller.util.SaferConfiguration;

import java.net.MalformedURLException;
import java.net.URL;

import static io.github.drrb.goforgepoller.util.PropertyBuilder.property;

public class ForgePollerPluginConfig implements PackageMaterialConfiguration {
    private static final Log LOG = Log.getLogFor(ForgePollerPluginConfig.class);

    public static final String FORGE_URL = "FORGE_URL";

    public static final String MODULE_NAME = "MODULE_NAME";
    public static final String LOWER_VERSION_BOUND_INCLUSIVE = "LOWER_VERSION_BOUND_INCLUSIVE";
    public static final String UPPER_VERSION_BOUND_EXCLUSIVE = "UPPER_VERSION_BOUND_EXCLUSIVE";

    @Override
    @EntryPoint
    public RepositoryConfiguration getRepositoryConfiguration() {
        RepositoryConfiguration repoConfig = new RepositoryConfiguration();
        repoConfig.add(property(FORGE_URL).withDisplayName("Forge URL").build());
        return repoConfig;
    }

    @Override
    @EntryPoint
    public PackageConfiguration getPackageConfiguration() {
        PackageConfiguration packageConfig = new PackageConfiguration();
        packageConfig.add(property(MODULE_NAME)
                .withDisplayName("Module Name")
                .withPartOfIdentity(true)
                .withDisplayOrder(0)
                .build());
        packageConfig.add(property(LOWER_VERSION_BOUND_INCLUSIVE)
                .withRequired(false)
                .withDisplayName("Version to poll >=")
                .withDisplayOrder(1)
                .build());
        packageConfig.add(property(UPPER_VERSION_BOUND_EXCLUSIVE)
                .withRequired(false)
                .withDisplayName("Version to poll <")
                .withDisplayOrder(2)
                .build());
        return packageConfig;
    }

    @Override
    @EntryPoint
    public ValidationResult isRepositoryConfigurationValid(RepositoryConfiguration repositoryConfiguration) {
        SaferConfiguration configuration = new SaferConfiguration(repositoryConfiguration);
        String forgeUrl = configuration.get(FORGE_URL);
        LOG.info("Validating configuration for forge (URL = %s)", forgeUrl);

        ValidationResult validationResult = new ValidationResult();
        if (forgeUrl.isEmpty()) {
            validationResult.addError(new ValidationError(FORGE_URL, "Forge URL is mandatory"));
        } else {
            try {
                URL url = new URL(forgeUrl);
                if (!url.getProtocol().matches("^https?$")) {
                    validationResult.addError(new ValidationError(FORGE_URL, "Forge URL must be an HTTP(S) URL"));
                }
            } catch (MalformedURLException e) {
                validationResult.addError(new ValidationError(FORGE_URL, "Forge URL must be a URL"));
            }
        }
        return validationResult;
    }

    @Override
    @EntryPoint
    public ValidationResult isPackageConfigurationValid(PackageConfiguration packageConfiguration, RepositoryConfiguration repositoryConfiguration) {
        SaferConfiguration configuration = new SaferConfiguration(packageConfiguration);
        String moduleName = configuration.get(MODULE_NAME);
        String lowerVersionBound = configuration.get(LOWER_VERSION_BOUND_INCLUSIVE);
        String upperVersionBound = configuration.get(UPPER_VERSION_BOUND_EXCLUSIVE);
        LOG.info("Validating configuration for module (name = %s, lowerVersionBound = %s, upperVersionBound = %s)", moduleName, lowerVersionBound, upperVersionBound);

        ValidationResult validationResult = new ValidationResult();

        if (moduleName.isEmpty()) {
            validationResult.addError(new ValidationError(MODULE_NAME, "Module name is mandatory"));
        } else if (!moduleName.matches("\\A[a-z][a-z0-9_]*/[a-z][a-z0-9_]*\\Z")) {
            validationResult.addError(new ValidationError(MODULE_NAME, "Module name should be in format \"author/module\""));
        }

        if (!(lowerVersionBound.isEmpty() || Version.isValid(lowerVersionBound))) {
            validationResult.addError(new ValidationError(LOWER_VERSION_BOUND_INCLUSIVE, "Version to poll >= should be a version number"));
        }

        if (!(upperVersionBound.isEmpty() || Version.isValid(upperVersionBound))) {
            validationResult.addError(new ValidationError(UPPER_VERSION_BOUND_EXCLUSIVE, "Version to poll < should be a version number"));
        }

        return validationResult;
    }
}
