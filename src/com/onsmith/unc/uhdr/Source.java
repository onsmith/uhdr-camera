package com.onsmith.unc.uhdr;

import java.io.OutputStream;

public interface Source extends Processor {
  void pipeTo(OutputStream stream);
}
