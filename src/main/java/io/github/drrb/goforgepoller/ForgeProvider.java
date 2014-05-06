/**
 * Go Puppet Forge Poller
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
 * along with Go Puppet Forge Poller. If not, see <http://www.gnu.org/licenses />.
 */
package io.github.drrb.goforgepoller;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialPoller;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialProvider;

@Extension
public class ForgeProvider implements PackageMaterialProvider {

    private final Injector injector;

    public ForgeProvider() {
        injector = Guice.createInjector(new ForgePollerModule());
    }

    public ForgePollerPluginConfig getConfig() {
        return injector.getInstance(ForgePollerPluginConfig.class);
    }

    public PackageMaterialPoller getPoller() {
        return injector.getInstance(ForgePoller.class);
    }
}
