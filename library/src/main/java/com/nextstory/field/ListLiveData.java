package com.nextstory.field;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

/**
 * 리스트 LiveData
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public final class ListLiveData<T> extends MutableLiveData<List<T>> {
    private final List<T> items;

    public ListLiveData() {
        this(new ArrayList<>());
    }

    public ListLiveData(List<T> value) {
        super();
        setValue(items = value);
    }

    @NonNull
    @Override
    public List<T> getValue() {
        return items;
    }

    public T postSet(int index, T item) {
        T t = items.set(index, item);
        postValue(items);
        return t;
    }

    public T set(int index, T item) {
        T t = items.set(index, item);
        setValue(items);
        return t;
    }

    public T postRemove(int index) {
        T t = items.remove(index);
        postValue(items);
        return t;
    }

    public T remove(int index) {
        T t = items.remove(index);
        setValue(items);
        return t;
    }

    public void postAdd(T item) {
        items.add(item);
        postValue(items);
    }

    public void add(T item) {
        items.add(item);
        setValue(items);
    }

    public void postAdd(int index, T item) {
        items.add(index, item);
        postValue(items);
    }

    public void add(int index, T item) {
        items.add(index, item);
        setValue(items);
    }

    public void postAddAll(List<T> item) {
        items.addAll(item);
        postValue(items);
    }

    public void addAll(List<T> item) {
        items.addAll(item);
        setValue(items);
    }

    public void postClear() {
        items.clear();
        postValue(items);
    }

    public void clear() {
        items.clear();
        setValue(items);
    }

    public int size() {
        return items.size();
    }

    public T get(int index) {
        return items.get(index);
    }
}
