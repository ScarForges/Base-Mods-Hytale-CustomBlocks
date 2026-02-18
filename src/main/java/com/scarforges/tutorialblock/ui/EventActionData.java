/*
 * ============================================================
 * TUTORIAL: EventActionData - Datos de eventos de UI
 * ============================================================
 *
 * Esta clase es el "contenedor de datos" que la UI envia al
 * servidor cuando el jugador interactua con la interfaz.
 *
 * Cuando el jugador hace click en un boton de la UI, Hytale
 * serializa los datos del evento usando este CODEC y los envia
 * al metodo handleDataEvent() de nuestro panel.
 *
 * Es un patron comun: siempre necesitas una clase EventData
 * con su CODEC para que InteractiveCustomUIPage funcione.
 *
 * ============================================================
 */
package com.scarforges.tutorialblock.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class EventActionData {

    // El JSON crudo que llega del evento de la UI
    public String actionDataJson;

    public EventActionData() {
        this.actionDataJson = "";
    }

    // ============================================================
    // CODEC: Define como serializar/deserializar los datos del evento.
    // "ActionDataJson" es la clave que Hytale usa para el payload.
    // BuilderCodec es el patron estandar para registrar tipos custom.
    // ============================================================
    @SuppressWarnings("unchecked")
    public static final BuilderCodec<EventActionData> CODEC = ((BuilderCodec.Builder<EventActionData>)
        BuilderCodec.builder(EventActionData.class, EventActionData::new)
            .append(
                new KeyedCodec<>("ActionDataJson", Codec.STRING),
                (obj, val) -> obj.actionDataJson = val,
                obj -> obj.actionDataJson
            )
            .add())
        .build();
}
