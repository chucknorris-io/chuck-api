package io.chucknorris.lib;

import java.util.Date;
import org.springframework.stereotype.Service;

@Service
public class DateUtil {

  public Date now() {
    return new Date();
  }
}
