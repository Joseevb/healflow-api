package dev.jose.healflow_api.config;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ApiPaths {

  public static final String PREFIX = "${api.prefix}";
  public static final String VERSION = "${api.version}";
  public static final String BASE_PATH = PREFIX + "/" + VERSION;
}
