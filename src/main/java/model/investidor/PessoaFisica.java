package model.investidor;

import model.ativo.Ativo;
import model.ativo.Criptomoeda;
import model.ativo.Stock;

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

    public PerfilInvestimento getPerfilInvestimento() {
        return this.perfil;
    }

    @Override
    public void comprar(Ativo ativo, BigDecimal quantidade, BigDecimal precoExecucao) {
        if (ativo instanceof Criptomoeda && perfil != PerfilInvestimento.ARROJADO)
            throw new IllegalArgumentException("Apenas investidores Arrojados podem comprar criptoativos.");

        if (ativo instanceof Stock && perfil == PerfilInvestimento.CONSERVADOR)
            throw new IllegalArgumentException("Investidores Conservadores não podem comprar stocks.");

        if (ativo.isQualificado() && getPatrimonio().compareTo(new BigDecimal("1000000")) < 0)
            throw new IllegalArgumentException("Somente investidores qualificados (patrimônio ≥ R$1.000.000,00) podem comprar este ativo.");

        getCarteira().adicionarAtivo(ativo, quantidade, precoExecucao);
    }

    @Override
    public String toString() {
        return super.toString() + " - Perfil: " + perfil;
    }

}