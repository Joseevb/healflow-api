package dev.jose.healflow_api.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "user_medicines")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMedicinesEntity {

  @Data
  @Embeddable
  @NoArgsConstructor
  @AllArgsConstructor
  public static class UserMedicineId implements Serializable {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "medicine_id")
    private Integer medicineId;
  }

  @EmbeddedId private UserMedicineId id;

  @MapsId("userId")
  @ManyToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private UserEntity user;

  @Column(name = "start_date", nullable = false)
  private LocalDateTime startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDateTime endDate;

  @Column(name = "frequency", nullable = false)
  private String frequency;

  @Column(name = "dosage", nullable = false)
  private String dosage;
}
