import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DeploymentUpsert extends VBox {
    private ClusterManager clusterManager;
    private MainBorderPane mainBorderPane;

    // AI Citation:
    // Task: Used ChatGPT to learn how to switch out panes and go back and forth between them
    // Integration: Took advantage of Runnable to restore original center pane when returning
    public DeploymentUpsert(Runnable goBack, ClusterManager clusterManager, MainBorderPane mainBorderPane) {
        this.clusterManager = clusterManager;
        this.mainBorderPane = mainBorderPane;
    	//styling 
        setSpacing(15);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #e5e7eb"); //dirty white

        // Title (only this is centered)
        Label titleLabel = new Label("Create a new Deployment");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: red;");

        HBox titleBox = new HBox(titleLabel, errorLabel); // Add labels to HBox pane
        titleBox.setAlignment(Pos.CENTER); // Center the title horizontally
        getChildren().addAll(titleBox, errorLabel);

        // This is the main part of the form
        // This is where I will collect user input

        // Cluster Selection Dropdown
        Label clusterLabel = new Label("Select Cluster:");
        ComboBox<String> clusterComboBox = new ComboBox<>();
        List<Cluster> clusters = clusterManager.getAllClusters();
        // add clusters to dropdown 
        for (Cluster c : clusters) {
            clusterComboBox.getItems().add(c.getName());
        }
        if (!clusters.isEmpty()) {
            clusterComboBox.setValue(clusters.get(0).getName());
        }
        // Make sure its not too big
        clusterComboBox.setMaxWidth(250);

        // Deployment Name
        Label deploymentNameLabel = new Label("Deployment Name:");
        TextField deploymentNameTextField = new TextField();
        deploymentNameTextField.setMaxWidth(250);

        // Image
        Label imageLabel = new Label("Image:");
        TextField imageTextField = new TextField();
        imageTextField.setMaxWidth(250);

        // CPU
        Label cpuLabel = new Label("CPU:");
        TextField cpuTextField = new TextField();
        cpuTextField.setMaxWidth(250);

        // Memory
        Label memoryLabel = new Label("Memory:");
        TextField memoryTextField = new TextField();
        memoryTextField.setMaxWidth(250);

        // Disk Space
        Label diskLabel = new Label("Disk Space:");
        TextField diskTextField = new TextField();
        diskTextField.setMaxWidth(250);

        // Replicas
        Label replicasLabel = new Label("Replicas:");
        TextField replicasTextField = new TextField();
        replicasTextField.setMaxWidth(250);
        
        // Create button. This will validate the input and create deployment object
        Button createButton = new Button("Create");
        createButton.setOnAction(event -> {
        	errorLabel.setText(""); // clear out old errors
        	// Get info from text box
            String selectedClusterName = clusterComboBox.getValue();
            String name = deploymentNameTextField.getText();
            String image = imageTextField.getText();
            String cpu = cpuTextField.getText();
            String memory = memoryTextField.getText();
            String diskSpace = diskTextField.getText();
            int replicas;

            // Validate the data
            if (selectedClusterName == null || selectedClusterName.isEmpty()) {
                errorLabel.setText("Please select a cluster!");
                return;
            }

            if (name.isEmpty()) {
                errorLabel.setText("Deployment name cannot be empty!");
                return;
            }

            if (image.isEmpty()) {
            	errorLabel.setText("Image cannot be empty!");
                return;
            }

            if (!cpu.matches("\\d+")) {
            	errorLabel.setText("CPU must be a number!");
                return;
            }
            if (!memory.matches("\\d+")) {
            	errorLabel.setText("Memory must be a number!");
                return;
            }
            if (!diskSpace.matches("\\d+")) {
            	errorLabel.setText("Disk space must be a number!");
                return;
            }

            try {
            	replicas = Integer.parseInt(replicasTextField.getText().trim());
            	if (replicas < 1) {
            		errorLabel.setText("Replicas must be at least 1!");
            		return;
            	}
            } catch (NumberFormatException e) {
            	errorLabel.setText("Replicas must be a number!");
            	return;
            }
            // Create the deployment object
            Deployment deployment = new Deployment(name, image, replicas);
            deployment.setCpu(cpu);
            deployment.setMemory(memory);
            deployment.setDiskSpace(diskSpace);

            // Add deployment to the selected cluster using business logic layer
            clusterManager.addDeploymentToCluster(selectedClusterName, deployment);
            
            // Create nodes on cluster if none
            Cluster cluster = clusterManager.getClusterByName(selectedClusterName);
            if(cluster.getNodeCount() == 0) {
            	Node newNode = new Node("NewNode", "AMD");
            	cluster.addNode(newNode);
            	
            	// Add pods to node
            	for(int i = 0; i < replicas; i++) {
            		Pod pod = new Pod(image + "Pod", "newNamespace");
            		newNode.addPod(pod); 
            		Container container = new Container(image + "Container", image);
            		pod.addContainer(container);
            	}
            }
            
            
            mainBorderPane.refreshDefaultPane();
            
            
			goBack.run(); // Go back to default center pane
        });

        // Add everything
        getChildren().addAll(
            clusterLabel
            , clusterComboBox
            , deploymentNameLabel
            , deploymentNameTextField
            , imageLabel
            , imageTextField
            , cpuLabel
            , cpuTextField
            , memoryLabel
            , memoryTextField
            , diskLabel
            , diskTextField
            , replicasLabel
            , replicasTextField
            , createButton
        );
    }
 
}
