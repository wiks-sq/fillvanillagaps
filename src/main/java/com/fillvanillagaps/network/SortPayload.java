package com.fillvanillagaps.network;

import com.fillvanillagaps.FillVanillaGaps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Client asks the server to sort the section of the open menu that contains {@code slotIndex} (-1: pick automatically). */
public record SortPayload(int containerId, int slotIndex) implements CustomPacketPayload {
	public static final Type<SortPayload> TYPE = new Type<>(FillVanillaGaps.id("sort"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SortPayload> CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, SortPayload::containerId,
		ByteBufCodecs.VAR_INT, SortPayload::slotIndex,
		SortPayload::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
