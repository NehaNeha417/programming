package charts.barGraph;

import benchmarks.Benchmark;
import benchmarks.BullMergeBenchmark;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Ethan Petuchowski 3/6/16
 */
public class Benchmarker extends Application {

    static GraphicsContext gc;

    static Benchmark benchmark = new BullMergeBenchmark();

    public static void main(String[] args) {
        launch(args);
    }

    @Override public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Benchmarker");
        StackPane root = new StackPane();
        final int HEIGHT = 700;
        final int WIDTH = 700;
        Scene s = new Scene(root, WIDTH, HEIGHT, Color.BLACK);
        final Canvas canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();
        root.getChildren().addAll(canvas);
        ObservableList<Double> data = FXCollections.observableArrayList();
        NonNegativeBarGraph lineGraph = new NonNegativeBarGraph(gc);
        lineGraph.drawBasedOn(data);
        primaryStage.setScene(s);
        primaryStage.show();
        primaryStage.show();
        new Thread() {
            @Override public void run() {
                benchmark.runTheBenchmarks(data);
            }
        }.start();
    }
}
