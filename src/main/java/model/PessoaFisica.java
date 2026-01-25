package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PessoaFisica extends Investidor {

    private final PerfilInvestimento perfil; // Conservador, Moderado, Arrojado

    public PessoaFisica(String nome, String cpf, LocalDate dataNascimento,
                        String telefone, Endereco endereco, BigDecimal patrimonio,
                        PerfilInvestimento perfil) {
        super(nome, cpf, dataNascimento, telefone, endereco, patrimonio);
        this.perfil = perfil;
    }

    public PerfilInvestimento getPerfil() {
        return perfil;
    }

    @Override
    public String toString() {
        return super.toString() + " - Perfil: " + perfil;
    }

}