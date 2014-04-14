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
package io.github.drrb.forge;

import java.util.Objects;

public class Version implements Comparable<Version> {

    public static final Version ZERO = new Version(String.valueOf(0));
    public static final Version INFINITY = new Version(String.valueOf(Integer.MAX_VALUE));
    private final String string;

    private Version(String string) {
        this.string = string;
    }

    public static Version of(String version) {
        return new Version(version);
    }

    @Override
    public int compareTo(Version that) {
        String[] versionAParts = this.string.split("\\.");
        String[] versionBParts = that.string.split("\\.");
        for (int i = 0; i < versionAParts.length && i < versionBParts.length; i++) {
            Integer versionAPart = Integer.parseInt(versionAParts[i]);
            Integer versionBPart = Integer.parseInt(versionBParts[i]);
            if (!versionAPart.equals(versionBPart)) {
                return versionAPart.compareTo(versionBPart);
            }
        }
        if (versionAParts.length < versionBParts.length) {
            return -1;
        } else if (versionAParts.length > versionBParts.length) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return string;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (other.getClass() != getClass()) return false;

        Version that = (Version) other;
        return Objects.equals(this.string, that.string);
    }

    @Override
    public int hashCode() {
        return Objects.hash(string);
    }
}
