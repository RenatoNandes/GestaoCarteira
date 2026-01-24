package model;

public class Endereco {

    private final String rua;
    private final String numero;
    private final String bairro;
    private final String cep;
    private final String cidade;
    private final String estado;

    public Endereco(String rua, String numero, String bairro,
                    String cep, String cidade, String estado) {
        if (rua == null || rua.isBlank()) throw new IllegalArgumentException("Rua não pode ser nula ou vazia.");
        if (numero == null || numero.isBlank())
            throw new IllegalArgumentException("Número não pode ser nulo ou vazio.");
        if (bairro == null || bairro.isBlank())
            throw new IllegalArgumentException("Bairro não pode ser nulo ou vazio.");
        if (cep == null || cep.isBlank()) throw new IllegalArgumentException("CEP não pode ser nulo ou vazio.");
        if (cidade == null || cidade.isBlank())
            throw new IllegalArgumentException("Cidade não pode ser nula ou vazia.");
        if (estado == null || estado.isBlank())
            throw new IllegalArgumentException("Estado não pode ser nulo ou vazio.");

        this.rua = rua;
        this.numero = numero;
        this.bairro = bairro;
        this.cep = cep;
        this.cidade = cidade;
        this.estado = estado;
    }

    @Override
    public String toString() {
        return String.format("%s, %s - %s, %s - %s/%s", rua, numero, bairro, cep, cidade, estado);
    }

}