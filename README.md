Snake with 3 difficulty levels and an online leaderboard.

If you want to build it on your own simply do

`https://github.com/AndrexoHD/SimpleSnake.git`  
`cd SimpleSnake`  
`mvn clean package`

Then you should be able to execute the `Snake.jar` file found in the target directory.

Note that if you want the online leaderboard you have to download the game from the [Release](https://github.com/AndrexoHD/SimpleSnake/releases) section.
If you don't want to do that you can [implement](https://github.com/AndrexoHD/SimpleSnake/blob/b4a9f603f83c4d4bc65afff5e89e7426a9ef1fea/src/andrexohd/snakegame/JSONLeaderboard.java#L17) your own JSON real-time database.
