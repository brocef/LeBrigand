/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lebrigand.core.spyglass;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

public class ObjectFieldPair {

    private final WeakReference<Object> obj;
    private final Field f;

    public ObjectFieldPair(Object obj, Field f) {
        this.obj = new WeakReference<>(obj);
        this.f = f;
    }

    protected WeakReference<Object> getWeakObjectReference() {
        return this.obj;
    }

    public Object getObject() throws ObjectExpiredException {
        Object o = this.obj.get();
        if (o == null) {
            throw new ObjectExpiredException();
        }
        return o;
    }

    public Field getField() {
        return this.f;
    }

    private void ensureAccessible() throws ObjectExpiredException {
        if (!this.f.canAccess(this.getObject())) {
            this.f.setAccessible(true);
        }
    }

    public Object get() throws IllegalArgumentException, IllegalAccessException, ObjectExpiredException {
        this.ensureAccessible();
        return this.f.get(this.getObject());
    }

    public ObjectFieldPair getAsObjectFieldPair(String fieldName) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, ObjectExpiredException {
        Object o = this.get();
        return new ObjectFieldPair(o, Utils.getField(o.getClass(), fieldName));
    }

    public int getInt() throws IllegalArgumentException, IllegalAccessException, ObjectExpiredException {
        this.ensureAccessible();
        return this.f.getInt(this.getObject());
    }

    public char getChar() throws IllegalArgumentException, IllegalAccessException, ObjectExpiredException {
        this.ensureAccessible();
        return this.f.getChar(this.getObject());
    }

    public byte getByte() throws IllegalArgumentException, IllegalAccessException, ObjectExpiredException {
        this.ensureAccessible();
        return this.f.getByte(this.getObject());
    }

    public short getShort() throws IllegalArgumentException, IllegalAccessException, ObjectExpiredException {
        this.ensureAccessible();
        return this.f.getShort(this.getObject());
    }

    public long getLong() throws IllegalArgumentException, IllegalAccessException, ObjectExpiredException {
        this.ensureAccessible();
        return this.f.getLong(this.getObject());
    }

    public float getFloat() throws IllegalArgumentException, IllegalAccessException, ObjectExpiredException {
        this.ensureAccessible();
        return this.f.getFloat(this.getObject());
    }

    public double getDouble() throws IllegalArgumentException, IllegalAccessException, ObjectExpiredException {
        this.ensureAccessible();
        return this.f.getDouble(this.getObject());
    }

    public boolean getBoolean() throws IllegalArgumentException, IllegalAccessException, ObjectExpiredException {
        this.ensureAccessible();
        return this.f.getBoolean(this.getObject());
    }

}
