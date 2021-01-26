# Openshift deployment manifests

The keycloak manifests assume the namespace `managed-service-registry` exists, so your first step should be.

```
oc new-project managed-service-registry
```

Then you can apply the keycloak manifests, note this keycloak deployment is only used for demo and development purposes.

```
oc apply -f templates/keycloak/00-operator.yaml
oc apply -f templates/keycloak/01-keycloak.yaml
```

Wait until keycloak is running. And then update the configmap with the keycloak route and and apply it.

```
sed s,{KEYCLOAK_ROUTE_PLACEHOLDER},$(oc get route keycloak --template='{{ .spec.host }}'),g templates/config/00-apicurio-registry-configmap.yaml | oc apply -f -
oc apply -f templates/config/00-service-api-configmap.yaml
```

Now you can apply the rest of the manifests.
```
oc apply -f templates
```

To be continued...