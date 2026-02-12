package dev.jose.healflow_api.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Table(
    name = "health_scores",
    indexes = {
      @Index(name = "idx_health_score_user_id", columnList = "user_id"),
      @Index(name = "idx_health_score_calculated_at", columnList = "calculated_at DESC"),
      @Index(name = "idx_health_score_user_calculated", columnList = "user_id, calculated_at DESC")
    })
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class HealthScoreEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Column(name = "overall_score", nullable = false)
  @Builder.Default
  private Integer overallScore = 0;

  @Column(name = "cardiovascular_score")
  @Builder.Default
  private Integer cardiovascularScore = 0;

  @Column(name = "metabolic_score")
  @Builder.Default
  private Integer metabolicScore = 0;

  @Column(name = "lifestyle_score")
  @Builder.Default
  private Integer lifestyleScore = 0;

  @Column(name = "vital_signs_score")
  @Builder.Default
  private Integer vitalSignsScore = 0;

  @Column(name = "calculated_at", nullable = false)
  private Instant calculatedAt;

  @Column(name = "data_points_count", nullable = false)
  @Builder.Default
  private Integer dataPointsCount = 0;

  @Column(name = "period_days", nullable = false)
  @Builder.Default
  private Integer periodDays = 90;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;
}
