#!/bin/bash

set -eo pipefail


PROJECT_NAME="srs-fleet-manager"

display_usage() {
    cat <<EOT


##########################################################################################################################

 This script gets triggered by the automated CI/CD jobs of AppSRE. It builds and tests 
 '${PROJECT_NAME}' whenever a pull request is raised.


 Usage: $0 [options]
 Example: $0 --version 2.0.0.Final
 
 options include:
 
 -v, --version     Version of apicurio-registry-tenant-manager-client. If not set defaults to '2.0.0.Final'
 -h, --help        This help message

##########################################################################################################################


EOT
}


build_project() {
    echo "#######################################################################################################"
    echo " Building Project '${PROJECT_NAME}'..."
    echo "#######################################################################################################"
    # AppSRE environments doesn't have maven and jdk11 which are required dependencies for building this project
    # Installing these dependencies is a tedious task and also since it's a shared instance, installing the required versions of these dependencies is not possible sometimes
    # Hence, using custom container that packs the required dependencies with the specific required versions
    # docker run --rm -t -u $(id -u):$(id -g) -v $(pwd):/home/user --workdir /home/user quay.io/riprasad/srs-project-builder:latest bash -c "./build-project.sh"

    docker pull quay.io/app-sre/mk-ci-tools:latest
    docker run -v $(pwd):/opt/srs -w /opt/srs -e HOME=/tmp -u $(id -u) \
        -e APICURIO_REGISTRY_REPO=https://gitlab.cee.redhat.com/service-registry/srs-service-registry.git \
        quay.io/app-sre/mk-ci-tools:latest make pr-check
}




main() { 

    # Parse command line arguments
    while [ $# -gt 0 ]
    do
        arg="$1"

        case $arg in
          -h|--help)
            shift
            display_usage
            exit 0
            ;;
          *)
            echo "Unknown argument: $1"
            display_usage
            exit 1
            ;;
        esac
        shift
    done


    # function calls
    build_project
    
}

main $*
