package model.investidor;

import model.carteira.Carteira;
import model.ativo.Ativo;

import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class Investidor {

    private final String nome;
    private final String identificador; // CPF ou CNPJ
    private final String telefone;
    private final LocalDate dataNascimento;
    private final Endereco endereco;
    private BigDecimal patrimonio;
    private final Carteira carteira;

    public Investidor(String nome, String identificador, LocalDate dataNascimento,
                      String telefone, Endereco endereco, BigDecimal patrimonio) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome não pode ser nulo ou vazio.");
        if (identificador == null || identificador.isBlank())
            throw new IllegalArgumentException("Identificador não pode ser nulo ou vazio.");
        if (dataNascimento == null) throw new IllegalArgumentException("Data de nascimento não pode ser nula.");
        if (telefone == null || telefone.isBlank())
            throw new IllegalArgumentException("Telefone não pode ser nulo ou vazio.");
        if (endereco == null) throw new IllegalArgumentException("Endereço não pode ser nulo.");
        if (patrimonio == null || patrimonio.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Patrimônio não pode ser negativo.");

        this.nome = nome;
        this.identificador = identificador;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.endereco = endereco;
        this.patrimonio = patrimonio;
        this.carteira = new Carteira();
    }

    public String getNome() {
        return nome;
    }

    public String getIdentificador() {
        return identificador;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public String getTelefone() {
        return telefone;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public BigDecimal getPatrimonio() {
        return patrimonio;
    }

    public Carteira getCarteira() {
        return carteira;
    }

    public void atualizarPatrimonio(BigDecimal novoValor) {
        if (novoValor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Patrimônio não pode ser negativo.");
        }
        this.patrimonio = novoValor;
    }

    public void comprar(Ativo ativo, BigDecimal quantidade, BigDecimal precoExecucao) {
        carteira.adicionarAtivo(ativo, quantidade, precoExecucao);
    }

    public void vender(Ativo ativo, BigDecimal quantidade) {
        carteira.removerAtivo(ativo, quantidade);
    }

    @Override
    public String toString() {
        return String.format("Investidor: %s (%s) - Nascimento: %s - Tel: %s - Endereço: %s - Patrimônio: R$ %s",
                nome, identificador, dataNascimento, telefone, endereco, patrimonio);
    }

}