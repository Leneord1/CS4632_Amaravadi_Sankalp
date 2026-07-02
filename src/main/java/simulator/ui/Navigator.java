package simulator.ui;

import simulator.ui.view.View;

// Screen switching contract passed to views so they can request navigation.
public interface Navigator {
    void navigateTo(View view);

    void exit();
}
