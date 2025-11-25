// Kubernetes Java Client API imports
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.Metrics;
import io.kubernetes.client.custom.PodMetrics;
import io.kubernetes.client.custom.PodMetricsList;
import io.kubernetes.client.custom.ContainerMetrics;

// Regular imports
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * This class acts as a wrapper for the Kubernetes Java Client library.
 * It provides easy access to the Kubernetes API and methods to fetch cluster data.
 */
public class ApiInterface {

	private final CoreV1Api coreApi;
	private final AppsV1Api appsApi;
	private final Metrics metricsApi;


	// Constructor. 
	public ApiInterface(CoreV1Api coreApi, AppsV1Api appsApi, Metrics metricsApi) {
		this.coreApi = coreApi;
		this.appsApi = appsApi;
		this.metricsApi = metricsApi;
	}

// Wrapper methods
	
	public void fetchCluster(Cluster cluster) {
		// Since right now I only have the ability to run 1 cluster 
		// 			this will remain empty
	}
	
	
	// This method will get the info about nodes on the actual cluster and create objects for them
	public void fetchNodes(Cluster cluster) throws ApiException {
		System.out.println("Fetching nodes...");
		
		// Get list of all nodes in cluster
		// V1NodeList stores a list of V1Nodes, apiversion, kind, and metadata.
		V1NodeList nodeList = coreApi.listNode(null, null, null, null, null, null, null, null, null, null, null);

		// Get info for each node. 
		for (V1Node k8sNode : nodeList.getItems()) {
			String nodeName = k8sNode.getMetadata().getName();
			String architecture = k8sNode.getStatus().getNodeInfo().getArchitecture();

			// Get node cpu, memory, and storage
			Map<String, Quantity> capacity = k8sNode.getStatus().getCapacity();
			String cpu = capacity.get("cpu").toSuffixedString();
			String memory = capacity.get("memory").toSuffixedString();
			String storage = capacity.containsKey("ephemeral-storage") ?
				capacity.get("ephemeral-storage").toSuffixedString() : "Unknown";

			// Create node object
			Node node = new Node(nodeName, architecture);
			
			// Assign values
			node.setCpu(cpu);
			node.setMemory(memory);
			node.setDiskSpace(storage);

			// Add node to cluster
			cluster.addNode(node);

			System.out.println("  - Node: " + nodeName + " (CPU: " + cpu + ", Memory: " + memory + ")");
		}
	}


	// This will fetch the pods from each node and create objects for them and add it to the node
	public void fetchPods(Cluster cluster) throws ApiException {
		System.out.println("\nFetching pods...");
		V1PodList podList = coreApi.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null, null, null);

		// Build a map of node names to Node objects for quick lookup
		Map<String, Node> nodeMap = new HashMap<>();
		for (Node node : cluster.getNodes()) {
			nodeMap.put(node.getName(), node);
		}

		for (V1Pod k8sPod : podList.getItems()) {
			String podName = k8sPod.getMetadata().getName();
			String namespace = k8sPod.getMetadata().getNamespace();
			String nodeName = k8sPod.getSpec().getNodeName();
			String podIP = k8sPod.getStatus().getPodIP(); 
			String status = k8sPod.getStatus().getPhase();

			// Create pod object
			Pod pod = new Pod(podName);
			pod.setNamespace(namespace);
			pod.setNodeName(nodeName);
			pod.setIp(podIP);
			pod.setStatus(status);
			

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
						System.out.println("Current cpu for pod " + podName + " is " + pod.getCpu());
					}
					else {
						System.out.println("No cpu data found for pod: " + podName);
					}
					if (requests.containsKey("memory")) {
						pod.setMemory(requests.get("memory").toSuffixedString());
						System.out.println("Current memory for pod " + podName + " is" + pod.getMemory());
					}
					else {
						System.out.println("No memory data found for pod: " + podName);
					}
				}
			}

			// Add pod to appropriate node
			if (nodeName != null && nodeMap.containsKey(nodeName)) {
				nodeMap.get(nodeName).addPod(pod);
			}

			System.out.println("  - Pod: " + podName + " (Namespace: " + namespace + ", Node: " + nodeName + ")");
		}
	}

	// This will fetch all Deployments from the actual cluster
	public void fetchDeployments(Cluster cluster) throws ApiException {
		System.out.println("\nFetching deployments...");
		V1DeploymentList deploymentList = appsApi.listDeploymentForAllNamespaces(null, null, null, null, null, null, null, null, null, null, null);

		// Build a map of pods by namespace/name for quick lookup
		Map<String, Pod> podMap = new HashMap<>();
		for (Node node : cluster.getNodes()) {
			for (Pod pod : node.getPods()) {
				// We need to reconstruct the namespace/name key
				// This is a simplified approach - in reality we'd need to track namespace in Pod
				podMap.put(pod.getName(), pod);
			}
		}

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
			// Note: This is a simplified approach - in reality we should prob match by labels
			String deploymentPrefix = deploymentName + "-";
			for (Map.Entry<String, Pod> entry : podMap.entrySet()) {
				if (entry.getKey().startsWith(deploymentPrefix)) {
					deployment.addManagedPod(entry.getValue());
				}
			}

			cluster.addDeployment(deployment);

			System.out.println("  - Deployment: " + deploymentName + " (Namespace: " + namespace + ", Replicas: " + replicas + ", Image: " + image + ")");
		}
	}

	
	// AI Citation. This method was created with the help of claude code. Nov 14, 2025
	// Prompt: How do I fetch live metrics from the kubernetes API for a javafx line chart
	// Implementation: Used feedback from AI to use differnent method for fetching metrics used in line chart
	
	/**
	 * Fetches real-time metrics (CPU and memory usage) for a specific pod.
	 * Returns a Map with "cpu" and "memory" keys containing the current usage values.
	 *
	 * @param podName the name of the pod
	 * @param namespace the namespace of the pod
	 * @return Map containing "cpu" (in millicores) and "memory" (in bytes) as strings, or null if metrics unavailable
	 */
	public Map<String, String> fetchPodMetrics(String podName, String namespace) {
		try {
			// getPodMetrics returns a PodMetricsList for all pods in a namespace
			PodMetricsList podMetricsList = metricsApi.getPodMetrics(namespace);

			if (podMetricsList == null || podMetricsList.getItems() == null) {
				return null;
			}

			// Find the specific pod we're looking for
			PodMetrics podMetrics = null;
			for (PodMetrics pm : podMetricsList.getItems()) {
				if (pm.getMetadata() != null && podName.equals(pm.getMetadata().getName())) {
					podMetrics = pm;
					break;
				}
			}

			if (podMetrics == null || podMetrics.getContainers() == null || podMetrics.getContainers().isEmpty()) {
				return null;
			}

			// Aggregate metrics across all containers in the pod
			double totalCpuMillicores = 0;
			long totalMemoryBytes = 0;

			for (ContainerMetrics container : podMetrics.getContainers()) {
				Map<String, Quantity> usage = container.getUsage();

				if (usage.containsKey("cpu")) {
					// Quantity.getNumber() returns the value in cores (e.g., 0.917 cores)
					// We need to convert from cores to millicores by multiplying by 1000
					Quantity cpuQuantity = usage.get("cpu");
					double cpuCores = cpuQuantity.getNumber().doubleValue();
					double cpuMillicores = cpuCores * 1000.0;

					totalCpuMillicores += cpuMillicores;
				}

				if (usage.containsKey("memory")) {
					// Memory is in bytes
					totalMemoryBytes += usage.get("memory").getNumber().longValue();
				}
			}

			Map<String, String> metrics = new HashMap<>();
			// Store CPU as millicores (with decimal precision)
			metrics.put("cpu", String.format("%.2f", totalCpuMillicores));
			metrics.put("memory", String.valueOf(totalMemoryBytes));

			return metrics;

		} catch (ApiException e) {
			System.err.println("Error fetching metrics for pod " + podName + ": " + e.getMessage());
			return null;
		}
	}
}
