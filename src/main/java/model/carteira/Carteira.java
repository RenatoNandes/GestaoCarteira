package model.carteira;

import exception.QuantidadeInsuficienteException;
import exception.QuantidadeInvalidaException;
import model.ativo.Ativo;
import model.ativo.TipoRenda;
import model.investidor.Origem;

import java.math.RoundingMode;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Carteira {

    private final Map<Ativo, BigDecimal> ativos = new HashMap<>();
    private final Map<Ativo, BigDecimal> valorGastoPorAtivo = new HashMap<>();


    // Adiciona ativos (compra)
    public void adicionarAtivo(Ativo ativo, BigDecimal quantidade, BigDecimal precoExecucao) {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new QuantidadeInvalidaException("Não foi possível comprar: quantidade deve ser maior que zero.");
        }
        if (precoExecucao == null || precoExecucao.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço de execução deve ser maior que zero.");
        }
        ativos.merge(ativo, quantidade, BigDecimal::add);
        // custo da compra em REAL: converte o preço de execução e multiplica pela quantidade
        BigDecimal custoCompraEmReal = ativo.converterValorParaReal(precoExecucao).multiply(quantidade);
        valorGastoPorAtivo.merge(ativo, custoCompraEmReal, BigDecimal::add);
    }

    public void adicionarAtivo(Ativo ativo, BigDecimal quantidade) {
        adicionarAtivo(ativo, quantidade, ativo.getPrecoAtual());
    }

    // Remove ativos (venda)

    public void removerAtivo(Ativo ativo, BigDecimal quantidade) {
        BigDecimal atual = ativos.getOrDefault(ativo, BigDecimal.ZERO);

        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new QuantidadeInvalidaException("Quantidade inválida para venda.");
        }
        if (atual.compareTo(quantidade) < 0) {
            throw new QuantidadeInsuficienteException("Não foi possível vender: quantidade insuficiente.");
        }

        // custo total da posição atual (em REAL)
        BigDecimal custoTotal = valorGastoPorAtivo.getOrDefault(ativo, BigDecimal.ZERO);

        // custo médio = custo total / quantidade atual
        BigDecimal custoMedio = custoTotal.divide(atual, 10, RoundingMode.HALF_UP);

        // remove custo proporcional ao vendido
        BigDecimal custoRemovido = custoMedio.multiply(quantidade);
        BigDecimal novoCustoTotal = custoTotal.subtract(custoRemovido);

        BigDecimal novaQtd = atual.subtract(quantidade);

        if (novaQtd.compareTo(BigDecimal.ZERO) == 0) {
            ativos.remove(ativo);
            valorGastoPorAtivo.remove(ativo);
        } else {
            ativos.put(ativo, novaQtd);
            // evita negativo por arredondamento
            if (novoCustoTotal.compareTo(BigDecimal.ZERO) < 0) novoCustoTotal = BigDecimal.ZERO;

            valorGastoPorAtivo.put(ativo, novoCustoTotal);
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

    public void exibirCarteira() {
        if (ativos.isEmpty()) {
            System.out.println("Carteira vazia.");
            return;
        }
        int i = 1;
        for (Map.Entry<Ativo, BigDecimal> entry : ativos.entrySet()) {
            System.out.println(i++ + " - " + entry.getKey() + " | Quantidade: " + entry.getValue());
        }
    }

    public void adicionarGastoPorAtivo(Ativo ativo, java.math.BigDecimal gasto) {
        if (ativo == null || gasto == null) return;
        if (gasto.compareTo(java.math.BigDecimal.ZERO) <= 0) return;
        valorGastoPorAtivo.merge(ativo, gasto, java.math.BigDecimal::add);
    }

    public Ativo getAtivoPorIndice(int indice) {
        int i = 1;
        for (Ativo ativo : ativos.keySet()) {
            if (i == indice) return ativo;
            i++;
        }
        throw new IllegalArgumentException("Índice inválido.");
    }

    public BigDecimal getValorTotalAtual() {
        return valorTotalAtual(); // apenas delega para o método já existente
    }

    // retorna o valor gasto total (soma de valorGastoPorAtivo)
    public java.math.BigDecimal getValorTotalGasto() {
        return valorGastoPorAtivo.values().stream()
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    // retorna o valor gasto para um ativo específico (ou ZERO)
    public java.math.BigDecimal getValorGastoPorAtivo(Ativo ativo) {
        return valorGastoPorAtivo.getOrDefault(ativo, java.math.BigDecimal.ZERO);
    }

    public void carregarMovimentacoesDeArquivo(String caminho, data.AtivoManager ativoManager) {
        List<String[]> linhas = utils.CsvReader.lerCsv(caminho);
        if (linhas == null || linhas.isEmpty()) return;

        for (String[] cols : linhas) {
            try {
                String tipo = cols.length > 0 ? cols[0].trim().toUpperCase() : "";
                String ticker = cols.length > 1 ? cols[1].trim() : "";
                String qtdStr = cols.length > 2 ? cols[2].trim() : "0";
                String precoStr = cols.length > 3 ? cols[3].trim() : "";
                java.math.BigDecimal quantidade = qtdStr.isEmpty() ? java.math.BigDecimal.ZERO
                        : new java.math.BigDecimal(qtdStr.replace(",", "."));
                java.math.BigDecimal preco = precoStr.isEmpty() ? null
                        : new java.math.BigDecimal(precoStr.replace(",", "."));

                // procura ativo na carteira
                Ativo alvo = null;
                for (Ativo a : ativos.keySet()) {
                    if (a.getTicker().equalsIgnoreCase(ticker)) {
                        alvo = a;
                        break;
                    }
                }
                // se não achou na carteira, tenta no AtivoManager
                if (alvo == null && ativoManager != null) {
                    for (Ativo a : ativoManager.getAtivos()) {
                        if (a.getTicker().equalsIgnoreCase(ticker)) {
                            alvo = a;
                            break;
                        }
                    }
                }

                if (alvo == null) {
                    System.out.println("Ativo não encontrado para ticker: " + ticker + " (linha ignorada)");
                    continue;
                }

                if ("C".equals(tipo)) {
                    java.math.BigDecimal precoUsado = preco != null ? preco : alvo.getPrecoAtual();
                    adicionarAtivo(alvo, quantidade);
                    if (preco != null) {
                        java.math.BigDecimal gasto = precoUsado.multiply(quantidade);
                        valorGastoPorAtivo.merge(alvo, gasto, java.math.BigDecimal::add);
                    }
                } else if ("V".equals(tipo)) {
                    removerAtivo(alvo, quantidade);
                } else {
                    System.out.println("Tipo de movimentação inválido: " + tipo + " (linha ignorada)");
                }
            } catch (Exception e) {
                System.out.println("Erro ao processar movimentação: " + e.getMessage());
            }
        }
    }

    public void exibirCarteiraDetalhada() {
        if (ativos.isEmpty()) {
            System.out.println("Carteira vazia.");
            return;
        }
        System.out.printf("%-12s %-10s %-18s %-18s%n", "IDENTIFICADOR", "QUANT", "VALOR GASTO (R$)", "VALOR ATUAL (R$)");
        for (Map.Entry<Ativo, BigDecimal> entry : ativos.entrySet()) {
            Ativo ativo = entry.getKey();
            BigDecimal qtd = entry.getValue();
            BigDecimal valorGasto = getValorGastoPorAtivo(ativo); // já implementado
            BigDecimal valorAtual = ativo.converterParaReal().multiply(qtd);
            System.out.printf("%-12s %-10s %-18s %-18s%n",
                    ativo.getTicker(),
                    qtd,
                    valorGasto,
                    valorAtual);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Carteira:\n");
        ativos.forEach((ativo, qtd) -> sb.append(ativo).append(" - Qtd: ").append(qtd).append("\n"));
        sb.append("Valor total atual: R$ ").append(valorTotalAtual());
        return sb.toString();
    }

}