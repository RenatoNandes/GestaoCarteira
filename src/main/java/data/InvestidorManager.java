package data;

import exception.MovimentacaoInvalidaException;
import model.carteira.Carteira;
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
            } catch (MovimentacaoInvalidaException e) {
                System.out.println("Erro ao remover ativo da carteira de " + inv.getNome() + ": " + e.getMessage());
            }
        }
    }

    public void carregarInvestidoresDeArquivo(String caminho) {
        List<String[]> linhas = utils.CsvReader.lerCsv(caminho);
        if (!linhas.isEmpty()) {
            linhas.removeFirst(); // remove cabeçalho
        }

        for (String[] cols : linhas) {
            try {
                // Nome
                String nome = cols.length > 0 ? cols[0].trim() : "";
                if (nome.isEmpty()) throw new IllegalArgumentException("Nome não pode ser vazio.");

                // CPF ou CNPJ
                String documento = cols.length > 1 ? cols[1].trim() : "";
                if (documento.isEmpty()) throw new IllegalArgumentException("CPF/CNPJ não pode ser vazio.");

                // Data de nascimento/fundação
                java.time.LocalDate nascimento = (cols.length > 2 && !cols[2].trim().isEmpty())
                        ? java.time.LocalDate.parse(cols[2].trim())
                        : null;

                // Telefone
                String telefone = cols.length > 3 ? cols[3].trim() : "";

                // Endereço detalhado
                String rua    = cols.length > 4 ? cols[4].trim() : "";
                String numero = cols.length > 5 ? cols[5].trim() : "";
                String bairro = cols.length > 6 ? cols[6].trim() : "";
                String cep    = cols.length > 7 ? cols[7].trim() : "";
                String cidade = cols.length > 8 ? cols[8].trim() : "";
                String estado = cols.length > 9 ? cols[9].trim() : "";

                model.investidor.Endereco endereco = new model.investidor.Endereco(
                        rua, numero, bairro, cep, cidade, estado
                );

                // Patrimônio
                java.math.BigDecimal patrimonio = (cols.length > 10 && !cols[10].trim().isEmpty())
                        ? new java.math.BigDecimal(cols[10].trim().replace(",", "."))
                        : java.math.BigDecimal.ZERO;

                // Campo extra: Perfil (PF) ou Razão Social (PJ)
                String campoExtra = cols.length > 11 ? cols[11].trim() : "";

                // Decidir se é PF ou PJ pelo documento
                if (documento.matches("\\d{11}")) {
                    // Pessoa Física
                    model.investidor.PerfilInvestimento perfil;
                    try {
                        perfil = campoExtra.isEmpty()
                                ? model.investidor.PerfilInvestimento.CONSERVADOR
                                : model.investidor.PerfilInvestimento.valueOf(campoExtra.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        perfil = model.investidor.PerfilInvestimento.CONSERVADOR;
                    }

                    model.investidor.Investidor inv = new model.investidor.PessoaFisica(
                            nome, documento, nascimento, telefone, endereco, patrimonio, perfil
                    );
                    adicionarInvestidor(inv);

                } else if (documento.matches("\\d{14}")) {
                    // Institucional
                    String razaoSocial = campoExtra.isEmpty() ? nome : campoExtra;

                    model.investidor.Investidor inv = new model.investidor.Institucional(
                            nome, documento, nascimento, telefone, endereco, patrimonio, razaoSocial
                    );
                    adicionarInvestidor(inv);

                } else {
                    throw new IllegalArgumentException("Documento inválido (CPF deve ter 11 dígitos, CNPJ 14).");
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
        Carteira carteiraAntiga = antigo.getCarteira();
        Carteira carteiraNova = novo.getCarteira();

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