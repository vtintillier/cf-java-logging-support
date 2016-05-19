#!/usr/bin/env ruby 

require 'json'

if ARGV.size < 3
  puts "usage ruby merge_templates.rb <template-name> <template-file>+"
end

template_name = ARGV.shift
merged_template = {}

ARGV.each do |tfile|
    t = JSON::load(File.new(tfile))
    merged_template.merge!(t)
end

merged_template['template'] = template_name
merged_template['order'] = 99
jj merged_template
