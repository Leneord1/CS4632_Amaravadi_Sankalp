package simulator.ui;

import simulator.ui.view.View;

// Test double that captures navigation requests instead of switching scenes.
class RecordingNavigator implements Navigator {
    View lastView;
    int navigateCount;
    boolean exitCalled;

    @Override
    public void navigateTo(View view) {
        this.lastView = view;
        this.navigateCount++;
    }

    @Override
    public void exit() {
        this.exitCalled = true;
    }
}
