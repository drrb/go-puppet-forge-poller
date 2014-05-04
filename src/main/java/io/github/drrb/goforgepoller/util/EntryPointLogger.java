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

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class EntryPointLogger implements MethodInterceptor {
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Log log = getLog(invocation.getMethod().getDeclaringClass());

        log.debug(methodSignatureFormat(invocation), invocation.getArguments());
        try {
            Object returnValue = invocation.proceed();
            log.debug(invocation.getMethod().getName() + " -> %s", returnValue);
            return returnValue;
        } catch (Throwable throwable) {
            log.error(invocation.getMethod() + " throwing error", throwable);
            throw throwable;
        }
    }

    private String methodSignatureFormat(MethodInvocation invocation) {
        String methodName = invocation.getMethod().getName();
        StringBuilder messageBuilder = new StringBuilder(methodName);
        messageBuilder.append("(");
        for (int i = 0; i < invocation.getArguments().length; i++) {
            if (i > 0) {
                messageBuilder.append(", ");
            }
            messageBuilder.append("%s");
        }
        messageBuilder.append(")");
        return messageBuilder.toString();
    }

    protected Log getLog(Class<?> declaringClass) {
        return Log.getLogFor(declaringClass);
    }
}