package data;

import exception.AtivoInvalidoException;
import model.*;
import utils.CsvReader;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AtivoData {

    // Carregar Ações
    public static List<Acao> carregarAcoes(String caminho) {
        List<String[]> linhas = CsvReader.lerCsv(caminho);
        return linhas.stream().map(c -> {
            try {
                return new Acao(
                        c[1], // Nome
                        c[0], // Ticker
                        new BigDecimal(c[2].replace(",", ".")), // Preço
                        c[3].equals("1") // Qualificado?
                );
            } catch (AtivoInvalidoException e) {
                // Mostra a exceção mas não quebra o programa
                System.out.println("Erro ao criar ação: " + e.getMessage());
                return null; // retorna nulo para esse ativo
            }
        }).filter(a -> a != null).toList(); // remove os nulos da lista final
    }

    private static TipoRendimento parseTipoRendimento(String valor) {
        String v = valor.trim().toUpperCase();
        switch (v) {
            case "SELIC":
                return TipoRendimento.SELIC;
            case "PREFIXADO":
                return TipoRendimento.PREFIXADO;
            case "IPCA+":
                return TipoRendimento.IPCA_MAIS;
            default:
                throw new IllegalArgumentException("Tipo de rendimento inválido: " + valor);
        }
    }

    // Carregar Tesouros
    public static List<Tesouro> carregarTesouros(String caminho) {
        List<String[]> linhas = CsvReader.lerCsv(caminho);
        return linhas.stream().map(c -> {
            try {
                return new Tesouro(
                        c[1], // Nome
                        c[0], // Ticker
                        new BigDecimal(c[2].replace(",", ".")), // Preço
                        false, // Tesouro não é restrito
                        parseTipoRendimento(c[3]), // Tipo de rendimento
                        LocalDate.parse(c[4], DateTimeFormatter.ofPattern("dd/MM/yyyy")) // Vencimento
                );
            } catch (Exception e) {
                System.out.println("Erro ao criar Tesouro: " + e.getMessage());
                return null;
            }
        }).filter(t -> t != null).toList();
    }

    // Carregar Stocks (ações internacionais)
    public static List<Stock> carregarStocks(String caminho) {
        List<String[]> linhas = CsvReader.lerCsv(caminho);
        return linhas.stream().map(c ->
                new Stock(
                        c[1], // Nome
                        c[0], // Ticker
                        new BigDecimal(c[2].replace(",", ".")), // Preço em USD
                        false, // Não restrito
                        c[3], // Bolsa
                        c[4], // Setor
                        BigDecimal.valueOf(5.0) // Fator de conversão USD->BRL (ajuste conforme necessário)
                )
        ).toList();
    }

    // Carregar Criptomoedas
    public static List<Criptomoeda> carregarCriptos(String caminho) {
        List<String[]> linhas = CsvReader.lerCsv(caminho);
        return linhas.stream().map(c -> {
            try {
                BigDecimal quantidadeMaxima = null;
                if (c.length > 4 && !c[4].isBlank()) {
                    quantidadeMaxima = new BigDecimal(c[4]);
                }

                return new Criptomoeda(
                        c[1], // Nome
                        c[0], // Ticker
                        new BigDecimal(c[2].replace(",", ".")), // Preço em USD
                        false, // Não restrito
                        c[3], // Algoritmo Consenso
                        quantidadeMaxima, // pode ser null
                        BigDecimal.valueOf(5.0) // Fator de conversão USD->BRL
                );
            } catch (Exception e) {
                System.out.println("Erro ao criar Criptomoeda: " + e.getMessage());
                return null;
            }
        }).filter(c -> c != null).toList();
    }

    // Carregar FIIs
    public static List<Fii> carregarFiis(String caminho) {
        List<String[]> linhas = CsvReader.lerCsv(caminho);
        return linhas.stream().map(c -> {
            try {
                return new Fii(
                        c[1], // Nome
                        c[0], // Ticker
                        new BigDecimal(c[3].replace(",", ".").replace("-", "0")), // Preço
                        false, // Não restrito
                        c[2], // Setor
                        new BigDecimal(c[4].replace(",", ".").replace("-", "0")), // Último dividendo
                        new BigDecimal(c[5].replace(",", ".").replace("-", "0"))  // Taxa administração
                );
            } catch (Exception e) {
                System.out.println("Erro ao criar FII: " + e.getMessage());
                return null;
            }
        }).filter(f -> f != null).toList();
    }

}