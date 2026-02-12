package dev.jose.healflow_api.persistence.entities;

import dev.jose.healflow_api.enumerations.AppointmentStatus;
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
    name = "appointments",
    indexes = {
      @Index(name = "idx_appointment_date", columnList = "appointment_date"),
      @Index(name = "idx_appointment_status", columnList = "status"),
      @Index(name = "idx_client_id", columnList = "client_id"),
      @Index(name = "idx_specialist_id", columnList = "specialist_id")
    })
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "client_id", nullable = false)
  private UserEntity client;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "specialist_id", nullable = false)
  private SpecialistEntity specialist;

  @Column(name = "appointment_date", nullable = false)
  private Instant appointmentDate;

  @Column(name = "duration_minutes", nullable = false)
  private Short durationMinutes;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  @Builder.Default
  private AppointmentStatus status = AppointmentStatus.PENDING;

  @Column(name = "notes", columnDefinition = "TEXT")
  private String notes;

  @Column(name = "cancellation_reason", columnDefinition = "TEXT")
  private String cancellationReason;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;
}
