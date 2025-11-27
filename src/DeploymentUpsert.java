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
import io.kubernetes.client.openapi.ApiException;

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

            // Create deployment via Kubernetes API
            try {
                // Call ClusterManager to create deployment in Kubernetes
                clusterManager.createDeploymentInCluster(
                    selectedClusterName,
                    name,
                    image,
                    replicas,
                    cpu,
                    memory,
                    diskSpace
                );

                // Show success dialog
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText("Deployment Created");
                successAlert.setContentText(
                    "Deployment '" + name + "' created successfully with " +
                    replicas + " replicas."
                );
                successAlert.showAndWait();

                // Refresh dashboard to show new deployment
                mainBorderPane.refreshDefaultPane();

                // Return to main view
                goBack.run();

            } catch (ApiException e) {
                // Handle Kubernetes API errors
                String errorMsg = getErrorMessage(e, name);

                // Show error dialog
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Deployment Creation Failed");
                errorAlert.setHeaderText("Failed to create deployment");
                errorAlert.setContentText(errorMsg);
                errorAlert.showAndWait();

                // Also show in error label
                errorLabel.setText("Error: " + errorMsg);

                // Log detailed error for debugging
                System.err.println("Failed to create deployment: " + e.getMessage());
                e.printStackTrace();

                // DO NOT call goBack() - keep user on form to fix issues

            } catch (IllegalArgumentException e) {
                // Cluster not found or other validation error
                errorLabel.setText("Error: " + e.getMessage());
            } catch (IllegalStateException e) {
                // ApiInterface not set
                errorLabel.setText("Error: " + e.getMessage());
            }
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

    /**
     * Converts Kubernetes ApiException to user-friendly error message.
     */
    private String getErrorMessage(ApiException e, String deploymentName) {
        int code = e.getCode();

        switch (code) {
            case 409:
                return "Deployment '" + deploymentName + "' already exists. Please choose a different name.";
            case 400:
                return "Invalid deployment configuration. Check your inputs.";
            case 401:
                return "Authentication failed. Check Kubernetes configuration.";
            case 403:
                return "Permission denied. Insufficient cluster permissions.";
            case 422:
                return "Deployment validation failed. Check resource values.";
            case 500:
            case 503:
                return "Kubernetes server error. Please try again later.";
            default:
                return "Unexpected error (code " + code + "): " + e.getMessage();
        }
    }

}
