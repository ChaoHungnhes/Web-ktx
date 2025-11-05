package com.example.WebKtx.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constant {
    public static final String TRX_DATE_PATTERN = "yyyyMMdd";
    public static final String LOGBACK_DATE_PATTERN = "yyyy-MM-dd";
    public static final String REQUEST_BODY = "REQUEST_BODY";
    public static final String USER_ID = "USER_ID";
    public static final String RESPONSE_TIME_PATTERN = "yyyyMMdd hh:mm:ss";

    public static final String STT_Y = "Y";
    public static final String STT_N = "N";


    @UtilityClass
    public static final class RequestStatus {
        public static final String SUCCESS = "SUCCESS";
        public static final String FAIL = "FAIL";
    }

}
