/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lebrigand.core.spyglass;

import java.lang.reflect.Field;

public class Utils {
    public static Field getField(Class c, String fieldName) throws NoSuchFieldException {
        if (c == null) {
            throw new NoSuchFieldException();
        } else {
            for (Field f : c.getDeclaredFields()) {
                if (f.getName().equals(fieldName)) {
                    return f;
                }
            }
            return Utils.getField(c.getSuperclass(), fieldName);
        }
    }
}
