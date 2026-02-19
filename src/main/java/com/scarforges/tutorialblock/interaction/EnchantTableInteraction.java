/*
 * ============================================================
 * TUTORIAL: EnchantTableInteraction - Interaccion del Bloque
 * ============================================================
 *
 * Cuando el jugador pulsa la tecla F en el Enchant Table,
 * Hytale ejecuta interactWithBlock() de esta clase.
 *
 * Lo que hacemos aqui:
 *   1. Obtener Player y PlayerRef del contexto de interaccion
 *   2. Crear un EnchantTablePanel (nuestra UI custom)
 *   3. Abrirlo con player.getPageManager().openCustomPage()
 *
 * CONCEPTOS CLAVE:
 *   - SimpleBlockInteraction: clase base de Hytale para
 *     interacciones con bloques. Maneja posicion, cooldown, etc.
 *   - CODEC: necesario para que Hytale cree instancias de esta
 *     clase al leer el JSON de interaccion.
 *   - El JSON dice { "Type": "Tutorial_EnchantTableInteraction" }
 *     y el plugin lo conecta con esta clase en setup().
 *
 * ============================================================
 */
package com.scarforges.tutorialblock.interaction;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import com.scarforges.tutorialblock.ui.EnchantTablePanel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.logging.Logger;

public class EnchantTableInteraction extends SimpleBlockInteraction {

    private static final Logger LOGGER = Logger.getLogger("Tutorial-EnchantTable");

    // CODEC: Permite que Hytale deserialice esta interaccion desde JSON.
    // No tiene parametros extra, asi que solo extendemos el CODEC base.
    public static final BuilderCodec<EnchantTableInteraction> CODEC =
        BuilderCodec.builder(
            EnchantTableInteraction.class, 
            EnchantTableInteraction::new, 
            SimpleBlockInteraction.CODEC
        ).build();

    @Override
    protected void interactWithBlock(@Nonnull World world,
                                      @Nonnull CommandBuffer<EntityStore> commandBuffer,
                                      @Nonnull InteractionType interactionType,
                                      @Nonnull InteractionContext interactionContext,
                                      @Nullable ItemStack itemInHand,
                                      @Nonnull Vector3i targetBlock,
                                      @Nonnull CooldownHandler cooldownHandler) {
        try {
            // 1. Obtener la entidad del jugador
            Ref<EntityStore> entityRef = interactionContext.getEntity();
            Store<EntityStore> store = entityRef.getStore();

            // 2. Obtener componente Player (necesario para abrir UI)
            Player player = (Player) store.getComponent(entityRef, Player.getComponentType());
            if (player == null) {
                LOGGER.warning("No se pudo obtener Player del contexto");
                return;
            }

            // 3. Obtener PlayerRef (identifica al jugador)
            PlayerRef playerRef = (PlayerRef) store.getComponent(entityRef, PlayerRef.getComponentType());
            if (playerRef == null) {
                LOGGER.warning("No se pudo obtener PlayerRef del contexto");
                return;
            }

            LOGGER.info("Abriendo UI para " + playerRef.getUsername());

            // 4. Crear la pagina de UI y abrirla
            EnchantTablePanel panel = new EnchantTablePanel(playerRef);
            player.getPageManager().openCustomPage(entityRef, store, (CustomUIPage) panel);

        } catch (Exception e) {
            LOGGER.warning("Error abriendo Enchant Table: " + e.getMessage());
        }
    }

    @Override
    protected void simulateInteractWithBlock(@Nonnull InteractionType interactionType,
                                              @Nonnull InteractionContext interactionContext,
                                              @Nullable ItemStack itemInHand,
                                              @Nonnull World world,
                                              @Nonnull Vector3i targetBlock) {
        // No necesitamos simulacion del lado cliente
    }
}
