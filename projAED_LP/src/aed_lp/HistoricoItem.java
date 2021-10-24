package aed_lp;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class HistoricoItem {
    private Item item;
    private Utilizador quemColocou;
    private Utilizador quemRemoveu;
    private LocalDateTime colocado;
    private LocalDateTime removido;

    public HistoricoItem(Item item, Utilizador quemColocou, LocalDateTime colocado) {
        this.item = item;
        this.quemColocou = quemColocou;
        this.colocado = colocado;
    }

    public void setQuemRemoveu(Utilizador quemRemoveu) {
        this.quemRemoveu = quemRemoveu;
    }

    public void setRemovido(LocalDateTime removido) {
        this.removido = removido;
    }

    public Item getItem() {
        return item;
    }

    public Utilizador getQuemColocou() {
        return quemColocou;
    }

    public Utilizador getQuemRemoveu() {
        return quemRemoveu;
    }

    public LocalDateTime getColocado() {
        return colocado;
    }

    public LocalDateTime getRemovido() {
        return removido;
    }

    @Override
    public String toString() {
        return "HistoricoItem{" +
                "item=" + item +
                ", colocado=" + colocado +
                ", quemColocou=" + quemColocou +
                ", removido=" + removido +
                ", quemRemoveu=" + quemRemoveu +
                '}';
    }
}
