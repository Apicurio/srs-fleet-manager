
if [ -z "$1" ]
  then
    echo "Registry name is required"
    exit 1
fi

REGISTRY_NAME=$1

http http://$(oc get route service-api --template='{{ .spec.host }}')/api/serviceregistry_mgmt/v1/registries name=$REGISTRY_NAME

echo 
sleep 1

http http://$(oc get route service-api --template='{{ .spec.host }}')/api/serviceregistry_mgmt/v1/registries