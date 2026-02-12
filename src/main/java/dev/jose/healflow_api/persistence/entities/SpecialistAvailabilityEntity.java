package dev.jose.healflow_api.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(
    name = "specialist_availabilities",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_specialist_day_time",
          columnNames = {"specialist_id", "day_of_week", "start_time"})
    })
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialistAvailabilityEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "specialist_id", nullable = false)
  private SpecialistEntity specialist;

  @Enumerated(EnumType.STRING)
  @Column(name = "day_of_week", nullable = false, length = 10)
  private DayOfWeek dayOfWeek;

  @Column(name = "start_time", nullable = false)
  private LocalTime startTime;

  @Column(name = "end_time", nullable = false)
  private LocalTime endTime;

  @Column(name = "is_available", nullable = false)
  @Builder.Default
  private Boolean isAvailable = true;
}
