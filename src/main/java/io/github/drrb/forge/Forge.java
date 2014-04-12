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
import java.util.Collections;
import java.util.LinkedList;

public class Forge {
    public static class PingFailure extends Exception {

        public PingFailure(String message) {
            super(message);
        }

        public PingFailure(String message, Throwable cause) {
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
        ping(getUrl());
    }

    //TODO: accept package name instead of Go API class
    public void ping(String moduleName) throws PingFailure {
        ping(moduleUrl(moduleName));
    }

    public ModuleRelease getLatestVersion(String moduleName) {
        try {
            HttpResponse response = get(moduleUrl(moduleName));
            ModuleMetadata moduleMetadata = response.parseAs(ModuleMetadata.class);
            LinkedList<ModuleRelease> releases = moduleMetadata.getReleases();
            Collections.sort(releases, ModuleRelease.versionComparator());
            return releases.getLast();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void ping(URI url) throws PingFailure {
        try {
            get(url);
        } catch (HttpResponseException e) {
            //TODO: Improve this message
            throw new PingFailure("Failed to connect to Forge", e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private URI moduleUrl(String moduleName) {
        //TODO: this will fail
        String url = getUrl() + "/" + moduleName + ".json";
        return URI.create(url);
    }

    private HttpResponse get(URI url) throws IOException {
        HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(url));
        request.setParser(new JsonObjectParser(new JacksonFactory()));
        return request.execute();
    }
}
