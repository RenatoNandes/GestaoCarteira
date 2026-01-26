package model.ativo;

import exception.AtivoInvalidoException;
import model.investidor.Origem;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Tesouro extends Ativo {

    private final TipoRendimento tipoRendimento;
    private final LocalDate dataVencimento;

    public Tesouro(String nome,
                   String ticker,
                   BigDecimal precoAtual,
                   boolean restritoQualificados,
                   TipoRendimento tipoRendimento,
                   LocalDate dataVencimento) {

        super(nome, ticker, precoAtual, restritoQualificados, TipoRenda.FIXA, Origem.NACIONAL);

        if (tipoRendimento == null) {
            throw new AtivoInvalidoException("Tipo de rendimento do Tesouro não pode ser nulo.");
        }

        if (dataVencimento == null) {
            throw new AtivoInvalidoException("Data de vencimento do Tesouro não pode ser nula.");
        }

        this.tipoRendimento = tipoRendimento;
        this.dataVencimento = dataVencimento;
    }

    public TipoRendimento getTipoRendimento() {
        return tipoRendimento;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    @Override
    public String toString() {
        return String.format("Tesouro: %s (%s) - Tipo: %s - Vencimento: %s - R$ %s",
                getNome(), getTicker(), tipoRendimento, dataVencimento, getPrecoAtual());
    }

}