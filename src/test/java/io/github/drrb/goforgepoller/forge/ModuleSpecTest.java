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
import com.thoughtworks.go.plugin.api.material.packagerepository.Property;
import io.github.drrb.goforgepoller.ForgePollerPluginConfig;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ModuleSpecTest {

    @Test
    public void factoryCreatesInstance() throws Exception {
        PackageConfiguration packageConfig = new PackageConfiguration();
        packageConfig.add(new Property(ForgePollerPluginConfig.MODULE_NAME, "puppetlabs/apache"));
        packageConfig.add(new Property(ForgePollerPluginConfig.LOWER_VERSION_BOUND_INCLUSIVE, "1.0.0"));
        packageConfig.add(new Property(ForgePollerPluginConfig.UPPER_VERSION_BOUND_EXCLUSIVE, "1.1.0"));

        ModuleSpec moduleSpec = new ModuleSpec.Factory().build(packageConfig);

        assertThat(moduleSpec.getName(), is("puppetlabs/apache"));
        assertThat(moduleSpec.getLowerVersionBound(), is(Version.of("1.0.0")));
        assertThat(moduleSpec.getUpperVersionBound(), is(Version.of("1.1.0")));
    }

    @Test
    public void factoryIgnoresNullVersionNumbers() throws Exception {
        PackageConfiguration packageConfig = new PackageConfiguration();
        packageConfig.add(new Property(ForgePollerPluginConfig.MODULE_NAME, "puppetlabs/apache"));
        packageConfig.add(new Property(ForgePollerPluginConfig.LOWER_VERSION_BOUND_INCLUSIVE, null));
        packageConfig.add(new Property(ForgePollerPluginConfig.UPPER_VERSION_BOUND_EXCLUSIVE, null));

        ModuleSpec moduleSpec = new ModuleSpec.Factory().build(packageConfig);

        assertThat(moduleSpec.getLowerVersionBound(), is(Version.ZERO));
        assertThat(moduleSpec.getUpperVersionBound(), is(Version.INFINITY));
    }

    @Test
    public void factoryIgnoresEmptyVersionNumbers() throws Exception {
        PackageConfiguration packageConfig = new PackageConfiguration();
        packageConfig.add(new Property(ForgePollerPluginConfig.MODULE_NAME, "puppetlabs/apache"));
        packageConfig.add(new Property(ForgePollerPluginConfig.LOWER_VERSION_BOUND_INCLUSIVE, " "));
        packageConfig.add(new Property(ForgePollerPluginConfig.UPPER_VERSION_BOUND_EXCLUSIVE, "  "));

        ModuleSpec moduleSpec = new ModuleSpec.Factory().build(packageConfig);

        assertThat(moduleSpec.getLowerVersionBound(), is(Version.ZERO));
        assertThat(moduleSpec.getUpperVersionBound(), is(Version.INFINITY));
    }

}
