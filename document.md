# Commands

# Controller to view
These are the methods that the model uses to push updates to the view.
```
void joinedPlayer(String player)
```
```
int askHowManyPlayers()
```
```
void notifyState(Model.State.Type state)
```
```
void movedTimer()
```
```
void pushPositions(List<FlightBoard.Pawn> order, List<Integer> offset)
```
```
void pushCard(Card.CompressedCard card)
```
```
void pushCardChanges(JSONObject data)
```
## View to controller - Remote client's methods
These are the RMI calls that the view sends to the controller.
```
public Result<Integer> moveTimer()
```
```
public Result<String> setReady()
```
```
public Result<String> quit()
```
```
public Result<Tile> setTile(Coordinate c, Tile t, Tile.Rotation rotation)
```
```
public Result<Tile> getTile(Coordinate c)
```
```
public Tile.Rotation getRotation(Coordinate c)
```
```
public Result<Tile> bookTile(Tile t)
```
```
public Result<Tile> useBookedTile(Tile t, Tile.Rotation rotation, Coordinate c)
```
```
public List<Tile> getBooked()
```
```
public Result<String> remove(Coordinate c)
```
```
public Set<Coordinate> checkShip()
```
```
public ShipBoard.CompressedShipBoard getShip()
```
```
public Result<String> init(Optional<Coordinate> purple, Optional<Coordinate> brown)
```
```
public List<GoodsBoard.Type> getReward()
```
```
public Result<Integer> placeReward(GoodsBoard.Type t, Coordinate c)
```
```
public int getCash()
```
```
public Result<Integer> drop(Coordinate c)
```
```
public Result<Integer> drop(Coordinate c, GoodsBoard.Type t)
```
```
public Result<String> setCannonsToUse(Map<Tile.Rotation, Integer> map)
```
```
public Result<Tile> drawTile()
```
```
public Result<List<Tile>> getSeenTiles()
```
```
public Result<String> giveTile(Tile t)
```
```
public Result<Tile> getTileFromSeen(Tile t)
```
```
public Result<Card.CompressedCard> drawCard()
```
```
public Result<JSONObject> setInput(JSONObject json)
```
```
public Result<JSONObject> getCardData()
```
```
public Result<Card[][]> getVisible()
```
```
public void joinedPlayer(String player)
```
```
public int askHowManyPlayers()
```
```
public void notifyState(Model.State.Type state)
```
```
public void movedTimer()
```
```
public void pushPositions(List<FlightBoard.Pawn> order, List<Integer> offset)
```
```
public void pushCard(Card.CompressedCard card)
```
```
public void pushCardChanges(JSONObject data)
```

## Methods from the RMI interface
These are the methods that the view uses on the remote objects.
```
Result<Integer> moveTimer(String name)
```
```
Result<String> setReady(String name)
```
```
Result<String> quit(String name)
```
```
Result<Tile> setTile(String name, Coordinate c, Tile t, Tile.Rotation rotation)
```
```
Result<Tile> getTile(String name, Coordinate c)
```
```
Tile.Rotation getRotation(String name, Coordinate c)
```
```
Result<Tile> bookTile(String name, Tile t)
```
```
Result<Tile> useBookedTile(String name, Tile t, Tile.Rotation rotation, Coordinate c)
```
```
List<Tile> getBooked(String name)
```
```
Result<String> remove(String name, Coordinate c)
```
```
Set<Coordinate> checkShip(String name)
```
```
ShipBoard.CompressedShipBoard getShip(String name)
```
```
Result<String> init(String name, Optional<Coordinate> purple, Optional<Coordinate> brown)
```
```
List<GoodsBoard.Type> getReward(String name)
```
```
Result<Integer> placeReward(String name, GoodsBoard.Type t, Coordinate c)
```
```
int getCash(String name)
```
```
Result<Integer> drop(String name, Coordinate c)
```
```
Result<Integer> drop(String name, Coordinate c, GoodsBoard.Type t)
```
```
Result<String> setCannonsToUse(String name, Map<Tile.Rotation, Integer> map)
```
```
Result<Tile> drawTile(String name)
```
```
Result<List<Tile>> getSeenTiles(String name)
```
```
Result<String> giveTile(String name, Tile t)
```
```
Result<Tile> getTileFromSeen(String name, Tile t)

Result<Card.CompressedCard> drawCard(String name)
```
```
Result<JSONObject> setInput(String name, JSONObject json)
```
```
Result<JSONObject> getCardData(String name)
```
```
Result<Card[][]> getVisible(String name)
```
