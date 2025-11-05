import java.util.Map;
// This class acts like a wrapper for the kubernetes java client library
// It will create easy access to the kubernetes API
public class ApiInterface {
	
	

	private static void getNodes(Cluster cluster, Map<String, Node> nodeMap, CoreV1Api coreApi, AppsV1Api appsApi ) {

		// Step 5: Fetch and populate nodes
		System.out.println("Fetching nodes...");
		V1NodeList nodeList = coreApi.listNode(null, null, null, null, null, null, null, null, null, null, null);

		for (V1Node k8sNode : nodeList.getItems()) {
			String nodeName = k8sNode.getMetadata().getName();
			String architecture = k8sNode.getStatus().getNodeInfo().getArchitecture();

			// Get node capacity
			Map<String, Quantity> capacity = k8sNode.getStatus().getCapacity();
			String cpu = capacity.get("cpu").toSuffixedString();
			String memory = capacity.get("memory").toSuffixedString();
			String storage = capacity.containsKey("ephemeral-storage") ?
				capacity.get("ephemeral-storage").toSuffixedString() : "Unknown";

			// Create node object
			Node node = new Node(nodeName, architecture);
			node.setCpu(cpu);
			node.setMemory(memory);
			node.setDiskSpace(storage);

			cluster.addNode(node);
			nodeMap.put(nodeName, node);

			System.out.println("  - Node: " + nodeName + " (CPU: " + cpu + ", Memory: " + memory + ")");
		}

	}

	
	private static void getPods(Map<String, Pod> podMap, Map<String, Node> nodeMap, CoreV1Api coreApi, AppsV1Api appsApi ) {
		// Step 6: Fetch and populate pods
		System.out.println("\nFetching pods...");
		V1PodList podList = coreApi.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null, null, null);



		for (V1Pod k8sPod : podList.getItems()) {
			String podName = k8sPod.getMetadata().getName();
			String namespace = k8sPod.getMetadata().getNamespace();
			String nodeName = k8sPod.getSpec().getNodeName();

			// Create pod object
			Pod pod = new Pod(podName);

			// Add containers to pod
			for (V1Container container : k8sPod.getSpec().getContainers()) {
				String containerName = container.getName();
				String image = container.getImage();
				pod.addContainer(new Container(containerName, image));
			}

			// Get resource requests if available
			if (k8sPod.getSpec().getContainers().size() > 0) {
				V1Container firstContainer = k8sPod.getSpec().getContainers().get(0);
				if (firstContainer.getResources() != null && firstContainer.getResources().getRequests() != null) {
					Map<String, Quantity> requests = firstContainer.getResources().getRequests();
					if (requests.containsKey("cpu")) {
						pod.setCpu(requests.get("cpu").toSuffixedString());
					}
					if (requests.containsKey("memory")) {
						pod.setMemory(requests.get("memory").toSuffixedString());
					}
				}
			}

			// Add pod to appropriate node
			if (nodeName != null && nodeMap.containsKey(nodeName)) {
				nodeMap.get(nodeName).addPod(pod);
			}

			podMap.put(namespace + "/" + podName, pod);

			System.out.println("  - Pod: " + podName + " (Namespace: " + namespace + ", Node: " + nodeName + ")");
		}
	}
	
	
	private static void getDeployments(Cluster cluster, Map<String, Pod> podMap ) {
		
		System.out.println("\nFetching deployments...");
		V1DeploymentList deploymentList = appsApi.listDeploymentForAllNamespaces(null, null, null, null, null, null, null, null, null, null, null);

		for (V1Deployment k8sDeployment : deploymentList.getItems()) {
			String deploymentName = k8sDeployment.getMetadata().getName();
			String namespace = k8sDeployment.getMetadata().getNamespace();
			Integer replicas = k8sDeployment.getSpec().getReplicas();

			// Get the container image from the pod template
			String image = "unknown";
			if (k8sDeployment.getSpec().getTemplate().getSpec().getContainers().size() > 0) {
				image = k8sDeployment.getSpec().getTemplate().getSpec().getContainers().get(0).getImage();
			}

			// Create deployment object
			Deployment deployment = new Deployment(deploymentName, image, replicas != null ? replicas : 0);

			// Try to find and associate pods managed by this deployment
			// Note: This is a simplified approach - in reality you'd match by labels
			String deploymentPrefix = deploymentName + "-";
			for (Map.Entry<String, Pod> entry : podMap.entrySet()) {
				if (entry.getKey().startsWith(namespace + "/" + deploymentPrefix)) {
					deployment.addManagedPod(entry.getValue());
				}
			}
		}

		cluster.addDeployment(deployment);

		System.out.println("  - Deployment: " + deploymentName + " (Namespace: " + namespace + ", Replicas: " + replicas + ", Image: " + image + ")");
	

	}

}
