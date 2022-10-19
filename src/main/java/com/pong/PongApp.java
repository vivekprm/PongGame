package com.pong;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

public class PongApp extends GameApplication {
    private BatComponent playerBat;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Pong");
        settings.setVersion("1.0");
    }
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Up") {
            @Override
            protected void onAction() {
                playerBat.up();
            }
            @Override
            protected void onActionEnd() {
                playerBat.stop();
            }
        }, KeyCode.W);
        getInput().addAction(new UserAction("Down") {
            @Override
            protected void onAction() {
                playerBat.down();
            }
            @Override
            protected void onActionEnd() {
                playerBat.stop();
            }
        }, KeyCode.S);
    }
    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("player1score", 0);
        vars.put("player2score", 0);
    }
    @Override
    protected void initGame() {
        getWorldProperties().<Integer>addListener("player1score", (old, newScore) -> {
            if (newScore == 11) {
                showGameOver("Player 1");
            }
        });
        getWorldProperties().<Integer>addListener("player2score", (old, newScore) -> {
            if (newScore == 11) {
                showGameOver("Player 2");
            }
        });
        getGameWorld().addEntityFactory(new PongFactory());
        getGameScene().setBackgroundColor(Color.rgb(0, 0, 5));
        initScreenBounds();
        initGameObjects();
    }
    private void showGameOver(String winner) {
        getDialogService().showMessageBox(winner + " won!", getGameController()::exit);
    }
    private void initScreenBounds() {
        Entity walls =
                entityBuilder()
                        .type(EntityType.WALL)
                        .collidable()
                        .buildScreenBounds(150);
        getGameWorld().addEntity(walls);
    }
    private void initGameObjects() {
        Entity ball = spawn("ball", getAppWidth() / 2 - 5, getAppHeight() / 2 - 5);
        Entity bat1 = spawn("bat", new SpawnData(getAppWidth() / 4, getAppHeight() / 2 - 30).put("isPlayer", true));
        Entity bat2 = spawn("bat", new SpawnData(3 * getAppWidth() / 4 - 20, getAppHeight() / 2 - 30).put("isPlayer", false));
        playerBat = bat1.getComponent(BatComponent.class);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0, 0);
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BALL, EntityType.WALL) {
            @Override
            protected void onHitBoxTrigger(Entity a, Entity b, HitBox boxA, HitBox boxB) {
                if (boxB.getName().equals("LEFT")) {
                    inc("player2score", +1);
                } else if (boxB.getName().equals("RIGHT")) {
                    inc("player1score", +1);
                }
            }
        });
    }

    @Override
    protected void initUI() {
        Text textPixels1 = new Text();
        textPixels1.setTranslateX(20); // x = 50
        textPixels1.setTranslateY(20); // y = 100
        textPixels1.setFill(Color.GREEN);
        FXGL.getGameScene().addUINode(textPixels1); // add to the screen
        textPixels1.textProperty().bind(FXGL.getWorldProperties().intProperty("player1score").asString());

        Text textPixels2 = new Text();
        textPixels2.setTranslateX(FXGL.getAppWidth() - 20); // x = 50
        textPixels2.setTranslateY(20); // y = 100
        textPixels2.setFill(Color.RED);
        FXGL.getGameScene().addUINode(textPixels2); // add to the screen
        textPixels2.textProperty().bind(FXGL.getWorldProperties().intProperty("player2score").asString());
    }
}
