package io.chucknorris.api.lib;

import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DateUtil {
    public Date now() {
        return new Date();
    }
}
