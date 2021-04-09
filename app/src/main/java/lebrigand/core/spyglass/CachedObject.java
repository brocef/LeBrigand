package lebrigand.core.spyglass;

import java.lang.ref.WeakReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CachedObject {

    private static final Logger log = Logger.getLogger(CachedObject.class.getName());
    private final int id;
    private final WeakReference<Object> objRef;
    private final String className;

    public CachedObject(Object obj) {
        // #NoNullZone
        if (obj == null) {
            throw new NullPointerException();
        }
        this.objRef = new WeakReference(obj);
        this.id = System.identityHashCode(obj);
        this.className = obj.getClass().getName();
    }

    public int getId() {
        return this.id;
    }

    public Object getObject() throws CachedObjectExpiredException {
        Object o = this.objRef.get();
        if (o == null) {
            CachedObject.log.log(Level.FINE, String.format("%s object has expired (id: %d)", this.className, this.id));
            throw new CachedObjectExpiredException();
        } else {
            return o;
        }
    }

    public String getClassName() {
        return this.className;
    }

    public boolean hasExpired() {
        try {
            this.getObject();
        } catch (CachedObjectExpiredException ex) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CachedObject) {
            CachedObject obj = (CachedObject) o;
            return this.id == obj.id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + this.id;
        return hash;
    }
}
