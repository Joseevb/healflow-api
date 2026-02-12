package dev.jose.healflow_api.persistence.entities;

import dev.jose.healflow_api.enumerations.SpecialistTypeEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Table(name = "specialists")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialistEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "first_name", nullable = false, length = 100)
  private String firstName;

  @Column(name = "last_name", nullable = false, length = 100)
  private String lastName;

  @Column(name = "email", nullable = false, unique = true, length = 255)
  private String email;

  @Column(name = "phone", length = 20)
  private String phone;

  @Column(name = "license_number", unique = true, length = 50)
  private String licenseNumber;

  @Column(name = "profile_picture_name")
  private String profilePictureName;

  @Column(name = "is_active", nullable = false)
  @Builder.Default
  private Boolean isActive = true;

  @Column(name = "consultation_duration_minutes", nullable = false)
  @Builder.Default
  private Short consultationDurationMinutes = 30;

  @Enumerated(EnumType.STRING)
  @Column(name = "specialty", nullable = false)
  private SpecialistTypeEnum specialty;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @OneToMany(mappedBy = "specialist", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<SpecialistAvailabilityEntity> availabilities = new ArrayList<>();

  @OneToMany(mappedBy = "specialist", cascade = CascadeType.ALL)
  @Builder.Default
  private List<AppointmentEntity> appointments = new ArrayList<>();

  public String getFullName() {
    return String.format("Dr. %s %s", firstName, lastName);
  }
}
