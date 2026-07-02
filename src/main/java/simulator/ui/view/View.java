package simulator.ui.view;

import javafx.scene.Parent;

// Common contract for a screen. Each view exposes its root node for mounting.
public interface View {
    Parent getRoot();
}
