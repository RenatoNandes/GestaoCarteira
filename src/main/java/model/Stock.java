package model;

import exception.AtivoInvalidoException;

import java.math.BigDecimal;

public class Stock extends Ativo {

    private final String bolsaNegociacao;
    private final String setor;
    private final BigDecimal fatorConversao; // ex: dólar -> real

    public Stock(String nome,
                 String ticker,
                 BigDecimal precoAtual,
                 boolean restritoQualificados,
                 String bolsaNegociacao,
                 String setor,
                 BigDecimal fatorConversao) {

        super(nome, ticker, precoAtual, restritoQualificados, TipoRenda.VARIAVEL, Origem.INTERNACIONAL);

        if (bolsaNegociacao == null || bolsaNegociacao.isBlank()) {
            throw new AtivoInvalidoException("Bolsa de negociação não pode ser nula ou vazia.");
        }

        if (setor == null || setor.isBlank()) {
            throw new AtivoInvalidoException("Setor da Stock não pode ser nulo ou vazio.");
        }

        if (fatorConversao == null || fatorConversao.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AtivoInvalidoException("Fator de conversão deve ser maior que zero.");
        }

        this.bolsaNegociacao = bolsaNegociacao.trim();
        this.setor = setor.trim();
        this.fatorConversao = fatorConversao;
    }

    public String getBolsaNegociacao() {
        return bolsaNegociacao;
    }

    public String getSetor() {
        return setor;
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
        return String.format("Stock: %s (%s) - Setor: %s - Bolsa: %s - US$ %s (~R$ %s)",
                getNome(), getTicker(), setor, bolsaNegociacao,
                getPrecoAtual(), converterParaReal());
    }

}
