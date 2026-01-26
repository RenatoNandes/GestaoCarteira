package data;

import model.ativo.Ativo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AtivoManager {

    private final List<Ativo> ativos = new ArrayList<>();

    public AtivoManager() {
        // Carrega todos os ativos dos CSVs
        ativos.addAll(AtivoData.carregarAcoes("acao.csv"));
        ativos.addAll(AtivoData.carregarFiis("fii.csv"));
        ativos.addAll(AtivoData.carregarTesouros("tesouro.csv"));
        ativos.addAll(AtivoData.carregarStocks("stock.csv"));
        ativos.addAll(AtivoData.carregarCriptos("criptoativo.csv"));
    }

    public List<Ativo> getAtivos() {
        return ativos;
    }

    public void cadastrarAtivo(Ativo ativo) {
        ativos.add(ativo);
    }

    public void cadastrarEmLote(List<Ativo> novos) {
        ativos.addAll(novos);
    }

    public void editarAtivo(int indice, BigDecimal novoPreco) {
        Ativo ativo = ativos.get(indice);
        ativo.atualizarPreco(novoPreco);
    }

    public void excluirAtivo(int indice) {
        ativos.remove(indice);
    }

    // Relatórios
    public void listarTodos() {
        ativos.forEach(System.out::println);
    }

    public void listarPorTipo(Class<? extends Ativo> tipo) {
        ativos.stream()
                .filter(tipo::isInstance)
                .forEach(System.out::println);
    }
}