#!/bin/bash

set -eo pipefail


PROJECT_NAME="srs-fleet-manager"
TENANT_MANAGER_CLIENT_VERSION="2.0.0.Final"


MVN_BUILD_COMMAND="mvn -B clean install ${BUILD_FLAGS}"


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
    local BUILD_FLAGS="$1"
    echo "#######################################################################################################"
    echo " Building Project '${PROJECT_NAME}'..."
    echo " Build Command: ${MVN_BUILD_COMMAND}"
    echo "#######################################################################################################"
    ${MVN_BUILD_COMMAND}
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
          -v|--version)
            shift
            TENANT_MANAGER_CLIENT_VERSION="$1"
            ;;
          *)
            echo "Unknown argument: $1"
            display_usage
            exit 1
            ;;
        esac
        shift
    done


    # Setting additional build flags
    BUILD_FLAGS="-Dapicurio-registry-tenant-manager-client.version=${TENANT_MANAGER_CLIENT_VERSION}"

    # function calls
    build_project ${BUILD_FLAGS}
    
}

main $*
