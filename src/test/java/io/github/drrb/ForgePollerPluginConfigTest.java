/**
 * go-forge-poller
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
 * along with go-forge-poller. If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.drrb;

import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.Property;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;

import static com.thoughtworks.go.plugin.api.material.packagerepository.Property.*;
import static io.github.drrb.ForgePollerPluginConfig.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;
import org.junit.Before;
import org.junit.Test;

public class ForgePollerPluginConfigTest {

    private ForgePollerPluginConfig config;

    @Before
    public void setUp() throws Exception {
        config = new ForgePollerPluginConfig();
    }

    @Test
    public void shouldAcceptRepoConfigIfForgeUrlSpecified() throws Exception {
        RepositoryConfiguration repoConfig = new RepositoryConfiguration();
        repoConfig.add(new Property(FORGE_URL, "http://forge.puppetlabs.com"));

        ValidationResult validationResult = config.isRepositoryConfigurationValid(repoConfig);

        assertThat(validationResult.isSuccessful(), is(true));
    }

    @Test
    public void shouldRejectRepoConfigIfUrlIsEmpty() throws Exception {
        RepositoryConfiguration repoConfig = new RepositoryConfiguration();
        repoConfig.add(new Property(FORGE_URL, "  "));

        ValidationResult validationResult = config.isRepositoryConfigurationValid(repoConfig);

        assertThat(validationResult.isSuccessful(), is(false));
        assertThat(validationResult.getMessages(), hasItem(equalTo("Forge URL is required")));
    }

    @Test
    public void shouldRejectRepoConfigIfUrlIsNull() throws Exception {
        RepositoryConfiguration repoConfig = new RepositoryConfiguration();
        repoConfig.add(new Property(FORGE_URL, null));

        ValidationResult validationResult = config.isRepositoryConfigurationValid(repoConfig);

        assertThat(validationResult.isSuccessful(), is(false));
        assertThat(validationResult.getMessages(), hasItem(equalTo("Forge URL is required")));
    }

    @Test
    public void shouldAcceptPackageConfigIfNoVersionsSpecified() throws Exception {
        PackageConfiguration packageConfig = new PackageConfiguration();
        packageConfig.add(new Property(MODULE_NAME, "puppetlabs/apache"));

        ValidationResult validationResult = config.isPackageConfigurationValid(packageConfig, new RepositoryConfiguration());

        assertThat(validationResult.isSuccessful(), is(true));
    }

    @Test
    public void shouldRejectPackageConfigIfModuleNameIsEmpty() throws Exception {
        PackageConfiguration packageConfig = new PackageConfiguration();
        packageConfig.add(new Property(MODULE_NAME, "  "));

        ValidationResult validationResult = config.isPackageConfigurationValid(packageConfig, new RepositoryConfiguration());

        assertThat(validationResult.isSuccessful(), is(false));
        assertThat(validationResult.getMessages(), hasItem(equalTo("Module name is required")));
    }

    @Test
    public void shouldRejectPackageConfigIfModuleNameIsNull() throws Exception {
        PackageConfiguration packageConfig = new PackageConfiguration();
        packageConfig.add(new Property(MODULE_NAME, null));

        ValidationResult validationResult = config.isPackageConfigurationValid(packageConfig, new RepositoryConfiguration());

        assertThat(validationResult.isSuccessful(), is(false));
        assertThat(validationResult.getMessages(), hasItem(equalTo("Module name is required")));
    }

    @Test
    public void shouldRejectPackageConfigIfModuleNameInWrongFormat() throws Exception {
        PackageConfiguration packageConfig = new PackageConfiguration();
        packageConfig.add(new Property(MODULE_NAME, "apache"));

        ValidationResult validationResult = config.isPackageConfigurationValid(packageConfig, new RepositoryConfiguration());

        assertThat(validationResult.isSuccessful(), is(false));
        assertThat(validationResult.getMessages(), hasItem(equalTo("Module name should be in format \"author/module\"")));
    }
}
