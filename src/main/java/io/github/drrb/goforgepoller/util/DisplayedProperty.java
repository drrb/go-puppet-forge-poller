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

import com.thoughtworks.go.plugin.api.config.Property;

public class DisplayedProperty {

    private Property property;

    public static DisplayedProperty property(String name) {
        return new DisplayedProperty(name);
    }

    public DisplayedProperty(String name) {
        this.property = new Property(name);
    }

    public DisplayedProperty withDisplayName(String displayName) {
        property = property.with(Property.DISPLAY_NAME, displayName);
        return this;
    }

    public DisplayedProperty withDisplayOrder(int displayOrder) {
        property = property.with(Property.DISPLAY_ORDER, displayOrder);
        return this;
    }

    public DisplayedProperty withRequired(boolean required) {
        property = property.with(Property.REQUIRED, required);
        return this;
    }

    public DisplayedProperty withPartOfIdentity(boolean partOfIdentity) {
        property = property.with(Property.PART_OF_IDENTITY, partOfIdentity);
        return this;
    }

    public Property build() {
        return property;
    }
}
