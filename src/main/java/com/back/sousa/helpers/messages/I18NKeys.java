package com.back.sousa.helpers.messages;

import lombok.experimental.UtilityClass;

@UtilityClass
public class I18NKeys {
  @UtilityClass
  public static class UserMessage {
    public static final String NOT_FOUND = "store.not_found";
    public static final String HOUR_BLOCKED = "store.hour_blocked";
    public static final String PERMANENTLY_BLOCKED = "store.permanently_blocked";
    public static final String INVALID_CREDENTIALS = "store.invalid_credentials";
  }
}
