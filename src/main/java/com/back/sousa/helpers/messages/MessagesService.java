package com.back.sousa.helpers.messages;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessagesService {

  private final MessageSource message;

  public String getMessage(String code, Object... args) {
    var locale = LocaleContextHolder.getLocale();
    try {
      return message.getMessage(code, args, locale);
    } catch (NoSuchMessageException exception) {
      return code;
    }
  }
}
