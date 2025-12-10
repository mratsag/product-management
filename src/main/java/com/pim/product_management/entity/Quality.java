package com.pim.product_management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quality")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quality extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    @Builder.Default
    private Integer score = 0;

    @Column(columnDefinition = "JSON")
    private String result;
}