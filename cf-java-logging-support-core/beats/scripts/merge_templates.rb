#!/usr/bin/env ruby

require 'json'

if ARGV.size < 3
  puts "usage ruby merge_templates.rb <template-name> <template-file>+"
end

template_name = ARGV.shift
merged_template = {}

def deep_merge(dst, src)
  dst.merge!(src) {
    |key, oldval, newval|
    if oldval.kind_of?(Hash) && newval.kind_of?(Hash)
      deep_merge(oldval, newval)
    else
      newval
    end
  }
end
ARGV.each do |tfile|
    t = JSON::load(File.new(tfile))
    deep_merge(merged_template, t)
end

merged_template['template'] = template_name
merged_template['order'] = 99
jj merged_template
