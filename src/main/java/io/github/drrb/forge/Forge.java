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

import java.io.IOException;
import java.net.URI;
import java.util.*;

public class Forge {
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

    private final String forgeUrl;
    private final HttpTransport httpTransport;

    public Forge(String forgeUrl, HttpTransport httpTransport) {
        this.forgeUrl = forgeUrl;
        this.httpTransport = httpTransport;
    }

    public URI getUrl() {
        return URI.create(forgeUrl);
    }

    public void ping() throws PingFailure {
        try {
            ping(getUrl());
        } catch (PingFailure e) {
            throw new PingFailure(String.format("Failed to connect to forge at %s", getUrl()), e);
        }
    }

    public void ping(ModuleSpec module) throws PingFailure {
        try {
            ping(moduleUrl(module));
        } catch (PingFailure e) {
            throw new PingFailure(String.format("Failed to query for module %s", module.getName()), e);
        }
    }

    public ModuleRelease getLatestVersion(ModuleSpec module) throws ModuleNotFound {
        List<ModuleRelease> releases = getAllVersions(module);

        SortedSet<ModuleRelease> orderedReleases = new TreeSet<>(releases);
        ModuleRelease upperVersionBound = ModuleRelease.with(module.getUpperVersionBound());
        ModuleRelease lowerVersionBound = ModuleRelease.with(module.getLowerVersionBound());

        try {
            return orderedReleases.tailSet(lowerVersionBound).headSet(upperVersionBound).last();
        } catch (NoSuchElementException e) {
            throw new ModuleNotFound(String.format("No module versions found satisfying '%s'", module), e);
        }
    }

    private List<ModuleRelease> getAllVersions(ModuleSpec module) throws ModuleNotFound {
        try {
            HttpResponse response = get(moduleUrl(module));
            ModuleMetadata moduleMetadata = response.parseAs(ModuleMetadata.class);
            return moduleMetadata.getReleases();
        } catch (IOException e) {
            throw new ModuleNotFound(String.format("Failed to list versions of module '%s'", module), e);
        }
    }

    private void ping(URI url) throws PingFailure {
        try {
            get(url);
        } catch (IOException e) {
            throw new PingFailure(String.format("Failed to connect to '%s'", url), e);
        }
    }

    private URI moduleUrl(ModuleSpec moduleSpec) {
        //TODO: this will fail
        String url = getUrl() + "/" + moduleSpec.getName() + ".json";
        return URI.create(url);
    }

    private HttpResponse get(URI url) throws IOException {
        HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(url));
        request.setParser(new JsonObjectParser(new JacksonFactory()));
        return request.execute();
    }
}
