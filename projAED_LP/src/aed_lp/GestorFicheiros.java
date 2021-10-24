package aed_lp;

import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class GestorFicheiros {

    private final String filename;

    public GestorFicheiros(String filename){
        this.filename=filename;
    }


    public void load(Gestor gestor) {

        In in = new In(new File(filename)); // abertura do ficheiro/stream de entrada
        /* utilizadores

            6
            1, Manuel, basic
            2, Pedro, basic
            ....
        */

        Utilizador userDefault=null;

        int num = in.readInt(); in.readLine();
        for (int i=0;i<num;i++){
            String[] texto = in.readLine().split(",");
            int id = Integer.parseInt(texto[0].trim());
            String nome = texto[1].trim();
            String tipo = texto[2].trim();
            userDefault = gestor.inserirUtilizador(id, tipo, nome);
        }

        /*  regioes, caches e items
            3
            norte, 7
            geocache1, basic, 41.1720859, -8.6148178, 3, canudo, livro, oculos
            geocache2, basic, 41.1605747, -8.5855818, 3, bola, brinquedo, camisola
            ....
            geocache7, basic, 41.2421226, -8.6807401, 1, bilhetes_aviao
            centro, 5
            geocache8, basic, 40.2094433, -8.4208601, 2, canudo, capa
            geocache9, basic, 40.2027321, -8.4333746, 0
           */
        int numRegioes = in.readInt(); in.readLine();
        for (int i=0;i<numRegioes;i++){
            String[] texto = in.readLine().split(",");
            String regiao = texto[0].trim();
            int numCaches = Integer.parseInt(texto[1].trim());

            for (int c=0;c<numCaches;c++){
                texto = in.readLine().split(",");
                String nome = texto[0].trim();
                int dificuldade=Cache.BASIC;
                switch (texto[1].trim()){
                    case "basic":
                        dificuldade= Cache.BASIC;
                        break;
                    case "premium":
                        dificuldade= Cache.PREMIUM;
                }
                Coordenada coords = new Coordenada(Double.parseDouble(texto[2].trim()),
                                                    Double.parseDouble(texto[3].trim()));
                Cache cache = gestor.inserirCache(nome,userDefault,coords,regiao, LocalDateTime.now(), dificuldade);
                int numItems = Integer.parseInt(texto[4].trim());
                for(int n=0;n<numItems;n++){
                    gestor.registarItem(texto[5+n].trim(),cache, userDefault, LocalDateTime.now());
                }
            }
        }


        /*
            travel bugs e historico de localizacao
            3
            travelbug1, Maria, geocache1, geocache15
            travelbug2, Pedro, geocache2, geocache17
            travelbug3, Filomena, geocache3, geocache12

            o travel bug pode repetir-se para ter outros transportes ????

         */

        num = in.readInt(); in.readLine();
        for (int i=0;i<num;i++){
            String[] texto = in.readLine().split(",");
            String tbug = texto[0].trim();
            Utilizador user = gestor.pesquisaUtilizador(texto[1].trim());
            Cache origem = gestor.pesquisaCache(texto[2].trim());
            Cache destino = gestor.pesquisaCache(texto[3].trim());

            TravelBug travelBug = gestor.pesquisaTravelBug(texto[0].trim());
            if (travelBug==null){
                travelBug = gestor.registarTravelBug(tbug, origem, user, LocalDateTime.now());
            }
            //registar o transporte
            try {
                travelBug.registarRecolha(user, LocalDateTime.now());
                travelBug.registarColocacao(destino.getLocalizacao(), user, LocalDateTime.now());
            } catch (Exception e) {
                e.printStackTrace();
            }
            //ver se o travel bug ja existe e criar se nao existir
        }

        //carregar os grafos globais
        //dois grafos com peso distancia e tempo


        /*
            ligacoes entre caches
            50
            geocache1, geocache2, 5.2, 60
            geocache1, geocache3, 8.2, 102
            geocache1, geocache4, 6.2, 70
            */
        //ler o numero de edges
        num = in.readInt(); in.readLine();
        for (int i=0;i<num;i++) {
            String[] texto = in.readLine().split(",");
            Cache cache1 = gestor.pesquisaCache(texto[0].trim());
            Cache cache2 = gestor.pesquisaCache(texto[1].trim());
            float distance = Float.parseFloat(texto[2].trim());
            int time = Integer.parseInt(texto[3].trim());
            gestor.addCachePath(cache1, cache2, distance, time);
        }




    }


    public void write(Gestor gestor){
       /* colocado por esta ordem
        utilizadores id, tipo, nome
        items id nome
        travelbugs id,nome, localizacoes, transporte

        cache nome, id proprietario, localizacao,regiao,criacao,difucldade
        items(id),
        hisotrico items
        item(id),  quemColocou quemRemoveu colocado removido;

        utilizadores_items_colocados(id)
        utilizadores_tbugs(id)
        cachesvisitadas   cache(nome) itemColocado itemRemovido datahora;
        cachesescondidas nomes
       */
        Out out = new Out(filename); // abertura do ficheiro/stream de saida

        //utilizadores
        HashMap<Integer, Utilizador> util = gestor.getUtilizadoresPorId();
        out.println(util.size());                                       //numero de utilizadores
        for(Utilizador u: util.values()){
            out.println(u.getId()+","+u.getTipo()+","+u.getNome());     //id,tipo,nome
        }

        //items
        //colecionar todos os items, estao em varias colecoes : cache, historico user,
        Set<Item> items = gestor.getItems();
        out.println(items.size());                                      //numero de items
        for(Item item : items){
            out.println(item.getId()+","+item.getNome());               //id,nome
        }

        //travelbugs
        ArrayList<TravelBug> tbugs = gestor.getTravelBugs();
        out.println(tbugs.size());                                      //numero de travelbugs
        for(TravelBug tbug: tbugs){                                             //2 localizacoes == 4 valores double
            String localizacoes=""+tbug.getHistoricoLocalizacoes().size();     //numero de localizcoes
            for(Coordenada l: tbug.getHistoricoLocalizacoes())
                localizacoes+=","+l.getLatitude()+","+l.getLongitude();

            String transportes=""+tbug.getHistoricoTransporte().size();
            for(Transporte t: tbug.getHistoricoTransporte())
                transportes+=","+t.getUtilizador().getId()+","+t.getInicio()+","+t.getFim();

            out.println(tbug.getId()+","+tbug.getNome()+","+localizacoes+transportes);               //id,nome,2,33.44,44.55,3,2,2021-01-01 10:00:00,2021-01-01 10:00:00,....
        }

        //caches
        ArrayList<Cache> caches = gestor.getCaches();
        out.println(caches.size());                                     //numero de caches
        for(Cache cache: caches){
            out.println(cache.getNome()+","+cache.getProprietario().getId()
                    +","+cache.getLocalizacao().getLatitude()+","+cache.getLocalizacao().getLongitude()
                    +","+cache.getRegiao()+","+cache.getCriacao()+","+cache.getDificuldade());
            //numero de items que possui seguido dos ids numa unica linha
            String cacheitems=""+cache.getItems().size();
            for(Item item: cache.getItems())
                cacheitems+=","+item.getId();
            out.println(cacheitems);
            //numero de items no historico seguido da informacao do historico
            String histitems=""+cache.getHistoricoItems().size();
            for(HistoricoItem h: cache.getHistoricoItems())
                histitems+=","+h.getItem().getId()
                          +","+h.getQuemColocou().getId()
                          +","+(h.getQuemRemoveu()==null ? "null" : h.getQuemRemoveu().getId())
                          +","+h.getColocado()+","+h.getRemovido();
            out.println(histitems);
        }

        //utilizadores_items_colocados(id)
        out.println(util.size());                                       //numero de utilizadores
        for(Utilizador u: util.values()){
            String itemscol = u.getId() + "," + u.getItemsColocados().size();
            for(Item i: u.getItemsColocados())
                itemscol+=","+i.getId();
            out.println(itemscol);
        }

        //utilizadores_travelbugs
        out.println(util.size());                                       //numero de utilizadores
        for(Utilizador u: util.values()){
            String tbs = u.getId() + "," + u.getTravelBugs().size();
            for(TravelBug i: u.getTravelBugs())
                tbs+=","+i.getId();
            out.println(tbs);
        }

        //cachesvisitadas   cache(nome) itemColocado itemRemovido datahora;
        out.println(util.size());                                       //numero de utilizadores
        for(Utilizador u: util.values()){
            String cv = u.getId() + "," + u.getCachesVisitadas().size();
            for(Visita v: u.getCachesVisitadas())
                cv+=","+v.getCache().getNome()
                        +","+(v.getItemColocado()==null?null:v.getItemColocado().getId())
                        +","+(v.getItemRemovido()==null?null:v.getItemRemovido().getId())
                        +","+v.getDatahora();
            out.println(cv);
        }

        //cachesescondidas nomes
        out.println(util.size());                                       //numero de utilizadores
        for(Utilizador u: util.values()){
            String ce = u.getId() + "," + u.getCachesEscondidas().size();
            for(Cache c: u.getCachesEscondidas())
                ce+=","+c.getNome();
            out.println(ce);
        }

        out.close();

    }

}
