# Tank War Game - UML Class Diagram

## Simple Class Diagram in Mermaid.js

```mermaid
classDiagram
    %% Main Entry Point
    class Main {
        +main(String[] args)
        +start(Stage stage)
    }
    
    %% Abstract Base Class
    class GameObject {
        <<abstract>>
        #double x
        #double y
        #double width
        #double height
        #boolean active
        +update()*
        +render(GraphicsContext)*
        +intersects(GameObject) boolean
        +isSolid() boolean
    }
    
    %% Game Entities
    class Tank {
        -int health
        -Direction direction
        -MovementStrategy strategy
        -double speed
        -boolean isPlayer
        +move(Direction, List)
        +fire() Missile
        +takeDamage(int)
        +heal()
    }
    
    class Missile {
        -Direction direction
        -Tank owner
        -int DAMAGE
        +update()
        +getDamage() int
    }
    
    class Wall {
        +update()
        +render(GraphicsContext)
    }
    
    class MedPack {
        -double pulseTimer
        +update()
        +isSolid() boolean
    }
    
    class Explosion {
        -int currentFrame
        -int totalFrames
        +update()
        +isSolid() boolean
    }
    
    %% Direction Enum
    class Direction {
        <<enumeration>>
        UP
        DOWN
        LEFT
        RIGHT
        +getDx() int
        +getDy() int
        +opposite() Direction
    }
    
    %% Strategy Pattern
    class MovementStrategy {
        <<interface>>
        +getNextMove(Tank, List, Tank) Direction
    }
    
    class PlayerMovementStrategy {
        +getNextMove() Direction
    }
    
    class RandomMovementStrategy {
        -Random random
        -Direction currentDirection
        +getNextMove() Direction
    }
    
    class AggressiveMovementStrategy {
        -Random random
        -Direction currentDirection
        +getNextMove() Direction
    }
    
    %% Factory Pattern (Singleton)
    class GameObjectFactory {
        <<singleton>>
        -static GameObjectFactory instance
        +getInstance() GameObjectFactory
        +createPlayerTank() Tank
        +createEnemyTank() Tank
        +createWall() Wall
        +createMedPack() MedPack
        +createExplosion() Explosion
    }
    
    %% Game Engine (Singleton)
    class GameEngine {
        <<singleton>>
        -static GameEngine instance
        -Tank playerTank
        -List~Tank~ enemyTanks
        -List~Missile~ missiles
        -List~Wall~ walls
        -List~MedPack~ medPacks
        -int score
        -int lives
        +getInstance() GameEngine
        +update()
        +keyPressed(KeyCode)
        +checkCollisions()
    }
    
    %% Observer Pattern
    class GameEvent {
        <<enumeration>>
        TANK_DESTROYED
        ENEMY_DESTROYED
        PLAYER_DESTROYED
        MEDPACK_COLLECTED
        GAME_WON
        GAME_LOST
    }
    
    class GameEventListener {
        <<interface>>
        +onGameEvent(GameEvent, Object)
    }
    
    class GameEventManager {
        -List~GameEventListener~ listeners
        +addListener(GameEventListener)
        +notifyListeners(GameEvent, Object)
    }
    
    %% Resource Manager (Singleton)
    class ResourceManager {
        <<singleton>>
        -static ResourceManager instance
        -Map~String,Image~ imageCache
        +getInstance() ResourceManager
        +getTankImage(Direction) Image
        +getMissileImage(Direction) Image
        +getExplosionImage(int) Image
    }
    
    %% UI Classes
    class GameWindow {
        -GameEngine gameEngine
        -Canvas canvas
        -GameUI gameUI
        +start(Stage)
        +render()
        +onGameEvent(GameEvent, Object)
    }
    
    class GameUI {
        -GameEngine gameEngine
        -Label scoreLabel
        -ProgressBar healthBar
        +createTopPanel() VBox
        +update()
    }
    
    %% Inheritance Relationships
    GameObject <|-- Tank
    GameObject <|-- Missile
    GameObject <|-- Wall
    GameObject <|-- MedPack
    GameObject <|-- Explosion
    
    %% Strategy Pattern Relationships
    MovementStrategy <|.. PlayerMovementStrategy
    MovementStrategy <|.. RandomMovementStrategy
    MovementStrategy <|.. AggressiveMovementStrategy
    Tank --> MovementStrategy
    Tank --> Direction
    
    %% Missile Relationships
    Missile --> Direction
    Missile --> Tank
    
    %% Factory Relationships
    GameObjectFactory ..> Tank
    GameObjectFactory ..> Wall
    GameObjectFactory ..> MedPack
    GameObjectFactory ..> Missile
    GameObjectFactory ..> Explosion
    
    %% Game Engine Relationships
    GameEngine --> GameObjectFactory
    GameEngine --> GameEventManager
    GameEngine --> Tank
    GameEngine --> Missile
    GameEngine --> Wall
    GameEngine --> MedPack
    GameEngine --> Explosion
    
    %% Observer Pattern Relationships
    GameEventManager --> GameEventListener
    GameEventManager --> GameEvent
    GameEventListener <|.. GameWindow
    
    %% UI Relationships
    Main --> GameWindow
    GameWindow --> GameEngine
    GameWindow --> GameUI
    GameUI --> GameEngine
    
    %% Resource Manager
    Tank ..> ResourceManager
    Missile ..> ResourceManager
    Explosion ..> ResourceManager
```

---

## Design Patterns Summary

### 1. **Singleton Pattern**
- `GameEngine` - Single game instance
- `GameObjectFactory` - Single factory instance
- `ResourceManager` - Single resource loader

### 2. **Factory Pattern**
- `GameObjectFactory` - Creates all game objects

### 3. **Strategy Pattern**
- `MovementStrategy` - Different tank behaviors
- `PlayerMovementStrategy` - Keyboard control
- `RandomMovementStrategy` - Random AI
- `AggressiveMovementStrategy` - Chase player AI

### 4. **Observer Pattern**
- `GameEventManager` - Event publisher
- `GameEventListener` - Event subscriber interface
- `GameWindow` - Event subscriber implementation

---

## Key Relationships

### Inheritance (is-a)
- Tank **is-a** GameObject
- Missile **is-a** GameObject
- Wall **is-a** GameObject
- MedPack **is-a** GameObject
- Explosion **is-a** GameObject

### Composition (has-a)
- Tank **has-a** MovementStrategy
- Tank **has-a** Direction
- Missile **has-a** Direction
- GameEngine **has-a** List of Tanks, Missiles, Walls, etc.

### Implementation (implements)
- PlayerMovementStrategy **implements** MovementStrategy
- RandomMovementStrategy **implements** MovementStrategy
- AggressiveMovementStrategy **implements** MovementStrategy
- GameWindow **implements** GameEventListener

### Dependency (uses)
- Factory **creates** game objects
- ResourceManager **provides** images to renderable objects
- GameEngine **uses** Factory to create objects

---

**This diagram shows all major classes and their relationships in a clean, simple format!**

