package com.alisavran.fruitninjaclone;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Random;

public class FruitNinja extends ApplicationAdapter implements InputProcessor { // inputprocessor kullanıcının uygulama esnasında klavye ve mouse hareketlerini algılar
	SpriteBatch batch;
	Texture background;
	Texture apple;
	Texture banana;
	Texture cherry;
	Texture coconut;
	Texture bomb;
	Texture coin;
	Texture rotten;
	Texture heart;

	BitmapFont font;
	FreeTypeFontGenerator fontGenerator;
	Random random = new Random();
	Array<Fruit> fruitArray = new Array<Fruit>();

	int lives = 5;
	int score = 0;

	float genCounter = 0;
	private final float startGenSpeed = 1.5f;
	float genSpeed = startGenSpeed;

	private double currentTime;
	private double gameOverTime = -1.0f;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("background.png");
		apple = new Texture("apple.png");
		banana = new Texture("banana.png");
		cherry = new Texture("cherry.png");
		coconut = new Texture("coconut.png");
		bomb = new Texture("bomb.png");
		coin = new Texture("golden_.png");
		rotten = new Texture("rotten.png");
		heart = new Texture("heart.png");
		Fruit.radius = Math.max(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) /15f;
		Gdx.input.setInputProcessor(this);

		fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Medium.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
		params.color = Color.BLUE;
		params.size = 40;
		params.characters = "0123456789 ScreCutoplay:.+-!";
		font = fontGenerator.generateFont(params);

	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		double newTime = TimeUtils.millis()/1000.0;
		System.out.println("New Time :" + newTime);
		float deltaTime = (float) Math.min(newTime -currentTime,0.3f);
		System.out.println("Delta Time : " + deltaTime);
		currentTime = newTime;

		addItem();

		if (lives <= 0 && gameOverTime == 0f){
			//game over
			gameOverTime = currentTime;
		}

		if (lives>0){
			//game mode

			genSpeed -= deltaTime*0.001f;

			if (genSpeed < 0.5f) {
				genSpeed = 0.5f;
			}

			System.out.println("genspeed = " + genSpeed);
			System.out.println("gencounter = " + genCounter);

			if (genCounter <= 0f){
				genCounter = genSpeed;
				addItem();
			}else{
				genCounter -= deltaTime;
			}

			for (int i = 0; i<lives; i++){ // canları çizdirme
				batch.draw(heart,i*25f+20f,Gdx.graphics.getHeight()-35f,30f,30f);
			}

			for (Fruit fruit : fruitArray){
				fruit.update(deltaTime);

				switch (fruit.type){
					case APPLE:
						batch.draw(apple,fruit.getPosition().x,fruit.getPosition().y,Fruit.radius,Fruit.radius);
						break;
					case BANANA:
						batch.draw(banana,fruit.getPosition().x,fruit.getPosition().y,Fruit.radius,Fruit.radius);
						break;
					case COCONUT:
						batch.draw(coconut,fruit.getPosition().x,fruit.getPosition().y,Fruit.radius,Fruit.radius);
						break;
					case CHERRY:
						batch.draw(cherry,fruit.getPosition().x,fruit.getPosition().y,Fruit.radius,Fruit.radius);
						break;
					case EXTRA:
						batch.draw(coin,fruit.getPosition().x,fruit.getPosition().y,Fruit.radius,Fruit.radius);
						break;
					case ENEMY:
						batch.draw(rotten,fruit.getPosition().x,fruit.getPosition().y,Fruit.radius,Fruit.radius);
						break;
					case BOMB:
						batch.draw(bomb,fruit.getPosition().x,fruit.getPosition().y,Fruit.radius,Fruit.radius);
						break;
					case LIFE:
						batch.draw(heart,fruit.getPosition().x,fruit.getPosition().y,Fruit.radius,Fruit.radius);
						break;
				}

			}

			boolean holdLives = false;
			Array<Fruit> toRemove = new Array<Fruit>();
			for (Fruit fruit : fruitArray){
				if(fruit.outOfScreen()){
					toRemove.add(fruit);

					if (fruit.living && fruit.type == Fruit.Type.APPLE){
						lives --;
						holdLives = true;
						break;
					} else if (fruit.living && fruit.type == Fruit.Type.BANANA) {
						lives --;
						holdLives = true;
						break;
					}else if (fruit.living && fruit.type == Fruit.Type.CHERRY) {
						lives --;
						holdLives = true;
						break;
					}else if (fruit.living && fruit.type == Fruit.Type.COCONUT) {
						lives --;
						holdLives = true;
						break;
					}
				}
			}

			if (holdLives){
				for (Fruit f: fruitArray){
					f.living=false;
				}
			}

			for (Fruit f: toRemove){
				fruitArray.removeValue(f,true);
			}
		}


		font.draw(batch,"Score:"+score,30,40);
		if (lives <= 0){
			font.draw(batch,"Cut to play!",Gdx.graphics.getWidth()*0.5f,Gdx.graphics.getHeight()*0.5f);
		}
		batch.end();
	}

	private void addItem() {
		// Meyve oluşturulmadan önce sayaç kontrolü yapılır
		if (genCounter <= 0) {
			float position = random.nextFloat() * Math.max(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			Vector2 velocity = new Vector2((Gdx.graphics.getWidth() * 0.5f - position) * (0.3f + (random.nextFloat() - 0.5f)), Gdx.graphics.getHeight() * 0.5f);

			float typeChance = random.nextFloat();
			Fruit.Type type;

			// Rastgele meyve türünü belirliyoruz
			if (typeChance > 0.98) {
				type = Fruit.Type.LIFE;
			} else if (typeChance > 0.90) {
				type = Fruit.Type.EXTRA;
			} else if (typeChance>0.54) {
				type = Fruit.Type.BOMB;
			} else if (typeChance > 0.55) {
				type = Fruit.Type.ENEMY;
			} else if (typeChance > 0.61) {
				type = Fruit.Type.CHERRY;
			} else if (typeChance > 0.50) {
				type = Fruit.Type.BANANA;
			} else if (typeChance > 0.45) {
				type = Fruit.Type.COCONUT;
			} else {
				type = Fruit.Type.APPLE;
			}

			// Yeni bir meyve nesnesi oluşturup ekliyoruz
			Fruit item = new Fruit(new Vector2(position, -Fruit.radius), velocity, type);
			fruitArray.add(item);

			// Sayaç sıfırlanır ve yeni meyve eklenme süresi başlatılır
			genCounter = genSpeed;
		}
	}


	@Override
	public void dispose () {
		batch.dispose();
		font.dispose();
		fontGenerator.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {

		if (lives <= 0 && currentTime  - gameOverTime > 2f){
			//menu mode

			gameOverTime = 0f;
			score = 0;
			lives = 5;
			genSpeed = startGenSpeed;
			fruitArray.clear();

		}else {
			//game mode

			Array<Fruit> toRemove = new Array<Fruit>();
			Vector2 position = new Vector2(screenX,Gdx.graphics.getHeight() - screenY);
			int plusScore =0;
			for (Fruit f : fruitArray){

				System.out.println("distance:" + position.dst2(f.position));
				System.out.println("distance:" + f.clicked(position));
				System.out.println("distance:" + Fruit.radius * Fruit.radius + 1);


				if (f.clicked(position)){
					toRemove.add(f);

					switch (f.type){
						case APPLE:
							plusScore++;
							break;
						case BANANA:
							plusScore++;
							break;
						case CHERRY:
							plusScore++;
							break;
						case COCONUT:
							plusScore++;
							break;
						case EXTRA:
							plusScore+=3;
							score++;
							break;
						case ENEMY:
							lives--;
							break;
						case BOMB:
							lives= 0;
							break;
						case LIFE:
							lives++;
							break;

					}
				}
			}

			score += plusScore * plusScore;

			for (Fruit f : toRemove){
				fruitArray.removeValue(f,true);
			}

		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}
}
