package aed_lp;

import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.RedBlackBST;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class Gestor {

    private RedBlackBST<String, Utilizador> utilizadores = new RedBlackBST<>();
    private HashMap<Integer,Utilizador> utilizadoresPorId = new HashMap<>();
    private ArrayList<Cache> caches = new ArrayList<>();
    private HashMap<String, List<Cache>> cachesPorRegiao = new HashMap<>();
    private ArrayList<TravelBug> travelBugs = new ArrayList<>();
    private Set<Item> items = new HashSet<Item>();

    private List<CachePath> cachePaths = new ArrayList<>();


    private GestorGrafo gestorGrafo;//classe auxiliar para os grafos
    //private Map<Cache, Integer> graphCacheIndex;
    private EdgeWeightedDigraph graphDistance;
    private EdgeWeightedDigraph graphTime;

    //estruturas para arquivo dos dados
    private ArrayList<Utilizador> utilizadoresRemovidos = new ArrayList<>();


    public EdgeWeightedDigraph getGraphDistance(){
        return graphDistance;
    }

    public EdgeWeightedDigraph getGraphTime(){
        return graphTime;
    }

    /**
     * pesquisa um utilizador por nome
     * @param nome
     * @return o utilizador ou null se nao encontrado
     */
    public Utilizador pesquisaUtilizador(String nome){
        return utilizadores.get(nome);
    }

    /**
     * pesquisa um utilizador por id
     * @param id
     * @return o utilizador ou null se nao encontrado
     */
    public Utilizador pesquisaUtilizador(int id){
        return utilizadoresPorId.get(id);
    }

    /**
     * insere um novo utilizador
     * @param tipo
     * @param nome
     * @return o novo utilizador
     */
    public Utilizador inserirUtilizador(String tipo, String nome){
        Utilizador u = new Utilizador(tipo, nome);
        //guarda na BST e no HashMap
        utilizadoresPorId.put(u.getId(),u);
        utilizadores.put(nome,u);

        return u;
    }

    public Utilizador inserirUtilizador(int id, String tipo, String nome){
        Utilizador u = new Utilizador(id, tipo, nome);
        //guarda na BST e no HashMap
        utilizadoresPorId.put(u.getId(),u);
        utilizadores.put(nome,u);

        return u;
    }

    /**
     * edita os dados do utilizador
     * @param id id do utilizador a editar
     * @param novoTipo
     * @param novoNome
     * @return o utilizador editado
     * @throws Exception se o utilizador nao existir
     */
    public Utilizador editarUtilizador(int id, String novoTipo, String novoNome) throws Exception {
        Utilizador u = pesquisaUtilizador(id);
        if (u==null){
            throw new Exception("Utilizador nao encontrado");
        }

        //se nome diferente é necessario remover e repor na arvore
        boolean nomeDiferente = !novoNome.equals(u.getNome());

        if (nomeDiferente)
            utilizadores.delete(u.getNome());

        u.setTipo(novoTipo);
        u.setNome(novoNome);

        if (nomeDiferente)
            utilizadores.put(novoNome,u);

        return u;
    }


    /**
     * remove um utilizador
     * @param id
     * @return o utilizador removido ou null se o utilizador nao existir
     */
    public Utilizador removerUtilizador(int id) throws Exception {
        Utilizador u = utilizadoresPorId.get(id);

        if (u==null){
            throw new Exception("Utilizador nao encontrado");
        }

        //verificar que pode ser removido
        //verificar que nao é proprietario de nenhuma cache
        for(Cache cache: caches){
            if (cache.getProprietario().equals(u)){
                throw new Exception("Nao pode ser removido pois é proprietario de Caches");
            }
        }

        if (!u.getCachesEscondidas().isEmpty()){
            throw new Exception("Nao pode ser removido pois é já criou caches");
        }

        if (!u.getCachesVisitadas().isEmpty()){
            throw new Exception("Nao pode ser removido pois é já visitou caches");
        }

        utilizadoresPorId.remove(u.getId());
        utilizadores.delete(u.getNome());

        arquivarUtilizador(u);

        return u;
    }




    public Item registarItem(String itemNome, Cache cache, Utilizador quemColocou, LocalDateTime colocado){
        Item item = new Item(itemNome);

        items.add(item);

        cache.addItem(item, quemColocou, colocado);

        quemColocou.getItemsColocados().add(item);
        quemColocou.getCachesVisitadas().add(new Visita(cache,item,null,colocado));
        return item;
    }
    /*
    public Item recolherItem(String itemNome, Cache cache, Utilizador quemRecolheu, LocalDateTime recolhido) throws Exception {

        for(Item item: cache.getItems()){
            if (! (item instanceof TravelBug) && item.getNome().equals(itemNome)){
                cache.removeItem(item, quemRecolheu, recolhido);
                quemRecolheu.getCachesVisitadas().add(new Visita(cache,null, item, recolhido));
                return item;
            }
        }

        throw new Exception("Item nao encontrado");
    }*/

    public Item registarVisita(String itemColocado, String itemRemovido, Cache cache, Utilizador quem, LocalDateTime quando) throws Exception {

        for(Item item: cache.getItems()){
            if (! (item instanceof TravelBug) && item.getNome().equals(itemRemovido)){
                cache.removeItem(item, quem, quando);

                Item novoItem = new Item(itemColocado);
                items.add(novoItem);
                cache.addItem(novoItem, quem, quando);

                quem.getItemsColocados().add(novoItem);
                quem.getCachesVisitadas().add(new Visita(cache,novoItem,item,quando));

                return item;
            }
        }

        throw new Exception("Item nao encontrado");
    }


    public TravelBug registarTravelBug(String itemNome, Cache cache, Utilizador quemColocou, LocalDateTime colocado){
        TravelBug item = new TravelBug(itemNome);

        cache.addItem(item, quemColocou, colocado);

        quemColocou.getItemsColocados().add(item);
        quemColocou.getCachesVisitadas().add(new Visita(cache, item, null, colocado));

        //regista a localizacao inicial
        item.registarLocalizacao(cache.getLocalizacao());

        travelBugs.add(item);

        return item;
    }

    public TravelBug recolherTravelBug(String itemNome, Cache cache, Utilizador quemRecolheu, LocalDateTime recolhido) throws Exception {

        for(Item item: cache.getItems()){
            if (item instanceof TravelBug && item.getNome().equals(itemNome)){
                cache.removeItem(item, quemRecolheu, recolhido);

                TravelBug tb = (TravelBug)item;
                tb.registarRecolha(quemRecolheu, recolhido);

                quemRecolheu.getTravelBugs().add(tb);
                quemRecolheu.getCachesVisitadas().add(new Visita(cache, null, item, recolhido));

                return tb;
            }
        }

        throw new Exception("Item nao encontrado");
    }

    public TravelBug colocarTravelBug(String itemNome, Cache cache, Utilizador quemColocou, LocalDateTime colocado) throws Exception {

        for(TravelBug tb: quemColocou.getTravelBugs()){
            if (tb.getNome().equals(itemNome)){
                //registar a nova localizacao e o fim do transporte anterior
                tb.registarColocacao(cache.getLocalizacao(),quemColocou, colocado);

                //coloca na lista de items desta cache
                cache.addItem(tb, quemColocou, colocado);

                //remove da lista de travelbugs deste utilizador
                quemColocou.getTravelBugs().remove(tb);

                return tb;
            }
        }

        throw new Exception("TravelBug nao encontrado no utilizador");
    }

    /////////////////////////////////////
    private void arquivarUtilizador(Utilizador u){
        utilizadoresRemovidos.add(u);
    }




    //estruturas para arquivo dos dados
    private ArrayList<Cache> cachesRemovidss = new ArrayList<>();

    /**
     * pesquisa um cache por id
     * @param nome
     * @return a cache ou null se nao encontrada
     */
    public Cache pesquisaCache(String nome){
        for(Cache c: caches){
            if (c.getNome().equals(nome))
                return c;
        }

        return null;
    }

    public List<Cache> pesquisaCachesPorRegiao(String regiao){
        return new ArrayList(cachesPorRegiao.get(regiao));
    }

    public Cache inserirCache(String nome, Utilizador proprietario, Coordenada localizacao, String regiao, LocalDateTime criacao, int dificuldade){
        Cache c = new Cache(nome, proprietario, localizacao, regiao, criacao, dificuldade);
        //guarda na lista e no HashMap por regiao
        List<Cache> l;
        if (cachesPorRegiao.containsKey(regiao)) {
            l = cachesPorRegiao.get(regiao);
        }else{
            l = new ArrayList<>();
            cachesPorRegiao.put(c.getRegiao(),l);
        }
        l.add(c);

        caches.add(c);

        //registar a cache no proprietario
        proprietario.getCachesEscondidas().add(c);

        return c;
    }

    /**
     *
     * @param nome
     * @param proprietario
     * @param localizacao
     * @param regiao
     * @param criacao
     * @param dificuldade
     * @return
     * @throws Exception
     */
    public Cache editarCache(String nome, Utilizador proprietario, Coordenada localizacao, String regiao, LocalDateTime criacao, int dificuldade) throws Exception {
        Cache cache = pesquisaCache(nome);
        if (cache==null)
            throw new Exception("A Cache nao existe");

        //mover a cache parfa a nova regiao caso tenha mudado
        String regiaoAtual = cache.getRegiao();

        if (!regiaoAtual.equals(regiao)){
            cachesPorRegiao.get(regiaoAtual).remove(cache);
            List<Cache> l;
            if (cachesPorRegiao.containsKey(regiao)) {
                l = cachesPorRegiao.get(regiao);
            }else{
                l = new ArrayList<>();
                cachesPorRegiao.put(regiao,l);
            }
            l.add(cache);
        }

        cache.setProprietario(proprietario);
        cache.setLocalizacao(localizacao);
        cache.setRegiao(regiao);
        cache.setCriacao(criacao);
        cache.setDificuldade(dificuldade);

        return cache;
    }


    /**
     * remove um utilizador
     * @param nome
     * @return o utilizador removido ou null se o utilizador nao existir
     */
    public Cache removerCache(String nome) throws Exception {
        Cache cache = pesquisaCache(nome);
        if (cache==null)
            throw new Exception("A Cache nao existe");

        //verificar que pode ser removida
        //verificar se possui items, se possuir nao pode ser removida
        if (cache.getItems().size()>0){
            throw new Exception("Nao pode ser removido pois é possui items");
        }

        //remover da lista
        caches.remove(cache);

        //remover da cache por regiao
        cachesPorRegiao.get(cache.getRegiao()).remove(cache);

        //remover das caches escondidas do proprietario
        cache.getProprietario().getCachesEscondidas().remove(cache);

        //arquivar
        arquivarCache(cache);


        return cache;
    }

    private void arquivarCache(Cache cache) {
        cachesRemovidss.add(cache);
    }




    //////////////////////////////////////////////////////////////
    // pesquisas


    //a Todas as caches visitadas por um utilizador. Global e por região;

    List<Cache> cachesVisitadas(Utilizador utilizador) throws Exception {
        List<Cache> cv = new ArrayList<>();
        for(Visita v: utilizador.getCachesVisitadas()){
            cv.add(v.getCache());
        }

        return cv;
    }

    List<Cache> cachesVisitadas(Utilizador utilizador, String regiao) throws Exception {
        List<Cache> cv = new ArrayList<>();
        for(Visita v: utilizador.getCachesVisitadas()){
            if (v.getCache().getRegiao().equals(regiao))
                cv.add(v.getCache());
        }

        return cv;
    }

    //b Todas as caches não visitadas por um utilizador. Global e por região;
    List<Cache> cachesNaoVisitadas(Utilizador utilizador) throws Exception {
        List<Cache> cnv = (List<Cache>) caches.clone();

        for(Visita v: utilizador.getCachesVisitadas()){
            cnv.remove(v.getCache());
        }

        return cnv;
    }

    List<Cache> cachesNaoVisitadas(Utilizador utilizador, String regiao) throws Exception {

        List<Cache> cnv = new ArrayList<>();
        for(Cache c: caches){
            if (c.getRegiao().equals(regiao))
                cnv.add(c);
        }
        for(Visita v: utilizador.getCachesVisitadas()){
            if (v.getCache().getRegiao().equals(regiao))
                cnv.add(v.getCache());
        }

        return cnv;
    }

    //c) Todos os utilizadores que já visitaram uma dada cache;
    List<Utilizador> visitantesCache(Cache cache) throws Exception {
        List<Utilizador> visitantes = new ArrayList<>();
        for(String name: utilizadores.keys()) {
            Utilizador utilizador = utilizadores.get(name);
            for (Visita v : utilizador.getCachesVisitadas()) {
                if (v.getCache().equals(cache)) {
                    visitantes.add(utilizador);
                    break;
                }
            }
        }

        return visitantes;
    }

    //d) Todas as caches premium que têm pelo menos um objecto;

    /////////////

    List<Cache> cachePremiumUmObjeto(Cache cache) throws Exception {

        List<Cache> cachesUmObjeto = new ArrayList<>();
        for(Cache c: caches){
            if (c.getDificuldade()==Cache.PREMIUM){
                if (!c.getItems().isEmpty()) {
                    cachesUmObjeto.add(cache);
                }
            }
        }

        return caches;
    }



    /////////////




    //f


    public TravelBug maiorNumeroLocalizacoes() throws Exception {
        int maiorNumLocalizacoes = -1;
        TravelBug maior = null;
        for(TravelBug tb: travelBugs){
            if (tb.getNumeroLocalizacoes() > maiorNumLocalizacoes){
                maiorNumLocalizacoes = tb.getNumeroLocalizacoes();
                maior = tb;
            }
        }

        return maior;
    }

    //Top-5 de utilizadores que visitaram maior no de caches num dado período
    //temporal;
    public List<Utilizador> top5MaiorNumeroCachesVisitadas(LocalDate inicio, LocalDate fim){
        List<Utilizador> top5 = new ArrayList<>();
        List<Integer> top5num = new ArrayList<>();

        for(String k: utilizadores.keys()){
            Utilizador u = utilizadores.get(k);

            int num = u.getCachesVisitadasPorPeriodo(inicio, fim);

            //determinar em que ponto do top este utilizador fica
            int i;
            for(i=0;i<top5.size();i++){
                if (num>top5num.get(i)){
                    break;
                }
            }
            //se chegou ao fim da lista entao nao é maior e por isso vai para o fim da lista
            if (i==top5.size()){
                if (i<5){
                    top5.add(u);
                    top5num.add(num);
                }
            }else{
                top5.add(i,u);
                top5num.add(i,num);
                if (top5.size()>5){
                    top5.remove(5);
                    top5num.remove(5);
                }
            }
        }

        return top5;
    }

    public void addCachePath(Cache cache1, Cache cache2, float distance, int time){
        cachePaths.add(new CachePath(cache1, cache2, distance, time));
    }


    public void loadFromFile(String filename){
        GestorFicheiros gf = new GestorFicheiros(filename);

        utilizadores = new RedBlackBST<>();
        utilizadoresPorId = new HashMap<>();
        caches = new ArrayList<>();
        cachesPorRegiao = new HashMap<>();
        travelBugs = new ArrayList<>();

        gf.load(this);

    }

    public void writeToFile(String filename){
        GestorFicheiros gf = new GestorFicheiros(filename);
        gf.write(this);
    }

    public void buildGrafos(){
        //R12
        gestorGrafo = new GestorGrafo(this);
        //graphCacheIndex = gestorGrafo.buildCacheIndex(caches);
        graphDistance = gestorGrafo.buildGraphDistance(caches, cachePaths);
        graphTime = gestorGrafo.buildGraphTime(caches, cachePaths);
    }


    public TravelBug pesquisaTravelBug(String nome) {
        for(TravelBug tb: travelBugs){
            if (tb.getNome().equals(nome)){
                return tb;
            }
        }
        return null;
    }

    public HashMap<Integer, Utilizador> getUtilizadoresPorId() {
        return utilizadoresPorId;
    }

    public Set<Item> getItems() {
        return items;
    }

    public ArrayList<TravelBug> getTravelBugs() {
        return travelBugs;
    }

    public ArrayList<Cache> getCaches() {
        return caches;
    }

    public void now() {
        for(TravelBug bug: travelBugs){
            System.out.println("Travel Bug: " + bug.getNome() + " (" + bug.getId()+")");
            if (bug.emTransporte()){
                System.out.println("    em transporte pelo utilizador: " + bug.getUltimoUtilizador().getNome());
                System.out.println("    Levantado em " + bug.getUltimoTransporte().getInicio());
            }else if (bug.getUltimoTransporte()!=null){
                System.out.println("    em " + bug.getLocalizacaoAtual() + ", colocado por " + bug.getUltimoUtilizador().getNome());
                System.out.println("    Colocado em " + bug.getUltimoTransporte().getFim());
            }else{
                System.out.println("    em " + bug.getLocalizacaoAtual() + " e ainda nao foi transportado nenhuma vez");
            }
            System.out.println("    Caminhos percorridos:");
            ArrayList<Transporte> transportes = bug.getHistoricoTransporte();
            if (transportes.size()==0){
                System.out.println("        nao foi transportado...");
            }else{
                ArrayList<Coordenada> localizacoes = bug.getHistoricoLocalizacoes();
                //22,33 (2021-01-01) -> 44.55 (2021-02-01) por Antonio
                for (int i=0;i<transportes.size();i++) {
                    String caminho = "        " + localizacoes.get(i) + " (" + transportes.get(i).getInicio() + ") --> ";
                    if (transportes.get(i).getFim() != null){
                        caminho+= localizacoes.get(i + 1) + " (" + transportes.get(i).getFim() + ") ";
                    }else{
                        caminho+=" em transporte ...";
                    }
                    caminho+=" por " + transportes.get(i).getUtilizador().getNome();
                    System.out.println(caminho);
                }
            }
        }
    }

    //grafos

    //R13. Deve ser possível criar um conjunto (set) de vértices manual ou baseado em atributos
    //(e.g. zona, dificuldade, número de visitantes, etc); Os conjuntos criados terão como objetivo
    //definir vértices/nós a evitar/preferir para o cálculo de caminhos.

    /**
     * constroi um set com todas as caches de uma regiao
     * @param regiao
     * @return
     */
    public CacheSet buildRegiaoCacheSet(String regiao){
        CacheSet set = new CacheSet();

        List<Cache> caches = cachesPorRegiao.get(regiao);

        if (caches!=null){
            set.addAll(caches);
        }

        return set;
    }

    public CacheSet buildDificuldadeCacheSet(int dificuldade){
        CacheSet set = new CacheSet();

        for (Cache cache:caches) {
            if (cache.getDificuldade()==dificuldade)
                set.add(cache);
        }

        return set;
    }

    public Map<Cache,Integer> getVisitasPorCache(){
        Map<Cache,Integer> visitas = new HashMap<>();

        for(String u: utilizadores.keys()){
            for(Visita v: utilizadores.get(u).getCachesVisitadas()){
                int n=0;
                if (visitas.containsKey(v.getCache())){
                    n = visitas.get(v.getCache());
                }
                visitas.put(v.getCache(),n+1);
            }
        }

        return visitas;
    }


    /**
     *
     * @param numeroVisitantes numero minimo de visitantes
     * @return
     */
    public CacheSet buildMostVisitedCacheSet(int numeroVisitantes){
        CacheSet set = new CacheSet();

        Map<Cache, Integer> visitasPorCache = getVisitasPorCache();

        for (Cache cache:caches) {
            //se a cache foi visitada pelo menos n vezes
            if (visitasPorCache.containsKey(cache) && visitasPorCache.get(cache) >= numeroVisitantes )
                set.add(cache);
        }

        return set;
    }

    /**
     * usado para construi um conjunto de caches que sao menos frequentadas
     * @param numeroVisitantes numero maximo de visitantes
     * @return
     */
    public CacheSet buildLeastVisitedCacheSet(int numeroVisitantes){
        CacheSet set = new CacheSet();

        Map<Cache, Integer> visitasPorCache = getVisitasPorCache();

        for (Cache cache:caches) {
            //se a cache foi visitada no maximo n vezes
            if (!visitasPorCache.containsKey(cache) || visitasPorCache.get(cache) <= numeroVisitantes )
                set.add(cache);
        }

        return set;
    }
/*
    O modelo de dados deve prever a utilização de algoritmos genéricos de gestão e verificação de grafos,
    nomeadamente:
    a) Algoritmos de cálculo do caminho de menor custo entre duas caches; O custo a
    considerar deverá ter como base um ou mais dos pesos considerados em R12 (e.g. caminho mais curto,
    caminho mais rápido, caminho mais simples, etc); Deverá também ser considerada a opção para
    efetuar as mesmas pesquisas evitando um determinado conjunto de pontos (como definido em R13);
*/
    //R14a
    List<CachePath> caminhoMaisCurto(Cache origem, Cache destino){
        return gestorGrafo.caminhoMenorCusto(graphDistance, caches, origem, destino);
    }
    //R14a
    List<CachePath> caminhoMaisRapido(Cache origem, Cache destino){
        return gestorGrafo.caminhoMenorCusto(graphTime, caches, origem, destino);
    }

    //R14a set de caches a evitar
    List<CachePath> caminhoMaisCurto(Cache origem, Cache destino, CacheSet cachesToAvoid){
        return gestorGrafo.caminhoMenorCusto(graphDistance, caches, origem, destino, cachesToAvoid);
    }

    //R14b subgrafos
    EdgeWeightedDigraph subGrafoDistanceCachesPorTipo(int tipo, List<Cache> cachesFiltradas){
        //obter uma lista de caches premium
        for(Cache cache: caches){
            if (cache.getDificuldade()==tipo)
                cachesFiltradas.add(cache);
        }

        return gestorGrafo.buildSubgrafo(graphDistance,caches,cachesFiltradas);
    }

    EdgeWeightedDigraph subGrafoDistanceCachesPorRegiao(String regiao, List<Cache> cachesFiltradas){
        //obter uma lista de caches premium
        for(Cache cache: caches){
            if (cache.getRegiao().equalsIgnoreCase(regiao))
                cachesFiltradas.add(cache);
        }

        return gestorGrafo.buildSubgrafo(graphDistance,caches,cachesFiltradas);
    }

    //R14b subgrafos
    EdgeWeightedDigraph subGrafoTimeCaches(int tipo, List<Cache> cachesFiltradas) {
        //obter uma lista de caches premium
        for(Cache cache: caches){
            if (cache.getDificuldade()==tipo)
                cachesFiltradas.add(cache);
        }

        return gestorGrafo.buildSubgrafo(graphTime,caches,cachesFiltradas);
    }



    //R14b usar os mesmos algortimos

    /**
     * permite calcular o caminho de menor custo usando um subgrafo
     * obtido previamente com subGrafoTimeCachesPremium ou subGrafoDistanceCachesPremium
     * @param subgrafo
     * @param origem
     * @param destino
     * @return
     */
    List<CachePath> caminhoMenorCusto(EdgeWeightedDigraph subgrafo, List<Cache> cachesSubGrafo, Cache origem, Cache destino){
        return gestorGrafo.caminhoMenorCusto(subgrafo, cachesSubGrafo, origem, destino);
    }

    //R14b usar os mesmos algortimos - set de caches a evitar

    /**
     * permite calcular o caminho de menor custo usando um subgrafo e evitando certos caches
     * obtido previamente com subGrafoTimeCachesPremium ou subGrafoDistanceCachesPremium
     * @param subgrafo
     * @param origem
     * @param destino
     * @param cachesToAvoid
     * @return
     */
    List<CachePath> caminhoMenorCusto(EdgeWeightedDigraph subgrafo, List<Cache> cachesSubGrafo, Cache origem, Cache destino, CacheSet cachesToAvoid){
        return gestorGrafo.caminhoMenorCusto(subgrafo, cachesSubGrafo, origem, destino, cachesToAvoid);
    }


    public List<CachePath> caminhoMaiorVisitas(Cache cache, int maxTime) {
        return gestorGrafo.caminhoMaiorVisitas(graphTime, caches, cache, maxTime);
    }
}

