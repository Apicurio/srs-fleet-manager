#!/bin/bash

echo "Linting openshift templates"
spectral lint templates/srs-fleet-manager-template.yaml --ruleset https://raw.githubusercontent.com/Apicurio/apicurio-registry/main/scripts/ocp-template-ruleset.js