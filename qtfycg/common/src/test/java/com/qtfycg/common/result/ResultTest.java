package com.qtfycg.common.result;

import com.qtfycg.common.enums.GlobalErrorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    @Test
    void okWithDataBuildsSuccessfulResponse() {
        Result<String> response = Result.ok("payload");

        assertTrue(response.isSuccess());
        assertEquals(GlobalErrorCode.OK.getStatus(), response.getStatus());
        assertEquals(GlobalErrorCode.OK.getCode(), response.getCode());
        assertEquals(GlobalErrorCode.OK.getMessage(), response.getMessage());
        assertEquals("payload", response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void failWithErrorCodeBuildsFailedResponse() {
        Result<Void> response = Result.fail(GlobalErrorCode.FAIL);

        assertFalse(response.isSuccess());
        assertEquals(GlobalErrorCode.FAIL.getStatus(), response.getStatus());
        assertEquals(GlobalErrorCode.FAIL.getCode(), response.getCode());
        assertEquals(GlobalErrorCode.FAIL.getMessage(), response.getMessage());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void failCanOverrideMessageWithoutChangingCode() {
        Result<Void> response = Result.fail(GlobalErrorCode.FAIL, "database unavailable");

        assertFalse(response.isSuccess());
        assertEquals(GlobalErrorCode.FAIL.getStatus(), response.getStatus());
        assertEquals(GlobalErrorCode.FAIL.getCode(), response.getCode());
        assertEquals("database unavailable", response.getMessage());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void globalErrorCodeCanBeUsedThroughErrorCodeContract() {
        ErrorCode errorCode = GlobalErrorCode.FAIL;

        assertEquals(500, errorCode.getStatus());
        assertEquals("FAIL", errorCode.getCode());
        assertEquals("fail", errorCode.getMessage());
    }
}
