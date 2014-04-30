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

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import io.github.drrb.goforgepoller.ForgePollerPluginConfig;
import io.github.drrb.goforgepoller.forge.api.ModuleMetadata;
import io.github.drrb.goforgepoller.forge.api.ModuleRelease;
import io.github.drrb.goforgepoller.forge.api.ModuleReleases;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

public class Forge {

    public static class Factory {
        public Forge build(RepositoryConfiguration repoConfig) {
            String baseUrl = repoConfig.get(ForgePollerPluginConfig.FORGE_URL).getValue();
            try {
                return new Forge(new URL(baseUrl), new NetHttpTransport());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class PingFailure extends Exception {

        public PingFailure(String message) {
            super(message);
        }

        public PingFailure(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class ModuleNotFound extends Exception {
        public ModuleNotFound(String message) {
            super(message);
        }

        public ModuleNotFound(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private final URL baseUrl;
    private final HttpTransport httpTransport;

    public Forge(URL baseUrl, HttpTransport httpTransport) {
        this.baseUrl = baseUrl;
        this.httpTransport = httpTransport;
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

    public void ping() throws PingFailure {
        try {
            ping(getBaseUrl());
        } catch (PingFailure e) {
            throw new PingFailure(String.format("Failed to connect to forge at %s", getBaseUrl()), e);
        }
    }

    public void ping(ModuleSpec module) throws PingFailure {
        try {
            ping(moduleUrl(module));
        } catch (PingFailure e) {
            throw new PingFailure(String.format("Failed to query for module %s", module.getName()), e);
        }
    }

    public ModuleVersion getLatestVersion(ModuleSpec module) throws ModuleNotFound {
        SortedSet<Version> versions = getAllVersions(module);

        try {
            SortedSet<Version> versionsInRange = versions.tailSet(module.getLowerVersionBound()).headSet(module.getUpperVersionBound());
            Version latestReleaseInRange = versionsInRange.last();

            return getModuleVersion(module, latestReleaseInRange);
        } catch (NoSuchElementException e) {
            throw new ModuleNotFound(String.format("No module versions found satisfying '%s'", module), e);
        }
    }

    private ModuleVersion getModuleVersion(ModuleSpec module, Version version) throws ModuleNotFound {
        try {
            ModuleReleases allReleases = get(releasesUrl(module)).parseAs(ModuleReleases.class);
            Map<Version, ModuleRelease> moduleReleases = allReleases.getReleases(module.getName());
            ModuleRelease moduleRelease = moduleReleases.get(version);
            return ModuleVersion.with(module, version, url(moduleRelease.getFile()));
        } catch (IOException e) {
            throw new ModuleNotFound(String.format("Failed to look up release information for module '%s'", module), e);
        }
    }

    private SortedSet<Version> getAllVersions(ModuleSpec module) throws ModuleNotFound {
        try {
            HttpResponse response = get(moduleUrl(module));
            ModuleMetadata moduleMetadata = response.parseAs(ModuleMetadata.class);
            return new TreeSet<>(moduleMetadata.getVersions());
        } catch (IOException e) {
            throw new ModuleNotFound(String.format("Failed to list versions of module '%s'", module), e);
        }
    }

    private void ping(URL url) throws PingFailure {
        try {
            get(url);
        } catch (IOException e) {
            throw new PingFailure(String.format("Failed to connect to '%s'", url), e);
        }
    }

    private URL moduleUrl(ModuleSpec moduleSpec) {
        return url("/%s.json", moduleSpec.getName());
    }

    private URL releasesUrl(ModuleSpec moduleSpec) {
        return url("/api/v1/releases.json?module=%s", moduleSpec.getName());
    }

    private URL url(String pathFormat, Object... args) {
        try {
            return new URL(getBaseUrl(), Paths.get(getBaseUrl().getPath(), String.format(pathFormat, args)).toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpResponse get(URL url) throws IOException {
        HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(url));
        request.setParser(getParser());
        return request.execute();
    }

    private JsonObjectParser getParser() {
        return new JsonObjectParser(new JacksonFactory());
    }

    @Override
    public String toString() {
        return baseUrl.toString();
    }
}
