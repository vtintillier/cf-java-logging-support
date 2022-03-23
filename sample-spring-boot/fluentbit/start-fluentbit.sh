#!/bin/bash

# read and export nested environment variables to make them accessible to fluentbit

export CLS_HOST=$(echo $VCAP_SERVICES | jq -r '."cloud-logging"? | .[0]? | ."credentials"? | ."Fluentd-endpoint"')
export CLS_USER=$(echo $VCAP_SERVICES | jq -r '."cloud-logging"? | .[0]? | ."credentials"? | ."Fluentd-username"')
export CLS_PASSWD=$(echo $VCAP_SERVICES | jq -r '."cloud-logging"? | .[0]? | ."credentials"? | ."Fluentd-password"')

# check if Fluentd endpoint is provided
if [[ "${CLS_HOST}" == "null" ]]; then 
    echo "No Cloud Logging service instance found, aborting process. Maybe check the instance name."
else 
    echo "Cloud Logging serivce instance found. Downloading and configuring telegraf..."
    
    if [ ! -f fluent-bit-1.8.14/build/bin/fluent-bit ]
    then
        # download, extract and build Fluent Bit
        wget 'https://github.com/fluent/fluent-bit/archive/refs/tags/v1.8.14.tar.gz'
        tar xzf v1.8.14.tar.gz
        pushd fluent-bit-1.8.14/build
        cmake ..
        make
        popd
    fi

    # start Fluent Bit using the configuration file
    fluent-bit-1.8.14/build/bin/fluent-bit --quiet --config fluentbit/fluentbit.conf
fi
