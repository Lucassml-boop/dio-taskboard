package com.lucas.taskboard.menu;

import com.lucas.taskboard.model.Board;
import com.lucas.taskboard.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class MainMenu {

    private final BoardService boardService;
    private final BoardDetailMenu boardDetailMenu;

    private final Scanner scanner = new Scanner(System.in);

    public void exibirMenu() {
        while (true) {
            System.out.println("\n===== MENU PRINCIPAL =====");
            System.out.println("1. Criar novo board");
            System.out.println("2. Selecionar board");
            System.out.println("3. Excluir board");
            System.out.println("4. Sair");
            System.out.print("Escolha uma opção: ");

            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1" -> criarNovoBoard();
                case "2" -> selecionarBoard();
                case "3" -> excluirBoard();
                case "4" -> {
                    System.out.println("Saindo do sistema. Até logo!");
                    return;
                }
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private void criarNovoBoard() {
        System.out.print("Digite o nome do novo board: ");
        String nome = scanner.nextLine();
        Board novoBoard = boardService.criarBoardComColunasFixas(nome);
        System.out.println("Board criado com sucesso: " + novoBoard.getNome());
    }

    private void selecionarBoard() {
        List<Board> boards = boardService.listarTodosBoards();
        if (boards.isEmpty()) {
            System.out.println("Nenhum board disponível.");
            return;
        }

        System.out.println("\n=== Boards Disponíveis ===");
        for (int i = 0; i < boards.size(); i++) {
            System.out.println((i + 1) + ". " + boards.get(i).getNome());
        }

        System.out.print("Escolha o número do board: ");
        try {
            int indice = Integer.parseInt(scanner.nextLine()) - 1;
            if (indice >= 0 && indice < boards.size()) {
                Board boardSelecionado = boards.get(indice);
                boardDetailMenu.abrirMenuDoBoard(boardSelecionado);
            } else {
                System.out.println("Índice inválido.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Digite um número.");
        }
    }

    private void excluirBoard() {
        List<Board> boards = boardService.listarTodosBoards();
        if (boards.isEmpty()) {
            System.out.println("Nenhum board para excluir.");
            return;
        }

        System.out.println("\n=== Boards Disponíveis ===");
        for (int i = 0; i < boards.size(); i++) {
            System.out.println((i + 1) + ". " + boards.get(i).getNome());
        }

        System.out.print("Escolha o número do board a excluir: ");
        try {
            int indice = Integer.parseInt(scanner.nextLine()) - 1;
            if (indice >= 0 && indice < boards.size()) {
                boardService.excluirBoard(boards.get(indice).getId());
                System.out.println("Board excluído com sucesso.");
            } else {
                System.out.println("Índice inválido.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Digite um número.");
        }
    }
}
