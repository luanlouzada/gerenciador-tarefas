package br.com.gerenciador.infrastructure.exception;

import br.com.gerenciador.domain.exception.AuthenticateException;
import br.com.gerenciador.domain.exception.SystemException;
import br.com.gerenciador.domain.exception.TaskException;
import br.com.gerenciador.domain.exception.UserException;
import br.com.gerenciador.domain.exception.enums.ErrorCodeEnum;
import br.com.gerenciador.infrastructure.dto.response.BaseResponse;
import br.com.gerenciador.infrastructure.dto.response.ErrorResponse;
import br.com.gerenciador.infrastructure.dto.response.ValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

@ControllerAdvice
public class HandleControllerAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<ValidationError>> HandleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        var error = new ErrorResponse(ErrorCodeEnum.SYS0004.getMessage(),
                ErrorCodeEnum.SYS0004.getCode(),
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(fieldError -> new ValidationError(fieldError.getField(), fieldError.getDefaultMessage()))
                        .collect(Collectors.toList()));
        return new ResponseEntity<>(BaseResponse.<ValidationError>builder().success(false).error(error).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticateException.class)
    public ResponseEntity<BaseResponse<String>> HandleAuthenticateException(AuthenticateException ex, WebRequest request) {
        var error = new ErrorResponse(
                ex.getMessage(),
                ex.getCode(),
                null
        );
        return new ResponseEntity<>(BaseResponse.<String>builder().success(false).error(error).build(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<BaseResponse<String>> HandleUserException(UserException ex, WebRequest request) {
        var error = new ErrorResponse(
                ex.getMessage(),
                ex.getCode(),
                null
        );
        return new ResponseEntity<>(BaseResponse.<String>builder().success(false).error(error).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TaskException.class)
    public ResponseEntity<BaseResponse<String>> HandleTaskException(TaskException ex, WebRequest request) {
        var error = new ErrorResponse(
                ex.getMessage(),
                ex.getCode(),
                null

        );
        return new ResponseEntity<>(BaseResponse.<String>builder().success(false).error(error).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SystemException.class)
    public ResponseEntity<BaseResponse<String>> HandleSystemException(SystemException ex, WebRequest request) {
        var error = new ErrorResponse(
                ex.getMessage(),
                ex.getCode(),
                null

        );
        return new ResponseEntity<>(BaseResponse.<String>builder().success(false).error(error).build(), HttpStatus.BAD_REQUEST);


    }


}
