package com.lucas.taskboard.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TempoEmColuna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "task_card_id")
    private TaskCard taskCard;

    @ManyToOne(optional = false)
    @JoinColumn(name = "coluna_id")
    private Coluna coluna;

    private LocalDateTime dataEntrada;

    private LocalDateTime dataSaida;
}
