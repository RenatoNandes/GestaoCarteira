package model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Carteira {

    private final Map<Ativo, BigDecimal> ativos = new HashMap<>();

    // Adiciona ativos (compra)
    public void adicionarAtivo(Ativo ativo, BigDecimal quantidade) {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }
        ativos.merge(ativo, quantidade, BigDecimal::add);
    }

    // Remove ativos (venda)
    public void removerAtivo(Ativo ativo, BigDecimal quantidade) {
        BigDecimal atual = ativos.getOrDefault(ativo, BigDecimal.ZERO);
        if (atual.compareTo(quantidade) < 0) {
            throw new IllegalArgumentException("Quantidade insuficiente para venda.");
        }
        BigDecimal novaQtd = atual.subtract(quantidade);
        if (novaQtd.compareTo(BigDecimal.ZERO) == 0) {
            ativos.remove(ativo);
        } else {
            ativos.put(ativo, novaQtd);
        }
    }

    // Valor total atual da carteira (em reais)
    public BigDecimal valorTotalAtual() {
        return ativos.entrySet().stream()
                .map(e -> e.getKey().converterParaReal().multiply(e.getValue()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Percentual de renda fixa
    public BigDecimal percentualRendaFixa() {
        BigDecimal total = valorTotalAtual();
        if (total.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;

        BigDecimal rendaFixa = ativos.entrySet().stream()
                .filter(e -> e.getKey().getTipoRenda() == TipoRenda.FIXA)
                .map(e -> e.getKey().converterParaReal().multiply(e.getValue()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return rendaFixa.multiply(BigDecimal.valueOf(100)).divide(total, 2, BigDecimal.ROUND_HALF_UP);
    }

    // Percentual de renda variável
    public BigDecimal percentualRendaVariavel() {
        BigDecimal total = valorTotalAtual();
        if (total.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;

        BigDecimal rendaVariavel = ativos.entrySet().stream()
                .filter(e -> e.getKey().getTipoRenda() == TipoRenda.VARIAVEL)
                .map(e -> e.getKey().converterParaReal().multiply(e.getValue()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return rendaVariavel.multiply(BigDecimal.valueOf(100)).divide(total, 2, BigDecimal.ROUND_HALF_UP);
    }

    // Percentual de ativos nacionais
    public BigDecimal percentualNacional() {
        BigDecimal total = valorTotalAtual();
        if (total.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;

        BigDecimal nacional = ativos.entrySet().stream()
                .filter(e -> e.getKey().getOrigem() == Origem.NACIONAL)
                .map(e -> e.getKey().converterParaReal().multiply(e.getValue()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return nacional.multiply(BigDecimal.valueOf(100)).divide(total, 2, BigDecimal.ROUND_HALF_UP);
    }

    // Percentual de ativos internacionais
    public BigDecimal percentualInternacional() {
        BigDecimal total = valorTotalAtual();
        if (total.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;

        BigDecimal internacional = ativos.entrySet().stream()
                .filter(e -> e.getKey().getOrigem() == Origem.INTERNACIONAL)
                .map(e -> e.getKey().converterParaReal().multiply(e.getValue()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return internacional.multiply(BigDecimal.valueOf(100)).divide(total, 2, BigDecimal.ROUND_HALF_UP);
    }

    public Map<Ativo, BigDecimal> getAtivos() {
        return ativos;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Carteira:\n");
        ativos.forEach((ativo, qtd) -> sb.append(ativo).append(" - Qtd: ").append(qtd).append("\n"));
        sb.append("Valor total atual: R$ ").append(valorTotalAtual());
        return sb.toString();
    }

}