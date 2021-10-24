package aed_lp;

import edu.princeton.cs.algs4.AcyclicSP;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        Gestor gestor = new Gestor();

        gestor.loadFromFile("input.txt");

        Utilizador admin = gestor.inserirUtilizador("Admin", "Antonio");
        Utilizador basic = gestor.inserirUtilizador("Basic","Bernardo");
        Utilizador premium = gestor.inserirUtilizador("Premium","Paula");

        Cache cache1 = gestor.inserirCache("geocache100", basic, new Coordenada(40.0,-9), "Porto"
                , LocalDateTime.parse("2021-01-01T10:00:00")
                , 10);

        Cache cache2 = gestor.inserirCache("geocache101", admin, new Coordenada(40.5,-9), "Coimbra"
                , LocalDateTime.parse("2021-01-01T10:00:00")
                , 10);

        Cache cache3 = gestor.inserirCache("geocache102", premium, new Coordenada(41,-9), "Lisboa"
                , LocalDateTime.parse("2021-01-01T10:00:00")
                , 10);


        gestor.registarItem("Bola", cache1, basic, LocalDateTime.parse("2021-01-02T10:00:00") );
        gestor.registarItem("Anel", cache2, premium, LocalDateTime.parse("2021-01-03T10:00:00") );
        gestor.registarItem("Livro", cache3, admin, LocalDateTime.parse("2021-01-04T10:00:00") );
        gestor.registarItem("Isqueiro", cache1, basic, LocalDateTime.parse("2021-01-05T10:00:00") );
        gestor.registarItem("Lapis", cache2, premium, LocalDateTime.parse("2021-01-06T10:00:00") );

        gestor.registarVisita("matchbox","Lapis", cache2, basic, LocalDateTime.parse("2021-03-06T10:00:00") );
        gestor.registarVisita("garrafa","Livro", cache3, basic, LocalDateTime.parse("2021-03-16T10:00:00") );

        gestor.registarTravelBug("tb1", cache2, basic, LocalDateTime.parse("2021-02-01T10:00:00") );
        gestor.recolherTravelBug("tb1", cache2, admin, LocalDateTime.parse("2021-02-02T10:00:00") );
        gestor.colocarTravelBug("tb1", cache3, admin, LocalDateTime.parse("2021-02-03T10:00:00") );
        gestor.recolherTravelBug("tb1", cache3, basic, LocalDateTime.parse("2021-03-02T10:00:00") );
        gestor.colocarTravelBug("tb1", cache1, basic, LocalDateTime.parse("2021-03-03T10:00:00") );
        gestor.recolherTravelBug("tb1", cache1, premium, LocalDateTime.parse("2021-03-05T10:00:00") );

        gestor.writeToFile("output.txt");


        //construir os grafos
        gestor.buildGrafos();

        /*
        AcyclicSP a = new AcyclicSP(gestor.getGraphTime(), 0);
        */


        //pesquisas
        gestor.now();
        System.out.println("___________________________________");
        System.out.println("___________________________________");

        System.out.println("Conjunto de caches da regiao norte");
        CacheSet cacheSet = gestor.buildRegiaoCacheSet("norte");
        printCaches(cacheSet);


        System.out.println("___________________________________");
        System.out.println("___________________________________");

        System.out.println("Conjunto de caches basic");
        cacheSet = gestor.buildDificuldadeCacheSet(Cache.BASIC);
        printCaches(cacheSet);

        System.out.println("___________________________________");
        System.out.println("___________________________________");

        System.out.println("Visitas por cache");
        Map<Cache,Integer> visitasPorCache = gestor.getVisitasPorCache();
        for(Cache cache : visitasPorCache.keySet()){
            System.out.println("Visitas: " + visitasPorCache.get(cache) + " - " + cache);
        }

        System.out.println("___________________________________");
        System.out.println("___________________________________");

        System.out.println("Caches mais visitadas com pelo menos 4 visitantes");
        cacheSet = gestor.buildMostVisitedCacheSet(4);
        printCaches(cacheSet);

        System.out.println("___________________________________");
        System.out.println("___________________________________");

        System.out.println("Caches menos visitadas com o maximo 3 visitantes");
        cacheSet = gestor.buildLeastVisitedCacheSet(3);
        printCaches(cacheSet);

        System.out.println("___________________________________");
        System.out.println("___________________________________");

        System.out.println("Caminho mais curto entre geocache 1 e geocache 15");
        List<CachePath> path = gestor.caminhoMaisCurto(gestor.pesquisaCache("geocache1"),
                gestor.pesquisaCache("geocache15"));
        printPath(path);

        System.out.println("___________________________________");
        System.out.println("___________________________________");

        System.out.println("Caminho mais rapido entre geocache 3 e geocache 17");
        path = gestor.caminhoMaisRapido(gestor.pesquisaCache("geocache3"),
                gestor.pesquisaCache("geocache17"));
        printPath(path);

        System.out.println("___________________________________");
        System.out.println("___________________________________");


        System.out.println("Caminho mais curto entre geocache 3 e geocache 17");
        path = gestor.caminhoMaisCurto(gestor.pesquisaCache("geocache3"),
                gestor.pesquisaCache("geocache17"));
        printPath(path);

        System.out.println("___________________________________");
        System.out.println("___________________________________");

        System.out.println("Caminho mais curto entre geocache 3 e geocache 17");
        System.out.println("evitando as mais visitadas");
        cacheSet = gestor.buildMostVisitedCacheSet(5);
        //e a cache 10
        cacheSet.add(gestor.pesquisaCache("geocache10"));
        path = gestor.caminhoMaisCurto(gestor.pesquisaCache("geocache3"),
                gestor.pesquisaCache("geocache17"),
                cacheSet);
        printPath(path);


        System.out.println("___________________________________");
        System.out.println("___________________________________");


        System.out.println("Dentre as caches basic obter o Caminho mais curto");
        System.out.println("entre geocache 3 e geocache 17");

        List<Cache> cachesSubgrafo =new ArrayList<>();
        EdgeWeightedDigraph grafo = gestor.subGrafoDistanceCachesPorTipo(Cache.BASIC, cachesSubgrafo);
        path = gestor.caminhoMenorCusto(grafo,cachesSubgrafo,
                gestor.pesquisaCache("geocache3"),
                gestor.pesquisaCache("geocache17"));
        printPath(path);





        aed_lp.graphviewer.Main graphViewer = new aed_lp.graphviewer.Main();

        //regiao
        //cachesSubgrafo = new ArrayList<>();
        //grafo = gestor.subGrafoDistanceCachesPorRegiao("centro", cachesSubgrafo);
        //graphViewer.showGraph(grafo, cachesSubgrafo);


        //todas
        //graphViewer.showGraph(gestor.getGraphDistance(), gestor.getCaches());

        System.out.println("CAIXEIRO VIAJANTE");

        System.out.println("___________________________________");
        System.out.println("___________________________________");


        //caixeiro viajante
        path = gestor.caminhoMaiorVisitas(gestor.pesquisaCache("geocache3"),300);
        int totalTime = 0;
        for (CachePath p: path) {
            totalTime+=p.getTime();
        }

        System.out.println("___________________________________");
        System.out.println("___________________________________");

        System.out.println("Caminho com mais caches visitadas, tempo: " + totalTime);
        printPathTime(path);

        path = gestor.caminhoMaiorVisitas(gestor.pesquisaCache("geocache3"),400);
        totalTime = 0;
        for (CachePath p: path) {
            totalTime+=p.getTime();
        }

        System.out.println("___________________________________");
        System.out.println("___________________________________");

        System.out.println("Caminho com mais caches visitadas, tempo: " + totalTime);
        printPathTime(path);


    }

    private static void printPath(List<CachePath> path) {
        boolean first=true;
        for(CachePath cachePath : path){
            if (first) {
                System.out.print("Cache: " + cachePath.getCache1().getNome());
                first = false;
            }


            System.out.print(" >> ("+String.format("%.2f",cachePath.getDistance())+") >> " + cachePath.getCache2().getNome());
        }
        System.out.println();
    }

    private static void printPathTime(List<CachePath> path) {
        boolean first=true;
        for(CachePath cachePath : path){
            if (first) {
                System.out.print("Cache: " + cachePath.getCache1().getNome());
                first = false;
            }
            System.out.print(" >> ("+cachePath.getTime()+") >> " + cachePath.getCache2().getNome());
        }
        System.out.println();
    }

    private static void printCaches(CacheSet cacheSet) {
        for(Cache cache :cacheSet){
            System.out.println("Cache: " + cache);
        }
    }
}
