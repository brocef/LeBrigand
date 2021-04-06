package src.core.spyglass.hooks;

import com.sun.jdi.IntegerValue;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;

public class IntegerHook extends DerivedHook {
	private int value;
	
//	public IntegerHook(VirtualMachine vm, String classname, String fieldName) {
//		super(vm, classname, fieldName);
//	}
	
	public IntegerHook(VirtualMachine vm, ObjectRefHook hook, String fieldName) {
		super(vm, hook, fieldName);
	}
	
	public HookType getHookType() {
		return HookType.INTEGER;
	}
	
	public int getValue() {
		if (!initialized && !setUpHook())
			return -1;
		Value v = holder.getValue(field);
		if (v == null) {
			initialized = false;
			return -1;
		}
		value = ((IntegerValue) v).intValue();
		return value;
	}
}
