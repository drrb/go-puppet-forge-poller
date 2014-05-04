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
package io.github.drrb.test;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class BetweenMatcher<T extends Comparable<T>> extends TypeSafeMatcher<T> {
    public static class Builder<T extends Comparable<T>> {

        private final T lower;
        private final Boolean lowerInclusive;

        public Builder(T lower, Boolean lowerInclusive) {
            this.lower = lower;
            this.lowerInclusive = lowerInclusive;
        }

        public static <T extends Comparable<T>> Builder<T> betweenInclusive(T lower) {
            return new Builder<>(lower, true);
        }

        public static <T extends Comparable<T>> Builder<T> betweenExclusive(T lower) {
            return new Builder<>(lower, false);
        }

        public static <T extends Comparable<T>> Builder<T> between(T lower) {
            return betweenInclusive(lower);
        }

        public BetweenMatcher<T> and(T upper) {
            return andInclusive(upper);
        }

        public BetweenMatcher<T> andInclusive(final T upper) {
            return and(upper, true);
        }

        public BetweenMatcher<T> andExclusive(final T upper) {
            return and(upper, false);
        }

        public BetweenMatcher<T> and(T upper, Boolean upperInclusive) {
            return new BetweenMatcher<>(lower, lowerInclusive, upper, upperInclusive);
        }
    }

    private final T lower;
    private final Boolean lowerInclusive;
    private final T upper;
    private final Boolean upperInclusive;

    private BetweenMatcher(T lower, Boolean lowerInclusive, T upper, Boolean upperInclusive) {
        this.lower = lower;
        this.lowerInclusive = lowerInclusive;
        this.upper = upper;
        this.upperInclusive = upperInclusive;
    }

    @Override
    protected boolean matchesSafely(T item) {
        return highEnough(item) && lowEnough(item);
    }

    private boolean lowEnough(T item) {
        if (upperInclusive) {
            return item.compareTo(upper) <= 0;
        } else {
            return item.compareTo(upper) < 0;
        }
    }

    private boolean highEnough(T item) {
        if (lowerInclusive) {
            return item.compareTo(lower) >= 0;
        } else {
            return item.compareTo(lower) > 0;
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("greater than ").appendValue(lower).appendText(" and less than ").appendValue(upper);
    }
}
