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
package io.github.drrb.util;

import java.util.Comparator;

public class VersionComparator implements Comparator<String> {

    @Override
    public int compare(String versionA, String versionB) {
        String[] versionAParts = versionA.split("\\.");
        String[] versionBParts = versionB.split("\\.");
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
}