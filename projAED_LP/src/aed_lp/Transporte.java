package aed_lp;

import java.time.LocalDateTime;

public class Transporte {
    private Utilizador utilizador;
    private LocalDateTime inicio;
    private LocalDateTime fim=null;

    public Transporte(Utilizador utilizador, LocalDateTime inicio) {
        this.utilizador = utilizador;
        this.inicio = inicio;
    }

    public Utilizador getUtilizador() {
        return utilizador;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public LocalDateTime getFim() {
        return fim;
    }

    public void setFim(LocalDateTime fim) {
        this.fim = fim;
    }
}
