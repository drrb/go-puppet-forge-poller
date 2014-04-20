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
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialPoller;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageRevision;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import com.thoughtworks.go.plugin.api.response.Result;
import io.github.drrb.goforgepoller.forge.Forge;
import io.github.drrb.goforgepoller.forge.ModuleRelease;
import io.github.drrb.goforgepoller.forge.ModuleSpec;
import io.github.drrb.goforgepoller.util.Exceptions;

public class ForgePoller implements PackageMaterialPoller {

    private static final Logger LOGGER = Logger.getLoggerFor(ForgePoller.class);
    private final Forge.Factory forgeFactory;
    private final ModuleSpec.Factory moduleSpecFactory;

    public ForgePoller(Forge.Factory forgeFactory, ModuleSpec.Factory moduleSpecFactory) {
        this.forgeFactory = forgeFactory;
        this.moduleSpecFactory = moduleSpecFactory;
    }

    @Override
    public Result checkConnectionToRepository(RepositoryConfiguration repositoryConfiguration) {
        Forge forge = forgeFactory.build(repositoryConfiguration);
        try {
            forge.ping();
            return success();
        } catch (Forge.PingFailure pingFailure) {
            return error(pingFailure);
        }
    }

    @Override
    public Result checkConnectionToPackage(PackageConfiguration packageConfiguration, RepositoryConfiguration repositoryConfiguration) {
        ModuleSpec moduleSpec = moduleSpecFactory.build(packageConfiguration);
        Forge forge = forgeFactory.build(repositoryConfiguration);

        try {
            forge.ping(moduleSpec);
            return success("Found %s.", moduleSpec.getName());
        } catch (Forge.PingFailure pingFailure) {
            return error(pingFailure);
        }
    }

    @Override
    public PackageRevision getLatestRevision(PackageConfiguration packageConfiguration, RepositoryConfiguration repositoryConfiguration) {
        ModuleSpec module = moduleSpecFactory.build(packageConfiguration);
        Forge forge = forgeFactory.build(repositoryConfiguration);
        log("Looking up latest revision of module %s in forge %s", module, forge);

        try {
            ModuleRelease latestRelease = forge.getLatestVersion(module);
            return latestRelease.toPackageRevision();
        } catch (Forge.ModuleNotFound moduleNotFound) {
            log("Module %s not found in forge %s: %s", module, forge, moduleNotFound);
            return null;
        }
    }

    @Override
    public PackageRevision latestModificationSince(PackageConfiguration packageConfiguration, RepositoryConfiguration repositoryConfiguration, PackageRevision lastKnownRevision) {
        ModuleSpec module = moduleSpecFactory.build(packageConfiguration);
        Forge forge = forgeFactory.build(repositoryConfiguration);
        log("Looking up latest revision of module %s in forge %s since version %s", module, forge, lastKnownRevision.getRevision());

        try {
            ModuleRelease latestRelease = forge.getLatestVersion(module);
            //TODO: warn if this release is earlier than lastKnownRevision
            return latestRelease.toPackageRevision();
        } catch (Forge.ModuleNotFound moduleNotFound) {
            log("Module %s not found in forge %s: %s", module, forge, moduleNotFound);
            return null;
        }
    }

    private Result success(String message, String... args) {
        return success().withSuccessMessages(String.format(message, args));
    }

    private Result success() {
        return new Result();
    }

    private Result error(Throwable error) {
        return new Result().withErrorMessages(Exceptions.render(error));
    }

    protected void log(String message, Object... args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Throwable) {
                args[i] = Exceptions.render((Throwable) args[i]);
            }
        }
        LOGGER.info(String.format(message, args));
    }
}
