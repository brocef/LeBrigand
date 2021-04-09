/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lebrigand.core.spyglass;

import java.lang.reflect.Field;
import java.util.Objects;

public class Derivative {

    private final String className;
    private final String fieldName;
    private final String fieldTypeName;

    public Derivative(String className, Field f) {
        this.className = className;
        this.fieldName = f.getName();
        this.fieldTypeName = f.getType().getName();
    }

    public String getClassName() {
        return this.className;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String asKey() {
        return String.format("%s %s", this.className, this.fieldName);
    }

    @Override
    public String toString() {
        return String.format("Derivative %s %s (Field Type: %s)", this.className, this.fieldName, this.fieldTypeName);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Derivative)) {
            return false;
        } else {
            Derivative other = (Derivative) o;
            return this.asKey().equals(other.asKey());
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.className);
        hash = 71 * hash + Objects.hashCode(this.fieldName);
        hash = 71 * hash + Objects.hashCode(this.fieldTypeName);
        return hash;
    }

}
