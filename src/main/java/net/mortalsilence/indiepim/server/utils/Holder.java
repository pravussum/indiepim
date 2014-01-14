package net.mortalsilence.indiepim.server.utils;

public class Holder<T> {
	T item;
	public Holder(){};
	public Holder( T item) { set( item);}
	public T get( ) { return item;}
	public void set( T item) { this.item = item;}
}