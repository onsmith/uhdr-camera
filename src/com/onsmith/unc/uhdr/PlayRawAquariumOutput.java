package com.onsmith.unc.uhdr;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PlayRawAquariumOutput {
  private static final int clock = (0x1 << 10), // Camera clock speed, in hertz
                           fps   = 30,          // Initial frame rate of player, in hertz
                           iMin  = 0,           // Initial minimum for player intensity range
                           iMax  = 600;         // Initial maximum for player intensity range
  
  
  public static void main(String[] args) throws IOException {
    // Background Image
    HDRImage bg = new BufferedHDRImage(ImageIO.read(new File("img/bg.jpg")));
    
    // Sprites
    Sprite[] sprites = new Sprite[] {
      new Sprite("img/sprite1.jpg", new int[][] {
        {261, 244},
        {261, 244},
        {261, 244},
        {261, 244},
        {265, 244},
        {267, 244},
        {269, 244},
        {269, 244},
        {281, 246},
        {289, 246},
        {295, 248},
        {299, 246},
        {301, 246},
        {309, 246},
        {313, 246},
        {317, 246},
        {323, 246},
        {325, 248},
        {329, 252},
        {333, 258},
        {343, 260},
        {343, 260},
        {345, 260},
        {351, 266},
        {355, 272},
        {361, 282},
        {365, 290},
        {367, 294},
        {367, 302},
        {367, 312},
        {369, 320},
        {371, 332},
        {375, 342},
        {375, 352},
        {375, 358},
        {375, 366},
        {375, 372},
        {375, 378},
        {375, 390},
        {375, 396},
        {373, 404},
        {371, 414},
        {369, 416},
        {369, 418},
        {365, 428},
        {365, 428},
        {365, 430},
        {365, 434},
        {361, 436},
        {353, 438},
        {341, 440},
        {335, 440},
        {329, 440},
        {323, 440},
        {317, 438},
        {309, 438},
        {301, 438},
        {301, 438},
        {297, 436},
        {289, 434},
        {279, 428},
        {271, 428},
        {263, 424},
        {257, 418},
        {253, 412},
        {249, 406},
        {247, 402},
        {247, 400},
        {247, 396},
        {247, 394},
        {247, 392},
        {247, 386},
        {249, 382},
        {249, 376},
        {249, 372},
        {251, 364},
        {253, 362},
        {257, 358},
        {257, 356},
        {257, 352},
        {257, 352},
        {261, 344},
        {261, 342},
        {261, 332},
        {261, 320},
        {261, 316},
        {263, 308},
        {267, 302},
        {273, 292},
        {277, 290},
        {283, 284},
        {285, 278},
        {295, 268},
        {295, 266},
        {299, 262},
        {301, 260},
        {307, 256},
        {309, 254},
        {313, 254},
        {313, 254},
      }),
    };
    
    // Source<PixelFire>
    HDRScene aquarium = new AquariumScene(bg, sprites);
    Source<PixelFire> aquariumStream = new SceneIntegrator(clock, aquarium);
    
    // Player
    FramelessPlayer player = new FramelessPlayer(
      aquarium.getWidth(),
      aquarium.getHeight(),
      clock, fps, iMin, iMax,
      aquariumStream
    );
    player.start();
  }
}
