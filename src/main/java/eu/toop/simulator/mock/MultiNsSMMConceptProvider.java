/**
 * Copyright (C) 2018-2019 toop.eu
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.toop.simulator.mock;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;
import eu.toop.connector.api.smm.ISMMConceptProvider;
import eu.toop.connector.api.smm.MappedValueList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Map;

/**
 * This class represents a semantic mapping service that contains multiple source and
 * destination namespaces and is able to perform bidirectional mapping (a source can act
 * as a destination and vice versa)
 *
 * @author muhammet yildiz
 * @since 0.10.6
 */
public class MultiNsSMMConceptProvider implements ISMMConceptProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(MultiNsSMMConceptProvider.class);

  private final Map<String, Map<String, Map<String, String>>> m_nsMap;

  public MultiNsSMMConceptProvider(Map<String, Map<String, Map<String, String>>> nsMap) {

    ValueEnforcer.notNull(nsMap, "namespace mapping");

    this.m_nsMap = nsMap;
  }

  @Nonnull
  @Override
  public MappedValueList getAllMappedValues(@Nonnull String sLogPrefix, @Nonnull String sSourceNamespace, @Nonnull String sDestNamespace) throws IOException {
    LOGGER.info(sLogPrefix +
        "Using static Map for SMM mappings from '" +
        sSourceNamespace +
        "' to '" +
        sDestNamespace +
        "'");

    final MappedValueList ret = new MappedValueList();


    if (m_nsMap.containsKey(sSourceNamespace)) {
      Map<String, Map<String, String>> targetMap = m_nsMap.get(sSourceNamespace);
      if (targetMap.containsKey(sDestNamespace)) {
        Map<String, String> concepts = targetMap.get(sDestNamespace);

        for (final Map.Entry<String, String> aEntry : concepts.entrySet())
          ret.addMappedValue(sSourceNamespace, aEntry.getKey(), sDestNamespace, aEntry.getValue());
      }
    }

    if (ret.isEmpty() && LOGGER.isDebugEnabled()){
        LOGGER.debug("Namespace query mismatch. Queried '" + sSourceNamespace + "' to '" + sDestNamespace);
    }

    return ret;
  }

  @Override
  public String toString() {
    return new ToStringGenerator(this).getToString();
  }
}
