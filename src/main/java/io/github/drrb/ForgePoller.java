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
package io.github.drrb;

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialPoller;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageRevision;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import com.thoughtworks.go.plugin.api.response.Result;
import io.github.drrb.forge.Forge;
import io.github.drrb.forge.ModuleRelease;
import io.github.drrb.forge.ModuleSpec;
import io.github.drrb.util.Exceptions;

import static io.github.drrb.ForgePollerPluginConfig.FORGE_URL;
import static io.github.drrb.ForgePollerPluginConfig.MODULE_NAME;

public class ForgePoller implements PackageMaterialPoller {

    private static final Logger LOGGER = Logger.getLoggerFor(ForgePoller.class);
    private final ForgeFactory forgeFactory;

    public ForgePoller(ForgeFactory forgeFactory) {
        this.forgeFactory = forgeFactory;
    }

    @Override
    public Result checkConnectionToRepository(RepositoryConfiguration repositoryConfiguration) {
        Forge forge = forgeFactory.build(repositoryConfiguration);
        try {
            forge.ping();
            return new Result().withSuccessMessages("Connection successful");
        } catch (Forge.PingFailure pingFailure) {
            return new Result().withErrorMessages(Exceptions.render(pingFailure));
        }
    }

    @Override
    public Result checkConnectionToPackage(PackageConfiguration packageConfiguration, RepositoryConfiguration repositoryConfiguration) {
        ModuleSpec moduleSpec = ModuleSpec.from(packageConfiguration);
        Forge forge = forgeFactory.build(repositoryConfiguration);

        try {
            forge.ping(moduleSpec);
            return new Result().withSuccessMessages("Found " + moduleSpec.getName());
        } catch (Forge.PingFailure pingFailure) {
            return new Result().withErrorMessages(Exceptions.render(pingFailure));
        }
    }

    @Override
    public PackageRevision getLatestRevision(PackageConfiguration packageConfiguration, RepositoryConfiguration repositoryConfiguration) {
        log("getLatestRevision called with module %s, for forge %s", packageConfiguration.get(MODULE_NAME).getValue(), repositoryConfiguration.get(FORGE_URL).getValue());
        Forge forge = forgeFactory.build(repositoryConfiguration);
        try {
            ModuleRelease latestRelease = forge.getLatestVersion(ModuleSpec.from(packageConfiguration));
            //TODO: fill these in properly
            //return new PackageRevision(latestRelease.getVersion(), null, latestRelease.getAuthor(), "Version " + latestRelease.getVersion() + " released", latestRelease.getUrl());
            return new PackageRevision(latestRelease.getVersion(), null, null);
        } catch (Forge.ModuleNotFound moduleNotFound) {
            log(Exceptions.render(moduleNotFound));
            return null;
        }
    }

    @Override
    public PackageRevision latestModificationSince(PackageConfiguration packageConfiguration, RepositoryConfiguration repositoryConfiguration, PackageRevision packageRevision) {
        log("latestModificationSince %s called with module %s, for forge %s", packageRevision.getRevision(), packageConfiguration.get(MODULE_NAME).getValue(), repositoryConfiguration.get(FORGE_URL).getValue());
        //TODO: inline this and warn if latest version is earlier than previous known one (like the Maven repo poller does)
        return getLatestRevision(packageConfiguration, repositoryConfiguration);
    }

    protected void log(String message, Object... args) {
        LOGGER.info(String.format(message, args));
    }
}
