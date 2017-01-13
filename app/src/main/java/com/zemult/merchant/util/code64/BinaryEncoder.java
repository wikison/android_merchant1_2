package com.zemult.merchant.util.code64;

/**
 * Created by Wikison on 2016/4/13.
 */
public interface BinaryEncoder extends Encoder {
    byte[] encode(byte[] var1) throws EncoderException;
}