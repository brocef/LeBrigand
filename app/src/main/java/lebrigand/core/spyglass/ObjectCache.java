package lebrigand.core.spyglass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ObjectCache {

    private final Map<String, Set<CachedObject>> cachedObjectMap;
    private final Map<Integer, CachedObject> cachedIds;

    public ObjectCache() {
        this.cachedObjectMap = new HashMap<>();
        this.cachedIds = new HashMap<>();
    }

    public void clear() {
        this.cachedIds.clear();
        this.cachedObjectMap.clear();
    }
    
    public boolean contains(int id) {
        return this.cachedIds.containsKey(id);
    }
    
    public CachedObject getCachedObject(String className) throws TooManyResultsException {
        return this.getCachedObject(className, new HashSet<>());
    }

    public CachedObject getCachedObject(String className, Set<Integer> ignoredIds) throws TooManyResultsException {
        Set<CachedObject> objs = this.getCachedObjects(className);
        ArrayList<CachedObject> candidates = new ArrayList<>();
        for (CachedObject o : objs) {
            if (!ignoredIds.contains(o.getId())) {
                candidates.add(o);
            }
        }
        if (candidates.size() > 1) {
            throw new TooManyResultsException();
        } else if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(0);
    }

    public Set<CachedObject> getCachedObjects(String className) {
        Set<CachedObject> cacheSet;
        if (this.cachedObjectMap.containsKey(className)) {
            cacheSet = this.cachedObjectMap.get(className);
        } else {
            cacheSet = new HashSet<>();
            this.cachedObjectMap.put(className, cacheSet);
        }
        // Removed expired objects before returning cached objects in an attempt
        // to keep this cache up-to-date
        return this.pruneExpiredObjects(cacheSet);
    }

    private Set<CachedObject> pruneExpiredObjects(Set<CachedObject> cacheSet) {
        Set<CachedObject> duplicate = new HashSet<>(cacheSet);
        for (CachedObject obj : duplicate) {
            if (obj.hasExpired()) {
                cacheSet.remove(obj);
                this.cachedIds.remove(obj.getId());
            }
        }
        return cacheSet;
    }

    public CachedObject addToCache(Object obj) {
        int id = System.identityHashCode(obj);
        if (this.cachedIds.containsKey(id)) {
            return this.cachedIds.get(id);
        }
        CachedObject cachedObj = new CachedObject(obj);
        this.cachedIds.put(id, cachedObj);
        this.getCachedObjects(cachedObj.getClassName()).add(cachedObj);
        return cachedObj;
    }
}
