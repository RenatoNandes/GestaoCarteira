package data;

import exception.AtivoInvalidoException;
import model.ativo.*;
import utils.CsvReader;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;
import java.nio.file.Paths;

public class AtivoData {

    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static BigDecimal parseBigDecimalSafe(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty() || t.equals("-")) return null;
        try {
            t = t.replace(".", "").replace(",", "."); // trata milhares e decimais "1.234,56"
            return new BigDecimal(t);
        } catch (Exception e) {
            throw new IllegalArgumentException("Número inválido: '" + s + "'");
        }
    }

    private static String safeGet(String[] cols, int idx) {
        if (cols == null || idx < 0 || idx >= cols.length) return "";
        return cols[idx] == null ? "" : cols[idx].trim();
    }

    private static LocalDate parseDataSafe(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        return LocalDate.parse(s.trim(), DT_FORMAT);
    }

    // Carrega Ações
    public static List<Acao> carregarAcoes(String caminho) {
        List<String[]> linhas = CsvReader.lerCsv(caminho);
        if (linhas == null) return Collections.emptyList();

        return linhas.stream().map(c -> {
            try {
                String ticker = safeGet(c, 0);
                String nome = safeGet(c, 1);
                BigDecimal preco = parseBigDecimalSafe(safeGet(c, 2));
                boolean qualificado = "1".equals(safeGet(c, 3));

                if (preco == null) {
                    System.out.println("Linha ignorada (preço inválido) em Ações: " + String.join(",", c));
                    return null;
                }

                return new Acao(nome, ticker, preco, qualificado);
            } catch (AtivoInvalidoException e) {
                System.out.println("Erro ao criar ação: " + e.getMessage());
                return null;
            } catch (Exception e) {
                System.out.println("Erro inesperado ao criar ação: " + e.getMessage());
                return null;
            }
        }).filter(a -> a != null).collect(Collectors.toList());
    }

    private static TipoRendimento parseTipoRendimento(String valor) {
        if (valor == null) throw new IllegalArgumentException("Tipo de rendimento nulo");
        String v = valor.trim().toUpperCase();
        switch (v) {
            case "SELIC":
                return TipoRendimento.SELIC;
            case "PREFIXADO":
                return TipoRendimento.PREFIXADO;
            case "IPCA+":
            case "IPCA":
                return TipoRendimento.IPCA_MAIS;
            default:
                throw new IllegalArgumentException("Tipo de rendimento inválido: " + valor);
        }
    }

    // Carrega Tesouros
    public static List<Tesouro> carregarTesouros(String caminho) {
        List<String[]> linhas = CsvReader.lerCsv(caminho);
        if (linhas == null) return Collections.emptyList();

        return linhas.stream().map(c -> {
            try {
                String ticker = safeGet(c, 0);
                String nome = safeGet(c, 1);
                BigDecimal preco = parseBigDecimalSafe(safeGet(c, 2));
                String tipoRendStr = safeGet(c, 3);
                String vencStr = safeGet(c, 4);

                if (preco == null) {
                    System.out.println("Linha ignorada (preço inválido) em Tesouros: " + String.join(",", c));
                    return null;
                }

                TipoRendimento tr = parseTipoRendimento(tipoRendStr);
                LocalDate venc = parseDataSafe(vencStr);
                return new Tesouro(nome, ticker, preco, false, tr, venc);
            } catch (Exception e) {
                System.out.println("Erro ao criar Tesouro: " + e.getMessage());
                return null;
            }
        }).filter(t -> t != null).collect(Collectors.toList());
    }

    // Carrega Stocks (ações internacionais)
    public static List<Stock> carregarStocks(String caminho) {
        List<String[]> linhas = CsvReader.lerCsv(caminho);
        if (linhas == null) return Collections.emptyList();

        return linhas.stream().map(c -> {
            try {
                String ticker = safeGet(c, 0);
                String nome = safeGet(c, 1);
                BigDecimal preco = parseBigDecimalSafe(safeGet(c, 2));
                String bolsa = safeGet(c, 3);
                String setor = safeGet(c, 4);
                BigDecimal fatorConv = parseBigDecimalSafe(safeGet(c, 5));
                if (fatorConv == null) fatorConv = BigDecimal.valueOf(5.0);

                if (preco == null) {
                    System.out.println("Linha ignorada (preço inválido) em Stocks: " + String.join(",", c));
                    return null;
                }

                return new Stock(nome, ticker, preco, false, bolsa, setor, fatorConv);
            } catch (Exception e) {
                System.out.println("Erro ao criar Stock: " + e.getMessage());
                return null;
            }
        }).filter(s -> s != null).collect(Collectors.toList());
    }

    // Carrega Criptomoedas
    public static List<Criptomoeda> carregarCriptos(String caminho) {
        List<String[]> linhas = CsvReader.lerCsv(caminho);
        if (linhas == null) return Collections.emptyList();

        return linhas.stream().map(c -> {
            try {
                String ticker = safeGet(c, 0);
                String nome = safeGet(c, 1);
                BigDecimal preco = parseBigDecimalSafe(safeGet(c, 2));
                String consenso = safeGet(c, 3);
                BigDecimal quantidadeMaxima = null;
                if (c.length > 4 && !safeGet(c, 4).isBlank()) {
                    quantidadeMaxima = parseBigDecimalSafe(safeGet(c, 4));
                }
                BigDecimal fatorConv = parseBigDecimalSafe(safeGet(c, 5));
                if (fatorConv == null) fatorConv = BigDecimal.valueOf(5.0);

                if (preco == null) {
                    System.out.println("Linha ignorada (preço inválido) em Criptos: " + String.join(",", c));
                    return null;
                }

                return new Criptomoeda(nome, ticker, preco, false, consenso, quantidadeMaxima, fatorConv);
            } catch (Exception e) {
                System.out.println("Erro ao criar Criptomoeda: " + e.getMessage());
                return null;
            }
        }).filter(x -> x != null).collect(Collectors.toList());
    }

    // Carrega FIIs
    public static List<Fii> carregarFiis(String caminho) {
        List<String[]> linhas = CsvReader.lerCsv(caminho);
        if (linhas == null) return Collections.emptyList();

        return linhas.stream().map(c -> {
            try {
                String ticker = safeGet(c, 0);
                String nome = safeGet(c, 1);
                String setor = safeGet(c, 2);
                BigDecimal preco = parseBigDecimalSafe(safeGet(c, 3));
                BigDecimal ultimoDiv = parseBigDecimalSafe(safeGet(c, 4));
                BigDecimal taxaAdm = parseBigDecimalSafe(safeGet(c, 5));

                if (preco == null) {
                    System.out.println("Linha ignorada (preço inválido) em FIIs: " + String.join(",", c));
                    return null;
                }

                if (ultimoDiv == null) ultimoDiv = BigDecimal.ZERO;
                if (taxaAdm == null) taxaAdm = BigDecimal.ZERO;

                return new Fii(nome, ticker, preco, false, setor, ultimoDiv, taxaAdm);
            } catch (Exception e) {
                System.out.println("Erro ao criar FII: " + e.getMessage());
                return null;
            }
        }).filter(f -> f != null).collect(Collectors.toList());
    }

    public static List<Ativo> carregarPorArquivoGenerico(String caminho) {
        if (caminho == null || caminho.isBlank()) return Collections.emptyList();
        String nome = Paths.get(caminho).getFileName().toString().toLowerCase();

        try {
            if (nome.contains("acao") || nome.contains("acoes")) {
                return carregarAcoes(caminho).stream().map(a -> (Ativo) a).collect(Collectors.toList());
            } else if (nome.contains("fii") || nome.contains("fiis")) {
                return carregarFiis(caminho).stream().map(f -> (Ativo) f).collect(Collectors.toList());
            } else if (nome.contains("tesouro")) {
                return carregarTesouros(caminho).stream().map(t -> (Ativo) t).collect(Collectors.toList());
            } else if (nome.contains("stock") || nome.contains("stocks")) {
                return carregarStocks(caminho).stream().map(s -> (Ativo) s).collect(Collectors.toList());
            } else if (nome.contains("cripto") || nome.contains("criptomoeda")) {
                return carregarCriptos(caminho).stream().map(c -> (Ativo) c).collect(Collectors.toList());
            } else {
                // tenta carregar como ações
                return carregarAcoes(caminho).stream().map(a -> (Ativo) a).collect(Collectors.toList());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar ativos do arquivo: " + e.getMessage(), e);
        }
    }
}