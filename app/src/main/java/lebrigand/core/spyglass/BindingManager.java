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
    public final Map<String, Set<String>> classFieldMap;
    // Map "value_class_name" -> List<Derivative>
    public final Map<String, Set<Derivative>> derivativesMap;

    private final String[] classPrefixes;

    public BindingManager(String[] classPrefixes) {
        try {
            this.rootObj = null;
            this.classFieldMap = new HashMap<>();
            this.derivativesMap = new HashMap<>();
            this.classPrefixes = classPrefixes;
            this.rootObjField = BindingManager.class.getDeclaredField("rootObj");
            this.self = this;
            this.selfField = BindingManager.class.getDeclaredField("self");
            this.createSelfBindings();
        } catch (NoSuchFieldException | SecurityException ex) {
            Logger.getLogger(BindingManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException();
        }
    }

    public BindingManager() {
        this(new String[0]);
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
            ObjectFieldPair objfield = this.getDerivative(className, ignoredHashCodes).getAsObjectFieldPair(fieldName);
            return objfield.getInt();
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ObjectExpiredException ex) {
            Logger.getLogger(BindingManager.class.getName()).log(Level.WARNING, null, ex);
        }
        throw new ValueDerivationFailedError();
    }

    public char getChar(String className, String fieldName) throws ValueDerivationFailedError {
        return this.getChar(className, fieldName, new HashSet<>());
    }

    public char getChar(String className, String fieldName, Set<Integer> ignoredHashCodes) throws ValueDerivationFailedError {
        try {
            ObjectFieldPair objfield = this.getDerivative(className, ignoredHashCodes).getAsObjectFieldPair(fieldName);
            return objfield.getChar();
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ObjectExpiredException ex) {
            Logger.getLogger(BindingManager.class.getName()).log(Level.WARNING, null, ex);
        }
        throw new ValueDerivationFailedError();
    }

    public byte getByte(String className, String fieldName) throws ValueDerivationFailedError {
        return this.getByte(className, fieldName, new HashSet<>());
    }

    public byte getByte(String className, String fieldName, Set<Integer> ignoredHashCodes) throws ValueDerivationFailedError {
        try {
            ObjectFieldPair objfield = this.getDerivative(className, ignoredHashCodes).getAsObjectFieldPair(fieldName);
            return objfield.getByte();
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ObjectExpiredException ex) {
            Logger.getLogger(BindingManager.class.getName()).log(Level.WARNING, null, ex);
        }
        throw new ValueDerivationFailedError();
    }

    public short getShort(String className, String fieldName) throws ValueDerivationFailedError {
        return this.getShort(className, fieldName, new HashSet<>());
    }

    public short getShort(String className, String fieldName, Set<Integer> ignoredHashCodes) throws ValueDerivationFailedError {
        try {
            ObjectFieldPair objfield = this.getDerivative(className, ignoredHashCodes).getAsObjectFieldPair(fieldName);
            return objfield.getShort();
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ObjectExpiredException ex) {
            Logger.getLogger(BindingManager.class.getName()).log(Level.WARNING, null, ex);
        }
        throw new ValueDerivationFailedError();
    }

    public long getLong(String className, String fieldName) throws ValueDerivationFailedError {
        return this.getLong(className, fieldName, new HashSet<>());
    }

    public long getLong(String className, String fieldName, Set<Integer> ignoredHashCodes) throws ValueDerivationFailedError {
        try {
            ObjectFieldPair objfield = this.getDerivative(className, ignoredHashCodes).getAsObjectFieldPair(fieldName);
            return objfield.getLong();
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ObjectExpiredException ex) {
            Logger.getLogger(BindingManager.class.getName()).log(Level.WARNING, null, ex);
        }
        throw new ValueDerivationFailedError();
    }

    public float getFloat(String className, String fieldName) throws ValueDerivationFailedError {
        return this.getFloat(className, fieldName, new HashSet<>());
    }

    public float getFloat(String className, String fieldName, Set<Integer> ignoredHashCodes) throws ValueDerivationFailedError {
        try {
            ObjectFieldPair objfield = this.getDerivative(className, ignoredHashCodes).getAsObjectFieldPair(fieldName);
            return objfield.getFloat();
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ObjectExpiredException ex) {
            Logger.getLogger(BindingManager.class.getName()).log(Level.WARNING, null, ex);
        }
        throw new ValueDerivationFailedError();
    }

    public double getDouble(String className, String fieldName) throws ValueDerivationFailedError {
        return this.getDouble(className, fieldName, new HashSet<>());
    }

    public double getDouble(String className, String fieldName, Set<Integer> ignoredHashCodes) throws ValueDerivationFailedError {
        try {
            ObjectFieldPair objfield = this.getDerivative(className, ignoredHashCodes).getAsObjectFieldPair(fieldName);
            return objfield.getDouble();
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ObjectExpiredException ex) {
            Logger.getLogger(BindingManager.class.getName()).log(Level.WARNING, null, ex);
        }
        throw new ValueDerivationFailedError();
    }

    public boolean getBoolean(String className, String fieldName) throws ValueDerivationFailedError {
        return this.getBoolean(className, fieldName, new HashSet<>());
    }

    public boolean getBoolean(String className, String fieldName, Set<Integer> ignoredHashCodes) throws ValueDerivationFailedError {
        try {
            ObjectFieldPair objfield = this.getDerivative(className, ignoredHashCodes).getAsObjectFieldPair(fieldName);
            return objfield.getBoolean();
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ObjectExpiredException ex) {
            Logger.getLogger(BindingManager.class.getName()).log(Level.WARNING, null, ex);
        }
        throw new ValueDerivationFailedError();
    }

    public Object getObject(String className, String fieldName) throws ValueDerivationFailedError {
        return this.getObject(className, fieldName, new HashSet<>());
    }

    public Object getObject(String className, String fieldName, Set<Integer> ignoredHashCodes) throws ValueDerivationFailedError {
        try {
            ObjectFieldPair objfield = this.getDerivative(className, ignoredHashCodes).getAsObjectFieldPair(fieldName);
            return objfield.get();
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ObjectExpiredException ex) {
            Logger.getLogger(BindingManager.class.getName()).log(Level.WARNING, null, ex);
        }
        throw new ValueDerivationFailedError();
    }

    protected ObjectFieldPair getDerivative(String className) throws ValueDerivationFailedError {
        return this.getDerivative(className, true);
    }

    protected ObjectFieldPair getDerivative(String className, Set<Integer> ignoredHashCodes) throws ValueDerivationFailedError {
        ignoredHashCodes = new HashSet<>(ignoredHashCodes);
        ignoredHashCodes.add(0); // Always ignore null
        return this.getDerivative(className, true, ignoredHashCodes);
    }

    private ObjectFieldPair getDerivative(String className, boolean rebuildMappingsIfFailed) throws ValueDerivationFailedError {
        return this.getDerivative(className, rebuildMappingsIfFailed, BindingManager.createDefaultHashSet());
    }

    private ObjectFieldPair getDerivative(String className, boolean rebuildMappingsIfFailed, Set<Integer> ignoredHashCodes) throws ValueDerivationFailedError {
        /**
         * Get the ObjectFieldPair that, when evaluated, will yield an Object of
         * class `className`.
         */
        BindingManager.log.log(Level.INFO, "Attempting to get derivative for {0}", className);
        // The null hash code is always ignored
        if (!this.derivativesMap.containsKey(className)) {
            // Target class not yet mapped, let's hope we find it now
            this.buildMappings();
        }
        Set<Derivative> derivs = this.derivativesMap.get(className);
        if (derivs == null) {
            throw new ValueDerivationFailedError();
        }

        for (Derivative d : derivs) {
            BindingManager.log.log(Level.INFO, "Trying {0}", d);
            // For each derivative that will yield a className object, try to resolve one of them

            try {
                ObjectFieldPair ownerDerivativePair;
                if (d.getClassName().equals(className)) {
                    // The target class derives itself, this is the case for the BindingManager object
                    if (className.equals(BindingManager.class.getName())) {
                        ownerDerivativePair = new ObjectFieldPair(this, this.selfField);
                    } else {
                        // This is a very bizarre case, hopefully there's another derivative to check
                        throw new ValueDerivationFailedError();
                    }
                } else {
                    ownerDerivativePair = this.getDerivative(d.getClassName(), ignoredHashCodes);
                }
                Object targetOwner = ownerDerivativePair.getField().get(ownerDerivativePair.getObject());
                Field targetField = Utils.getField(targetOwner.getClass(), d.getFieldName());
                // And just before we actually return this new pair, make sure that isn't in the ignore list
                ObjectFieldPair targetPair = new ObjectFieldPair(targetOwner, targetField);
                Object targetObject = targetPair.get();
                if (ignoredHashCodes.contains(System.identityHashCode(targetObject))) {
                    continue;
                }
                return targetPair;
            } catch (ValueDerivationFailedError | IllegalArgumentException | IllegalAccessException | ObjectExpiredException ex) {
                log.log(Level.INFO, String.format("Failed to use %s, trying next one if possible", d), ex);
            } catch (NoSuchFieldException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
        if (rebuildMappingsIfFailed) {
            this.buildMappings();
            return this.getDerivative(className, false, ignoredHashCodes);
        } else {
            throw new ValueDerivationFailedError();
        }
    }

    public void buildMappings() {
        this.buildMappings(this, this, this.selfField, BindingManager.createDefaultHashSet());
    }

    private void buildMappings(Object o, Object owner, Field derivationField, Set<Integer> seenHashes) {
        seenHashes.add(System.identityHashCode(o));
        Class ownerClass = owner.getClass();
        Class c = o.getClass();

        String ownerTypeName = ownerClass.getName();
        String objectTypeName = c.getName();

        Derivative d = new Derivative(ownerTypeName, derivationField);
        Set<Derivative> derivs;
        if (!this.derivativesMap.containsKey(objectTypeName)) {
            derivs = new HashSet<>();
            this.derivativesMap.put(objectTypeName, derivs);
        } else {
            derivs = this.derivativesMap.get(objectTypeName);
        }
        if (derivs.add(d)) {
            log.log(Level.INFO, "Added a Derivative for type {0}: {1}", new Object[]{objectTypeName, d});
        }

        log.log(Level.INFO, "Inspecting object of type {0}", c.getName());
        Set<String> fieldNames;
        if (!this.classFieldMap.containsKey(c.getName())) {
            fieldNames = new HashSet<>();
            this.classFieldMap.put(c.getName(), fieldNames);
        } else {
            fieldNames = this.classFieldMap.get(c.getName());
        }

        Set<Field> fields = this.getAllFields(c);

        log.log(Level.INFO, "{0} has {1} fields: {2}", new Object[]{objectTypeName, fields.size(), fields.toString()});

        for (Field f : fields) {
            Class fieldType = f.getType();

            // Ignore fields for objects that aren't in BindingManager.classPrefixes
            if (!fieldType.isPrimitive()) {
                boolean ignore = false;
                for (String prefix : this.classPrefixes) {
                    if (fieldType.getName().startsWith(prefix)) {
                        ignore = false;
                        break;
                    }
                    // If there's at least one classPrefix, then ignore is true until a match is found
                    ignore = true;
                }
                if (ignore) {
                    continue;
                }
            } else {
                continue;
            }

            fieldNames.add(f.getName());

            try {
                if (!f.canAccess(o)) {
                    f.setAccessible(true);
                }
                Object fieldValue = f.get(o);
                int fieldValueHash = System.identityHashCode(fieldValue);
                log.log(Level.INFO, "{0}.{1} = {2} (hash: {3})", new Object[]{objectTypeName, f.getName(), fieldValue, Integer.toString(fieldValueHash)});
                if (!seenHashes.contains(fieldValueHash)) {
                    this.buildMappings(fieldValue, o, f, seenHashes);
                }
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                log.log(Level.SEVERE, null, ex);
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
