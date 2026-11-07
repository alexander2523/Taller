package mx.taller.common;
import org.slf4j.Logger; import org.slf4j.LoggerFactory; import org.springframework.http.*; import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestControllerAdvice
public class ApiExceptionHandler {
 private static final Logger log=LoggerFactory.getLogger(ApiExceptionHandler.class);
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<?> validacion(MethodArgumentNotValidException e){ Map<String,String> m=new LinkedHashMap<>(); e.getBindingResult().getFieldErrors().forEach(x->m.put(x.getField(),x.getDefaultMessage())); return ResponseEntity.badRequest().body(Map.of("mensaje","Datos inválidos","errores",m)); }
 @ExceptionHandler(IllegalArgumentException.class) ResponseEntity<?> conflicto(IllegalArgumentException e){return ResponseEntity.badRequest().body(Map.of("mensaje",e.getMessage()));}
 @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class) ResponseEntity<?> noEncontrado(org.springframework.web.servlet.resource.NoResourceFoundException e){return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje","Recurso no encontrado"));}
 @ExceptionHandler(Exception.class) ResponseEntity<?> error(Exception e){log.error("Excepción no controlada",e); return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("mensaje","Ocurrió un error interno"));}
}
