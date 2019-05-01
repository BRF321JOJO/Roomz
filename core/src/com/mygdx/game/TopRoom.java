package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TopRoom implements Screen {
    static MyGdxGame game;
    private OrthographicCamera camera;
    private Viewport viewport;

    private CharacterHealth characterHealthclass;
    private Character character;
    private Boss boss;
    private RedHealth redHealth;
    private SpacetoShoot spacetoShoot;
    private Laser[] laser;
    private WinGame winGame;

    private Music backgroundmusic = Gdx.audio.newMusic(Gdx.files.internal("Ashtonsong3.mp3"));

    private boolean renderspacetoshoot = true;

    private int pausecounter;

    private Sound shootlaser = Gdx.audio.newSound(Gdx.files.internal("Shootlaser.mp3"));

    private int canshootlaser = 0;

    //Currently bosshealth and characterhealth don't control anything
    private int characterhealth = 9;
    private int bosshealth = 100;

    private boolean characterinvincible = false;
    private int invinciblecounter;

    private Sound hurtsound = Gdx.audio.newSound(Gdx.files.internal("BossHurt.mp3"));
//    private Sound bosshurt = Gdx.audio.newSound(Gdx.files.internal("Bosshurt.wav"));
//    private Sound characterhurt = Gdx.audio.newSound(Gdx.files.internal("Bosshurt.wav"));
//    private Sound characterdeath = Gdx.audio.newSound(Gdx.files.internal("Bosshurt.wav"));
//    private Sound bossdeath = Gdx.audio.newSound(Gdx.files.internal("Bosshurt.wav"));

    private boolean bossdeathplayonce;
    private boolean characterdeathplayonce;
    private Music wingamemusic = Gdx.audio.newMusic(Gdx.files.internal("happymusic.mp3"));

    private boolean wongame = false;

    private int bosscheckcounter;
    private boolean userandombossvelocity = true;

    TopRoom(MyGdxGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, MyGdxGame.SCREEN_WIDTH, MyGdxGame.SCREEN_HEIGHT);
        viewport = new FitViewport(MyGdxGame.SCREEN_WIDTH, MyGdxGame.SCREEN_HEIGHT, camera);

        redHealth = new RedHealth(game.batch);
        laser = new Laser[3];

        //Sets laser position to off screen
        for (int i = 0; i <= laser.length - 1; i++) {
            laser[i] = new Laser(game.batch, MyGdxGame.SCREEN_WIDTH, 0, 0, 0);
        }

        characterHealthclass = new CharacterHealth(game.batch);
        character = new Character(game.batch, GameScreen.savedposx, 150, 75, 75, 5, 5, 1);
        boss = new Boss(game.batch);
        spacetoShoot = new SpacetoShoot(game.batch);
        winGame = new WinGame(game.batch);
    }

    @Override
    public void show() {
        backgroundmusic.play();
        backgroundmusic.setLooping(true);
        backgroundmusic.setVolume(0.6f);
    }

    @Override
    public void render(float delta) {
        update();

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        redHealth.render();

        for (int i = 0; i <= laser.length - 1; i++) {
            laser[i].render();
        }

        characterHealthclass.render();

        character.render();

        //Only renders boss if the width actually exists
        if (boss.width > 0) {
            boss.render();
        }

        if (renderspacetoshoot) {
            spacetoShoot.render();
        }

        if (wongame) {
            winGame.render();
        }

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        backgroundmusic.dispose();
        shootlaser.dispose();
//        bosshurt.dispose();
//        characterhurt.dispose();
//        characterdeath.dispose();
//        bossdeath.dispose();
        wingamemusic.dispose();
    }

    private void update(){

        if (pausecounter <= 100) {
            pausecounter++;
        } else{
            renderspacetoshoot = false;
        }

        //All of this only happens when spacetoshoot stops rendering
        if(!renderspacetoshoot) {

            //characterHealthclass.update();
            redHealth.update();
            character.update();
            boss.update();
            for(int i = 0; i <=laser.length-1; i++) {
                laser[i].update();
            }
            //Only runs when the laser is off screen, meaning can only shoot one laser at a time
            if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                if (laser[0].posx >= MyGdxGame.SCREEN_WIDTH && canshootlaser == 0) {
                    shootlaser.play(0.2f);
                    laser[0].posx = character.posx + character.width;
                    laser[0].posy = character.posy + character.width / 2;
                    laser[0].velx = 20;
                    canshootlaser++;
                }
                else if (laser[1].posx >= MyGdxGame.SCREEN_WIDTH && canshootlaser == 1) {
                    shootlaser.play(0.2f);
                    laser[1].posx = character.posx + character.width;
                    laser[1].posy = character.posy + character.width / 2;
                    laser[1].velx = 20;
                    canshootlaser++;
                }
                else if (laser[2].posx >= MyGdxGame.SCREEN_WIDTH && canshootlaser == 2){
                    shootlaser.play(0.2f);
                    laser[2].posx = character.posx + character.width;
                    laser[2].posy = character.posy + character.width / 2;
                    laser[2].velx = 20;
                    canshootlaser = 0;
                }
            }

            //This keeps the player in bound
            if (character.posx < 0) {
                character.velx = 0;
                character.posx = 0;
            } else {
                character.velx = 7;
            }
            if (character.posy < 0) {
                character.vely = 0;
                character.posy = 0;
            } else {
                character.vely = 7;
            }

            if (character.posx > MyGdxGame.SCREEN_WIDTH - character.width) {
                character.velx = 0;
                character.posx = MyGdxGame.SCREEN_WIDTH - character.width;
            } else {
                character.velx = 7;
            }

            if (character.posy > MyGdxGame.SCREEN_HEIGHT - character.height) {
                character.vely = 0;
                character.posy = MyGdxGame.SCREEN_HEIGHT - character.height;
            } else {
                character.vely = 7;
            }


            //Checks if character or boss die
            if(characterhealth<=0){
                if(characterdeathplayonce) {
                    hurtsound.play(0.4f);
                    //characterdeath.play(0.4f);
                    characterdeathplayonce = false;
                }
            }

            if (bosshealth <= 0) {
                if(bossdeathplayonce) {
                    hurtsound.play(0.4f);
                    //bossdeath.play(0.7f);
                    bossdeathplayonce = false;
                }
            }


            //Makes character invincible for a short time (30 frames)
            if (characterinvincible) {
                if(invinciblecounter <= 30) {
                    invinciblecounter++;
                } else{
                    characterinvincible = false;
                    invinciblecounter = 0;
                }
            }

            //Checks collision for player against boss
            //Added where this can only collide when the boss actually exists on screen (rendered)
            if(!characterinvincible && boss.width>0) {
                if (character.isCollide(boss)) {
                    hurtsound.play(0.7f);
                    //characterhurt.play(0.7f);
                    characterhealth--;
                    characterinvincible = true;
                    redHealth.width-=80;
                }
            }

            //Collision for boss against laser
            for(int i = 0; i<=laser.length-1; i++) {
                if (boss.isCollide(laser[i])) {
                    hurtsound.play(0.2f);
                    hurtsound.play(0.2f);
                    bosshealth--;
                    //Shrinks boss when hurt
                    boss.height -= 5;
                    boss.width -= 5;

                    //Speeds up boss when hurt
                    if(boss.velx > 0){
                        boss.velx++;
                    } else if (boss.velx<0){
                        boss.velx--;
                    }

                    if(boss.vely>0){
                        boss.vely++;
                    } else if(boss.vely<0){
                        boss.vely--;
                    }
                    bosshealth--;
                }
            }

            //Stops rendering laser if it collides with boss by sending it off screen
            for(int i = 0; i <=laser.length-1; i++) {
                if (laser[i].isCollide(boss)) {
                    laser[i].posx = MyGdxGame.SCREEN_WIDTH;
                }
            }


            //Makes boss follow you
            if(userandombossvelocity) {
                userandombossvelocity = false;

                //Makes boss follow player
                if (character.posx < boss.posx) {
                    if(boss.velx>0) {
                        boss.velx = -boss.velx;
                    }
                }

                if (character.posy < boss.posy) {
                    if(boss.vely>0) {
                        boss.vely = -boss.velx;
                    }
                }
            }

            if(!userandombossvelocity) {
                if (bosscheckcounter <= 100) {
                    bosscheckcounter++;
                } else {
                    userandombossvelocity = true;
                    bosscheckcounter = 0;
                }
            }



            //This actually loops because boss.width will continue to be true
            if(boss.width <= 0 || bosshealth <=0){
                wingamemusic.play();
                wingamemusic.setVolume(0.7f);
                backgroundmusic.stop();
                wongame = true;
                boss.width=0;
            }

            if(characterhealth==0){
                TopRoom.game.setScreen(new GameScreen(TopRoom.game));
            }

        }

    }
}
