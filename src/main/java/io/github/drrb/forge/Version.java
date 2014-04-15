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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Version implements Comparable<Version> {

    public static final Version ZERO = new Version(String.valueOf(0));
    public static final Version INFINITY = new Version(String.valueOf(Integer.MAX_VALUE));
    private final String string;
    private final List<Object> components;

    private Version(String string) {
        if (!string.matches("\\A\\d+([.-][a-zA-Z0-9]+)*\\Z")) {
            throw new IllegalArgumentException("Expected a version number matching /\\A\\d+([.-][a-zA-Z0-9]+)*\\Z/, but got \"" + string + "\"");
        }
        this.string = string.replaceAll("-", ".pre.");
        this.components = parse(string);
    }

    public static Version of(String version) {
        return new Version(version);
    }

    private static List<Object> parse(String string) {
        String[] parts = string.split("\\.");
        List<Object> components = new ArrayList<>(parts.length);
        for (String part : parts) {
            if (part.matches("\\d+")) {
                components.add(Integer.valueOf(part));
            } else {
                components.add(part);
            }
        }
        return components;
    }

    @Override
    public int compareTo(Version that) {
        List<Object> versionAParts = this.components;
        List<Object> versionBParts = that.components;
        for (int i = 0; i < versionAParts.size() && i < versionBParts.size(); i++) {
            Object versionAPart = versionAParts.get(i);
            Object versionBPart = versionBParts.get(i);
            if (!versionAPart.equals(versionBPart)) {
                return compareParts(versionAPart, versionBPart);
            }
        }
        if (versionAParts.size() < versionBParts.size()) {
            return -1;
        } else if (versionAParts.size() > versionBParts.size()) {
            return 1;
        } else {
            return 0;
        }
    }

    private int compareParts(Object versionAPart, Object versionBPart) {
        if (versionAPart instanceof String && versionBPart instanceof Integer) {
            return -1;
        } else if (versionAPart instanceof Integer && versionBPart instanceof String) {
            return 1;
        } else if (versionAPart instanceof Integer) {
            return ((Integer) versionAPart).compareTo((Integer) versionBPart);
        } else {
            return ((String) versionAPart).compareTo((String) versionBPart);
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
