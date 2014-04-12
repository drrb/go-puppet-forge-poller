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

import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageRevision;
import com.thoughtworks.go.plugin.api.material.packagerepository.Property;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.github.drrb.ForgePollerPluginConfig.FORGE_URL;
import static io.github.drrb.ForgePollerPluginConfig.MODULE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ForgeTest {
    private Forge forge;
    private MockHttpTransport httpTransport;
    private Map<String, MockLowLevelHttpResponse> responses = new HashMap<String, MockLowLevelHttpResponse>();
    private PackageConfiguration packageConfig;

    @Before
    public void setUp() throws Exception {
        httpTransport = new MockHttpTransport() {
            @Override
            public LowLevelHttpRequest buildRequest(String method, final String url) throws IOException {
                return new MockLowLevelHttpRequest() {
                    @Override
                    public LowLevelHttpResponse execute() throws IOException {
                        return responses.get(url);
                    }
                };
            }
        };

        RepositoryConfiguration repoConfig = new RepositoryConfiguration();
        repoConfig.add(new Property(FORGE_URL, "http://forge.example.com"));
        forge = new Forge(repoConfig, httpTransport);

        packageConfig = new PackageConfiguration();
        packageConfig.add(new Property(MODULE_NAME, "puppetlabs/apache"));
    }

    @Test
    public void shouldDoNothingIfPingReturns200() throws Exception {
        responses.put("http://forge.example.com", new MockLowLevelHttpResponse().setStatusCode(200));
        forge.ping();
    }

    @Test(expected = Forge.PingFailure.class)
    public void shouldRaiseExceptionIfPingReturnsAnError() throws Exception {
        responses.put("http://forge.example.com", new MockLowLevelHttpResponse().setStatusCode(404));
        forge.ping();
    }

    @Test
    public void shouldDoNothingIfPackagePingReturns200() throws Exception {
        responses.put("http://forge.example.com/puppetlabs/apache.json", new MockLowLevelHttpResponse().setStatusCode(200));
        forge.ping(packageConfig);
    }

    @Test(expected = Forge.PingFailure.class)
    public void shouldRaiseExceptionIfPackagePingReturnsAnError() throws Exception {
        responses.put("http://forge.example.com/puppetlabs/apache.json", new MockLowLevelHttpResponse().setStatusCode(404));
        forge.ping(packageConfig);
    }

    @Test
    public void shouldReturnLatestRelease() throws Exception {
        String metadata = "{\"releases\":[{\"version\":\"1.0.1\"},{\"version\":\"1.0.10\"},{\"version\":\"0.11.0\"}]}";
        MockLowLevelHttpResponse response = new MockLowLevelHttpResponse().setStatusCode(200).setContent(metadata);
        responses.put("http://forge.example.com/puppetlabs/apache.json", response);

        PackageRevision latestVersion = forge.getLatestVersion(packageConfig);
        assertThat(latestVersion.getRevision(), is("1.0.10"));
    }
}
