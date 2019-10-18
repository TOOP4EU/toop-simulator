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

import com.helger.commons.string.ToStringGenerator;
import com.typesafe.config.Config;
import eu.toop.commander.util.CommanderUtil;
import eu.toop.connector.api.smm.ISMMConceptProvider;
import eu.toop.connector.api.smm.MappedValueList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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

  /**
   * Create the concept provider by initializin the semantic map
   */
  public MultiNsSMMConceptProvider() {
    this.m_nsMap = initializeSemanticMap();
  }

  /**
   * Parse and resolve the sms.conf file/classpath resource and create
   * a mapping from and to TOOP concepts.
   * @return
   */
  private static Map<String, Map<String, Map<String, String>>> initializeSemanticMap() {
    LOGGER.debug("Preparing SMS Simulator");

    Config config = CommanderUtil.resolveConfiguration("sms");

    List<Map<String, Object>> mappings = (List<Map<String, Object>>) config.getAnyRef("Mappings");

    //iterate over every mapping, create an inverse of it as well, and add both to the MultiNSSmm...
    //this can be simplified by helper objects

    Map<String, Map<String, Map<String, String>>> mapOpMapsOfMaps = new HashMap<>();

    for (Map<String, Object> mapping : mappings) {

      Map<String, String> conceptMap = new HashMap<>();
      Map<String, String> inverseConceptMap = new HashMap<>();

      String sourceNS = (String) mapping.get("sourceNS");
      String targetNS = (String) mapping.get("targetNS");
      Map<String, String> concepts = (Map<String, String>) mapping.get("concepts");

      for (String key : concepts.keySet()) {
        String value = concepts.get(key);
        conceptMap.put(key, value);
        inverseConceptMap.put(value, key);
      }

      //forward mapping
      populateMapping(mapOpMapsOfMaps, conceptMap, sourceNS, targetNS);

      //backwards mappind
      populateMapping(mapOpMapsOfMaps, inverseConceptMap, targetNS, sourceNS);
    }

    return mapOpMapsOfMaps;

  }

  private static void populateMapping(Map<String, Map<String, Map<String, String>>> conversionMap,
                                      Map<String, String> conceptMap, String sourceNS, String targetNS) {
    Map<String, Map<String, String>> nsMap;
    if (conversionMap.containsKey(sourceNS)) {
      nsMap = conversionMap.get(sourceNS);
    } else {
      nsMap = new HashMap<>();
      conversionMap.put(sourceNS, nsMap);
    }

    if (nsMap.containsKey(targetNS)) {
      throw new IllegalStateException(sourceNS + "->" + targetNS + " mapping already defined");
    }

    nsMap.put(targetNS, conceptMap);
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
