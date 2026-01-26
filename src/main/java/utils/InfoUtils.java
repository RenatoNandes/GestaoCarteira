package utils;

import model.ativo.TipoRendimento;
import model.investidor.Endereco;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InfoUtils {
    private final Scanner scanner;

    public InfoUtils(Scanner sc) {
        this.scanner = sc;
    }

    public String lerNome(String nome) {
        while (true) {
            System.out.print(nome + ": ");
            String entrada = scanner.nextLine().trim();
            if (!entrada.isEmpty()) {
                return entrada;
            }
            System.out.println("Entrada inválida. O campo não pode ser vazio.");
        }
    }

    // CPF (9 dígitos numéricos)
    public String lerCPF() {
        while (true) {
            System.out.print("CPF (9 dígitos): ");
            String entrada = scanner.nextLine().trim();
            if (entrada.matches("\\d{9}")) {
                return entrada;
            }
            System.out.println("CPF inválido. Digite exatamente 9 números.");
        }
    }

    // CNPJ (14 dígitos numéricos)
    public String lerCNPJ() {
        while (true) {
            System.out.print("CNPJ (14 dígitos): ");
            String entrada = scanner.nextLine().trim();
            if (entrada.matches("\\d{14}")) {
                return entrada;
            }
            System.out.println("CNPJ inválido. Digite exatamente 14 números.");
        }
    }

    // Telefone (11 dígitos: DDD + 9 + número)
    public String lerTelefone() {
        while (true) {
            System.out.print("Telefone (11 dígitos - DDD + 9 + número): ");
            String entrada = scanner.nextLine().trim();
            if (entrada.matches("\\d{11}") && entrada.charAt(2) == '9') {
                return entrada;
            }
            System.out.println("Telefone inválido. Deve ter 11 dígitos (DDD + 9 + número).");
        }
    }

    // Data com validação de formato yyyy-MM-dd
    public LocalDate lerData(String campo) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (true) {
            System.out.print(campo + ": ");
            String entrada = scanner.nextLine().trim();
            try {
                return LocalDate.parse(entrada, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida. Use o formato yyyy-MM-dd.");
            }
        }
    }

    public String lerRazaoSocial() {
        while (true) {
            System.out.print("Razão Social: ");
            String entrada = scanner.nextLine().trim();
            if (!entrada.isEmpty()) {
                return entrada;
            }
            System.out.println("Entrada inválida. O campo não pode ser vazio.");
        }
    }

    // Endereço completo com validações específicas
    public Endereco lerEndereco() {
        System.out.println("=== Endereço ===");
        String rua = lerNome("Rua");
        String numero;
        while (true) {
            System.out.print("Número: ");
            numero = scanner.nextLine().trim();
            if (!numero.isEmpty() && numero.matches(".*\\d.*")) { // precisa ter pelo menos um número
                break;
            }
            System.out.println("Número inválido. Deve conter ao menos um dígito (ex: 50 ou 50A).");
        }
        String bairro = lerNome("Bairro");

        String cep;
        while (true) {
            System.out.print("CEP (8 dígitos): ");
            cep = scanner.nextLine().trim();
            if (cep.matches("\\d{8}")) {
                break;
            }
            System.out.println("CEP inválido. Digite exatamente 8 números.");
        }

        String cidade = lerNome("Cidade");
        String estado = lerNome("Estado/UF");

        return new Endereco(rua, numero, bairro, cep, cidade, estado);
    }

    public BigDecimal lerPatrimonio() {
        while (true) {
            System.out.print("Patrimônio (R$): ");
            String entrada = scanner.nextLine().trim();
            try {
                BigDecimal valor = new BigDecimal(entrada);
                if (valor.compareTo(BigDecimal.ZERO) >= 0) {
                    return valor;
                }
                System.out.println("O patrimônio não pode ser negativo.");
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número válido.");
            }
        }
    }
    public String lerTicker() {
        System.out.print("Identificador (ticker): ");
        return scanner.nextLine().trim();
    }

    public BigDecimal lerPreco() {
        System.out.print("Preço (ex: 12.34): ");
        String s = scanner.nextLine().trim();
        if (s.isEmpty()) return null;
        return new BigDecimal(s);
    }

    public String lerTexto(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }

    public BigDecimal lerBigDecimal(String prompt) {
        System.out.print(prompt + ": ");
        String s = scanner.nextLine().trim();
        return new BigDecimal(s);
    }

    public BigDecimal lerBigDecimalOpcional(String prompt) {
        System.out.print(prompt + " (enter para pular): ");
        String s = scanner.nextLine().trim();
        return s.isEmpty() ? null : new BigDecimal(s);
    }

    public TipoRendimento lerTipoRendimento(String prompt) {
        System.out.print(prompt + " (DIGITE PREFIXADO/IPCA/SELIC): ");
        String s = scanner.nextLine().trim().toUpperCase();
        return TipoRendimento.valueOf(s); // trate IllegalArgumentException se quiser validação
    }

}