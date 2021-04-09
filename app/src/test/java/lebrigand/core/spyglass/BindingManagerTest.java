package lebrigand.core.spyglass;

import java.util.HashSet;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

class BindingTestObject {
    boolean bool = false;
    byte b = 1;
    char c = 'a';
    short s = 2;
    int i = 3;
    long l = 4l;
    float f = 1.5f;
    double d = 2.5d;
    BindingTestObject o = null;
    BindingTestObject o2 = null;
}

class BindingTestObject2 extends BindingTestObject {
    
}

class BindingTestObject3 extends BindingTestObject {
    
}


public class BindingManagerTest {
    
    public BindingManagerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of setRootObject method, of class BindingManager.
     */
    @Test
    public void testSetRootObject() {
        System.out.println("setRootObject");
        Object o = new Object();
        BindingManager instance = new BindingManager();
        instance.setRootObject(o);
        if (instance.getRootObject() != o)
            fail("Root obj settter/getter failed");
    }

    /**
     * Test of buildMappings method, of class BindingManager.
     */
    @Test
    public void testBuildMappings() {
        System.out.println("buildMappings");
        BindingManager instance = new BindingManager();
        try {
            // Can't set the root obj to be null
            instance.setRootObject(null);
            fail("Should have failed without a root obj");
        } catch (NullPointerException ex) {}
        // Building mappings succeeds always
        instance.buildMappings();
        
        BindingTestObject root = new BindingTestObject();
        
        try {
            instance.setRootObject(null);
            fail("Cannot build mappings with null root");
        } catch (NullPointerException ex) {}
        assertEquals(1, instance.classFieldMap.size());
        
        instance.setRootObject(root);
        instance.buildMappings();
        
        assertEquals(2, instance.classFieldMap.size());
        
        Set<String> rootDerivs = instance.classFieldMap.get(root.getClass().getName());
        assertEquals(2, rootDerivs.size());
    }    
    
    @Test
    public void testGetInt() {
        System.out.println("getInt");
        BindingManager instance = new BindingManager();
        BindingTestObject root = new BindingTestObject();
        root.i = 4;
        
        instance.setRootObject(root);
        System.out.println(instance.derivativesMap.toString());
        try {
            assertEquals(4, instance.getInt(root.getClass().getName(), "i"));
        } catch (ValueDerivationFailedError ex) {
            fail("Raised ValueDerivationFailedError: "+ex.toString());
        }
    }
    
    @Test(expected = ValueDerivationFailedError.class)
    public void testGetUnknownObject() throws ValueDerivationFailedError {
        System.out.println("testGetUnknownObject");
        BindingManager instance = new BindingManager();
        BindingTestObject root = new BindingTestObject();
        instance.setRootObject(root);
        instance.getInt("fake.class.name", "xyz");
    }

    @Test
    public void testNestedGetInt() throws ValueDerivationFailedError {
        System.out.println("testNestedGetInt");
        BindingManager instance = new BindingManager();
        BindingTestObject root = new BindingTestObject();
        BindingTestObject2 child_1 = new BindingTestObject2();
        BindingTestObject3 child_2 = new BindingTestObject3();
        root.o = child_1;
        child_1.o2 = child_2;
        child_2.i = 8;
        
        instance.setRootObject(root);
        assertEquals(8, instance.getInt(child_2.getClass().getName(), "i"));
    }
    
    @Test
    public void testHandleObjectExpiration() throws ValueDerivationFailedError, IllegalArgumentException, IllegalAccessException, ObjectExpiredException {
        System.out.println("testHandleObjectExpiration");
        BindingManager instance = new BindingManager();
        BindingTestObject root = new BindingTestObject();
        BindingTestObject3 child_2 = new BindingTestObject3();
        root.o = new BindingTestObject2();
        root.o.o2 = child_2;
        child_2.i = 8;
        
        instance.setRootObject(root);
        
        assertEquals(8, instance.getInt(child_2.getClass().getName(), "i"));
        ObjectFieldPair child_2_owner = instance.getDerivative(child_2.getClass().getName());
        assertEquals(child_2, child_2_owner.get());
        assertEquals(root.o, child_2_owner.getObject());
        
        // Remove refs to child_1 and then force garbage collection
        root.o = null;
        System.gc();
        
        try {
            child_2_owner.getObject();
            fail("Expected child_1 to have expired");
        } catch (ObjectExpiredException ex) {}
        
        root.o2 = child_2;
        // Now make child_2 owned by root, we should be able to get the right derivative
        assertEquals(8, instance.getInt(child_2.getClass().getName(), "i"));
        ObjectFieldPair new_child_2_owner = instance.getDerivative(child_2.getClass().getName());
        assertEquals(child_2, new_child_2_owner.get());
        assertEquals(root, new_child_2_owner.getObject());
    }
    
    @Test
    public void testFindObjectWithIdBlacklist() throws ValueDerivationFailedError {
        System.out.println("testFindObjectWithIdBlacklist");
        BindingManager instance = new BindingManager();
        BindingTestObject root = new BindingTestObject();
        BindingTestObject2 child_1 = new BindingTestObject2();
        BindingTestObject3 child_2 = new BindingTestObject3();
        BindingTestObject3 child_3 = new BindingTestObject3();
        BindingTestObject3 child_4 = new BindingTestObject3();
        root.o = child_1;
        root.o2 = child_3;
        child_1.o = child_4;
        child_1.o2 = child_2;
        
        child_2.i = 7;
        child_3.i = 8;
        child_4.i = 9;
        
        instance.setRootObject(root);
        String targetClass = BindingTestObject3.class.getName();
        Set<Integer> ignoredIds = new HashSet<>();
        ignoredIds.add(System.identityHashCode(child_3));
        ignoredIds.add(System.identityHashCode(child_4));
        assertEquals(7, instance.getInt(targetClass, "i", ignoredIds));
        
        ignoredIds.clear();
        ignoredIds.add(System.identityHashCode(child_2));
        ignoredIds.add(System.identityHashCode(child_4));
        assertEquals(8, instance.getInt(targetClass, "i", ignoredIds));
        
        ignoredIds.clear();
        ignoredIds.add(System.identityHashCode(child_2));
        ignoredIds.add(System.identityHashCode(child_3));
        assertEquals(9, instance.getInt(targetClass, "i", ignoredIds));
        
        ignoredIds.add(System.identityHashCode(child_4));
        System.out.println();
        System.out.println(ignoredIds.toString());
        System.out.println();
        try {
            int val = instance.getInt(targetClass, "i", ignoredIds);
            fail("Should not have resolved to a value! "+val);
        } catch (ValueDerivationFailedError ex) {}
    }
}