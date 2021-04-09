package lebrigand.core.spyglass;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BindingManager {

    /**
     * Given a root object, find all ways to derive objects of some name.
     */
    private static final Logger log = Logger.getLogger(BindingManager.class.getName());
    protected Object rootObj;
    protected final Field rootObjField;
    protected final BindingManager self;
    protected final Field selfField;
    // Map "class_name" -> Set<"field_name">
    protected final Map<String, Set<String>> classFieldMap;
    // Map "value_class_name" -> List<Derivative>
    protected final Map<String, Set<Derivative>> derivativesMap;
    protected final ObjectCache cache;

    private final String[] classPrefixes;

    public BindingManager(String[] classPrefixes) {
        try {
            this.rootObj = null;
            this.classFieldMap = new HashMap<>();
            this.derivativesMap = new HashMap<>();
            this.cache = new ObjectCache();
            this.classPrefixes = classPrefixes;
            this.rootObjField = BindingManager.class.getDeclaredField("rootObj");
            this.self = this;
            this.selfField = BindingManager.class.getDeclaredField("self");
            this.createSelfBindings();
        } catch (NoSuchFieldException | SecurityException ex) {
            Logger.getLogger(BindingManager.class.getName()).log(Level.SEVERE, "Failed to initialize BindingManager", ex);
            throw new RuntimeException();
        }
    }

    public BindingManager() {
        this(new String[0]);
    }

    private Set<String> getClassFields(String className) {
        Set<String> fieldNameSet;
        if (!this.classFieldMap.containsKey(className)) {
            fieldNameSet = new HashSet<>();
            this.classFieldMap.put(className, fieldNameSet);
        } else {
            fieldNameSet = this.classFieldMap.get(className);
        }
        return fieldNameSet;
    }

    private void createSelfBindings() {
        /**
         * Create bindings to reference this class to itself to create the
         * base-case for exploring the binding graph.
         */
        Set<String> selfClassFields = new HashSet<>();
        Set<Derivative> selfDerivatives = new HashSet<>();
        selfClassFields.add("self");
        selfDerivatives.add(new Derivative(BindingManager.class.getName(), this.selfField));
        this.classFieldMap.put(BindingManager.class.getName(), selfClassFields);
        this.derivativesMap.put(BindingManager.class.getName(), selfDerivatives);
    }

    public void setRootObject(Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
        this.rootObj = o;
    }

    public Object getRootObject() {
        return this.rootObj;
    }

    public int getInt(String className, String fieldName) throws ValueDerivationFailedError {
        return this.getInt(className, fieldName, new HashSet<>());
    }

    public int getInt(String className, String fieldName, Set<Integer> ignoredHashCodes) throws ValueDerivationFailedError {
        try {
            ObjectFieldPair objfield = this.deriveObjectFieldPair(className, fieldName, ignoredHashCodes);
            return objfield.getInt();
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ObjectExpiredException | TooManyResultsException ex) {
            Logger.getLogger(BindingManager.class.getName()).log(Level.WARNING, null, ex);
        }
        throw new ValueDerivationFailedError();
    }

    public char getChar(String className, String fieldName) throws ValueDerivationFailedError {
        return this.getChar(className, fieldName, new HashSet<>());
    }

    public char getChar(String className, String fieldName, Set<Integer> ignoredHashCodes) throws ValueDerivationFailedError {
        try {
            ObjectFieldPair objfield = this.deriveObjectFieldPair(className, fieldName, ignoredHashCodes);
            return objfield.getChar();
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ObjectExpiredException | TooManyResultsException ex) {
            Logger.getLogger(BindingManager.class.getName()).log(Level.WARNING, null, ex);
        }
        throw new ValueDerivationFailedError();
    }

    public byte getByte(String className, String fieldName) throws ValueDerivationFailedError {
        return this.getByte(className, fieldName, new HashSet<>());
    }

    public byte getByte(String className, String fieldName, Set<Integer> ignoredHashCodes) throws ValueDerivationFailedError {
        try {
            ObjectFieldPair objfield = this.deriveObjectFieldPair(className, fieldName, ignoredHashCodes);
            return objfield.getByte();
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ObjectExpiredException | TooManyResultsException ex) {
            Logger.getLogger(BindingManager.class.getName()).log(Level.WARNING, null, ex);
        }
        throw new ValueDerivationFailedError();
    }

    public short getShort(String className, String fieldName) throws ValueDerivationFailedError {
        return this.getShort(className, fieldName, new HashSet<>());
    }

    public short getShort(String className, String fieldName, Set<Integer> ignoredHashCodes) throws ValueDerivationFailedError {
        try {
            ObjectFieldPair objfield = this.deriveObjectFieldPair(className, fieldName, ignoredHashCodes);
            return objfield.getShort();
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ObjectExpiredException | TooManyResultsException ex) {
            Logger.getLogger(BindingManager.class.getName()).log(Level.WARNING, null, ex);
        }
        throw new ValueDerivationFailedError();
    }

    public long getLong(String className, String fieldName) throws ValueDerivationFailedError {
        return this.getLong(className, fieldName, new HashSet<>());
    }

    public long getLong(String className, String fieldName, Set<Integer> ignoredHashCodes) throws ValueDerivationFailedError {
        try {
            ObjectFieldPair objfield = this.deriveObjectFieldPair(className, fieldName, ignoredHashCodes);
            return objfield.getLong();
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ObjectExpiredException | TooManyResultsException ex) {
            Logger.getLogger(BindingManager.class.getName()).log(Level.WARNING, null, ex);
        }
        throw new ValueDerivationFailedError();
    }

    public float getFloat(String className, String fieldName) throws ValueDerivationFailedError {
        return this.getFloat(className, fieldName, new HashSet<>());
    }

    public float getFloat(String className, String fieldName, Set<Integer> ignoredHashCodes) throws ValueDerivationFailedError {
        try {
            ObjectFieldPair objfield = this.deriveObjectFieldPair(className, fieldName, ignoredHashCodes);
            return objfield.getFloat();
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ObjectExpiredException | TooManyResultsException ex) {
            Logger.getLogger(BindingManager.class.getName()).log(Level.WARNING, null, ex);
        }
        throw new ValueDerivationFailedError();
    }

    public double getDouble(String className, String fieldName) throws ValueDerivationFailedError {
        return this.getDouble(className, fieldName, new HashSet<>());
    }

    public double getDouble(String className, String fieldName, Set<Integer> ignoredHashCodes) throws ValueDerivationFailedError {
        try {
            ObjectFieldPair objfield = this.deriveObjectFieldPair(className, fieldName, ignoredHashCodes);
            return objfield.getDouble();
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ObjectExpiredException | TooManyResultsException ex) {
            Logger.getLogger(BindingManager.class.getName()).log(Level.WARNING, null, ex);
        }
        throw new ValueDerivationFailedError();
    }

    public boolean getBoolean(String className, String fieldName) throws ValueDerivationFailedError {
        return this.getBoolean(className, fieldName, new HashSet<>());
    }

    public boolean getBoolean(String className, String fieldName, Set<Integer> ignoredHashCodes) throws ValueDerivationFailedError {
        try {
            ObjectFieldPair objfield = this.deriveObjectFieldPair(className, fieldName, ignoredHashCodes);
            return objfield.getBoolean();
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ObjectExpiredException | TooManyResultsException ex) {
            Logger.getLogger(BindingManager.class.getName()).log(Level.WARNING, null, ex);
        }
        throw new ValueDerivationFailedError();
    }

    public Object getObject(String className, String fieldName) throws ValueDerivationFailedError {
        return this.getObject(className, fieldName, new HashSet<>());
    }

    public Object getObject(String className, String fieldName, Set<Integer> ignoredHashCodes) throws ValueDerivationFailedError {
        try {
            ObjectFieldPair objfield = this.deriveObjectFieldPair(className, fieldName, ignoredHashCodes);
            return objfield.get();
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ObjectExpiredException | TooManyResultsException ex) {
            Logger.getLogger(BindingManager.class.getName()).log(Level.WARNING, null, ex);
        }
        throw new ValueDerivationFailedError();
    }

    protected ObjectFieldPair deriveObjectFieldPair(String className, String fieldName, Set<Integer> ignoredHashCodes) throws ValueDerivationFailedError, TooManyResultsException, NoSuchFieldException {
        ignoredHashCodes = new HashSet<>(ignoredHashCodes);
        ignoredHashCodes.add(0); // Always ignore null
        Object targetObj = this.deriveObject(className, true, ignoredHashCodes);
        return new ObjectFieldPair(targetObj, fieldName);
    }
    
    protected Object deriveObject(String className, boolean rebuildMappingsIfFailed, Set<Integer> ignoredHashCodes) throws ValueDerivationFailedError, TooManyResultsException {
        /**
         * Get the ObjectFieldPair that, when evaluated, will yield an Object of
         * class `className`.
         */
        BindingManager.log.log(Level.FINE, "Attempting to derive for {0}", className);

        try {
            // If the object is in the cache and not expired, return it
            CachedObject cachedObj = this.cache.getCachedObject(className, ignoredHashCodes);
            // A null CachedObject represents a cache miss
            if (cachedObj != null) {
                return cachedObj.getObject();
            }
        } catch (TooManyResultsException ex) {
            // There were too many results, this is problematic as we can't know which one to use
            Logger.getLogger(BindingManager.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } catch (CachedObjectExpiredException ex) {
            // Somehow the object expired but made it into the result set, attempt to re-derive
            // the object
            BindingManager.log.log(Level.FINE, ex.getMessage(), ex);
        }

        if (!this.derivativesMap.containsKey(className)) {
            // Target class not yet mapped, let's hope we find it now
            this.buildMappings();
        }
        Set<Derivative> derivs = this.derivativesMap.get(className);
        if (derivs == null) {
            throw new ValueDerivationFailedError();
        }

        for (Derivative d : derivs) {
            // Each Derivative descirbes a way to acquire an object of class className
            BindingManager.log.log(Level.FINE, "Trying {0}", d);

            ObjectFieldPair ownerDerivativePair;
            Object owner;

            if (d.getClassName().equals(className)) {
                // The target class derives itself, this is the case for the BindingManager object
                if (className.equals(BindingManager.class.getName())) {
                    owner = this;
                } else {
                    // This is a very bizarre case, hopefully there's another derivative to check
                    throw new RuntimeException(String.format("deriveObject base-case failed, %s, %s", className, d.getClassName()));
                }
            } else {
                // When trying to derive the owner, allow the owner to also rebuild mappings if failed
                try {
                    owner = this.deriveObject(d.getClassName(), true, ignoredHashCodes);
                } catch (ValueDerivationFailedError ex) {
                    // Failed to derive this owner, just try next possible derivation
                    continue;
                }
            }
            try {
                ownerDerivativePair = new ObjectFieldPair(owner, d.getFieldName());
            } catch (NoSuchFieldException ex) {
                Logger.getLogger(BindingManager.class.getName()).log(Level.FINE, null, ex);
                continue;
            }

            Object candidateObj;
            try {
                candidateObj = ownerDerivativePair.get();
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(BindingManager.class.getName()).log(Level.SEVERE, null, ex);
                throw new ValueDerivationFailedError();
            } catch (ObjectExpiredException ex) {
                Logger.getLogger(BindingManager.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }
            
            // Not caching null
            if (candidateObj != null) {
                CachedObject cachedCandidate = this.cache.addToCache(candidateObj);
                if (!ignoredHashCodes.contains(cachedCandidate.getId()))
                    return candidateObj;
            }
        }

        // We got an object, but we have to make sure it's not in the ignore set
        if (rebuildMappingsIfFailed) {
            this.buildMappings();
            return this.deriveObject(className, false, ignoredHashCodes);
        }
        
        throw new ValueDerivationFailedError();
    }

    public void buildMappings() {
        this.buildMappings(this, this, this.selfField, BindingManager.createDefaultHashSet());
    }

    private void buildMappings(Object o, Object owner, Field derivationField, Set<Integer> seenHashes) {
        CachedObject obj = this.cache.addToCache(o);
        seenHashes.add(obj.getId());
        Class ownerClass = owner.getClass();

        String ownerTypeName = ownerClass.getName();
        String objectTypeName = obj.getClassName();

        Derivative d = new Derivative(ownerTypeName, derivationField);
        Set<Derivative> derivs;
        if (!this.derivativesMap.containsKey(objectTypeName)) {
            derivs = new HashSet<>();
            this.derivativesMap.put(objectTypeName, derivs);
        } else {
            derivs = this.derivativesMap.get(objectTypeName);
        }
        if (derivs.add(d)) {
            log.log(Level.FINE, "Added a Derivative for type {0}: {1}", new Object[]{objectTypeName, d});
        }

        log.log(Level.FINE, "Inspecting object of type {0}", objectTypeName);
        Set<String> fieldNames = this.getClassFields(objectTypeName);

        Set<Field> fields = this.getAllFields(o.getClass());

        log.log(Level.FINE, "{0} has {1} fields: {2}", new Object[]{objectTypeName, fields.size(), fields.toString()});

        for (Field f : fields) {
            Class fieldType = f.getType();

            // Ignore fields for objects that aren't in BindingManager.classPrefixes
            if (fieldType.isPrimitive()) {
                continue;
            }

            fieldNames.add(f.getName());

            try {
                if (!f.canAccess(o)) {
                    f.setAccessible(true);
                }
            } catch (IllegalArgumentException ex) {
                // IllegalArgumentException will be thrown if the field is private, but we can change that
                log.log(Level.FINER, ex.getMessage(), ex);
                f.setAccessible(true);
            }
            Object fieldValue;
            try {
                fieldValue = f.get(o);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                log.log(Level.SEVERE, ex.getClass().getName() + ": " + ex.getMessage() + " (Current obj: " + o.getClass().getName() + ")", ex);
                continue;
            }
            int fieldValueHash = System.identityHashCode(fieldValue);
            log.log(Level.FINE, "{0}.{1} = {2} (hash: {3})", new Object[]{objectTypeName, f.getName(), fieldValue, Integer.toString(fieldValueHash)});
            if (!seenHashes.contains(fieldValueHash)) {
                boolean ignore = false;
                for (String prefix : this.classPrefixes) {
                    if (fieldValue.getClass().getName().startsWith(prefix)) {
                        ignore = false;
                        break;
                    }
                    ignore = true;
                }
                if (!ignore) {
                    this.buildMappings(fieldValue, o, f, seenHashes);
                }
            }
        }
    }

    private Set<Field> getAllFields(Class c) {
        Set<Field> allFields = new HashSet<>();
        if (c == null) {
            return allFields;
        } else if (c == this.getClass()) {
            allFields.add(this.rootObjField);
        } else {
            allFields.addAll(Arrays.asList(c.getDeclaredFields()));
            allFields.addAll(this.getAllFields(c.getSuperclass()));
        }
        return allFields;
    }

    private static Set<Integer> createDefaultHashSet() {
        Set<Integer> set = new HashSet<>();
        set.add(0);
        return set;
    }
}
