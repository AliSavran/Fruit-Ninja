package com.alisavran.fruitninjaclone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class Fruit {

    public static float radius = 60f;
    public enum Type {
        APPLE, BANANA,COCONUT,CHERRY,EXTRA, ENEMY,BOMB, LIFE;
    }
        Type type;
        Vector2 position, velocity;
        public boolean living;

        Fruit(Vector2 position, Vector2 velocity, Type type){
            this.position = position;
            this.velocity = velocity;
            this.type = type;
        }

        public  boolean clicked(Vector2 click){
            if (position.dst2(click) <= radius * radius +1){
                return true;
            }
            return false;
        }

        public final Vector2 getPosition(){
            return position;
        }

        public boolean outOfScreen(){
            return (position.y < -2f * radius);
        }

        public void update(float dt){
            velocity.y -= dt * (Gdx.graphics.getHeight() * 0.2f);
            velocity.x -= dt * Math.signum(velocity.x) * 5f;
            position.mulAdd(velocity,dt);
        }
}
