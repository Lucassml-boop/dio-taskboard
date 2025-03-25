package com.lucas.taskboard.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TaskCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descricao;
    private LocalDateTime dataCriacao;

    private boolean bloqueado;
    private String motivoBloqueio;

    private LocalDateTime dataBloqueio;
    private String motivoDesbloqueio;
    private LocalDateTime dataDesbloqueio;

    @ManyToOne
    private Coluna coluna;

    @OneToMany(mappedBy = "taskCard", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<TempoEmColuna> temposEmColuna = new ArrayList<>();

    public void adicionarTempoEmColuna(TempoEmColuna tempoEmColuna) {
        temposEmColuna.add(tempoEmColuna);
    }
}
