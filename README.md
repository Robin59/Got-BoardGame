Got-BoardGame
=============

Online Game of Thrones board game, based on the seconde edition.
The game is using Jogre Beta 0.3 by Bob Marks.

For more details about the game check : http://www.fantasyflightgames.com/edge_minisite.asp?eidm=172
And the rules book : http://www.fantasyflightgames.com/ffg_content/agot-bg-2nd-ed/support/VA65_AGoT2_Rulebook_web.pdf
More details Jogre : http://jogre.sourceforge.net

How to install and launch the game
-----------

First install Jogre server (or have one of your friends doing it) and the line <game id="gameOfThrones" host="true" minPlayers="3" maxPlayers="6"/> under the line <supported_games>  to the file jogre/server/server.xml.
Then download Got-BoardGame, install it in your jogre/games/gameofthrones repertory, compile the game with "build.xml" .
Finally lunch jogre/server/server.sh (or .bat or have one of your friends doing it), then lunch jogre/games/gameofthrones/client.sh (or .bat).

If you struggle to compile the game, here's an already compiled version with jogre tools :
http://www.mediafire.com/download/ne6e5usrev0bqz0/Got_BoardGame.zip
All you have to is to launch jogre/server/server.bat and then jogre/games/gameOfThrones/client.bat.

There are four already made accounts that you can use to test the game, bob, dave, john, sharon.

About 
-----

The Game is not complete and the documentation is not clear or inexistent. 
I intend to improve the game, clean the code and write the documentation as soon as possible. Right now the code is a mess, I feel like it will be very hard to anyone to help on the core game. But you can change the place where pieces appear, open src/graphism/Board.java , go to the method coordinate and change the values.
Also I intend to do a small tutorial(but not in game) to explain how to play the game.
It seems to have small differences between the Linux and windows version, the windows  version is not tested and more bugs may occur.
