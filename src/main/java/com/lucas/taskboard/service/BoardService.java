package com.lucas.taskboard.service;

import com.lucas.taskboard.model.Board;
import com.lucas.taskboard.model.Coluna;
import com.lucas.taskboard.model.TipoColuna;
import com.lucas.taskboard.repository.BoardRepository;
import com.lucas.taskboard.repository.ColunaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final ColunaRepository colunaRepository;

    public Board criarBoardComColunasFixas(String nome) {
        Board board = new Board();
        board.setNome(nome);
        board.setColunas(new ArrayList<>());

        Coluna inicio = Coluna.builder()
                .nome("Início")
                .ordem(0)
                .tipo(TipoColuna.INICIO)
                .board(board)
                .build();

        Coluna pendente = Coluna.builder()
                .nome("Pendente")
                .ordem(1)
                .tipo(TipoColuna.PENDENTE)
                .board(board)
                .build();

        Coluna finalizacao = Coluna.builder()
                .nome("Final")
                .ordem(2)
                .tipo(TipoColuna.FINAL)
                .board(board)
                .build();


        Coluna cancelamento = Coluna.builder()
                .nome("Cancelamento")
                .ordem(3)
                .tipo(TipoColuna.CANCELAMENTO)
                .board(board)
                .build();

        board.getColunas().add(inicio);
        board.getColunas().add(pendente);
        board.getColunas().add(finalizacao);
        board.getColunas().add(cancelamento);

        return boardRepository.save(board);
    }

    @Transactional
    public List<Board> listarTodosBoards() {
        List<Board> boards = boardRepository.findAll();

        for (Board board : boards) {
            board.getColunas().sort(Comparator.comparingInt(Coluna::getOrdem));
        }

        return boards;
    }

    public void excluirBoard(Long id) {
        boardRepository.deleteById(id);
    }
}
