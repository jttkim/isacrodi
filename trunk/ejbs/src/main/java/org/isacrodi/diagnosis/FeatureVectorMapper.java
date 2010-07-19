package org.isacrodi.diagnosis;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.Iterator;
import javax.naming.*;


import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;
import org.isacrodi.util.io.*;
import java.util.List;
import java.util.ArrayList;
import libsvm.*;


/**
 * Feature Vector Mapper.
 *
 * <p>Feature vector mappers map instances of {@link FeatureVector} to
 * a target feature vector space. This is typically necessary when
 * using external classifiers that require samples to be provided as
 * target feature vector instances.</p>
 *
 * <p>Mappings should be bijective. There currently is nothing to
 * enforce that. Nonetheless, mappers that are not invertible should
 * clearly be flagged up as such.</p>
 */
public interface FeatureVectorMapper<MappingTarget>
{
  /**
   * Determine the dimension of the target space.
   *
   * <p>This is the number of features that can occur in a target
   * vector. For sparse vectors this should be the maximum index of a
   * feature. For array-like vectors this should be the length of the
   * arrays that are returned by the {@link #map} method.</p>
   *
   * @return the dimension of the target space
   */
  public int targetSpaceDimension();

  /**
   * Map a feature vector to a target feature vector.
   *
   * @return the target feature vector
   */
  public MappingTarget map(FeatureVector featureVector);
}
