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

import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialPoller;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class ForgeProviderTest {

    private ForgeProvider provider;

    @Before
    public void setUp() throws Exception {
        provider = new ForgeProvider();
    }

    @Test
    public void shouldReturnPluginConfig() {
        PackageMaterialConfiguration config = provider.getConfig();
        assertThat(config, is(not(nullValue())));
    }

    @Test
    public void shouldReturnPoller() throws Exception {
        PackageMaterialPoller poller = provider.getPoller();
        assertThat(poller, is(not(nullValue())));
    }
}
