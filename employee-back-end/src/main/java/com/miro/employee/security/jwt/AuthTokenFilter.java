package com.miro.employee.security.jwt;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.miro.employee.security.service.UserDetailsServiceImpl;

public class AuthTokenFilter extends OncePerRequestFilter {
  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  // print Info to console
  private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {

      // printRequestInfo(request);

      // Cookie[] cookies = request.getCookies();
      // getCookieString(cookies).intern();

      // System.out.println("################## " +
      // getCookieString(cookies).split(","));

      // Проверявам бисквитата от браузъра и от АПИ,
      // Ако Една от 2 те е ОК, потребителя ще се индетифицира и заявката ще мине
      String jwt = null;
      String jwtCookie = parseJwtFromCookie(request).get("jwt");
      
      String jwtAPI = parseJwt(request);
      if (jwtCookie != null) {
        jwt = jwtCookie;
      } else {
        jwt = jwtAPI;
      }
      ;

      if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
        String username = jwtUtils.getUserNameFromJwtToken(jwt);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception e) {
      logger.error("Cannot set user authentication: {}");
    }

    filterChain.doFilter(request, response);

    // printResponseInfo(response);
  }

  private String parseJwt(HttpServletRequest request) {

    String headerAuth = request.getHeader("Authorization");

    // System.out.println("-------> AuthTokenFilter --->headerAuth " + headerAuth);

    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      return headerAuth.substring(7);
    }

    return null;
  }

  private Map<String, String> parseJwtFromCookie(HttpServletRequest request) {
    Map<String, String> parsedCookie = new HashMap<>();
    Cookie[] cookies = request.getCookies();

    if (cookies == null)
      return null;

    for (Cookie cookie : cookies) {
      // System.out.println(cookie.getName() + " : " + cookie.getValue());
      logger.info("parsed cookie: " + cookie.getName() + " : " + cookie.getValue());
      parsedCookie.put(cookie.getName(), cookie.getValue());

    }
    if(parsedCookie != null){
    parsedCookie.forEach(
        (key, value) -> {
          System.out.println(key + "for each " + value);
        });
        }
    return parsedCookie;

  }

  // Show all header Key
  private void getAllHeaderKey(HttpServletRequest request) {
    HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    Collections.list(req.getHeaderNames()).stream().forEach(System.out::println);

  }

  // Print All Request info - very nice
  private void printRequestInfo(HttpServletRequest req) {
    System.out.println("---------------------------------------------------");
    System.out.println("Client Request Header");

    StringBuffer requestURL = req.getRequestURL();
    String queryString = req.getQueryString();

    if (queryString == null) {
      logger.info("url: " + requestURL.toString());
    } else {
      logger.info("url: " + requestURL.append('?').append(queryString).toString());
    }

    logger.info("method:" + req.getMethod());

    // print all the headers
    Enumeration headerNames = req.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = (String) headerNames.nextElement();
      logger.info(" request header: " + headerName + ":" + req.getHeader(headerName));
    }

    // print all the request params
    Enumeration params = req.getParameterNames();
    while (params.hasMoreElements()) {
      String paramName = (String) params.nextElement();
      logger.info("Attribute: '" + paramName + "', Value: '" + req.getParameter(paramName) + "'");
    }
  }

  // Print All Response info - very nice
  private void printResponseInfo(HttpServletResponse res) {

    System.out.println("-------------------------------------------------");
    System.out.println("Server Response Header");

    Collection<String> names = res.getHeaderNames();
    if (names == null) {
      return;
    }
    Iterator namesIterator = names.iterator();
    while (namesIterator.hasNext()) {
      String name = (String) namesIterator.next();
      Collection<String> values = res.getHeaders(name);
      if (values != null) {
        Iterator valuesIterator = values.iterator();
        while (valuesIterator.hasNext()) {
          String value = (String) valuesIterator.next();
          // System.out.println(name + " : " + value );
          logger.info("response header: " + name + ":" + value);
        }
      }
    }

    ContentCachingResponseWrapper resp = new ContentCachingResponseWrapper(res);
    byte[] responseBody = resp.getContentAsByteArray();
    logger.info("response body = {}", new String(responseBody, StandardCharsets.UTF_8));

  }

  // Print only Header value !
  private void getHeaderAll(HttpServletRequest request) {
    Enumeration<String> headerNames = request.getHeaderNames();

    if (headerNames != null) {
      while (headerNames.hasMoreElements()) {
        System.out.println("Header: " + " : " + request.getHeader(headerNames.nextElement()));
      }
    }
  }

  private String getCookieString(Cookie[] cookies) {
    if (cookies == null)
      return "null";
    StringJoiner stringJoiner = new StringJoiner(",", "[", "]");
    for (Cookie cookie : cookies) {
      // System.out.println(cookie.getName() + " : " + cookie.getValue());
      stringJoiner.add(ReflectionToStringBuilder.toString(cookie));
    }
    return stringJoiner.toString();
  }

}

/*
 * Функцията чете целия инпут стреам и го принтира в конзолата, но като се
 * използва дава грешка
 * 
 * ServletInputStream mServletInputStream = request.getInputStream();
 * byte[] httpInData = new byte[request.getContentLength()];
 * int retVal = -1;
 * StringBuilder stringBuilder = new StringBuilder();
 * 
 * while ((retVal = mServletInputStream.read(httpInData)) != -1) {
 * for (int i = 0; i < retVal; i++) {
 * stringBuilder.append(Character.toString((char) httpInData[i]));
 * }
 * }
 * 
 * System.out.println("------->  AuthTokenFilter  " + stringBuilder.toString())
 * ;
 */

/*
 * HttpServletRequest request1 = request;
 * byte[] httpInData = new byte[request1.getContentLength()];
 * int retVal = -1;
 * StringBuilder stringBuilder = new StringBuilder();
 * 
 * while ((retVal = request1.getInputStream().read(httpInData)) != -1) {
 * for (int i = 0; i < retVal; i++) {
 * stringBuilder.append(Character.toString((char) httpInData[i]));
 * }
 * }
 * System.out.println("------->  AuthTokenFilter  " + stringBuilder.toString())
 * ;
 */

/*
 * String marshalledXml =
 * org.apache.commons.io.IOUtils.toString(request.getInputStream());
 * 
 * Also keep in mind that you have to choose between request.getParameter(name)
 * and request.getInputStream. You can't use both.
 * 
 */
/*
 * System.out.println("------->  AuthTokenFilter  " + request.getHeaderNames());
 * System.out.println("------->  AuthTokenFilter  " + request.getAuthType());
 * System.out.println("------->  AuthTokenFilter  " + request.getRequestURI());
 * System.out.println("------->  AuthTokenFilter  " + request.getQueryString());
 * System.out.println("------->  AuthTokenFilter  " + request.getContextPath());
 * System.out.println("------->  AuthTokenFilter  " + request.getPathInfo());
 * System.out.println("------->  AuthTokenFilter  " + request.getProtocol());
 * System.out.println("------->  AuthTokenFilter  " +
 * request.getHttpServletMapping());
 * System.out.println("------->  AuthTokenFilter  " +
 * request.getParameterNames());
 * System.out.println("------->  AuthTokenFilter  " + request.getSession());
 * System.out.println("------->  AuthTokenFilter  " +
 * request.getHeader("Authorization"));
 * System.out.println("------->  AuthTokenFilter  " + request.getCookies());
 * System.out.println("------->  AuthTokenFilter  " +
 * request.getTrailerFields());
 * 
 * System.out.println("------->  AuthTokenFilter  " + request.getLocalAddr());
 * System.out.println("------->  AuthTokenFilter  " + request.getRemoteAddr());
 * System.out.println("------->  AuthTokenFilter  " + request.getServerName());
 * System.out.println("------->  AuthTokenFilter  " +
 * request.getAttribute("Cookie"));
 * System.out.println("------->  AuthTokenFilter  " +
 * request.getParameterValues("jwt"));
 * System.out.println("------->  AuthTokenFilter  " + request.getInputStream());
 * System.out.println("------->  AuthTokenFilter  " +
 * request.getContentLength());
 * System.out.println("------->  AuthTokenFilter  " + request.getContentType());
 * System.out.println("------->  AuthTokenFilter  " +
 * request.getContentLengthLong());
 */