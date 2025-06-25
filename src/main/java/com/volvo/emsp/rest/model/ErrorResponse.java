package com.volvo.emsp.rest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.List;

@Schema(description = "Standard error response")
public record ErrorResponse(
        @Schema(description = "HTTP status code",
                example = "400")
        int status,

        @Schema(description = "Error message title",
                example = "Invalid parameter")
        String title,

        @Schema(description = "Error message details",
                example = "Invalid parameter: Invalid email")
        List<String> details,

        @Schema(description = "Path of the request that generated the error",
                example = "/api/accounts")
        String path,

        @Schema(description = "Error timestamp",
                example = "2025-06-24T10:15:30.123Z")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
        Date timestamp
) {

}
