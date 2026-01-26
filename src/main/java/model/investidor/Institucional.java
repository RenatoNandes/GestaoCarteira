package model.investidor;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Institucional extends Investidor {

    private final String razaoSocial;

    public Institucional(String nome, String cnpj, LocalDate dataNascimento,
                         String telefone, Endereco endereco, BigDecimal patrimonio,
                         String razaoSocial) {
        super(nome, cnpj, dataNascimento, telefone, endereco, patrimonio);
        if (razaoSocial == null || razaoSocial.isBlank()) {
            throw new IllegalArgumentException("Razão social não pode ser nula ou vazia.");
        }
        this.razaoSocial = razaoSocial;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    @Override
    public String toString() {
        return super.toString() + " - Razão Social: " + razaoSocial;
    }

}