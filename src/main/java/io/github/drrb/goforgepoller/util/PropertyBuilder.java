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
package io.github.drrb.goforgepoller.util;


import com.thoughtworks.go.plugin.api.config.Property;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialProperty;

public class PropertyBuilder {

    private Property property;

    public static PropertyBuilder property(String name) {
        return new PropertyBuilder(name);
    }

    private PropertyBuilder(String name) {
        this.property = new PackageMaterialProperty(name);
    }

    private PropertyBuilder(String name, String value) {
        this.property = new PackageMaterialProperty(name, value);
    }

    public static Property property(String name, String value) {
        return new PropertyBuilder(name, value).build();
    }

    public PropertyBuilder withDisplayName(String displayName) {
        property = property.with(Property.DISPLAY_NAME, displayName);
        return this;
    }

    public PropertyBuilder withDisplayOrder(int displayOrder) {
        property = property.with(Property.DISPLAY_ORDER, displayOrder);
        return this;
    }

    public PropertyBuilder withRequired(boolean required) {
        property = property.with(Property.REQUIRED, required);
        return this;
    }

    public PropertyBuilder withPartOfIdentity(boolean partOfIdentity) {
        property = property.with(Property.PART_OF_IDENTITY, partOfIdentity);
        return this;
    }

    public Property build() {
        return property;
    }
}
