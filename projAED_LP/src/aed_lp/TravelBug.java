package aed_lp;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class TravelBug extends Item{
    private ArrayList<Coordenada> historicoLocalizacoes;
    private ArrayList<Transporte> historicoTransporte;

    public TravelBug(String nome) {
        super(nome);
        this.historicoLocalizacoes = new ArrayList<>();
        this.historicoTransporte = new ArrayList<>();
    }

    public TravelBug(int id, String nome) {
        super(id, nome);
        this.historicoLocalizacoes = new ArrayList<>();
        this.historicoTransporte = new ArrayList<>();
    }

    public ArrayList<Coordenada> getHistoricoLocalizacoes() {
        return historicoLocalizacoes;
    }

    public ArrayList<Transporte> getHistoricoTransporte() {
        return historicoTransporte;
    }

    public Coordenada getLocalizacaoAtual(){
        if (historicoLocalizacoes.size()>0)
            return historicoLocalizacoes.get(historicoLocalizacoes.size()-1);
        else
            return null;
    }

    public boolean emTransporte(){
        //esta em trasnsporte se o ultimo transporte nao possuir fim
        if (historicoTransporte.size()==0)
            return false;

        return historicoTransporte.get(historicoTransporte.size()-1).getFim()==null;
        
    }

    public Transporte getUltimoTransporte() {
        if (historicoTransporte.size()>0)
            return historicoTransporte.get(historicoTransporte.size()-1);
        else
            return null;
    }

    public Utilizador getUltimoUtilizador(){
        if (historicoTransporte.size()>0)
            return historicoTransporte.get(historicoTransporte.size()-1).getUtilizador();
        else
            return null;
    }

    @Override
    public String toString() {
        return "TravelBug: " + super.toString();
    }

    public void registarLocalizacao(Coordenada localizacao) {
        historicoLocalizacoes.add(localizacao);
    }

    public void registarRecolha(Utilizador quemRecolheu, LocalDateTime recolhido) throws Exception {
        //se houver um transporte pendente nao é possivel registar a recolha
        if (!historicoTransporte.isEmpty()){
            if (historicoTransporte.get(historicoTransporte.size()-1).getFim()==null){
                throw new Exception("Este TravelBug esta em transporte");
            }
        }

        Transporte t = new Transporte(quemRecolheu, recolhido);
        historicoTransporte.add(t);

    }

    public void registarColocacao(Coordenada localizacao, Utilizador quemColocou, LocalDateTime colocado) throws Exception {
        if (historicoTransporte.isEmpty()){
            throw new Exception("Este travelBug nao esta em transporte");
        }
        if (historicoTransporte.get(historicoTransporte.size()-1).getFim()!=null){
            throw new Exception("Este travelBug nao esta em transporte");
        }
        if (!historicoTransporte.get(historicoTransporte.size()-1).getUtilizador().equals(quemColocou)){
            throw new Exception("Este travelBug nao foi recolhido por este utilizador");
        }

        historicoTransporte.get(historicoTransporte.size()-1).setFim(colocado);

        registarLocalizacao(localizacao);

    }

    public int getNumeroLocalizacoes(){
        return historicoLocalizacoes.size();
    }


}



