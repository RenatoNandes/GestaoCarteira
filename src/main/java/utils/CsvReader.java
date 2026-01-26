package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {

    public static List<String[]> lerCsv(String caminhoArquivo) {
        List<String[]> linhas = new ArrayList<>();

        if (caminhoArquivo == null || caminhoArquivo.isBlank()) {
            System.out.println("Caminho do arquivo CSV vazio.");
            return linhas;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            boolean primeiraLinha = true;

            while ((linha = br.readLine()) != null) {
                if (primeiraLinha) { // ignora cabeçalho
                    primeiraLinha = false;
                    continue;
                }
                if (linha.isBlank()) continue;

                String[] campos = linha.split(";");
                linhas.add(campos);
            }
        } catch (IOException e) {
            System.out.println("Não foi possível ler o CSV: " + caminhoArquivo + " (" + e.getMessage() + ")");
        }

        return linhas;
    }
}
