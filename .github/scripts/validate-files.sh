#!/bin/bash

echo "Linting openshift templates"
spectral lint templates/srs-fleet-manager-template.yaml --ruleset .github/scripts/ocp-template-ruleset.js