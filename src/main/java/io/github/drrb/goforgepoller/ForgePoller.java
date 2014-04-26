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

import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialPoller;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageRevision;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import com.thoughtworks.go.plugin.api.response.Result;
import io.github.drrb.goforgepoller.forge.Forge;
import io.github.drrb.goforgepoller.forge.ModuleSpec;
import io.github.drrb.goforgepoller.forge.ModuleVersion;
import io.github.drrb.goforgepoller.forge.Version;
import io.github.drrb.goforgepoller.util.Log;

import static io.github.drrb.goforgepoller.util.Results.error;
import static io.github.drrb.goforgepoller.util.Results.success;

public class ForgePoller implements PackageMaterialPoller {

    private static final Log LOG = Log.getLogFor(ForgePoller.class);
    private final Forge.Factory forgeFactory;
    private final ModuleSpec.Factory moduleSpecFactory;

    public ForgePoller(Forge.Factory forgeFactory, ModuleSpec.Factory moduleSpecFactory) {
        this.forgeFactory = forgeFactory;
        this.moduleSpecFactory = moduleSpecFactory;
    }

    @Override
    public Result checkConnectionToRepository(RepositoryConfiguration repositoryConfiguration) {
        LOG.debug("checkConnectionToRepository(%s)", repositoryConfiguration);

        Forge forge = forgeFactory.build(repositoryConfiguration);
        LOG.info("Checking connection to forge at %s", forge);

        try {
            forge.ping();
            return success();
        } catch (Forge.PingFailure pingFailure) {
            return error(pingFailure);
        }
    }

    @Override
    public Result checkConnectionToPackage(PackageConfiguration packageConfiguration, RepositoryConfiguration repositoryConfiguration) {
        LOG.debug("checkConnectionToPackage(%s, %s)", packageConfiguration, repositoryConfiguration);

        ModuleSpec moduleSpec = moduleSpecFactory.build(packageConfiguration);
        Forge forge = forgeFactory.build(repositoryConfiguration);
        LOG.info("Checking connection to module %s in forge at %s", moduleSpec, forge);

        try {
            forge.ping(moduleSpec);
            return success("Found %s.", moduleSpec.getName());
        } catch (Forge.PingFailure pingFailure) {
            return error(pingFailure);
        }
    }

    @Override
    public PackageRevision getLatestRevision(PackageConfiguration packageConfiguration, RepositoryConfiguration repositoryConfiguration) {
        LOG.debug("getLatestRevision(%s, %s)", packageConfiguration, repositoryConfiguration);

        ModuleSpec module = moduleSpecFactory.build(packageConfiguration);
        Forge forge = forgeFactory.build(repositoryConfiguration);

        LOG.info("Looking up latest revision of module %s in forge %s", module, forge);
        try {
            ModuleVersion latestRelease = forge.getLatestVersion(module);
            return latestRelease.toPackageRevision();
        } catch (Forge.ModuleNotFound moduleNotFound) {
            LOG.info("Module %s not found in forge %s: %s", module, forge, moduleNotFound);
            return null;
        }
    }

    @Override
    public PackageRevision latestModificationSince(PackageConfiguration packageConfiguration, RepositoryConfiguration repositoryConfiguration, PackageRevision lastKnownRevision) {
        LOG.debug("latestModificationSince(%s, %s, %s)", packageConfiguration, repositoryConfiguration, lastKnownRevision);

        ModuleSpec module = moduleSpecFactory.build(packageConfiguration);
        Forge forge = forgeFactory.build(repositoryConfiguration);
        Version lastKnownReleaseVersion = Version.of(lastKnownRevision.getRevision());

        LOG.info("Looking up latest release of module %s in forge %s since version %s", module, forge, lastKnownReleaseVersion);
        try {
            ModuleVersion latestRelease = forge.getLatestVersion(module);
            if (latestRelease.getVersion().isGreaterThan(lastKnownReleaseVersion)) {
                //TODO: warn if this release is earlier than lastKnownRevision
                return latestRelease.toPackageRevision();
            } else {
                return null;
            }
        } catch (Forge.ModuleNotFound moduleNotFound) {
            LOG.info("Module %s not found in forge %s: %s", module, forge, moduleNotFound);
            return null;
        }
    }
}
