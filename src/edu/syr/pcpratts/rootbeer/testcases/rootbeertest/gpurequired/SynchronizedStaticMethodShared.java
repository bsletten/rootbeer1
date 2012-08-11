package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.gpurequired;

public class SynchronizedStaticMethodShared {

  public static int m_Value;

  public static synchronized void increment() {
    m_Value++;
  }
}
