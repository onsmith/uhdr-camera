package com.onsmith.unc.uhdr.scripts;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import com.onsmith.unc.uhdr.AquariumScene;
import com.onsmith.unc.uhdr.BufferedHDRImage;
import com.onsmith.unc.uhdr.FramelessPlayer;
import com.onsmith.unc.uhdr.HDRImage;
import com.onsmith.unc.uhdr.HDRScene;
import com.onsmith.unc.uhdr.PixelFire;
import com.onsmith.unc.uhdr.ScaledHDRImage;
import com.onsmith.unc.uhdr.SceneIntegrator;
import com.onsmith.unc.uhdr.Source;
import com.onsmith.unc.uhdr.Sprite;

public class GenerateAndPlayRaw {
  private static final int clock = (0x1 << 10), // Camera clock speed, in hertz
                           fps   = 30,          // Initial frame rate of player, in hertz
                           iMin  = 0,           // Minimum for player intensity range
                           iMax  = 1000000;     // Maximum for player intensity range
  
  
  public static void main(String[] args) throws IOException {
    // Background Image
    HDRImage bg = new BufferedHDRImage(ImageIO.read(new File("img/bg.jpg")));
    bg = new ScaledHDRImage(bg, 0, 1000000);
    
    // Sprites
    HDRImage sprite1 = new BufferedHDRImage(ImageIO.read(new File("img/sprite1.jpg")));
    sprite1 = new ScaledHDRImage(sprite1, 800000, 1000000);
    Sprite[] sprites = new Sprite[] {
      new Sprite(sprite1, new int[][] {
        {76, 84},
        {76, 84},
        {76, 84},
        {76, 84},
        {78, 84},
        {78, 84},
        {78, 84},
        {80, 84},
        {80, 84},
        {82, 84},
        {84, 84},
        {86, 84},
        {86, 84},
        {88, 82},
        {92, 82},
        {92, 82},
        {96, 82},
        {96, 82},
        {96, 82},
        {96, 82},
        {98, 82},
        {100, 82},
        {100, 82},
        {102, 82},
        {106, 82},
        {106, 82},
        {106, 82},
        {106, 82},
        {108, 82},
        {108, 82},
        {108, 82},
        {108, 82},
        {110, 82},
        {110, 82},
        {112, 82},
        {112, 82},
        {112, 82},
        {116, 82},
        {118, 82},
        {120, 82},
        {120, 82},
        {124, 82},
        {124, 82},
        {126, 82},
        {130, 82},
        {130, 82},
        {136, 82},
        {136, 82},
        {144, 82},
        {146, 82},
        {146, 82},
        {148, 82},
        {148, 82},
        {148, 82},
        {152, 82},
        {154, 82},
        {156, 82},
        {156, 82},
        {158, 82},
        {162, 82},
        {162, 82},
        {164, 82},
        {164, 82},
        {166, 82},
        {170, 82},
        {170, 82},
        {172, 82},
        {172, 82},
        {174, 82},
        {174, 82},
        {174, 82},
        {174, 82},
        {176, 82},
        {180, 82},
        {180, 82},
        {182, 82},
        {184, 82},
        {184, 82},
        {188, 82},
        {192, 82},
        {192, 82},
        {198, 84},
        {198, 84},
        {200, 84},
        {202, 84},
        {202, 84},
        {204, 86},
        {204, 86},
        {206, 86},
        {208, 86},
        {208, 86},
        {210, 86},
        {210, 86},
        {214, 88},
        {214, 88},
        {214, 88},
        {220, 88},
        {220, 88},
        {222, 88},
        {222, 88},
        {226, 88},
        {226, 88},
        {228, 88},
        {228, 88},
        {228, 88},
        {228, 90},
        {228, 90},
        {230, 92},
        {232, 92},
        {234, 92},
        {234, 92},
        {238, 92},
        {238, 92},
        {240, 92},
        {242, 92},
        {242, 92},
        {242, 92},
        {246, 94},
        {246, 94},
        {246, 94},
        {248, 94},
        {250, 96},
        {250, 96},
        {250, 96},
        {252, 96},
        {252, 96},
        {252, 96},
        {256, 96},
        {256, 98},
        {258, 98},
        {258, 98},
        {260, 100},
        {260, 100},
        {262, 100},
        {264, 100},
        {264, 100},
        {266, 100},
        {266, 100},
        {266, 102},
        {268, 104},
        {268, 104},
        {270, 104},
        {270, 104},
        {274, 106},
        {276, 108},
        {276, 108},
        {284, 110},
        {284, 110},
        {286, 110},
        {290, 112},
        {290, 112},
        {290, 114},
        {290, 114},
        {292, 118},
        {296, 118},
        {296, 118},
        {296, 118},
        {300, 120},
        {300, 120},
        {300, 122},
        {300, 124},
        {302, 124},
        {302, 124},
        {302, 126},
        {302, 126},
        {302, 128},
        {302, 128},
        {302, 130},
        {304, 132},
        {304, 136},
        {304, 136},
        {304, 138},
        {304, 138},
        {308, 140},
        {308, 142},
        {308, 142},
        {308, 148},
        {308, 148},
        {308, 150},
        {308, 152},
        {308, 152},
        {308, 156},
        {308, 156},
        {308, 158},
        {308, 160},
        {308, 160},
        {308, 164},
        {308, 164},
        {308, 164},
        {310, 166},
        {310, 166},
        {310, 166},
        {310, 168},
        {310, 168},
        {312, 170},
        {312, 170},
        {314, 174},
        {314, 176},
        {314, 176},
        {316, 178},
        {318, 182},
        {318, 182},
        {318, 182},
        {318, 182},
        {318, 182},
        {318, 182},
        {318, 182},
        {320, 184},
        {320, 186},
        {320, 186},
        {320, 186},
        {320, 188},
        {320, 192},
        {320, 192},
        {320, 192},
        {320, 192},
        {320, 194},
        {320, 194},
        {320, 196},
        {320, 196},
        {320, 200},
        {320, 200},
        {320, 202},
        {320, 202},
        {320, 204},
        {320, 204},
        {320, 206},
        {320, 206},
        {320, 210},
        {320, 212},
        {320, 212},
        {320, 212},
        {320, 214},
        {320, 218},
        {320, 218},
        {320, 218},
        {320, 220},
        {320, 220},
        {320, 220},
        {320, 222},
        {320, 224},
        {320, 224},
        {320, 224},
        {320, 228},
        {320, 228},
        {320, 228},
        {320, 228},
        {320, 228},
        {320, 230},
        {320, 230},
        {320, 232},
        {320, 232},
        {320, 232},
        {320, 236},
        {320, 236},
        {320, 236},
        {320, 236},
        {320, 236},
        {320, 238},
        {320, 238},
        {320, 238},
        {318, 240},
        {318, 240},
        {316, 244},
        {316, 246},
        {316, 246},
        {316, 246},
        {316, 248},
        {316, 248},
        {316, 248},
        {316, 248},
        {316, 248},
        {316, 252},
        {316, 254},
        {316, 254},
        {316, 256},
        {316, 258},
        {316, 258},
        {316, 260},
        {316, 260},
        {314, 260},
        {314, 260},
        {314, 264},
        {312, 264},
        {312, 266},
        {312, 266},
        {312, 268},
        {312, 268},
        {310, 270},
        {310, 270},
        {310, 270},
        {310, 272},
        {310, 272},
        {310, 276},
        {310, 276},
        {308, 278},
        {306, 280},
        {306, 280},
        {306, 284},
        {306, 284},
        {304, 286},
        {304, 286},
        {304, 288},
        {304, 288},
        {304, 288},
        {304, 290},
        {304, 294},
        {304, 294},
        {304, 296},
        {304, 296},
        {304, 298},
        {304, 298},
        {304, 298},
        {304, 300},
        {304, 300},
        {304, 300},
        {304, 300},
        {304, 304},
        {304, 304},
        {304, 304},
        {304, 304},
        {302, 306},
        {302, 306},
        {302, 306},
        {302, 306},
        {302, 306},
        {302, 306},
        {302, 306},
        {302, 308},
        {302, 308},
        {300, 310},
        {300, 310},
        {298, 312},
        {298, 312},
        {298, 312},
        {298, 314},
        {296, 316},
        {296, 316},
        {294, 318},
        {294, 318},
        {292, 320},
        {290, 324},
        {290, 324},
        {286, 330},
        {286, 332},
        {286, 332},
        {286, 332},
        {286, 336},
        {286, 336},
        {286, 338},
        {286, 338},
        {282, 340},
        {282, 340},
        {280, 342},
        {280, 344},
        {280, 344},
        {280, 346},
        {280, 346},
        {278, 348},
        {278, 348},
        {276, 350},
        {274, 350},
        {274, 350},
        {272, 352},
        {266, 354},
        {266, 354},
        {264, 356},
        {264, 356},
        {262, 358},
        {260, 360},
        {260, 360},
        {256, 360},
        {256, 360},
        {254, 360},
        {254, 360},
        {252, 360},
        {252, 360},
        {250, 360},
        {250, 360},
        {250, 360},
        {248, 362},
        {246, 364},
        {246, 364},
        {244, 364},
        {240, 364},
        {240, 364},
        {236, 364},
        {236, 364},
        {234, 366},
        {234, 366},
        {234, 366},
        {234, 366},
        {232, 366},
        {232, 366},
        {228, 366},
        {228, 366},
        {228, 366},
        {228, 366},
        {228, 366},
        {228, 366},
        {228, 366},
        {228, 366},
        {228, 366},
        {228, 366},
        {228, 366},
        {228, 366},
        {228, 366},
        {226, 366},
        {226, 366},
        {226, 366},
        {224, 366},
        {222, 366},
        {222, 366},
        {220, 366},
        {220, 366},
        {214, 368},
        {212, 368},
        {212, 368},
        {208, 368},
        {206, 368},
        {206, 368},
        {204, 368},
        {204, 368},
        {202, 368},
        {202, 368},
        {198, 368},
        {196, 368},
        {196, 368},
        {194, 368},
        {194, 368},
        {188, 368},
        {186, 368},
        {186, 368},
        {180, 368},
        {180, 368},
        {178, 368},
        {178, 368},
        {178, 368},
        {176, 368},
        {176, 368},
        {174, 368},
        {174, 368},
        {174, 368},
        {170, 368},
        {170, 368},
        {166, 368},
        {166, 368},
        {162, 368},
        {160, 368},
        {158, 368},
        {158, 368},
        {158, 368},
        {156, 368},
        {154, 368},
        {154, 368},
        {150, 368},
        {150, 368},
        {148, 368},
        {148, 368},
        {146, 368},
        {146, 366},
        {144, 364},
        {144, 364},
        {144, 364},
        {140, 364},
        {140, 364},
        {140, 364},
        {140, 364},
        {140, 364},
        {140, 364},
        {140, 364},
        {138, 362},
        {138, 362},
        {136, 362},
        {136, 362},
        {132, 362},
        {132, 360},
        {132, 360},
        {130, 358},
        {130, 358},
        {128, 356},
        {126, 356},
        {126, 356},
        {126, 352},
        {122, 352},
        {122, 352},
        {122, 350},
        {122, 350},
        {122, 348},
        {122, 348},
        {120, 346},
        {120, 346},
        {118, 342},
        {118, 340},
        {114, 338},
        {114, 338},
        {112, 336},
        {112, 336},
        {112, 334},
        {112, 334},
        {112, 330},
        {108, 328},
        {108, 328},
        {102, 328},
        {98, 328},
        {98, 328},
        {96, 326},
        {96, 326},
        {96, 326},
        {94, 324},
        {94, 324},
        {92, 324},
        {92, 324},
        {92, 322},
        {92, 322},
        {92, 318},
        {88, 318},
        {88, 318},
        {88, 318},
        {88, 316},
        {88, 316},
        {88, 316},
        {86, 314},
        {86, 314},
        {86, 312},
        {86, 312},
        {86, 312},
        {86, 312},
        {86, 312},
        {86, 308},
        {86, 308},
        {86, 306},
        {86, 306},
        {82, 304},
        {82, 302},
        {82, 302},
        {80, 298},
        {80, 298},
        {78, 296},
        {78, 296},
        {78, 294},
        {78, 294},
        {78, 292},
        {78, 292},
        {78, 290},
        {78, 290},
        {76, 290},
        {76, 290},
        {76, 286},
        {76, 286},
        {76, 284},
        {76, 284},
        {76, 282},
        {76, 282},
        {76, 282},
        {76, 280},
        {74, 278},
        {74, 278},
        {74, 274},
        {74, 274},
        {74, 272},
        {72, 270},
        {72, 270},
        {72, 270},
        {72, 268},
        {72, 268},
        {72, 268},
        {70, 264},
        {70, 264},
        {68, 262},
        {68, 262},
        {68, 262},
        {68, 262},
        {68, 260},
        {68, 260},
        {66, 260},
        {66, 258},
        {66, 258},
        {66, 254},
        {66, 254},
        {64, 252},
        {64, 252},
        {64, 252},
        {64, 250},
        {64, 250},
        {64, 248},
        {62, 246},
        {62, 246},
        {62, 242},
        {62, 242},
        {60, 240},
        {60, 240},
        {60, 238},
        {60, 236},
        {60, 236},
        {60, 236},
        {60, 232},
        {58, 232},
        {58, 230},
        {58, 230},
        {58, 228},
        {58, 226},
        {58, 226},
        {58, 224},
        {58, 224},
        {58, 220},
        {58, 220},
        {58, 218},
        {58, 216},
        {58, 216},
        {58, 212},
        {58, 210},
        {58, 210},
        {56, 208},
        {56, 208},
        {56, 206},
        {56, 204},
        {56, 204},
        {56, 198},
        {56, 198},
        {56, 196},
        {56, 192},
        {56, 192},
        {56, 188},
        {56, 188},
        {56, 182},
        {56, 180},
        {56, 180},
        {56, 174},
        {56, 174},
        {56, 172},
        {56, 170},
        {56, 170},
        {56, 168},
        {56, 168},
        {56, 168},
        {56, 164},
        {56, 164},
        {56, 162},
        {56, 162},
        {56, 160},
        {56, 160},
        {56, 160},
        {56, 160},
        {56, 160},
        {56, 160},
        {56, 160},
        {56, 160},
        {56, 160},
        {56, 156},
        {56, 156},
        {56, 156},
        {56, 156},
        {60, 154},
        {60, 154},
        {60, 152},
        {60, 150},
        {60, 150},
        {62, 146},
        {62, 146},
        {64, 144},
        {64, 144},
        {64, 144},
        {64, 144},
        {68, 142},
        {68, 142},
        {68, 140},
        {68, 140},
        {70, 138},
        {70, 138},
        {72, 136},
        {72, 132},
        {72, 132},
        {72, 130},
        {72, 130},
        {74, 130},
        {74, 130},
        {74, 128},
        {74, 128},
        {74, 128},
        {74, 128},
        {74, 128},
        {74, 126},
        {74, 126},
        {76, 124},
        {76, 124},
        {76, 124},
        {76, 124},
        {76, 124},
        {76, 124},
        {76, 124},
        {76, 124},
        {78, 122},
        {78, 122},
        {78, 122},
        {80, 122},
        {80, 122},
        {80, 122},
        {80, 122},
        {82, 122},
        {84, 122},
        {84, 118},
        {84, 118},
        {86, 118},
        {86, 118},
        {86, 118},
        {86, 118},
        {86, 116},
        {88, 116},
        {88, 116},
        {88, 114},
        {90, 112},
        {90, 112},
        {92, 112},
        {92, 112},
        {94, 112},
        {94, 110},
        {94, 110},
        {94, 110},
        {94, 110},
        {96, 110},
        {96, 110},
        {98, 106},
        {98, 106},
        {98, 106},
        {98, 106},
        {98, 106},
        {100, 104},
        {100, 104},
        {100, 104},
        {100, 104},
        {100, 104},
        {100, 104},
        {100, 104},
        {100, 104},
        {100, 104},
        {100, 104},
        {100, 104},
        {100, 104},
        {102, 102},
        {102, 102},
        {102, 98},
        {102, 98},
        {102, 98},
        {102, 98},
        {104, 96},
        {104, 96},
        {104, 96},
        {104, 96},

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