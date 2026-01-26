package model.ativo;

import exception.AtivoInvalidoException;
import model.investidor.Origem;

import java.math.BigDecimal;

public class Acao extends Ativo {

    private final TipoAcao tipoAcao;

    public Acao(String nome, String ticker, BigDecimal precoAtual, boolean restritoQualificados) {
        super(nome, ticker, precoAtual, restritoQualificados, TipoRenda.VARIAVEL, Origem.NACIONAL);
        this.tipoAcao = definirTipoPeloTicker(ticker);
    }

    public TipoAcao getTipoAcao() {
        return tipoAcao;
    }

    private TipoAcao definirTipoPeloTicker(String ticker) {
        String t = ticker.trim().toUpperCase();

        if (t.endsWith("11")) {
            return TipoAcao.UNIT;
        }

        char ultimo = t.charAt(t.length() - 1);

        if (ultimo == '3') {
            return TipoAcao.ORDINARIA;
        }
        if (ultimo == '4' || ultimo == '5' || ultimo == '6') {
            return TipoAcao.PREFERENCIAL;
        }

        // se nao encaixa em nenhum lugar eh invalido
        throw new AtivoInvalidoException("Ticker de ação inválido: " + ticker);
    }

    @Override
    public String toString() {
        return String.format("Ação: %s (%s) - Tipo: %s - R$ %s",
                getNome(), getTicker(), tipoAcao, getPrecoAtual());
    }

}
