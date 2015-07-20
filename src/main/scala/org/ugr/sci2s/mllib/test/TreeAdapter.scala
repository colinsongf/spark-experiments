package org.ugr.sci2s.mllib.test

import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.tree.model.DecisionTreeModel
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.ugr.sci2s.mllib.test.{MLExperimentUtils => MLEU}
import org.apache.spark.mllib.classification.ClassificationModel
import org.apache.spark.mllib.linalg._

object TreeAdapter extends ClassifierAdapter {
  
	override def algorithmInfo (parameters: Map[String, String]): String = {
      val numClasses = parameters.getOrElse("cls-numClasses", "2")
      val impurity = parameters.getOrElse("cls-impurity", "gini")
      val maxDepth = parameters.getOrElse("cls-maxDepth", "5")
      val maxBins = parameters.getOrElse("cls-maxBins", "32")
		
  		s"Algorithm: Decision Tree (DT)\n" + 
			s"numClasses: $numClasses\n" +
			s"impurity: $impurity\n" + 
			s"maxDepth: $maxDepth\n" +
			s"maxDepth: $maxDepth\n\n"		
	}
  
  override def classify (train: RDD[LabeledPoint], parameters: Map[String, String]): ClassificationModelAdapter = {
    val numClasses = MLEU.toInt(parameters.getOrElse("cls-numClasses", "2"), 2)
    val impurity = parameters.getOrElse("cls-impurity", "gini")
    val maxDepth = MLEU.toInt(parameters.getOrElse("cls-maxDepth", "5"), 5)
    val maxBins = MLEU.toInt(parameters.getOrElse("cls-maxBins", "32"), 32)
    val categoricalFeaturesInfo = Map[Int, Int]()
    val model = DecisionTree.trainClassifier(train, 
        numClasses, categoricalFeaturesInfo, impurity, maxDepth, maxBins)
    new TreeAdapter(model)
  }
  
	def classify (train: RDD[LabeledPoint], parameters: Map[String, String], nominalInfo: Map[Int, Int]): ClassificationModelAdapter = {
  	val numClasses = MLEU.toInt(parameters.getOrElse("cls-numClasses", "2"), 2)
		val impurity = parameters.getOrElse("cls-impurity", "gini")
		val maxDepth = MLEU.toInt(parameters.getOrElse("cls-maxDepth", "5"), 5)
		val maxBins = MLEU.toInt(parameters.getOrElse("cls-maxBins", "32"), 32)
		val model = DecisionTree.trainClassifier(train, 
        numClasses, nominalInfo, impurity, maxDepth, maxBins)
		new TreeAdapter(model)
	}

}

class TreeAdapter(model: DecisionTreeModel) extends ClassificationModelAdapter {
  
  override def predict(data: RDD[Vector]): RDD[Double] = {
    model.predict(data)
  }
      
  override def predict(data: Vector): Double = {
    model.predict(data)
  }
}


