package aed_lp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Cache {
    public static final int BASIC=1;
    public static final int PREMIUM=2;

    private String nome;
    private Utilizador proprietario;
    private Coordenada localizacao;
    private String regiao;
    private LocalDateTime criacao;
    private int dificuldade;
    private ArrayList<Item> items;
    private ArrayList<HistoricoItem> historicoItems;

    public Cache(String nome, Utilizador proprietario, Coordenada localizacao, String regiao, LocalDateTime criacao, int dificuldade) {
        this.nome=nome;
        this.proprietario = proprietario;
        this.localizacao = localizacao;
        this.regiao = regiao;
        this.criacao = criacao;
        this.dificuldade = dificuldade;
        items = new ArrayList<>();
        historicoItems = new ArrayList<>();
    }

    /**
     * adiciona um item à cache
     * @param item
     * @param quemColocou
     * @param colocado
     */
    public void addItem(Item item, Utilizador quemColocou, LocalDateTime colocado){
        items.add(item);
        HistoricoItem hist = new HistoricoItem(item, quemColocou, colocado);
        historicoItems.add(hist);
    }

    public void removeItem(Item item, Utilizador quemRemoveu, LocalDateTime removido) throws Exception {
        if (!items.contains(item))
            throw new Exception("O Item nao existe nesta cache");

        items.remove(item);

        //atualizar o historico deste item
        for(HistoricoItem hist : historicoItems){
            if (hist.getItem().equals(item)){
                hist.setRemovido(removido);
                hist.setQuemRemoveu(quemRemoveu);
            }
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome){
        this.nome=nome;
    }

    public Utilizador getProprietario() {
        return proprietario;
    }

    public void setProprietario(Utilizador proprietario) {
        this.proprietario = proprietario;
    }

    public Coordenada getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Coordenada localizacao) {
        this.localizacao = localizacao;
    }

    public String getRegiao() {
        return regiao;
    }

    public void setRegiao(String regiao) {
        this.regiao = regiao;
    }

    public LocalDateTime getCriacao() {
        return criacao;
    }

    public void setCriacao(LocalDateTime criacao) {
        this.criacao = criacao;
    }

    public int getDificuldade() {
        return dificuldade;
    }

    public void setDificuldade(int dificuldade) {
        this.dificuldade = dificuldade;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public ArrayList<HistoricoItem> getHistoricoItems() {
        return historicoItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cache cache = (Cache) o;
        return nome.equals(cache.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }

    @Override
    public String toString() {
        return "Cache{" +
                "nome=" + nome +
                ", proprietario=" + proprietario +
                ", localizacao=" + localizacao +
                ", regiao='" + regiao + '\'' +
                ", criacao=" + criacao +
                ", dificuldade=" + dificuldade +
                '}';
    }
}
