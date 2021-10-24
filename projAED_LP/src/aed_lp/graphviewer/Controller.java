package aed_lp.graphviewer;

import aed_lp.Cache;
import edu.princeton.cs.algs4.DirectedEdge;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

public class Controller {

    protected static final int GROUP_MARGIN = 20;

    public TextArea edgesField;
    public TextField verticesNumberField;
    private GeoGraph gG;
    public Group graphGroup;
    private double radius = 20;


    private int cachesWidth;
    private int cachesHeight;
    private int ofsetWidth;
    private int ofsetHeight;
    private Stage stage;
    private List<Cache> caches;

    void setStage(Stage stage) {
        this.stage=stage;
    }


    public void createGraphGroup(){
        double screenWidth = stage.getWidth()-20; //graphGroup.getBoundsInLocal().getWidth();
        double screenHeight = stage.getHeight()-50;// graphGroup.getBoundsInLocal().getHeight();

        double rW = screenWidth/cachesWidth;
        double rH = screenHeight/cachesHeight;
        //usar o menor dos racios
        double r= rW < rH ? rW : rH;

        graphGroup.getChildren().clear();

        for(int i=0; i<gG.V(); i++){

            double x = (gG.getVertexPosX(i)-ofsetWidth) * r;
            double y = (gG.getVertexPosY(i)-ofsetHeight) * r;
            Circle c = new Circle(x, y, radius);
            c.setFill(Color.YELLOW);

            StackPane stack = new StackPane();
            stack.setLayoutX(x-radius);
            stack.setLayoutY(y-radius);
            //stack.getChildren().addAll(c, new Text(i + ""));
            stack.getChildren().addAll(c, new Text(caches.get(i).getNome()));

            graphGroup.getChildren().add(stack);

            if(gG.E() > 0){
                for(Integer adj: gG.adj(i)){
                    Line line = new Line(x, y, (gG.getVertexPosX(adj)-ofsetWidth)*r, (gG.getVertexPosY(adj)-ofsetHeight)*r);
                    graphGroup.getChildren().add(line);
                }
            }
        }
    }

    public void createGraph(List<Cache> caches, Iterable<DirectedEdge> edges){
        this.caches = caches;


        ChangeListener<Number> stageSizeListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                createGraphGroup();
            }
        };

        stage.widthProperty().addListener(stageSizeListener);
        stage.heightProperty().addListener(stageSizeListener);


        try{
            int numVertices=caches.size();

            //determinar as dimensoes ocupadas pelas caches
            int minLat,maxLat;
            int minLng,maxLng;
            //multiplica-se as coordenadas por 10000 para ficar com 5 decimais
            minLng=maxLng=(int)(caches.get(0).getLocalizacao().getLongitude()*10000);
            minLat=maxLat=(int)(caches.get(0).getLocalizacao().getLatitude()*10000);

            for (Cache cache: caches){
                int lng = (int) (cache.getLocalizacao().getLongitude()*10000);
                int lat = (int) (cache.getLocalizacao().getLatitude()*10000);
                if (lat<minLat) minLat=lat;
                if (lng<minLng) minLng=lng;
                if (lat>maxLat) maxLat=lat;
                if (lng>maxLng) maxLng=lng;
            }

            createNewGraph(numVertices);

            for(DirectedEdge edge: edges){

                int v = edge.from();
                int adj = edge.to();

                if(!gG.containsEdge(v, adj))
                    gG.addEdge(v, adj);
            }

            cachesWidth  = maxLng - minLng;
            cachesHeight = maxLat - minLat;

            ofsetWidth = minLng;
            ofsetHeight = minLat;

            //definir a posicao de cada cache
            int i=0;
            for (Cache cache: caches){
                int lng = (int)(cache.getLocalizacao().getLongitude()*10000);
                int lat = (int)(cache.getLocalizacao().getLatitude()*10000);
                gG.setVertexPosition(i,lng,lat);
                i++;
            }

            createGraphGroup();
        } catch(NumberFormatException e){
            System.out.println("Error: Values not inserted");
        }

    }

    public void old_createGraphGroup(){
        graphGroup.getChildren().clear();

        for(int i=0; i<gG.V(); i++){
            Circle c = new Circle(gG.getVertexPosX(i), gG.getVertexPosY(i), radius);
            c.setFill(Color.WHITE);

            StackPane stack = new StackPane();
            stack.setLayoutX(gG.getVertexPosX(i)-radius);
            stack.setLayoutY(gG.getVertexPosY(i)-radius);
            stack.getChildren().addAll(c, new Text(i + ""));

            graphGroup.getChildren().add(stack);

            if(gG.E() > 0){
                for(Integer adj: gG.adj(i)){
                    Line line = new Line(gG.getVertexPosX(i), gG.getVertexPosY(i), gG.getVertexPosX(adj), gG.getVertexPosY(adj));
                    graphGroup.getChildren().add(line);
                }
            }
        }
    }

    private void createNewGraph(int nVertices){
        if(gG == null){
            gG = new GeoGraph(nVertices);
        } else
            gG = new GeoGraph(gG, nVertices);
    }

    public void handleClearButtonAction(ActionEvent actionEvent) {
        graphGroup.getChildren().clear();
        edgesField.setText("");
        verticesNumberField.setText("");
        gG = null;
    }

    public void handleVerticesButtonAction(ActionEvent actionEvent) {
        try{
            createNewGraph(Integer.parseInt(verticesNumberField.getText()));
            createGraphGroup();
        } catch(NumberFormatException e){
            System.out.println("Error: Vertices not inserted");
        }
    }

    public void handleEdgesButtonAction(ActionEvent actionEvent) {
        try{
            if(gG != null)
                gG = new GeoGraph(gG);
            else
                createNewGraph(Integer.parseInt(verticesNumberField.getText()));

            String[] lines = edgesField.getText().split("\n");
            for(String line: lines){
                String[] position = line.split(";");
                int v = Integer.parseInt(position[0]);
                int adj = Integer.parseInt(position[1]);

                if(!gG.containsEdge(v, adj))
                    gG.addEdge(v, adj);
            }
            createGraphGroup();
        } catch(NumberFormatException e){
            System.out.println("Error: Values not inserted");
        }
    }

}
