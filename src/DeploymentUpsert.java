import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import io.kubernetes.client.openapi.ApiException;

public class DeploymentUpsert extends ScrollPane {
    private ClusterManager clusterManager;
    private MainBorderPane mainBorderPane;

    // AI Citation:
    // Task: Used ChatGPT to learn how to switch out panes and go back and forth between them
    // Integration: Took advantage of Runnable to restore original center pane when returning
    public DeploymentUpsert(Runnable goBack, ClusterManager clusterManager, MainBorderPane mainBorderPane) {
        this.clusterManager = clusterManager;
        this.mainBorderPane = mainBorderPane;

        // Scroll faster
        addEventFilter(ScrollEvent.SCROLL, event -> {
            double delta = event.getDeltaY() * 6; // Increase multiplier for faster scrolling
            setVvalue(getVvalue() - delta / getContent().getBoundsInLocal().getHeight());
            event.consume();
        });

        // Create main container for everything
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setStyle("-fx-background-color: #e5e7eb"); //dirty white

		// Back button
		Button backButton = new Button("Back");
		backButton.getStyleClass().add("button");
		backButton.setOnAction(e -> goBack.run());

		// HBox for back button
		HBox backButtonBox = new HBox(backButton);
		backButtonBox.setAlignment(Pos.TOP_RIGHT);

        // Title (only this is centered)
        Label titleLabel = new Label("Create a new Deployment");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: red;");

        HBox titleBox = new HBox(titleLabel, errorLabel); // Add labels to HBox pane
        titleBox.setAlignment(Pos.CENTER); // Center the title horizontally
        mainContainer.getChildren().addAll(backButtonBox,titleBox, errorLabel);

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

        // Image (editable dropdown with common images)
        Label imageLabel = new Label("Image:");
        ComboBox<String> imageComboBox = new ComboBox<>();
        imageComboBox.getItems().addAll(
            "nginx",
            "redis",
//            "postgres",
//            "mysql",
            "mongo",
            "httpd"
//            "alpine"

        );
        imageComboBox.setEditable(true); // Allow custom input
        imageComboBox.setPromptText("Select or type custom image");
        imageComboBox.setMaxWidth(250);

        // CPU Dropdown
        Label cpuLabel = new Label("CPU (millicores):");
        ComboBox<String> cpuComboBox = new ComboBox<>();
        cpuComboBox.getItems().addAll(
            "50",
            "100",
            "250",
            "500",
            "1000",
            "2000"
        );
        cpuComboBox.setValue("100"); // Default value
        cpuComboBox.setMaxWidth(250);

        // Memory (options in Mi)
        Label memoryLabel = new Label("Memory (Mi):");
        ComboBox<String> memoryComboBox = new ComboBox<>();
        memoryComboBox.getItems().addAll(
            "64",
            "128",
            "256",
            "512",
            "1024",
            "2048"
        );
        memoryComboBox.setValue("128"); // Default value
        memoryComboBox.setMaxWidth(250);

        // Disk Space (dropdown with reasonable options in Mi)
        Label diskLabel = new Label("Disk Space (Mi):");
        ComboBox<String> diskComboBox = new ComboBox<>();
        diskComboBox.getItems().addAll(
            "128",
            "256",
            "512",
            "1024",
            "2048",
            "4096"
        );
        diskComboBox.setValue("512"); // Default value
        diskComboBox.setMaxWidth(250);

        // Replicas (dropdown with reasonable options)
        Label replicasLabel = new Label("Replicas:");
        ComboBox<String> replicasComboBox = new ComboBox<>();
        replicasComboBox.getItems().addAll(
            "1",
            "2",
            "3",
            "4",
            "5",
            "10"
        );
        replicasComboBox.setValue("1"); // Default value
        replicasComboBox.setMaxWidth(250);
        
        // Create button. This will validate the input and create deployment object
        Button createButton = new Button("Create");
        createButton.setOnAction(event -> {
        	errorLabel.setText(""); // clear out old errors
        	// Get info from combo boxes and text fields
            String selectedClusterName = clusterComboBox.getValue();
            String name = deploymentNameTextField.getText();
            String image = imageComboBox.getValue();
            String cpu = cpuComboBox.getValue();
            String memory = memoryComboBox.getValue();
            String diskSpace = diskComboBox.getValue();
            int replicas;

            // Validate the data
            if (selectedClusterName == null || selectedClusterName.isEmpty()) {
                errorLabel.setText("Please select a cluster!");
                return;
            }

            if (name == null || name.isEmpty()) {
                errorLabel.setText("Deployment name cannot be empty!");
                return;
            }
            else {
            	// Convert to lower(kubernetes does not allow uppercase in names)
            	name = name.toLowerCase();
            	
            	//regex pattern for checking if it starts/ends with lower case
            	// or digit. And only has a-z, 0-9 or -
                Pattern regex = Pattern.compile("^[a-z0-9]([-a-z0-9]*[a-z0-9])?$");
                if(!regex.matcher(name).matches()) {
                	errorLabel.setText(
                	"Deployment name not valid. Can only contatin a-z letters, 0-9 numbers, and hyphens (-)");
                return;
                }
            }

            if (image == null || image.trim().isEmpty()) {
            	errorLabel.setText("Image cannot be empty!");
                return;
            }

            if (cpu == null || !cpu.matches("\\d+")) {
            	errorLabel.setText("CPU must be a number!");
                return;
            }
            if (memory == null || !memory.matches("\\d+")) {
            	errorLabel.setText("Memory must be a number!");
                return;
            }
            if (diskSpace == null || !diskSpace.matches("\\d+")) {
            	errorLabel.setText("Disk space must be a number!");
                return;
            }

            try {
            	String replicasValue = replicasComboBox.getValue();
            	if (replicasValue == null || replicasValue.trim().isEmpty()) {
            		errorLabel.setText("Please select number of replicas!");
            		return;
            	}
            	replicas = Integer.parseInt(replicasValue.trim());
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
                
                // Show progress bar for a couple seconds, then show success
                progressBar(() -> {
                    // Show success dialog
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText("Deployment Created");
                    successAlert.setContentText(
                        "Deployment created successfully "
                    );
                    successAlert.showAndWait();

                    // Refresh dashboard to show new deployment
                    mainBorderPane.refreshDefaultPane();

                    // Return to main view
                    goBack.run();
                }, 100);

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
        mainContainer.getChildren().addAll(
            clusterLabel
            , clusterComboBox
            , deploymentNameLabel
            , deploymentNameTextField
            , imageLabel
            , imageComboBox
            , cpuLabel
            , cpuComboBox
            , memoryLabel
            , memoryComboBox
            , diskLabel
            , diskComboBox
            , replicasLabel
            , replicasComboBox
            , createButton
        );

        // Set the content of the ScrollPane
        setContent(mainContainer);

        // Configure ScrollPane properties
        setFitToWidth(true);
        setPannable(true);
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
    
    
    private void progressBar(Runnable onComplete, int time) {
        Stage owner = (Stage) getScene().getWindow();
        ProgressWindow progressWindow = new ProgressWindow(owner);
        progressWindow.show();

        // Create background task to avoid freezing UI
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Animate progress 
                for (int i = 0; i <= time; i++) {
                    updateProgress(i, time);
                    Thread.sleep(100); // 200ms x 10 = 2 seconds
                }
                return null;
            }
        };

        // Update progress window as task progresses
        task.progressProperty().addListener((obs, oldVal, newVal) -> {
            progressWindow.update(newVal.doubleValue());
        });

        // When done, close window and run callback
        task.setOnSucceeded(e -> {
            progressWindow.dispose();
            onComplete.run();
        });

        // Run in background thread
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

}
