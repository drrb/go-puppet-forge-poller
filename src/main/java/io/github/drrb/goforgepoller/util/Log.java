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
    public static Log getLogFor(Class<?> type) {
        return new Log(type);
    }

    private final Logger logger;

    public Log(Class<?> type) {
        this.logger = Logger.getLoggerFor(type);
    }

    public void debug(String message, Object... args) {
        for (int i = 0; i < args.length; i++) {
            XStream xstream = new XStream();
            args[i] = xstream.toXML(args[i]);
        }
        logger.debug(String.format(message, args));
    }

    public void info(String message, Object... args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Throwable) {
                args[i] = Exceptions.render((Throwable) args[i]);
            }
        }
        logger.info(String.format(message, args));
    }
}
