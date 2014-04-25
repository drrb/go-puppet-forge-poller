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

import com.thoughtworks.go.plugin.api.material.packagerepository.PackageRevision;

import java.util.Date;

public class ModuleVersion {
    private final String name;
    private final Version version;

    public ModuleVersion(String name, Version version) {
        this.name = name;
        this.version = version;
    }

    public static ModuleVersion with(ModuleSpec moduleSpec, Version version) {
        return new ModuleVersion(moduleSpec.getName(), version);
    }

    public Version getVersion() {
        return version;
    }

    public PackageRevision toPackageRevision() {
        //TODO: fill these in properly
        //return new PackageRevision(getVersion(), null, getAuthor(), "Version " + getVersion() + " released", release.getBaseUrl());
        PackageRevision revision = new PackageRevision(version.toString(), /* TODO: should this be just 'new Date()' when we don't know the last modified date? */ new Date(0), "user");
        //TODO: add these to make location/version available to pipeline as env variables
        //revision.addData("LOCATION", "http://example.com");
        //revision.addData("VERSION", getVersion());
        return revision;
    }
}
