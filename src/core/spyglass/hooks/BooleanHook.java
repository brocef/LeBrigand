package lebrigand.core.spyglass.hooks;

import com.sun.jdi.BooleanValue;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;

public class BooleanHook extends DerivedHook {
	private boolean value;
	
//	public BooleanHook(VirtualMachine vm, String classname, String fieldName) {
//		super(vm, classname, fieldName);
//	}
	
	public BooleanHook(VirtualMachine vm, ObjectRefHook hook, String fieldName) {
		super(vm, hook, fieldName);
	}
	
	public HookType getHookType() {
		return HookType.BOOLEAN;
	}
	
	public boolean getValue() {
		if (!initialized && !setUpHook())
			return false;
		Value v = holder.getValue(field);
		if (v == null) {
			initialized = false;
			return false;
		}
		value = ((BooleanValue) v).booleanValue();
		return value;
	}
}
