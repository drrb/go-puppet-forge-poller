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
package io.github.drrb.test;

import io.github.drrb.goforgepoller.util.Log;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class NoLogging extends TestWatcher {

    private static final NoLogging INSTANCE = new NoLogging();

    private NoLogging() {
    }

    public static NoLogging rule() {
        return INSTANCE;
    }

    @Override
    protected void starting(Description description) {
        Log.disable();
    }

    @Override
    protected void finished(Description description) {
        Log.enable();
    }
}
