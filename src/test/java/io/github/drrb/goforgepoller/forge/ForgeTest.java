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
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import io.github.drrb.goforgepoller.ForgePollerPluginConfig;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static io.github.drrb.goforgepoller.util.PropertyBuilder.property;
import static io.github.drrb.test.Matchers.url;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class ForgeTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

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

        forge = new Forge(new URL("http://forge.example.com/forge"), httpTransport);
    }

    @Test
    public void factoryCreatesForge() throws Exception {
        RepositoryConfiguration repoConfig = new RepositoryConfiguration();
        repoConfig.add(property(ForgePollerPluginConfig.FORGE_URL, "http://example.com"));
        forge = new Forge.Factory().build(repoConfig);
        assertThat(forge.getBaseUrl().toString(), is("http://example.com"));
    }

    @Test
    public void shouldRaiseExceptionIfUrlIsInvalid() throws Exception {
        RepositoryConfiguration repoConfig = new RepositoryConfiguration();
        repoConfig.add(property(ForgePollerPluginConfig.FORGE_URL, "x"));

        exception.expectMessage(containsString("no protocol"));
        forge = new Forge.Factory().build(repoConfig);
    }

    @Test
    public void shouldDoNothingIfPingReturns200() throws Exception {
        responses.put("http://forge.example.com/forge", response(200));
        forge.ping();
    }

    @Test(expected = Forge.PingFailure.class)
    public void shouldRaiseExceptionIfPingReturnsAnError() throws Exception {
        responses.put("http://forge.example.com/forge", response(404));
        forge.ping();
    }

    @Test
    public void shouldDoNothingIfPackagePingReturns200() throws Exception {
        responses.put("http://forge.example.com/forge/puppetlabs/apache.json", response(200));
        forge.ping(ModuleSpec.of("puppetlabs/apache"));
    }

    @Test(expected = Forge.PingFailure.class)
    public void shouldRaiseExceptionIfPackagePingReturnsAnError() throws Exception {
        responses.put("http://forge.example.com/forge/puppetlabs/apache.json", response(404));
        forge.ping(ModuleSpec.of("puppetlabs/apache"));
    }

    @Test
    public void shouldReturnLatestRelease() throws Exception {
        responses.put("http://forge.example.com/forge/puppetlabs/apache.json",
                response(200).setContent("{\"releases\":[{\"version\":\"1.0.1\"},{\"version\":\"1.0.10\"},{\"version\":\"0.11.0\"}]}"));
        responses.put("http://forge.example.com/forge/api/v1/releases.json?module=puppetlabs/apache",
                response(200).setContent("{\"puppetlabs/apache\":[{\"version\":\"1.0.1\"},{\"version\":\"0.11.0\"},{\"version\":\"1.0.10\", \"file\": \"/modules/puppetlabs/apache/1.0.0.tar.gz\"}]}"));

        ModuleVersion latestVersion = forge.getLatestVersion(ModuleSpec.of("puppetlabs/apache"));
        assertThat(latestVersion.getVersion(), is(Version.of("1.0.10")));
    }

    @Test
    public void shouldReturnLatestReleaseBeforeUpperBound() throws Exception {
        responses.put("http://forge.example.com/forge/puppetlabs/apache.json",
                response(200).setContent("{\"releases\":[{\"version\":\"1.0.1\"},{\"version\":\"1.0.10\"},{\"version\":\"0.11.0\"}]}"));
        responses.put("http://forge.example.com/forge/api/v1/releases.json?module=puppetlabs/apache",
                response(200).setContent("{\"puppetlabs/apache\":[{\"version\":\"1.0.1\"},{\"version\":\"1.0.10\"},{\"version\":\"0.11.0\", \"file\": \"/modules/puppetlabs/apache/0.11.0.tar.gz\"}]}"));

        ModuleVersion latestVersion = forge.getLatestVersion(ModuleSpec.of("puppetlabs/apache").withVersionLessThan(Version.of("1.0.0")));
        assertThat(latestVersion.getVersion(), is(Version.of("0.11.0")));
        assertThat(latestVersion.getUrl(), is(url("http://forge.example.com/forge/modules/puppetlabs/apache/0.11.0.tar.gz")));
    }

    @Test
    public void shouldRaiseExceptionIfLatestReleaseIsBeforeLowerBound() throws Exception {
        responses.put("http://forge.example.com/forge/puppetlabs/apache.json",
                response(200).setContent("{\"releases\":[{\"version\":\"1.0.1\"},{\"version\":\"1.0.10\"},{\"version\":\"0.11.0\"}]}"));

        exception.expect(Forge.ModuleNotFound.class);
        forge.getLatestVersion(ModuleSpec.of("puppetlabs/apache").withVersionGreaterThanOrEqualTo(Version.of("1.1.0")));
    }

    @Test
    public void shouldRaiseExceptionIfModuleNotFound() throws Exception {
        responses.put("http://forge.example.com/forge/puppetlabs/apache.json", response(404));

        exception.expect(Forge.ModuleNotFound.class);
        forge.getLatestVersion(ModuleSpec.of("puppetlabs/apache"));
    }

    @Test
    public void shouldRaiseExceptionIfModuleReleaseDetailsNotAvailable() throws Exception {
        responses.put("http://forge.example.com/forge/puppetlabs/apache.json",
                response(200).setContent("{\"releases\":[{\"version\":\"1.0.1\"},{\"version\":\"1.0.10\"},{\"version\":\"0.11.0\"}]}"));
        responses.put("http://forge.example.com/forge/api/v1/releases.json?module=puppetlabs/apache", response(500));

        exception.expect(Forge.ModuleNotFound.class);
        forge.getLatestVersion(ModuleSpec.of("puppetlabs/apache"));
    }

    @Test
    public void shouldCopeWithTrailingSlashOnUrl() throws Exception {
        forge = new Forge(new URL("http://forge.example.com/forge/"), httpTransport);

        responses.put("http://forge.example.com/forge/puppetlabs/apache.json", response(200));

        forge.ping(ModuleSpec.of("puppetlabs/apache"));
    }

    private MockLowLevelHttpResponse response(int statusCode) {
        return new MockLowLevelHttpResponse().setStatusCode(statusCode);
    }
}
