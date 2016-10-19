package com.txw.controller.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;

/**
 * Created by tangxiewen on 2016/10/18.
 */
public class SessionHelpUtils {
    public static HttpSession getSession(){
      HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
              .getRequest().getSession();
        return session;

    }
}
