package com.yaowb.commitlog;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author yaowenbin
 * @Date 2023/5/4
 */
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class EncodeException extends Exception {

    private final PutMessageResult result;

}
