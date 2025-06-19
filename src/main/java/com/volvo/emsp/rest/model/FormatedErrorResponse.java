package com.volvo.emsp.rest.model;

import java.util.Date;
import java.util.List;

public record FormatedErrorResponse(int status, String title, List<String> details, String path, Date timestamp) {

}
