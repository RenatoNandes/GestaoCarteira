package data;

import model.investidor.Institucional;
import model.investidor.Investidor;
import model.ativo.Ativo;
import model.investidor.PessoaFisica;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InvestidorManager {

    private final List<Investidor> investidores = new ArrayList<>();

    public void adicionarInvestidor(Investidor inv) {
        investidores.add(inv);
    }

    public List<Investidor> getInvestidores() {
        return investidores;
    }

    public Investidor buscarPorIdentificador(String id) {
        return investidores.stream()
                .filter(i -> i.getIdentificador().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    public void removerInvestidor(String id) {
        investidores.removeIf(i -> i.getIdentificador().equalsIgnoreCase(id));
    }

    public void removerPorIdentificadores(List<String> ids) {
        List<String> lower = ids.stream().map(String::toLowerCase).collect(Collectors.toList());
        investidores.removeIf(i -> lower.contains(i.getIdentificador().toLowerCase()));
    }

    // Propaga exclusão de um ativo para todas as carteiras
    public void removerAtivoDeTodasCarteiras(Ativo ativo) {
        for (Investidor inv : investidores) {
            try {
                // tenta remover completamente o ativo da carteira
                // se a Carteira não tiver método para remover completamente, usa a quantidade atual
                var carteira = inv.getCarteira();
                if (carteira.getAtivos().containsKey(ativo)) {
                    var qtd = carteira.getAtivos().get(ativo);
                    carteira.removerAtivo(ativo, qtd); // usa removerAtivo(Ativo, BigDecimal)
                }
            } catch (Exception ignored) {
                // se algo falhar continua para os outros investidores
            }
        }
    }
    public void carregarInvestidoresDeArquivo(String caminho) {
        List<String[]> linhas = utils.CsvReader.lerCsv(caminho);
        if (linhas == null || linhas.isEmpty()) return;

        for (String[] cols : linhas) {
            try {
                // Exemplo de layout esperado (ajuste conforme CSV):
                // tipo;nome;identificador;telefone;data;patrimonio;campoExtra...
                // tipo = "PF" ou "PJ"
                String tipo = cols.length > 0 ? cols[0].trim().toUpperCase() : "";
                String nome = cols.length > 1 ? cols[1].trim() : "";
                String identificador = cols.length > 2 ? cols[2].trim() : "";
                String telefone = cols.length > 3 ? cols[3].trim() : "";
                java.time.LocalDate data = (cols.length > 4 && !cols[4].trim().isEmpty())
                        ? java.time.LocalDate.parse(cols[4].trim()) : null;
                java.math.BigDecimal patrimonio = (cols.length > 5 && !cols[5].trim().isEmpty())
                        ? new java.math.BigDecimal(cols[5].trim().replace(",", ".")) : java.math.BigDecimal.ZERO;

                if ("PF".equals(tipo) || "PESSOA_FISICA".equals(tipo) || "F".equals(tipo)) {
                    // perfil padrão conservador se não informado
                    model.investidor.PerfilInvestimento perfil = model.investidor.PerfilInvestimento.CONSERVADOR;
                    model.investidor.Investidor inv = new model.investidor.PessoaFisica(nome, identificador, data, telefone, null, patrimonio, perfil);
                    adicionarInvestidor(inv);
                } else {
                    // institucional: pode ter razao social em cols[6]
                    String razao = cols.length > 6 ? cols[6].trim() : "";
                    model.investidor.Investidor inv = new model.investidor.Institucional(nome, identificador, data, telefone, null, patrimonio, razao);
                    adicionarInvestidor(inv);
                }
            } catch (Exception e) {
                System.out.println("Linha de investidor ignorada (erro): " + e.getMessage());
            }
        }
    }
    public void atualizarInvestidor(String identificador, String novoNome, java.math.BigDecimal novoPatrimonio) {
        Investidor antigo = buscarPorIdentificador(identificador);
        if (antigo == null) throw new IllegalArgumentException("Investidor não encontrado: " + identificador);

        Investidor novo;

        try {
            if (antigo instanceof PessoaFisica pf) {
                novo = new PessoaFisica(
                        novoNome,
                        pf.getIdentificador(),
                        pf.getDataNascimento(),
                        pf.getTelefone(),
                        pf.getEndereco(),
                        novoPatrimonio,
                        pf.getPerfilInvestimento()
                );
            } else if (antigo instanceof Institucional inst) {
                novo = new Institucional(
                        novoNome,
                        inst.getIdentificador(),
                        inst.getDataNascimento(),
                        inst.getTelefone(),
                        inst.getEndereco(),
                        novoPatrimonio,
                        inst.getRazaoSocial()
                );
            } else {
                throw new IllegalStateException("Tipo de investidor desconhecido: " + antigo.getClass());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar novo investidor: " + e.getMessage(), e);
        }
        var carteiraAntiga = antigo.getCarteira();
        var carteiraNova = novo.getCarteira();

        for (var entry : carteiraAntiga.getAtivos().entrySet()) {
            Ativo ativo = entry.getKey();
            java.math.BigDecimal quantidade = entry.getValue();

            carteiraNova.getAtivos().put(ativo, quantidade);

            java.math.BigDecimal custo = carteiraAntiga.getValorGastoPorAtivo(ativo);
            if (custo != null && custo.compareTo(java.math.BigDecimal.ZERO) >= 0) {
                carteiraNova.definirCustoPosicao(ativo, custo);
            }
        }

        int idx = investidores.indexOf(antigo);
        if (idx >= 0) {
            investidores.set(idx, novo);
        } else {
            investidores.remove(antigo);
            investidores.add(novo);
        }
    }

}