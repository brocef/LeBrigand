package lebrigand.core.spyglass.hooks;

import java.util.List;

import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;

public abstract class Hook {
	protected VirtualMachine vm;
	protected String className, fieldName;
	protected ObjectRefHook holder;
	protected Field field;
	protected boolean initialized;

	//	public Hook(VirtualMachine vm, String classname, String fieldName) {
	//		this.vm = vm;
	//		this.className = classname;
	//		this.fieldName = fieldName;
	//		this.holder = null;
	//		this.type = null;
	//		setUpHook();
	//	}

	public Hook(VirtualMachine vm, String className) {
		this.vm = vm;
		this.className = className;
		this.holder = null;
		this.fieldName = null;
		this.field = null;
		this.initialized = false;
		setUpHook();
	}

	public Hook(VirtualMachine vm, ObjectRefHook holder, String fieldName) {
		this.vm = vm;
		this.className = null;
		this.holder = holder;
		this.fieldName = fieldName;
		this.field = null;
		this.initialized = false;
		setUpHook();
	}

	public abstract HookType getHookType();

	public String getClassName() {
		return className == null ? holder.getClassName() : className;
	}

	protected abstract boolean setUpHook();
	
//	protected boolean setUpHook() {
//		initialized = (holder == null || holder.setUpHook());
//
//		if (fieldName == null) {
//			
//			if (type == null)  initialized = false;
//		} else {
//			if (holder.initialized || holder.setUpHook()) {
//				field = holder.getType().fieldByName(fieldName);
////				System.out.println();
////				System.out.println(holder.getTypeName());
////				System.out.println(fieldName);
//				if (this instanceof ObjectRefHook)
//					type = ((ObjectReference) holder.getValue(field)).referenceType();
//				else
//					type = null;
////				System.out.println(type.name());
//			} else {
//				field = null;
//				type = null;
//			}
//		}
//
//		return initialized;
//	}
}
