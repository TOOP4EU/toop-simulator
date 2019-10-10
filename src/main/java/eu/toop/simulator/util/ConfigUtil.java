package eu.toop.simulator.util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * This class provides utility methods for typesafe config library
 *
 * @author yerlibilgin
 */
public class ConfigUtil {
  /**
   * The Logger instance
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigUtil.class);
  /**
   * Try to read the given path name (possibly adding the extension <code>.conf</code> as a file or a resource.
   * <br><br>
   * <p>
   * First try a file with name <code>pathname + ".conf"</code> and then
   * if it doesn't exist, try the resource <code>pathname + ".conf"</code>. If the resource also does not exist,
   * then throw an Exception
   * </p>
   *
   * @param pathname the name of the config file to be parsed
   * @return the parsed Config object
   */
  public static Config resolveConfiguration(String pathname) {
    Config config;
    File file = new File(pathname + ".conf");
    if (file.exists()) {
      LOGGER.info("Loading config from the file \"" + file.getName() + "\"");
      config = ConfigFactory.parseFile(file).resolve();
    } else {
      LOGGER.info("Loading config from the resource \"" + pathname + ".conf\"");
      config = ConfigFactory.load(pathname).resolve();
    }
    return config;
  }
}
