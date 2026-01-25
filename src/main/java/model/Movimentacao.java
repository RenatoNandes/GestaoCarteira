package model;

import exception.InvestidorNaoQualificadoException;
import exception.MovimentacaoInvalidaException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Movimentacao {

    private final int id;
    private final TipoMovimentacao tipo; // COMPRA ou VENDA
    private final Investidor investidor;
    private final Ativo ativo;
    private final BigDecimal quantidade;
    private final BigDecimal precoExecucao;
    private final LocalDateTime data;

    public Movimentacao(int id, TipoMovimentacao tipo, Investidor investidor,
                        Ativo ativo, BigDecimal quantidade, BigDecimal precoExecucao) {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new MovimentacaoInvalidaException("Quantidade deve ser maior que zero.");
        }
        if (precoExecucao == null || precoExecucao.compareTo(BigDecimal.ZERO) <= 0) {
            throw new MovimentacaoInvalidaException("Preço de execução deve ser maior que zero.");
        }

        this.id = id;
        this.tipo = tipo;
        this.investidor = investidor;
        this.ativo = ativo;
        this.quantidade = quantidade;
        this.precoExecucao = precoExecucao;
        this.data = LocalDateTime.now();
    }

    public void executar() {
        Carteira carteira = investidor.getCarteira();

        // Para que somente investidores qualificados comprem ativos restritos
        if (tipo == TipoMovimentacao.COMPRA && ativo.isQualificados()) {
            if (!(investidor instanceof Institucional)) {
                throw new InvestidorNaoQualificadoException(
                        "Investidor não qualificado não pode comprar ativo restrito: " + ativo.getNome()
                );
            }
        }
        if (tipo == TipoMovimentacao.COMPRA) {
            carteira.adicionarAtivo(ativo, quantidade);
        } else if (tipo == TipoMovimentacao.VENDA) {
            carteira.removerAtivo(ativo, quantidade);
        }
    }

    @Override
    public String toString() {
        return String.format("Movimentação #%d - %s %s (%s) Qtd: %s Preço: R$ %s Data: %s",
                id, tipo, ativo.getNome(), ativo.getTicker(),
                quantidade, precoExecucao, data);
    }

}