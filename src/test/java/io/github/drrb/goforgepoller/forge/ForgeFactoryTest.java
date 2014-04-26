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

import com.thoughtworks.go.plugin.api.config.Property;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import org.junit.Before;
import org.junit.Test;

import static io.github.drrb.goforgepoller.ForgePollerPluginConfig.FORGE_URL;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ForgeFactoryTest {

    private Forge.Factory forgeFactory;

    @Before
    public void setUp() throws Exception {
        forgeFactory = new Forge.Factory();
    }

    @Test
    public void shouldBuildForge() throws Exception {
        RepositoryConfiguration repoConfig = new RepositoryConfiguration();
        repoConfig.add(new Property(FORGE_URL).withDefault("http://forge.example.com"));
        Forge forge = forgeFactory.build(repoConfig);
        assertThat(forge.getBaseUrl().toString(), is("http://forge.example.com"));
    }
}
