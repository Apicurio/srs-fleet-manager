#!/bin/bash

echo "Linting openshift templates"
spectral lint templates/srs-fleet-manager-template.yaml --ruleset https://raw.githubusercontent.com/Apicurio/apicurio-registry/master/scripts/ocp-template-ruleset.js