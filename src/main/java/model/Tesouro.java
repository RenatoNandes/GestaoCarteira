package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Tesouro extends Ativo {

    private final String tipoRendimento;
    private final LocalDate dataVencimento;

    public Tesouro(String nome,
                   String ticker,
                   BigDecimal precoAtual,
                   boolean restritoQualificados,
                   String tipoRendimento,
                   LocalDate dataVencimento) {

        super(nome, ticker, precoAtual, restritoQualificados, TipoRenda.FIXA, Origem.NACIONAL);

        if (tipoRendimento == null || tipoRendimento.isBlank()) {
            throw new AtivoInvalidoException("Tipo de rendimento do Tesouro não pode ser nulo ou vazio.");
        }

        String tr = tipoRendimento.trim().toUpperCase();
        if (!tr.equals(TipoRendimento.SELIC)
                && !tr.equals(TipoRendimento.PREFIXADO)
                && !tr.equals(TipoRendimento.IPCA_MAIS)) {
            throw new AtivoInvalidoException("Tipo de rendimento inválido: " + tipoRendimento);
        }

        if (dataVencimento == null) {
            throw new AtivoInvalidoException("Data de vencimento do Tesouro não pode ser nula.");
        }

        this.tipoRendimento = tr;
        this.dataVencimento = dataVencimento;
    }

    public String getTipoRendimento() {
        return tipoRendimento;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }
}