package model.ativo;

import exception.AtivoInvalidoException;
import model.investidor.Origem;

import java.math.BigDecimal;

public abstract class Ativo {
    private final String nome;
    private final String ticker;
    private BigDecimal precoAtual; // não vamos usar double para evitar imprecisão
    private final boolean restritoQualificado;
    private final TipoRenda tipoRenda; // fixa/variável
    private final Origem origem; // nacional/internacional;

    public Ativo(String nome, String ticker, BigDecimal precoAtual, boolean restritoQualificado, TipoRenda tipoRenda, Origem origem) {
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
        this.restritoQualificado = restritoQualificado;
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

    public boolean isQualificado() {
        return restritoQualificado;
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
    public BigDecimal converterValorParaReal(BigDecimal valor) {
        if (valor == null) {
            throw new AtivoInvalidoException("Valor para conversão não pode ser nulo.");
        }
        return valor; // padrão: nacional
    }

    // preço atual convertido para real
    public BigDecimal converterParaReal() {
        return converterValorParaReal(precoAtual);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - R$ %s", nome, ticker, precoAtual);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (getClass() != o.getClass()) return false;

        Ativo other = (Ativo) o;
        String t1 = (this.getTicker() == null) ? "" : this.getTicker().trim().toUpperCase();
        String t2 = (other.getTicker() == null) ? "" : other.getTicker().trim().toUpperCase();
        return t1.equals(t2);
    }

    @Override
    public int hashCode() {
        String t = (this.getTicker() == null) ? "" : this.getTicker().trim().toUpperCase();
        return 31 * getClass().hashCode() + t.hashCode();
    }


}


