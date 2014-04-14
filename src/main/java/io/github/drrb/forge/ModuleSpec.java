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
package io.github.drrb.forge;

import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;

import java.util.Objects;

import static io.github.drrb.ForgePollerPluginConfig.MODULE_NAME;

public class ModuleSpec {
    private final String name;
    private final Version upperVersionBound;

    public ModuleSpec(String name) {
        this(name, Version.INFINITY);
    }

    public ModuleSpec(String name, Version upperVersionBound) {
        this.name = name;
        this.upperVersionBound = upperVersionBound;
    }

    public static ModuleSpec of(String moduleName) {
        return new ModuleSpec(moduleName);
    }

    public static ModuleSpec from(PackageConfiguration packageConfig) {
        return of(packageConfig.get(MODULE_NAME).getValue());
    }

    public ModuleSpec withVersionLessThan(Version upperVersionBound) {
        return new ModuleSpec(name, upperVersionBound);
    }

    public String getName() {
        return name;
    }

    public Version getUpperVersionBound() {
        return upperVersionBound;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (other.getClass() != getClass()) return false;

        ModuleSpec that = (ModuleSpec) other;
        return Objects.equals(this.name, that.name)
                && Objects.equals(this.upperVersionBound, that.upperVersionBound);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, upperVersionBound);
    }
}
