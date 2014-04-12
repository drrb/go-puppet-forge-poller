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

import com.google.api.client.http.*;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageRevision;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import io.github.drrb.ForgePollerPluginConfig;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

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
        ping(moduleUrl(packageConfig));
    }

    public PackageRevision getLatestVersion(PackageConfiguration packageConfig) {
        try {
            HttpResponse response = get(moduleUrl(packageConfig));
            ModuleMetadata moduleMetadata = response.parseAs(ModuleMetadata.class);
            LinkedList<ModuleRelease> releases = moduleMetadata.getReleases();
            Collections.sort(releases, ModuleRelease.versionComparator());
            ModuleRelease latestRelease = releases.getLast();
            return new PackageRevision(latestRelease.getVersion(), null, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    private String moduleUrl(PackageConfiguration packageConfig) {
        return getUrl() + "/" + packageConfig.get(MODULE_NAME).getValue() + ".json";
    }

    private HttpResponse get(String url) throws IOException {
        HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(url));
        request.setParser(new JsonObjectParser(new JacksonFactory()));
        return request.execute();
    }
}
