/*
 * ============================================================
 * TUTORIAL: EnchantTablePanel - UI Interactiva
 * ============================================================
 *
 * Esta es la pagina de UI que se abre al pulsar la tecla F
 * en el Enchant Table. Tiene un boton que envia un mensaje
 * al chat del jugador.
 *
 * CICLO DE VIDA:
 *   1. build()           -> Carga el archivo .ui y vincula botones a eventos
 *   2. handleDataEvent() -> Se ejecuta cuando el jugador pulsa un boton
 *   3. onDismiss()       -> Se ejecuta al cerrar con ESC
 *
 * CONCEPTOS CLAVE:
 *   - InteractiveCustomUIPage<T>: clase base para UIs con eventos
 *   - EventActionData: el CODEC que serializa los datos del evento
 *   - UICommandBuilder: permite modificar la UI desde el servidor
 *   - sendUpdate(): envia cambios de UI al cliente en tiempo real
 *
 * ============================================================
 */
package com.scarforges.tutorialblock.ui;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.Message;

import java.awt.Color;
import java.util.logging.Logger;

public class EnchantTablePanel extends InteractiveCustomUIPage<EventActionData> {

    private static final Logger LOGGER = Logger.getLogger("Tutorial-EnchantUI");
    private static final String UI_PATH = "Pages/EnchantTable/EnchantTablePanel.ui";

    private final PlayerRef playerRef;
    private int clickCount = 0;

    public EnchantTablePanel(PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, EventActionData.CODEC);
        this.playerRef = playerRef;
    }

    // ============================================================
    // BUILD: Carga el archivo .ui y vincula botones a eventos
    // Se ejecuta automaticamente al abrir la pagina
    // ============================================================
    public void build(Ref<EntityStore> ref, UICommandBuilder uiBuilder,
                      UIEventBuilder eventBuilder, Store<EntityStore> store) {

        // Cargar el layout de la UI desde el archivo .ui
        uiBuilder.append(UI_PATH);

        // Vincular el boton "SAY HELLO" -> envia ActionId: "HELLO" al servidor
        eventBuilder.addEventBinding(
            CustomUIEventBindingType.Activating,
            "#BtnHello",
            new EventData().append("ActionId", "HELLO")
        );

        // Vincular el boton de cerrar -> envia ActionId: "CLOSE"
        eventBuilder.addEventBinding(
            CustomUIEventBindingType.Activating,
            "#CloseButton",
            new EventData().append("ActionId", "CLOSE")
        );

        LOGGER.info("EnchantTablePanel abierto para " + playerRef.getUsername());
    }

    // ============================================================
    // HANDLE DATA EVENT: Se ejecuta cada vez que el jugador
    // pulsa un boton en la UI. Recibe un JSON con el ActionId.
    // ============================================================
    public void handleDataEvent(Ref<EntityStore> ref, Store<EntityStore> store, String rawData) {
        // Parsear el JSON que viene del evento de la UI
        JsonObject json = JsonParser.parseString(rawData).getAsJsonObject();
        String actionId = json.get("ActionId").getAsString();

        switch (actionId) {
            case "HELLO":
                clickCount++;

                // Enviar mensaje al chat usando la API de Hytale (Message.join + .color)
                playerRef.sendMessage(Message.join(
                    Message.raw("[Mesa Tutorial] ").color(Color.ORANGE),
                    Message.raw("Hola " + playerRef.getUsername()).color(Color.CYAN),
                    Message.raw("! Has pulsado " + clickCount + " vez/veces.").color(Color.WHITE)
                ));

                // Actualizar el texto de la UI en tiempo real con sendUpdate()
                UICommandBuilder uiBuilder = new UICommandBuilder();
                UIEventBuilder eventBuilder = new UIEventBuilder();
                uiBuilder.set("#StatusText.Text", "Has pulsado " + clickCount + " vez/veces!");
                sendUpdate(uiBuilder, eventBuilder, false);
                break;

            case "CLOSE":
                // Cerrar la UI estableciendo la pagina a None
                Player player = (Player) store.getComponent(ref, Player.getComponentType());
                player.getPageManager().setPage(ref, store, Page.None);
                break;
        }
    }

    public void onDismiss(Ref<EntityStore> ref, Store<EntityStore> store) {
        LOGGER.info("Panel cerrado para " + playerRef.getUsername());
    }
}
