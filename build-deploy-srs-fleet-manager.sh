#!/bin/bash

set -eo pipefail


PROJECT_NAME="srs-fleet-manager"
TENANT_MANAGER_CLIENT_VERSION="2.0.0.Final"
IMAGE_REGISTRY="quay.io"
IMAGE_ORG="rhoas"
IMAGE_NAME="${PROJECT_NAME}"
IMAGE_TAG="latest"
PATH_APPLICATION_PROPERTIES="./core/src/main/resources/application.properties"


MVN_BUILD_COMMAND="mvn -B clean install ${BUILD_FLAGS}"
DOCKER_BUILD_COMMAND="mvn -B install -DskipTests -Dquarkus.container-image.build=true ${BUILD_FLAGS}"



display_usage() {
    cat <<EOT


##########################################################################################################################

 This script gets triggered by the automated CI/CD jobs of AppSRE. It builds and pushes '${PROJECT_NAME}' image to the
 'rhoas' organization in 'quay.io' registry(defaults). Quay-organization, Image name and tags are configurable.

 In order to work, it needs the following variables defined in the CI/CD configuration of the project:

 RHOAS_QUAY_USER - The name of the robot account
                   used to push images to 'quay.io'

 RHOAS_QUAY_TOKEN - The token of the robot account
                    used to push images to 'quay.io'

 The machines that run this script need to have access to internet, so that the built images can be pushed.


 Usage: $0 [options]
 Example: $0 --version 2.0.0.Final --org rhoas --name srs-fleet-manager --tag 2.0.0.Final
 
 options include:
 
 -v, --version     Version of apicurio-registry-tenant-manager-client. If not set defaults to '2.0.0.Final'
 -o, --org         The organization the container image will be part of. If not set defaults to 'rhoas'
 -n, --name        The name of the container image. If not set defaults to 'srs-fleet-manager'
 -t, --tag         The tag of the container image. If not set defaults to 'latest'
 -h, --help        This help message

##########################################################################################################################


EOT
}


update_image_registry() {
    local QUARKUS_PROPERTY="%prod.quarkus.container-image.registry=${IMAGE_REGISTRY}"
    echo "Adding property '${QUARKUS_PROPERTY}' to application.properties ..."
    echo "${QUARKUS_PROPERTY}" >> ${PATH_APPLICATION_PROPERTIES}
}


update_image_org() {
    local QUARKUS_PROPERTY="%prod.quarkus.container-image.group=${IMAGE_ORG}"
    echo "Adding property '${QUARKUS_PROPERTY}' to application.properties ..."
    echo "${QUARKUS_PROPERTY}" >> ${PATH_APPLICATION_PROPERTIES}
}


update_image_name() {
    local QUARKUS_PROPERTY="%prod.quarkus.container-image.name=${IMAGE_NAME}"
    echo "Adding property '${QUARKUS_PROPERTY}' to application.properties ..."
    echo "${QUARKUS_PROPERTY}" >> ${PATH_APPLICATION_PROPERTIES}
}


update_image_tag() {
    local QUARKUS_PROPERTY="%prod.quarkus.container-image.tag=${IMAGE_TAG}"
    echo "Adding property '${QUARKUS_PROPERTY}' to application.properties ..."
    echo "${QUARKUS_PROPERTY}" >> ${PATH_APPLICATION_PROPERTIES}
}


build_project() {
    local BUILD_FLAGS="$1"
    echo "#######################################################################################################"
    echo " Building Project '${PROJECT_NAME}'..."
    echo " Build Command: ${MVN_BUILD_COMMAND}"
    echo "#######################################################################################################"
    ${MVN_BUILD_COMMAND}
}


build_image() {
    local BUILD_FLAGS="$1"
    echo "#######################################################################################################"
    echo " Building Image ${IMAGE_REGISTRY}/${IMAGE_ORG}/${IMAGE_NAME}:${IMAGE_TAG}"
    echo " IMAGE_REGISTRY: ${IMAGE_REGISTRY}"
    echo " IMAGE_ORG: ${IMAGE_ORG}"
    echo " IMAGE_NAME: ${IMAGE_NAME}"
    echo " IMAGE_TAG: ${IMAGE_TAG}"
    echo " Build Command: ${DOCKER_BUILD_COMMAND}"
    echo "#######################################################################################################"
    ${DOCKER_BUILD_COMMAND}
}


push_image() {
    echo "Logging to ${IMAGE_REGISTRY}..."
    echo "docker login -u ${RHOAS_QUAY_USER} -p ${RHOAS_QUAY_TOKEN} ${IMAGE_REGISTRY}"
    docker login -u "${RHOAS_QUAY_USER}" -p "${RHOAS_QUAY_TOKEN}" "${IMAGE_REGISTRY}"
    if [ $? -eq 0 ]
    then
      echo "Login to ${IMAGE_REGISTRY} Succeeded!"
    else
      echo "Login to ${IMAGE_REGISTRY} Failed!"
    fi

    echo "#######################################################################################################"
    echo " Pushing Image ${IMAGE_REGISTRY}/${IMAGE_ORG}/${IMAGE_NAME}:${IMAGE_TAG}"
    echo "#######################################################################################################"
    docker push "${IMAGE_REGISTRY}/${IMAGE_ORG}/${IMAGE_NAME}:${IMAGE_TAG}"
    if [ $? -eq 0 ]
    then
      echo "Image successfully pushed to ${IMAGE_REGISTRY}"
    else
      echo "Image Push Failed!"
    fi
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
          -o|--org)
            shift
            IMAGE_ORG="$1"
            ;;
          -n|--name)
            shift
            IMAGE_NAME="$1"
            ;;
          -t|--tag)
            shift
            IMAGE_TAG="$1"
            ;;
          *)
            echo "Unknown argument: $1"
            display_usage
            exit 1
            ;;
        esac
        shift
    done
    
    # The credentials to quay.io will be provided during pipeline runtime and you should make sure that following environment variables are available
    if [[ ! -z "${RHOAS_QUAY_USER}" ]] && [[ ! -z "${RHOAS_QUAY_TOKEN}" ]]; then
       echo "==| RHOAS Quay.io user and token is set, will push images to RHOAS org |=="
    else
       echo "RHOAS Quay.io user and token is not set. Aborting the process..."
       exit 1
    fi


    # Setting additional build flags
    BUILD_FLAGS="-Dapicurio-registry-tenant-manager-client.version=${TENANT_MANAGER_CLIENT_VERSION}"

    # function calls
    update_image_registry
    update_image_org
    update_image_name
    update_image_tag
    build_project ${BUILD_FLAGS}
    build_image ${BUILD_FLAGS}
    push_image

}

main $*
