# Tutorial: 3 Tipos de Bloques Custom en Hytale

## Que hace este mod?

Mod tutorial que enseña a crear **3 tipos de bloques custom** diferentes.
Cada bloque tiene su propio modelo y textura, y cada uno demuestra un patron distinto:

| Bloque | ID para /give | Tipo | Que enseña |
|--------|--------------|------|------------|
| **Tutorial Enchant Table** | `Tutorial_Customui_Table` | UI Interactiva | Abrir una Custom UI con botones funcionales |
| **Tutorial Trash Block** | `Tutorial_Trash_Block` | Inventario | Bloque con contenedor (ItemContainerState) |
| **Tutorial Craft Table** | `Tutorial_Craft_Table` | Mesa de Crafteo | Sistema nativo de crafting (Bench) |

---

## Estructura del Proyecto

```
TutorialCustomBlock/
├── build.gradle
├── gradle.properties
├── settings.gradle
│
└── src/main/
    ├── java/com/scarforges/tutorialblock/
    │   │
    │   ├── TutorialBlockPlugin.java          # Plugin principal - registra todo en setup()
    │   │
    │   ├── interaction/
    │   └── EnchantTableInteraction.java  # Interaccion: abre la UI al pulsar la tecla F
    │   │
    │   ├── ui/
    │   │   ├── EventActionData.java          # CODEC para datos de eventos de UI
    │   │   └── EnchantTablePanel.java        # Panel UI con boton "SAY HELLO"
    │   │
    │   └── blockstate/
    │       └── TutorialTrashState.java       # BlockState: inventario tipo papelera
    │
    └── resources/
        ├── manifest.json
        │
        ├── Server/Item/
        │   ├── Items/
        │   │   ├── Tutorial_Customui_Table.json  # Bloque 1: UI Interactiva
        │   │   ├── Tutorial_Trash_Block.json     # Bloque 2: Inventario
        │   │   └── Tutorial_Craft_Table.json     # Bloque 3: Mesa de Crafteo
        │   │
        │   ├── RootInteractions/Block/
        │   │   └── Tutorial_Open_EnchantUI.json  # Lista de interacciones
        │   │
        │   └── Interactions/Block/
        │       └── Tutorial_Open_EnchantUI.json  # Enlace con la clase Java
        │
        └── Common/
            ├── Blocks/Benches/
            │   ├── Tutorial1.blockymodel         # Modelo bloque 1 (Enchant Table)
            │   ├── Tutorial2.blockymodel         # Modelo bloque 2 (Trash Block)
            │   └── Tutorial3.blockymodel         # Modelo bloque 3 (Craft Table)
            │
            ├── Icons/
            │   ├── ItemsGenerated/
            │   │   ├── Tutorial1.png             # Icono bloque 1
            │   │   ├── Tutorial2.png             # Icono bloque 2
            │   │   └── Tutorial3.png             # Icono bloque 3
            │   └── CraftingCategories/
            │       └── Tutorial_Basic.png        # Icono de categorias de crafteo
            │
            └── UI/Custom/Pages/EnchantTable/
                └── EnchantTablePanel.ui          # Layout de la UI
```

---

## Los 3 Bloques Explicados

### 1. Enchant Table (UI Interactiva)

**Patron:** `SimpleBlockInteraction` + `InteractiveCustomUIPage`

El jugador pulsa la tecla F en el bloque y se abre una UI custom con un boton "SAY HELLO".
Al pulsarlo, envia un mensaje al chat y actualiza el texto de la UI en tiempo real.

**Flujo:**
```
Tecla F en bloque
  → JSON: "Use": "Tutorial_Open_EnchantUI"
  → RootInteractions → Interactions → Java CODEC
  → EnchantTableInteraction.interactWithBlock()
  → Crea EnchantTablePanel y abre la UI
  → Jugador pulsa boton → handleDataEvent()
```

**Archivos involucrados:**
- `Tutorial_Customui_Table.json` - Definicion del bloque
- `Tutorial_Open_EnchantUI.json` (x2) - RootInteractions + Interactions
- `EnchantTableInteraction.java` - Logica de interaccion
- `EnchantTablePanel.java` - Logica de la UI
- `EventActionData.java` - CODEC de eventos
- `EnchantTablePanel.ui` - Layout visual

### 2. Trash Block (Inventario)

**Patron:** `ItemContainerState` + `TickableBlockState`

El jugador abre el bloque como un cofre, mete items dentro.
Al cerrar el inventario, cuenta los stacks, envia un mensaje y borra todo.

**Flujo:**
```
Tecla F en bloque
  → JSON: "Use": "Open_Container" (interaccion nativa)
  → Se abre el inventario (27 slots)
  → onOpen() → guarda PlayerRef, envia saludo
  → tick() cada frame → detecta cierre (wasOpen && !isOpen)
  → Cuenta stacks → envia mensaje → container.clear()
```

**Archivos involucrados:**
- `Tutorial_Trash_Block.json` - Definicion del bloque con State
- `TutorialTrashState.java` - Toda la logica del inventario

### 3. Craft Table (Mesa de Crafteo)

**Patron:** Sistema nativo `Bench` de Hytale

Funciona como las mesas de crafteo nativas del juego.
Se configura 100% por JSON, sin codigo Java necesario.
Incluye **2 categorias** de crafteo configuradas (`Tutorial_Basic` y `Tutorial_2tab`), listas para añadir recetas.

**Flujo:**
```
Tecla F en bloque
  → JSON: "Use": "Open_Bench" (interaccion nativa)
  → Se abre la UI de crafteo nativa
  → Muestra categorias y recetas configuradas en el JSON
```

**Archivos involucrados:**
- `Tutorial_Craft_Table.json` - Definicion completa (bloque + bench + categorias)

---

## Como Compilar

```bash
cd TutorialCustomBlock
.\gradlew.bat clean build
```

El JAR se genera en `build/libs/ScarTutorialCustomBlock-1.0.0.jar`.

## Como Usar en el Juego

1. Copia el JAR a la carpeta de mods: `%APPDATA%/Hytale/UserData/Mods/`
2. Inicia el servidor
3. Usa `/give Tutorial_Customui_Table` para obtener el bloque de UI
4. Usa `/give Tutorial_Trash_Block` para obtener el bloque inventario
5. Usa `/give Tutorial_Craft_Table` para obtener la mesa de crafteo
6. Coloca y pulsa F en cada bloque para probar

---

## Modelos y Texturas

Cada bloque tiene su propio modelo y textura independientes:

| Bloque | Modelo | Textura | Icono |
|--------|--------|---------|-------|
| Enchant Table | `Blocks/Benches/Tutorial1.blockymodel` | `Blocks/Benches/Tutorial1.png` | `Icons/ItemsGenerated/Tutorial1.png` |
| Trash Block | `Blocks/Benches/Tutorial2.blockymodel` | `Blocks/Benches/Tutorial2.png` | `Icons/ItemsGenerated/Tutorial2.png` |
| Craft Table | `Blocks/Benches/Tutorial3.blockymodel` | `Blocks/Benches/Tutorial3.png` | `Icons/ItemsGenerated/Tutorial3.png` |

Para cambiar la textura de un bloque, reemplaza el PNG correspondiente en:
```
Common/Blocks/Benches/TutorialX.png
```

---

## Dependencias (manifest.json)

```json
{
  "Dependencies": {
    "Hytale:InteractionModule": "*",
    "Hytale:EntityModule": "*"
  },
  "IncludesAssetPack": true,
  "LoadOrder": "POSTWORLD"
}
```

- `InteractionModule`: Necesario para interacciones custom (Enchant Table)
- `EntityModule`: Necesario para BlockStates custom (Trash Block)
- `IncludesAssetPack`: El mod incluye texturas, iconos y archivos de UI
- `POSTWORLD`: El mod se carga despues de que el mundo este listo
