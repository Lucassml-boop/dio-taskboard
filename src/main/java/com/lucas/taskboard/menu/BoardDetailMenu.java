package com.lucas.taskboard.menu;

import com.lucas.taskboard.model.Board;
import com.lucas.taskboard.model.TaskCard;
import com.lucas.taskboard.service.TaskCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class BoardDetailMenu {

    private final TaskCardService taskCardService;
    private final Scanner scanner = new Scanner(System.in);

    public void abrirMenuDoBoard(Board board) {
        while (true) {
            System.out.println("\n===== MENU DO BOARD: " + board.getNome() + " =====");
            System.out.println("1. Criar novo card");
            System.out.println("2. Mover card para próxima coluna");
            System.out.println("3. Cancelar card");
            System.out.println("4. Bloquear card");
            System.out.println("5. Desbloquear card");
            System.out.println("6. Voltar ao menu principal");
            System.out.println("7. Gerar relatório de tempo do card");
            System.out.print("Escolha uma opção: ");

            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1" -> criarCard(board);
                case "2" -> moverCard(board);
                case "3" -> cancelarCard(board);
                case "4" -> bloquearCard(board);
                case "5" -> desbloquearCard(board);
                case "6" -> {
                    System.out.println("Retornando ao menu principal...");
                    return;
                }
                case "7" -> gerarRelatorio(board);
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private void criarCard(Board board) {
        System.out.print("Digite o título do card: ");
        String titulo = scanner.nextLine();
        System.out.print("Digite a descrição do card: ");
        String descricao = scanner.nextLine();

        taskCardService.criarCard(board, titulo, descricao);
        System.out.println("Card criado com sucesso!");
    }

    private void moverCard(Board board) {
        List<TaskCard> cards = taskCardService.listarCardsDoBoard(board);
        TaskCard card = escolherCard(cards);
        if (card == null) return;

        if (card.isBloqueado()) {
            System.out.println("Esse card está bloqueado e não pode ser movido.");
            return;
        }

        taskCardService.moverParaProximaColuna(card);
        System.out.println("Card movido com sucesso para a próxima coluna!");
    }

    private void cancelarCard(Board board) {
        List<TaskCard> cards = taskCardService.listarCardsDoBoard(board);
        TaskCard card = escolherCard(cards);
        if (card == null) return;

        taskCardService.cancelarCard(card);
    }

    private void bloquearCard(Board board) {
        List<TaskCard> cards = taskCardService.listarCardsDoBoard(board);
        TaskCard card = escolherCard(cards);
        if (card == null) return;

        System.out.print("Digite o motivo do bloqueio: ");
        String motivo = scanner.nextLine();
        taskCardService.bloquearCard(card, motivo);
    }

    private void desbloquearCard(Board board) {
        List<TaskCard> cards = taskCardService.listarCardsDoBoard(board);
        TaskCard card = escolherCard(cards);
        if (card == null) return;

        System.out.print("Digite o motivo do desbloqueio: ");
        String motivo = scanner.nextLine();
        taskCardService.desbloquearCard(card, motivo);
    }

    private void gerarRelatorio(Board board) {
        List<TaskCard> cards = taskCardService.listarCardsDoBoard(board);
        TaskCard card = escolherCard(cards);
        if (card == null) return;

        taskCardService.gerarRelatorioDeTempo(card);
    }

    private TaskCard escolherCard(List<TaskCard> cards) {
        if (cards.isEmpty()) {
            System.out.println("Nenhum card disponível.");
            return null;
        }

        System.out.println("\n=== Cards Disponíveis ===");
        for (int i = 0; i < cards.size(); i++) {
            TaskCard c = cards.get(i);
            System.out.printf("%d. [%s] %s (Coluna: %s, Bloqueado: %s)%n",
                    i + 1,
                    c.getId(),
                    c.getTitulo(),
                    c.getColuna().getNome(),
                    c.isBloqueado() ? "Sim" : "Não"
            );
        }

        System.out.print("Escolha o número do card: ");
        try {
            int indice = Integer.parseInt(scanner.nextLine()) - 1;
            if (indice >= 0 && indice < cards.size()) {
                return cards.get(indice);
            } else {
                System.out.println("Índice inválido.");
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Digite um número.");
            return null;
        }
    }
}
