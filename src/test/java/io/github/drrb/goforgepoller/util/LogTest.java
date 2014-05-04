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
 * along with Go Puppet Forge Poller. If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.drrb.goforgepoller.util;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class LogTest {

    private Log log;

    @Before
    public void setUp() throws Exception {
        log = spy(new Log(null) {
            @Override
            protected void log(Level level, String message) {}
        });
    }

    @Test
    public void shouldSerializeDebugLogArgs() throws Exception {
        log.debug("DEBUG: %s", new LinkedList<>(asList("a", "b", "c")));

        verify(log).log(Log.Level.DEBUG, "DEBUG: <linked-list>\n  <string>a</string>\n  <string>b</string>\n  <string>c</string>\n</linked-list>");
    }

    @Test
    public void shouldFormatInfoLog() throws Exception {
        log.info("INFO: %s", new LinkedList<>(asList("a", "b", "c")));

        verify(log).log(Log.Level.INFO, "INFO: [a, b, c]");
    }

    @Test
    public void shouldRenderExceptionsInInfoLog() throws Exception {
        Exception exception = new Exception("Failure", new Exception("Cause"));
        log.info("INFO: %s", exception);

        verify(log).log(Log.Level.INFO, "INFO: Failure (Cause)");
    }

    @Test
    public void shouldPrintStackTraceForErrorMessages() throws Exception {
        Exception exception = new Exception("something happened");
        log.error("Error", exception);

        verify(log).log(eq(Log.Level.ERROR), startsWith("Error\njava.lang.Exception: something happened"));
    }
}
