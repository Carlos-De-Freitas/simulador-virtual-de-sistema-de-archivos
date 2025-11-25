/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

/**
 *
 * @author Usuario
 */
public class ListaSimple<E> {

    private static class Node<E> {
        E data;
        Node<E> next;

        Node(E data) {
            this.data = data;
        }
    }

    private Node<E> head;
    private Node<E> tail;
    private int size;

    public ListaSimple() {
        head = null;
        tail = null;
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void addLast(E element) {
        Node<E> node = new Node<>(element);
        if (isEmpty()) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            tail = node;
        }
        size++;
    }

    public void addFirst(E element) {
        Node<E> node = new Node<>(element);
        if (isEmpty()) {
            head = node;
            tail = node;
        } else {
            node.next = head;
            head = node;
        }
        size++;
    }

    public E peekFirst() {
        return (head == null) ? null : head.data;
    }

    public E removeFirst() {
        if (isEmpty()) {
            return null;
        }
        E value = head.data;
        head = head.next;
        if (head == null) {
            tail = null;
        }
        size--;
        return value;
    }

    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Índice inválido: " + index);
        }
        Node<E> current = head;
        int i = 0;
        while (i < index) {
            current = current.next;
            i++;
        }
        return current.data;
    }

    public E set(int index, E element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Índice inválido: " + index);
        }
        Node<E> current = head;
        int i = 0;
        while (i < index) {
            current = current.next;
            i++;
        }
        E old = current.data;
        current.data = element;
        return old;
    }

    public E removeAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Índice inválido: " + index);
        }
        if (index == 0) {
            return removeFirst();
        }
        Node<E> prev = head;
        int i = 0;
        while (i < index - 1) {
            prev = prev.next;
            i++;
        }
        Node<E> target = prev.next;
        prev.next = target.next;
        if (target == tail) {
            tail = prev;
        }
        size--;
        return target.data;
    }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }
}
