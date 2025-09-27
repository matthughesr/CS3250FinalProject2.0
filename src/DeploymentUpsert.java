import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DeploymentUpsert extends VBox {
    public DeploymentUpsert() {
        setSpacing(15);
        setPadding(new Insets(20));

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
}
