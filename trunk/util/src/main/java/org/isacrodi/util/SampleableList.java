package org.isacrodi.util;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;


/**
 * An ArrayList that supports drawing of random samples.
 */
public class SampleableList<T> extends ArrayList<T>
{
  public SampleableList()
  {
    super();
  }


  public SampleableList(Collection<? extends T> c)
  {
    this();
    for (T e : c)
    {
      this.add(e);
    }
  }


  public T randomSample(Random rng)
  {
    if (this.size() == 0)
    {
      return (null);
    }
    else
    {
      return (this.get(rng.nextInt(this.size())));
    }
  }


  public List<T> randomSampleList(Random rng, int numSamples)
  {
    if (this.size() < numSamples)
    {
      throw new RuntimeException(String.format("request of %d samples from a list of %d elements", numSamples, this.size()));
    }
    int[] index = new int[this.size()];
    for (int i = 0; i < this.size(); i++)
    {
      index[i] = i;
    }
    for (int i = 0; i < this.size(); i++)
    {
      int j = rng.nextInt(this.size());
      int tmp = index[i];
      index[i] = index[j];
      index[j] = tmp;
    }
    ArrayList<T> l = new ArrayList<T>();
    for (int i = 0; i < numSamples; i++)
    {
      l.add(this.get(index[i]));
    }
    return (l);
  }
}
