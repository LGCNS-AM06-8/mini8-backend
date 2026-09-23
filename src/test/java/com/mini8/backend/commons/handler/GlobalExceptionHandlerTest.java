package com.mini8.backend.commons.handler;

import static org.assertj.core.api.Assertions.assertThat;

import com.mini8.backend.commons.exception.BusinessException;
import com.mini8.backend.commons.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void convertsExceptionsToTheCommonResponse() {
    var business = handler.handleBusinessException(new BusinessException(ErrorCode.NOT_FOUND));
    assertThat(business.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(business.getBody())
        .isEqualTo(
            new GlobalExceptionHandler.ErrorResponse("NOT_FOUND", "요청한 리소스를 찾을 수 없습니다.", null));

    var bindingResult = new BeanPropertyBindingResult(new Object(), "request");
    bindingResult.addError(new FieldError("request", "careerYears", "잘못된 값"));
    var invalid =
        handler.handleInvalidInput(new MethodArgumentNotValidException(null, bindingResult));
    assertThat(invalid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(invalid.getBody().field()).isEqualTo("careerYears");

    var internal = handler.handleException(new RuntimeException("노출하면 안 되는 메시지"));
    assertThat(internal.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(internal.getBody().message()).isEqualTo("서버 내부 오류가 발생했습니다.");
  }
}
