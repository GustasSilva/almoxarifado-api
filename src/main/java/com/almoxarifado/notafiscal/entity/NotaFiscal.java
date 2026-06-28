package com.almoxarifado.notafiscal.entity;

import com.almoxarifado.movimentacao.entity.TipoMovimentacao;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notas_fiscais")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotaFiscal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 9)
    private String numero;

    @Column(nullable = false, length = 3)
    @Builder.Default
    private String serie = "001";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoMovimentacao tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatusNotaFiscal status = StatusNotaFiscal.EMITIDA;

    @Column(name = "emitente_razao_social", nullable = false, length = 150)
    private String emitenteRazaoSocial;

    @Column(name = "emitente_cnpj", nullable = false, length = 20)
    private String emitenteCnpj;

    @Column(name = "emitente_endereco", nullable = false, length = 255)
    private String emitenteEndereco;

    @Column(name = "destinatario_nome", nullable = false, length = 150)
    private String destinatarioNome;

    @Column(name = "destinatario_cnpj_cpf", length = 20)
    private String destinatarioCnpjCpf;

    @Column(name = "destinatario_endereco", length = 255)
    private String destinatarioEndereco;

    @Column(name = "data_emissao", nullable = false)
    private LocalDateTime dataEmissao;

    @Column(name = "valor_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorTotal;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column(name = "pdf_path", length = 500)
    private String pdfPath;

    @OneToMany(mappedBy = "notaFiscal", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<NotaFiscalItem> itens = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;
}
