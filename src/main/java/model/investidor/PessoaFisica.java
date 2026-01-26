package model.investidor;

import exception.QuantidadeInsuficienteException;
import exception.QuantidadeInvalidaException;
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
    public void comprar(Ativo ativo, int qtd) {
        if (qtd <= 0) {
            throw new QuantidadeInvalidaException("Não foi possível comprar: quantidade deve ser maior que zero.");
        }

        // Regras específicas para Pessoa Física
        if (ativo instanceof Criptomoeda && perfil != PerfilInvestimento.ARROJADO) {
            throw new IllegalArgumentException("Apenas investidores Arrojados podem comprar criptoativos.");
        }

        if (ativo instanceof Stock && perfil == PerfilInvestimento.CONSERVADOR) {
            throw new IllegalArgumentException("Investidores Conservadores não podem comprar stocks.");
        }

        // Ativos qualificados só podem ser comprados por PF com patrimônio >= 1.000.000
        if (ativo.isQualificado() && getPatrimonio().compareTo(new BigDecimal("1000000")) < 0) {
            throw new IllegalArgumentException("Somente investidores qualificados (patrimônio ≥ R$1.000.000,00) podem comprar este ativo.");
        }

        // Se passou pelas regras, adiciona na carteira
        getCarteira().adicionarAtivo(ativo, BigDecimal.valueOf(qtd));
    }

    @Override
    public void vender(Ativo ativo, int qtd) {
        if (qtd <= 0) {
            throw new QuantidadeInvalidaException("Não foi possível vender: quantidade deve ser maior que zero.");
        }

        try {
            getCarteira().removerAtivo(ativo, BigDecimal.valueOf(qtd));
        } catch (IllegalArgumentException e) {
            throw new QuantidadeInsuficienteException("Não foi possível vender: quantidade insuficiente.");
        }
    }

    @Override
    public String toString() {
        return super.toString() + " - Perfil: " + perfil;
    }

}