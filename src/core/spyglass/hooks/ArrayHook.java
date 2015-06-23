package lebrigand.core.spyglass.hooks;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;

public class ArrayHook extends DerivedHook {
	private ArrayReference value;
	private boolean refresh;
	
//	public ArrayHook(VirtualMachine vm, String classname, String fieldName) {
//		super(vm, classname, fieldName);
//	}
	
	public ArrayHook(VirtualMachine vm, ObjectRefHook hook, String fieldName, boolean refresh) {
		super(vm, hook, fieldName);
		this.refresh = refresh;
	}
	
	public ArrayHook(VirtualMachine vm, ObjectRefHook hook, String fieldName) {
		this(vm, hook, fieldName, false);
	}
	
	public HookType getHookType() {
		return HookType.ARRAY;
	}
	
	public ArrayReference getValue() {
		if (!initialized && !setUpHook())
			return null;
		Value v = holder.getValue(field, refresh);
		if (v == null)
			value = null;
		else
			value = (ArrayReference) v;
		return value;
	}
}
