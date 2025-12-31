package com.watercooler.backend.global.error.exception

import com.watercooler.backend.global.error.ErrorCode

open class BusinessException(val errorCode: ErrorCode) : RuntimeException()
