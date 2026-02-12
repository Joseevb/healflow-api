package dev.jose.healflow_api.services;

import dev.jose.medicines.api.MedicinesApi;
import dev.jose.medicines.api.MedicinesApi.GetMedicinesRequest;
import dev.jose.medicines.api.StatisticsApi;
import dev.jose.medicines.model.FieldValuesResponseDTO;
import dev.jose.medicines.model.MedicineDTO;
import dev.jose.medicines.model.PaginatedMedicinesResponseDTO;
import dev.jose.medicines.model.StatsResponseDTO;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

@Service
@RequiredArgsConstructor
public class MedicineServiceImpl implements MedicineService {

  private final MedicinesApi medicinesApi;
  private final StatisticsApi statisticsApi;

  private static final int STARTING_PAGE = 1;
  private static final int PAGE_SIZE = 50;

  @Override
  public PaginatedMedicinesResponseDTO searchMedicines(
      @Nullable String search,
      @Nullable String category,
      @Nullable Integer page,
      @Nullable Map<String, String> fields) {

    var request =
        new GetMedicinesRequest()
            .pageSize(PAGE_SIZE)
            .page(Optional.ofNullable(page).filter(p -> p >= STARTING_PAGE).orElse(STARTING_PAGE));

    Optional.ofNullable(search).ifPresent(request::search);
    Optional.ofNullable(category).ifPresent(request::category);

    Optional.ofNullable(fields)
        .ifPresent(
            map ->
                map.forEach(
                    (key, value) ->
                        Optional.ofNullable(value)
                            .filter(v -> !v.isBlank())
                            .flatMap(
                                v ->
                                    Optional.ofNullable(
                                        ReflectionUtils.findMethod(
                                            GetMedicinesRequest.class, key, String.class)))
                            .ifPresent(
                                method -> ReflectionUtils.invokeMethod(method, request, value))));

    return medicinesApi.getMedicines(request);
  }

  @Override
  public MedicineDTO getMedicineById(Integer id) {
    return medicinesApi.getMedicinesById(id.toString());
  }

  @Override
  public StatsResponseDTO getStats() {
    return statisticsApi.getMedicinesStats();
  }

  @Override
  public FieldValuesResponseDTO getFieldValues(String fieldName) {
    return medicinesApi.getMedicinesFieldsByFieldName(fieldName);
  }
}
