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

import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.thoughtworks.go.plugin.api.material.packagerepository.Property;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import io.github.drrb.goforgepoller.ForgePollerPluginConfig;
import io.github.drrb.goforgepoller.forge.api.ModuleRelease;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ForgeTest {
    private Forge forge;
    private MockHttpTransport httpTransport;
    private Map<String, MockLowLevelHttpResponse> responses = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        httpTransport = new MockHttpTransport() {
            @Override
            public LowLevelHttpRequest buildRequest(String method, final String url) throws IOException {
                return new MockLowLevelHttpRequest() {
                    @Override
                    public LowLevelHttpResponse execute() throws IOException {
                        assertThat("Unexpected request to " + url, responses.keySet(), hasItem(url));
                        return responses.get(url);
                    }
                };
            }
        };

        forge = new Forge("http://forge.example.com/forge", httpTransport);
    }

    @Test
    public void factoryCreatesForge() throws Exception {
        RepositoryConfiguration repoConfig = new RepositoryConfiguration();
        repoConfig.add(new Property(ForgePollerPluginConfig.FORGE_URL, "http://example.com"));
        forge = new Forge.Factory().build(repoConfig);
        assertThat(forge.getBaseUrl().toString(), is("http://example.com"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldRaiseExceptionIfUrlIsInvalid() throws Exception {
        forge = new Forge("x", httpTransport);
        forge.ping();
    }

    @Test
    public void shouldDoNothingIfPingReturns200() throws Exception {
        responses.put("http://forge.example.com/forge", new MockLowLevelHttpResponse().setStatusCode(200));
        forge.ping();
    }

    @Test(expected = Forge.PingFailure.class)
    public void shouldRaiseExceptionIfPingReturnsAnError() throws Exception {
        responses.put("http://forge.example.com/forge", new MockLowLevelHttpResponse().setStatusCode(404));
        forge.ping();
    }

    @Test
    public void shouldDoNothingIfPackagePingReturns200() throws Exception {
        responses.put("http://forge.example.com/forge/puppetlabs/apache.json", new MockLowLevelHttpResponse().setStatusCode(200));
        forge.ping(ModuleSpec.of("puppetlabs/apache"));
    }

    @Test(expected = Forge.PingFailure.class)
    public void shouldRaiseExceptionIfPackagePingReturnsAnError() throws Exception {
        responses.put("http://forge.example.com/forge/puppetlabs/apache.json", new MockLowLevelHttpResponse().setStatusCode(404));
        forge.ping(ModuleSpec.of("puppetlabs/apache"));
    }

    @Test
    public void shouldReturnLatestRelease() throws Exception {
        String metadata = "{\"releases\":[{\"version\":\"1.0.1\"},{\"version\":\"1.0.10\"},{\"version\":\"0.11.0\"}]}";
        MockLowLevelHttpResponse response = new MockLowLevelHttpResponse().setStatusCode(200).setContent(metadata);
        responses.put("http://forge.example.com/forge/puppetlabs/apache.json", response);

        ModuleVersion latestVersion = forge.getLatestVersion(ModuleSpec.of("puppetlabs/apache"));
        assertThat(latestVersion.getVersion(), is(Version.of("1.0.10")));
    }

    @Test
    public void shouldReturnLatestReleaseBeforeUpperBound() throws Exception {
        String metadata = "{\"releases\":[{\"version\":\"1.0.1\"},{\"version\":\"1.0.10\"},{\"version\":\"0.11.0\"}]}";
        MockLowLevelHttpResponse response = new MockLowLevelHttpResponse().setStatusCode(200).setContent(metadata);
        responses.put("http://forge.example.com/forge/puppetlabs/apache.json", response);

        ModuleVersion latestVersion = forge.getLatestVersion(ModuleSpec.of("puppetlabs/apache").withVersionLessThan(Version.of("1.0.0")));
        assertThat(latestVersion.getVersion(), is(Version.of("0.11.0")));
    }

    @Test(expected = Forge.ModuleNotFound.class)
    public void shouldRaiseExceptionIfLatestReleaseIsBeforeLowerBound() throws Exception {
        String metadata = "{\"releases\":[{\"version\":\"1.0.1\"},{\"version\":\"1.0.10\"},{\"version\":\"0.11.0\"}]}";
        MockLowLevelHttpResponse response = new MockLowLevelHttpResponse().setStatusCode(200).setContent(metadata);
        responses.put("http://forge.example.com/forge/puppetlabs/apache.json", response);

        forge.getLatestVersion(ModuleSpec.of("puppetlabs/apache").withVersionGreaterThanOrEqualTo(Version.of("1.1.0")));
    }

    @Test(expected = Forge.ModuleNotFound.class)
    public void shouldRaiseExceptionIfModuleNotFound() throws Exception {
        MockLowLevelHttpResponse response = new MockLowLevelHttpResponse().setStatusCode(404);
        responses.put("http://forge.example.com/forge/puppetlabs/apache.json", response);

        forge.getLatestVersion(ModuleSpec.of("puppetlabs/apache"));
    }
}
