package app;

import data.AtivoData;
import model.*;          // importa todas as classes do package model
import exception.*;      // importa todas as exceções personalizadas
import utils.CsvReader;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Acao> acoes = AtivoData.carregarAcoes("acao.csv");
        List<Tesouro> tesouros = AtivoData.carregarTesouros("tesouro.csv");
        List<Stock> stocks = AtivoData.carregarStocks("stock.csv");
        List<Criptomoeda> criptos = AtivoData.carregarCriptos("criptoativo.csv");
        List<Fii> fiis = AtivoData.carregarFiis("fii.csv");

        System.out.println("Ações:");
        acoes.stream().limit(3).forEach(System.out::println);

        System.out.println("\nTesouros:");
        tesouros.stream().limit(3).forEach(System.out::println);

        System.out.println("\nStocks:");
        stocks.stream().limit(3).forEach(System.out::println);

        System.out.println("\nCriptomoedas:");
        criptos.stream().limit(3).forEach(System.out::println);

        System.out.println("\nFIIs:");
        fiis.stream().limit(3).forEach(System.out::println);

        try {
            // 1. Criar endereço
            Endereco endereco = new Endereco("Rua A", "123", "Centro", "36000-000", "Juiz de Fora", "MG");

            // 2. Criar investidores
            PessoaFisica pf = new PessoaFisica(
                    "Renato",
                    "123.456.789-00",
                    LocalDate.of(1995, 5, 20),
                    "32 99999-9999",
                    endereco,
                    new BigDecimal("10000"),
                    PerfilInvestimento.CONSERVADOR
            );

            Institucional inst = new Institucional(
                    "Banco XPTO",
                    "12.345.678/0001-99",
                    LocalDate.of(1980, 1, 1),
                    "11 3333-4444",
                    endereco,
                    new BigDecimal("1000000"),
                    "Banco XPTO S.A."
            );

            // 3. Criar ativos
            Acao petrobras = new Acao("Petrobras", "PETR4", new BigDecimal("30.50"), false);
            Tesouro tesouroSelic = new Tesouro("Tesouro Selic 2029", "TES2029", new BigDecimal("1000"), false, TipoRendimento.SELIC, LocalDate.of(2029, 3, 1));
            Stock apple = new Stock("Apple Inc.", "AAPL", new BigDecimal("180"), true, "NASDAQ", "Tecnologia", new BigDecimal("5.0"));

            // 4. Movimentações válidas
            Movimentacao compraPF = new Movimentacao(1, TipoMovimentacao.COMPRA, pf, petrobras, new BigDecimal("10"), petrobras.getPrecoAtual());
            compraPF.executar();

            Movimentacao compraInst = new Movimentacao(2, TipoMovimentacao.COMPRA, inst, apple, new BigDecimal("50"), apple.getPrecoAtual());
            compraInst.executar();

            // 5. Movimentação inválida (PF tentando comprar ativo restrito)
            try {
                Movimentacao compraRestrita = new Movimentacao(3, TipoMovimentacao.COMPRA, pf, apple, new BigDecimal("5"), apple.getPrecoAtual());
                compraRestrita.executar();
            } catch (InvestidorNaoQualificadoException e) {
                System.out.println("Exceção esperada: " + e.getMessage());
            }

            // 6. Mostrar dados e carteiras
            System.out.println("\nDados Pessoa Física:");
            System.out.println(pf);   // imprime os dados do investidor

            System.out.println("\nCarteira Pessoa Física:");
            System.out.println(pf.getCarteira());

            System.out.println("\nDados Institucional:");
            System.out.println(inst); // imprime os dados do investidor

            System.out.println("\nCarteira Institucional:");
            System.out.println(inst.getCarteira());


        } catch (Exception e) {
            System.out.println("Erro inesperado: " + e.getMessage());
        }
    }
}