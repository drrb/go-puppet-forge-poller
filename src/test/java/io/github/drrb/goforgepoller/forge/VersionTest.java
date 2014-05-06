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

import org.junit.Test;

import static io.github.drrb.test.Matchers.greaterThan;
import static io.github.drrb.test.Matchers.lessThan;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class VersionTest {

    @Test
    public void equalVersionsAreEqual() throws Exception {
        assertThat(Version.of("1.0.0"), is(equalTo(Version.of("1.0.0"))));
        assertThat(Version.of("1.0.0"), is(not(greaterThan(Version.of("1.0.0")))));
        assertThat(Version.of("1.0.0"), is(not(lessThan(Version.of("1.0.0")))));
    }

    @Test
    public void simpleNumbersAreOrderedNormally() throws Exception {
        assertThat(Version.of("2"), is(greaterThan(Version.of("1"))));
        assertThat(Version.of("1"), is(lessThan(Version.of("2"))));
    }

    @Test
    public void normalVersionNumbersAreOrderedByMajorVersion() throws Exception {
        assertThat(Version.of("2.0.0"), is(greaterThan(Version.of("1.2.0"))));
        assertThat(Version.of("1.2.0"), is(lessThan(Version.of("2.0.0"))));
    }

    @Test
    public void versionsAreSortedNumerically() throws Exception {
        assertThat(Version.of("1.10.0"), is(greaterThan(Version.of("1.2.0"))));
        assertThat(Version.of("1.2.0"), is(lessThan(Version.of("1.10.0"))));
    }

    @Test
    public void extraPartsComeLater() throws Exception {
        assertThat(Version.of("1.0.0.0"), is(greaterThan(Version.of("1.0.0"))));
        assertThat(Version.of("1.0.0"), is(lessThan(Version.of("1.0.0.0"))));
    }

    @Test
    public void suffixedComeBeforeUnsuffixed() throws Exception {
        assertThat(Version.of("1.0.0-beta"), is(lessThan(Version.of("1.0.0"))));
    }

    @Test
    public void orderIncludesSuffix() throws Exception {
        assertThat(Version.of("1.0.0-beta"), is(lessThan(Version.of("1.0.0-beta2"))));
    }

    @Test
    public void dodgyVersionNumbersSortedByBestEffort() throws Exception {
        assertThat(Version.of("1.xxxx"), is(lessThan(Version.of("1.0.0"))));
    }

    @Test
    public void throwsAnExceptionIfNumberIsCompleteGarbage() throws Exception {
        try {
            Version.of("$20");
            fail("Expected an exception");
        } catch (RuntimeException e) {
        }
    }
}
