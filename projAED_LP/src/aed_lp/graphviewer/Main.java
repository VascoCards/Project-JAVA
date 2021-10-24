package aed_lp.graphviewer;

import aed_lp.Cache;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private static Iterable<DirectedEdge> edges;;
    private static List<Cache> caches;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("graphview.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);

        primaryStage.setTitle("Graph Creator");
        primaryStage.setScene(scene);
        primaryStage.show();

        Controller ctl = (Controller)fxmlLoader.getController();

        ctl.setStage(primaryStage);
        ctl.createGraph(caches,edges);



    }


    public static void main(String[] args) {
        launch(args);
    }

    public void showGraph(EdgeWeightedDigraph graphDistance, List<Cache> caches) {
        Main.edges = graphDistance.edges();
        Main.caches=caches;
        launch(new String[0]);
    }
}
