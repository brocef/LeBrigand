package src.core.spyglass.hooks;

import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;

public abstract class DerivedHook extends Hook {
	public DerivedHook(VirtualMachine vm, ObjectRefHook holder, String fieldName) {
		super(vm, holder, fieldName);
	}

	@Override
	protected boolean setUpHook() {
		return setUpDerivedHook(this);
	}

	public void printDebug() {
		System.out.printf("Derived Hook %s is initialized? %b\r\n", fieldName, initialized);
		System.out.printf("    derived from %s which is initialized? %b\r\n", holder.className, holder.initialized);
	}
	
	protected static boolean setUpDerivedHook(Hook hook) {
//		if (hook.initialized) return true;
		if (hook.holder.setUpHook()) {
//			System.out.println(hook.holder.getObjectReference().referenceType().name());
			ObjectReference ref = hook.holder.getObjectReference();
			if (ref == null)
				return hook.initialized = false;
			ReferenceType type = ref.referenceType();
			Field f = type.fieldByName(hook.fieldName);
			hook.field = f;
//			hook.field = hook.holder.getObjectReference().referenceType().fieldByName(hook.fieldName);
			
			return hook.initialized = true;
		}
		return hook.initialized = false;
	}
}
