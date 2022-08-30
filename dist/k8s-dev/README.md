
# Start Multitenant Apicurio Registry infrastructure for Kubernetes [dev-mode]

This folder contains the Kubernetes resources to start a local and self-contained dev environemnt (Keycloak included).

This has been tested starting `minikube` with the following configuration:

```bash
minikube start --driver=docker --memory 8192 --cpus 3
```

In this folder, deploy the resources with:

```bash
kubectl apply -f .
```

Wait for all the pods to be ready:

```bash
kubectl wait --for=condition=Ready pods --all --timeout=600s
```

And start the necessary tunnels with the script:

```bash
./port-forward.sh
```
(stop the port forwards as usual with `Ctrl+C`)

Now you can access Apicurio UI at [http://localhost:9090](http://localhost:9090)

Enjoy!
