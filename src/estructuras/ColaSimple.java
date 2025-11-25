/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

/**
 *
 * @author Usuario
 */
public class ColaSimple<E> {

    private final ListaSimple<E> list;

    public ColaSimple() {
        this.list = new ListaSimple<>();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }

    public void enqueue(E element) {
        list.addLast(element);
    }

    public E dequeue() {
        return list.removeFirst();
    }

    public E peek() {
        return list.peekFirst();
    }

    public void clear() {
        list.clear();
    }
}