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
package io.github.drrb.goforgepoller.util;

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.xstream.XStream;

public class Log {
    private static final ThreadLocal<Boolean> globallyEnabled = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return true;
        }
    };

    public static void disable() {
        globallyEnabled.set(false);
    }

    public static void enable() {
        globallyEnabled.set(true);
    }

    private static Boolean isEnabled() {
        return globallyEnabled.get();
    }

    public static Log getLogFor(Class<?> type) {
        return new Log(Logger.getLoggerFor(type));
    }

    private final Logger logger;

    public enum Level {
        DEBUG, INFO
    }

    public Log(Logger logger) {
        this.logger = logger;
    }

    public void debug(String format, Object... args) {
        for (int i = 0; i < args.length; i++) {
            XStream xstream = new XStream();
            args[i] = xstream.toXML(args[i]);
        }
        if (isEnabled()) {
            logFormatted(Level.DEBUG, format, args);
        }
    }

    public void info(String format, Object... args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Throwable) {
                args[i] = Exceptions.render((Throwable) args[i]);
            }
        }
        if (isEnabled()) {
            logFormatted(Level.INFO, format, args);
        }
    }

    private void logFormatted(Level level, String format, Object... args) {
        String message = String.format(format, args);
        log(level, message);
    }

    protected void log(Level level, String message) {
        switch (level) {
            case DEBUG: logger.debug(message); break;
            case INFO: logger.info(message); break;
        }
    }
}
