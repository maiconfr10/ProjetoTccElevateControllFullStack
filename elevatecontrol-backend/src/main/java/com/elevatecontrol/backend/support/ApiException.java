package com.elevatecontrol.backend.support;
import org.springframework.http.HttpStatus;
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    public ApiException(HttpStatus status, String message) { super(message); this.status=status; }
    public HttpStatus getStatus() { return status; }
    public static ApiException bad(String msg) { return new ApiException(HttpStatus.BAD_REQUEST,msg); }
    public static ApiException forbidden() { return new ApiException(HttpStatus.FORBIDDEN,"Acesso nao autorizado."); }
    public static ApiException notFound(String name) { return new ApiException(HttpStatus.NOT_FOUND,name+" nao encontrado."); }
    public static ApiException conflict(String msg) { return new ApiException(HttpStatus.CONFLICT,msg); }
}
