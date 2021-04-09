package lebrigand.core.spyglass;

import java.awt.Component;
import java.awt.Container;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ComponentManager {

    private Container root;

    public ComponentManager() {
        this.root = null;
    }

    public void setRoot(Container root) {
        this.root = root;
    }

    public List<WeakReference<Component>> findComponents(String componentClassName) {
        return this.findComponents(componentClassName, this.root, new ArrayList<>());
    }

    private List<WeakReference<Component>> findComponents(String componentClassName, Container container, List<WeakReference<Component>> comps) {
        for (Component comp : container.getComponents()) {
            if (comp.getClass().getName().equals(componentClassName)) {
                comps.add(new WeakReference(comp));
            }
            if (comp instanceof Container) {
                return this.findComponents(componentClassName, (Container) comp, comps);
            }
        }
        return comps;
    }

    public WeakReference<Component> findComponent(String componentClassName) {
        List<WeakReference<Component>> comps = this.findComponents(componentClassName);

        if (comps.size() != 1) {
            throw new IllegalStateException();
        }

        return comps.get(0);
    }
}
