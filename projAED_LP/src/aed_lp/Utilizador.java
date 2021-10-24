package aed_lp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class Utilizador {
    private static int lastId=0;
    private int id;
    private String tipo;
    private String nome;
    private ArrayList<Item> itemsColocados;
    private ArrayList<TravelBug> travelBugs;
    private ArrayList<Visita> cachesVisitadas;
    private ArrayList<Cache> cachesEscondidas;

    public Utilizador(String tipo, String nome) {
        this.id = ++lastId;
        this.tipo = tipo;
        this.nome = nome;
        itemsColocados = new ArrayList<>();
        travelBugs = new ArrayList<>();
        cachesVisitadas=new ArrayList<>();
        cachesEscondidas=new ArrayList<>();
    }

    public Utilizador(int id, String tipo, String nome) {
        this.id = id;
        this.tipo = tipo;
        this.nome = nome;
        if (id>lastId)
            lastId=id;
        itemsColocados = new ArrayList<>();
        travelBugs = new ArrayList<>();
        cachesVisitadas=new ArrayList<>();
        cachesEscondidas=new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ArrayList<Item> getItemsColocados() {
        return itemsColocados;
    }

    public ArrayList<TravelBug> getTravelBugs() {
        return travelBugs;
    }

    public ArrayList<Visita> getCachesVisitadas() {
        return cachesVisitadas;
    }

    public ArrayList<Cache> getCachesEscondidas() {
        return cachesEscondidas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utilizador that = (Utilizador) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Utilizador{" +
                "id=" + id +
                ", tipo='" + tipo + '\'' +
                ", nome='" + nome + '\'' +
                '}';
    }

    public int getCachesVisitadasPorPeriodo(LocalDate inicio, LocalDate fim) {
        int n=0;
        for(Visita visita: cachesVisitadas){
            if (  visita.getDatahora().toLocalDate().compareTo(inicio)>=0
               && visita.getDatahora().toLocalDate().compareTo(fim)<=0
            )
                n++;
        }
        return n;
    }
}
