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

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageRevision;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import io.github.drrb.ForgePollerPluginConfig;

import java.io.IOException;

import static io.github.drrb.ForgePollerPluginConfig.MODULE_NAME;

public class Forge {
    public static class PingFailure extends Exception {

        public PingFailure(String message) {
            super(message);
        }

        public PingFailure(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private final RepositoryConfiguration repoConfig;
    private final HttpTransport httpTransport;

    public Forge(RepositoryConfiguration repoConfig, HttpTransport httpTransport) {
        this.repoConfig = repoConfig;
        this.httpTransport = httpTransport;
    }

    public String getUrl() {
        return repoConfig.get(ForgePollerPluginConfig.FORGE_URL).getValue();
    }

    public void ping() throws PingFailure {
        ping(getUrl());
    }

    public void ping(PackageConfiguration packageConfig) throws PingFailure {
        ping(getUrl() + "/" + packageConfig.get(MODULE_NAME).getValue() + ".json");
    }

    public PackageRevision getLatestVersion(PackageConfiguration packageConfig) {
        return null;
    }

    private void ping(String url) throws PingFailure {
        try {
            get(url);
        } catch (HttpResponseException e) {
            //TODO: Improve this message
            throw new PingFailure("Failed to connect to Forge", e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HttpResponse get(String url) throws IOException {
        return httpTransport.createRequestFactory().buildGetRequest(new GenericUrl(url)).execute();
    }
}
