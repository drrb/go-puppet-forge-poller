#!/bin/bash -exu
#
# Go Forge Poller
# Copyright (C) 2014 drrb
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with Go Forge Poller. If not, see <http://www.gnu.org/licenses/>.
#

VERSION=${1:-current}

rm -rf repo
mkdir -p repo

wget http://www.thoughtworks.com/products/docs/go/${VERSION}/help/resources/go-plugin-api-current.jar
wget http://www.thoughtworks.com/products/docs/go/${VERSION}/help/resources/go-plugin-api-javadoc-current.jar

go_version=`unzip -q -c go-plugin-api-current.jar META-INF/MANIFEST.MF | awk '/Go-Version/ {print $2}'`

mvn org.apache.maven.plugins:maven-install-plugin:2.3.1:install-file \
                         -Dfile=go-plugin-api-current.jar \
                         -DgroupId=com.thoughtworks \
                         -DartifactId=go-plugin-api \
                         -Dversion=${go_version} \
                         -Dpackaging=jar \
                         -DlocalRepositoryPath=repo

mvn org.apache.maven.plugins:maven-install-plugin:2.3.1:install-file \
                         -Dfile=go-plugin-api-javadoc-current.jar \
                         -DgroupId=com.thoughtworks \
                         -DartifactId=go-plugin-api \
                         -Dversion=${go_version} \
                         -Dclassifier=javadoc \
                         -Dpackaging=jar \
                         -DlocalRepositoryPath=repo

rm go-plugin-api-current.jar
rm go-plugin-api-javadoc-current.jar

