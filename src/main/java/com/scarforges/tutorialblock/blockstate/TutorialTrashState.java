/*
 * ============================================================
 * TUTORIAL: TutorialTrashState - Bloque tipo Inventario
 * ============================================================
 *
 * Este bloque actua como un contenedor (inventario).
 * Cuando el jugador lo abre, puede poner items dentro.
 * Al cerrar el inventario:
 *   1. Cuenta cuantos stacks hay
 *   2. Envia un mensaje al chat con la cantidad
 *   3. Borra todos los items (como una papelera)
 *
 * PATRON: Extiende ItemContainerState (la base de Hytale para
 * bloques con inventario, como cofres y papeleras).
 * Implementa TickableBlockState para ejecutar logica cada tick.
 *
 * REGISTRO: En TutorialBlockPlugin.setup():
 *   getBlockStateRegistry().registerBlockState(
 *       TutorialTrashState.class,
 *       "Tutorial_Trash_Block",
 *       TutorialTrashState.CODEC,
 *       ItemContainerStateData.class,
 *       ItemContainerStateData.CODEC
 *   );
 *
 * JSON: El bloque referencia este state con:
 *   "State": { "Id": "Tutorial_Trash_Block", "Capacity": 27 }
 *
 * ============================================================
 */
package com.scarforges.tutorialblock.blockstate;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.player.windows.ContainerBlockWindow;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.state.TickableBlockState;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.awt.Color;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class TutorialTrashState extends ItemContainerState implements TickableBlockState {

    private static final Logger LOGGER = Logger.getLogger("Tutorial-TrashBlock");

    // ============================================================
    // CODEC: Necesario para que Hytale sepa crear instancias
    // de esta clase. Solo necesitamos un builder basico.
    // ============================================================
    public static final BuilderCodec<TutorialTrashState> CODEC = BuilderCodec.builder(
        TutorialTrashState.class, TutorialTrashState::new
    ).build();

    // Guardar referencia al ultimo jugador que abrio el inventario
    private PlayerRef lastPlayer = null;

    // Tracking: estaba abierto en el tick anterior?
    private boolean wasOpen = false;

    public TutorialTrashState() {
        super();
    }

    // ============================================================
    // ON OPEN: Se llama cuando un jugador abre el contenedor.
    // Guardamos su PlayerRef para poder enviarle mensajes
    // cuando cierre el inventario.
    // ============================================================
    @Override
    public void onOpen(Ref<EntityStore> ref, World world, Store<EntityStore> store) {
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef != null) {
            this.lastPlayer = playerRef;
            // Enviar mensaje de bienvenida al jugador
            playerRef.sendMessage(Message.join(
                Message.raw("[Papelera Tutorial] ").color(Color.ORANGE),
                Message.raw("Contenedor abierto! Los items se contaran al cerrar.").color(Color.WHITE)
            ));
            LOGGER.info("Trash container abierto por " + playerRef.getUsername());
        }
    }

    // ============================================================
    // TICK: Se ejecuta cada tick del servidor (~20 veces/segundo).
    // Aqui detectamos cuando el inventario pasa de abierto a cerrado
    // comparando el estado del tick anterior con el actual.
    // ============================================================
    @Override
    public void tick(float dt, int index, ArchetypeChunk<ChunkStore> chunk,
                     Store<ChunkStore> store, CommandBuffer<ChunkStore> commandBuffer) {

        // getWindows() devuelve Map<UUID, ContainerBlockWindow>
        // Si esta vacio = nadie tiene el inventario abierto
        Map<UUID, ContainerBlockWindow> currentWindows = getWindows();
        boolean isOpen = !currentWindows.isEmpty();

        // Mientras este abierto, actualizar el PlayerRef desde la ventana
        if (isOpen) {
            for (ContainerBlockWindow window : currentWindows.values()) {
                PlayerRef pRef = window.getPlayerRef();
                if (pRef != null) {
                    lastPlayer = pRef;
                }
            }
        }

        // MOMENTO CLAVE: El inventario se acaba de cerrar
        // (wasOpen era true en el tick anterior, ahora isOpen es false)
        if (wasOpen && !isOpen) {
            ItemContainer container = getItemContainer();

            if (container != null && !container.isEmpty()) {
                // Contar stacks no vacios
                int stackCount = 0;
                for (short i = 0; i < container.getCapacity(); i++) {
                    ItemStack stack = container.getItemStack(i);
                    if (stack != null) {
                        stackCount++;
                    }
                }

                // Enviar mensaje al jugador con el conteo
                if (lastPlayer != null && stackCount > 0) {
                    lastPlayer.sendMessage(Message.join(
                        Message.raw("[Papelera Tutorial] ").color(Color.ORANGE),
                        Message.raw("Habia ").color(Color.WHITE),
                        Message.raw(String.valueOf(stackCount)).color(Color.CYAN).bold(true),
                        Message.raw(" stack(s) en el contenedor! Todo eliminado!").color(Color.WHITE)
                    ));
                    LOGGER.info("Trash container tenia " + stackCount + " stacks, limpiando...");;
                }

                // Borrar todo el contenido (funcion papelera)
                container.clear();
            }

            lastPlayer = null;
        }

        wasOpen = isOpen;
    }
}
