package com.lucas.taskboard.service;

import com.lucas.taskboard.model.*;
import com.lucas.taskboard.repository.CardRepository;
import com.lucas.taskboard.repository.ColunaRepository;
import com.lucas.taskboard.repository.TaskCardRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskCardService {

    private static final Logger logger = LoggerFactory.getLogger(TaskCardService.class);

    private final TaskCardRepository taskCardRepository;
    private final ColunaRepository colunaRepository;
    private final CardRepository cardRepository;

    public void criarCard(Board board, String titulo, String descricao) {
        Optional<Coluna> colunaInicio = board.getColunas().stream()
                .filter(c -> c.getTipo() == TipoColuna.INICIO)
                .findFirst();

        if (colunaInicio.isEmpty()) {
            throw new RuntimeException("Coluna de início não encontrada.");
        }

        TaskCard card = TaskCard.builder()
                .titulo(titulo)
                .descricao(descricao)
                .dataCriacao(LocalDateTime.now())
                .bloqueado(false)
                .coluna(colunaInicio.get())
                .build();

        taskCardRepository.save(card);
    }

    public List<TaskCard> listarCardsDoBoard(Board board) {
        return taskCardRepository.findAll().stream()
                .filter(card -> card.getColuna().getBoard().getId().equals(board.getId()))
                .collect(Collectors.toList());
    }

    public void registrarSaidaColuna(TaskCard card) {
        Coluna colunaAtual = card.getColuna();
        LocalDateTime agora = LocalDateTime.now();

        card.getTemposEmColuna().stream()
                .filter(tempo -> tempo.getColuna().equals(colunaAtual) && tempo.getDataSaida() == null)
                .findFirst()
                .ifPresent(tempo -> tempo.setDataSaida(agora));
    }

    public void moverParaProximaColuna(TaskCard card) {
        if (card.isBloqueado()) {
            throw new RuntimeException("Card está bloqueado e não pode ser movido.");
        }

        Coluna colunaAtual = card.getColuna();
        Board board = colunaAtual.getBoard();

        List<Coluna> colunasOrdenadas = board.getColunas().stream()
                .sorted(Comparator.comparingInt(Coluna::getOrdem))
                .toList();

        int indexAtual = colunasOrdenadas.indexOf(colunaAtual);

        if (indexAtual == -1 || indexAtual == colunasOrdenadas.size() - 1) {
            throw new RuntimeException("Card já está na última coluna ou coluna não encontrada.");
        }

        Coluna proximaColuna = colunasOrdenadas.get(indexAtual + 1);
        LocalDateTime agora = LocalDateTime.now();

        TempoEmColuna novoTempo = TempoEmColuna.builder()
                .coluna(proximaColuna)
                .taskCard(card)
                .dataEntrada(agora)
                .build();

        card.getTemposEmColuna().add(novoTempo);
        card.setColuna(proximaColuna);

        taskCardRepository.save(card);

        logger.info("Card movido com sucesso para a coluna: {}", proximaColuna.getNome());
    }

    @Transactional
    public void moverParaProximaColuna(Long cardId) {
        TaskCard card = taskCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card não encontrado."));

        registrarSaidaColuna(card);
        moverParaProximaColuna(card);
    }

    public void cancelarCard(TaskCard card) {
        Board board = card.getColuna().getBoard();

        Optional<Coluna> cancelamento = board.getColunas().stream()
                .filter(c -> c.getTipo() == TipoColuna.CANCELAMENTO)
                .findFirst();

        if (cancelamento.isEmpty()) {
            logger.warn("Coluna de cancelamento não encontrada.");
            return;
        }

        card.setColuna(cancelamento.get());
        taskCardRepository.save(card);
        logger.info("Card movido para a coluna de cancelamento.");
    }

    public void bloquearCard(TaskCard card, String motivo) {
        if (card.isBloqueado()) {
            logger.warn("Card já está bloqueado.");
            return;
        }

        card.setBloqueado(true);
        card.setMotivoBloqueio(motivo);
        card.setDataBloqueio(LocalDateTime.now());
        taskCardRepository.save(card);
        logger.info("Card bloqueado com sucesso.");
    }

    public void desbloquearCard(TaskCard card, String motivo) {
        if (!card.isBloqueado()) {
            logger.warn("Esse card não está bloqueado.");
            return;
        }

        card.setBloqueado(false);
        card.setMotivoDesbloqueio(motivo);
        card.setDataDesbloqueio(LocalDateTime.now());
        taskCardRepository.save(card);
        logger.info("Card desbloqueado com sucesso.");
    }

    public void gerarRelatorioDeTempo(TaskCard card) {
        logger.info("📋 Relatório de tempo para o card: {}", card.getTitulo());
        logger.info("--------------------------------------------------");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (TempoEmColuna tempo : card.getTemposEmColuna()) {
            String nomeColuna = tempo.getColuna().getNome();
            LocalDateTime entrada = tempo.getDataEntrada();

            LocalDateTime saida = tempo.getDataSaida() != null ? tempo.getDataSaida() : LocalDateTime.now();

            Duration duracao = Duration.between(entrada, saida);
            long horas = duracao.toHours();
            long minutos = duracao.toMinutesPart();
            long segundos = duracao.toSecondsPart();

            logger.info("🟦 Coluna: {} | Entrada: {} | Saída: {} | Tempo: {:02}h {:02}m {:02}s",
                    String.format("%-15s", nomeColuna),
                    entrada.format(formatter),
                    saida.format(formatter),
                    horas, minutos, segundos
            );
        }
    }
}
