package vswe.stevesfactory.ui.manager.tool.group;

import vswe.stevesfactory.ui.manager.FactoryManagerGUI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class GroupDataModel {

    private FactoryManagerGUI gui;
    private List<Consumer<String>> addListeners = new ArrayList<>();
    private List<Consumer<String>> removeListeners = new ArrayList<>();
    private List<BiConsumer<String, String>> updateListeners = new ArrayList<>();

    public GroupDataModel(FactoryManagerGUI gui) {
        this.gui = gui;
    }

    public int addListenerAdd(Consumer<String> listener) {
        addListeners.add(listener);
        return addListeners.size() - 1;
    }

    public void removeListenerAdd(int id) {
        addListeners.remove(id);
    }

    public int addListenerRemove(Consumer<String> listener) {
        removeListeners.add(listener);
        return removeListeners.size() - 1;
    }

    public void removeListenerRemove(int id) {
        removeListeners.remove(id);
    }

    public int addListenerUpdate(BiConsumer<String, String> listener) {
        updateListeners.add(listener);
        return updateListeners.size() - 1;
    }

    public void removeListenerUpdate(int id) {
        updateListeners.remove(id);
    }

    public Collection<String> getGroups() {
        return gui.getController().getGroups();
    }

    public boolean addGroup(String group) {
        if (gui.getController().getGroups().add(group)) {
            for (Consumer<String> listener : addListeners) {
                listener.accept(group);
            }
            return true;
        }
        return false;
    }

    public boolean removeGroup(String group) {
        if (gui.getController().getGroups().remove(group)) {
            for (Consumer<String> listener : removeListeners) {
                listener.accept(group);
            }
            return true;
        }
        return false;
    }

    public boolean updateGroup(String from, String to) {
        if (gui.getController().getGroups().remove(from)) {
            gui.getController().getGroups().add(to);
            for (BiConsumer<String, String> listener : updateListeners) {
                listener.accept(from, to);
            }
            return true;
        }
        return false;
    }
}
