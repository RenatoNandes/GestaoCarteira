package utils;

import java.util.Scanner;

public class InputUtils {

    private final Scanner scanner;

    public InputUtils(Scanner scanner) {
        this.scanner = scanner;
    }

    public int lerOpcao(int min, int max) {
        while (true) {
            try {
                String s = scanner.nextLine().trim();
                int v = Integer.parseInt(s);
                if (v >= min && v <= max) {
                    return v;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.print("Entrada inválida. Escolha entre " + min + " e " + max + ": ");
        }
    }
    public int lerQuantidade() {
        while (true) {
            try {
                int valor = Integer.parseInt(scanner.nextLine());
                if (valor <= 0) {
                    System.out.print("Quantidade deve ser maior que zero. Digite novamente: ");
                } else {
                    return valor;
                }
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Digite um número inteiro: ");
            }
        }
    }

    public boolean lerBoolean(String prompt) {
        System.out.print(prompt + " (s/n): ");
        String s = scanner.nextLine().trim().toLowerCase();
        return s.startsWith("s");
    }

}