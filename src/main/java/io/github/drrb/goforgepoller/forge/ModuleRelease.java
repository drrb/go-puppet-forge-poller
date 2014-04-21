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
package io.github.drrb.goforgepoller.forge;


import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageRevision;

import java.util.Date;

public class ModuleRelease extends GenericJson implements Comparable<ModuleRelease> {
    @Key
    private String version;

    public String getVersion() {
        return version;
    }

    public static ModuleRelease with(Version version) {
        ModuleRelease moduleRelease = new ModuleRelease();
        moduleRelease.version = version.toString();
        return moduleRelease;
    }

    @Override
    public int compareTo(ModuleRelease other) {
        return getVersion().compareTo(other.getVersion());
    }

    public static ModuleRelease fromPackageRevision(PackageRevision packageRevision) {
        return ModuleRelease.with(Version.of(packageRevision.getRevision()));
    }

    public PackageRevision toPackageRevision() {
        //TODO: fill these in properly
        //return new PackageRevision(release.getVersion(), null, release.getAuthor(), "Version " + release.getVersion() + " released", release.getBaseUrl());
        PackageRevision revision = new PackageRevision(getVersion(), new Date(0), "user");
        //TODO: add these to make location/version available to pipeline as env variables
//        revision.addData("LOCATION", "http://example.com");
//        revision.addData("VERSION", getVersion());
        return revision;
    }
}
