package app;

import io.Menu;
import data.InvestidorManager;
import data.AtivoManager;
import utils.InputUtils;
import utils.InfoUtils;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // utilitários de entrada/infos
        InputUtils inputUtils = new InputUtils(sc);
        InfoUtils infoUtils = new InfoUtils(sc);

        // managers
        AtivoManager ativoManager = new AtivoManager();
        InvestidorManager investidorManager = new InvestidorManager();

        // cria o menu com as dependências
        Menu menu = new Menu(inputUtils, infoUtils, ativoManager, investidorManager);
        menu.exibirMenuPrincipal();

        sc.close();
    }
}