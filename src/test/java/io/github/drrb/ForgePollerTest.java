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

import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageRevision;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import com.thoughtworks.go.plugin.api.response.Result;
import io.github.drrb.forge.Forge;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static io.github.drrb.forge.Forge.PingFailure;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ForgePollerTest {
    private ForgePoller poller;
    @Mock
    private Forge forge;
    @Mock
    private ForgeFactory forgeFactory;

    private RepositoryConfiguration repoConfig;
    private PackageConfiguration packageConfig;
    private PackageRevision packageVersion;

    @Before
    public void setUp() throws Exception {
        poller = new ForgePoller(forgeFactory);

        packageConfig = new PackageConfiguration();
        repoConfig = new RepositoryConfiguration();

        when(forgeFactory.build(repoConfig)).thenReturn(forge);
    }

    @Test
    public void shouldReturnSuccessWhenForgeConnectionSucceeds() throws Exception {
        Result result = poller.checkConnectionToRepository(repoConfig);

        assertThat(result.isSuccessful(), is(true));
    }

    @Test
    public void shouldReturnFailureWhenForgeConnectionFails() throws Exception {
        doThrow(new PingFailure("Connection refused")).when(forge).ping();

        Result result = poller.checkConnectionToRepository(repoConfig);

        assertThat(result.isSuccessful(), is(false));
        assertThat(result.getMessages(), hasItem(containsString("Connection refused")));
    }

    @Test
    public void shouldReturnSuccessWhenPackageConnectionSucceeds() throws Exception {
        Result result = poller.checkConnectionToPackage(packageConfig, repoConfig);

        assertThat(result.isSuccessful(), is(true));
    }

    @Test
    public void shouldReturnFailureWhenPackageConnectionFails() throws Exception {
        doThrow(new PingFailure("Not found")).when(forge).ping(packageConfig);

        Result result = poller.checkConnectionToPackage(packageConfig, repoConfig);

        assertThat(result.isSuccessful(), is(false));
        assertThat(result.getMessages(), hasItem(containsString("Not found")));
    }

    @Test
    public void shouldReturnLatestVersionOfPackage() throws Exception {
        when(forge.getLatestVersion(packageConfig)).thenReturn(packageVersion);

        PackageRevision result = poller.getLatestRevision(packageConfig, repoConfig);

        assertThat(result, is(packageVersion));
    }

    @Test
    public void shouldReturnLatestVersionOfPackage() throws Exception {
        when(forge.getLatestVersion(packageConfig)).thenReturn(packageVersion);

        PackageRevision result = poller.getLatestRevision(packageConfig, repoConfig);

        assertThat(result, is(packageVersion));
    }
}
