package com.onsmith.unc.uhdr;

import java.io.InputStream;

public interface Sink extends Processor {
  void pipeFrom(InputStream stream);
}
