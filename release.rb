#!/usr/bin/env ruby
#
# Go Puppet Forge Poller
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
# along with Go Puppet Forge Poller. If not, see <http://www.gnu.org/licenses />.
#


require 'json'
require 'rexml/document'
require 'net/http'
include REXML

def run(command, *args)
    system "#{command}  #{args.join ' '}" or raise "mvn command failed"
end

def mvn(*commands)
    run "mvn --batch-mode", *commands
end

def git(*args)
    run "git", *args
end

def read_xpath(file, xpath)
    release_pom = Document.new(File.read(file))
    XPath.first(release_pom, xpath)
end

class String
    def green
        "\e[32m#{self}\e[0m"
    end

    def red
        "\e[31m#{self}\e[0m"
    end

    def yellow
        "\e[33m#{self}\e[0m"
    end
end

class Github
    def initialize
        @base_url = "https://api.github.com"
        api_key_file = File.expand_path("~/.github/release.apikey")
        raise "Put your github credentials in ~/.github/release.apikey as 'username:key'" unless File.exist? api_key_file
        @username, @key = File.read(api_key_file).strip.split ":"
    end

    def post(path, data, &block)
        puts "POST #{path}"
        request = Net::HTTP::Post.new(path)
        request.body = data.to_json
        send request
    end

    def patch(path, data, &block)
        puts "PATCH #{path}"
        request = Net::HTTP::Patch.new(path)
        request.body = data.to_json
        response = send(request, &block)
    end

    def get(path, &block)
        puts "GET #{path}"
        request = Net::HTTP::Get.new(path)
        send request
    end

    def upload(path, file)
        puts "POST #{file}"
        request = Net::HTTP::Post.new(path)
        request.body = File.read(file)
        send(request, "https://uploads.github.com", "application/octet-stream")
    end

    def send(request, base_url = @base_url, content_type = "application/json")
        request.add_field('Content-Type', content_type)
        request.basic_auth @username, @key

        uri = URI.parse(base_url)
        http = Net::HTTP.new(uri.host, uri.port)
        http.use_ssl = true
        response = http.request(request)
        puts response.code
        if block_given?
            yield(response)
        else
            unless response.code =~ /^2../
                raise "Request returned non-success:\n#{response.body}"
            end
            JSON.parse(response.body)
        end
    end
end

mvn "release:prepare -DdryRun"
release_version = read_xpath("pom.xml.tag", "/project/version").text
mvn "release:clean", "release:prepare", "release:perform"
git "push"

release_version = "1.0.1"
target_go_version = read_xpath("pom.xml", "/project/properties/go.version").text
github = Github.new
data = {
    "tag_name" => release_version,
    "target_commitish" => "master",
    "name" => "v#{release_version}",
    "body" => "**Target Go version:** #{target_go_version}",
    "draft" => false,
    "prerelease" => false
}
release = github.get("/repos/drrb/go-puppet-forge-poller/releases").find {|release| release["tag_name"] == release_version}
if release
    puts "Release #{release_version} exists. Updating it..."
    github.patch("/repos/drrb/go-puppet-forge-poller/releases/#{release['id']}", data)
else
    puts "Creating release #{release_version}..."
    github.post("/repos/drrb/go-puppet-forge-poller/releases", data)
    release = github.get("/repos/drrb/go-puppet-forge-poller/releases").find {|release| release["tag_name"] == release_version}
end

puts "Uploading artifact"
github.upload("/repos/drrb/go-puppet-forge-poller/releases/#{release['id']}/assets?name=go-puppet-forge-poller-#{release_version}.jar", "target/checkout/target/release-repo/io/github/drrb/go-puppet-forge-poller/#{release_version}/go-puppet-forge-poller-#{release_version}.jar")
