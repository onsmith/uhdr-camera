package com.onsmith.unc.uhdr;

import java.io.OutputStream;

public interface DataSource extends DataProcessor {
  void pipeTo(OutputStream stream);
}
