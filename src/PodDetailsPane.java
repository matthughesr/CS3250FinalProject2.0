import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import java.util.Map;
import javafx.scene.input.ScrollEvent;


public class PodDetailsPane extends ScrollPane{
	private MainBorderPane mainBorderPane;

	public PodDetailsPane(Runnable goBack,Pod pod, MainBorderPane mainBorderPane) {
		this.mainBorderPane = mainBorderPane;

		// Scroll faster
		addEventFilter(ScrollEvent.SCROLL, event -> {
		    double delta = event.getDeltaY() * 6; // Increase multiplier for faster scrolling
		    setVvalue(getVvalue() - delta / getContent().getBoundsInLocal().getHeight());
		    event.consume();
		});
		
		
		// Create a VBox to hold all the content
		VBox contentBox = new VBox(10);
		// contentBox.setAlignment(Pos.TOP_CENTER);

		// Title label with bold styling
		Label labelPart = new Label("Pod Name: ");
		labelPart.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
		// title label part 2: name of pod
		Label namePart = new Label(pod.getName());
		namePart.setStyle("-fx-font-size: 18px; -fx-text-fill: #3366ff;");

		// HBox to center things
		HBox titleBox = new HBox(5, labelPart, namePart); // Add labels to HBox pane
		titleBox.setAlignment(Pos.CENTER); // Center the title horizontally
		contentBox.getChildren().addAll(titleBox);
		
		// Labels for pod specific details
		Label namespaceLabel = new Label("Namespace: " + pod.getNamespace());
		
		Label nodeLabel = new Label("Node: " + pod.getNodeName());
		
		Label ipLabel = new Label("IP: " + pod.getIp());
		ipLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");

		Label cpuLabel = new Label("CPU: " + pod.getCpu());
		cpuLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");

		Label memoryLabel = new Label("Memory: " + pod.getMemory());
		memoryLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");

		Label diskSpaceLabel = new Label("Disk Space: " + pod.getDiskSpace());
		diskSpaceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");
		

		// Add all labels to the info box
		contentBox.getChildren().addAll(namespaceLabel, ipLabel, cpuLabel, memoryLabel, diskSpaceLabel, nodeLabel);

		
		
		
		// Line chart to show CPU data
		// Line chart docs "https://docs.oracle.com/javafx/2/charts/line-chart.htm"
		final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Seconds");
        yAxis.setLabel("CPU (millicores)");
        //creating the chart
        final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);
        lineChart.setTitle("CPU Usage");

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("CPU Usage");
        lineChart.getData().add(series);

        // Set ID for CSS targeting
        lineChart.setId("cpu-chart");

        // Load external CSS file
        try {
            String cssPath = new java.io.File("src/pod-details.css").toURI().toString();
            lineChart.getStylesheets().add(cssPath);
        } catch (Exception e) {
            System.err.println("Could not load CSS file: " + e.getMessage());
        }

        // Timeline for updating CPU every second
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(1), event -> {
                // Fetch live metrics from Kubernetes API
                ApiInterface apiInterface = mainBorderPane.getClusterManager().getApiInterface();

                if (apiInterface != null) {
                    Map<String, String> metrics = apiInterface.fetchPodMetrics(pod.getName(), pod.getNamespace());

                    if (metrics != null && metrics.containsKey("cpu")) {
                        try {
                            double cpuValue = Double.parseDouble(metrics.get("cpu"));
                            cpuLabel.setText(String.format("CPU: %.2f millicores", cpuValue));

                            // x = time in seconds
                            int x = series.getData().size();

                            series.getData().add(new XYChart.Data<>(x, cpuValue));

                            // Limit number of points displayed and re-index
                            if (series.getData().size() > 100) {
                                series.getData().remove(0);
                                // Re-index remaining points to prevent gaps
                                for (int i = 0; i < series.getData().size(); i++) {
                                    series.getData().get(i).setXValue(i);
                                }
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Error parsing CPU value: " + e.getMessage());
                        }
                    } else {
                        System.err.println("No metrics available for pod " + pod.getName());
                    }
                }
            })
        );
        

        // Run CPU timeline forever
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        contentBox.getChildren().add(lineChart);

        
        
        
        
        
        
        
        
        // ========== MEMORY CHART ==========
        final NumberAxis x2Axis = new NumberAxis();
        final NumberAxis y2Axis = new NumberAxis();
        x2Axis.setLabel("Seconds");
        y2Axis.setLabel("Memory (MiB)");

        // Creating the memory chart
        final LineChart<Number,Number> lineChart2 = new LineChart<Number,Number>(x2Axis, y2Axis);
        lineChart2.setTitle("Memory Usage");

        XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
        series2.setName("Memory Usage");
        lineChart2.getData().add(series2);

        // Set ID for CSS targeting
        lineChart2.setId("memory-chart");

        // Load external CSS file
        try {
            String cssPath = new java.io.File("src/pod-details.css").toURI().toString();
            lineChart2.getStylesheets().add(cssPath);
        } catch (Exception e) {
            System.err.println("Could not load CSS file: " + e.getMessage());
        }

        // Timeline for updating memory every second
        Timeline timeline2 = new Timeline(
            new KeyFrame(Duration.seconds(1), event -> {
                // Fetch live metrics from Kubernetes API
                ApiInterface apiInterface = mainBorderPane.getClusterManager().getApiInterface();

                if (apiInterface != null) {
                    Map<String, String> metrics = apiInterface.fetchPodMetrics(pod.getName(), pod.getNamespace());

                    if (metrics != null && metrics.containsKey("memory")) {
                        try {
                            // Memory is in bytes, convert to MiB (Mebibytes)
                            double memoryBytes = Double.parseDouble(metrics.get("memory"));
                            double memoryMiB = memoryBytes / (1024.0 * 1024.0);
                            memoryLabel.setText(String.format("Memory: %.2f MiB", memoryMiB));

                            // x = time in seconds
                            int x = series2.getData().size();

                            series2.getData().add(new XYChart.Data<>(x, memoryMiB));

                            // Limit number of points displayed and re-index
                            if (series2.getData().size() > 100) {
                                series2.getData().remove(0);
                                // Re-index remaining points to prevent gaps
                                for (int i = 0; i < series2.getData().size(); i++) {
                                    series2.getData().get(i).setXValue(i);
                                }
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Error parsing memory value: " + e.getMessage());
                        }
                    } else {
                        System.err.println("No memory metrics available for pod " + pod.getName());
                    }
                }
            })
        );

        // Run memory timeline forever
        timeline2.setCycleCount(Animation.INDEFINITE);
        timeline2.play();

        contentBox.getChildren().add(lineChart2);

		// Set the content of the ScrollPane
		setContent(contentBox);

		// Configure ScrollPane properties
		setFitToWidth(true);
		setPannable(true);
		
		
		
		
		
		
		
		
		
		
		

		
		
	}

}
