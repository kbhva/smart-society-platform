package com.smartsociety.platform.common;
import org.springframework.http.HttpStatus;
public class DomainException extends RuntimeException { private final HttpStatus status; public DomainException(HttpStatus s,String m){super(m);status=s;} public HttpStatus status(){return status;} }
