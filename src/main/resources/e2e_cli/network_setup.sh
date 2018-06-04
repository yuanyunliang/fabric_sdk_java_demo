#!/bin/bash
#
# Copyright IBM Corp. All Rights Reserved.
#
# SPDX-License-Identifier: Apache-2.0
#

UP_DOWN="$1"
CH_NAME="$2"
CC_NAME="$3"
CLI_TIMEOUT="$4"

: ${CLI_TIMEOUT:="10000"}

COMPOSE_FILE=docker-compose-e2e.yaml

function printHelp () {
	echo "Usage: ./network_setup <up|down> <\$channel-name> <\$chaincode-name> <\$cli_timeout>.\nThe arguments must be in order."
}

function validateArgs () {
	if [ -z "${UP_DOWN}" ]; then
		echo "Option up / down / restart not mentioned"
		printHelp
		exit 1
	fi
    if [ -z "${CH_NAME}" ]; then
        CH_NAME=mychannel
        echo "setting to default channel 'mychannel'"
    fi
    if [ -z "${CC_NAME}" ]; then
        CC_NAME=account
        echo "setting to default chaincode 'consumer'"
    fi
}

function clearContainers () {
    CONTAINER_IDS=$(docker ps -aq)
    if [ -z "$CONTAINER_IDS" -o "$CONTAINER_IDS" = " " ]; then
        echo "---- No containers available for deletion ----"
    else
        docker rm -f $CONTAINER_IDS
    fi
}

function removeUnwantedImages() {
    DOCKER_IMAGE_IDS=$(docker images | grep "dev\|none\|test-vp\|peer[0-9]-" | awk '{print $3}')
    if [ -z "$DOCKER_IMAGE_IDS" -o "$DOCKER_IMAGE_IDS" = " " ]; then
        echo "---- No images available for deletion ----"
    else
        docker rmi -f $DOCKER_IMAGE_IDS
    fi
}

function networkUp () {
    if [ -d "./crypto-config" ]; then
      echo "crypto-config directory already exists."
    else
      source generateArtifacts.sh $CH_NAME
    fi

    folder="crypto-config/peerOrganizations/org1.example.com/ca"
    privName1=""
    for file_a in ${folder}/*
        do
            temp_file1=`basename $file_a`
            if [ ${temp_file1##*.} != "pem" ];then
               privName1=$temp_file1
            fi
        done
    echo $privName1

    folder="crypto-config/peerOrganizations/org2.example.com/ca"
    privName2=""
    for file_b in ${folder}/*
        do
            temp_file2=`basename $file_b`
            if [ ${temp_file2##*.} != "pem" ];then
               privName2=$temp_file2
            fi
        done
    echo $privName2

    CHANNEL_NAME=$CH_NAME CHAINCODE_NAME=$CC_NAME TIMEOUT=$CLI_TIMEOUT docker-compose -f $COMPOSE_FILE up -d 2>&1

    if [ $? -ne 0 ]; then
	    echo "ERROR !!!! Unable to pull the images "
	    exit 1
    fi
    docker logs -f cli
}

function networkDown () {
    docker-compose -f $COMPOSE_FILE down

    clearContainers

    removeUnwantedImages

    rm -rf channel-artifacts/*.block channel-artifacts/*.tx crypto-config
}

validateArgs

if [ "${UP_DOWN}" == "up" ]; then
	networkUp
elif [ "${UP_DOWN}" == "down" ]; then
	networkDown
elif [ "${UP_DOWN}" == "restart" ]; then
	networkDown
	networkUp
else
	printHelp
	exit 1
fi
