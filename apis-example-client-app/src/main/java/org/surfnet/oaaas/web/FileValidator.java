package org.surfnet.oaaas.web;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.surfnet.oaaas.model.File;

/**
 * @author Wei Qiu <wei@qiu.es>
 */
public class FileValidator implements Validator {

  @Override
  public boolean supports(Class<?> paramClass) {
    return File.class.equals(paramClass);
  }

  @Override
  public void validate(Object obj, Errors errors) {
    File file = (File) obj;
    if (file.getFile().getSize() == 0) {
      errors.rejectValue("file", "valid.file");
    }
  }
}
