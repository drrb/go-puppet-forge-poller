# Go Puppet Forge Poller

A [Go](http://www.go.cd) plugin that polls a Puppet forge

[![Build Status](https://travis-ci.org/drrb/go-puppet-forge-poller.svg?branch=master)](https://travis-ci.org/drrb/go-puppet-forge-poller)
[![Coverage Status](https://coveralls.io/repos/drrb/go-puppet-forge-poller/badge.png?branch=master)](https://coveralls.io/r/drrb/go-puppet-forge-poller?branch=master)

## Installing

```
sudo -u go wget --directory-prefix=/var/lib/go-server/plugins/external https://github.com/drrb/go-puppet-forge-poller/releases/download/1.0.0/go-puppet-forge-poller-1.0.0.jar
sudo service go-server restart
```

## Building From Source

```
$ ./download-dependencies.sh
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
