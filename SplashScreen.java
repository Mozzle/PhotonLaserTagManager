import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class SplashScreen extends Sprite {
    public int animationState;

    public SplashScreen(int x, int y, int w, int h) {
        super(x, y, w, h);

        image = View.loadImage("src/logo.jpg");
    }

    public void update() {
        
    }

    public void updateScreenSize(int screenW, int screenH) {
        this.screenW = screenW;
        this.screenH = screenH;
        this.x = (int)((this.screenW / 2) - (this.w / 2));
        this.y = (int)((this.screenH / 2) - (this.h / 2));
    }
}
