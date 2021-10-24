package aed_lp;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Visita {
    private Cache cache;
    private Item itemColocado;
    private Item itemRemovido;
    private LocalDateTime datahora;

    public Visita(Cache cache, Item itemColocado, Item itemRemovido, LocalDateTime datahora) {
        this.cache = cache;
        this.itemColocado = itemColocado;
        this.itemRemovido = itemRemovido;
        this.datahora = datahora;
    }

    public Cache getCache() {
        return cache;
    }

    public Item getItemColocado() {
        return itemColocado;
    }

    public Item getItemRemovido() {
        return itemRemovido;
    }

    public LocalDateTime getDatahora() {
        return datahora;
    }

    @Override
    public String toString() {
        return "Visita{" +
                "cache=" + cache +
                ", datahora=" + datahora +
                ", itemColocado=" + itemColocado +
                ", itemRemovido=" + itemRemovido +
                '}';
    }
}
