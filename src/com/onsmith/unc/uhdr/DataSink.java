package com.onsmith.unc.uhdr;

import java.io.InputStream;

public interface DataSink extends DataProcessor {
  void pipeFrom(InputStream stream);
}