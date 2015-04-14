package org.surfnet.oaaas.model;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Wei Qiu <wei@qiu.es>
 */
public class File {
  MultipartFile file;

  public MultipartFile getFile() {
    return file;
  }

  public void setFile(MultipartFile file) {
    this.file = file;
  }
}
