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
package io.github.drrb.test;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class Matchers {
    private Matchers() {
    }

    public static <T extends Comparable<T>> Matcher<T> greaterThan(final T other) {
        return new TypeSafeMatcher<T>() {
            @Override
            protected boolean matchesSafely(T item) {
                return item.compareTo(other) > 0;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("greater than ").appendValue(other);
            }
        };
    }

    public static <T extends Comparable<T>> Matcher<T> lessThan(final T other) {
        return new TypeSafeMatcher<T>() {
            @Override
            protected boolean matchesSafely(T item) {
                return item.compareTo(other) < 0;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("less than ").appendValue(other);
            }
        };
    }
}
