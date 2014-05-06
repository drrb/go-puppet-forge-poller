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
package io.github.drrb.goforgepoller.util;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.*;

public class EntryPointLoggerTest {


    public static class LoggableObject {

        @EntryPoint
        public String doSomething(String firstArgument, String secondArgument) {
            return "return value";
        }

        @EntryPoint
        public void failToDoSomething() {
            throw new RuntimeException("Something bad happened");
        }

        public void doSomethingSilently() {
        }

    }

    private LoggableObject service;
    private Log log;

    @Before
    public void setUp() throws Exception {
        log = mock(Log.class);
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bindInterceptor(Matchers.any(), Matchers.annotatedWith(EntryPoint.class), new EntryPointLogger() {
                    @Override
                    protected Log getLog(Class<?> declaringClass) {
                        return log;
                    }
                });
            }
        });
        service = injector.getInstance(LoggableObject.class);
    }

    @Test
    public void writesMethodInvocationsToDebugLog() throws Exception {
        service.doSomething("a", "b");

        verify(log).debug(startsWith("doSomething"), eq("a"), eq("b"));
        verify(log).debug(startsWith("doSomething ->"), eq("return value"));
    }

    @Test
    public void writesThrownExceptionsToErrorLog() throws Exception {
        try {
            service.failToDoSomething();
            fail("Expected an error");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Something bad happened"));
            verify(log).error(contains("failToDoSomething() throwing error"), eq(e));
        }
    }

    @Test
    public void doesntWriteToLogForNonEntrypointMethods() throws Exception {
        service.doSomethingSilently();
        verify(log, never()).debug(anyString(), anyVararg());
    }
}
