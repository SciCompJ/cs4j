/**
 * 
 */
package net.sci.image.morphology.watershed;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 * @param <K>
 *            the type used to order the values
 * @param <V>
 *            the values to add into the queue.
 */
public class OrderedQueue<K,V>
{

    TreeMap<K,LinkedList<V>> listQueue = new TreeMap<>();
    
    
    public void add(K key, V value)
    {
        LinkedList<V> list = listQueue.get(key);
        if (list == null)
        {
            list = new LinkedList<V>();
            listQueue.put(key, list);
        }
        list.add(value);
    }
    
    public boolean isEmpty()
    {
        return listQueue.isEmpty();
    }
    
    public boolean hasNext()
    {
        return !listQueue.isEmpty();
    }
    
    public V remove()
    {
        Map.Entry<K,LinkedList<V>> entry = listQueue.firstEntry();
        LinkedList<V> list = entry.getValue();
        V res = list.remove();
        if (list.isEmpty())
        {
            listQueue.remove(entry.getKey());
        }
        return res;
    }
}
