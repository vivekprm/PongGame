package com.pong;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;
import static java.lang.Math.abs;
import static java.lang.Math.signum;

public class BallComponent extends Component {
    private PhysicsComponent physics;
    @Override
    public void onUpdate(double tpf) {
        limitVelocity();
        checkOffscreen();
    }
    private void limitVelocity() {
        // don't move too slow in X direction
        if (abs(physics.getVelocityX()) < 5 * 60) {
            physics.setVelocityX(signum(physics.getVelocityX()) * 5 * 60);
        }
        // don't move too fast in Y direction
        if (abs(physics.getVelocityY()) > 5 * 60 * 2) {
            physics.setVelocityY(signum(physics.getVelocityY()) * 5 * 60);
        }
    }
    // we use a physics engine, so it is possible to push the ball through a wall to outside of the screen, hence the check
    private void checkOffscreen() {
        var viewport = getGameScene().getViewport();
        var visArea = viewport.getVisibleArea();
        if (getEntity().getBoundingBoxComponent().isOutside(visArea)) {
            physics.overwritePosition(new Point2D(
                    getAppWidth() / 2,
                    getAppHeight() / 2
            ));
        }
    }
}
