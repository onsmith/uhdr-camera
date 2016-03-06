package com.onsmith.aar;

import java.io.OutputStream;

public interface DataSource extends DataProcessor {
  void pipeTo(OutputStream stream);
}
