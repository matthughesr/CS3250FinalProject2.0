import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DeploymentUpsert extends VBox {
    private Cluster cluster;
    
    public DeploymentUpsert(Runnable goBack, Cluster cluster) {
        this.cluster = cluster;
    	//styling 
        setSpacing(15);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #e5e7eb"); //dirty white

        // Title (only this is centered)
        Label titleLabel = new Label("Create a new Deployment");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER); // Center the title horizontally
        getChildren().add(titleBox);

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
        
        // Create button
        Button createButton = new Button("Create");
        createButton.setOnAction(event -> { 
            String name = deploymentNameTextField.getText();
            String image = imageTextField.getText();
            String cpu = cpuTextField.getText();
            String memory = memoryTextField.getText();
            String diskSpace = diskTextField.getText();
            int replicas;
            
            if (name.isEmpty()) {
                showAlert("Deployment name cannot be empty!");
                return;
            }

            if (image.isEmpty()) {
                showAlert("Image cannot be empty!");
                return;
            }

            try {
                replicas = Integer.parseInt(replicasTextField.getText().trim());
                if (replicas < 1) {
                    showAlert("Replicas must be at least 1!");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Replicas must be a number!");
                return;
            }

            // Optional: validate CPU, memory, disk if needed
            if (!cpu.matches("\\d+")) {
                showAlert("CPU must be a number!");
                return;
            }
            if (!memory.matches("\\d+")) {
                showAlert("Memory must be a number!");
                return;
            }
            if (!diskSpace.matches("\\d+")) {
                showAlert("Disk space must be a number!");
                return;
            }

            // Create the deployment object
            Deployment deployment = new Deployment(name, image, replicas);
            deployment.setCpu(cpu);
            deployment.setMemory(memory);
            deployment.setDiskSpace(diskSpace);
            
            // Add the deployment to the cluster
            cluster.addDeployment(deployment);
            
            // Show success message
            showSuccessAlert("Deployment '" + name + "' created successfully!");
            
			goBack.run();
        });

        // Add everything 
        getChildren().addAll(
            deploymentNameLabel
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
            ,createButton
        );
    }
    
    // Method
    
    // Helper method for alerts. 

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
