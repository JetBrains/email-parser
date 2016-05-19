/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *    AbstractStringDistanceFunction.java
 *    Copyright (C) 2008 Bruno Woltzenlogel Paleo (http://www.logic.at/people/bruno/ ; http://bruno-wp.blogspot.com/)
 *
 */

package practice.email.clustering

import weka.core.Attribute
import weka.core.Instance
import weka.core.Instances
import weka.core.NormalizableDistance
import weka.core.neighboursearch.PerformanceStats

/**
 * Represents the abstract ancestor for string-based distance functions, like
 * EditDistance.

 * @author Bruno Woltzenlogel Paleo
 * *
 * @version $Revision: 8108 $
 */
abstract class AbstractStringDistanceFunction : NormalizableDistance {

    /**
     * Constructor that doesn't set the data
     */
    constructor() : super() {
    }

    /**
     * Constructor that sets the data

     * @param data the set of instances that will be used for
     * *             later distance comparisons
     */
    constructor(data: Instances) : super(data) {
    }


    /**
     * Updates the current distance calculated so far with the new difference
     * between two attributes. The difference between the attributes was
     * calculated with the difference(int,double,double) method.

     * @param currDist the current distance calculated so far
     * *
     * @param diff     the difference between two new attributes
     * *
     * @return the update distance
     * *
     * @see .difference
     */
    override fun updateDistance(currDist: Double, diff: Double): Double {
        return currDist + diff * diff
    }

    /**
     * Computes the difference between two given attribute
     * values.

     * @param index the attribute index
     * *
     * @param val1  the first value
     * *
     * @param val2  the second value
     * *
     * @return the difference
     */
    protected fun difference(index: Int, string1: String, string2: String): Double {
        when (m_Data.attribute(index).type()) {
            Attribute.STRING -> {
                val diff = stringDistance(string1, string2)
                if (m_DontNormalize == true) {
                    return diff
                } else {
                    if (string1.length > string2.length) {
                        return diff / string1.length.toDouble()
                    } else {
                        return diff / string2.length.toDouble()
                    }
                }
                return 0.0
            }

            else -> return 0.0
        }
    }

    /**
     * Calculates the distance between two instances. Offers speed up (if the
     * distance function class in use supports it) in nearest neighbour search by
     * taking into account the cutOff or maximum distance. Depending on the
     * distance function class, post processing of the distances by
     * postProcessDistances(double []) may be required if this function is used.

     * @param first       the first instance
     * *
     * @param second      the second instance
     * *
     * @param cutOffValue If the distance being calculated becomes larger than
     * *                    cutOffValue then the rest of the calculation is
     * *                    discarded.
     * *
     * @param stats       the performance stats object
     * *
     * @return the distance between the two given instances or
     * * Double.POSITIVE_INFINITY if the distance being
     * * calculated becomes larger than cutOffValue.
     */
    override fun distance(first: Instance, second: Instance, cutOffValue: Double, stats: PerformanceStats?): Double {
        var sqDistance = 0.0
        val numAttributes = m_Data.numAttributes()

        validate()

        var diff: Double

        for (i in 0..numAttributes - 1) {
            diff = 0.0
            if (m_ActiveIndices[i]) {
                diff = difference(i, first.stringValue(i), second.stringValue(i))
            }
            sqDistance = updateDistance(sqDistance, diff)
            if (sqDistance > cutOffValue * cutOffValue) return java.lang.Double.POSITIVE_INFINITY
        }
        val distance = Math.sqrt(sqDistance)
        return distance
    }

    /**
     * Calculates the distance between two strings.
     * Must be implemented by any non-abstract StringDistance class

     * @param stringA the first string
     * *
     * @param stringB the second string
     * *
     * @return the distance between the two given strings
     */
    internal abstract fun stringDistance(stringA: String, stringB: String): Double

}
