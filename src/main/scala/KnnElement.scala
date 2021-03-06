import scala.collection.mutable.ListBuffer

/**
 * Created on 2015-05-02.
 *
 * @author Jakub Zawislak
 */
class KnnElement {
  val stringList = new ListBuffer[String]()
  val doubleList = new ListBuffer[Double]()
  val categoryList = new ListBuffer[Category]()
  private var classValue: String = _
  private var determinedClassValue: String = _

  def addElement(newVal: Any): Unit = {
    newVal match {
      case s: String => stringList.append(s)
      case d: Double => doubleList.append(d)
      case b: Category => categoryList.append(b)
    }
  }

  def classVal = classValue

  def setClassVal(newVal: String) = classValue = newVal

  def determinedClassVal = determinedClassValue

  def setDeterminedClassVal(newVal: String) = determinedClassValue = newVal

  def calculateDistance(toCompare: KnnElement, standardizationArray: Option[Array[Double]] = None: Option[Array[Double]]): Double = {
    val distance = toCompare.stringList.zip(this.stringList).map(a => levenshteinDistance(a._1, a._2)) ++
      toCompare.categoryList.zip(this.categoryList).map(a => if (a._1 != a._2) 1D else 0D) ++
      toCompare.doubleList.zip(this.doubleList).map(a => scala.math.abs(a._1 - a._2))
    if (standardizationArray.isDefined) {
      distance.zip(standardizationArray.get).map(a => if (a._2 != 0) a._1 / a._2 else a._1).sum
    } else {
      distance.sum
    }
  }

  override def toString = {
    s""" StringList: $stringList
        |DoubleList: $doubleList
        |CategoryList: $categoryList
        |ClassValue: $classValue
     """.stripMargin
  }

  //implementation based on http://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Java
  def levenshteinDistance(str1: String, str2: String) = {
    val disArray = Array.ofDim[Int](str1.length + 1, str2.length + 1)
    for (i <- 1 to str1.length) {
      disArray(i)(0) = i
    }
    for (j <- 1 to str2.length) {
      disArray(0)(j) = j
    }
    for (i <- 1 to str1.length)
      for (j <- 1 to str2.length) {
        disArray(i)(j) = Math.min(
          disArray(i - 1)(j) + 1,
          Math.min(disArray(i)(j - 1) + 1,
            disArray(i - 1)(j - 1) + (if (str1.charAt(i - 1) == str2.charAt(j - 1)) 0 else 1)))
      }
    disArray(str1.length)(str2.length).toDouble
  }
}