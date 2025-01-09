package com.back.sousa.helpers.messages;

import lombok.experimental.UtilityClass;

@UtilityClass
public class I18NKeys {
  @UtilityClass
  public static class UserMessage {
    public static final String NOT_FOUND = "account.not_found";
    public static final String HOUR_BLOCKED = "account.hour_blocked";
    public static final String PERMANENTLY_BLOCKED = "account.permanently_blocked";
    public static final String INVALID_CREDENTIALS = "account.invalid_credentials";
    public static final String USER_BLOCKED = "account.user_blocked";
    public static final String EMAIL_NOT_VALIDATED = "account.email_not_validated";
    
  }
}
