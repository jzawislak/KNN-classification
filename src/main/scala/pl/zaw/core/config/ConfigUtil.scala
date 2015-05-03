package pl.zaw.core.config

import java.io.File
import java.util

import com.typesafe.config._
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

/**
 * Loads properties files.
 * Created on 2015-04-28.
 *
 * @author Jakub Zawislak
 */
object ConfigUtil {
  private val SUFFIX = ".conf"

  private val logger = Logger(LoggerFactory.getLogger(this.getClass.getName))
  var appName = None: Option[String]
  var config = None: Option[Config]

  /**
   * Initializes config for application. Should be called once at the beginning.
   *
   * Loads default config from classpath resources and local config from path set in system environments.
   * Default config file should be named [[appName]].conf, [[appName]].json or [[appName]].properties.
   * System variable may be named [[appName]]+[[SUFFIX]]
   *
   * @param appName application's name
   */
  def init(appName: String) = {
    this.appName = Some(appName)

    //load default
    config = Some(ConfigFactory.defaultOverrides())
    //load file from the same directory
    val userFile = new File(s"$appName${ConfigUtil.SUFFIX}")
    if (userFile.exists) {
      logger.info("Loading user local config file.")
      config = Some(config.get.withFallback(ConfigFactory.parseFile(userFile)))
    }
    //load file from system property
    val systemPath = Option(System.getProperty(s"$appName${ConfigUtil.SUFFIX}"))
    if (systemPath.isDefined) {
      val systemFile = new File(systemPath.get)
      if (systemFile.exists()) {
        logger.info("Loading user config from system env file.")
        config = Some(config.get.withFallback(ConfigFactory.parseFile(systemFile)))
      }
    }
    //load default from resource
    logger.info("Loading default config from resources.")
    config = Some(config.get.withFallback(ConfigFactory.load(appName)))
  }

  /**
   * Returns property's value as [[Option[String] ]]
   * @param property property's name
   */
  @throws[MissingInit]("if the init wasn't called first")
  def getProperty(property: String): Option[String] = {
    if (!config.isDefined) {
      throw new MissingInit("Init properties first.")
    }
    try {
      Some(config.get.getString(property))
    } catch {
      case _: ConfigException => None
    }
  }

  /**
   * Returns property's value as [[Option[Boolean] ]]
   * @param property property's name
   */
  @throws[MissingInit]("if the init wasn't called first")
  def getPropertyAsBoolean(property: String): Option[Boolean] = {
    if (!config.isDefined) {
      throw new MissingInit("Init properties first.")
    }
    try {
      Some(config.get.getBoolean(property))
    } catch {
      case _:ConfigException => None
    }
  }

  /**
   * Returns property's value as [[Option[Int] ]]
   * @param property property's name
   */
  @throws[MissingInit]("if the init wasn't called first")
  def getPropertyAsInt(property: String): Option[Int] = {
    if (!config.isDefined) {
      throw new MissingInit("Init properties first.")
    }
    try {
      Some(config.get.getInt(property))
    } catch {
      case _:ConfigException => None
    }
  }

  /**
   * Returns property's value as [[Option[util.List[String]] ]]
   * @param property property's name
   */
  @throws[MissingInit]("if the init wasn't called first")
  def getPropertyAsStringList(property: String): Option[util.List[String]] = {
    if (!config.isDefined) {
      throw new MissingInit("Init properties first.")
    }
    try {
      Some(config.get.getStringList(property))
    } catch {
      case _:ConfigException => None
    }
  }
}