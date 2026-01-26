package model.ativo;

import exception.AtivoInvalidoException;
import model.investidor.Origem;

import java.math.BigDecimal;

public class Fii extends Ativo {
    private final String segmento;
    private final BigDecimal ultimoDividendo;
    private final BigDecimal taxaAdministracao;

    public Fii(String nome,
               String ticker,
               BigDecimal precoAtual,
               boolean restritoQualificados,
               String segmento,
               BigDecimal ultimoDividendo,
               BigDecimal taxaAdministracao) {

        super(nome, ticker, precoAtual, restritoQualificados, TipoRenda.VARIAVEL, Origem.NACIONAL);

        if (segmento == null || segmento.isBlank()) {
            throw new AtivoInvalidoException("Segmento do FII não pode ser nulo ou vazio.");
        }

        if (ultimoDividendo == null || ultimoDividendo.compareTo(BigDecimal.ZERO) < 0) {
            throw new AtivoInvalidoException("Último dividendo do FII não pode ser nulo ou negativo.");
        }

        if (taxaAdministracao == null || taxaAdministracao.compareTo(BigDecimal.ZERO) < 0) {
            throw new AtivoInvalidoException("Taxa de administração do FII não pode ser nula ou negativa.");
        }

        this.segmento = segmento;
        this.ultimoDividendo = ultimoDividendo;
        this.taxaAdministracao = taxaAdministracao;
    }

    public String getSegmento() {
        return segmento;
    }

    public BigDecimal getUltimoDividendo() {
        return ultimoDividendo;
    }

    public BigDecimal getTaxaAdministracao() {
        return taxaAdministracao;
    }

    // metodo que exibe a taxa concatenada + %
    public String getTaxaAdministracaoFormatada() {
        return taxaAdministracao.toPlainString() + "%";
    }

}
