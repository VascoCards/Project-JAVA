package aed_lp;

import edu.princeton.cs.algs4.DijkstraSP;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestorGrafo {
    private final Gestor gestor;

    public GestorGrafo(Gestor gestor)
    {
        this.gestor=gestor;
    }

    public Map<Cache,Integer> buildCacheIndex(List<Cache> caches){

        Map<Cache,Integer> cacheIndex=new HashMap<>();
        for(int i =0;i<caches.size();i++){
            cacheIndex.put(caches.get(i),i);
        }

        return cacheIndex;
    }


    public EdgeWeightedDigraph buildGraphDistance(List<Cache> caches, List<CachePath> paths){
        EdgeWeightedDigraph graph = new EdgeWeightedDigraph(caches.size());

        Map<Cache, Integer> index = buildCacheIndex(caches);

        for(CachePath path: paths){
            graph.addEdge(new DirectedEdge(index.get(path.getCache1()), index.get(path.getCache2()), path.getDistance()));
        }
        return graph;
    }

    public EdgeWeightedDigraph buildGraphTime(List<Cache> caches, List<CachePath> paths){
        EdgeWeightedDigraph graph = new EdgeWeightedDigraph(caches.size());

        Map<Cache, Integer> index = buildCacheIndex(caches);

        for(CachePath path: paths){
            graph.addEdge(new DirectedEdge(index.get(path.getCache1()), index.get(path.getCache2()), path.getTime()));
        }
        return graph;
    }

    /**
     *  calcula o caminho com menos custo entre dois vertices
     *
     * devolve uma lista de CachePath, apenas o atributo distance é preenchido no CachePath
     * mesmo que o grafo seja baseado no tempo
     *
     * @param graph
     * @param caches
     * @param origem
     * @param destino
     * @return
     */
    public List<CachePath> caminhoMenorCusto(
            EdgeWeightedDigraph graph,
            List<Cache> caches,
            Cache origem, Cache destino) {

        List<CachePath> list = new ArrayList<>();
        //criar um index das caches para facilitar a pesquisa do indice a colocar no grafo
        Map<Cache, Integer> graphCacheIndex = buildCacheIndex(caches);
        Integer iOrigem=graphCacheIndex.get(origem);
        Integer iDestino=graphCacheIndex.get(destino);
        if (iOrigem==null || iDestino==null)
            return list;

        //usando o metodo dijkstra
        DijkstraSP sp = new DijkstraSP(graph, iOrigem);
        if (sp.hasPathTo(iDestino)){
            Iterable<DirectedEdge> path = sp.pathTo(iDestino);
            //percorre os ramos
            for (DirectedEdge edge : path) {
                list.add(new CachePath(caches.get(edge.from()), caches.get(edge.to()), edge.weight(),-1));
            }
        }

        return list;
    }


    public List<CachePath> caminhoMenorCusto(
            EdgeWeightedDigraph graph,
            List<Cache> caches,
            Cache origem, Cache destino,
            CacheSet cachesToAvoid) {

        if (cachesToAvoid.contains(origem) || cachesToAvoid.contains(destino)){
            throw new IllegalArgumentException("A Origem ou o Destino nao podem fazer parte do conjunto de caches a evitar");
        }

        //construi o subgrafo apenas com as caches SEM as caches a evitar
        List<Cache> subGraphCaches = new ArrayList<>(caches);
        subGraphCaches.removeAll(cachesToAvoid);
        EdgeWeightedDigraph subgraph = buildSubgrafo(graph, caches,subGraphCaches);

        return caminhoMenorCusto(subgraph, subGraphCaches, origem, destino);

    }

    /**
     * constroi um subgrafo baseado no grafo indicado
     * mas apenas com as caches indicadas em subgraphCaches
     * @param graph
     * @param caches
     * @param subGraphCaches
     * @return
     */
    public EdgeWeightedDigraph buildSubgrafo(
            EdgeWeightedDigraph graph,
            List<Cache> caches,
            List<Cache> subGraphCaches
    ) {

        //criar um novo grafo
        //com as caches indicadas
        //index para todas as caches do grafo
        //Map<Cache, Integer> graphCacheIndex = buildCacheIndex(caches);
        //e para as caches do subgrafo
        Map<Cache, Integer> subGraphCacheIndex = buildCacheIndex(subGraphCaches);

        //criar novo grafo em que no numero de vertices é o size do subGraphCaches
        EdgeWeightedDigraph subgraph = new EdgeWeightedDigraph(subGraphCaches.size());

        for (DirectedEdge edge: graph.edges()){
            Cache from = caches.get(edge.from());
            Cache to = caches.get(edge.to());

            //se ambas existem no subgrafo..
            if (subGraphCaches.contains(from) && subGraphCaches.contains(to)){
                //obter o
                //        return graph;s indices no subgrafo
                int iFrom = subGraphCacheIndex.get(from);
                int iTo = subGraphCacheIndex.get(to);
                subgraph.addEdge(new DirectedEdge(iFrom,iTo, edge.weight()));
            }
        }
        return subgraph;
    }

////////
    ////////
    ///////
    public List<CachePath> caminhoMaiorVisitas(EdgeWeightedDigraph graph, ArrayList<Cache> caches, Cache cache, int maxTime) {

        List<Integer> caminho = new ArrayList<>();
        List<Integer> caminhoMaiorVisitas = new ArrayList<>();

        //como os vertices do digraph sao indices
        Map<Cache, Integer> index = buildCacheIndex(caches);

        //obter o indice da cache de partida
        int from = index.get(cache);

        caminhoMaiorVisitas(graph, from, maxTime, caminho, 0, caminhoMaiorVisitas);


        List<CachePath> cmv = new ArrayList<>();
        //necessario converter os indices para a list de cachePath
        int to;
        for (int i=1;i<caminhoMaiorVisitas.size();i++) {
            to=caminhoMaiorVisitas.get(i);

            //procurar o edge que leva de from a to
            for(DirectedEdge edge : graph.adj(from)){
                if (edge.to()==to){
                    CachePath cp = new CachePath(caches.get(from), caches.get(to), 0, (int)edge.weight());
                    cmv.add(cp);
                    break;
                }
            }

            from=to;
        }

        return cmv;
    }

    //metodo recursivo
    private void caminhoMaiorVisitas(EdgeWeightedDigraph graph, int cache, int maxTime,
                                     List<Integer> caminho, int tempoCaminho,
                                     List<Integer> caminhoMaiorVisitas) {

        //vai percorrendo o grafo
        //o caminho vai sendo construido, quando chegar a um ponto em que nao pode avancar
        //mais ou o tempo foi excedido compara o caminho com o maior ate ao momento e substitui se necessário
        //aplica um algoritmo de força bruta tentando percorrer todos os caminhos possiveis desde que no tempo maxiumo

        caminho.add(cache);
        Iterable<DirectedEdge> outEdges = graph.adj(cache);
        for (DirectedEdge edge: outEdges) {
            int toCache = edge.to();

            //se ainda nao se visitou esta cache
            if (!caminho.contains(toCache)){
                //se o caminho para toCache nao exceder o tempo maximo
                if (tempoCaminho+edge.weight()<=maxTime){
                    //continua para esta cache
                    caminhoMaiorVisitas(graph, toCache, maxTime, caminho,
                                        (int)(tempoCaminho+edge.weight()), caminhoMaiorVisitas);
                }
            }
        }
        //determinar se este caminho visita mais caches
        if (caminho.size()>caminhoMaiorVisitas.size()){
            //substitui o caminhoMaior modificando o conteudo da propria lista
            caminhoMaiorVisitas.clear();
            caminhoMaiorVisitas.addAll(caminho);
        }

        for (Integer integer : caminho) {
            System.out.print(integer+" ");
        }
        System.out.println();

        //ao voltar para tras remove-se esta cache que é a ultima
        caminho.remove(caminho.size()-1);
    }
}
