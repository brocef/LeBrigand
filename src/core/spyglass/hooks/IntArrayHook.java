package lebrigand.core.spyglass.hooks;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.VirtualMachine;

public class IntArrayHook extends ArrayHook {
	private int[] value;
	
//	public IntArrayHook(VirtualMachine vm, String classname, String fieldName) {
//		super(vm, classname, fieldName);
//	}
	
	public IntArrayHook(VirtualMachine vm, ObjectRefHook hook, String fieldName) {
		super(vm, hook, fieldName, false);
	}
	
	public IntArrayHook(VirtualMachine vm, ObjectRefHook hook, String fieldName, boolean refresh) {
		super(vm, hook, fieldName, refresh);
	}
	
	@Override
	public HookType getHookType() {
		return HookType.INTARRAY;
	}

	public int[] getIntArray() {
		return getIntArray(false);
	}
	
	public int[] getIntArray(boolean reversed) {
		ArrayReference ref = getValue();
		if (ref == null)
			value = null;
		else {
			if (value == null)
				value = new int[ref.length()];
			for (int i=0; i<value.length; i++)
				value[value.length-1-i] = ((IntegerValue) ref.getValue(i)).intValue();
		}
		return value;
	}
}
