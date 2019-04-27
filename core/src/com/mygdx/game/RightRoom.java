package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class RightRoom implements Screen {
    static MyGdxGame game;
    private OrthographicCamera camera;
    private Viewport viewport;

    private Character character;
    private Platform[] platform;
    private RightKey rightKey;

    private boolean kaizoinvert = false;
    static int jumppresses;


    RightRoom(MyGdxGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(kaizoinvert, MyGdxGame.SCREEN_WIDTH, MyGdxGame.SCREEN_HEIGHT);
        viewport = new FitViewport(MyGdxGame.SCREEN_WIDTH,MyGdxGame.SCREEN_HEIGHT, camera);

        character = new Character(game.batch, 100, 100, 25, 26, 4, 0, 2);

        platform = new Platform[2];
        platform[0] = new Platform(game.batch,0,50,250,50,0,0);
        platform[1] = new Platform(game.batch, 350,100,500,10,3,0);
        //platform[2] = new Platform(game.batch, )

        //Makes the platforms entities
        for (int i = 0; i <= platform.length-1; i++) {
            Entity.entities.add(platform[i]);
        }

        rightKey = new RightKey(game.batch);

    }

    @Override
    public void show() {

    }


    @Override
    public void render(float delta) {
        update();

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        character.render();
        for (int i = 0; i <= platform.length-1; i++) {
            platform[i].render();
        }
        rightKey.render();

        game.batch.end();

}

    @Override
    public void resize(int width, int height) {
        //Updated the viewport according to the resizing
        viewport.update(width, height);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }

    //This method changes the value for a boolean which inverts the screen
    private void kaizoupdatemethod() {
        //Only works if player is on screen
        if(character.posy - character.height >= 0) {
            if (!kaizoinvert) {
                kaizoinvert = true;
            } else if (kaizoinvert) {
                kaizoinvert = false;
            }
        }
        camera.setToOrtho(kaizoinvert, MyGdxGame.SCREEN_WIDTH, MyGdxGame.SCREEN_HEIGHT);
    }

    private void resetplater() {
        if(character.posy - character.height <= 0) {
            character.posy = 100;
            character.posx = 100;
        }
    }

    private void platformxmovement(int i, int rightbound, int leftbound) {
        if(platform[i].posx >= rightbound) {
            platform[i].velx= -platform[i].velx;
        } else if (platform[i].posx <= leftbound) {
            platform[i].velx = -platform[i].velx;
        }
    }

    public void update() {

        //Update methods
        character.update();
        for(int i = 0; i <= platform.length-1; i++) {
            platform[i].update();
        }
        rightKey.update();

        //Class specific updates

        //Makes it so platform moves at speed of its velocity
        platform[1].posx += platform[1].velx;
        //Method which sets the bounds of movement
        platformxmovement(1, 650, 250);

        //Makes it so that the character is affected by gravity
        character.posy += character.vely;

        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {

            //Inverts screen when jump
            kaizoupdatemethod();

            //Pressing W increases the velocity, which in turn increases the position
            jumppresses++;
            //This makes it so that a character can only jump a maximum of twice
            if(jumppresses == 1) {
                character.vely = 15;
                character.posy += character.vely;
            }
            if(jumppresses == 2) {
                character.vely = 15;
                character.posy += character.vely;
            }

        } else {
            //This makes sure there is constant negative velocity
            character.vely--;
        }

        if (character.posy >= 50 && character.posy <= 100 && character.posx < 100) {
            GameScreen.savedposx = MyGdxGame.SCREEN_WIDTH-character.posx;
            GameScreen.savedposy = MyGdxGame.SCREEN_HEIGHT/2-character.height/2;
            GameScreen.savedID = character.ID;
            RightRoom.game.setScreen(new GameScreen(RightRoom.game));
        }

        resetplater();

        //Updated way to check for collision
        for (int i=0; i<=platform.length-1; i++){
            if(character.isCollide(platform[i])){
                //Makes it so the character is moved to the top of the platform it's on
                character.posy = platform[i].posy + platform[i].height-1;

                //These lines make it so that the character will move along with the platform
                character.posx += platform[i].velx;
                //character.posy += platform[i].vely;

                character.handleCollision(platform[i]);
            }
        }
    }
}