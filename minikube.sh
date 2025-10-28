

# run in git bash
# https://kubernetes.io/docs/tutorials/hello-minikube/


minikube start

kubectl create deployment hello-node --image=registry.k8s.io/e2e-test-images/agnhost:2.53 -- /agnhost netexec --http-port=8080

gradle build
gradle run








🚀 Step 2: Create the new nginx demo deployment

Run:

kubectl create deployment hello-node --image=nginxdemos/hello


This deploys a small NGINX container that serves a friendly “Welcome to nginx!” page.

🌐 Step 3: Expose it as a NodePort service

Run:

kubectl expose deployment hello-node --type=NodePort --port=80


This makes your pod accessible from outside the cluster (via a local Minikube proxy).

🔍 Step 4: Open it in your browser

Run:

minikube service hello-node


This command automatically opens the test app in your default browser — you’ll see a colorful “Welcome to nginx!” page with some pod info (like hostname, IP, etc).