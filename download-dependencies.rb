#!/usr/bin/env ruby
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

require 'fileutils'
require 'rexml/document'
require 'net/http'
require 'uri'

include FileUtils
include REXML

pom = Document.new(File.new("pom.xml"))
$go_version = pom.get_text("/project/properties/go.version")

def download_jar(filename)
    url = URI.parse("http://www.thoughtworks.com/products/docs/go/#{$go_version}/help/resources/#{filename}")
    print "Downloading #{url}..."
    Net::HTTP.start(url.host) do |http|
        open(filename, "w") do |file|
            http.request_get(url.path) do |resp|
                resp.read_body do |segment|
                    print "."
                    file.write(segment)
                end
            end
        end
        puts " Done"
    end
end

def maven_install
    command_bits = [
        "mvn",
        "org.apache.maven.plugins:maven-install-plugin:2.5.1:install-file",
        "-Dfile=go-plugin-api-current.jar",
        "-DgroupId=com.thoughtworks",
        "-DartifactId=go-plugin-api",
        "-Dversion=#{$go_version}",
        "-Djavadoc=go-plugin-api-javadoc-current.jar",
        "-Dpackaging=jar",
    ]
    system command_bits.join(" ")
end

begin
    download_jar("go-plugin-api-current.jar")
    download_jar("go-plugin-api-javadoc-current.jar")

    puts "Installing jars in local Maven repo"
    maven_install
ensure
    puts "Removing downloaded files"
    rm_f "go-plugin-api-current.jar"
    rm_f "go-plugin-api-javadoc-current.jar"
end

puts "Done"
