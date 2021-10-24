package aed_lp;

import java.util.Objects;

public class Item {
    private static int lastId=0;

    private int id;
    private String nome;

    public Item(String nome) {
        this.id = ++lastId;
        this.nome = nome;
    }

    public Item(int id, String nome) {
        this.id = id;
        if (id>lastId)
            lastId=id;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                '}';
    }
}
