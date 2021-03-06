package org.clulab.wm.eidos.system

import org.clulab.linnaeus.model.graph.eidos.EidosNetwork
import org.clulab.linnaeus.model.graph.eidos.EidosNode
import org.clulab.linnaeus.model.io.eidos.EidosReader
import org.scalatest._

import scala.collection.mutable

class TestDomainOntology extends FlatSpec with Matchers {

  def hasDuplicates(network: EidosNetwork): Boolean = {
    val visitor = new network.HierarchicalGraphVisitor()
    var paths = List.empty[Seq[String]]
    var path = mutable.Seq.empty[String]
    var duplicate = false

    visitor.foreachNode { (node: EidosNode, depth: Int) =>
      if (network.isLeaf(node)) {
        val newPath = path.slice(0, depth) :+ node.name

        println(newPath)
        if (paths.contains(newPath)) {
          duplicate = true
          println(s"Duplicate: $newPath")
        }
        else
          paths = newPath :: paths
      }
      else
        if (depth < path.size)
          path(depth) = node.name
        else
          path = path :+ node.name
      true
    }
    duplicate
  }

  behavior of "ontologies"

  def test(path: String): Unit = {
    it should "load and not have duplicates in " + path in {
      val network = new EidosNetwork()
      val reader = new EidosReader(network)

      // This is the load part which will fail on exception.
      reader.readFromFile(path)
      hasDuplicates(network) should be (false)
    }
  }

  Seq(
    "./wm_metadata.yml"
  ).foreach(path => test(path))
}
