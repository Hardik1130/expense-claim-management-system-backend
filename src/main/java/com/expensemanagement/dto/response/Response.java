package com.expensemanagement.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private boolean error;
    private String message;
    private Object data;
    private LocalDateTime timestamp;

    public static Response success(String message, Object data) {
        return Response.builder().error(false).message(message).data(data)
                .timestamp(LocalDateTime.now()).build();
    }

    public static Response failure(String message, Object data) {
        return Response.builder().error(true).message(message).data(data)
                .timestamp(LocalDateTime.now()).build();
    }
}
