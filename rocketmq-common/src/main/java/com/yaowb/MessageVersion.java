package com.yaowb;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @Author yaowenbin
 * @Date 2023/5/4
 */
@Getter
@Accessors(fluent = true)
public enum MessageVersion {

    V1(MessageDecoder.MESSAGE_MAGIC_CODE) {
      @Override
      public int topicLengthSize() {
          return 1;
      }
    };


    private final int magicCode;

    MessageVersion(int magicCode) {
        this.magicCode = magicCode;
    }

    public static MessageVersion valOf(int magicCode) {
        for (MessageVersion messageVersion : MessageVersion.values()) {
            if (messageVersion.magicCode() == magicCode) {
                return messageVersion;
            }
        }
        throw new IllegalArgumentException("Invalid magicCode " + magicCode);
    }

    public abstract int topicLengthSize();
}
