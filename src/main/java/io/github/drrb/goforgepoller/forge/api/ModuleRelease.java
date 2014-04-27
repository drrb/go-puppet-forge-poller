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

public class ModuleRelease extends GenericJson {
    @Key
    private String file;
    @Key
    private String version;

    public String getFile() {
        return file;
    }

    public Version getVersion() {
        return Version.of(version);
    }
}
