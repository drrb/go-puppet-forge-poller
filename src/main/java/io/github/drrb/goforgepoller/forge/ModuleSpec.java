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

import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;

import java.util.Objects;

import static io.github.drrb.goforgepoller.ForgePollerPluginConfig.*;

public class ModuleSpec {

    public static class Factory {
        public ModuleSpec build(PackageConfiguration packageConfig) {
            String moduleName = packageConfig.get(MODULE_NAME).getValue();
            String lowerVersionBoundNumber = packageConfig.get(LOWER_VERSION_BOUND_INCLUSIVE).getValue();
            String upperVersionBoundNumber = packageConfig.get(UPPER_VERSION_BOUND_EXCLUSIVE).getValue();
            return of(moduleName)
                    .withVersionGreaterThanOrEqualTo(Version.of(lowerVersionBoundNumber))
                    .withVersionLessThan(Version.of(upperVersionBoundNumber));
        }
    }

    private final String name;
    private final Version upperVersionBound;
    private final Version lowerVersionBound;

    public ModuleSpec(String name) {
        this(name, Version.ZERO, Version.INFINITY);
    }

    public ModuleSpec(String name, Version lowerVersionBound, Version upperVersionBound) {
        this.name = name;
        this.upperVersionBound = upperVersionBound;
        this.lowerVersionBound = lowerVersionBound;
    }

    public static ModuleSpec of(String moduleName) {
        return new ModuleSpec(moduleName);
    }

    public ModuleSpec withVersionLessThan(Version upperVersionBound) {
        return new ModuleSpec(name, lowerVersionBound, upperVersionBound);
    }

    public ModuleSpec withVersionGreaterThanOrEqualTo(Version lowerVersionBound) {
        return new ModuleSpec(name, lowerVersionBound, upperVersionBound);
    }

    public String getName() {
        return name;
    }

    public Version getLowerVersionBound() {
        return lowerVersionBound;
    }

    public Version getUpperVersionBound() {
        return upperVersionBound;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder(name);
        string.append(" >= ").append(lowerVersionBound);
        if (upperVersionBound != Version.INFINITY) {
            string.append(", < ").append(upperVersionBound);
        }
        return string.toString();
    }
}
