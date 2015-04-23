package com.resonant.core.graph.internal.matrix

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @author Calclavia
 */
class SparseMatrixTest {

	@Test
	def testPrint() {
		val mat = new SparseMatrix[Int, Int, Int](0 until 5, 0 until 5)
		assertEquals("SparseMatrix [5x5]\n  | 0 1 2 3 4 \n0 | 0 0 0 0 0 \n1 | 0 0 0 0 0 \n2 | 0 0 0 0 0 \n3 | 0 0 0 0 0 \n4 | 0 0 0 0 0 \n", mat.toString)
	}

	@Test
	def testApply() = {
		val mat = new SparseMatrix[Int, Int, Int](0 until 5, 0 until 5)
		mat(2, 3) = 5
		assertEquals("SparseMatrix [5x5]\n  | 0 1 2 3 4 \n0 | 0 0 0 0 0 \n1 | 0 0 0 0 0 \n2 | 0 0 0 5 0 \n3 | 0 0 0 0 0 \n4 | 0 0 0 0 0 \n", mat.toString)
	}

	@Test
	def testGet() = {
		val mat = new SparseMatrix[Int, Int, Int](0 until 5, 0 until 5)
		mat(2, 3) = 5
		assertEquals(5, mat(2, 3))
	}
}
