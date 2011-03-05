package org.isacrodi.util.io;

import java.io.OutputStream;


/**
 * An output stream that outputs nothing, like <code>/dev/null</code>.
 */
public class NullOutputStream extends OutputStream
{
  @Override
  public void close()
  {
  }


  @Override
  public void flush()
  {
  }


  @Override
  public void write(byte[] b)
  {
  }


  @Override
  public void write(byte[] b, int off, int len)
  {
  }


  @Override
  public void write(int b)
  {
  }
}