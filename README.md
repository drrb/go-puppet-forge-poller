# Go Puppet Forge Poller

A [Go](http://www.go.cd) plugin that polls a Puppet forge

[![Build Status](https://travis-ci.org/drrb/go-puppet-forge-poller.svg?branch=master)](https://travis-ci.org/drrb/go-puppet-forge-poller)
[![Coverage Status](https://img.shields.io/coveralls/drrb/go-forge-poller.svg)](https://coveralls.io/r/drrb/go-puppet-forge-poller?branch=master)

## Usage

### Add a Forge

First, add a Puppet forge as a *package repository* to check for modules:

![adding a forge](https://raw.githubusercontent.com/drrb/go-puppet-forge-poller/master/doc/1_add_repo.png)

### Add a Module

Then, when you're creating a pipline, add a module from the forge as a *package*:

![adding a module](https://raw.githubusercontent.com/drrb/go-puppet-forge-poller/master/doc/2_add_package.png)

### Download the Module

During your pipeline, you can download the module using the environment variables provided:

```
[go] setting environment variable 'GO_PACKAGE_PUPPET_FORGE_PUPPETLABS_STDLIB_LABEL' to value '4.1.0'
[go] setting environment variable 'GO_REPO_PUPPET_FORGE_PUPPETLABS_STDLIB_FORGE_URL' to value 'http://forge.puppetlabs.com'
[go] setting environment variable 'GO_PACKAGE_PUPPET_FORGE_PUPPETLABS_STDLIB_MODULE_NAME' to value 'puppetlabs/stdlib'
[go] setting environment variable 'GO_PACKAGE_PUPPET_FORGE_PUPPETLABS_STDLIB_VERSION' to value '4.1.0'
[go] setting environment variable 'GO_PACKAGE_PUPPET_FORGE_PUPPETLABS_STDLIB_LOCATION' to value 'http://forge.puppetlabs.com/system/releases/p/puppetlabs/puppetlabs-stdlib-4.1.0.tar.gz'
```

## Installing

Choose a version of the plugin according to your Go version:

<table>
    <tr>
        <th>Go Version</th>
        <th>Go Puppet Forge Poller Plugin Version</th>
    </tr>
    <tr>
        <td>13.4.x</td>
        <td>1.0.x</td>
    </tr>
    <tr>
        <td>14.1.x</td>
        <td>2.0.x</td>
    </tr>
    <tr>
        <td>14.2.x</td>
        <td>3.0.x</td>
    </tr>
</table>

Then install the plugin into the Go server's `plugins/external` directory and restart the Go server:

```
sudo -u go wget --directory-prefix=/var/lib/go-server/plugins/external https://github.com/drrb/go-puppet-forge-poller/releases/download/1.0.1/go-puppet-forge-poller-1.0.1.jar
sudo service go-server restart
```

## Building From Source

Assuming you have Maven installed, you can build the plugin from source as follows.

```
# Download the Go APIs (not in Maven Central yet)
$ ./download-dependencies.sh

# Build the plugin. The plugin will end up in target/go-puppet-forge-poller-<version>.jar
$ mvn package
```

## Contributing

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Make your changes, and add tests for them
4. Test your changes (`mvn test`)
5. Commit your changes (`git commit -am 'Add some feature'`)
6. Push to the branch (`git push origin my-new-feature`)
7. Create new Pull Request

## License

Go Puppet Forge Poller
Copyright (C) 2014 drrb

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see [http://www.gnu.org/licenses/](http://www.gnu.org/licenses/).
