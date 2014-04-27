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
package io.github.drrb.goforgepoller.forge.api;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import io.github.drrb.goforgepoller.forge.Version;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleReleases extends GenericJson {
    public Map<Version, ModuleRelease> getReleases(String module) throws IOException {
        Object moduleReleaseJsonObject = get(module);
        GenericJson releaseListJson = new GenericJson();
        releaseListJson.put("releases", moduleReleaseJsonObject);
        String releaseListJsonString = getFactory().toString(releaseListJson);
        ReleaseList releaseList = getFactory().fromString(releaseListJsonString, ReleaseList.class);
        HashMap<Version, ModuleRelease> releases = new HashMap<>();
        for (ModuleRelease release : releaseList.releases) {
            releases.put(release.getVersion(), release);
        }
        return releases;
    }

    public static class ReleaseList extends GenericJson {
        @Key
        private List<ModuleRelease> releases;

    }
}
