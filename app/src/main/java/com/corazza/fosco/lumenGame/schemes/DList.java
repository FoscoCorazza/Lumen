package com.corazza.fosco.lumenGame.schemes;

import android.graphics.Canvas;
import android.support.annotation.NonNull;

import com.corazza.fosco.lumenGame.gameObjects.Lumen;
import com.corazza.fosco.lumenGame.gameObjects.Star;
import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.geometry.dots.GridDot;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Simone on 12/08/2016.
 */
public class DList<T extends SchemeLayoutDrawable> extends SchemeLayoutDrawable implements List<T> {
    private List<T> list = new ArrayList<>();

    public DList() {
        this.list = new ArrayList<>();
    }

    @Override
    protected void initPaints() {}

    @Override
    public void render(Canvas canvas) {
        for(T t : getRawListCopy()){
            t.render(canvas);
        }
    }

    @Override
    public void render(Canvas canvas, int x1, int y1) {
        for(T t : getRawListCopy()){
            t.render(canvas, x1, y1);
        }
    }

    @Override
    public void update() {
        updateOpacity();
        for(T t : getRawListCopy()){
            t.inherit(this);
            t.update();
        }
    }

    /*@Override
    public void inherit(SchemeLayoutDrawable drawable) {
        for(T t : getRawListCopy()){
            t.inherit(this);
        }
    }*/


    public ArrayList<T> getRawListCopy() {
        return new ArrayList<>(list);
    }




    /*OVERRIDES DELLA LISTA*/

    public int size() {
        return list.size();
    }

    @NonNull
    @Override
    public List<T> subList(int start, int end) {
        return list.subList(start, end);
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @NonNull
    @Override
    public <E> E[] toArray(@NonNull E[] array) {
        return list.toArray(array);
    }

    @Override
    public T get(int i) {
        return list.get(i);
    }

    @Override
    public int indexOf(Object object) {
        return list.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean contains(Object object) {
        return list.contains(object);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> collection) {
        return list.containsAll(collection);
    }

    @Override
    public void add(int location, T object) {
        list.add(location, object);
    }

    public boolean add(T t) {
        return list.add(t);
    }

    @Override
    public boolean addAll(int location, Collection<? extends T> collection) {
        return list.addAll(location, collection);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        return list.addAll( collection);
    }


    @NonNull
    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return list.lastIndexOf(object);
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator() {
        return list.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator(int location) {
        return list.listIterator(location);
    }

    @Override
    public T remove(int location) {
        return list.remove(location);
    }

    @Override
    public boolean remove(Object object) {
        return list.remove(object);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> collection) {
        return list.removeAll(collection);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> collection) {
        return list.retainAll(collection);
    }

    @Override
    public T set(int location, T object) {
        return list.set(location, object);
    }

    public T elementIn(Dot dot) {
        for (T t : this) {
            if(t.isIn(dot)) return t;
        }
        return null;
    }
}
