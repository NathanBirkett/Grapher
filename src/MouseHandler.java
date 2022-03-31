import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class MouseHandler implements MouseWheelListener {
    int wheelAmount;

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        wheelAmount += e.getScrollAmount();
    }
}
