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
package io.github.drrb.goforgepoller.forge;

import com.thoughtworks.go.plugin.api.material.packagerepository.PackageRevision;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.util.Date;

import static io.github.drrb.test.BetweenMatcher.Builder.between;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ModuleVersionTest {

    private ModuleVersion moduleVersion;

    @Before
    public void setUp() throws Exception {
        moduleVersion = new ModuleVersion("puppetlabs/apache", Version.of("1.0.0"), new URL("http://forge.example.com/puppetlabs-apache-1.0.0.tar.gz"));
    }

    @Test
    public void shouldSetVersionNumber() throws Exception {
        PackageRevision packageRevision = moduleVersion.toPackageRevision();

        assertThat(packageRevision.getRevision(), is("1.0.0"));
    }

    @Test
    public void shouldSetUserToAuthor() throws Exception {
        PackageRevision packageRevision = moduleVersion.toPackageRevision();

        assertThat(packageRevision.getUser(), is("puppetlabs"));
    }

    @Test
    public void shouldSetTrackBackUrl() throws Exception {
        PackageRevision packageRevision = moduleVersion.toPackageRevision();

        assertThat(packageRevision.getTrackbackUrl(), is("http://forge.example.com/puppetlabs-apache-1.0.0.tar.gz"));
    }

    @Test
    public void shouldSetLocation() throws Exception {
        PackageRevision packageRevision = moduleVersion.toPackageRevision();

        assertThat(packageRevision.getDataFor("LOCATION"), is("http://forge.example.com/puppetlabs-apache-1.0.0.tar.gz"));
    }

    @Test
    public void shouldSetVersion() throws Exception {
        PackageRevision packageRevision = moduleVersion.toPackageRevision();

        assertThat(packageRevision.getDataFor("VERSION"), is("1.0.0"));
    }

    @Test
    public void shouldSetDateToNow() throws Exception {
        Date testStart = new Date();

        PackageRevision packageRevision = moduleVersion.toPackageRevision();

        Date testEnd = new Date();
        assertThat(packageRevision.getTimestamp(), is(between(testStart).and(testEnd)));
    }
}
