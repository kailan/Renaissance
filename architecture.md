## How Renaissance is structured

**TODO**: Make this better.

Renaissance tries to keep matches and lobbies as separate from each other as possible. This means that, if we need to in the future, we can very easily enable concurrent match support.
As such, the goal is that we make no assumptions on whether or not someone is in a match or not, as we did in HungerGames (v1).

Players are either in a lobby or in a match, as distinguished with their `lobby` and `match` properties. These should _not_ be set at the same time, as that means that the player is in an invalid state.
Communication between the different parts of Renaissance, such as Lobby <-> Match and Match <-> Modules is mainly via events such as `LobbyEndEvent` and similar.

To keep Renaissance fairly modular, the aim is that the code itself does not make assumptions about the state of the game and how the game functions. As such, it should be possible for us to change a relatively small amount of code for a new gamemode.
As such, it is not RLobby's task to transport the players to the pedestals, as this would make an assumption about the gamemode (pedestals -> hunger games).

## Loading of Renaissance and the Maps

1. Initialize config, read location of maps.
2. Load initial set of maps, only reading simple properties such as name and version. Modules might be malformed at this point, but that will be catched later.
3. Resolve lobbies of the maps, and error if any lobbies are not indicated as such or are not found at all.
4. Load the rotation file and check if any maps are missing or not a map (a lobby).
5. Setup an initial lobby for the first map in the rotation, and wait for enough players to join.

## Lobby and Match flow

1a. Lobby gets constructed by either the startup procedure or after the ending of a previous match.   
1b. Lobby already loads match in the background and emits an `MatchLoadEvent`. This event is mainly for ore generation, so that this can happen while players are not yet in the world.  
2. `PlayerJoinEvent` listeners add players to the lobby, clearing their state and setting their `lobby` property.  
3a. Once the lobby recognizes enough players are in (as set in the config), a countdown starts for the end of said lobby.  
3b. If a player at this point leaves, the countdown automatically cancels.  
4. Once the countdown successfully ends, a `LobbyEndEvent` gets emitted. The lobby then assumes that any module or the match itself transports the players out. After the `LobbyEndEvent`, it asserts that the amount of players still in the lobby `== 0`, and after that unloads itself.  
5. RMatch is initially dormant until any module starts its countdown to the start. As such, it does not contain any movement cancelling code itself (remember, modularity). This countdown is almost always started by the same module as the module responsible for teleporting the players to the match.  
6. Modules then handle every aspect about the match, communicating with the match via various events (`MatchStartEvent`, `MatchLoadEvent` etc...). The match object itself is just a simple container that keeps the world, modules and map together.  
7. Once a module decides the match has ended, other modules come available for transitioning Match -> Lobby. RMatch itself does not make assumptions about any rotation and the upcoming map.
