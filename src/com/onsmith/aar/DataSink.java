package com.onsmith.aar;

import java.io.InputStream;

public interface DataSink extends DataProcessor {
  void pipeFrom(InputStream stream);
}