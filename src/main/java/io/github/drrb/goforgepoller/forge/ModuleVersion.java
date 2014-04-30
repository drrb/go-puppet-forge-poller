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

import java.net.URL;
import java.util.Date;

public class ModuleVersion {
    private final String name;
    private final Version version;
    private final URL url;

    public ModuleVersion(String name, Version version, URL url) {
        this.name = name;
        this.version = version;
        this.url = url;
    }

    public static ModuleVersion with(ModuleSpec moduleSpec, Version version, URL url) {
        return new ModuleVersion(moduleSpec.getName(), version, url);
    }

    public Version getVersion() {
        return version;
    }

    public URL getUrl() {
        return url;
    }

    public PackageRevision toPackageRevision() {
        PackageRevision revision = new PackageRevision(version.toString(), new Date(), name.replaceAll("^(.*)/.*$", "$1"), null, getUrl().toString());
        revision.addData("LOCATION", getUrl().toString());
        revision.addData("VERSION", getVersion().toString());
        return revision;
    }
}
