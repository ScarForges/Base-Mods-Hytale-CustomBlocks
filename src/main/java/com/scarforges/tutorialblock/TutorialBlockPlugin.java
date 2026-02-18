/*
 * ============================================================
 * TUTORIAL: Plugin Principal - TutorialCustomBlock
 * ============================================================
 *
 * Este mod enseña 3 tipos de bloques custom en Hytale:
 *
 *   1) ENCHANT TABLE  - Bloque con Custom UI (InteractiveCustomUIPage)
 *   2) TRASH BLOCK    - Bloque con Inventario (ItemContainerState)
 *   3) CRAFT TABLE    - Mesa de Crafteo nativa (Bench, sin Java)
 *
 * REGISTRO:
 *   - Bloque 1: Se registra una interaccion custom (EnchantTableInteraction)
 *     que conecta el JSON "Type" con la clase Java via CODEC.
 *   - Bloque 2: Se registra un BlockState custom (TutorialTrashState)
 *     que extiende ItemContainerState para manejar el inventario.
 *   - Bloque 3: No necesita Java, todo se define en el JSON del bloque.
 *
 * LOS BLOQUES SE OBTIENEN CON: /give Tutorial_Enchant_Table (etc.)
 *
 * ============================================================
 */
package com.scarforges.tutorialblock;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;

import com.scarforges.tutorialblock.blockstate.TutorialTrashState;
import com.scarforges.tutorialblock.interaction.EnchantTableInteraction;

import java.util.logging.Level;

public class TutorialBlockPlugin extends JavaPlugin {

    private static TutorialBlockPlugin instance;

    public TutorialBlockPlugin(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        instance = this;

        getLogger().at(Level.INFO).log("===========================================");
        getLogger().at(Level.INFO).log("   TutorialCustomBlock v1.0.0");
        getLogger().at(Level.INFO).log("   by ScarForges");
        getLogger().at(Level.INFO).log("   Tutorial: 3 Bloques Custom");
        getLogger().at(Level.INFO).log("   1) Enchant Table  -> Custom UI");
        getLogger().at(Level.INFO).log("   2) Trash Block    -> Inventario");
        getLogger().at(Level.INFO).log("   3) Craft Table    -> Mesa de Crafteo");
        getLogger().at(Level.INFO).log("===========================================");

        // -- BLOQUE 1: Enchant Table (Custom UI) --
        // Registra la interaccion que abre la UI al hacer click derecho.
        // Conecta el string "Tutorial_EnchantTableInteraction" del JSON
        // con la clase Java EnchantTableInteraction.
        this.getCodecRegistry(Interaction.CODEC).register(
            "Tutorial_EnchantTableInteraction",
            EnchantTableInteraction.class,
            EnchantTableInteraction.CODEC
        );
        getLogger().at(Level.INFO).log("Bloque 1: EnchantTableInteraction registrada");

        // -- BLOQUE 2: Trash Block (Inventario) --
        // Registra el BlockState que maneja el inventario del bloque.
        // Conecta el "Id": "Tutorial_Trash_Block" del JSON con TutorialTrashState.
        this.getBlockStateRegistry().registerBlockState(
            TutorialTrashState.class,
            "Tutorial_Trash_Block",
            TutorialTrashState.CODEC,
            com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState.ItemContainerStateData.class,
            com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState.ItemContainerStateData.CODEC
        );
        getLogger().at(Level.INFO).log("Bloque 2: TutorialTrashState registrado");

        // -- BLOQUE 3: Craft Table (Mesa de Crafteo) --
        // No necesita registro en Java. El sistema nativo de Hytale se encarga
        // de todo usando la config "Bench" en el JSON del bloque.
        // Las recetas se definen en el JSON de cada item crafteable.
        getLogger().at(Level.INFO).log("Bloque 3: Craft Table (sistema nativo, sin Java)");

        getLogger().at(Level.INFO).log("TutorialCustomBlock cargado correctamente!");
    }

    @Override
    protected void start() {
        getLogger().at(Level.INFO).log("TutorialCustomBlock iniciado");
    }

    @Override
    protected void shutdown() {
        getLogger().at(Level.INFO).log("TutorialCustomBlock desactivado");
    }

    public static TutorialBlockPlugin getInstance() {
        return instance;
    }
}
