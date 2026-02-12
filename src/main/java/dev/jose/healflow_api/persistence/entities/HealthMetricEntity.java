package dev.jose.healflow_api.persistence.entities;

import dev.jose.healflow_api.enumerations.HealthMetricType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Table(
    name = "health_metrics",
    indexes = {
      @Index(name = "idx_health_metric_user_id", columnList = "user_id"),
      @Index(name = "idx_health_metric_type", columnList = "metric_type"),
      @Index(name = "idx_health_metric_recorded_at", columnList = "recorded_at"),
      @Index(name = "idx_health_metric_user_type", columnList = "user_id, metric_type"),
      @Index(name = "idx_health_metric_user_recorded", columnList = "user_id, recorded_at")
    })
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthMetricEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Enumerated(EnumType.STRING)
  @Column(name = "metric_type", nullable = false, length = 50)
  private HealthMetricType metricType;

  @Column(name = "metric_value", nullable = false, precision = 10, scale = 2)
  private BigDecimal value;

  @Column(name = "unit", nullable = false, length = 20)
  private String unit;

  @Column(name = "recorded_at", nullable = false)
  private Instant recordedAt;

  @Column(name = "notes", columnDefinition = "TEXT")
  private String notes;

  @Column(name = "source", length = 50)
  @Builder.Default
  private String source = "manual";

  @Column(name = "recorded_by_specialist_id")
  private UUID recordedBySpecialistId;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;
}
