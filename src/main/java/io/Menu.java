package io;

import data.*;
import model.ativo.*;
import model.investidor.*;
import utils.InfoUtils;
import utils.InputUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Menu {

    private final InputUtils inputUtils;
    private final InfoUtils infoUtils;
    private final AtivoManager ativoManager;
    private final InvestidorManager investidorManager;
    private Investidor investidorSelecionado;

    public Menu(InputUtils inputUtils, InfoUtils infoUtils, AtivoManager ativoManager, InvestidorManager investidorManager) {
        this.inputUtils = inputUtils;
        this.infoUtils = infoUtils;
        this.ativoManager = ativoManager;
        this.investidorManager = investidorManager;
    }

    public void exibirMenuPrincipal() {
        boolean rodando = true;
        while (rodando) {
            System.out.println("\n===== MENU PRINCIPAL =====");
            System.out.println("1 - Ativos");
            System.out.println("2 - Investidores");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");
            int opcao = inputUtils.lerOpcao(0, 2);
            switch (opcao) {
                case 1 -> menuAtivos();
                case 2 -> menuInvestidores();
                case 0 -> rodando = false;
            }
        }
    }

    private void menuAtivos() {
        boolean rodando = true;
        while (rodando) {
            System.out.println("\n===== MENU ATIVOS =====");
            System.out.println("1 - Cadastrar ativo");
            System.out.println("2 - Cadastrar ativo em lote");
            System.out.println("3 - Editar ativo (apenas preço)");
            System.out.println("4 - Excluir ativo");
            System.out.println("5 - Exibir relatório de ativos");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opção: ");
            int opcao = inputUtils.lerOpcao(0, 5);
            switch (opcao) {
                case 1 -> cadastrarAtivo();
                case 2 -> cadastrarAtivoEmLote();
                case 3 -> editarAtivo();
                case 4 -> excluirAtivo();
                case 5 -> relatorioAtivos();
                case 0 -> rodando = false;
            }
        }
    }

    private void cadastrarAtivo() {
        System.out.println("\n===== CADASTRAR ATIVO =====");
        System.out.println("1 - Ação | 2 - FII | 3 - Tesouro | 4 - Cripto | 5 - Stock");
        int tipo = inputUtils.lerOpcao(1, 5);

        String nome = infoUtils.lerNome("Nome do ativo");
        String ticker = infoUtils.lerTicker();
        BigDecimal preco = infoUtils.lerPreco();
        boolean restrito = inputUtils.lerBoolean("Restrito a qualificados? (s/n)");

        if (ticker == null || ticker.isBlank()) {
            System.out.println("Erro: identificador não pode ser nulo ou em branco.");
            return;
        }
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Erro: preço inválido (nulo, zero ou negativo).");
            return;
        }

        Ativo ativo;
        switch (tipo) {
            case 1 -> { // Acao(String nome, String ticker, BigDecimal precoAtual, boolean restritoQualificados)
                ativo = new Acao(nome, ticker, preco, restrito);
            }
            case 2 -> { // Fii(String nome, String ticker, BigDecimal precoAtual, boolean restritoQualificados, String segmento, BigDecimal ultimoDividendo, BigDecimal taxaAdministracao)
                String segmento = infoUtils.lerTexto("Segmento");
                BigDecimal ultimoDiv = infoUtils.lerBigDecimal("Último dividendo");
                BigDecimal taxaAdm = infoUtils.lerBigDecimal("Taxa de administração");
                ativo = new Fii(nome, ticker, preco, restrito, segmento, ultimoDiv, taxaAdm);
            }
            case 3 -> { // Tesouro(String nome, String ticker, BigDecimal precoAtual, boolean restritoQualificados, TipoRendimento tipoRendimento, LocalDate dataVencimento)
                TipoRendimento tr = infoUtils.lerTipoRendimento("Tipo de rendimento (ex: PREFIXADO, IPCA, SELIC)");
                LocalDate venc = infoUtils.lerData("Data de vencimento (yyyy-MM-dd)");
                ativo = new Tesouro(nome, ticker, preco, restrito, tr, venc);
            }
            case 4 -> { // Criptomoeda(String nome, String ticker, BigDecimal precoAtual, boolean restritoQualificados, String algoritmoConsenso, BigDecimal quantidadeMaxima, BigDecimal fatorConversao)
                String consenso = infoUtils.lerTexto("Algoritmo de consenso");
                BigDecimal quantidadeMax = infoUtils.lerBigDecimalOpcional("Quantidade máxima (enter para nenhum)");
                BigDecimal fatorConv = infoUtils.lerBigDecimal("Fator de conversão");
                ativo = new Criptomoeda(nome, ticker, preco, restrito, consenso, quantidadeMax, fatorConv);
            }
            case 5 -> { // Stock(String nome, String ticker, BigDecimal precoAtual, boolean restritoQualificados, String bolsaNegociacao, String setor, BigDecimal fatorConversao)
                String bolsa = infoUtils.lerTexto("Bolsa de negociação");
                String setor = infoUtils.lerTexto("Setor");
                BigDecimal fatorConv = infoUtils.lerBigDecimal("Fator de conversão");
                ativo = new Stock(nome, ticker, preco, restrito, bolsa, setor, fatorConv);
            }
            default -> {
                System.out.println("Tipo inválido.");
                return;
            }
        }

        ativoManager.cadastrarAtivo(ativo);
        System.out.println("Ativo cadastrado: " + ativo);
    }

    private void cadastrarAtivoEmLote() {
        System.out.println("\n===== CADASTRAR ATIVOS EM LOTE =====");
        String caminho = infoUtils.lerTexto("Informe o caminho do arquivo CSV");
        try {
            List<Ativo> novos = AtivoData.carregarPorArquivoGenerico(caminho);
            ativoManager.cadastrarEmLote(novos);
            System.out.println("Ativos carregados com sucesso.");
        } catch (Exception e) {
            System.out.println("Erro ao carregar ativos em lote: " + e.getMessage());
        }
    }

    private void editarAtivo() {
        List<Ativo> lista = ativoManager.getAtivos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum ativo disponível.");
            return;
        }
        for (int i = 0; i < lista.size(); i++) {
            System.out.println((i + 1) + " - " + lista.get(i));
        }
        int escolha = inputUtils.lerOpcao(1, lista.size());
        BigDecimal novoPreco = infoUtils.lerPreco();
        if (novoPreco == null || novoPreco.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Preço inválido.");
            return;
        }
        ativoManager.editarAtivo(escolha - 1, novoPreco);
        System.out.println("Preço atualizado para: R$ " + novoPreco);
    }

    private void excluirAtivo() {
        List<Ativo> lista = ativoManager.getAtivos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum ativo disponível.");
            return;
        }
        for (int i = 0; i < lista.size(); i++) {
            System.out.println((i + 1) + " - " + lista.get(i));
        }
        int escolha = inputUtils.lerOpcao(1, lista.size());
        Ativo alvo = lista.get(escolha - 1);
        // propagar remoção nas carteiras
        investidorManager.removerAtivoDeTodasCarteiras(alvo);
        // remover do manager
        ativoManager.excluirAtivo(escolha - 1);
        System.out.println("Ativo excluído: " + alvo.getTicker());
    }

    private void relatorioAtivos() {
        System.out.println("\n===== RELATÓRIO DE ATIVOS =====");
        System.out.println("1 - Todos");
        System.out.println("2 - Apenas Ações");
        System.out.println("3 - Apenas FIIs");
        System.out.println("4 - Apenas Criptoativos");
        System.out.println("5 - Apenas Stocks");
        System.out.println("6 - Apenas Tesouro");
        int opcao = inputUtils.lerOpcao(1, 6);
        switch (opcao) {
            case 1 -> ativoManager.listarTodos();
            case 2 -> ativoManager.listarPorTipo(Acao.class);
            case 3 -> ativoManager.listarPorTipo(Fii.class);
            case 4 -> ativoManager.listarPorTipo(Criptomoeda.class);
            case 5 -> ativoManager.listarPorTipo(Stock.class);
            case 6 -> ativoManager.listarPorTipo(Tesouro.class);
        }
    }

    private void menuInvestidores() {
        boolean rodando = true;
        while (rodando) {
            System.out.println("\n===== MENU INVESTIDORES =====");
            System.out.println("1 - Cadastrar investidor");
            System.out.println("2 - Cadastrar investidor em lote");
            System.out.println("3 - Exibir todos investidores");
            System.out.println("4 - Excluir investidores por lista de CPFs/CNPJs");
            System.out.println("5 - Selecionar investidor por CPF/CNPJ");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opção: ");
            int opcao = inputUtils.lerOpcao(0, 5);
            switch (opcao) {
                case 1 -> cadastrarInvestidor();
                case 2 -> cadastrarInvestidorEmLote();
                case 3 -> investidorManager.getInvestidores().forEach(System.out::println);
                case 4 -> excluirInvestidoresPorLista();
                case 5 -> selecionarInvestidor();
                case 0 -> rodando = false;
            }
        }
    }

    private void menuInvestidorSelecionado(model.investidor.Investidor inv) {
        boolean rodando = true;
        while (rodando) {
            System.out.println("\n===== INVESTIDOR: " + inv.getIdentificador() + " - " + inv.getNome() + " =====");
            System.out.println("1 - Editar informações do investidor");
            System.out.println("2 - Excluir investidor");
            System.out.println("3 - Exibir ativos do investidor");
            System.out.println("4 - Exibir valor total gasto ");
            System.out.println("5 - Exibir valor total atual");
            System.out.println("6 - Exibir porcentagens renda fixa / variável");
            System.out.println("7 - Exibir porcentagens nacional / internacional");
            System.out.println("8 - Salvar relatório (JSON)");
            System.out.println("9 - Adicionar movimentação de compra");
            System.out.println("10 - Adicionar movimentação de venda");
            System.out.println("11 - Adicionar lote de movimentações");
            System.out.println("0 - Voltar");
            int opcao = inputUtils.lerOpcao(0, 11);

            switch (opcao) {
                case 1 -> editarInvestidor(inv);
                case 2 -> {
                    investidorManager.removerInvestidor(inv.getIdentificador());
                    System.out.println("Investidor excluído.");
                    rodando = false;
                }
                case 3 -> {
                    System.out.println("\n--- ATIVOS ---");
                    inv.getCarteira().exibirCarteiraDetalhada();
                }
                case 4 -> {
                    try {
                        System.out.println("Valor total gasto: R$ " + inv.getCarteira().getValorTotalGasto());
                    } catch (NoSuchMethodError | UnsupportedOperationException e) {
                        System.out.println("Relatório de valor gasto não disponível (Carteira não armazena preço médio).");
                    }
                }
                case 5 -> System.out.println("Valor total atual: R$ " + inv.getCarteira().getValorTotalAtual());
                case 6 -> {
                    System.out.println("Renda fixa: " + inv.getCarteira().percentualRendaFixa() + "%");
                    System.out.println("Renda variável: " + inv.getCarteira().percentualRendaVariavel() + "%");
                }
                case 7 -> {
                    System.out.println("Nacional: " + inv.getCarteira().percentualNacional() + "%");
                    System.out.println("Internacional: " + inv.getCarteira().percentualInternacional() + "%");
                }
                case 8 -> salvarRelatorioInvestidor(inv);
                case 9 -> {
                    // compra
                    List<model.ativo.Ativo> lista = ativoManager.getAtivos();
                    if (lista.isEmpty()) {
                        System.out.println("Nenhum ativo disponível.");
                        break;
                    }

                    // escolher ativo
                    for (int i = 0; i < lista.size(); i++) {
                        System.out.println((i + 1) + " - " + lista.get(i));
                    }
                    int escolha = inputUtils.lerOpcao(1, lista.size());
                    model.ativo.Ativo ativoEscolhido = lista.get(escolha - 1);

                    // ler quantidade (BigDecimal) e preço de execução (BigDecimal)
                    BigDecimal quantidade = inputUtils.lerBigDecimalPositivo("Quantidade (pode ser decimal, ex 5.2): ");
                    BigDecimal precoExec = inputUtils.lerBigDecimalPositivo("Preço de execução (na moeda do ativo): ");

                    // comprar via Investidor (aplica regras de perfil/qualificado)
                    try {
                        inv.comprar(ativoEscolhido, quantidade, precoExec);
                        System.out.println("Compra registrada.");
                    } catch (Exception e) {
                        System.out.println("Falha na compra: " + e.getMessage());
                    }
                }

                case 10 -> {
                    // venda
                    var carteira = inv.getCarteira();
                    var ativosMap = carteira.getAtivos();

                    if (ativosMap.isEmpty()) {
                        System.out.println("Carteira vazia. Nada para vender.");
                        break;
                    }

                    // listar ativos da carteira
                    java.util.List<model.ativo.Ativo> lista = new java.util.ArrayList<>(ativosMap.keySet());
                    for (int i = 0; i < lista.size(); i++) {
                        model.ativo.Ativo a = lista.get(i);
                        java.math.BigDecimal qtdAtual = ativosMap.get(a);
                        System.out.println((i + 1) + " - " + a + " | Quantidade na carteira: " + qtdAtual);
                    }

                    // escolher ativo
                    int escolha = inputUtils.lerOpcao(1, lista.size());
                    model.ativo.Ativo ativoEscolhido = lista.get(escolha - 1);

                    // ler quantidade para vender (BigDecimal)
                    java.math.BigDecimal quantidade = inputUtils.lerBigDecimalPositivo("Quantidade para vender (pode ser decimal): ");

                    // vender via Investidor (não chama carteira direto)
                    try {
                        inv.vender(ativoEscolhido, quantidade);
                        System.out.println("Venda registrada.");
                    } catch (Exception e) {
                        System.out.println("Falha na venda: " + e.getMessage());
                    }
                }

                case 11 -> {
                    String caminho = infoUtils.lerTexto("Informe o caminho do arquivo CSV de movimentações");
                    try {
                        List<String[]> linhas = utils.CsvReader.lerCsv(caminho);
                        if (linhas == null || linhas.isEmpty()) {
                            System.out.println("Arquivo vazio ou não encontrado.");
                            break;
                        }

                        int ok = 0;
                        int erro = 0;

                        for (int idx = 0; idx < linhas.size(); idx++) {
                            String[] cols = linhas.get(idx);

                            try {
                                String tipo = cols.length > 0 ? cols[0].trim().toUpperCase() : "";
                                String ticker = cols.length > 1 ? cols[1].trim() : "";
                                String qtdStr = cols.length > 2 ? cols[2].trim() : "";
                                String precoStr = cols.length > 3 ? cols[3].trim() : "";

                                if (ticker.isBlank()) throw new IllegalArgumentException("Ticker vazio.");
                                if (qtdStr.isBlank()) throw new IllegalArgumentException("Quantidade vazia.");

                                BigDecimal quantidade = new BigDecimal(qtdStr.replace(",", "."));
                                if (quantidade.compareTo(BigDecimal.ZERO) <= 0)
                                    throw new IllegalArgumentException("Quantidade deve ser > 0.");

                                // procurar ativo pelo ticker no AtivoManager
                                model.ativo.Ativo ativoAlvo = null;
                                for (model.ativo.Ativo a : ativoManager.getAtivos()) {
                                    if (a.getTicker().equalsIgnoreCase(ticker)) {
                                        ativoAlvo = a;
                                        break;
                                    }
                                }
                                if (ativoAlvo == null) {
                                    throw new IllegalArgumentException("Ativo não encontrado no sistema: " + ticker);
                                }

                                // preço de execução: se vier vazio, usa o preço atual
                                BigDecimal precoExec = null;
                                if (!precoStr.isBlank()) {
                                    precoExec = new BigDecimal(precoStr.replace(",", "."));
                                    if (precoExec.compareTo(BigDecimal.ZERO) <= 0)
                                        throw new IllegalArgumentException("Preço de execução deve ser > 0.");
                                } else {
                                    precoExec = ativoAlvo.getPrecoAtual();
                                }

                                if ("C".equals(tipo)) {
                                    inv.comprar(ativoAlvo, quantidade, precoExec);
                                } else if ("V".equals(tipo)) {
                                    inv.vender(ativoAlvo, quantidade);
                                } else {
                                    throw new IllegalArgumentException("Tipo inválido (use C ou V): " + tipo);
                                }

                                ok++;
                            } catch (Exception e) {
                                erro++;
                                System.out.println("Linha " + (idx + 1) + " ignorada: " + e.getMessage());
                            }
                        }

                        System.out.println("Movimentações processadas. Sucesso: " + ok + " | Erros: " + erro);

                    } catch (Exception e) {
                        System.out.println("Erro ao carregar movimentações: " + e.getMessage());
                    }
                }
                case 0 -> rodando = false;
            }
        }
    }

    private void cadastrarInvestidor() {
        System.out.println("\n===== CADASTRAR INVESTIDOR =====");
        System.out.println("1 - Pessoa Física");
        System.out.println("2 - Institucional");
        int tipo = inputUtils.lerOpcao(1, 2);

        if (tipo == 1) {
            String nome = infoUtils.lerNome("Nome");
            String cpf = infoUtils.lerCPF();
            String telefone = infoUtils.lerTelefone();
            java.time.LocalDate data = infoUtils.lerData("Data de nascimento (yyyy-MM-dd)");
            var endereco = infoUtils.lerEndereco();
            java.math.BigDecimal patrimonio = infoUtils.lerPatrimonio();
            System.out.println("Perfil: 1 - Conservador | 2 - Moderado | 3 - Arrojado");
            int perfilOpcao = inputUtils.lerOpcao(1, 3);
            model.investidor.PerfilInvestimento perfil = perfilOpcao == 1
                    ? model.investidor.PerfilInvestimento.CONSERVADOR
                    : perfilOpcao == 2
                    ? model.investidor.PerfilInvestimento.MODERADO
                    : model.investidor.PerfilInvestimento.ARROJADO;
            model.investidor.Investidor inv = new model.investidor.PessoaFisica(nome, cpf, data, telefone, endereco, patrimonio, perfil);
            investidorManager.adicionarInvestidor(inv);
            System.out.println("Pessoa Física cadastrada: " + inv.getIdentificador());
        } else {
            String nome = infoUtils.lerNome("Nome Fantasia");
            String cnpj = infoUtils.lerCNPJ();
            String telefone = infoUtils.lerTelefone();
            java.time.LocalDate data = infoUtils.lerData("Data de fundação (yyyy-MM-dd)");
            var endereco = infoUtils.lerEndereco();
            java.math.BigDecimal patrimonio = infoUtils.lerPatrimonio();
            String razaoSocial = infoUtils.lerRazaoSocial();
            model.investidor.Investidor inv = new model.investidor.Institucional(nome, cnpj, data, telefone, endereco, patrimonio, razaoSocial);
            investidorManager.adicionarInvestidor(inv);
            System.out.println("Instituição cadastrada: " + inv.getIdentificador());
        }
    }

    private void cadastrarInvestidorEmLote() {
        System.out.println("\n===== CADASTRAR INVESTIDORES EM LOTE =====");
        String caminho = infoUtils.lerTexto("Informe o caminho do arquivo CSV");
        try {
            investidorManager.carregarInvestidoresDeArquivo(caminho);
            System.out.println("Investidores carregados com sucesso.");
        } catch (UnsupportedOperationException u) {
            System.out.println("Funcionalidade não implementada: " + u.getMessage());
        } catch (Exception e) {
            System.out.println("Erro ao carregar investidores: " + e.getMessage());
        }
    }

    private void excluirInvestidoresPorLista() {
        System.out.println("\n===== EXCLUIR INVESTIDORES =====");
        String entrada = infoUtils.lerTexto("Informe CPFs/CNPJs separados por vírgula");
        if (entrada == null || entrada.isBlank()) {
            System.out.println("Nenhum identificador informado.");
            return;
        }
        String[] ids = entrada.split(",");
        java.util.List<String> trimIds = java.util.Arrays.stream(ids)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        investidorManager.removerPorIdentificadores(trimIds);
        System.out.println("Operação concluída. Investidores removidos (se existiam).");
    }

    private void selecionarInvestidor() {
        String id = infoUtils.lerTexto("Informe CPF ou CNPJ do investidor");
        if (id == null || id.isBlank()) {
            System.out.println("Identificador vazio.");
            return;
        }
        model.investidor.Investidor inv = investidorManager.buscarPorIdentificador(id);
        if (inv == null) {
            System.out.println("Investidor não encontrado para: " + id);
            return;
        }
        menuInvestidorSelecionado(inv);
    }

    private void editarInvestidor(model.investidor.Investidor inv) {
        String novoNome = infoUtils.lerNome("Novo nome (enter para manter)");
        java.math.BigDecimal novoPatrimonio = infoUtils.lerBigDecimalOpcional("Novo patrimônio (vazio para manter)");

        if ((novoNome == null || novoNome.isBlank()) && novoPatrimonio == null) {
            System.out.println("Nada a alterar.");
            return;
        }

        String nomeParaAtualizar = (novoNome == null || novoNome.isBlank()) ? inv.getNome() : novoNome;
        java.math.BigDecimal patrimonioParaAtualizar = (novoPatrimonio == null) ? inv.getPatrimonio() : novoPatrimonio;

        try {
            investidorManager.atualizarInvestidor(inv.getIdentificador(), nomeParaAtualizar, patrimonioParaAtualizar);
            System.out.println("Investidor atualizado com sucesso.");
        } catch (Exception e) {
            System.out.println("Erro ao atualizar investidor: " + e.getMessage());
        }
    }

    private void comprarAtivoParaInvestidor(Investidor inv) {
        List<Ativo> lista = ativoManager.getAtivos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum ativo disponível.");
            return;
        }
        for (int i = 0; i < lista.size(); i++) System.out.println((i + 1) + " - " + lista.get(i));
        int escolha = inputUtils.lerOpcao(1, lista.size());
        Ativo ativo = lista.get(escolha - 1);

        BigDecimal quantidade = inputUtils.lerBigDecimalPositivo("Quantidade (pode ser decimal): ");
        BigDecimal precoExec = inputUtils.lerBigDecimalPositivo("Preço de execução: ");

        try {
            inv.comprar(ativo, quantidade, precoExec);
            System.out.println("Compra registrada.");
        } catch (Exception e) {
            System.out.println("Falha na compra: " + e.getMessage());
        }
    }


    private void venderAtivoDoInvestidor(Investidor inv) {
        var carteira = inv.getCarteira();
        var ativosMap = carteira.getAtivos();
        if (ativosMap.isEmpty()) {
            System.out.println("Carteira vazia.");
            return;
        }

        java.util.List<Ativo> lista = new java.util.ArrayList<>(ativosMap.keySet());
        for (int i = 0; i < lista.size(); i++) {
            Ativo a = lista.get(i);
            System.out.println((i + 1) + " - " + a + " | Quantidade: " + ativosMap.get(a));
        }

        int escolha = inputUtils.lerOpcao(1, lista.size());
        Ativo ativo = lista.get(escolha - 1);

        BigDecimal quantidade = inputUtils.lerBigDecimalPositivo("Quantidade para vender: ");

        try {
            inv.vender(ativo, quantidade);
            System.out.println("Venda registrada.");
        } catch (Exception e) {
            System.out.println("Falha na venda: " + e.getMessage());
        }
    }


    private void salvarRelatorioInvestidor(model.investidor.Investidor inv) {
        System.out.println("\n===== SALVAR RELATÓRIO DO INVESTIDOR =====");
        String caminho = infoUtils.lerTexto("Informe o caminho do arquivo de saída (ex: C:/temp/relatorio.json)");
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"identificador\": \"").append(inv.getIdentificador()).append("\",\n");
        sb.append("  \"nome\": \"").append(inv.getNome()).append("\",\n");
        sb.append("  \"patrimonio\": ").append(inv.getPatrimonio()).append(",\n");
        sb.append("  \"carteira\": [\n");

        var mapa = inv.getCarteira().getAtivos(); // Map<Ativo, BigDecimal>
        int i = 0;
        for (var entry : mapa.entrySet()) {
            var ativo = entry.getKey();
            var qtd = entry.getValue();
            java.math.BigDecimal valorGasto = inv.getCarteira().getValorGastoPorAtivo(ativo); // método abaixo
            java.math.BigDecimal valorAtual = ativo.converterParaReal().multiply(qtd);
            sb.append("    {\n");
            sb.append("      \"identificador\": \"").append(ativo.getTicker()).append("\",\n");
            sb.append("      \"nome\": \"").append(ativo.getNome()).append("\",\n");
            sb.append("      \"quantidade\": ").append(qtd).append(",\n");
            sb.append("      \"valorGasto\": ").append(valorGasto).append(",\n");
            sb.append("      \"valorAtual\": ").append(valorAtual).append("\n");
            sb.append("    }");
            i++;
            if (i < mapa.size()) sb.append(",");
            sb.append("\n");
        }

        sb.append("  ],\n");
        sb.append("  \"valorTotalGasto\": ").append(inv.getCarteira().getValorTotalGasto()).append(",\n");
        sb.append("  \"valorTotalAtual\": ").append(inv.getCarteira().getValorTotalAtual()).append("\n");
        sb.append("}\n");

        try (java.io.FileWriter fw = new java.io.FileWriter(caminho)) {
            fw.write(sb.toString());
            System.out.println("Relatório salvo em: " + caminho);
        } catch (Exception e) {
            System.out.println("Erro ao salvar relatório: " + e.getMessage());
        }
    }
}