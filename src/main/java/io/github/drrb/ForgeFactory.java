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
package io.github.drrb;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import io.github.drrb.forge.Forge;

public class ForgeFactory {
    public Forge build(RepositoryConfiguration repoConfig) {
        String forgeUrl = repoConfig.get(ForgePollerPluginConfig.FORGE_URL).getValue();
        return new Forge(forgeUrl, new NetHttpTransport());
    }
}
