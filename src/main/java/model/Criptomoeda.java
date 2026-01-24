package model;

import exception.AtivoInvalidoException;

import java.math.BigDecimal;

public class Criptomoeda extends Ativo {

    private final String algoritmoConsenso;
    private final BigDecimal quantidadeMaxima; // pode ser null (ex: ETH)
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
        this.quantidadeMaxima = quantidadeMaxima;
        this.fatorConversao = fatorConversao;
    }

    public String getAlgoritmoConsenso() {
        return algoritmoConsenso;
    }

    public BigDecimal getQuantidadeMaxima() {
        return quantidadeMaxima;
    }

    public BigDecimal getFatorConversao() {
        return fatorConversao;
    }

    @Override
    public BigDecimal converterParaReal() {
        return getPrecoAtual().multiply(fatorConversao);
    }

    @Override
    public String toString() {
        return String.format("Cripto: %s (%s) - Algoritmo: %s - US$ %s (~R$ %s)",
                getNome(), getTicker(), algoritmoConsenso,
                getPrecoAtual(), converterParaReal());
    }

}
