package model;

import java.math.BigDecimal;

public class Acao extends Ativo{
    private String tipoAcao;

    public Acao(String nome, String ticker, BigDecimal precoAtual, boolean restritoQualificados) {
        super(nome, ticker, precoAtual, restritoQualificados, TipoRenda.VARIAVEL, Origem.NACIONAL);
        this.tipoAcao = definirTipoPeloTicker(ticker);
    }

    public String getTipoAcao() {
        return tipoAcao;
    }

    private String definirTipoPeloTicker(String ticker) {
        String t = ticker.trim().toUpperCase();

        if (t.endsWith("11")) {
            return TipoAcao.UNIT;
        }

    }
}
