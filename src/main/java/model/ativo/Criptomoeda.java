package model.ativo;

import exception.AtivoInvalidoException;
import model.investidor.Origem;

import java.math.BigDecimal;

public class Criptomoeda extends Ativo {

    private final String algoritmoConsenso;
    private final BigDecimal fatorConversao;   // ex: dólar -> real

    public Criptomoeda(String nome,
                       String ticker,
                       BigDecimal precoAtual,
                       boolean restritoQualificados,
                       String algoritmoConsenso,
                       BigDecimal quantidadeMaxima,
                       BigDecimal fatorConversao) {

        super(nome, ticker, precoAtual, restritoQualificados, TipoRenda.VARIAVEL, Origem.INTERNACIONAL);

        if (algoritmoConsenso == null || algoritmoConsenso.isBlank()) {
            throw new AtivoInvalidoException("Algoritmo de consenso não pode ser nulo ou vazio.");
        }

        if (fatorConversao == null || fatorConversao.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AtivoInvalidoException("Fator de conversão deve ser maior que zero.");
        }

        if (quantidadeMaxima != null && quantidadeMaxima.compareTo(BigDecimal.ZERO) < 0) {
            throw new AtivoInvalidoException("Quantidade máxima não pode ser negativa.");
        }

        this.algoritmoConsenso = algoritmoConsenso.trim();
        this.fatorConversao = fatorConversao;
    }

    @Override
    public BigDecimal converterValorParaReal(BigDecimal valor) {
        if (valor == null) {
            throw new AtivoInvalidoException("Valor para conversão não pode ser nulo.");
        }
        return valor.multiply(fatorConversao);
    }


    @Override
    public String toString() {
        return String.format("Cripto: %s (%s) - Algoritmo: %s - US$ %s (~R$ %s)",
                getNome(), getTicker(), algoritmoConsenso,
                getPrecoAtual(), converterParaReal());
    }

}
