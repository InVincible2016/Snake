Changlong Zhong's Snake
Version 2.5 with graphical textures.

This game takes two command lines arguments.
The first arugment is the fps.
The second argument is the initial game speed/level.

When no command line argument is given, the game starts at 30 fps and game level 2 by default.

Coded in JDK 8.0;

Game Instruction:
At the splash screen, use key 's' to start the game;
After game started:
Use arrow keys to control the snake;
Use key 'r' to restart the game anytime during the game;
Use key 'l' to turn on level-up. hit aagin to turon off.
Use key space to pause the game; hit again to unpause.
Close game window when exit.


Some Details:
Score is calculated by : 
	Score = Score+Score + BodyLength / 2 + CurrentGameSpeed / 2
	if a food is eaten:
	Score += CurrentGameSpeed * 10

Frame rate support: 1 - 100 (theoritically can support up to 500, but not tested.)



Enhancement:
1: Level-up is disabled by default.
To enable level-up, press key 'l' after game started.
When level-up is enabled, game speed is increased by 1 (maximum level: 10) for every 8 food eaten. Game will continue after level up. ( I personally think this is better than restart the game).
Press 'l' again to disable this feature.

2: Graphical textures:
Splash screen back ground URL: "http://clipartcow.com/snake-clipart-image-8414/", free public source.
Other graphical components are drawn using online tool at :"https://sketch.io/sketchpad/" and converted using tool at : "https://pixlr.com/editor/"
