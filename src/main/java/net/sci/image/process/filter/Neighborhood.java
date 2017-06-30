/**
 * 
 */
package net.sci.image.process.filter;

/**
 * Iterates over the neighbor of an array element.
 * 
 * Instances of the Neighborhood interface are supposed to be defined with
 * respect to an element.
 * 
 * @author dlegland
 *
 */
//TODO: need to choose between "neighborhood of an element", or "factory for accessing the neighborhood of an element"
// -> for the moment, the first one was chosen
public interface Neighborhood extends Iterable<int[]>
{
}
