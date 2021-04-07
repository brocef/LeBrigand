package lebrigand.core.spyglass.hooks;

import java.util.List;

import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;

public class ObjectRefHook extends Hook {
	private ObjectReference value;
	private ReferenceType type;

	//	public ObjectRefHook(VirtualMachine vm, String classname, String fieldName) {
	//		super(vm, classname, fieldName);
	//	}

	public ObjectRefHook(VirtualMachine vm, ObjectRefHook hook, String fieldName) {
		super(vm, hook, fieldName);
	}

	public ObjectRefHook(VirtualMachine vm, String classname) {
		super(vm, classname);
	}

	public HookType getHookType() {
		return HookType.OBJECTREF;
	}
	
	public void printDebug() {
		System.out.printf("Object Reference Hook of %s is initialzed? %b\r\n",
				(className == null ? fieldName : className), initialized);
	}

	protected boolean setUpHook() {
		if (holder == null) {
			if (initialized) return true;
			List<ReferenceType> classes = vm.classesByName(className);
			this.type = null;
			for (ReferenceType ref:classes) {
				if (ref.name().equals(className)) {
					this.type = ref;
					return initialized = true;
				}
			}
		} else if (DerivedHook.setUpDerivedHook(this)) {
			if (field != null)
				this.type = field.declaringType();
			return initialized = (field != null);
		}
		return initialized = false;
	}

	public String getTypeName() {
		return (getObjectReference() == null ? null : getObjectReference().referenceType().name());
	}

	protected ReferenceType getType() {
		if (!initialized)
			setUpHook();
		return this.type;
	}

	public Value getValue(Field f) {
		return getValue(f, false);
	}

	public Value getValue(Field f, boolean refresh) {
		if (value == null || value.isCollected() || refresh)
			value = getObjectReference();
		return value == null ? null : value.getValue(f);
	}

	public ObjectReference getObjectReference() {
		if (!initialized && !setUpHook())
			return null;
		if (field == null) {
			value = null;
			List<ObjectReference> refs = this.type.instances(1);
			if (refs.isEmpty())
				initialized = false;
			else
				value = refs.get(0);
		} else {
			Value v = holder.getValue(field);
			if (v == null)
				value = null;
			else
				value = (ObjectReference) v;
		}
		return value;
	}
}
