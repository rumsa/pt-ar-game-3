package com.playtech.ptargame3.common.io.separator;


import java.nio.ByteBuffer;
import java.util.List;

public interface Encoder {

    List<ByteBuffer> encode(List<ByteBuffer> src);

}
