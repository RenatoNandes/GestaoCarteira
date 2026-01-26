package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {

    public static List<String[]> lerCsv(String caminhoArquivo) {
        if (caminhoArquivo == null || caminhoArquivo.isBlank()) {
            throw new IllegalArgumentException("Caminho do arquivo não pode ser vazio.");
        }

        List<String[]> linhas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            boolean primeiraLinha = true;

            while ((linha = br.readLine()) != null) {
                if (primeiraLinha) { // ignora cabeçalho
                    primeiraLinha = false;
                    continue;
                }
                // ignora linhas vazias
                if (linha.trim().isEmpty()) continue;

                String[] campos = linha.split(";");
                linhas.add(campos);
            }

            return linhas;

        } catch (IOException e) {
            throw new RuntimeException("Não foi possível ler o CSV: arquivo não encontrado (" + caminhoArquivo + ").");
        }
    }
}
