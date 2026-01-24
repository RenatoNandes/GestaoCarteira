package model;

import exception.AtivoInvalidoException;

import java.math.BigDecimal;

public abstract class Ativo {
    private final String nome;
    private final String ticker;
    private BigDecimal precoAtual; // não vamos usar double para evitar imprecisão
    private final boolean restritoQualificados;
    private final TipoRenda tipoRenda; // fixa/variável
    private final Origem origem; // nacional/internacional;

    public Ativo(String nome, String ticker, BigDecimal precoAtual, boolean restritoQualificados, TipoRenda tipoRenda, Origem origem) {
        if (nome == null || nome.isBlank()) {
            throw new AtivoInvalidoException("Nome do ativo não pode ser nulo ou vazio.");
        }

        if (ticker == null || ticker.isBlank()) {
            throw new AtivoInvalidoException("Ticker do ativo não pode ser nulo ou vazio.");
        }

        if (precoAtual == null || precoAtual.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AtivoInvalidoException("Preço do ativo deve ser maior que zero.");
        }

        if (tipoRenda == null) {
            throw new AtivoInvalidoException("Tipo de renda do ativo não pode ser nulo.");
        }

        if (origem == null) {
            throw new AtivoInvalidoException("Origem do ativo não pode ser nula.");
        }

        this.nome = nome;
        this.ticker = ticker;
        this.precoAtual = precoAtual;
        this.restritoQualificados = restritoQualificados;
        this.tipoRenda = tipoRenda;
        this.origem = origem;
    }

    public String getNome() {
        return nome;
    }

    public String getTicker() {
        return ticker;
    }

    public BigDecimal getPrecoAtual() {
        return precoAtual;
    }

    public boolean isQualificados() {
        return restritoQualificados;
    }

    public TipoRenda getTipoRenda() {
        return tipoRenda;
    }

    public Origem getOrigem() {
        return origem;
    }

    public void atualizarPreco(BigDecimal precoNovo) {
        if (precoNovo == null || precoNovo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AtivoInvalidoException("Novo preço deve ser maior que zero.");
        }

        this.precoAtual = precoNovo;
    }

    // metodo padrão, nacionais retornam o preço atual
    public BigDecimal converterParaReal() {
        return precoAtual;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - R$ %s", nome, ticker, precoAtual);
    }

}
