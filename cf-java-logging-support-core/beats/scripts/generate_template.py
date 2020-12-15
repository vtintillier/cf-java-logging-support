#!/usr/bin/env python3

"""
This script generates the ES template file ({beat}.template.json) from
the etc/fields.yml file.

Example usage:

   python generate_template.py etc/fields.yml etc/topbeat.template.json
"""

import sys
import yaml
import json


def fields_to_es_template(input, output, index):
    """
    Reads the YAML file from input and generates the JSON for
    the ES template in output. input and output are both file
    pointers.
    """

    # Custom properties
    docs = yaml.load(input)

    # No fields defined, can't generate template
    if docs is None:
        print("fields.yml is empty. Cannot generate template.")
        return

    # Remove sections as only needed for docs
    if "sections" in docs.keys():
        del docs["sections"]

    # Each template needs defaults
    if "defaults" not in docs.keys():
        print("No defaults are defined. Each template needs at least defaults defined.")
        return

    defaults = docs["defaults"]

    # skeleton
    template = {
        "template": index,
        "settings": {
            "index.refresh_interval": "5s"
        },
        "mappings": {
            "_default_": {
                "_all": {
                    "omit_norms": True,
                    "enabled": True
                },
                "properties": {},
                "dynamic_templates": [{
                    "template1": {
                        "match": "*",
                        "mapping": {
                            "type": "{dynamic_type}",
                            "index": defaults["index"],
                            "doc_values": defaults["doc_values"],
                            "ignore_above": defaults["ignore_above"]
                        }
                    }
                }]
            }
        }
    }

    # make sure we do have a few "default" fields
    properties = {}

    for doc, section in docs.items():
        if doc not in ["version", "defaults", "summary"]:
            prop = fill_section_properties(section, defaults)
            properties.update(prop)

    template["mappings"]["_default_"]["properties"] = properties
    #
    # Add these two "defaults"
    # THIS IS SUPER IMPORTANT FOR US, OTHERWISE ES MAY CHOKE
    #
    template["mappings"]["_default_"]["properties"]["@message"] = {
        "type": "string",
        "index": "analyzed"
    }
    template["mappings"]["_default_"]["properties"]["timestamp"] = {
        "type": "long",
        "doc_values": True,
        "ignore_malformed": True
    }
    template["mappings"]["_default_"]["properties"]["@timestamp"] = {
        "type": "date",
        "doc_values": True,
        "ignore_malformed": True
    }

    json.dump(template, output,
              indent=2, separators=(',', ': '),
              sort_keys=True)


def fill_section_properties(section, defaults):
    """
    Traverse the sections tree and fill in the properties
    map.
    """
    properties = {}

    if "fields" in section:
        for field in section["fields"]:
            prop = fill_field_properties(field, defaults)
            properties.update(prop)

    return properties


def fill_field_properties(field, defaults):
    """
    Add data about a particular field in the properties
    map.
    """
    properties = {}

    for key in defaults.keys():
        if key not in field:
            field[key] = defaults[key]

    # TODO: Make this more dyanmic
    if field.get("index") == "analyzed":
        properties[field["name"]] = {
            "type": field["type"],
            "index": "analyzed",
            "norms": {
                "enabled": False
            }
        }

    elif field.get("type") == "geo_point":
        properties[field["name"]] = {
            "type": "geo_point"
        }

    elif field.get("type") == "date":
        properties[field["name"]] = {
            "type": "date",
            "doc_values": True,
            "ignore_malformed": True
        }
    elif field.get("type") == "long":
        properties[field["name"]] = {
            "type": "long",
            "doc_values": True,
            "ignore_malformed": True
        }
    elif field.get("type") == "integer":
        properties[field["name"]] = {
            "type": "integer",
            "doc_values": True,
            "ignore_malformed": True
        }
    elif field.get("type") == "double":
        properties[field["name"]] = {
            "type": "double",
            "doc_values": True,
            "ignore_malformed": True
        }
    elif field.get("type") == "float":
        properties[field["name"]] = {
            "type": "float",
            "doc_values": True,
            "ignore_malformed": True
        }
    elif field.get("type") == "string":
        properties[field["name"]] = {
            "type": "string",
            "doc_values": True,
            "index": "not_analyzed"
        }
    elif field.get("type") == "group":
        prop = fill_section_properties(field, defaults)

        # Only add properties if they have a content
        if len(prop) is not 0:
            properties[field.get("name")] = {"properties": {}}
            properties[field.get("name")]["properties"] = prop




    elif field.get("ignore_above") == 0:
        properties[field["name"]] = {
            "type": field["type"],
            "index": field["index"],
            "doc_values": field["doc_values"]
        }

    return properties


if __name__ == "__main__":

    if len(sys.argv) != 3:
        print("Usage: %s beatpath beatname" % sys.argv[0])
        sys.exit(1)

    beat_path = sys.argv[1]
    beat_name = sys.argv[2]

    input = open(beat_path + "/etc/fields.yml", 'r')
    output = open(beat_path + "/etc/" + beat_name + ".template.json", 'w')

    try:
        fields_to_es_template(input, output, beat_name + "-*")
    finally:
        input.close()
        output.close()
