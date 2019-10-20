package eu.toop.simulator.util;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * A utility class that provides marshalling/unmarshalling
 * functions for XML files/resources
 *
 * @author yerlibilgin
 */
public class JAXBUtil {
  private static final Logger LOGGER = LoggerFactory.getLogger(JAXBUtil.class);

  /**
   * Unmarshals the path given to the given Type T using the object factory class provided as an argument.<br>
   * If a file with the given <code>path</code> name exists then it is parsed, otherwise
   * a resource with <code>"/" + path</code> is tried. If that also does not exist, then en exception is thrown
   * @param path file or resource to be parsed as XML.
   * @param objectFactoryClass the class of ObjectFactory generated for the root schema.
   * @return the java object parsed from the given path.
   */
  public static <T> T parseFileOrResource(String path, Class<?> objectFactoryClass) {
    InputStream inputStream = null;
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(objectFactoryClass);

      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

      if (new File(path).exists()) {
        inputStream = new FileInputStream(path);
      } else {
        inputStream = objectFactoryClass.getResourceAsStream("/" + path);
        if (inputStream == null)
          throw new IllegalStateException("A file nor a resource with the name " + path + " couldn't be found!");
      }
      T t = ((JAXBElement<T>) jaxbUnmarshaller.unmarshal(inputStream)).getValue();
      return t;
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      LOGGER.error(ex.getMessage(), ex);
      throw new IllegalStateException("Couldn't parse file/resource " + path +
          "[" + ex.getMessage() + "]. Check the log for details");
    } finally {
      try {
        inputStream.close();
      } catch (Exception ex) {
      }
    }
  }
}
